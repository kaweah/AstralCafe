/*
 *  SkyViewCanvas.java
 *  Astral Cafe
 *
 *  Copyright (c) 2004 Kaweah. All rights reserved.
 *
 *  Author: Dan Jensen
 *	Date of most recent change: 2004.09.30
 *	
 *	javac com/kaweah/astralcafe/SkyViewCanvas.java
 */

package com.kaweah.astralcafe;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.io.IOException;

import com.kaweah.astralcafe.model.Link;
import com.kaweah.astralcafe.model.Projection;
import com.kaweah.astralcafe.model.Star;
import com.kaweah.astralcafe.model.VisibleStar;
import com.kaweah.astralcafe.model.linkGroup;
import com.kaweah.astralcafe.model.starSet;
import com.kaweah.astralcafe.starbinder.SimpleBinder;
import com.kaweah.astralcafe.starbinder.StarBinder;

/**
 * Abstract {@link Canvas} that implements {@link SkyViewCanvasWrapper} only
 * with abstract methods. Partially implemented by {@link AstralCanvas}.
 *
 * @author Dan Jensen, <a href="http://kaweah.com">Kaweah Concepts</a>
 * @version 1.0
 */

@SuppressWarnings("serial")
public abstract class SkyViewCanvas extends Canvas implements SkyViewCanvasWrapper
{
    public static final int CM_NONE           = 0;
    public static final int CM_ADJACENT_STARS = 1;
    public static final int CM_USER_PATTERNS  = 2;
    public static final int CM_CONSTELLATIONS = 3;

	public static final int COLOR_STAR_MODE = 1 ;
	public static final int WHITE_STAR_MODE = 2 ;
	public static final int BLACK_STAR_MODE = 3 ;
	
	public static final int NO_LABEL_MODE = 0 ;
	public static final int STAR_NAME_MODE = 1 ;
	public static final int GREEK_LETTER_MODE = 2 ;

	protected static final double MIN_VIEW_BREADTH_ANGLE = 1.0;
	protected static final double MAX_VIEW_BREADTH_ANGLE = 90.0;

	protected Projection theProj ;
	protected starSet theStars ;
	protected ScrollingCanvasPanel scrollBarPanel = null;
	protected StarBinder binder;
	protected linkGroup theLinks ;
	protected int brightness = 3 ;
    protected int constellationMode = 0;
	protected boolean drawConstelsOnly = false;
	protected boolean refreshNeeded = true;
	protected int selectedStarIndex = -1;
	protected VisibleStar firstStar = null;
	protected VisibleStar secondStar = null;
	protected java.awt.Dimension oldSize ;

	protected int colorMode = COLOR_STAR_MODE;
	protected int starLabelMode = NO_LABEL_MODE;

	protected Color foregroundColor, backgroundColor;
	
	protected linkGroup customLinks = null;
	
	// static Logger logger = Logger.getLogger(SkyViewCanvas.class.getName()); 

    abstract public void init() throws IOException;

    public int getConstelVisibility()
    {
        return constellationMode;
    }

	public void
	setConstelVisibility(int cm)
	{
        if (constellationMode == 0)
        {
            if (cm != 0) // switching on
            {
                refreshNeeded = false;
                drawConstelsOnly = true;
                constellationMode = cm ;
                repaint() ;
            }
            else
            {
                // no action needed.
            }
        }
        else
        {
            constellationMode = cm ;
            refreshNeeded = true;

            if (cm == 0) // switching off
            {
                drawConstelsOnly = false;
                repaint() ;
            }
            else // changing mode
            {
                drawConstelsOnly = true;
                repaint() ;
            }
        }
	}

	
	public void setColorMode(int mode)
	{
		if (colorMode == BLACK_STAR_MODE)
		{
			if (mode != BLACK_STAR_MODE)
			{
				colorMode = mode;
				foregroundColor = Color.white;
				backgroundColor = Color.black;
				refreshNeeded = true;
				repaint();
			}
		}
		else
		{
			if (mode == BLACK_STAR_MODE)
			{
				colorMode = mode;
				foregroundColor = Color.black;
				backgroundColor = Color.white;
				refreshNeeded = true;
				repaint();
			}
			else if (mode != colorMode)
			{
				colorMode = mode;
				foregroundColor = Color.white;
				backgroundColor = Color.black;
				refreshNeeded = true;
				repaint();
			}					
		}
	
	}
	
	protected final void
	clear(Graphics g)
	{	
		Color savedColor = g.getColor();
		
		g.setColor(backgroundColor);
		
		g.fillRect( 0, 0, getSize().width, getSize().height ) ;

		g.setColor(savedColor) ;
	}


