/*
 *  AstralAppCanvas.java
 *  Astral Cafe
 *
 *  Copyright (c) 2004 Kaweah. All rights reserved.
 *	
 *  Author: Dan Jensen
 *	Date of most recent change: 2004.09.30
 *	
 *	javac -classpath .:log4j-1.2.8.jar com/kaweah/astralcafe/AstralAppCanvas.java
 */

package com.kaweah.astralcafe.app;

import java.io.IOException;
import java.util.Properties;

import com.kaweah.astralcafe.AstralCanvas;
import com.kaweah.astralcafe.ProjectingAC;
import com.kaweah.astralcafe.ScrollingCanvasPanel;
import com.kaweah.astralcafe.model.linkGroup;
import com.kaweah.astralcafe.model.orthoProj;
import com.kaweah.astralcafe.model.starSet;

import org.apache.log4j.Logger; 

/**
 * {@link ProjectingAC} (an {@link AstralCanvas} that generates its own
 * projections) to be used by standalone applications.
 *
 * @author Dan Jensen, <a href="http://kaweah.com">Kaweah Concepts</a>
 * @version 1.0
 */

@SuppressWarnings("serial")
class AstralAppCanvas extends ProjectingAC
{	
	private Properties properties;

	static Logger logger = Logger.getLogger(AstralAppCanvas.class.getName()); 

	//ÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑ

	AstralAppCanvas(ScrollingCanvasPanel scroller, Properties p)
	{
		super(scroller);
		
		properties = p;
		
		theStars = new starSet(properties);
		theLinks = new linkGroup(properties) ;
	}
	
	public void init() throws IOException
	{
		try
		{
			theStars.loadStarFile();
		}
		catch (IOException e)
		{
			logger.fatal("Failed to load star data.");
			throw e;
		}
		
		try
		{
			theLinks.loadFromFile();
		}
		catch (IOException e)
		{
			logger.error("Failed to load link data.");
			throw e;
		}

        // theLinks.serializeToFile("links.ser");

		theProj = new orthoProj(theStars, theLinks);
		
		loadProperties();

		oldSize = getSize();
		calcScreen();
	}


	private void loadProperties()
	{
		String param;
		
		if ((param = properties.getProperty("ra")) != null)
		{
			try
			{
				 double val = Double.parseDouble(param);
				 
				 if (val >= 0.0 && val <= 24.0)
				 {
				 	setRA(val);
				 }
			}
			catch (NumberFormatException e)
			{
				logger.warn("Illegal ra (right ascension) setting. Ignoring.");
				// not fatal, so ignore.
			}
		}

		if ((param = properties.getProperty("dec")) != null)
		{
			try
			{
				 double val = Double.parseDouble(param);
				 
				 if (val >= -90.0 && val <= 90.0)
				 {
				 	setDec(val);
				 }
			}
			catch (NumberFormatException e)
			{
				logger.warn("Illegal dec (declination) setting. Ignoring.");
				// not fatal, so ignore.
			}
		}

		if ((param = properties.getProperty("viewBreadthAngle")) != null)
		{
			try
			{
				 double val = Double.parseDouble(param);
				 
				 if (val >= MIN_VIEW_BREADTH_ANGLE && val <= MAX_VIEW_BREADTH_ANGLE)
				 {
				 	setViewBreadthAngle(val);
				 }
			}
			catch (NumberFormatException e)
			{
				logger.warn("Illegal viewBreadthAngle setting. Ignoring.");
				// not fatal, so ignore.
			}
		}
	}

	public void storeCustomLinks()
	{
		// write to local database?
	}
}
