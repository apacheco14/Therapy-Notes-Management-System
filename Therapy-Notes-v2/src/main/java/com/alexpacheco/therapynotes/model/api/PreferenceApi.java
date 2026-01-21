package com.alexpacheco.therapynotes.model.api;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import com.alexpacheco.therapynotes.controller.AppController;
import com.alexpacheco.therapynotes.controller.enums.ErrorCode;
import com.alexpacheco.therapynotes.controller.errorhandling.exceptions.TherapyAppException;
import com.alexpacheco.therapynotes.model.dao.PreferencesDao;
import com.alexpacheco.therapynotes.model.entities.Preference;

public class PreferenceApi
{
	private PreferencesDao preferencesDao = new PreferencesDao();
	
	public int savePreferences( List<Preference> preferences ) throws TherapyAppException
	{
		try
		{
			return preferencesDao.saveAll( preferences );
		}
		catch( SQLException e )
		{
			AppController.logException( "PreferenceApi", e );
			throw new TherapyAppException( "An internal database error occurred.", ErrorCode.DB_ERROR );
		}
	}
	
	public void resetAllToDefaults() throws TherapyAppException
	{
		try
		{
			preferencesDao.resetAllToDefaults();
		}
		catch( SQLException e )
		{
			AppController.logException( "PreferenceApi", e );
			throw new TherapyAppException( "An internal database error occurred.", ErrorCode.DB_ERROR );
		}
	}
	
	/**
	 * Get the value of a preference by key, returning default if not found
	 * 
	 * @param preferenceKey The preference key
	 * @param defaultValue  The default value to return if preference not found
	 * @return The preference value or default
	 * @throws TherapyAppException if database error occurs
	 */
	public String getValue( String preferenceKey, String defaultValue )
	{
		try
		{
			Optional<Preference> pref = preferencesDao.findByKey( preferenceKey );
			return pref.map( Preference::getValueAsString ).orElse( defaultValue );
		}
		catch( SQLException | TherapyAppException e )
		{
			AppController.logException( "PreferenceApi", e );
			return defaultValue;
		}
	}
	
	/**
	 * Get a boolean preference value
	 * 
	 * @param preferenceKey The preference key
	 * @param defaultValue  The default value if not found
	 * @return The boolean value
	 * @throws TherapyAppException if database error occurs
	 */
	public boolean getBoolean( String preferenceKey, boolean defaultValue )
	{
		try
		{
			Optional<Preference> pref = preferencesDao.findByKey( preferenceKey );
			return pref.map( Preference::getValueAsBoolean ).orElse( defaultValue );
		}
		catch( SQLException | TherapyAppException e )
		{
			AppController.logException( "PreferenceApi", e );
			return defaultValue;
		}
	}
	
	/**
	 * Get an integer preference value
	 * 
	 * @param preferenceKey The preference key
	 * @param defaultValue  The default value if not found
	 * @return The integer value
	 * @throws TherapyAppException if database error occurs
	 */
	public Integer getInt( String preferenceKey, int defaultValue )
	{
		try
		{
			Optional<Preference> pref = preferencesDao.findByKey( preferenceKey );
			return pref.map( Preference::getValueAsInt ).orElse( defaultValue );
		}
		catch( SQLException | TherapyAppException e )
		{
			AppController.logException( "PreferenceApi", e );
			return defaultValue;
		}
	}
	
	/**
	 * Get a double preference value
	 * 
	 * @param preferenceKey The preference key
	 * @param defaultValue  The default value if not found
	 * @return The double value
	 * @throws TherapyAppException if database error occurs
	 */
	public double getDouble( String preferenceKey, double defaultValue )
	{
		try
		{
			Optional<Preference> pref = preferencesDao.findByKey( preferenceKey );
			return pref.map( Preference::getValueAsDouble ).orElse( defaultValue );
		}
		catch( SQLException | TherapyAppException e )
		{
			AppController.logException( "PreferenceApi", e );
			return defaultValue;
		}
	}
}