	protected boolean
	resized()
	{
		return ( oldSize != getSize() );
	}

	public void
	update( Graphics g )
	{
		paint(g);
	}

	protected final void
	drawStars(Graphics g)
	{
		//	since this is where we'll end up immediately after the canvas is resized,
		//	let's check to see if the canvas has been resized, and if so,
		//	recalculate the projection.

		if ( resized() )
		{
			oldSize = getSize();
			calcScreen();		
		}

		for ( int i = 0 ; i < starsOnScreen() ; i++ )
		{
			VisibleStar thePt = starOnScreenAt( i ) ;
			
			// logger.debug( "count=" + i + "; x=" + thePt.getX() + "; y=" + thePt.getY() ) ;
			
			viewStar( g, thePt, true ) ;
		}
	}
	
	protected final void
	drawStandardConstels()
	{
        Graphics g = getGraphics();

        g.setColor( foregroundColor ) ;

        for( int i = 0 ; i < theLinks.getSize() ; i++ )
        {
            int beg = getLinkPointA(i) ;
            int end = getLinkPointB(i) ;

            if ( beg != -1 && end != -1 )
            {
                viewStarLink(starOnScreenAt(beg), starOnScreenAt(end));
            }
        }

        int customLinkCt = customLinks.getSize();

        if (customLinkCt > 0)
        {
            switch (colorMode)
            {
                case WHITE_STAR_MODE:
                case BLACK_STAR_MODE:
                    g.setColor(foregroundColor) ;
                    break;
                case COLOR_STAR_MODE:
                default:
                    g.setColor(Color.blue) ;
                    break;
            }

            for( int i = 0 ; i < customLinkCt ; i++ )
            {
                Link l = (Link) customLinks.getLink(i);

                int beg = getIndexEntryAt(l.getBeg());
                int end = getIndexEntryAt(l.getEnd()) ;

                if ( beg != -1 && end != -1 )
                {
                    viewStarLink(starOnScreenAt(beg), starOnScreenAt(end));
                }
            }
        }
	}

    /**
     * Draw automatically-generated constellations.
     *
     */

    protected final void
    drawConstels()
    {
        switch (constellationMode)
        {
            case CM_CONSTELLATIONS:
                drawStandardConstels();
                break;
            case CM_ADJACENT_STARS:
                SimpleBinder binder = new SimpleBinder(this);
                binder.execute();
                break;
            case CM_USER_PATTERNS:
                getGraphics().setColor(foregroundColor);
                StarBinder patternBinder = new StarBinder(this);
                patternBinder.execute();
                break;
            default:
                break;
        }
    }


	protected final void
	drawStarInfo(Graphics g, VisibleStar vStar)
	{
		if (vStar != null)
		{
			Star star = vStar.getStar();
			String name = star.getName().trim();
			String greekLetter = star.getGreek().trim();
			
			Color savedColor = g.getColor();
			
			switch (colorMode)
			{
				case WHITE_STAR_MODE:
				case BLACK_STAR_MODE:
					g.setColor(foregroundColor) ;
					break;
				case COLOR_STAR_MODE:
				default:
					g.setColor( vStar.getColor() ) ;
					break;
			}

			if (name.length() > 0)
			{
				if (greekLetter.length() > 0)
				{
					name = name + " (" + greekLetter + ")";
				}
			}
			else
			{
				if (greekLetter.length() > 0)
				{
					name = greekLetter;
				}
				else
				{
					name = "-";
				}
			}
			
			double ra = (int)(star.getRA()*100);
			double dec = (int)(star.getDec()*100);
			double mag = (int)(star.getMagnitude()*100);
			double abs = (int)(star.getAbsMag()*100);
			
			int lineOffset = 15;
			int panelOffset = 10;
			int x = vStar.getX() + panelOffset;
			int y = vStar.getY();
			
			g.drawString(name, x, y);
			y += lineOffset;
			g.drawString("RA=" + ra/100.0 + " Dec=" + dec/100.0, x, y);
			y += lineOffset;
			g.drawString("Mag=" + mag/100.0 + " Abs=" + abs/100.0, x, y);
			g.setColor(savedColor);	
		}
	}

	//ÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑ
	
