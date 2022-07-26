/*
 *  ProjectingAC.java
 *  Astral Cafe
 *
 *  Copyright (c) 2004 Kaweah. All rights reserved.
 *
 *  Author: Dan Jensen
 *	Date of most recent change: 2004.10.01
 *		
 *	javac -classpath .:log4j-1.2.8.jar com/kaweah/astralcafe/ProjectingAC.java
 */

package com.kaweah.astralcafe;

import com.kaweah.astralcafe.model.*;

/**
 * An {@link AstralCanvas} that generates its own projections.
 *
 * @author Dan Jensen, <a href="http://kaweah.com">Kaweah Concepts</a>
 * @version 1.0
 */

@SuppressWarnings("serial")
abstract public class ProjectingAC extends AstralCanvas
{	
	// static Logger logger = Logger.getLogger(ProjectingAC.class.getName()); 

	public ProjectingAC(ScrollingCanvasPanel scroller)
	{
		super(scroller);
	}
	

	public void
	setViewBreadthAngle(double vba)
	{
		if (theProj != null)
		{
			theProj.width = vba;
		}
	}


	public void
	setRA( double ra )
	{
		if (theProj != null)
		{
			theProj.setHOrigin(ra) ;
		}
	}


	public void
	setDec( double dec )
	{	
		if (theProj != null)
		{
			theProj.setVOrigin(dec) ;
		}
	}


	public void
	setBrightness( int b )
	{
		if (brightness != b)
		{
			brightness = b ;
			refreshNeeded = true;
		}
	}


	/*---------------------------------------------------------------------------
	 *
	 *	calcScreen
	 *
	 *---------------------------------------------------------------------------*/
	 
	public void calcScreen()
	{
		if (theProj != null)
		{
			theProj.calcScreen(getSize());
			refreshNeeded = true;
		}
	}

	@SuppressWarnings("unused")
	private void
	calcScreen(String params)
	{
		if (theProj != null)
		{
			theProj.calcScreen(getSize());
		}
	}


	 protected Point2DDouble findPos(int x, int y)
	 {
	 	Point2DDouble selection = null;
	 	
	 	if (theProj != null)
		{
			selection = theProj.findPos( x, y ) ;
	 	}
	 	
	 	return selection;
	 }

	 protected void setPosition(Point2DDouble selection)
	 {
	 
	 	if (theProj != null)
		{
			theProj.setPosition( selection ) ;
	 	}
	 }

	public double getHOrigin()
	{
		if (theProj != null)
		{
			return theProj.getHOrigin();
		}
		else return -1.0;
	}
	
	public double getVOrigin()
	{
		if (theProj != null)
		{
			return theProj.getVOrigin();
		}
		else return -1.0;
	}
	
	protected int starsOnScreen()
	{
		if (theProj != null)
		{
			return theProj.theScreen.starCount();
		}
		else return -1;
	}

	protected VisibleStar starOnScreenAt(int i)
	{
		if (theProj != null)
		{
			return theProj.theScreen.starAt(i);
		}
		else return null;
	}

	protected int getLinkPointA(int i)
	{
		if (theProj != null)
		{
			return theProj.getBeg(i);
		}
		else return -1;
	}
	
	protected int getLinkPointB(int i)
	{
		if (theProj != null)
		{
			return theProj.getEnd(i);
		}
		else return -1;
	}
	
	protected int getIndexEntryAt(int i)
	{
		if (theProj != null)
		{
			return theProj.getIndexEntryAt(i);
		}
		else return -1;
	}

}

