package com.kaweah.astralcafe.starbinder;

import com.kaweah.astralcafe.SkyViewCanvas;
import com.kaweah.astralcafe.model.Link;
// import org.apache.commons.math.linear.MatrixUtils;
// import org.apache.commons.math.linear.RealMatrix;
import Jama.Matrix;

import java.util.*;
import java.util.List;

/**
 * AstralCafe Star and Constellation Viewer
 * ©2010 Dan Jensen, Kaweah.com
 */

class StarPattern
{
    public static double[][] smileyPoints =
    {
        {0.5,0.433,0.25 ,0.0,-0.25 ,-0.433,-0.5,-0.433,-0.25 , 0.0, 0.25 , 0.433,-0.217,-0.125, 0.0 , 0.125, 0.217,0.0,-0.05 ,0.05},
        {0.0,0.25 ,0.433,0.5, 0.433, 0.25 , 0.0,-0.25 ,-0.433,-0.5,-0.433,-0.25 ,-0.125,-0.217,-0.25,-0.217,-0.125,0.0, 0.087,0.087}
    };
    public static double[][] smileyLinks =
    {
        {0,1,2,3,4,5,6,7,8, 9,10,11,12,13,14,15},
        {1,2,3,4,5,6,7,8,9,10,11, 0,13,14,15,16}
    };
    public static double[][] stickmanPoints = {{0.0,0.0,0.0,-1.0,1.0,-1.0,1.0},{1.5,0.5,-0.5,0.0,0.0,-1.5,-1.5}};
    public static double[][] stickmanLinks = {{0,1,1,1,2,2},{1,2,3,4,5,6}};
    public static double[][] starPoints = {{0.0,1.5,1.0,-1.0,-1.5},{1.5,0.5,-1.5,-1.5,0.5}};
    public static double[][] starLinks = {{0,2,4,1,3},{2,4,1,3,0}};
    private Matrix mVertices;
    private Matrix mLinks;
    public static List <StarPattern> patterns;

    public static void init()
    {
        if (patterns == null)
        {
            patterns = new ArrayList <StarPattern> ();
            // patterns.add(new StarPattern(smileyPoints, smileyLinks));
            patterns.add(new StarPattern(stickmanPoints, stickmanLinks));
            patterns.add(new StarPattern(starPoints, starLinks));
        }
    }

	public StarPattern(double[][] vertices, double[][] links)
	{
        mVertices = new Matrix(vertices);
        normalize();
        mLinks = new Matrix(links);
	}

    /**
     * Center a pattern about the origin and scale it so that its area (width * height) equals 1.0.
     */

    private void normalize()
    {
        double minx = Double.MAX_VALUE;
        double maxx = -Double.MAX_VALUE;
        double miny = Double.MAX_VALUE;
        double maxy = -Double.MAX_VALUE;

        for (int j = 0; j < mVertices.getColumnDimension(); j++)
        {
            double x = mVertices.get(0,j);
            double y = mVertices.get(1,j);

            if (x < minx) minx = x;
            if (x > maxx) maxx = x;
            if (y < miny) miny = y;
            if (y > maxy) maxy = y;
        }

        double width = maxx - minx;
        double height = maxy - miny;
        double scalar = 1.0 / Math.sqrt(width * height);
        double xcenter = minx + width / 2.0;
        double ycenter = miny + height / 2.0;

        for (int j = 0; j < mVertices.getColumnDimension(); j++)
        {
            double x = (mVertices.get(0,j) - xcenter) * scalar;
            double y = (mVertices.get(1,j) - ycenter) * scalar;

            mVertices.set(0,j,x);
            mVertices.set(1,j,y);
        }
    }

    public Matrix getVertices()
    {
        return mVertices;
    }

    /**
     * Determines and optionally draws links from an array of screen (visible star) indices.
     *
     * @param index
     * @param canvas
     * @return links associated with provided indices.
     */

    public Set <Link> getLinks(int[] index, SkyViewCanvas canvas)
    {
        Set <Link> links = new HashSet <Link> ();
        for (int i = 0; i < mLinks.getColumnDimension(); i++)
        {
            int beg = (int) mLinks.get(0,i);
            int end = (int) mLinks.get(1,i);
            if (index[beg] != -1 && index[end] != -1)
            {
                links.add(new Link(index[beg], index[end]));
                if (canvas != null)
                    canvas.viewStarLink(index[beg], index[end]);
            }
        }

        return links;
    }
}
