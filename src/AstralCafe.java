/*
 *  AstralCafe.java
 *  Astral Cafe
 *
 *  Copyright (c) 2002 Kaweah. All rights reserved.
 *
 *  Changes:
 *
 *  Dan Jensen              24.10.02 Initial code release
 *		
 */

import com.kaweah.astralcafe.app.AppObject;

import java.util.Properties;
import java.io.*;

/**
 *  Example application class for demonstrating Astral Cafe package
 *  capabilities.
 */

public class AstralCafe
{
	public static void main(String args[])
	{
		FileInputStream in = null;
		Properties properties = null;
		
		/*
		System.out.println("Arguments: " + args.length);
		
		for (int i = 0; i < args.length; i++)
			System.out.println("\t" + i + ": " + args[i]);
		*/
		
		try
		{
			if (args.length > 0)
				in = new FileInputStream(args[0]);
			else
				in = new FileInputStream("AstralCafe.properties");
			
			properties = new Properties();
			
			properties.load(in);
		}
		catch (IOException ex)
		{
			System.out.println(ex);
			ex.printStackTrace();
			
			// simply returning will not necessarily cause the application to exit.
			
			System.exit(0);
		}
		
		AppObject theApplication = new AppObject(properties);

		try
		{
			theApplication.init();
		}
		catch (IOException ex)
		{
			theApplication.exit();
			
			System.out.println(ex);
			ex.printStackTrace();
			
			// simply returning will not necessarily cause the application to exit.

			System.exit(0);
		}
	}

}
