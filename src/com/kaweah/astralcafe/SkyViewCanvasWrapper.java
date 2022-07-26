/*
 *  SkyViewCanvasWrapper.java
 *  Astral Cafe
 *
 *  Copyright (c) 2004 Kaweah. All rights reserved.
 *	
 *  Author: Dan Jensen
 *	Date of most recent change: 2004.09.30
 *	
 */

package com.kaweah.astralcafe;

/**
 * Interface consisting of all public Astral Cafe canvas methods.
 *
 * @author Dan Jensen, <a href="http://kaweah.com">Kaweah Concepts</a>
 * @version 1.0
 */

public interface SkyViewCanvasWrapper
{
	public void setRA(double ra);
	public void setDec(double dec);
	public void setViewBreadthAngle(double vba);
	public void setBrightness(int brightLevel);

    public int getConstelVisibility();
    public void setConstelVisibility(int constelMode);

	public void setColorMode(int mode);
	public void setStarLabelMode(int mode);
	public void setNavMode(int mode);
	
	public double getHOrigin();
	public double getVOrigin();
	public void calcScreen();
	public void storeCustomLinks();
}
