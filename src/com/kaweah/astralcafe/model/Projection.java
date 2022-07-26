/*
 *  Projection.java
 *  Astral Cafe
 *
 *  Copyright (c) 2004 Kaweah. All rights reserved.
 *
 *  Author: Dan Jensen
 *	Date of most recent change: 2004.09.26
 *		
 *	javac -classpath .:log4j-1.2.8.jar com/kaweah/astralcafe/model/Projection.java
 */

package com.kaweah.astralcafe.model;

import java.io.*;
import java.awt.Dimension;

import org.apache.log4j.Logger; 

/**
 * Provides common projection access methods, but must be subclassed to
 * provide actual projection functionality.
 *
 * @author Dan Jensen, <a href="http://kaweah.com">Kaweah Concepts</a>
 * @version 1.0
 */

public abstract class Projection
{
	//	major data structures
	
	protected starSet theStars ;
	protected linkGroup theLinks;
	public Screen theScreen;

	//	these constants are used by most projections, and nothing else.

	protected static final double RAD_TO_DEG	= 57.296 ;		//  ( 180 deg / PI rad )
	protected static final double DEG_TO_RAD	= 0.01745329 ;
	protected static final double RA_TO_RAD		= 0.26179939 ;
	protected static final double RAD_TO_RA		= 3.81971860 ;	//	1/RA_TO_RAD
	protected static final double Pi2           = 1.570796327 ;

	//	A projection has an inherent dimension which may not always be in sync
	//	with the containing canvas.  Especially since a projection contains a
	//	list (Vector) of stars that depends on this dimension, a projection
	//	should keep a record of the port size that the screen is based upon.

	public short portHeight = 300 ;
	public short portWidth = 300 ;
	protected short halfPortHeight = 150 ;
	protected short halfPortWidth = 150 ;
	
	//	nsSign: Why did we need this? Can't it be derived from vorigin?
	
	public double nsSign = 1 ;
	
	public double horigin = 12.0 ;	// Right Ascension, in hours.
	public double vorigin = 90.0 ;	// Declination, in degrees.

	//	public data
	
	public double width = 90.0 ;	// degrees
	
	public boolean timeUpdated = true ;
	
	static Logger logger = Logger.getLogger(Projection.class.getName()); 

	public Projection(starSet st, linkGroup links)
	{
		if( st != null )
		{
			theStars = st ;
		}
		
		theLinks = links;

		theScreen = new Screen(theStars.getSize()) ;
	}

	// accessors
	
	public double getHOrigin()
	{
		return horigin;
	}
	
	public double getVOrigin()
	{
		return vorigin;
	}
	
	// mutators

	public void setHOrigin(double h)
	{
		while (h > 24.0) h -= 24.0;
		while (h < 0.0) h += 24.0;
		
		horigin = h;
	}

	public void setVOrigin(double v)
	{
		if (v > 90.0) v = 90.0;
		if (v < -90.0) v = -90.0;
		
		vorigin = v;
		
		nsSign = ( vorigin > 0.0 ) ? 1.0 : -1.0 ;
	}
	
	public void printSerializedScreen (OutputStream out) throws IOException
	{
		try
		{
			ObjectOutputStream stream = new ObjectOutputStream(out);
			stream.writeObject(theScreen);
			stream.close();
			logger.debug(theScreen.toString());
		}
		catch(IOException e)
		{
			logger.error(e.getMessage());
		}
	}
	
	public void print (PrintWriter out)
	{
		theScreen.print(out);
	}

	public int getBeg(int i)
	{
		int beg = theLinks.getBeg(i);
		if (beg == -1)
			return -1;
		else
			return theScreen.index[beg] ;
	}
	
	public int getEnd(int i)
	{
		int end = theLinks.getEnd(i);
		if (end == -1)
			return -1;
		else
			return theScreen.index[end] ;
	}

	public int getIndexEntryAt(int i)
	{
		return theScreen.getIndexEntryAt(i);
	}
	
	public abstract Point2DDouble findPos( int newh, int newv );
	public abstract void setPosition(Point2DDouble selection);
	public abstract void calcScreen( Dimension size );


}
