package com.alexpacheco.therapynotes.util;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import com.alexpacheco.therapynotes.controller.AppController;

public class DbUtil
{
	private static final String DB_FILENAME = "therapy_notes.db";
	private static String cachedDbUrl;
	
	private static synchronized String getDbUrl()
	{
		if( cachedDbUrl == null )
		{
			String dbPath = AppController.getConfiguredDbPath();
			
			if( JavaUtils.isNullOrEmpty( dbPath ) )
			{
				cachedDbUrl = "jdbc:sqlite:" + DB_FILENAME;
			}
			else
			{
				File dbDir = new File( dbPath );
				if( !dbDir.exists() )
				{
					dbDir.mkdirs();
				}
				cachedDbUrl = "jdbc:sqlite:" + dbPath + File.separator + DB_FILENAME;
			}
		}
		return cachedDbUrl;
	}
	
	/**
	 * Gets a connection to the SQLite database with Foreign Keys enabled.
	 */
	public static Connection getConnection( String dbUrl ) throws SQLException
	{
		Connection conn = DriverManager.getConnection( dbUrl );
		
		// Ensure Foreign Keys are active for this specific connection session
		try( Statement stmt = conn.createStatement() )
		{
			stmt.execute( "PRAGMA foreign_keys = ON;" );
		}
		
		if( conn != null )
		{
			System.out.println( "Connected to SQLite database." );
		}
		
		return conn;
	}
	
	/**
	 * Gets a connection to the SQLite database with Foreign Keys enabled.
	 */
	public static Connection getConnection() throws SQLException
	{
		return getConnection( getDbUrl() );
	}
	
	public static void executeSqlScript( Connection conn, InputStream inputStream )
	{
		// Use a delimiter that handles the semicolon while ignoring them inside triggers
		// Note: For complex scripts with triggers, it's often safer to split by a custom delimiter
		// or ensure the Scanner handles the procedural blocks.
		try( Scanner s = new Scanner( inputStream ).useDelimiter( ";" ) )
		{
			conn.setAutoCommit( false ); // Use a transaction for speed and safety
			
			try( Statement st = conn.createStatement() )
			{
				while( s.hasNext() )
				{
					String line = s.next().trim();
					if( !line.isEmpty() )
					{
						st.execute( line );
					}
				}
				conn.commit();
			}
			catch( SQLException e )
			{
				conn.rollback();
				AppController.logException( "DbUtil", e );
				System.err.println( "Error executing script: " + e.getMessage() );
			}
		}
		catch( SQLException e )
		{
			AppController.logException( "DbUtil", e );
			System.err.println( "Transaction error: " + e.getMessage() );
		}
	}
	
	/**
	 * Reads a specialized trigger file where statements are separated by // instead of ; to avoid conflicts with internal trigger logic.
	 */
	public static void executeTriggerScript( Connection conn, InputStream inputStream )
	{
		// Set the delimiter to //
		try( Scanner s = new Scanner( inputStream ).useDelimiter( "//" ) )
		{
			conn.setAutoCommit( false );
			
			try( Statement st = conn.createStatement() )
			{
				while( s.hasNext() )
				{
					String triggerBlock = s.next().trim();
					
					// Skip empty blocks or comments
					if( !triggerBlock.isEmpty() && !triggerBlock.startsWith( "--" ) )
					{
						st.execute( triggerBlock );
					}
				}
				conn.commit();
			}
			catch( SQLException e )
			{
				conn.rollback();
				AppController.logException( "DbUtil", e );
				System.err.println( "SQL Error while loading triggers: " + e.getMessage() );
			}
		}
		catch( SQLException e )
		{
			AppController.logException( "DbUtil", e );
			System.err.println( "Transaction error: " + e.getMessage() );
		}
	}
	
	/**
	 * Should not return null so that DB queries do not get error.
	 * 
	 * @param searchKey
	 * @return
	 */
	public static String validateSearchKey( String searchKey )
	{
		if( searchKey == null || searchKey.strip().length() < 2 )
		{
			return "";
		}
		else
		{
			return searchKey.strip();
		}
	}
}
