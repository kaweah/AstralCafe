/*
 *  ScrollingCanvasPanel.java
 *  Astral Cafe
 *
 *  Copyright (c) 2004 Kaweah. All rights reserved.
 *
 *  Author: Dan Jensen
 *	Date of most recent change: 2004.10.01
 *	
 *	javac -classpath .:log4j-1.2.8.jar com/kaweah/astralcafe/ScrollingCanvasPanel.java
 */

package com.kaweah.astralcafe;

import java.awt.* ;
import java.applet.Applet;
import java.awt.event.*;
import java.util.Properties;
import java.io.IOException;

//ÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑ

@SuppressWarnings("serial")
public class ScrollingCanvasPanel extends Panel
implements AdjustmentListener, SkyViewCanvasWrapper
{
	private AstralCanvas theCanvas;
	private Scrollbar vs, hs ;
	private Properties properties = null;
	// private Applet applet = null;
	
	// static Logger logger = Logger.getLogger(ScrollingCanvasPanel.class.getName()); 

	//ÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑ

	public ScrollingCanvasPanel(Applet a)
	{	
		super();
	
		// applet = a;
	}
	
	
	public ScrollingCanvasPanel(Properties props)
	{	
		super();
		
		properties=props;
	}
	
	
	public void init(AstralCanvas canvas) throws IOException
	{/*
		if (applet == null)
		{
			theCanvas = canvas;
		}
		else if (applet.getParameter("preloadStars") == null)
		{
			theCanvas = new AppletCanvas(applet, this);
		}
		else
		{
			theCanvas = new PreloadingAppletCanvas(applet, this);
		}*/
		
		theCanvas = canvas;

		if (theCanvas != null)
		{
			theCanvas.init();

			int initial_x = (int) ( 180 - ( theCanvas.getHOrigin() * 7.5 ) ) ;
			int initial_y = (int) ( 90 - theCanvas.getVOrigin() ) ;
		
			vs = new Scrollbar(	Scrollbar.VERTICAL,
								initial_y, 15, 0, 180 ) ;
								
			hs = new Scrollbar(	Scrollbar.HORIZONTAL,
								initial_x, 15, 0, 180 ) ;
		
			vs.addAdjustmentListener(this);
			hs.addAdjustmentListener(this);
	
			GridBagLayout layout = new GridBagLayout();
			setLayout(layout);
			GridBagConstraints constraints = new GridBagConstraints();
	
			constraints.fill = GridBagConstraints.BOTH;
			constraints.gridx = 0;
			constraints.gridy = 0;
			constraints.weightx = 100;
			constraints.weighty = 100;
			
			add(theCanvas, constraints) ;
	
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.gridx = 0;
			constraints.gridy = 1;
			constraints.weightx = 100;
			constraints.weighty = 0; // should not grow or shrink
	
			add(hs, constraints) ;
	
			constraints.fill = GridBagConstraints.VERTICAL;
			constraints.gridx = 1;
			constraints.gridy = 0;
			constraints.weightx = 0; // should not grow or shrink
			constraints.weighty = 100;
	
			add(vs, constraints) ;		
					
			setSize( getPreferredSize() ) ;
		}
		else
		{
			throw new IOException("Canvas not constructed. Cannot initialize scrolling panel.");
		}
	}
	
	public AstralCanvas getCanvas()
	{
		return theCanvas;
	}
	
	public void
	adjustmentValueChanged(AdjustmentEvent e)
	{
		if (e.getAdjustable() == hs)
		{
			setRA(24.0 - hs.getValue() / 7.5);
		}
		else
		{
			setDec(90.0 - vs.getValue());
		}
	}
	

	public void
	updateScrollBars()
	{
		hs.setValue( (int) ( ( 24.0 - theCanvas.getHOrigin() ) * 7.5 ) ) ;
		vs.setValue( 90 - (int) theCanvas.getVOrigin() ) ;
	}


	public void
	calcScreen()
	{
		if (theCanvas != null)
		{
			theCanvas.calcScreen();
			theCanvas.repaint();
		}
	}
		
	public void
	setBrightness( int b )
	{
		if (theCanvas != null)
		{
			theCanvas.setBrightness( b ) ;
			theCanvas.repaint();
		}
	}
	
	
    public int
    getConstelVisibility()
    {
        if (theCanvas != null)
        {
            return theCanvas.getConstelVisibility() ;
        }
        return 0;
    }

	public void
	setConstelVisibility(int cm)
	{
		if (theCanvas != null)
		{
			theCanvas.setConstelVisibility(cm) ;
		}
	}
	
	public void
	setRA( double ra )
	{
		if (theCanvas != null)
		{
			theCanvas.setRA(ra);
			calcScreen();
			updateScrollBars();
		}
	}

	public void
	setDec( double dec )
	{
		if (theCanvas != null)
		{
			theCanvas.setDec(dec);
			calcScreen();
			updateScrollBars();
		}
	}

	public void
	setViewBreadthAngle(double vba)
	{
		if (theCanvas != null)
		{
			theCanvas.setViewBreadthAngle(vba);
			calcScreen();
		}
	}

	public double
	getHOrigin()
	{
		if (theCanvas != null)
		{
			return theCanvas.getHOrigin();
		}
		else
		{
			return -1.0;
		}
	}

	public double
	getVOrigin()
	{
		if (theCanvas != null)
		{
			return theCanvas.getVOrigin();
		}
		else
		{
			return -1.0;
		}
	}
	
	public void setColorMode(int mode)
	{
		if (theCanvas != null)
		{
			theCanvas.setColorMode(mode);
		}
	}

	public void setNavMode(int mode)
	{
		if (theCanvas != null)
		{
			theCanvas.setNavMode(mode);
		}
	}
	
	public void setStarLabelMode(int mode)
	{
		if (theCanvas != null)
		{
			theCanvas.setStarLabelMode(mode);
		}
	}

	public void storeCustomLinks()
	{
		if (theCanvas != null)
		{
			theCanvas.storeCustomLinks();
		}
	}
	
	Properties getProperties()
	{
		return properties;
	}
}
