package com.kaweah.astralcafe.starbinder;

import com.kaweah.astralcafe.SkyViewCanvas;
import com.kaweah.astralcafe.model.*;
// import org.apache.commons.math.linear.MatrixUtils;
// import org.apache.commons.math.linear.RealMatrix;
import Jama.Matrix;

import org.jdesktop.swingworker.SwingWorker;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * AstralCafe Star and Constellation Viewer
 * ©2010 Dan Jensen, Kaweah.com
 */

public class StarBinder extends SwingWorker<List<Set <Link>>, Object>
{
    public static double TWO_PI = 6.283;

    private SkyViewCanvas mCanvas;

    // settings for matching visible stars to patterns

    /**
     * Permissible distance between a particular star and a particular pattern vertex, scaled to a fraction of the
     * pattern being matched. Must be greater than zero.
     */

    public double tolerance = 0.2;

    /**
     * The distance to translate the pattern match domain between iterations, scaled to a fraction of the pattern being
     * matched (1.0 for no overlap; 0.5 for 50% overlap). Must be greater than zero.
     */

    public double translationalPrecision = 1.0;

    /**
     * Amount of rotation iterations in the test.
     * minimum value: 1 (no rotation)
     */

    public int rotationSteps = 10;

    /**
     * Minimum scale to be applied to pattern.
     */

    public double minScale = 30.0;

    /**
     * Maximum scale to be applied to pattern.
     */

    public double maxScale = 70.0;

    /**
     * Amount of scaling iterations in the test.
     * minimum value: 1 (use mean of min and max scales)
     */

    public int scalingSteps = 4;

    /**
     * Fraction of scale beyond which visual noise is not calculated.
     */

    public double mNoiseHorizon = 0.5;

    /**
     * Fraction of scale beyond which visual noise is constant, and below which noise increases proportional to
     * distance.
     */

    public double mNoiseThreshold = 0.2;

    /**
     * Weighing factor in favor of error over noise.
     */

    public int mErrorToNoiseWeight = 4;

    private static Set <StarBinder> instances = new HashSet <StarBinder> ();

	public StarBinder(SkyViewCanvas canvas)
	{
        mCanvas = canvas;

        // cancel all other threads. Run only one at a time.
        for (StarBinder binder : instances)
        {
            binder.cancel(true);
        }
        instances.clear();
        instances.add(this);
        // addPropertyChangeListener(canvas);
        StarPattern.init();
	}

    public void scanForPatterns()
    {
        StarPattern.init();
        
        for (StarPattern pattern : StarPattern.patterns)
        {
            scanForMatches(pattern);
        }
    }

    @Override
    protected List<Set <Link>> doInBackground() throws Exception
    {
        List <Set <Link>> links = new ArrayList<Set <Link>> ();

        for (StarPattern pattern : StarPattern.patterns)
        {
            links.addAll(scanForMatches(pattern));
        }

        return links;
    }

    private List <Set<Link>> scanForMatches(StarPattern pattern)
    {
        Set <Link> bestMatch = null;
        Projection projection = mCanvas.getProjection();
        Screen screen = projection.theScreen;
        List <Set<Link>> links = new ArrayList<Set<Link>>();
        int minError = Integer.MAX_VALUE;
        int columnCount = pattern.getVertices().getColumnDimension();

        for (int r = 0; r < rotationSteps; r++)
        {
            double a = 0.0;
            if (rotationSteps != 0) // prevent division by zero.
                a = TWO_PI * r / rotationSteps;
            double cosa = Math.cos(a);
            double sina = Math.sin(a);
            double[][] rotator = {{cosa,-sina},{sina,cosa}};
            Matrix rotMatrix = new Matrix(rotator);
            Matrix rotated = rotMatrix.times(pattern.getVertices());

            for (int s = 0; s < scalingSteps; s++)
            {
                double scale = minScale;
                if (scalingSteps != 1)
                    scale += (maxScale-minScale) * s / (scalingSteps-1);
                else scale = (maxScale+minScale) / 2.0; // scale half-way between min and max.
                int diffThreshold = (int) (scale * tolerance);
                Matrix scaled = rotated.times(scale);
                double margin = scale;
                double delta = scale/translationalPrecision;

                for (double x = margin; x < projection.portWidth-margin; x += delta)
                {
                    Matrix finalMatrix = scaled.copy();
                    for (int j = 0; j < columnCount; j++)
                    {
                    	double entry = finalMatrix.get(0, j);
                        finalMatrix.set(0,j,entry+x);
                        entry = finalMatrix.get(1, j);
                        finalMatrix.set(1,j,entry+margin);
                    }

                    for (double y = margin; y < projection.portHeight-margin; y += delta)
                    {
                        for (int j = 0; j < columnCount; j++)
                        {
                        	double entry = finalMatrix.get(1, j);
                            finalMatrix.set(1,j,entry+delta);
                        }
                        
                        int error = 0;
                        int[] index = new int[columnCount];
                        for (int i = 0; i < index.length; i++)
                        {
                            index[i] = -1;
                        }

                        for (int j = 0; j < finalMatrix.getColumnDimension(); j++)
                        {
                            for (int i = 0; i < screen.starCount(); i++)
                            {
                                if (Thread.currentThread().isInterrupted()) return links;
                                VisibleStar star = screen.starAt(i);
                                if (star.getBrightness() > mCanvas.getBrightness()) break; // not visible
                                int diffx = Math.abs((int)finalMatrix.get(0,j)-star.getX());
                                int diffy = Math.abs((int)finalMatrix.get(1,j)-star.getY());
                                int distance = diffx + diffy;
                                if (distance < diffThreshold)
                                {
                                    // Exclude stars already in use.
                                    boolean alreadyUsed = false;
                                    for (int k = 0; k < j; k++)
                                    {
                                        if (index[k] == i)
                                        {
                                            alreadyUsed = true;
                                            break;
                                        }
                                    }
                                    if (alreadyUsed) continue;
                                    
                                    error += distance;
                                    index[j] = i; // index of matching star

                                    // Remaining stars are less bright: skip.
                                    break;
                                }
                            }
                        }

                        int misses = 0;
                        for (int j = 0; j < columnCount; j++)
                        {
                            if (index[j] == -1) misses++;
                        }

                        if (misses == 0)
                        {
                            Set <Link> currentLinks = pattern.getLinks(index, mCanvas);
                            firePropertyChange("currentLinks", null, Integer.valueOf(links.size()));

                            int noise = getStarNoise(screen, currentLinks, scale);

                            links.add(currentLinks);

                            error /= columnCount;

                            int weightedError = error * mErrorToNoiseWeight + noise;

                            // save best match

                            if (weightedError < minError)
                            {
                                minError = weightedError;
                                bestMatch = currentLinks;
                            }
                        }
                    }
                }
            }
        } // end rotation loop

        // TODO: Cannot change color of graphics context without passing it as an argument.

        Graphics gc = mCanvas.getGraphics();
        Color savedColor = gc.getColor();
        gc.setColor(Color.RED);
        if (bestMatch != null)
            for (Link link : bestMatch)
            {
                mCanvas.viewStarLink(link.beg, link.end);
            }
        gc.setColor(savedColor);

        return links;
    }

