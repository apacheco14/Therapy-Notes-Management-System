package com.alexpacheco.therapynotes.model.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.alexpacheco.therapynotes.controller.enums.PreferenceType;
import com.alexpacheco.therapynotes.controller.errorhandling.exceptions.TherapyAppException;
import com.alexpacheco.therapynotes.model.entities.Preference;
import com.alexpacheco.therapynotes.util.AppLogger;
import com.alexpacheco.therapynotes.util.DateFormatUtil;
import com.alexpacheco.therapynotes.util.DbUtil;

/**
 * Data Access Object for user preferences. Provides CRUD operations and utility methods for managing application preferences.
 */
public class PreferencesDao
{
	// SQL Statements
	private static final String SQL_SELECT_ALL = "SELECT preference_key, preference_value, preference_type, display_name, "
			+ "description, default_value, category, update_date " + "FROM user_preferences ORDER BY category, display_name";
	
	private static final String SQL_SELECT_BY_KEY = "SELECT preference_key, preference_value, preference_type, display_name, "
			+ "description, default_value, category, update_date " + "FROM user_preferences WHERE preference_key = ?";
	
	private static final String SQL_SELECT_BY_CATEGORY = "SELECT preference_key, preference_value, preference_type, display_name, "
			+ "description, default_value, category, update_date " + "FROM user_preferences WHERE category = ? ORDER BY display_name";
	
	private static final String SQL_SELECT_CATEGORIES = "SELECT DISTINCT category FROM user_preferences WHERE category IS NOT NULL ORDER BY category";
	
	private static final String SQL_INSERT = "INSERT INTO user_preferences (preference_key, preference_value, preference_type, "
			+ "display_name, description, default_value, category) " + "VALUES (?, ?, ?, ?, ?, ?, ?)";
	
	private static final String SQL_UPDATE_VALUE = "UPDATE user_preferences SET preference_value = ? " + "WHERE preference_key = ?";
	
	private static final String SQL_UPDATE_FULL = "UPDATE user_preferences SET preference_value = ?, preference_type = ?, "
			+ "display_name = ?, description = ?, default_value = ?, category = ? WHERE preference_key = ?";
	
	private static final String SQL_DELETE = "DELETE FROM user_preferences WHERE preference_key = ?";
	
	private static final String SQL_RESET_TO_DEFAULT = "UPDATE user_preferences SET preference_value = default_value WHERE preference_key = ?";
	
	private static final String SQL_RESET_CATEGORY_TO_DEFAULTS = "UPDATE user_preferences SET preference_value = default_value WHERE category = ?";
	
	private static final String SQL_RESET_ALL_TO_DEFAULTS = "UPDATE user_preferences SET preference_value = default_value";
	
	private static final String SQL_EXISTS = "SELECT 1 FROM user_preferences WHERE preference_key = ?";
	
	private static final String SQL_COUNT_BY_CATEGORY = "SELECT COUNT(*) FROM user_preferences WHERE category = ?";
	
	private static final String SQL_SELECT_CUSTOMIZED = "SELECT preference_key, preference_value, preference_type, display_name, "
			+ "description, default_value, category, update_date " + "FROM user_preferences WHERE preference_value != default_value "
			+ "OR (preference_value IS NULL AND default_value IS NOT NULL) "
			+ "OR (preference_value IS NOT NULL AND default_value IS NULL) " + "ORDER BY category, display_name";
	
	/**
	 * Get all preferences ordered by category and display name
	 * 
	 * @return List of all preferences
	 * @throws SQLException if database error occurs
	 */
	public List<Preference> findAll() throws SQLException, TherapyAppException
	{
		List<Preference> preferences = new ArrayList<>();
		
		try( Connection connection = DbUtil.getConnection();
				PreparedStatement stmt = connection.prepareStatement( SQL_SELECT_ALL );
				ResultSet rs = stmt.executeQuery() )
		{
			while( rs.next() )
			{
				preferences.add( mapResultSetToPreference( rs ) );
			}
		}
		
		return preferences;
	}
	
