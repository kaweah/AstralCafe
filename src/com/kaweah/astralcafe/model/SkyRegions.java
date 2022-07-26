/*
 *  SkyRegions.java
 *  Astral Cafe
 *
 *  Copyright (c) 2004 Kaweah. All rights reserved.
 *
 *  Author: Dan Jensen
 *	Date of most recent change: 2004.09.21
 *
 *	javac -classpath .:log4j-1.2.8.jar com/kaweah/astralcafe/model/SkyRegions.java	
 */

package com.kaweah.astralcafe.model;

import java.io.*;
import java.util.Properties;

/**
 * A set of regions in the sky dome.
 *
 * @author Dan Jensen, <a href="http://kaweah.com">Kaweah Concepts</a>
 * @version 1.0
 */

public class SkyRegions implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final short records = 88 ;	
	private ConstelRecord [] constelTable ;
	private String [] constelNames ;

	protected Properties properties;
	
	// static Logger logger = Logger.getLogger(SkyRegions.class.getName()); 

	//ÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑ

	public SkyRegions(Properties p)
	{
		// logger.info( "Instantiating SkyRegions ..." ) ;
		
		properties = p;

		constelTable = new ConstelRecord [records] ;
		constelNames = new String [records] ;
	}


	public void load( DataInputStream starStream ) throws IOException
	{
		// logger.info( "Loading sky regions ..." ) ;

		try
		{
			for ( short c = 0 ; c < records ; c++ )
			{
				constelTable[c] = new ConstelRecord() ;
	
				constelTable[c].load( starStream ) ;
			}
	
			byte byteArray [] ;
			
			byteArray = new byte [4] ;
	
			for ( short c = 0 ; c < records ; c++ )
			{
				starStream.read( byteArray ) ;
				
				constelNames[c] = new String(byteArray);
	
				// logger.debug(byteArray[0] + ":" + byteArray[1] + ":" + byteArray[2] + ":" + byteArray[3]); 
				// logger.debug("Constellation Name: " + constelNames[c] ); 
			}
		}
		catch ( IOException e )
		{
			// logger.fatal( "Sequential read failed." ) ;
			throw e;
		}
	} // end load()


	public void write( DataOutputStream stream ) throws IOException
	{
		// logger.info( "Writing sky regions ..." ) ;

		try
		{
			for ( short c = 0 ; c < records ; c++ )
			{
				constelTable[c].write(stream);
			}

			for ( short c = 0 ; c < records ; c++ )
			{
				stream.writeBytes(constelNames[c]);
			}
	
		}
		catch ( IOException e )
		{
			// logger.fatal( "Sequential write failed." ) ;
			throw e;
		}

	} // end write()

    public void exportToFile(String filename) throws IOException
    {
        File exportFile = new File(filename);

        try
        {
            Writer writer = new FileWriter(exportFile);

            export(writer) ;

            writer.close() ;
        }
        catch ( FileNotFoundException e )
        {
            // logger.fatal("File disappeared.");
            throw e;
        }
        catch ( IOException e )
        {
            // logger.fatal("Sequential read failed.");
            throw e;
        }
    }

    public void export(Writer writer) throws IOException
    {
        // logger.info( "Exporting sky regions ..." ) ;

        try
        {
            for ( short c = 0 ; c < records ; c++ )
            {
                writer.write(constelNames[c] + "\t");
                constelTable[c].export(writer);
            }
        }
        catch ( IOException e )
        {
            // logger.fatal( "Sequential write failed." ) ;
            throw e;
        }

    } // end export()

	public String toString()
	{
		String s = "Regions = " + records + ": ";
		for (int i = 0; i < records; i++)
		{
			s = s + "\n" + constelNames[i] + ":" + constelTable[i].toString() ;
		}
		return s;
	}

	
	public int getSize()
	{
		return records;
	}


	public String getName(int i)
	{
		return constelNames[i];
	}

}
