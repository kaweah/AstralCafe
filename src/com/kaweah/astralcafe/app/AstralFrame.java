/*
 *  AstralFrame.java
 *  Astral Cafe
 *
 *  Copyright (c) 2004 Kaweah. All rights reserved.
 *	
 *  Author: Dan Jensen
 *	Date of most recent change: 2004.09.21
 *	
 *	javac -classpath .:log4j-1.2.8.jar com/kaweah/astralcafe/AstralFrame.java
 */

package com.kaweah.astralcafe.app;

import java.awt.* ;
import java.awt.event.*;
import java.util.Properties;
import java.io.IOException;

import javax.swing.JFrame;

import org.apache.log4j.Logger; 

import com.kaweah.astralcafe.*;

/**
 * {@link JFrame} to be used by standalone applications as a container for
 * the {@link ScrollingCanvasPanel}.
 *
 * @author Dan Jensen, <a href="http://kaweah.com">Kaweah Concepts</a>
 * @version 1.0
 */
    
@SuppressWarnings("serial")
class AstralFrame extends JFrame implements ActionListener, KeyListener
{
	private DashBoard dash;
	private ScrollingCanvasPanel theScrollPanel ;
	protected Properties properties;
	private boolean dashVisible;
	
	static Logger logger = Logger.getLogger(AstralFrame.class.getName()); 

	//ÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑ

	AstralFrame(Properties p)
	{	
		super("Astral App");
	
		int width = 550, height = 500;
	
		properties = p;
			
		Integer widthObject = Integer.getInteger(properties.getProperty("initialWidth"));
		Integer heightObject = Integer.getInteger(properties.getProperty("initialHeight"));
		
		if (widthObject != null)
			width = widthObject.intValue();
			
		if (heightObject != null)
			height = heightObject.intValue();
		
		logger.info("Initial frame dimensions: " + width + "x" + height);
		
		setSize(width, height);
		
		addWindowListener(new ACWindowAdapter());
				
		theScrollPanel = new ScrollingCanvasPanel(properties);

		dash = new DashBoard(theScrollPanel);
		dashVisible = true;
	}
	

	private void setLayout()
	{
		setLayout( new BorderLayout() ) ;

		add(theScrollPanel, "Center");
		
		if (dashVisible)
			add(dash, "South");
	}

	
	public void init() throws IOException
	{
		String param;
		
		theScrollPanel.init(new AstralAppCanvas(theScrollPanel, properties));

		if ((param = properties.getProperty("brightness")) != null)
		{
			try
			{
				int val = Integer.parseInt(param);

				if (val > 0 && val < 7)
				{
					dash.setBrightnessLevel(val);
				}
			}
			catch (NumberFormatException e)
			{
				logger.warn("Illegal brightness setting. Ignoring.");
				// not fatal, so ignore.
			}
		}
		
		if ((param = properties.getProperty("colorMode")) != null)
		{
			try
			{
				int val = Integer.parseInt(param);
				
				if (val > 0 && val < 4)
				{
					dash.setDisplayMode(val);
				}
			}
			catch (NumberFormatException e)
			{
				logger.warn("Illegal colorMode setting. Ignoring.");
				// not fatal, so ignore.
			}
		}

		if ((param = properties.getProperty("dashBoard")) != null)
		{
			try
			{
				if (param.toLowerCase().equals("false") )
				{
					dashVisible = false;
				}
			}
			catch (NumberFormatException e)
			{
				logger.warn("Illegal dashBoard (visibility) setting. Ignoring.");
				// not fatal, so ignore.
			}
		}

		setLayout();

		setMenuBar(new StarMenu(this));
		
		addKeyListener(this);
	}
	
	
	public Properties getProperties()
	{
		return properties;
	}
	
	
	protected class ACWindowAdapter extends WindowAdapter
	{	
		public void windowClosing(WindowEvent e)
		{
			dispose();
		}

		// do not override windowClosed() if multiple documents may be open.

		public void windowClosed(WindowEvent e)
		{
			System.exit(0);
		}
	}


