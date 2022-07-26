/*
 *  StarMenu.java
 *  Astral Cafe
 *
 *  Copyright (c) 2004 Kaweah. All rights reserved.
 *
 *  Author: Dan Jensen
 *	Date of most recent change: 2004.09.21
 *		
 */

package com.kaweah.astralcafe.app;

import java.awt.*;
import java.awt.event.ActionListener;


/**
 * {@link MenuBar} to be used in managing an implementation of
 * {@link AstralFrame}.
 *
 * @author Dan Jensen, <a href="http://kaweah.com">Kaweah Concepts</a>
 * @version 1.0
 */

@SuppressWarnings("serial")
class StarMenu extends MenuBar
{
	public static final String MENU_LABEL_NATURAL_PATTERNS = "Natural Patterns ...";
	public static final String MENU_LABEL_USER_PATTERNS = "User-Defined Patterns ...";
	
	StarMenu(ActionListener actionListener)
	{
		initFileMenu(actionListener);
		initViewMenu(actionListener);
		initPatternMenu(actionListener);
	}
	
	private void initViewMenu(ActionListener actionListener)
	{
		Menu viewMenu = new Menu( "View" ) ;
		Menu raMenu = new Menu( "Right Ascension" ) ;
		Menu decMenu = new Menu( "Declination" ) ;
		Menu widthMenu = new Menu( "Width" ) ;
		
		viewMenu.addActionListener(actionListener);
		raMenu.addActionListener(actionListener);
		decMenu.addActionListener(actionListener);
		widthMenu.addActionListener(actionListener);
		
		MenuItem timeMenu = new MenuItem("Time...");
		timeMenu.setEnabled(false);
		
		MenuItem toolsItem = new MenuItem("Toggle DashBoard");
		toolsItem.setActionCommand("ToggleDashBoard");
		
		MenuItem menuItem = null;
		
		menuItem = new MenuItem( " 0" );
		menuItem.setActionCommand("ra:0");
		raMenu.add(menuItem);
		menuItem = new MenuItem( " 3" );
		menuItem.setActionCommand("ra:3");
		raMenu.add(menuItem);
		menuItem = new MenuItem( " 6" );
		menuItem.setActionCommand("ra:6");
		raMenu.add(menuItem);
		menuItem = new MenuItem( " 9" );
		menuItem.setActionCommand("ra:9");
		raMenu.add(menuItem);
		menuItem = new MenuItem( "12" );
		menuItem.setActionCommand("ra:12");
		raMenu.add(menuItem);
		menuItem = new MenuItem( "15" );
		menuItem.setActionCommand("ra:15");
		raMenu.add(menuItem);
		menuItem = new MenuItem( "18" );
		menuItem.setActionCommand("ra:18");
		raMenu.add(menuItem);
		menuItem = new MenuItem( "21" );
		menuItem.setActionCommand("ra:21");
		raMenu.add(menuItem);
		
		menuItem = new MenuItem( "90 N" );
		menuItem.setActionCommand("dec:90 N");
		decMenu.add(menuItem);
		menuItem = new MenuItem( "60 N" );
		menuItem.setActionCommand("dec:60 N");
		decMenu.add(menuItem);
		menuItem = new MenuItem( "30 N" );
		menuItem.setActionCommand("dec:30 N");
		decMenu.add(menuItem);
		menuItem = new MenuItem( " 0  " );
		menuItem.setActionCommand("dec:0");
		decMenu.add(menuItem);
		menuItem = new MenuItem( "30 S" );
		menuItem.setActionCommand("dec:30 S");
		decMenu.add(menuItem);
		menuItem = new MenuItem( "60 S" );
		menuItem.setActionCommand("dec:60 S");
		decMenu.add(menuItem);
		menuItem = new MenuItem( "90 S" );
		menuItem.setActionCommand("dec:90 S");
		decMenu.add(menuItem);

		menuItem = new MenuItem( "  7.5" );
		menuItem.setActionCommand("width:7.5");
		widthMenu.add(menuItem);
		menuItem = new MenuItem( " 15.0" );
		menuItem.setActionCommand("width:15.0");
		widthMenu.add(menuItem);
		menuItem = new MenuItem( " 30.0" );
		menuItem.setActionCommand("width:30.0");
		widthMenu.add(menuItem);
		menuItem = new MenuItem( " 60.0" );
		menuItem.setActionCommand("width:60.0");
		widthMenu.add(menuItem);
		menuItem = new MenuItem( " 90.0" );
		menuItem.setActionCommand("width:90.0");
		widthMenu.add(menuItem);
		menuItem = new MenuItem( "180.0" );
		menuItem.setActionCommand("width:180.0");
		widthMenu.add(menuItem);

		viewMenu.add( raMenu ) ;
		viewMenu.add( decMenu ) ;
		viewMenu.add( widthMenu ) ;
		
		viewMenu.add(timeMenu) ;
		viewMenu.add(toolsItem);
		
		add( viewMenu ) ;
	}
	
	private void initFileMenu(ActionListener actionListener)
	{
		Menu fileMenu = new Menu( "File" );
		fileMenu.setEnabled(false);
		
		Menu subMenu = new Menu( "Save Image" );

		fileMenu.addActionListener(actionListener);
		subMenu.addActionListener(actionListener);
		
		fileMenu.add( new MenuItem( "Open..." ) ) ;
		fileMenu.add( new MenuItem( "Close" ) ) ;
		subMenu.add( new MenuItem( "JPEG/JFIF" ) ) ;
		subMenu.add( new MenuItem( "PNG" ) ) ;
		fileMenu.add( subMenu ) ;
		fileMenu.add( new MenuItem( "-" ) ) ;
		fileMenu.add( new MenuItem( "Print..." ) ) ;
		
		add( fileMenu ) ;
	}

	private void initPatternMenu(ActionListener actionListener)
	{
		Menu menu = new Menu("Patterns");
		
		menu.addActionListener(actionListener);
		
		menu.add( new MenuItem(MENU_LABEL_NATURAL_PATTERNS) ) ;
		menu.add( new MenuItem(MENU_LABEL_USER_PATTERNS) ) ;
		
		add(menu) ;
	}
}
