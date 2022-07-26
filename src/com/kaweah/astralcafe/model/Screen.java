/*
 *  Screen.java
 *  Astral Cafe
 *
 *  Copyright (c) 2004 Kaweah. All rights reserved.
 *
 *  Author: Dan Jensen
 *	Date of most recent change: 2004.09.21
 *		
 *	javac -classpath .:log4j-1.2.8.jar com/kaweah/astralcafe/model/Screen.java
 */

package com.kaweah.astralcafe.model;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.Vector;

/**
 * Represents a 2-D window to the sky, consisting of a {@link Vector} that
 * contains {@link VisibleStar}s, and also a magnitude index of the stars
 * therein.
 *
 * @author Dan Jensen, <a href="http://kaweah.com">Kaweah Concepts</a>
 * @version 1.0
 */

public class Screen implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Vector <VisibleStar> stars;
	private int    indexSize;
	public  int    index[];

	// static Logger logger = Logger.getLogger(Screen.class.getName()); 

	public Screen(int totStars)
	{
		index = new int[indexSize = totStars];
		stars = new Vector <VisibleStar> ();
	}
	
	public void purge()
	{
		// logger.info("Purging screen ...");
		stars.removeAllElements();
	}
	
	public int starCount()
	{
		return stars.size();
	}
	
	public void addStar(VisibleStar star)
	{
		stars.addElement(star);
	}
	
	public VisibleStar starAt(int index)
	{
		return (VisibleStar) ((VisibleStar)(stars.elementAt(index))).clone();
	}

	public String toString()
	{
		String s = "Stars = " + starCount();
		for (int i = 0; i < starCount(); i++)
		{
			s = s + "\n" + starAt(i).toString();
		}
		return s;
	}

	public void print (PrintWriter out)
	{
		for (int i = 0; i < starCount(); i++)
		{
			starAt(i).print(out);
		}
	}
	
	
	public int getIndexEntryAt(int i)
	{
		if (i >= 0 && i < indexSize)
			return index[i] ;
		else return -1;
	}


}