	public void actionPerformed(ActionEvent e)
	{
		String arg = e.getActionCommand();
	
		logger.info( "Menu: " + arg ) ;

		if ( arg.equals("Quit") )
		{
			dispose();
			// simply returning will not necessarily cause the application to exit.
			System.exit(0);
		}
		if ( arg.equals("ToggleDashBoard") )
		{
			if (dashVisible)
			{
				removeAll();
				add(theScrollPanel, "Center");
			}
			else
			{
				add(dash, "South");
			}

			dashVisible = !dashVisible;
		}
		else if ( arg.equals( "width:7.5" ) )
		{
			theScrollPanel.setViewBreadthAngle( 7.5 ) ;
		}
		else if ( arg.equals( "width:15.0" ) )
		{
			theScrollPanel.setViewBreadthAngle( 15.0 ) ;
		}
		else if ( arg.equals( "width:30.0" ) )
		{
			theScrollPanel.setViewBreadthAngle( 30.0 ) ;
		}
		else if ( arg.equals( "width:60.0" ) )
		{
			theScrollPanel.setViewBreadthAngle( 60.0 ) ;
		}
		else if ( arg.equals( "width:90.0" ) )
		{
			theScrollPanel.setViewBreadthAngle( 90.0 ) ;
		}
		else if ( arg.equals( "width:180.0" ) )
		{
			theScrollPanel.setViewBreadthAngle( 180.0 ) ;
		}
		else if ( arg.equals( "ra:0" ) )
		{
			theScrollPanel.setRA( 0.0 ) ;
		}
		else if ( arg.equals( "ra:3" ) )
		{
			theScrollPanel.setRA( 3.0 ) ;
		}
		else if ( arg.equals( "ra:6" ) )
		{
			theScrollPanel.setRA( 6.0 ) ;
		}
		else if ( arg.equals( "ra:9" ) )
		{
			theScrollPanel.setRA( 9.0 ) ;
		}
		else if ( arg.equals( "ra:12" ) )
		{
			theScrollPanel.setRA( 12.0 ) ;
		}
		else if ( arg.equals( "ra:15" ) )
		{
			theScrollPanel.setRA( 15.0 ) ;
		}
		else if ( arg.equals( "ra:18" ) )
		{
			theScrollPanel.setRA( 18.0 ) ;
		}
		else if ( arg.equals( "ra:21" ) )
		{
			theScrollPanel.setRA( 21.0 ) ;
		}
		else if ( arg.equals( "dec:90 N" ) )
		{
			theScrollPanel.setDec( 90.0 ) ;
		}
		else if ( arg.equals( "dec:60 N" ) )
		{
			theScrollPanel.setDec( 60.0 ) ;
		}
		else if ( arg.equals( "dec:30 N" ) )
		{
			theScrollPanel.setDec( 30.0 ) ;
		}
		else if ( arg.equals( "dec:0" ) )
		{
			theScrollPanel.setDec( 0.0 ) ;
		}
		else if ( arg.equals( "dec:30 S" ) )
		{
			theScrollPanel.setDec( -30.0 ) ;
		}
		else if ( arg.equals( "dec:60 S" ) )
		{
			theScrollPanel.setDec( -60.0 ) ;
		}
		else if ( arg.equals( "dec:90 S" ) )
		{
			theScrollPanel.setDec( -90.0 ) ;
		}
		else if ( arg.equals(StarMenu.MENU_LABEL_NATURAL_PATTERNS) )
		{
			new NaturalPatternConfigDialog(this);
		}
		else if ( arg.equals(StarMenu.MENU_LABEL_USER_PATTERNS) )
		{
			new UserDefinedPatternConfigDialog(this);
		}
	}

	public synchronized void
	dispose()
	{
		super.dispose();
	}

	public void keyPressed(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_D)
		{
			if ((e.getModifiersEx() & KeyEvent.ALT_DOWN_MASK) == KeyEvent.ALT_DOWN_MASK)
			{
				logger.info("Toggling dashboard.");
			
				if (dashVisible)
				{
					removeAll();
					add(theScrollPanel, "Center");
				}
				else
				{
					add(dash, "South");
				}
	
				dashVisible = !dashVisible;
			}
		}
	}
	
	public void keyReleased(KeyEvent e)
	{
	}
	
	public void keyTyped(KeyEvent e)
	{
		
	}
	
	public AstralCanvas getCanvas()
	{
		return theScrollPanel.getCanvas();
	}
	
}
