/*
 *  Point2DDouble.java
 *  Kaweah
 *
 *  Copyright (c) 2004 Kaweah. All rights reserved.
 *
 *  Author: Dan Jensen
 *	Date of most recent change: 2004.09.21
 *	
 */

package com.kaweah.astralcafe.model;

/**
 * Simple utility class that represents a double-precision point in 2-D space.
 *
 * @author Dan Jensen, <a href="http://kaweah.com">Kaweah Concepts</a>
 * @version 1.0
 */

public class Point2DDouble implements java.io.Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double x ;
	private double y ;
	
	public Point2DDouble()
	{
		
	}
	
	public Point2DDouble(double xi, double yi)
	{
		x = xi;
		y = yi;
	}

	public double getX() {return x;}
	public double getY() {return y;}
	
	public void setX(double newX) {x = newX;}
	public void setY(double newY) {y = newY;}
}
