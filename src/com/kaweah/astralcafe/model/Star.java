/*
 *  Star.java
 *  Astral Cafe
 *
 *  Copyright (c) 2004 Kaweah. All rights reserved.
 *
 *  Author: Dan Jensen
 *	Date of most recent change: 2004.09.21
 *		
 *	javac -classpath .:log4j-1.2.8.jar com/kaweah/astralcafe/model/Star.java
 */

package com.kaweah.astralcafe.model;

import java.io.*;
import java.lang.Math;

/**
 * Primary representation of a star. See {@link VisibleStar} for a higher-level
 * representation.
 *
 * @author Dan Jensen, <a href="http://kaweah.com">Kaweah Concepts</a>
 * @version 1.0
 */

public class Star implements Cloneable, Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected static final double RAD_TO_DEG	= 180.0 / Math.PI;
	protected static final double DEG_TO_RAD	= Math.PI / 180.0;
	protected static final double RA_TO_RAD		= 0.26179939;
	protected static final double RAD_TO_RA		= 1.0 / RA_TO_RAD;

	protected double rA;  // was RA
	protected double dec; // was DEC
	protected double magnitude;

	protected double rAToObserver;        // defaults to ra.
	protected double decToObserver;       // defaults to dec.
	protected double magnitudeToObserver; // defaults to magnitude.

	protected double colorIndex; // was bv
	protected double absMag;     // was mv

	protected String name ;
	protected String greek ;
	protected String notes ; // reserved for future use

	
	public double getRA() {return rA;}
	public double getDec() {return dec;}
	public double getMagnitude() {return magnitude;}
	public double getRAToObserver() {return rAToObserver;}
	public double getDecToObserver() {return decToObserver;}
	public double getMagnitudeToObserver() {return magnitudeToObserver;}
	public double getColorIndex() {return colorIndex;}
	public double getAbsMag() {return absMag;}
	public String getName() {return name;}
	public String getGreek() {return greek;}
	public String getNotes() {return notes;}

	protected void setRA(double rA) {this.rA = rA;}
	protected void setDec(double dec) {this.dec = dec;}
	
	protected void setMagnitude(double magnitude) {this.magnitude = magnitude;}

	protected void setRAToObserver(double rAToObserver) {this.rAToObserver = rAToObserver;}
	protected void setDecToObserver(double decToObserver) {this.decToObserver = decToObserver;}
	
	// static Logger logger = Logger.getLogger(Star.class.getName()); 

	protected void setMagnitudeToObserver(double magnitudeToObserver)
	{
		this.magnitudeToObserver = magnitudeToObserver;
	}


	public boolean load( DataInputStream starStream )
	{
		try
		{
			rA         = (double) starStream.readFloat() ;
			dec        = (double) starStream.readFloat() ;
			magnitude  = (double) starStream.readFloat() ;
			colorIndex = (double) starStream.readFloat() ;
			absMag     = (double) starStream.readFloat() ;
			
			resetTranslation();

			byte nameLen  = starStream.readByte() ;
			byte greekLen = starStream.readByte() ;
			byte notesLen = starStream.readByte() ;
			
			// starStream.skipBytes( 1 ) ;
			
			byte [] nameBytes = new byte [nameLen] ;
			byte [] greekBytes = new byte [greekLen] ;
			byte [] notesBytes = new byte [notesLen] ;
			
			starStream.read( nameBytes ) ;
			starStream.read( greekBytes ) ;
			starStream.read( notesBytes ) ;

			name  = new String(nameBytes);
			greek = new String(greekBytes);
			notes = new String(notesBytes);
		}
		catch ( IOException e )
		{
			// logger.fatal( "Sequential read failed." ) ;
			return false;
		}

		// logger.debug("right ascen: " + getRA()) ;
		// logger.debug("declination: " + getDec()) ;
		// logger.debug("magnitude:   " + getMagnitude()) ;
		// logger.debug("B-V index:   " + getColorIndex()) ;
		// logger.debug("abs mag    : " + getAbsMag()) ;
		
		// logger.debug("name:  " + getName()) ;
		// logger.debug("greek: " + getGreek()) ;
		// logger.debug("notes: " + getNotes() ) ;
		
		return true ;

	}


	public boolean write( DataOutputStream stream )
	{
		try
		{
			stream.writeFloat((float) rA) ;
			stream.writeFloat((float) dec) ;
			stream.writeFloat((float) magnitude) ;
			stream.writeFloat((float) colorIndex) ;
			stream.writeFloat((float) absMag) ;
			
			stream.writeByte(name.length()) ;
			stream.writeByte(greek.length()) ;
			stream.writeByte(notes.length()) ;
			
			// stream.writeByte( 0 ) ;
			
			stream.writeBytes( name ) ;
			stream.writeBytes( greek ) ;
			stream.writeBytes( notes ) ;
		}
		catch ( IOException e )
		{
			// logger.fatal( "Sequential write failed." ) ;
			return false;
		}

		return true ;
	}
	
	/**
	 * Export star record in tab-delimited format.
	 * 
	 * @param stream Writer object to be used to output tab-delimited record.
	 * @return true if successful.
	 */
	
	public boolean export(Writer stream)
	{
		try
		{
			stream.write(""+rA);
			stream.write("\t"+dec);
			stream.write("\t"+magnitude);
			stream.write("\t"+colorIndex);
			stream.write("\t"+absMag);
			
			stream.write("\t" + name);
			stream.write("\t" + greek);
			stream.write("\t" + notes);
		}
		catch ( IOException e )
		{
			// logger.fatal( "Export failed." ) ;
			return false;
		}

		return true ;
	}

	/**
	 * Convert polar values of StarStruc object to cartesian values, and place
	 * the results in “thisStar”.  All values are with respect to current center,
	 * which on the first conversion is the sun.
	 */

	public Vector3D getCartesian()
	{
		// assuming the distance formula (given in StarList 2000) uses a natural
		// logarithm.
		
		double dist = Math.exp((getMagnitudeToObserver() + 5.0 - getAbsMag()) / 5.0 ) ;
		
		// be sure to include declination in horizontal distance calculations.
		//
		// overlooking this will make horizontal distances for polar stars too
		// large, thus producing “holes” at the poles.
		
		double decWidth = dist * Math.cos(getDecToObserver() * DEG_TO_RAD);
		
		// at DEC=0, z should be zero
		// at RA=0, y should be zero, and x should be “decWidth”.
				
		Vector3D point =
			new Vector3D(
				decWidth * Math.cos(getRAToObserver() * RA_TO_RAD),
				decWidth * Math.sin(getRAToObserver() * RA_TO_RAD),
				dist * Math.sin(getDecToObserver() * DEG_TO_RAD) );
	
		return point;
	}

	/**
	 *
	 */

	protected void setAppearance(Vector3D point)
	{
		// Set magnitude of starStruc.
		//
		// assuming the distance formula (given in StarList 2000) uses a natural
		// logarithm (log).
		
		setMagnitudeToObserver(getAbsMag() - 5 + 5 * Math.log(point.getMagnitude()));
		
		// Set DEC & RA of starStruc.
		//
		// atan2( y, x ) where tan = y/x
		// the result lies between -PI & +PI
		
		setDecToObserver(Math.atan2(point.getZ(), point.getXYMagnitude()) * RAD_TO_DEG);
		setRAToObserver(Math.atan2(point.getY(), point.getX()) * RAD_TO_RA);
	}

	/**
	 *
	 *
	 */

	public void translate(Vector3D newCenter)
	{
		Vector3D point = getCartesian();
		point.translate(newCenter);
		setAppearance(point);
	}

	/**
	 * Reset relative position and magnitude to Earth/Solar System observer.
	 */

	public void resetTranslation()
	{
		setRAToObserver(getRA());
		setDecToObserver(getDec());
		setMagnitudeToObserver(getMagnitude());
	}

	public Object clone()
	{
		try
		{
			return super.clone();
		}
		catch (CloneNotSupportedException e)
		{
			// this should not happen, since this class is cloneable.
			throw new InternalError();
		}
	}
	
}