	/**
	 * Get a preference by its key
	 * 
	 * @param preferenceKey The unique preference key
	 * @return Optional containing the preference if found
	 * @throws SQLException if database error occurs
	 */
	public Optional<Preference> findByKey( String preferenceKey ) throws SQLException, TherapyAppException
	{
		try( Connection connection = DbUtil.getConnection(); PreparedStatement stmt = connection.prepareStatement( SQL_SELECT_BY_KEY ) )
		{
			stmt.setString( 1, preferenceKey );
			
			try( ResultSet rs = stmt.executeQuery() )
			{
				if( rs.next() )
				{
					return Optional.of( mapResultSetToPreference( rs ) );
				}
			}
		}
		
		AppLogger.logDatabaseOperation( "SELECT", "user_preferences", true );
		return Optional.empty();
	}
	
	/**
	 * Get all preferences in a specific category
	 * 
	 * @param category The category name
	 * @return List of preferences in the category
	 * @throws SQLException if database error occurs
	 */
	public List<Preference> findByCategory( String category ) throws SQLException, TherapyAppException
	{
		List<Preference> preferences = new ArrayList<>();
		
		try( Connection connection = DbUtil.getConnection();
				PreparedStatement stmt = connection.prepareStatement( SQL_SELECT_BY_CATEGORY ) )
		{
			stmt.setString( 1, category );
			
			try( ResultSet rs = stmt.executeQuery() )
			{
				while( rs.next() )
				{
					preferences.add( mapResultSetToPreference( rs ) );
				}
			}
		}
		
		AppLogger.logDatabaseOperation( "SELECT", "user_preferences", true );
		return preferences;
	}
	
	/**
	 * Get all distinct category names
	 * 
	 * @return List of category names
	 * @throws SQLException if database error occurs
	 */
	public List<String> findAllCategories() throws SQLException
	{
		List<String> categories = new ArrayList<>();
		
		try( Connection connection = DbUtil.getConnection();
				PreparedStatement stmt = connection.prepareStatement( SQL_SELECT_CATEGORIES );
				ResultSet rs = stmt.executeQuery() )
		{
			while( rs.next() )
			{
				categories.add( rs.getString( "category" ) );
			}
		}
		
		AppLogger.logDatabaseOperation( "SELECT", "user_preferences", true );
		return categories;
	}
	
	/**
	 * Get all preferences grouped by category
	 * 
	 * @return Map of category name to list of preferences
	 * @throws SQLException if database error occurs
	 */
	public Map<String, List<Preference>> findAllGroupedByCategory() throws SQLException, TherapyAppException
	{
		Map<String, List<Preference>> grouped = new HashMap<>();
		List<Preference> allPreferences = findAll();
		
		for( Preference pref : allPreferences )
		{
			String category = pref.getCategory() != null ? pref.getCategory() : "General";
			grouped.computeIfAbsent( category, k -> new ArrayList<>() ).add( pref );
		}
		
		return grouped;
	}
	
	/**
	 * Get all preferences that have been customized (differ from default)
	 * 
	 * @return List of customized preferences
	 * @throws SQLException if database error occurs
	 */
	public List<Preference> findCustomized() throws SQLException, TherapyAppException
	{
		List<Preference> preferences = new ArrayList<>();
		
		try( Connection connection = DbUtil.getConnection();
				PreparedStatement stmt = connection.prepareStatement( SQL_SELECT_CUSTOMIZED );
				ResultSet rs = stmt.executeQuery() )
		{
			while( rs.next() )
			{
				preferences.add( mapResultSetToPreference( rs ) );
			}
		}
		
		AppLogger.logDatabaseOperation( "SELECT", "user_preferences", true );
		return preferences;
	}
	
	/**
	 * Insert a new preference
	 * 
	 * @param preference The preference to insert
	 * @return true if successful
	 * @throws SQLException if database error occurs
	 */
	public boolean insert( Preference preference ) throws SQLException
	{
		try( Connection connection = DbUtil.getConnection(); PreparedStatement stmt = connection.prepareStatement( SQL_INSERT ) )
		{
			stmt.setString( 1, preference.getPreferenceKey() );
			stmt.setString( 2, preference.getPreferenceValue() );
			stmt.setString( 3, preference.getPreferenceType() != null ? preference.getPreferenceType().getDbValue()
					: PreferenceType.STRING.getDbValue() );
			stmt.setString( 4, preference.getDisplayName() );
			stmt.setString( 5, preference.getDescription() );
			stmt.setString( 6, preference.getDefaultValue() );
			stmt.setString( 7, preference.getCategory() );
			
			AppLogger.logDatabaseOperation( "INSERT", "user_preferences", true );
			return stmt.executeUpdate() > 0;
		}
	}
	
