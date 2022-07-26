/*
 *  AppObject.java
 *  Astral Cafe
 *
 *  Copyright (c) 2004 Kaweah. All rights reserved.
 *	
 *  Author: Dan Jensen
 *	Date of most recent change: 2004.09.21
 *		
 *	javac -classpath .:log4j-1.2.8.jar com/kaweah/astralcafe/AppObject.java
 */

package com.kaweah.astralcafe.app;

import java.util.Properties;
import java.io.IOException;

import org.apache.log4j.Logger; 
import org.apache.log4j.PropertyConfigurator;


/**
 * Convenience class to be used by applications to manage a {@link AstralFrame}.
 *
 * @author Dan Jensen, <a href="http://kaweah.com">Kaweah Concepts</a>
 * @version 1.0
 */

public class AppObject
{
	protected AstralFrame frame;
	protected Properties properties;
	private String configPathName = null;

	static Logger logger = Logger.getLogger(AppObject.class.getName()); 
	
	public
	void initLogger()
	{
		configPathName = properties.getProperty("loggerConfigPathName");
		if (configPathName == null)
			configPathName = "/Volumes/MacDrive/Documents/KaweahPage/WEB-INF/classes/log4j-app.lcf";
		PropertyConfigurator.configure(configPathName);
	}

	public AppObject(Properties p)
	{
		properties = p;
	
		initLogger();
	
		logger.info( "Welcome to Astral Cafe." );
		logger.info( "Would you like some stars with your Java?" );
		logger.info( "(c) 1997-2004, Dan Jensen, Kaweah Concepts." );
		logger.info( "http://kaweah.com/" );
		logger.info( "djensen@kaweah.com" );
		logger.info( "San Jose, California" );
		logger.info( "Release Notes:" );
		logger.info( "Functionality not yet implemented:" );
		logger.info( " - All time-related functionality; automatic scrolling." );
		logger.info( " - Messier objects." );
		logger.info( " - Status Bar." );
		logger.info( "Bugs:" );
		logger.info( " - Double-draw occurs upon zooming from canvas? (fixed?)" );
		
		frame = new AstralFrame(properties);		
	}
	
	public void init() throws IOException
	{
		frame.init();
		frame.setVisible(true);
	}
	
	public void exit()
	{
		frame.dispose();
	}

}
