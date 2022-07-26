/*
 *  linkGroup.java
 *  Astral Cafe
 *
 *  Copyright (c) 2004 Kaweah. All rights reserved.
 *
 *  Author: Dan Jensen
 *	Date of most recent change: 2004.10.01
 *	
 *	javac -classpath .:log4j-1.2.8.jar com/kaweah/astralcafe/model/linkGroup.java	
 */

package com.kaweah.astralcafe.model;

import java.io.*;
import java.util.Properties;
import java.util.Vector;

/**
 * A set of {@link Link}s that may either represent a single constellation
 * or a set of constellations.
 *
 * @author Dan Jensen, <a href="http://kaweah.com">Kaweah Concepts</a>
 * @version 1.0
 */

public class linkGroup implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected Vector <Link> list ;
	protected Properties properties;
	
	// static Logger logger = Logger.getLogger(linkGroup.class.getName()); 

	//ÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑ

	public linkGroup()
	{
		this((Properties)null);
		
		list = new Vector <Link> ();
	}

	public linkGroup(Properties p)
	{
		// logger.info( "Instantiating linkGroup ..." ) ;
		
		properties = p;
	}

	public boolean load( DataInputStream starStream )
	{
		// logger.info( "Loading Constellation Links ..." ) ;

		try
		{
			int records = starStream.readShort() ;
			
			// logger.debug( "records: " + records ) ;

			list = new Vector <Link> (records);
			
			for( short j = 0 ; j < records ; j++ )
			{
				int beg = starStream.readShort() ;
				int end = starStream.readShort() ;
			
				Link link = new Link(beg, end);
				list.addElement(link);
				// list[j] = new Link(beg, end) ;
							
				// logger.debug(	"list[" + j + "]: " +
				//				link.beg + "," + link.end ) ;

				/***
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
		// logger.info( "Writing Constellation Links ..." ) ;

		try
		{
			stream.writeShort(list.size()) ;
			
			for( short j = 0 ; j < list.size() ; j++ )
			{
				Link link = (Link) list.elementAt(j);
				stream.writeShort(link.beg) ;
				stream.writeShort(link.end) ;				
			}

		}
		catch ( IOException e )
		{
			// logger.fatal( "Sequential write failed." ) ;
			return false;
		}
		
		return true ;
	}

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

    public void serializeToFile(String filename) throws IOException
    {
        try
        {
            FileOutputStream fos = new FileOutputStream(filename);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject((linkGroup)this);

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

    public void export(Writer writer) throws IOException
    {
        // logger.info( "Exporting sky regions ..." ) ;

        try
        {
            for( short j = 0 ; j < list.size() ; j++ )
            {
                Link link = (Link) list.elementAt(j);
                writer.write("" + link.beg + "\t" + link.end + "\r\n");
            }
        }
        catch ( IOException e )
        {
            // logger.fatal( "Sequential write failed." ) ;
            throw e;
        }

    } // end export()
    
	//ÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑ

	public void loadFromFile() throws IOException
	{
		String linkSource;

		if (properties != null)
			linkSource = properties.getProperty("linkSource");
		else
		{
			throw new IOException("No properties object to obtain linkSource with.");
		}		

		// logger.info("Storing link data from " + linkSource); 

		//	open the data file:

		File linkFile = new File(linkSource);
		FileInputStream fileStream = new FileInputStream(linkFile);
		
		// logger.info("Links file open ..."); 
		
		//	prepare file
		
		try
		{
			DataInputStream stream = new DataInputStream(fileStream);

			/*-----------------------------------------------------------------
			 *	read constellation links
			 *
			 *	This is a one-dimensional array of links, not sorted by
			 *	constellation.
			 *-----------------------------------------------------------------*/

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
		

	} // end loadLinks()

	//ÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑÑ

	public void saveToFile() throws IOException
	{
		String linkTarget;
		
		if (properties != null)
			linkTarget = properties.getProperty("linkTarget");
		else
		{
			throw new IOException("No properties object to obtain linkTarget with.");
		}

		// logger.info("Storing link data to " + linkTarget); 

		//	open the data file:

		File linkFile = new File(linkTarget);
		FileOutputStream fileStream = new FileOutputStream(linkFile);
		
		// logger.info("Links file open ..."); 
		
		//	prepare file
		
		try
		{
			DataOutputStream stream = new DataOutputStream(fileStream);

			/*-----------------------------------------------------------------
			 *	write constellation links
			 *
			 *	This is a one-dimensional array of links, not sorted by
			 *	constellation.
			 *-----------------------------------------------------------------*/

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
		
	} // end saveLinks()
	
	public int getSize()
	{
		if (list != null)
			return list.size();
		else
			return -1;
	}
	
	public void init(Vector <Link> l)
	{
		list = l;
	}

	public void append(Vector <Link> l)
	{
		if (list != null && l != null)
		{
			for (int i = 0; i < l.size(); i++)
			{
				list.addElement(l.elementAt(i));
			}
		}
	}

	public void append(linkGroup lg)
	{
		append(lg.list);	
	}

	public void removeAllElements()
	{
		if (list != null)
		{
			list.removeAllElements();
		}
	}

	public void addElement(Link l)
	{
		if (list != null)
		{
			list.addElement(l);
		}
	}

	public Link getLink(int i)
	{
		return (Link) ((Link) list.elementAt(i)).clone();
	}
	
	public int getBeg(int i)
	{
		if (i >= 0 && i < list.size())
			return ((Link) list.elementAt(i)).beg;
		else
			return -1;
	}

	public int getEnd(int i)
	{
		if (i >= 0 && i < list.size())
			return ((Link) list.elementAt(i)).end;
		else
			return -1;
	}

	public String toString()
	{
		StringBuffer s = new StringBuffer();
		// s.append("" + getSize() + ":"));
		s.append(((Link)list.elementAt(0)).toString());
		for (int i = 1; i < list.size(); i++)
		{
			s.append("," + ((Link)list.elementAt(i)).toString());
		}
		return s.toString();
	}
}
