/*
 *  StarGroup.java
 *  Astral Cafe
 *
 *  Copyright (c) 2004 Kaweah. All rights reserved.
 *
 *  Author: Dan Jensen
 *	Date of most recent change: 2004.09.21
 *
 *	javac -classpath .:log4j-1.2.8.jar com/kaweah/astralcafe/model/StarGroup.java	
 */

package com.kaweah.astralcafe.model;

import java.io.*;
import java.util.Properties;
// import org.apache.log4j.Logger; 

/**
 * A group of {@link Star}s, intended to represent all visible stars in the sky.
 *
 * @author Dan Jensen, <a href="http://kaweah.com">Kaweah Concepts</a>
 * @version 1.0
 */

public class StarGroup implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected short records ;
	protected Star [] list ;
	protected Properties properties;
	
	// static Logger logger = Logger.getLogger(StarGroup.class.getName()); 

	//ÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑ

	public StarGroup(Properties p)
	{
		// logger.info( "Instantiating StarGroup ..." ) ;
		
		properties = p;
	}

	public void load( DataInputStream starStream ) throws IOException
	{
		// logger.info( "Loading stars ..." ) ;

		try
		{
			records = starStream.readShort() ;
			
			// logger.debug( "records: " + records ) ;

			list = new Star [records] ;
			
			if (list != null)
			{
				int timer = 0 ;

				for( int j = 0 ; j < records ; j++ )
				{
					list[j] = new Star() ;
				
					if (list[j] != null)
					{
						list[j].load( starStream ) ;
						
						// logger.debug( list[j].getGreek() + ": " + list[j].getName() ) ;
						
						if ( ++timer > 100 )
						{
							timer = 1 ;
							// logger.info( j + " of " + records + " stars loaded." ) ;
						}
					}
					else
					{
						throw new IOException("Memory allocation failure.");
					}
				}
			}
			else
			{
				throw new IOException("Memory allocation failure.");
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
		// logger.info( "Writing stars ..." ) ;

		try
		{
			short timer = 0 ;
			stream.writeShort(records) ;
			
			for( int j = 0 ; j < records ; j++ )
			{
				list[j].write( stream ) ;
				
				if ( ++timer > 100 )
				{
					timer = 1 ;
					// logger.info( j + " of " + records + " stars written." ) ;
				}
			}

		}
		catch ( IOException e )
		{
			// logger.fatal( "Sequential write failed." ) ;
			throw e;
		}

	} // end write()

	/**
	 * Export star group to a text file.
	 * 
	 * @param stream Writer object to be used to output tab-delimited record.
	 * throws IOException
	 */
	
	public void export(Writer stream) throws IOException
	{
		// logger.info( "Exporting stars ..." ) ;

		try
		{
			// short timer = 0 ;
			// stream.write(records) ;
			
			for( int j = 0 ; j < records ; j++ )
			{
				list[j].export( stream ) ;
				stream.write("\r\n");
				
				/*
				if ( ++timer > 100 )
				{
					timer = 1 ;
					logger.info( j + " of " + records + " stars exported." ) ;
				}*/
			}
		}
		catch (IOException x)
		{
			// logger.fatal( "Export failed." ) ;
			throw x;
		}
	}


	public void loadFromFile() throws IOException
	{
		String starSource;

		if (properties != null)
			starSource = properties.getProperty("starSource");
		else
		{
			throw new IOException("No properties object to obtain starSource with.");
		}		

		// logger.info("Reading star data from " + starSource); 

		//	open the data file:

		File starFile = new File(starSource);
		FileInputStream fileStream = new FileInputStream(starFile);
		
		// logger.info("Star file open ..."); 
		
		//	prepare file
		
		try
		{
			DataInputStream stream = new DataInputStream(fileStream);

			load( stream ) ;
	
			stream.close() ;
		}
		catch ( FileNotFoundException e )
		{
			// logger.fatal("File disappeared."); 
			throw e;
		}
		catch ( IOException e )
		{
			// logger.fatal("Sequential write failed."); 
			throw e;
		}
		

	} // end loadFromFile()


	public void saveToFile() throws IOException
	{
		String starTarget;
		
		if (properties != null)
			starTarget = properties.getProperty("starTarget");
		else
		{
			throw new IOException("No properties object to obtain starTarget with.");
		}

		// logger.info("Storing star data to " + starTarget); 

		//	open the data file:

		File starFile = new File(starTarget);
		FileOutputStream fileStream = new FileOutputStream(starFile);
		
		// logger.info("Star file open ..."); 
		
		//	prepare file
		
		try
		{
			DataOutputStream stream = new DataOutputStream(fileStream);

			write( stream ) ;
	
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
		
	} // end saveToFile()
	
    public void exportToFile(String filename) throws IOException
    {
        // logger.info("Exporting star data to " + filename);

        //	open the data file:

        File exportFile = new File(filename);
		
        // logger.info("Star file open ...");

        //	prepare file

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

    } // end exportToFile()

    public void serializeToFile(String filename) throws IOException
    {
        try
        {
            FileOutputStream fos = new FileOutputStream(filename);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject((StarGroup)this);

            oos.close();
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

	public String toString()
	{
		String s = "Stars = " + records + ": ";
		for (int i = 0; i < records; i++)
		{
			s = s + "\n" + list[i].toString() ;
		}
		return s;
	}
	
	public int getSize()
	{
		return records;
	}

	public Star getStar(int i)
	{
		return list[i];
	}

}