	/**
	 * Update only the value of an existing preference
	 * 
	 * @param preferenceKey The preference key
	 * @param newValue      The new value
	 * @return true if successful
	 * @throws SQLException if database error occurs
	 */
	public boolean updateValue( String preferenceKey, String newValue ) throws SQLException
	{
		try( Connection connection = DbUtil.getConnection(); PreparedStatement stmt = connection.prepareStatement( SQL_UPDATE_VALUE ) )
		{
			stmt.setString( 1, newValue );
			stmt.setString( 2, preferenceKey );
			
			AppLogger.logDatabaseOperation( "UPDATE", "user_preferences", true );
			return stmt.executeUpdate() > 0;
		}
	}
	
	/**
	 * Update all fields of an existing preference
	 * 
	 * @param preference The preference with updated values
	 * @return true if successful
	 * @throws SQLException if database error occurs
	 */
	public boolean update( Preference preference ) throws SQLException
	{
		try( Connection connection = DbUtil.getConnection(); PreparedStatement stmt = connection.prepareStatement( SQL_UPDATE_FULL ) )
		{
			stmt.setString( 1, preference.getPreferenceValue() );
			stmt.setString( 2, preference.getPreferenceType() != null ? preference.getPreferenceType().getDbValue()
					: PreferenceType.STRING.getDbValue() );
			stmt.setString( 3, preference.getDisplayName() );
			stmt.setString( 4, preference.getDescription() );
			stmt.setString( 5, preference.getDefaultValue() );
			stmt.setString( 6, preference.getCategory() );
			stmt.setString( 7, preference.getPreferenceKey() );
			
			AppLogger.logDatabaseOperation( "UPDATE", "user_preferences", true );
			return stmt.executeUpdate() > 0;
		}
	}
	
	/**
	 * Insert or update a preference (upsert)
	 * 
	 * @param preference The preference to save
	 * @return true if successful
	 * @throws SQLException if database error occurs
	 */
	public boolean save( Preference preference ) throws SQLException
	{
		if( exists( preference.getPreferenceKey() ) )
		{
			return update( preference );
		}
		else
		{
			return insert( preference );
		}
	}
	
	/**
	 * Save multiple preferences in a batch
	 * 
	 * @param preferences List of preferences to save
	 * @return Number of preferences successfully saved
	 * @throws SQLException if database error occurs
	 */
	public int saveAll( List<Preference> preferences ) throws SQLException
	{
		int savedCount = 0;
		
		for( Preference pref : preferences )
		{
			if( save( pref ) )
			{
				savedCount++;
			}
		}
		
		return savedCount;
	}
	
	/**
	 * Delete a preference by key
	 * 
	 * @param preferenceKey The preference key to delete
	 * @return true if successful
	 * @throws SQLException if database error occurs
	 */
	public boolean delete( String preferenceKey ) throws SQLException
	{
		try( Connection connection = DbUtil.getConnection(); PreparedStatement stmt = connection.prepareStatement( SQL_DELETE ) )
		{
			stmt.setString( 1, preferenceKey );
			AppLogger.logDatabaseOperation( "DELETE", "user_preferences", true );
			return stmt.executeUpdate() > 0;
		}
	}
	
	/**
	 * Reset a single preference to its default value
	 * 
	 * @param preferenceKey The preference key to reset
	 * @return true if successful
	 * @throws SQLException if database error occurs
	 */
	public boolean resetToDefault( String preferenceKey ) throws SQLException
	{
		try( Connection connection = DbUtil.getConnection(); PreparedStatement stmt = connection.prepareStatement( SQL_RESET_TO_DEFAULT ) )
		{
			stmt.setString( 1, preferenceKey );
			AppLogger.logDatabaseOperation( "UPDATE", "user_preferences", true );
			return stmt.executeUpdate() > 0;
		}
	}
	
