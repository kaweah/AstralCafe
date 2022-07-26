/*
 *  PopupBar.java
 *  Astral Cafe
 *
 *  Copyright (c) 2004 Kaweah. All rights reserved.
 *
 *  Author: Dan Jensen
 *	Date of most recent change: 2004.09.22
 *
 */

package com.kaweah.astralcafe;

import java.awt.*;
import java.awt.event.ItemListener;
import java.lang.reflect.Array;


/**
 * Upper {@link Panel} of the {@link DashBoard}.
 *
 * @author Dan Jensen, <a href="http://kaweah.com">Kaweah Concepts</a>
 * @version 1.0
 */

@SuppressWarnings("serial")
class PopupBar extends Panel
{
	private ItemListener parent ;

	private Choice brightMode ;
	private Choice displayMode;

	private static final String displayModeString[] = {"Color", "White-on-Black", "Black-on-White"};
	private static final String brightnessLevelString[] = {"1", "2", "3", "4", "5", "6"};
	
	PopupBar(ItemListener p)
	{
		parent = p ;
		int displayModes = Array.getLength(displayModeString);
		int brightnessLevels = Array.getLength(brightnessLevelString);
		
		setLayout( new FlowLayout() ) ;

		displayMode = new Choice() ;
		
		for (int i = 0; i < displayModes; i++)
			displayMode.add(displayModeString[i]);
						
		add( displayMode ) ;
			
		// add( new Label( "Motion" ) ) ;

		Choice motionMode = new Choice() ;

		motionMode.add("Click to Move");
		motionMode.add("Identify");
		motionMode.add("Link stars");

		add( motionMode ) ;
		
		add( new Label( "Bright" ) ) ;

		brightMode = new Choice() ;

		for (int i = 0; i < brightnessLevels; i++)
			brightMode.add(brightnessLevelString[i]);

		brightMode.select( "3" ) ;
		
		add( brightMode ) ;
		
		displayMode.addItemListener(parent);
		motionMode.addItemListener(parent);
		brightMode.addItemListener(parent);
		
		setSize( getPreferredSize() ) ;
	}
	
	void setDisplayMode(int m)
	{
		if (m > 0 && m < 4)
			displayMode.select(displayModeString[m-1]);
	}

	void setBrightnessLevel(int b)
	{
		if (b > 0 && b < 7)
			brightMode.select(brightnessLevelString[b-1]);
	}
}
