/*
 *  AppletCanvas.java
 *  Astral Cafe
 *
 *  Copyright (c) 2004 Kaweah. All rights reserved.
 *	
 *  Author: Dan Jensen
 *	Date of most recent change: 2004.10.01
 *		
 */

package com.kaweah.astralcafe.applet;

import java.applet.Applet ;
import java.io.*;
import java.net.URL;
import java.net.HttpURLConnection;

import com.kaweah.astralcafe.AstralCanvas;
import com.kaweah.astralcafe.ScrollingCanvasPanel;
import com.kaweah.astralcafe.model.*;
import com.kaweah.utilities.ModalMsgDialog;

/**
 * {@link AstralCanvas} to be used by applets that do not generate their own
 * projections.
 *
 * @author Dan Jensen, <a href="http://kaweah.com">Kaweah Concepts</a>
 * @version 1.0
 */

@SuppressWarnings("serial")
class AppletCanvas extends AstralCanvas
{	
	private Screen theScreen = null;	

	Applet applet = null;
	
	private static String serverURLStr = "http://www.kaweah.com";
	private static String servletPathStr = "/servlet/StarServer";
	private static String constelServletPathStr = "/servlet/ConstellationServer";
	
	private double horigin = 12.0;
	private double vorigin = 36.0;
	private double viewBreadthAngle = 90.0;

	//ÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑ

	public AppletCanvas(Applet a, ScrollingCanvasPanel scroller)
	{
		super(scroller);

		applet = a;
	}
	
	
	public void init() throws IOException
	{
		serverURLStr = applet.getParameter("serverURL");
		servletPathStr = applet.getParameter("servletPath");
		constelServletPathStr = applet.getParameter("constelServletPath");

		loadAppletParams();
		
		String constelData = applet.getParameter("constelData");
		if (constelData != null && constelData.length() != 0)
		{
			byte [] buf = null;
		
			try
			{
				buf = constelData.getBytes("ISO-8859-1"); 
				ByteArrayInputStream is = new ByteArrayInputStream(buf);
				ObjectInputStream stream = new ObjectInputStream(is);

				/*
				Vector groupsVector = (Vector) stream.readObject();
				if (groupsVector.size() != 0)
				{
					theLinks = (linkGroup) groupsVector.elementAt(0);
					new ModalMsgDialog("Link deserialization succeeded?");
				}
				*/
				
				theLinks = (linkGroup) stream.readObject();
				if (theLinks.getSize() != 0)
				{
					new ModalMsgDialog("Link deserialization succeeded?");
				}
				else
				{
					new ModalMsgDialog("Failed to deserialize links from param: empty group vector.");
					theLinks = getHTTPLinkData();
				}
			}
			catch (UnsupportedEncodingException e)
			{
				new ModalMsgDialog(e.getMessage());
				theLinks = getHTTPLinkData();
			}
			catch (IOException e)
			{
				new ModalMsgDialog(e.getMessage());
				theLinks = getHTTPLinkData();
			}
			catch (java.lang.ClassNotFoundException e)
			{
				new ModalMsgDialog(e.getMessage());
				theLinks = getHTTPLinkData();
			}
		}
		else
		{
			// new ModalMsgDialog("Failed to read constel data param value.");
			theLinks = getHTTPLinkData();
		}
		
		oldSize = getSize();
		calcScreen();		
	}


