/*
 *  ConstelRecord.java
 *  Astral Cafe
 *
 *  Copyright (c) 2004 Kaweah. All rights reserved.
 *
 *  Author: Dan Jensen
 *	Date of most recent change: 2004.09.21
 *		
 *	javac -classpath .:log4j-1.2.8.jar com/kaweah/astralcafe/model/ConstelRecord.java
 */

package com.kaweah.astralcafe.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Writer;

/**
 * A list of star indices used to represent a region of the sky dome.
 *
 * @author Dan Jensen, <a href="http://kaweah.com">Kaweah Concepts</a>
 * @version 1.0
 */

class ConstelRecord
{
	short records ;
	short [] list ;
	
	// static Logger logger = Logger.getLogger(ConstelRecord.class.getName()); 
	
	public boolean load( DataInputStream starStream )
	{

		try
		{
			records = starStream.readShort() ;
			
			// logger.debug( "records: " + records ) ;

			list = new short[records] ;
			
			for( short j = 0 ; j < records ; j++ )
			{
				list[j] = starStream.readShort() ;
				
				// logger.debug( "list[" + j + "]: " + list[j] ) ;

				/***
				oGroupData tg ;
				tg.group = i ;
				tg.rank = j ;

				raw[index]->g.push( &tg ) ;
				***/
			}

		}
		catch ( IOException e )
		{
			// logger.fatal( "Sequential read failed." ) ;
			return false;
		}
		
		return true ;

	}


	public boolean write( DataOutputStream stream )
	{

		try
		{
			stream.writeShort(records) ;
			
			list = new short[records] ;
			
			for( short j = 0 ; j < records ; j++ )
			{
				stream.writeShort(list[j]) ;
			}

		}
		catch ( IOException e )
		{
			// logger.fatal( "Sequential write failed." ) ;
			return false;
		}
		
		return true ;

	}

    /**
     * Export constellation records as a tab-delimited array.
     * @param writer
     * @return
     */
    
    public boolean export(Writer writer)
    {

        try
        {
            // writer.writeShort(records) ;

            list = new short[records] ;

            for( short j = 0 ; j < records ; j++ )
            {
                writer.write(Short.valueOf(list[j]).toString() + "\t") ;
            }

        }
        catch ( IOException e )
        {
            // logger.fatal( "Sequential write failed." ) ;
            return false;
        }

        return true ;

    }
}
