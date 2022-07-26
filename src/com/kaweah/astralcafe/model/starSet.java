/*
 *  starSet.java
 *  Astral Cafe
 *
 *  Copyright (c) 2004 Kaweah. All rights reserved.
 *
 *  Author: Dan Jensen
 *	Date of most recent change: 2004.09.21
 *
 *	javac -classpath .:log4j-1.2.8.jar com/kaweah/astralcafe/model/starSet.java		
 */

package com.kaweah.astralcafe.model;

import java.io.*;
import java.util.Properties;

/**
 * Primary representation of the visible sky, consisting of {@link Star}s and
 * {@link SkyRegions}, but not including any constellation data.
 *
 * @author Dan Jensen, <a href="http://kaweah.com">Kaweah Concepts</a>
 * @version 1.0
 */

public class starSet
{

	private byte fileType ;
	
	public StarGroup stars = null;
	private SkyRegions regions = null; 
	private Properties properties = null;
		
	// static Logger logger = Logger.getLogger(starSet.class.getName()); 

	//ÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑ

	public starSet(StarGroup s)
	{
		// logger.info( "Instantiating star data ..." ) ;
		
		stars = s;	
	}

	public starSet(Properties p)
	{
		// logger.info( "Instantiating star data ..." ) ;
		
		properties = p;
		
		stars = new StarGroup(properties);
		
		regions = new SkyRegions(properties);
	}

	//ÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑ

	public void loadStarFile() throws IOException
	{
		String dataSource = properties.getProperty("dataSource");

		// logger.info("Loading star data from " + dataSource); 

		//	open the data file:

		File starFile = new File(dataSource);
		FileInputStream fileStream = new FileInputStream(starFile);
		
		if ( starFile.exists() && starFile.canRead() && starFile.isFile() )
		{
			// logger.info("Star file exists and is readable. Preparing ..."); 
		
			//	prepare file
			
			try
			{
			
				DataInputStream starStream = new DataInputStream(fileStream) ;

				fileType = starStream.readByte() ;
				
				// logger.debug("File Type: " + fileType) ;
				
				stars.load(starStream);
                // stars.serializeToFile("stars3k.ser");
				regions.load(starStream);
                // regions.exportToFile("constels.txt");

	/***
				//----------------------------------------------------------------------------

				//	raw = (Star*) t_raw ;

				index.initialize( TotStars ) ;

				//----------------------------------------------------------------------------
				//
				//	load constellations
				//
				//	Data Block #3
				//
				//----------------------------------------------------------------------------

				TotRegions = MAX_CONSTEL ;

				count = sizeof(WORD) ;

				for( i = 0 ; i < TotRegions ; i++ )
				{
					rollTheWheel() ;

					constellation[i].records = *( (WORD*) &fbuffer[bufferIndex] ) ;
					bufferIndex += count ;

					constellation[i].list = new WORD[constellation[i].records] ;
					if( !constellation[i].list )
					{
						starAlert( "\pMemory allocation error!" ) ;
				         cleanExit( -1 ) ;
					}

					for( WORD j = 0 ; j < constellation[i].records ; j++ )
					{
						constellation[i].list[j] = *( (WORD*) &fbuffer[bufferIndex] ) ;
						bufferIndex += count ;

						WORD index = constellation[i].list[j] ;

						oGroupData tg ;
						tg.group = i ;
						tg.rank = j ;

						raw[index]->g.push( &tg ) ;
					}
				}

				//----------------------------------------------------------------------------
				//
				// load constellation names (abbreviated)
				//
				//----------------------------------------------------------------------------

				rollTheWheel() ;

				for( i = 0 ; i < TotRegions ; i++ )
				{
				 count = 4 ;

				 cname[i] = new char[4] ;
				 if( !cname[i] )
				 {
				   starAlert( "\pMemory allocation error!" ) ;
				   cleanExit( -1 ) ;
				 }

				 strncpy( cname[i], cstring(&fbuffer[bufferIndex]), int(count) ) ;
				 bufferIndex += count ;

				}

				//----------------------------------------------------------------------------
				//
				// load constellation links
				//
				// Data Block #4
				//
				//----------------------------------------------------------------------------

				linkGroup.records = *( (WORD*) &fbuffer[bufferIndex] ) ;
				bufferIndex += sizeof(WORD) ;

				linkGroup.list = new Link[linkGroup.records] ;
				if( !linkGroup.list )
				{
				 starAlert( "\pMemory allocation error!" ) ;
				 cleanExit( -1 ) ;
				}

				rollTheWheel() ;

				for( i = 0 ; i < linkGroup.records ; i++ )
				{
				 WORD beg = *( (WORD*) &fbuffer[bufferIndex] ) ;
				 bufferIndex += sizeof(WORD) ;
				 WORD end = *( (WORD*) &fbuffer[bufferIndex] ) ;
				 bufferIndex += sizeof(WORD) ;

				 linkGroup.list[i].beg = beg ;
				 linkGroup.list[i].end = end ;

				 if( beg > TotStars || end > TotStars )
				 {
				   starAlert( CONT_DIALOG_ID, "\pBad link value!" ) ;
				 }
				 else
				 {
				   groupData tg ;
				   tg.group = i ;

				   raw[beg]->l.push( &tg ) ;
				   raw[end]->l.push( &tg ) ;
				 }
				}

				//----------------------------------------------------------------------------
				//
				// load constellation link groups
				//
				// Data Block #5
				//
				//----------------------------------------------------------------------------

				TotRegions = *( (WORD*) &fbuffer[bufferIndex] ) ;
				bufferIndex += sizeof(WORD) ;

				conGroup = new Constel( TotRegions ) ;
				if( !conGroup )
				{
				 starAlert( "\pMemory allocation error!" ) ;
				 cleanExit( -1 ) ;
				}

				rollTheWheel() ;

				for( i = 0 ; i < TotRegions ; i++ )
				{
				 count = sizeof(BYTE) ;

				 conGroup->table[i].c = *( (BYTE*) &fbuffer[bufferIndex] ) ;
				 bufferIndex += count ;

				 conGroup->table[i].m = *( (BYTE*) &fbuffer[bufferIndex] ) ;
				 bufferIndex += count ;

				 count = sizeof(float) ;

				 conGroup->table[i].ra = *( (float*) &fbuffer[bufferIndex] ) ;
				 bufferIndex += count ;

				 conGroup->table[i].dec = *( (float*) &fbuffer[bufferIndex] ) ;
				 bufferIndex += count ;

				 BYTE nameLen = *( (BYTE*) &fbuffer[bufferIndex] ) ;
				 bufferIndex += sizeof(BYTE) ;

				 conGroup->table[i].link = new WORD[conGroup->table[i].m] ;
				 if( !conGroup->table[i].link )
				 {
				   starAlert( "\pMemory allocation error!" ) ;
				   cleanExit( -1 ) ;
				 }

				 for( j = 0 ; j < conGroup->table[i].m ; j++ )
				 {

				   WORD linkSlot = *( (WORD*) &fbuffer[bufferIndex] ) ;
				   bufferIndex += sizeof(WORD) ;

				   conGroup->table[i].link[j] = linkSlot ; 

				   // 1. Each link group keeps track of its constituent links.
				   // 2. In turn, each link records the group of which it is a member,
				   //    and also records where it is ranked in that group.

				   linkGroup.list[linkSlot].c = i ;
				   linkGroup.list[linkSlot].m = j ;
				 }

				 conGroup->table[i].name = new char[nameLen+1] ;
				 if( !conGroup->table[i].name )
				 {
				   starAlert( "\pMemory allocation error!" ) ;
				   cleanExit( -1 ) ;
				 }

				 strncpy( conGroup->table[i].name, cstring(&fbuffer[bufferIndex]),
				   int(nameLen) ) ;
				 bufferIndex += nameLen ;
				}
				
		***/
		
				starStream.close() ;
		
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
		else
		{
			// logger.fatal("Problem opening star file for reading."); 
			throw new FileNotFoundException("Could not open data source.");
		}

	}

	//ÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑ

	public void saveStarFile() throws IOException
	{
		String dataTarget = properties.getProperty("dataTarget");

		// logger.info("Storing star data to " + dataTarget); 

		//	open the data file:

		File starFile = new File(dataTarget);
		FileOutputStream fileStream = new FileOutputStream(starFile);
		
		// logger.info("Star file open ..."); 
		
		//	prepare file
		
		try
		{
		
			DataOutputStream stream = new DataOutputStream(fileStream);

			stream.writeByte(fileType) ;
			
			// logger.debug("File Type: " + fileType) ;
			
			//ÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑ

			if (stars != null)
			{
				stars.write(stream);
				regions.write(stream);			
			}
			else
			{
				// logger.fatal("Failed to allocate star object array!") ;
			}
	
			stream.close() ;
	
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
		

	} // end saveStarFile()

	public Star getStar(int i)
	{
		if (stars != null)
			return stars.getStar(i);
		else return null;
	}

	public int getSize()
	{
		if (stars != null)
			return stars.getSize();
		else return 0;
	}
}


