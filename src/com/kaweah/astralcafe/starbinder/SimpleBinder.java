package com.kaweah.astralcafe.starbinder;

import com.kaweah.astralcafe.SkyViewCanvas;
import com.kaweah.astralcafe.model.Link;
import com.kaweah.astralcafe.model.Screen;
import com.kaweah.astralcafe.model.VisibleStar;

import org.jdesktop.swingworker.SwingWorker;

import java.util.HashSet;
import java.util.Set;

/**
 * Simple star binder. Links stars with sufficient brightness and mutual proximity.
 *
 * AstralCafe Star and Constellation Viewer
 * ©2010 Dan Jensen, Kaweah.com
 */

public class SimpleBinder extends SwingWorker <Set <Link>, Object>
{
    private static Set <SimpleBinder> instances = new HashSet <SimpleBinder> ();

    private SkyViewCanvas mCanvas;

    /**
     * The maximum linkable distance for the brightest stars on the current screen.
     */

	public static int lowerMaxDistance = 20;

    /**
     * The maximum linkable distance for the dimmest stars on the current screen.
     */

	public static int upperMaxDistance = 30;

    /**
     * The maximum links allowed per star.
     * Must be greater than zero. Should be less than five.
     */

	public static int maxLinksPerStar = 3;

	public SimpleBinder(SkyViewCanvas canvas)
	{
        mCanvas = canvas;

        // cancel all other threads. Run only one at a time.
        for (SimpleBinder binder : instances)
        {
            binder.cancel(true);
        }
        instances.clear();
        instances.add(this);
        // addPropertyChangeListener(canvas);
	}

    @Override
    protected Set <Link> doInBackground() throws Exception
    {
        Set <Link> links = findLinks(mCanvas.getProjection().theScreen);

        mCanvas.getGraphics().setColor( mCanvas.getForeground()) ;

        for(Link link : links)
        {
            int beg = link.beg ;
            int end = link.end ;

            if ( beg != -1 && end != -1 )
            {
                mCanvas.viewStarLink(link.beg, link.end);
            }
        }

        return links;
    }

    /**
     * Find natural links between adjacent bright stars.
     *
     * @return a Set of Link objects.
     */

	public static Set <Link> findLinks(Screen screen)
	{
		Set <Link> links = new HashSet<Link>();
		int[] linksForStar = new int[screen.starCount()];

		for (int i = 0; i < screen.starCount()-1; i++)
		{
			if (linksForStar[i] < maxLinksPerStar)
			{
				VisibleStar origin = screen.starAt(i);
				for (int j = i+1; j < screen.starCount(); j++)
				{
					if (linksForStar[j] < maxLinksPerStar)
					{
						int distance = origin.distanceTo(screen.starAt(j));
						int maxDistance = lowerMaxDistance + (upperMaxDistance-lowerMaxDistance) * (1 - j / screen.starCount());
						if (distance <= maxDistance)
						{
							links.add(new Link(i, j));
							linksForStar[i]++;
							linksForStar[j]++;
						}
					}
				}
			}
		}

		return links;
	}
}
