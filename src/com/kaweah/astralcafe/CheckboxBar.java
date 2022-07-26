/*
 *  CheckboxBar.java
 *  Astral Cafe
 *
 *  Copyright (c) 2004 Kaweah. All rights reserved.
 *
 *  Author: Dan Jensen
 *	Date of most recent change: 2004.09.21
 *	
 */

package com.kaweah.astralcafe;

import java.awt.* ;
import java.awt.event.ItemListener;


/**
 * Lower {@link Panel} of the {@link DashBoard}.
 *
 * @author Dan Jensen, <a href="http://kaweah.com">Kaweah Concepts</a>
 * @version 1.0
 */

@SuppressWarnings("serial")
class CheckboxBar extends Panel
{
    public static final String CM_LABEL_NONE = "No Constellations";
    public static final String CM_LABEL_ADJACENT = "Adjacent Stars";
    public static final String CM_LABEL_PATTERNS = "User Patterns";
    public static final String CM_LABEL_STANDARD = "Standard Constellations";

	private Checkbox messierFlag, statusFlag ;
    private Choice displayMode;
    private Choice constelMode;

	public CheckboxBar(ItemListener itemListener)
	{
		setLayout( new FlowLayout() ) ;

		add( messierFlag = new Checkbox( "Messier" ) ) ;

        constelMode = new Choice() ;

        constelMode.add(CM_LABEL_NONE);
        constelMode.add(CM_LABEL_ADJACENT);
        constelMode.add(CM_LABEL_PATTERNS);
        constelMode.add(CM_LABEL_STANDARD);

        add( constelMode ) ;

		messierFlag.setEnabled(false); // Disable until supported.

		displayMode = new Choice() ;
		
		displayMode.add("No Labels");
		displayMode.add("Star Names");
		displayMode.add("Greek Letters");
				
		add( displayMode ) ;
		
		add( statusFlag = new Checkbox( "Status Bar" ) ) ;
				
		statusFlag.setEnabled(false); // Disable until supported.

		messierFlag.addItemListener(itemListener);
		constelMode.addItemListener(itemListener);
		displayMode.addItemListener(itemListener);
		statusFlag.addItemListener(itemListener);
				
		setSize( getPreferredSize() ) ;
	}
}
