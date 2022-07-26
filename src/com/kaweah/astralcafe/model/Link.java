/*
 *  Link.java
 *  Astral Cafe
 *
 *  Copyright (c) 2004 Kaweah. All rights reserved.
 *
 *  Author: Dan Jensen
 *	Date of most recent change: 2004.09.27
 *	
 *	javac com/kaweah/astralcafe/model/Link.java
 */

package com.kaweah.astralcafe.model;

import java.io.Serializable;

/**
 * A link between two stars, representing a segment of a constellation.
 *
 * @author Dan Jensen, <a href="http://kaweah.com">Kaweah Concepts</a>
 * @version 1.0
 */

public class Link implements Serializable, Cloneable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Link(int b, int e)
	{
		beg = b;
		end = e;
	}
	
	public int getBeg() {return beg;}
	public int getEnd() {return end;}

	public int beg ;
	public int end ;
	
	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			// this should not happen, since this class is cloneable.
			throw new InternalError();
		}
	}
	
	public String toString()
	{
		return "" + beg + ":" + end;
	}
}
