/*
 *  AstralApplet.java
 *  Astral Cafe
 *
 *  Copyright (c) 2004 Kaweah. All rights reserved.
 *	
 *  Author: Dan Jensen
 *	Date of most recent change: 2004.10.02
 *	
 */

package com.kaweah.astralcafe.applet;

import java.applet.Applet ;
import java.awt.* ;
import java.awt.event.*;
import java.io.IOException;

import com.kaweah.astralcafe.*;

/**
 * Example Astral Cafe applet that consists of a {@link ScrollingCanvasPanel}
 * and a {@link DashBoard}.
 *
 * @author Dan Jensen, <a href="http://kaweah.com">Kaweah Concepts</a>
 * @version 1.0
 */

@SuppressWarnings("serial")
public class AstralApplet extends Applet implements KeyListener
{
	private DashBoard dash;
	private ScrollingCanvasPanel theScrollPanel;
	private boolean dashVisible;
	
	public AstralApplet()
	{	
		super();
	
		theScrollPanel = new ScrollingCanvasPanel(this);

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
	
	
	public void init()
	{
		String param;
		AstralCanvas canvas;
		
		if (getParameter("preloadStars") == null)
		{
			canvas = new AppletCanvas(this, theScrollPanel);
		}
		else
		{
			canvas = new PreloadingAppletCanvas(this, theScrollPanel);
		}

		try
		{
			theScrollPanel.init(canvas);
		}
		catch (IOException e)
		{
			System.out.println("ScrollingCanvasPanel initialization failed!");
			return;
		}
		
		if ((param = getParameter("dashBoard")) != null)
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
				e.printStackTrace();
				// not fatal, so ignore.
			}
		}
		
		if ((param = getParameter("brightness")) != null)
		{
			try
			{
				int val = Integer.parseInt(param);
				 
				if (val > 0 && val < 7)
					dash.setBrightnessLevel(val);
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
				// not fatal, so ignore.
			}
		}
		
		if ((param = getParameter("colorMode")) != null)
		{
			try
			{
				int val = Integer.parseInt(param);
				
				if (val > 0 && val < 4)
					dash.setDisplayMode(val);
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
				// not fatal, so ignore.
			}
		}
		
		setLayout();

		// BUG: Can't get focus on these objects? 
		
		addKeyListener(this);
		theScrollPanel.addKeyListener(this);
		dash.addKeyListener(this);
	}
	
	
	public void start()
	{
		repaint();
	}
	
	public void
	destroy()
	{
		// theScrollPanel.storeCustomLinks();
	}
	
	/**
	 * Toggles visibility of dashboard when key combo "Alt-d" is pressed.
	 * 
	 * <p>Doesn't work yet; perhaps because the necessary objects can't get focus.</p>
	 *
	 */
	
	public void keyPressed(KeyEvent e)
	{
		if (e.getKeyCode() == KeyEvent.VK_D)
		{
			if ((e.getModifiersEx() & KeyEvent.ALT_DOWN_MASK) == KeyEvent.ALT_DOWN_MASK)
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
		}
	}
	
	public void keyReleased(KeyEvent e)
	{
	}
	
	public void keyTyped(KeyEvent e)
	{
		
	}
	
}
