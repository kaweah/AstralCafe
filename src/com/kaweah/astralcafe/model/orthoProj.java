/*
 *  orthoProj.java
 *  Astral Cafe
 *
 *  Copyright (c) 2004 Kaweah. All rights reserved.
 *
 *  Author: Dan Jensen
 *	Date of most recent change: 2004.09.21
 *		
 *	javac -classpath .:log4j-1.2.8.jar com/kaweah/astralcafe/model/orthoProj.java
 */

package com.kaweah.astralcafe.model;

import java.awt.Dimension;

/**
 * Calculates an orthographic projection, to represent a 3-D sky dome on a 2-D
 * surface.
 *
 * @author Dan Jensen, <a href="http://kaweah.com">Kaweah Concepts</a>
 * @version 1.0
 */

public class orthoProj extends Projection
{

	// static Logger logger = Logger.getLogger(orthoProj.class.getName()); 
	
	//ÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑ

	public
	orthoProj(starSet st, linkGroup links)
	{
		super(st, links);	
	}

	//ÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑ

	public void
	calcScreen( Dimension size )
	{
		// WORD rcd ;
		// int h, v ;
		double scale, sra, cra ;
		double z, y, iy, ix, viewz, viewy, sho, cho ;
	
		logger.info("Calculating projection ...");
		logger.debug("horigin=" + horigin + "; vorigin=" + vorigin) ;

		theScreen.purge() ;
		
		//	set the projections' dimension, according to the current port size.
		
		portHeight		= (short) size.height ;
		halfPortHeight	= (short) ( size.height / 2 ) ;
		portWidth		= (short) size.width ;
		halfPortWidth	= (short) ( size.width / 2 ) ;
		
		//	scale = pixels required to represent a 180-degree view.
		//	scale calculation dictates that the maximum width is 180 degrees.

		scale = halfPortHeight / Math.sin( width / ( 2 * RAD_TO_DEG ) ) ;
		
		//	0 <= viewz <= 1 is the fractional declination (latitude) of the viewpoint.
		//	0 <= viewy <= 1 is the fractional distance (from the celestial axis) of the viewpoint.
		
		viewz = Math.sin( vorigin * DEG_TO_RAD ) ;
		viewy = Math.cos( vorigin * DEG_TO_RAD ) ;
		
		
		//	0 <= sho <= 1 is the "vertical" right ascention (longitude) of the viewpoint.
		//	0 <= cho <= 1 is the "horizontal" right ascention (longitude) of the viewpoint.
		
		sho = Math.sin( horigin * RA_TO_RAD ) ;
		cho = Math.cos( horigin * RA_TO_RAD ) ;
	
	  //----------------------------------------------------------------------------
	  //
	  // main loop - calculate new screen
	  //
	  // Search down the magnitude index to the current brightness point, stored in
	  // the index.marker array.  If a star is within the new screen limits, add it
	  // to the screen array.
	  //
	  //----------------------------------------------------------------------------
	
		// disable until indexing is installed
		// rcd = sky->theScreen.index.marker[Brightness+1] ;
			  	
	  	short count = 0 ;
	
		for ( short i = 0 ; i < theStars.getSize() ; i++ )
		{	
			//	instantiate a new point for each star record.
			//	points from previous passes will not be lost with successive instantiations!
		
			VisibleStar star = new VisibleStar(theStars.getStar(i), i) ;

			// rollTheWheel() ;
	
			// sra = sky->indexStar(i)->sra * cho - sky->indexStar(i)->cra * sho ;
			// cra = sky->indexStar(i)->cra * cho + sky->indexStar(i)->sra * sho ;
	
			double ra	= theStars.getStar(i).getRA() * RA_TO_RAD ;
			double dec	= theStars.getStar(i).getDec() * DEG_TO_RAD ;
	
			//	calculate the current star's RA components, based upon RA components of the view.
	
			sra = Math.sin( ra ) * cho - Math.cos( ra ) * sho ;
			cra = Math.cos( ra ) * cho + Math.sin( ra ) * sho ;
	
			// iy = cra * sky->indexStar(i)->cdec ;
			// ix = sra * sky->indexStar(i)->cdec ;
	
			iy = cra * Math.cos( dec ) ;
			ix = sra * Math.cos( dec ) ;
			
			// y = iy * viewy + sky->indexStar(i)->sdec * viewz ;
			
			y = iy * viewy + Math.sin( dec ) * viewz ;

			if ( i < 5 )
			{
				logger.debug(	"ra=" + theStars.getStar(i).getRA() +
									"; dec=" + theStars.getStar(i).getDec() +
									"; name=" + theStars.getStar(i).name +
									"; mag=" + theStars.getStar(i).magnitude ) ;
			}

			if( y < 0 )
			{
				star = null ;
				theScreen.index[i] = -1 ;
				continue ;
			}
	    
			star.setX( halfPortWidth - (short)( ix * scale ) ) ;
			
			if( ( star.getX() < 0 ) || ( star.getX() > portWidth ) )
			{
				star = null ;
				theScreen.index[i] = -1 ;
				continue ;
			}
			
			z = Math.sin( dec ) * viewy - iy * viewz ;
			star.setY( halfPortHeight - (short)( z * scale ) ) ;

			// why were we filtering out p.x > halfPortHeight?

			if( ( star.getY() < 0 ) || ( star.getY() > portHeight ) )
			{
				star = null ;
				theScreen.index[i] = -1 ;
				continue ;
			}
			

			star.setBrightness((int) theStars.getStar(i).magnitude);
			theScreen.index[i] = count++ ;
	
			star.setColor(theStars.getStar(i).getColorIndex());
	
			logger.debug(	"x=" + star.getX() +
								"; y=" + star.getY() +
								"; bright=" + star.getBrightness() ) ;

			theScreen.addStar( star ) ;

	    	// 'st' will be set to the WORD-value rank of the current star.
	
	    	// screen[sc].st = sky->theScreen.index.list[i] ;
	
		}
	
	  //----------------------------------------------------------------------------
	  //
	  // screen background features.
	  //
	  //----------------------------------------------------------------------------
	/***
	  #ifdef TWILIGHT_OPTION
	  if( twilight )
	  {
	    clrTwilight(3) ;
	    for( WORD i = 0 ; i < sc ; i++ )
	      screen[i].viewStar( this, true ) ;
	  }
	  else
	  #endif // TWILIGHT_OPTION
	  if( mDensity )
	  {
	    magDensity(2) ;
	    ViewBuffer(2) ;  
	  }
	  else
	  {
	    clrScr() ;
	
	    //--------------------------------------------------------------------------
	    //
	    // Draw the sky, in order of brightness.
	    //
	    // I would prefer this to be put in the main loop, so that the display can
	    // be updated while calculations are made, but magDensity feature requires
	    // that the screen be calculated before it is executed.
	    //
	    //--------------------------------------------------------------------------
	
	    for( WORD i = 0 ; i < sc ; i++ )
	    {
	      screen[i].viewStar( this, true ) ;
	    }
	  }
	
	  // Since Cidentify() needs to examine the new view, call it AFTER the new
	  // view is drawn.
	
	  if( status ) drawFooter() ;
	  else Cidentify() ;
	***/
	}
	