	private void loadAppletParams()
	{
		String param;
		
		if ((param = applet.getParameter("ra")) != null)
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
				e.printStackTrace();
				// not fatal, so ignore.
			}
		}

		if ((param = applet.getParameter("dec")) != null)
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
				e.printStackTrace();
				// not fatal, so ignore.
			}
		}

		if ((param = applet.getParameter("viewBreadthAngle")) != null)
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
				e.printStackTrace();
				// not fatal, so ignore.
			}
		}
	}


	public void
	setViewBreadthAngle(double vba)
	{
		viewBreadthAngle = vba;
	}

	//ÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑ

	public void
	setRA( double ra )
	{
		horigin = ra ;
	}

	//ÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑ

	public void
	setDec( double dec )
	{	
		vorigin = dec ;
	}

	//ÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑ

	public void
	setBrightness( int b )
	{
		if (brightness != b)
		{
			brightness = b ;
			refreshNeeded = true;
		}
	}

	/*---------------------------------------------------------------------------
	 *
	 *	calcScreen
	 *
	 *---------------------------------------------------------------------------*/
	 
	public void calcScreen()
	{
		String params = "?mode=getScreen&dimh=" +
			getSize().height + "&dimw=" + getSize().width +
			"&horigin=" + horigin + "&vorigin=" + vorigin + 
			"&width=" + viewBreadthAngle + "&brightness=" + brightness +
			"&timeUpdated=false";
	
		theScreen = getHTTPStarData(params);
		
		refreshNeeded = true;
	}

	@SuppressWarnings("unused")
	private void
	calcScreen(String params)
	{
		// Request screen data from servlet.
		
		String defaultParams = "?dimh=" + getSize().height + "&dimw=" + getSize().width;
		
		if (params != null && params.length() > 0)
			params = defaultParams + "&" + params;
		else
			params = defaultParams;
			
		theScreen = getHTTPStarData(params);
		
		refreshNeeded = true;
	}

	/*---------------------------------------------------------------------------
	 *
	 *	getHTTPStarData
	 *
	 *---------------------------------------------------------------------------*/
	 
	 private Screen
	 getHTTPStarData(String params)
	 {
	 	Screen scr = null;
	 
		try
		{
			URL u = new URL(serverURLStr + servletPathStr + params);
			
			HttpURLConnection huc = (HttpURLConnection) u.openConnection();
			huc.setRequestMethod("GET");
			huc.connect();
			int code = huc.getResponseCode();
			if (code >= 200 && code < 300)
			{ 
				// String message = huc.getResponseMessage();
				// System.out.println(message);
				InputStream is = huc.getInputStream();
				ObjectInputStream stream = new ObjectInputStream(is);
				scr = (Screen) stream.readObject();
			}
			huc.disconnect();
		}
		catch (OptionalDataException e1)
		{
			new ModalMsgDialog(e1.getMessage());
		}
		catch (ClassNotFoundException e2)
		{
			new ModalMsgDialog(e2.getMessage());
		}
		catch (IOException e3)
		{
			new ModalMsgDialog(e3.getMessage());
		}
		
		return scr;	
	 }

	/*---------------------------------------------------------------------------
	 *
	 *	getHTTPPosition
	 *
	 *---------------------------------------------------------------------------*/
	 
	 private Point2DDouble
	 getHTTPPosition(String params)
	 {
	 	Point2DDouble pos = null;
	 
		try
		{
			URL u = new URL(serverURLStr + servletPathStr + params);
			
			HttpURLConnection huc = (HttpURLConnection) u.openConnection();
			huc.setRequestMethod("GET");
			huc.connect();
			int code = huc.getResponseCode();
			if (code >= 200 && code < 300)
			{ 
				InputStream is = huc.getInputStream();
				ObjectInputStream stream = new ObjectInputStream(is);
				pos = (Point2DDouble) stream.readObject();
			}
			huc.disconnect();
		}
		catch (OptionalDataException e1)
		{
			new ModalMsgDialog(e1.getMessage());
		}
		catch (ClassNotFoundException e2)
		{
			new ModalMsgDialog(e2.getMessage());
		}
		catch (IOException e3)
		{
			new ModalMsgDialog(e3.getMessage());
		}
		
		return pos;	
	 }
	 
	/*---------------------------------------------------------------------------
	 *
	 *	getHTTPLinkData
	 *
	 *---------------------------------------------------------------------------*/
	 
	 private linkGroup
	 getHTTPLinkData()
	 {
	 	linkGroup links = null;
	 
		try
		{
			URL u = new URL(serverURLStr + constelServletPathStr);
			
			HttpURLConnection huc = (HttpURLConnection) u.openConnection();
			huc.setRequestMethod("GET");
			huc.connect();
			int code = huc.getResponseCode();
			if (code >= 200 && code < 300)
			{ 
				// String message = huc.getResponseMessage();
				// System.out.println(message);
				InputStream is = huc.getInputStream();
				ObjectInputStream stream = new ObjectInputStream(is);
				links = (linkGroup) stream.readObject();
				stream.close();
			}
			huc.disconnect();
		}
		catch (OptionalDataException e1)
		{
			new ModalMsgDialog(e1.getMessage());
		}
		catch (ClassNotFoundException e2)
		{
			new ModalMsgDialog(e2.getMessage());
		}
		catch (IOException e3)
		{
			new ModalMsgDialog(e3.getMessage());
		}
		
		return links;	
	 }


	 protected Point2DDouble findPos(int x, int y)
	 {
	 	Point2DDouble selection = null;
	 	
		String params = "?mode=findPos&dimh=" +
			getSize().height + "&dimw=" + getSize().width +
			"&newh=" + x + "&newv=" + y;
					
		selection = getHTTPPosition(params);
	 	
	 	return selection;
	 }


	 protected void setPosition(Point2DDouble selection)
	 {	 
		horigin = selection.getX();
		vorigin = selection.getY();
	 }


	public double getHOrigin() {return horigin;}

	
	public double getVOrigin() {return vorigin;}

	
	protected int starsOnScreen()
	{
		return theScreen.starCount();
	}


	protected VisibleStar starOnScreenAt(int i)
	{
		return theScreen.starAt(i);
	}


	protected int getLinkPointA(int i)
	{
		int beg = theLinks.getBeg(i);
		if (beg == -1)
			return -1;
		else
			return theScreen.index[beg] ;
	}

	
	protected int getLinkPointB(int i)
	{
		int end = theLinks.getEnd(i);
		if (end == -1)
			return -1;
		else
			return theScreen.index[end] ;
	}

	protected int getIndexEntryAt(int i)
	{
		if (theScreen != null)
		{
			return theScreen.getIndexEntryAt(i);
		}
		else return -1;
	}

	public void storeCustomLinks()
	{
		try
		{
			String queryStr = "?links=" + customLinks.toString();
			URL u = new URL(serverURLStr + constelServletPathStr + queryStr);
			
			HttpURLConnection huc = (HttpURLConnection) u.openConnection();
			huc.setRequestMethod("GET");
			huc.connect();
			int code = huc.getResponseCode();
			String message = huc.getResponseMessage();
			huc.disconnect();

			if (code >= 200 && code < 300)
			{
				new ModalMsgDialog("Constellation data delivered to server.");
				theLinks.append(customLinks);
				customLinks.removeAllElements();
			}
			else
			{
				new ModalMsgDialog(
					"Could not deliver constellation data to server.\nServer response code = "
					+ code + "\nMessage = " + message);
			}
			
		}
		catch (OptionalDataException e1)
		{
			new ModalMsgDialog(e1.getMessage());
		}
		catch (IOException e3)
		{
			new ModalMsgDialog(e3.getMessage());
		}
	}
}

