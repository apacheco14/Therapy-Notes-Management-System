package com.alexpacheco.therapynotes.install;

import java.io.*;
import java.nio.file.*;
import java.util.Properties;

/**
 * Manages persistence of setup configuration and first-run state. Uses a properties file stored in the application data directory.
 */
public class SetupConfigurationManager
{
	private static final String CONFIG_FILENAME = "therapy_notes.properties";
	private static final String SETUP_COMPLETE_KEY = "setup.complete";
	
	private static Path getConfigFilePath()
	{
		String userHome = System.getProperty( "user.home" );
		String os = System.getProperty( "os.name" ).toLowerCase();
		String appDataDir;
		
		if( os.contains( "win" ) )
		{
			appDataDir = System.getenv( "APPDATA" );
			if( appDataDir == null )
			{
				appDataDir = userHome + File.separator + "AppData" + File.separator + "Roaming";
			}
		}
		else if( os.contains( "mac" ) )
		{
			appDataDir = userHome + File.separator + "Library" + File.separator + "Application Support";
		}
		else
		{
			appDataDir = userHome + File.separator + ".config";
		}
		
		return Paths.get( appDataDir, "TherapyNotes", CONFIG_FILENAME );
	}
	
	/**
	 * Check if first-run setup has been completed.
	 */
	public static boolean isSetupComplete()
	{
		try
		{
			Path configPath = getConfigFilePath();
			if( !Files.exists( configPath ) )
			{
				return false;
			}
			
			Properties props = loadProperties();
			return "true".equalsIgnoreCase( props.getProperty( SETUP_COMPLETE_KEY, "false" ) );
			
		}
		catch( Exception e )
		{
			// If we can't determine, assume not complete
			return false;
		}
	}
	
	/**
	 * Mark first-run setup as complete.
	 */
	public static void markSetupComplete() throws IOException
	{
		Properties props = loadProperties();
		props.setProperty( SETUP_COMPLETE_KEY, "true" );
		saveProperties( props );
	}
	
	/**
	 * Reset setup complete flag (for re-running setup wizard).
	 */
	public static void resetSetupComplete() throws IOException
	{
		Properties props = loadProperties();
		props.setProperty( SETUP_COMPLETE_KEY, "false" );
		saveProperties( props );
	}
	
	/**
	 * Save the complete setup configuration.
	 */
	public static void saveConfiguration( SetupConfiguration config ) throws IOException
	{
		Properties props = loadProperties();
		
		// Practice Information
		setPropertyIfNotNull( props, "practice.name", config.getPracticeName() );
		setPropertyIfNotNull( props, "practice.practitioner", config.getPractitionerName() );
		setPropertyIfNotNull( props, "practice.license", config.getLicenseNumber() );
		setPropertyIfNotNull( props, "practice.phone", config.getPhone() );
		setPropertyIfNotNull( props, "practice.email", config.getEmail() );
		setPropertyIfNotNull( props, "practice.address", config.getAddress() );
		
		// Database
		setPropertyIfNotNull( props, "database.path", config.getDatabasePath() );
		
		saveProperties( props );
	}
	
	/**
	 * Load the saved configuration.
	 */
	public static SetupConfiguration loadConfiguration()
	{
		SetupConfiguration config = new SetupConfiguration();
		
		try
		{
			Properties props = loadProperties();
			
			// Practice Information
			config.setPracticeName( props.getProperty( "practice.name" ) );
			config.setPractitionerName( props.getProperty( "practice.practitioner" ) );
			config.setLicenseNumber( props.getProperty( "practice.license" ) );
			config.setPhone( props.getProperty( "practice.phone" ) );
			config.setEmail( props.getProperty( "practice.email" ) );
			config.setAddress( props.getProperty( "practice.address" ) );
			
			// Database
			config.setDatabasePath( props.getProperty( "database.path" ) );
		}
		catch( Exception e )
		{
			// Return default config on error
		}
		
		return config;
	}
	
	/**
	 * Get a specific configuration value by key.
	 */
	public static String getValue( String key )
	{
		try
		{
			Properties props = loadProperties();
			return props.getProperty( key );
		}
		catch( Exception e )
		{
			return null;
		}
	}
	
	/**
	 * Get a specific configuration value with default.
	 */
	public static String getValue( String key, String defaultValue )
	{
		String value = getValue( key );
		return value != null ? value : defaultValue;
	}
	
	/**
	 * Set a specific configuration value.
	 */
	public static void setValue( String key, String value ) throws IOException
	{
		Properties props = loadProperties();
		props.setProperty( key, value );
		saveProperties( props );
	}
	
	// Private helper methods
	
	private static Properties loadProperties()
	{
		Properties props = new Properties();
		Path configPath = getConfigFilePath();
		
		if( Files.exists( configPath ) )
		{
			try( InputStream is = Files.newInputStream( configPath ) )
			{
				props.load( is );
			}
			catch( IOException e )
			{
				// Return empty properties on error
			}
		}
		
		return props;
	}
	
	private static void saveProperties( Properties props ) throws IOException
	{
		Path configPath = getConfigFilePath();
		
		// Ensure parent directories exist
		Files.createDirectories( configPath.getParent() );
		
		try( OutputStream os = Files.newOutputStream( configPath ) )
		{
			props.store( os, "Therapy Notes Configuration" );
		}
	}
	
	private static void setPropertyIfNotNull( Properties props, String key, String value )
	{
		if( value != null && !value.isEmpty() )
		{
			props.setProperty( key, value );
		}
	}
}