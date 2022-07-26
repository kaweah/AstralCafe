/*
 *  AstralCanvas.java
 *  Astral Cafe
 *
 *  Copyright (c) 2004 Kaweah. All rights reserved.
 *	
 *  Author: Dan Jensen
 *	Date of most recent change: 2004.10.01
 *	
 *	javac -classpath .:log4j-1.2.8.jar com/kaweah/astralcafe/AstralCanvas.java
 */

package com.kaweah.astralcafe;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.Math;

import com.kaweah.astralcafe.model.*;
import com.kaweah.astralcafe.starbinder.StarBinder;

/**
 * Partial implementation of the {@link SkyViewCanvas} abstract class.
 *
 * @author Dan Jensen, <a href="http://kaweah.com">Kaweah Concepts</a>
 * @version 1.0
 */

@SuppressWarnings("serial")
public abstract class AstralCanvas extends SkyViewCanvas
implements MouseMotionListener, PropertyChangeListener
{
	protected int xprev, yprev ;
	protected int xpos, ypos, xi, yi ;
		
	protected Point2DDouble mouseDownPos ;
		
	protected static final int DEFAULT_DRAWMODE = 0 ;
	protected static final int DRAW_DRAWMODE = DEFAULT_DRAWMODE+1 ;
	protected static final int REDRAW_DRAWMODE = DRAW_DRAWMODE+1 ;
	protected static final int CLEANUP_DRAWMODE = REDRAW_DRAWMODE+1 ;
	protected static final int XHAIRS = CLEANUP_DRAWMODE+1 ;

	public static final int REFRAME_MODE = 1 ;
	public static final int IDENTIFY_MODE = 2 ;
	public static final int LINK_MODE = 3 ;
	
	protected int drawMode = DEFAULT_DRAWMODE ;
	
	protected int navMode = REFRAME_MODE;
		
	// static Logger logger = Logger.getLogger(AstralCanvas.class.getName()); 

	public AstralCanvas(ScrollingCanvasPanel scroller)
	{
		super();
	
		scrollBarPanel = scroller;
		
		mouseDownPos = new Point2DDouble();
		
		addMouseListener(new ACMouseAdapter());
		addMouseMotionListener(this);

		foregroundColor = Color.white;
		backgroundColor = Color.black;
		setBackground(backgroundColor);
		setForeground(foregroundColor);
		
		customLinks = new linkGroup();
	
		binder = new StarBinder(this);
	}
	

	public void setNavMode(int mode)
	{
		if (navMode == LINK_MODE)
		{
			if (mode != LINK_MODE)
			{
				selectedStarIndex = -1;
				storeCustomLinks();
			}
		}
		
		navMode = mode;
		
	}

	public void mouseMoved(MouseEvent e)
	{
		xpos = e.getX();
		ypos = e.getY();
	}


	public void mouseDragged(MouseEvent e)
	{
		xpos = e.getX();
		ypos = e.getY();
		
		switch (navMode)
		{
			case REFRAME_MODE:
			
				switch (drawMode)
				{
					case DEFAULT_DRAWMODE:
						xi = xpos;
						yi = ypos;
						mouseDownPos = findPos( xpos, ypos ) ;
						drawMode = DRAW_DRAWMODE ; // initial draw state
						// logger.debug("mouseDragged(): entering DRAW_DRAWMODE.");
						break;
					case DRAW_DRAWMODE:
						// logger.debug("mouseDragged(): still in DRAW_DRAWMODE.");
						repaint();
						break;
					case REDRAW_DRAWMODE:
						// logger.debug("mouseDragged(): still in REDRAW_DRAWMODE.");
						repaint();
						break;
				}
				break;
				
			case IDENTIFY_MODE:
				break;
				
			case LINK_MODE:
				switch (drawMode)
				{
					case DEFAULT_DRAWMODE:
						xi = xpos;
						yi = ypos;
						firstStar = findStarNearScreenPos(xpos, ypos, 5);
						if (firstStar != null)
						{
							// logger.debug("Found first star for link.");
							drawMode = DRAW_DRAWMODE ; // initial draw state
						}
						break;
					case DRAW_DRAWMODE:
					case REDRAW_DRAWMODE:
						repaint();
						break;
				}
				break;
				
			default:
				break;
		}
	}

	//ÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑ

	protected class ACMouseAdapter extends MouseAdapter
	{
		public void mousePressed(MouseEvent e)
		{
			xpos = e.getX();
			ypos = e.getY();
			
			switch (navMode)
			{
				case REFRAME_MODE:
					if (drawMode == DEFAULT_DRAWMODE)
					{
						xi = xpos;
						yi = ypos;
						drawMode = DRAW_DRAWMODE ; // initial draw state
						
						// save mouseDownPos, for either click-to-move or drag-to-reframe
						
						mouseDownPos = findPos( xpos, ypos ) ;

						// logger.debug("mousePressed(): entering DRAW_DRAWMODE.");						
					}
					break;
					
				case IDENTIFY_MODE:
					break;
					
				case LINK_MODE:
					if (drawMode == DEFAULT_DRAWMODE)
					{
						xi = xpos;
						yi = ypos;
						firstStar = findStarNearScreenPos(xpos, ypos, 5);
						if (firstStar != null)
						{
							// logger.debug("Found first star for link.");
							drawMode = DRAW_DRAWMODE ; // initial draw state
						}
					}
					break;
					
				default:
					break;
			}
			
		}
	
	
		public void mouseReleased(MouseEvent e)
		{
			xpos = e.getX();
			ypos = e.getY();
		
			switch (navMode)
			{
				case REFRAME_MODE:

					if (drawMode == REDRAW_DRAWMODE || drawMode == DRAW_DRAWMODE)
					{
						Point2DDouble selection = new Point2DDouble() ;
						double deltax, deltay;
						
						// TODO: Dead code
						if (false)
						{
							selection = findPos(xpos, ypos) ;
							
							// Find displacement, in real sky coordinates, between mouse-down
							// and mouse-up positions. If the selection box has crossed over ra=0,
							// this value will be incorrect, so we must corrct it by adjusting it
							// to within mod-12.
							
							deltax = selection.getX() - mouseDownPos.getX() ;
							deltay = selection.getY() - mouseDownPos.getY() ;
							
							if (deltax < -12.0) deltax += 24.0;
							else if (deltax > 12.0) deltax -= 24.0;
							
							selection.setX(selection.getX() - deltax / 2);
							selection.setY(selection.getY() - deltay / 2);
						}
						else
						{
							selection = findPos( xi+ (xpos-xi)/2, yi + (ypos-yi)/2 ) ;
		
							deltax = selection.getX() - mouseDownPos.getX() ;
							deltay = selection.getY() - mouseDownPos.getY() ;
							
							if (deltax < -12.0) deltax += 24.0;
							else if (deltax > 12.0) deltax -= 24.0;
							
							deltax *= 2.0;
							deltay *= 2.0;
						}
						
						setPosition( selection ) ;
						scrollBarPanel.updateScrollBars() ;
						
						// setViewBreadthAngle: first convert deltax to degrees.
		
						deltax = Math.abs( deltax ) * 15.0 ;
						deltay = Math.abs( deltay ) ;
						
						double delta = ( deltay + deltax ) / 2.0; // midpoint between deltas
						if (delta > 180.0) delta = 180.0; // screen out large and small values.
						if (delta > 1.0) setViewBreadthAngle(delta);
			
						// logger.info("New View Width: ");
						// logger.info( delta + " degrees." );
			
						drawMode = CLEANUP_DRAWMODE ; // finished drawing box.

						// logger.debug("mousePressed(): entering CLEANUP_DRAWMODE.");
					}
					
					repaint() ;
					break;
				case IDENTIFY_MODE:
					VisibleStar selectedStar = findStarNearScreenPos(xpos,ypos,5);
					if (selectedStar != null)
					{
						selectedStarIndex = findStarNearScreenPos(xpos,ypos,5).getIndex();
						repaint() ;
					}
					break;
					
				case LINK_MODE:
					if (drawMode == REDRAW_DRAWMODE)
					{
						secondStar = findStarNearScreenPos(xpos,ypos,5);
						if (secondStar != null)
						{
							// logger.debug("Found second star for link.");
							customLinks.addElement(
								new Link(firstStar.getIndex(), secondStar.getIndex()));
						}
						drawMode = CLEANUP_DRAWMODE ; // finished drawing.
						repaint() ;
					}
					break;
					
				default:
					break;
			}
		}
	}


	private final void
	drawBox( Graphics g, int x, int y )
	{
		// logger.debug("drawBox("+x+","+y+")");

		if ( x >= xi && y >= yi )
		{
			g.drawRect( xi, yi, x-xi, y-yi ) ;
		}
		else if ( x < xi && y >= yi )
		{
			g.drawRect( x, yi, xi-x, y-yi ) ;
		}
		else if ( x >= xi && y < yi )
		{
			g.drawRect( xi, y, x-xi, yi-y ) ;
		}
		else if ( x < xi && y < yi )
		{
			g.drawRect( x, y, xi-x, yi-y ) ;
		}
	}


	@Override
	public void
	paint( Graphics g )
	{
		Color savedColor = g.getColor();

		switch (navMode)
		{
			case REFRAME_MODE:
			
				switch ( drawMode )
				{
		
					case DRAW_DRAWMODE:
		
						g.setColor(Color.red) ;
						g.setXORMode(backgroundColor);
				
						drawBox( g, xpos, ypos ) ;
						
						g.setPaintMode();

						xprev=xpos;
						yprev=ypos;
				
						drawMode = REDRAW_DRAWMODE;
				
						// logger.debug("paint(): entering REDRAW_DRAWMODE.");
						break ;
		
					case REDRAW_DRAWMODE:
					
						g.setColor(Color.red) ;
						g.setXORMode(backgroundColor);

						// Undraw previous
										
						drawBox( g, xprev, yprev ) ;		
		
						// draw new rectangle
					
						drawBox( g, xpos, ypos ) ;	
						
						g.setPaintMode();

						xprev=xpos;
						yprev=ypos;
		
						// logger.info("paint(): maintaining REDRAW_DRAWMODE.");
						break ;			
						
					case XHAIRS:
					
						//	draw yellow crosshairs
					
						g.setColor( Color.yellow ) ;
						
						g.drawLine( xpos,	ypos-5,	xpos,	ypos+5	) ;
						g.drawLine( xpos-5,	ypos,	xpos+5,	ypos	) ;
						
						break ;
		
					case CLEANUP_DRAWMODE:

						//	undraw previous
										
						drawMode = DEFAULT_DRAWMODE;
						clear(g);
						drawStars(g);
						drawConstels();
						
						// logger.info("paint(): entering DEFAULT_DRAWMODE.");
						break;

					default:
		
						if (refreshNeeded)
						{
							clear(g);
							drawStars(g);
							drawConstels();
							refreshNeeded = false;
						}
						else if (drawConstelsOnly)
						{
							drawConstels();
							drawConstelsOnly = false;
						}
						else
						{
							drawStars(g);
							drawConstels();
						}

						break;
										
				}
				break;
				
			case IDENTIFY_MODE:

				if (refreshNeeded)
				{
					clear(g);
					drawStars(g);
					drawConstels();
					refreshNeeded = false;
				}
				else if (drawConstelsOnly)
				{
					drawConstels();
					drawConstelsOnly = false;
				}
				else
				{
					drawStars(g);
					drawConstels();
				}

				break;
				
			case LINK_MODE:

				switch ( drawMode )
				{		
					case DRAW_DRAWMODE:
		
						g.setColor(Color.blue) ;
						g.setXORMode(backgroundColor);
				
						g.drawLine(firstStar.getX(), firstStar.getY(), xpos, ypos) ;

						g.setPaintMode();

						xprev=xpos;
						yprev=ypos;
				
						drawMode = REDRAW_DRAWMODE;

						break ;
		
					case REDRAW_DRAWMODE:
					
						//	undraw previous
										
						g.setXORMode(Color.blue) ;
						g.setColor(backgroundColor) ;

						g.drawLine(firstStar.getX(), firstStar.getY(), xprev, yprev) ;
		
						//	draw new rectangle
					
						g.drawLine(firstStar.getX(), firstStar.getY(), xpos, ypos) ;
						
						g.setPaintMode();

						xprev=xpos;
						yprev=ypos;
		
						break ;
					
					case CLEANUP_DRAWMODE:

						drawMode = DEFAULT_DRAWMODE;
	
						clear(g);
						drawStars(g);
						drawConstels();
						break;

					default:
					
						if (refreshNeeded)
						{
							clear(g);
							drawStars(g);
							drawConstels();
							refreshNeeded = false;
						}
						else if (drawConstelsOnly)
						{
							drawConstels();
							drawConstelsOnly = false;
						}
						else
						{
							drawStars(g);
							drawConstels();
						}
						
						break;
				}
				
				break;
				
			default:
				break;
		}
		
		g.setColor(savedColor);	
	}
	
    public void propertyChange(PropertyChangeEvent evt)
    {
        // SwingWorker.StateValue constelCount = (SwingWorker.StateValue) evt.getNewValue();
    }

	abstract protected Point2DDouble findPos(int x, int y);

	abstract protected void setPosition(Point2DDouble selection);

}

