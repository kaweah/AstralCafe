/*
 *  DashBoard.java
 *  Astral Cafe
 *
 *  Copyright (c) 2004 Kaweah. All rights reserved.
 *
 *  Author: Dan Jensen
 *	Date of most recent change: 2004.09.22
 *	
 *	javac -classpath .:log4j-1.2.8.jar com/kaweah/astralcafe/DashBoard.java	
 */

package com.kaweah.astralcafe;

import java.awt.* ;
import java.awt.event.*;

/**
 * Control {@link Panel} to be used in managing an implementation of
 * {@link SkyViewCanvasWrapper}
 *
 * @author Dan Jensen, <a href="http://kaweah.com">Kaweah Concepts</a>
 * @version 1.0
 */

@SuppressWarnings("serial")
public class DashBoard extends Panel implements ItemListener
{
	private SkyViewCanvasWrapper canvas;
	private PopupBar pub ;
	
	// static Logger logger = Logger.getLogger(DashBoard.class.getName()); 

	//ÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑ

	public DashBoard( SkyViewCanvasWrapper c )
	{
		canvas = c;
						
		setBackground( Color.lightGray ) ;
		setForeground( Color.black ) ;

		setLayout( new BorderLayout() ) ;
		add( pub = new PopupBar(this), "Center" ) ;
		add(new CheckboxBar(this), "South") ;		
	}

	public void setDisplayMode(int m)
	{
		pub.setDisplayMode(m) ;
		canvas.setColorMode(m) ;
	}

	public void setBrightnessLevel(int b)
	{
		pub.setBrightnessLevel(b) ;
		canvas.setBrightness(b) ;
	}
	
	public void itemStateChanged(ItemEvent e)
	{
		Object item = e.getItem();
	
		// logger.info("Item state changed: " + e.paramString());
		// logger.info("Item: " + item.toString());
		
		if ( item.toString().equals( "1" ) )
		{
			canvas.setBrightness( 1 ) ;
		}
		else if ( item.toString().equals( "2" ) )
		{
			canvas.setBrightness( 2 ) ;
		}
		else if ( item.toString().equals( "3" ) )
		{
			canvas.setBrightness( 3 ) ;
		}
		else if ( item.toString().equals( "4" ) )
		{
			canvas.setBrightness( 4 ) ;
		}
		else if ( item.toString().equals( "5" ) )
		{
			canvas.setBrightness( 5 ) ;
		}
		else if ( item.toString().equals( "6" ) )
		{
			canvas.setBrightness( 6 ) ;
		}
		else if ( item.toString().equals( "Color" ) )
		{
			canvas.setColorMode(SkyViewCanvas.COLOR_STAR_MODE) ;
		}
		else if ( item.toString().equals( "White-on-Black" ) )
		{
			canvas.setColorMode(SkyViewCanvas.WHITE_STAR_MODE) ;
		}
		else if ( item.toString().equals( "Black-on-White" ) )
		{
			canvas.setColorMode(SkyViewCanvas.BLACK_STAR_MODE) ;
		}
		else if ( item.toString().equals( "Click to Move" ) )
		{
			canvas.setNavMode(AstralCanvas.REFRAME_MODE) ;
		}
		else if ( item.toString().equals( "Identify" ) )
		{
			canvas.setNavMode(AstralCanvas.IDENTIFY_MODE) ;
		}
		else if ( item.toString().equals( "Link stars" ) )
		{
			canvas.setNavMode(AstralCanvas.LINK_MODE) ;
		}
		else if ( item.toString().equals( "Messier" ) )
		{
		}
        else if ( item.toString().equals(CheckboxBar.CM_LABEL_NONE) )
        {
            if (canvas.getConstelVisibility() != AstralCanvas.CM_NONE)
            {
                canvas.setConstelVisibility(AstralCanvas.CM_NONE) ;
                // logger.info("Constellations switched off.");
            }
        }
        else if ( item.toString().equals(CheckboxBar.CM_LABEL_ADJACENT) )
        {
            if (canvas.getConstelVisibility() != AstralCanvas.CM_ADJACENT_STARS)
            {
                // logger.info("Constellations set to " + item.toString());
                canvas.setConstelVisibility(AstralCanvas.CM_ADJACENT_STARS) ;
            }
        }
        else if ( item.toString().equals(CheckboxBar.CM_LABEL_PATTERNS) )
        {
            if (canvas.getConstelVisibility() != AstralCanvas.CM_USER_PATTERNS)
            {
                // logger.info("Constellations set to " + item.toString());
                canvas.setConstelVisibility(AstralCanvas.CM_USER_PATTERNS) ;
            }
        }
        else if ( item.toString().equals(CheckboxBar.CM_LABEL_STANDARD) )
        {
            if (canvas.getConstelVisibility() != AstralCanvas.CM_CONSTELLATIONS)
            {
                // logger.info("Constellations set to " + item.toString());
                canvas.setConstelVisibility(AstralCanvas.CM_CONSTELLATIONS) ;
            }
        }
		else if ( item.toString().equals( "No Labels" ) )
		{
			canvas.setStarLabelMode(SkyViewCanvas.NO_LABEL_MODE) ;
		}
		else if ( item.toString().equals( "Star Names" ) )
		{
			canvas.setStarLabelMode(SkyViewCanvas.STAR_NAME_MODE) ;
		}
		else if ( item.toString().equals( "Greek Letters" ) )
		{
			canvas.setStarLabelMode(SkyViewCanvas.GREEK_LETTER_MODE) ;
		}
		else if ( item.toString().equals( "Status Bar" ) )
		{
		}
	}
	
}