	//ÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑ
	//
	//  findPos
	//
	//  library functions: sqrt, tan, atan, atan2, cos, acos, sin, asin
	//
	//  calls: none
	//
	//  global inputs: scrType, vorigin, horigin, nsSign, width
	//
	//ÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑ

	public Point2DDouble
	findPos( int newh, int newv )
	{
		double deltah, deltav;
		double scale, A, a, cosa, cosB, b, c ;        // for orthographic
		Point2DDouble selection = new Point2DDouble() ;
		
		// distance from center of graph port in pixels:

		deltah = newh - halfPortWidth ;
		deltav = newv - halfPortHeight ;

		A = Math.atan( deltav / deltah ) ;
		if( deltah > 0 ) A = Pi2 + A ;
		else A = Pi2 - A ;
		// north pole at -90 degrees (south pole at +90 degrees?)
		b = Math.sqrt( deltav * deltav + deltah * deltah ) ;
		// distance formula - distance in pixels
		scale = halfPortHeight / Math.sin( width * DEG_TO_RAD / 2 ) ;
		// scale in pixels per unit radius ( sin(0-¹/4) )
		b /= scale ;
		// convert pixels to radians (unit radii)
		b = Math.asin(b) ;
		c = ( 90.0 - vorigin ) * DEG_TO_RAD ;
		// distance to north pole
	      
		// now we have a trig SAS solution ...
		cosa = Math.cos(b) * Math.cos(c) + Math.sin(b) * Math.sin(c) * Math.cos(A) ;
		a = Math.acos(cosa) ;
		
		// spherical 'law of cosines'
		selection.setY( 90.0 - a * RAD_TO_DEG );
		
		if ( a == 0 || c == 0 )
		{
			cosB = ( Math.cos(b) - Math.cos(c) * cosa ) / 0.0000001 ;
		}
		else
		{
			cosB = ( Math.cos(b) - Math.cos(c) * cosa ) / ( Math.sin(c) * Math.sin(a) ) ;
		}
		// 'law of cosines' again (reordered) ;
		if ( deltah > 0 )
			selection.setX(horigin - Math.acos(cosB) * RAD_TO_RA );
		else
			selection.setX(horigin + Math.acos(cosB) * RAD_TO_RA );

		logger.info("Mouse Selection:");
		logger.info("a    = " + a);
		logger.info("b    = " + b);
		logger.info("c    = " + c);
		logger.info("cosB = " + cosB);
		logger.info(selection.getX() + " hours;");
		logger.info(selection.getY() + " degrees.");

		return selection ;

	}
	
	//ÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑ
	//
	//  setPosition
	//
	//ÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑ

	public void
	setPosition(Point2DDouble selection)
	{
	    horigin = selection.getX() ;
	    vorigin = selection.getY() ;

		if ( vorigin > 90.0 ) vorigin = 90.0 ;
		if ( vorigin < -90.0 ) vorigin = -90.0 ;
	
	    while ( horigin > 24.0 ) horigin -= 24.0 ;
	    while ( horigin < 0.0 ) horigin += 24.0 ;

	    nsSign = ( vorigin > 0.0 ) ? 1.0 : -1.0 ;

		logger.info( "New Position:" ) ;
		logger.info( horigin + " hours;" ) ;
		logger.info( vorigin + " degrees." ) ;

	    // ex.decDeg = (long) vorigin ;
	    // ex.decMin = (long) ( ( vorigin - ex.decDeg ) * 60.0 * nsSign ) ;
	    // ex.raHour = (long) horigin ;
	    // ex.raMin  = (long) ( ( horigin - ex.raHour ) * 60.0 ) ;
	    
	    // width = width * disp * 2.0 / portHeight ;
	    // if( width < 0.1 ) width = 0.1 ;
	    // else if( width > 90.0 ) width = 90.0 ;
	    	    
	    // any zoom action that moves the sceen center changes the zenith so
	    // that it isn't current:

	    timeUpdated = false ;

	}

}