	protected final void
	viewStar( Graphics g, VisibleStar pt, boolean fillFlag )
	{
		int scrMag, mdiam, ht, vt ;
		Rectangle s_rect = new Rectangle() ;
	  
		//	wait until indexing is installed
		// scrMag = (int) d->Brightness + d->sky->raw[st]->nineScale() - 7 ;
		
		scrMag = brightness - pt.getBrightness() + 2 ;
		
		if ( scrMag < 1 ) return ;
	
		mdiam = scrMag / 2 ;
	
		ht = pt.getX() - mdiam ;
		vt = pt.getY() - mdiam ;
	    
		s_rect.x   = ht ;
		s_rect.y    = vt ;
		s_rect.width  = scrMag ;
		s_rect.height = scrMag ;
		
		Color savedColor = g.getColor();
		
	  	switch (colorMode)
	  	{
	  		case WHITE_STAR_MODE:
	  		case BLACK_STAR_MODE:
	  			g.setColor(foregroundColor) ;
	  			break;
	  		case COLOR_STAR_MODE:
	  		default:
	  			g.setColor( pt.getColor() ) ;
	  			break;
	  	}
	  	
		if( fillFlag )
		{
			// g.drawString( tempInt.toString(), s_rect.x, s_rect.y ) ;
			g.fillOval( s_rect.x, s_rect.y, s_rect.width, s_rect.height ) ;
		}
		else
		{
			// g.drawString( tempInt.toString(), s_rect.x, s_rect.y ) ;
			g.drawOval( s_rect.x, s_rect.y, s_rect.width, s_rect.height ) ;
		}
					
		String name = null;
		
		if (selectedStarIndex == pt.getIndex())
		{
			drawStarInfo(g, pt);
		}
		else
		{
			switch (starLabelMode)
			{
				case STAR_NAME_MODE:
					name = pt.getStar().getName();
					g.drawString(name, s_rect.x + 5, s_rect.y);
					break;
				case GREEK_LETTER_MODE:
					name = pt.getStar().getGreek();
					g.drawString(name, s_rect.x + 5, s_rect.y);
					break;
				default:
					break;
			}
		}
		
		g.setColor(savedColor);
	}

    public final void
    viewStarLink(int beg, int end)
    {
        VisibleStar pt1 = starOnScreenAt( beg );
        VisibleStar pt2 = starOnScreenAt( end );

        viewStarLink(pt1, pt2);
    }

	private final void
	viewStarLink(VisibleStar pt1, VisibleStar pt2)
	{
		int scrMag ;
	  
		//	wait until indexing is installed
		// scrMag = (int) d->Brightness + d->sky->raw[st]->nineScale() - 7 ;
		
		scrMag = brightness - pt1.getBrightness() + 2 ;
		
		if ( scrMag < 1 ) return ;
	
		scrMag = brightness - pt2.getBrightness() + 2 ;
		
		if ( scrMag < 1 ) return ;
		
		// Color savedColor = g.getColor();
		
	  	// g.setColor(foregroundColor) ;
	  
		getGraphics().drawLine( pt1.getX(), pt1.getY(), pt2.getX(), pt2.getY() ) ;
		
		// g.setColor(savedColor);
		
	}

	public void setStarLabelMode(int mode)
	{
		if (starLabelMode != NO_LABEL_MODE)
		{
			if (mode != starLabelMode)
			{
				refreshNeeded = true;
				starLabelMode = mode;
				repaint();
			}
		}
		else
		{
			if (mode != starLabelMode)
			{
				refreshNeeded = false;
				starLabelMode = mode;
				repaint();
			}
		}
	}

	protected VisibleStar findStarNearScreenPos(int x, int y, int maxDist)
	{
		int minDist = maxDist;
		VisibleStar closestStar = null;
	
		for (int i = 0; i < starsOnScreen(); i++)
		{
			VisibleStar vStar = starOnScreenAt(i);
			int deltax = Math.abs(vStar.getX() - x);
			int deltay = Math.abs(vStar.getY() - y);
			int dist = deltax+deltay; // approximation
			if (dist < minDist)
			{
				minDist = dist;
				closestStar = vStar;
				if (dist < 1) break;
			}
		}
		
		return closestStar;
	}

    public int getBrightness()
    {
        return brightness;
    }

    public Projection getProjection()
    {
        return theProj;
    }
    
    public StarBinder getStarBinder()
    {
    	return binder;
    }

	abstract public void setRA(double ra);
	abstract public void setDec(double dec);
	abstract public void setViewBreadthAngle(double vba);
	abstract public void setBrightness(int brightLevel);
	
	abstract public double getHOrigin();
	abstract public double getVOrigin();
	abstract public void calcScreen();
	abstract public void storeCustomLinks();
	
	abstract protected VisibleStar starOnScreenAt(int i);
	
	abstract protected int getIndexEntryAt(int i);

	abstract protected int getLinkPointA(int i);
	abstract protected int getLinkPointB(int i);

	abstract protected int starsOnScreen();
}
