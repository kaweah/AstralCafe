/*
 *  PreloadingAppletCanvas.java
 *  Astral Cafe
 *
 *  Copyright (c) 2004 Kaweah. All rights reserved.
 *	
 *  Author: Dan Jensen
 *	Date of most recent change: 2004.09.30
 *	
 *	javac com/kaweah/astralcafe/PreloadingAppletCanvas.java
 */

package com.kaweah.astralcafe.applet;

import java.applet.Applet ;
import java.io.*;
import java.net.URL;
import java.net.HttpURLConnection;

import com.kaweah.astralcafe.*;
import com.kaweah.astralcafe.model.*;
import com.kaweah.utilities.ModalMsgDialog;

/**
 * {@link ProjectingAC} (an {@link AstralCanvas} that generates its own
 * projections) to be used by applets.
 *
 * @author Dan Jensen, <a href="http://kaweah.com">Kaweah Concepts</a>
 * @version 1.0
 */

@SuppressWarnings("serial")
class PreloadingAppletCanvas extends ProjectingAC
{	
	Applet applet = null;
	
	private static String serverURLStr = "http://www.kaweah.com";
	private static String servletPathStr = "/software/astralcafe/getstars.php";
	private static String constelServletPathStr = "/software/astralcafe/getconstels.php";
	
	public PreloadingAppletCanvas(Applet a, ScrollingCanvasPanel scroller)
	{
		super(scroller);

		applet = a;
	}
	
	
	public void init() throws IOException
	{
		serverURLStr = applet.getParameter("serverURL");
		servletPathStr = applet.getParameter("servletPath");
		constelServletPathStr = applet.getParameter("constelServletPath");
		
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
		
		
		StarGroup stars = preloadStarData();
		theStars = new starSet(stars);
		theProj = new orthoProj(theStars, theLinks);

		loadAppletParams();

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


	/*---------------------------------------------------------------------------
	 *
	 *	getHTTPPosition
	 *
	 *---------------------------------------------------------------------------*/
	 
	 @SuppressWarnings("unused")
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


	/*---------------------------------------------------------------------------
	 *
	 *	preloadStarData
	 *
	 *---------------------------------------------------------------------------*/
	 
	private StarGroup
	preloadStarData()
	{
		StarGroup stars = null;
		
		try
		{
			URL u = new URL(serverURLStr + servletPathStr);
			
			HttpURLConnection huc = (HttpURLConnection) u.openConnection();
			huc.setRequestMethod("GET");
			huc.connect();
			int code = huc.getResponseCode();
			if (code >= 200 && code < 300)
			{ 
				InputStream is = huc.getInputStream();
				ObjectInputStream stream = new ObjectInputStream(is);
				stars = (StarGroup) stream.readObject();
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
		
		return stars;	
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

		/***

		try
		{
			URL u = new URL(serverURLStr + constelServletPathStr);
			
			HttpURLConnection huc = (HttpURLConnection) u.openConnection();
			huc.setRequestMethod("POST");
			huc.setDoOutput(true);
			huc.setDoOutput(true);
			huc.setUseCaches(false);
			// huc.connect();
			OutputStream out = huc.getOutputStream();

			ObjectOutputStream stream = new ObjectOutputStream(out);
			stream.writeObject(customLinks);
			stream.flush();
			stream.close();
			
			InputStream in = huc.getInputStream();
			
			InputStreamReader isr = new InputStreamReader(in); 
			BufferedReader br = new BufferedReader(isr); 
			String line = null; 

			while ( (line = br.readLine()) != null) 
			{ 
				new ModalMsgDialog("line: " + line); 
			} 

			theLinks.append(customLinks);
			customLinks.removeAllElements();
			
		}
		catch (OptionalDataException e1)
		{
			new ModalMsgDialog(e1.getMessage());
		}
		catch (IOException e3)
		{
			new ModalMsgDialog(e3.getMessage());
		}
		
		***/

	}
}

