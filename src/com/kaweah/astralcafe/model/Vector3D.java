/*
 *  Vector3D.java
 *  Astral Cafe
 *
 *  Copyright (c) 2004 Kaweah. All rights reserved.
 *
 *  Author: Dan Jensen
 *	Date of most recent change: 2004.09.21
 *		
 */

package com.kaweah.astralcafe.model;

import java.lang.Math;

/**
 * A point in 3-D cartesian space, used for translation of the viewer from
 * Earth's surface to other locations in space.
 *
 * @author Dan Jensen, <a href="http://kaweah.com">Kaweah Concepts</a>
 * @version 1.0
 */

class Vector3D
{
	protected double x;
	protected double y;
	protected double z;
	
	public Vector3D()
	{
		x = y = z = 0.0;
	}

	public Vector3D(double x, double y, double z)
	{
		setLocation(x, y, z);
	}
	
	public void setLocation(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double getX() {return x;}
	public double getY() {return y;}
	public double getZ() {return z;}
	
	// magnitude of this vector

	public double getMagnitude()
	{
		return Math.sqrt(x*x + y*y + z*z);
	}
	
	// horizontal magnitude of this vector
		
	public double getXYMagnitude()
	{
		return Math.sqrt(x*x + y*y);
	}
	
	public void translate(Vector3D offset)
	{
		x	= x - offset.x ;
		y	= y - offset.y ;
		z	= z - offset.z ;
		
	}
		
}
