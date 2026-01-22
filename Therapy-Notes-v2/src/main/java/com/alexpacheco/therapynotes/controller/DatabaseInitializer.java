package com.alexpacheco.therapynotes.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.alexpacheco.therapynotes.controller.enums.ErrorCode;
import com.alexpacheco.therapynotes.controller.enums.PreferenceKey;
import com.alexpacheco.therapynotes.controller.errorhandling.exceptions.TherapyAppException;
import com.alexpacheco.therapynotes.model.dao.PreferencesDao;
import com.alexpacheco.therapynotes.model.entities.Preference;
import com.alexpacheco.therapynotes.util.AppLogger;
import com.alexpacheco.therapynotes.util.DbUtil;
import com.alexpacheco.therapynotes.util.PreferencesUtil;

public class DatabaseInitializer
{
	private static final String SCHEMA_SCRIPT_FILE = "/schema-config.sql";
	private static final String TRIGGER_SCRIPT_FILE = "/trigger-config.sql";
	private static final String OPTIONS_SCRIPT_FILE = "/populate-option-tables.sql";
	
	public static void initDb() throws TherapyAppException
	{
		try( Connection conn = DbUtil.getConnection() )
		{
			if( conn != null )
			{
				DbUtil.executeSqlScript( conn, DatabaseInitializer.class.getResourceAsStream( SCHEMA_SCRIPT_FILE ) );
				AppLogger.info( "Database schema created successfully." );
				
				DbUtil.executeTriggerScript( conn, DatabaseInitializer.class.getResourceAsStream( TRIGGER_SCRIPT_FILE ) );
				AppLogger.info( "Database triggers created successfully." );
				
				if( !isDbPopulated( conn ) )
				{
					DbUtil.executeSqlScript( conn, DatabaseInitializer.class.getResourceAsStream( OPTIONS_SCRIPT_FILE ) );
					AppLogger.info( "Database lookup tables populated successfully." );
				}
			}
		}
		catch( SQLException e )
		{
			AppLogger.error( "Error initializing database: " + e.getMessage(), e );
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
			AppLogger.logDatabaseError( "SELECT COUNT(*)", "assessment_options", e );
			throw new TherapyAppException( e.getMessage(), ErrorCode.DB_ERROR );
		}
		
		return false;
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
			AppLogger.error( "Failed to initialize default preferences: " + e.getMessage(), e );
			throw new TherapyAppException( e.getMessage(), ErrorCode.DB_ERROR );
		}
	}
}