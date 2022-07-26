/*
 *  VisibleStar.java
 *  Astral Cafe
 *
 *  Copyright (c) 2004 Kaweah. All rights reserved.
 *
 *  Author: Dan Jensen
 *	Date of most recent change: 2004.09.21
 *		
 *	javac -classpath .:log4j-1.2.8.jar com/kaweah/astralcafe/model/VisibleStar.java
 */

package com.kaweah.astralcafe.model;

import java.awt.Color;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.Cloneable;
import java.lang.CloneNotSupportedException;
import java.lang.InternalError;

/**
 * Visual representation of a star, to be contained by a {@link Screen}.
 *
 * <p>Also consists of a reference to the associated {@link Star} and index
 * value, for access to star details and generating constellation links.</p>
 *
 * @author Dan Jensen, <a href="http://kaweah.com">Kaweah Concepts</a>
 * @version 1.0
 */

public class VisibleStar implements Cloneable, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int brightness ;
	private int x ;
	private int y ;
	private Color theColor ;
	
	private Star star;  /* so that name, greek letter, and other data can be
	                       acquired directly through this screen */
	
	private int index;  /* not necessary for display, but useful for using screen
	                       elements to build custom constellations. */

	// static Logger logger = Logger.getLogger(VisibleStar.class.getName()); 

	public VisibleStar(Star s, int i)
	{
		/*	we would clone() 's' first if we wanted to prevent modification of
			'star' by way of 's', but 'star' is intended to be a mere reference
			to 's', and so modification of 'star' by way of 's' is allowed. */
			
		star = s;
		index = i;
	}
	
	public int
	getX()
	{
		return x;
	}

	public int
	getIndex()
	{
		return index;
	}

	public int
	getY()
	{
		return y;
	}

	public int
	getBrightness()
	{
		return brightness;
	}

	public Color
	getColor()
	{
		return new Color(theColor.getRGB());
	}
	
	public Star
	getStar()
	{
		/*	we could clone() 'star' first if we wanted to prevent modification of
			'star' by the receiving object, but 'star' is intended to be a mere
			reference to a Star object in another object, and so modification of
			'star' by way of the receiving object is allowed. Protection of the
			original Star object is not the responsibility of this class. */
			
		return star;
	}
	
	public void
	setX( int newX )
	{
		x = newX;
	}
	
	public void
	setY( int newY )
	{
		y = newY;
	}

	public void
	setBrightness( int newBrightness )
	{
		brightness = newBrightness;
	}
	
	/**
	 * Determine distance (in pixels) to another visible star.
	 * 
	 * @param other the other visible star
	 * @return the distance in pixels
	 */
	
	public int distanceTo(VisibleStar other)
	{
		int diffx = other.getX() - getX();
		int diffy = other.getY() - getY();
		return (int) Math.sqrt(diffx * diffx + diffy * diffy);		
	}

	public void
	setColor( double bv )
	{
	  double t_bv = 2.0 - bv ;
	
	  if( t_bv > 2.35 )
	  {
	    theColor =	Color.white ;
	    return ;
	  }
	  else if( t_bv > 1.8 )
	  {
	    theColor =	Color.cyan ;
	    return ;
	  }
	  else if( t_bv > 1.2 )
	  {
	    theColor =	Color.blue ;
	    return ;
	  }
	  else if( t_bv > 0.6 )
	  {
	    theColor =	Color.yellow ;
	    return ;
	  }
	  else if( t_bv > 0.0 )
	  {
	    theColor =	Color.orange ;
	    return ;
	  }
	  else
	  {
	    theColor =	Color.red ;
	    return ;
	  }
	
	}

	public String toString()
	{
		String s = "" + x + ":" + y + ":" + brightness + ":" + theColor.getRed() +
			"." + theColor.getGreen() + "." + theColor.getBlue() + " " +
			getStar().toString();
		return s;
	}

	public void print (PrintWriter out)
	{
		out.println(toString());
	}
	
	public Object clone()
	{
		try
		{
			VisibleStar s = (VisibleStar) super.clone();
			s.star = (Star) star.clone();
			return s;
		}
		catch (CloneNotSupportedException e)
		{
			// this should not happen, since this class is cloneable.
			throw new InternalError();
		}
	}

}