	/**
	 * Reset all preferences in a category to their default values
	 * 
	 * @param category The category to reset
	 * @return Number of preferences reset
	 * @throws SQLException if database error occurs
	 */
	public int resetCategoryToDefaults( String category ) throws SQLException
	{
		try( Connection connection = DbUtil.getConnection();
				PreparedStatement stmt = connection.prepareStatement( SQL_RESET_CATEGORY_TO_DEFAULTS ) )
		{
			stmt.setString( 1, category );
			AppLogger.logDatabaseOperation( "UPDATE", "user_preferences", true );
			return stmt.executeUpdate();
		}
	}
	
	/**
	 * Reset all preferences to their default values
	 * 
	 * @throws SQLException if database error occurs
	 */
	public void resetAllToDefaults() throws SQLException
	{
		try( Connection connection = DbUtil.getConnection();
				PreparedStatement stmt = connection.prepareStatement( SQL_RESET_ALL_TO_DEFAULTS ) )
		{
			stmt.executeUpdate();
			AppLogger.logDatabaseOperation( "UPDATE", "user_preferences", true );
		}
	}
	
	/**
	 * Check if a preference exists
	 * 
	 * @param preferenceKey The preference key to check
	 * @return true if preference exists
	 * @throws SQLException if database error occurs
	 */
	public boolean exists( String preferenceKey ) throws SQLException
	{
		try( Connection connection = DbUtil.getConnection(); PreparedStatement stmt = connection.prepareStatement( SQL_EXISTS ) )
		{
			stmt.setString( 1, preferenceKey );
			
			try( ResultSet rs = stmt.executeQuery() )
			{
				AppLogger.logDatabaseOperation( "SELECT", "user_preferences", true );
				return rs.next();
			}
		}
	}
	
	/**
	 * Get the count of preferences in a category
	 * 
	 * @param category The category name
	 * @return Count of preferences
	 * @throws SQLException if database error occurs
	 */
	public int countByCategory( String category ) throws SQLException
	{
		try( Connection connection = DbUtil.getConnection(); PreparedStatement stmt = connection.prepareStatement( SQL_COUNT_BY_CATEGORY ) )
		{
			stmt.setString( 1, category );
			
			try( ResultSet rs = stmt.executeQuery() )
			{
				if( rs.next() )
				{
					AppLogger.logDatabaseOperation( "SELECT", "user_preferences", true );
					return rs.getInt( 1 );
				}
			}
		}
		
		return 0;
	}
	
	/**
	 * Initialize default preferences if they don't exist. Call this during application startup to ensure all required preferences exist.
	 * 
	 * @param defaultPreferences List of default preferences to initialize
	 * @return Number of preferences initialized
	 * @throws SQLException if database error occurs
	 */
	public int initializeDefaults( List<Preference> defaultPreferences ) throws SQLException
	{
		int initialized = 0;
		
		for( Preference pref : defaultPreferences )
		{
			if( !exists( pref.getPreferenceKey() ) )
			{
				if( insert( pref ) )
				{
					initialized++;
				}
			}
		}
		
		return initialized;
	}
	
	/**
	 * Map a ResultSet row to a Preference object
	 * 
	 * @param rs The ResultSet positioned at the row to map
	 * @return The mapped Preference object
	 * @throws SQLException        if database error occurs
	 * @throws TherapyAppException
	 */
	private Preference mapResultSetToPreference( ResultSet rs ) throws SQLException, TherapyAppException
	{
		Preference pref = new Preference();
		
		pref.setPreferenceKey( rs.getString( "preference_key" ) );
		pref.setPreferenceValue( rs.getString( "preference_value" ) );
		pref.setPreferenceType( PreferenceType.fromDbValue( rs.getString( "preference_type" ) ) );
		pref.setDisplayName( rs.getString( "display_name" ) );
		pref.setDescription( rs.getString( "description" ) );
		pref.setDefaultValue( rs.getString( "default_value" ) );
		pref.setCategory( rs.getString( "category" ) );
		pref.setLastModified( DateFormatUtil.toLocalDateTime( rs.getString( "update_date" ) ) );
		
		return pref;
	}
}