package main.java.com.alexpacheco.therapynotes.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import main.java.com.alexpacheco.therapynotes.controller.enums.ErrorCode;
import main.java.com.alexpacheco.therapynotes.controller.enums.LogLevel;
import main.java.com.alexpacheco.therapynotes.controller.enums.PreferenceKey;
import main.java.com.alexpacheco.therapynotes.controller.errorhandling.exceptions.TherapyAppException;
import main.java.com.alexpacheco.therapynotes.model.dao.PreferencesDao;
import main.java.com.alexpacheco.therapynotes.model.entities.Preference;
import main.java.com.alexpacheco.therapynotes.util.DbUtil;
import main.java.com.alexpacheco.therapynotes.util.PreferencesUtil;

public class DatabaseInitializer
{
	private static final String SCHEMA_SCRIPT_FILE = "/main/resources/schema-config.sql";
	private static final String TRIGGER_SCRIPT_FILE = "/main/resources/trigger-config.sql";
	private static final String OPTIONS_SCRIPT_FILE = "/main/resources/populate-option-tables.sql";
	
	public static void initDb() throws TherapyAppException
	{
		try( Connection conn = DbUtil.getConnection() )
		{
			if( conn != null )
			{
				DbUtil.executeSqlScript( conn, DatabaseInitializer.class.getResourceAsStream( SCHEMA_SCRIPT_FILE ) );
				AppController.logToDatabase( LogLevel.INFO, "DatabaseInitializer", "Database schema created successfully." );
				System.out.println( "Database schema created successfully." );
				
				DbUtil.executeTriggerScript( conn, DatabaseInitializer.class.getResourceAsStream( TRIGGER_SCRIPT_FILE ) );
				AppController.logToDatabase( LogLevel.INFO, "DatabaseInitializer", "Database triggers created successfully." );
				System.out.println( "Database triggers created successfully." );
				
				if( !isDbPopulated( conn ) )
				{
					DbUtil.executeSqlScript( conn, DatabaseInitializer.class.getResourceAsStream( OPTIONS_SCRIPT_FILE ) );
					AppController.logToDatabase( LogLevel.INFO, "DatabaseInitializer", "Database lookup tables populated successfully." );
					System.out.println( "Database lookup tables populated successfully." );
				}
			}
		}
		catch( SQLException e )
		{
			AppController.logException( "DatabaseInitializer", e );
			throw new TherapyAppException( e.getMessage(), ErrorCode.DB_ERROR );
		}
	}
	
	private static boolean isDbPopulated( Connection conn ) throws TherapyAppException
	{
		try
		{
			String sql = "SELECT COUNT(*) FROM assessment_options";
			
			PreparedStatement pstmt = conn.prepareStatement( sql );
			ResultSet rs = pstmt.executeQuery();
			if( rs.next() )
			{
				return rs.getInt( 1 ) > 0;
			}
		}
		catch( SQLException e )
		{
			AppController.logException( "DatabaseInitializer", e );
			throw new TherapyAppException( e.getMessage(), ErrorCode.DB_ERROR );
		}
		
		return false;
	}
	
	/**
	 * Deletes all log entries older than 90 days from the database. Should be called on application startup to maintain database size.
	 * 
	 * @return the number of log entries deleted
	 */
	public static int cleanupOldLogs( int daysToKeep )
	{
		String sql = "DELETE FROM app_logs WHERE timestamp < datetime('now', '-" + daysToKeep + " days')";
		int rowsDeleted = 0;
		
		try( Connection conn = DbUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement( sql ) )
		{
			rowsDeleted = pstmt.executeUpdate();
			
			if( rowsDeleted > 0 )
			{
				AppController.logToDatabase( LogLevel.INFO, "AppController",
						"Cleaned up " + rowsDeleted + " log entries older than 90 days" );
			}
		}
		catch( SQLException e )
		{
			System.err.println( "Failed to cleanup old logs: " + e.getMessage() );
			e.printStackTrace();
			AppController.logException( "AppController", e );
		}
		
		return rowsDeleted;
	}
	
	public static void initializeDefaultPreferences() throws TherapyAppException
	{
		try
		{
			List<Preference> defaults = new ArrayList<>();
			
			for( PreferenceKey key : PreferenceKey.values() )
			{
				defaults.add( PreferencesUtil.createPreferenceFromKey( key, key.getDefaultValue() ) );
			}
			
			new PreferencesDao().initializeDefaults( defaults );
		}
		catch( SQLException e )
		{
			AppController.logException( "DatabaseInitializer", e );
			throw new TherapyAppException( e.getMessage(), ErrorCode.DB_ERROR );
		}
	}
}