    /**
     * Quanify the visual noise in the vicinity of a pattern-constellation.
     *
     * @param screen the screen containing the visible
     * @param links the pattern, matched to stars on the screen.
     * @param scale the scale that the pattern is matched at.
     * 
     * @return a quantification of the visual noise in the vicinity.
     */

    private int getStarNoise(Screen screen, Set <Link> links, double scale)
    {
        int noise = 0;

        for (int i = 0; i < screen.starCount(); i++)
        {
            // noiseWeight decreases from (2 * count) to count (shouldn't diminish to zero).
            int noiseWeight = screen.starCount() * 2 - i;
            // int dist = 0;
            VisibleStar starC = screen.starAt(i);
            int minDist = Integer.MAX_VALUE;

            for (Link link : links)
            {
                int dist = Integer.MAX_VALUE;
                if (i != link.beg && i != link.end)
                {
                    VisibleStar starA = screen.starAt(link.beg);
                    VisibleStar starB = screen.starAt(link.end);

                    int lengthAB = starA.distanceTo(starB);
                    int lengthAC = starA.distanceTo(starC);
                    int lengthBC = starB.distanceTo(starC);

                    if (lengthAC != 0 && lengthBC != 0)
                        dist = getStarDistToLink(lengthAB, lengthAC, lengthBC);
                    else dist = 0;
                }
                else
                {
                    dist = 0;
                }

                if (dist < minDist)
                {
                    minDist = dist;
                }
            }

            /* We have the minimum distance, that is, the distance from this star to the closest point on the
               constellation. Next, we need to evaluate that distance as noise. Stars that are very close to
               the constellation strengthen its visibility, but all other stars in the proximity are visual noise.
               In general, are the closer stars noisier that the more marginal, remote stars? That might depend on the
               pattern.
            */

            int threshold = (int) (scale * mNoiseThreshold);

            if (minDist < threshold)
            {
                // close enough to support pattern; increases with distance
                noise += noiseWeight * minDist;
            }
            else if (minDist < scale * mNoiseHorizon)
            {
                // close enough to be noise
                noise += noiseWeight * threshold;
            }
        }

        // bigger scale means bigger distance means bigger noise values.

        return noise / (int)(scale * scale);
    }

    @SuppressWarnings("unused")
	private static int getStarDistToLink(Screen screen, int starIndex, Link link)
    {
        if (starIndex != link.beg && starIndex != link.end)
        {
            VisibleStar starA = screen.starAt(link.beg);
            VisibleStar starB = screen.starAt(link.end);
            VisibleStar starC = screen.starAt(starIndex);

            int lengthAB = starA.distanceTo(starB);
            int lengthAC = starA.distanceTo(starC);
            int lengthBC = starB.distanceTo(starC);

            if (lengthAB != 0 && lengthAC != 0 && lengthBC != 0)
                return getStarDistToLink(lengthAB, lengthAC, lengthBC);
            else return 0;
        }
        else
        {
            return 0;
        }
    }

    /**
     * Determine the minimum screen distance from a star to a line segment.
     *
     * @param lengthAB the on-screen distance between two linked stars.
     * @param lengthAC the on-screen distance between an unlinked star and one of two linked stars.
     * @param lengthBC the on-screen distance between an unlinked star and one of two linked stars.
     *
     * @return the minimum distance between t5he star and the link.
     */

    private static int getStarDistToLink(int lengthAB, int lengthAC, int lengthBC)
    {
        int dist = 0;
        double cosA = -(lengthBC*lengthBC-lengthAB*lengthAB-lengthAC*lengthAC)/(2*lengthAB*lengthAC);
        double cosB = -(lengthAC*lengthAC-lengthBC*lengthBC-lengthAB*lengthAB)/(2*lengthAB*lengthBC);
        double a = Math.acos(cosA);
        double b = Math.acos(cosB);

        if (a > TWO_PI/4.0)
        {
            dist = lengthAC;
        }
        else if (b > TWO_PI/4.0)
        {
            dist = lengthBC;
        }
        else
        {
            dist = (int) (lengthAC * Math.sin(a));
        }

        return dist;
    }

}
