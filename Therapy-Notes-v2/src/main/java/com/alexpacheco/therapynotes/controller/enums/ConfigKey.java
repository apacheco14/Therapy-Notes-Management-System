package com.alexpacheco.therapynotes.controller.enums;

/**
 * Enum representing configuration keys for various system options
 */
public enum ConfigKey
{
	SYMPTOMS( "symptoms", "Symptoms" ),
	AFFECT( "affect", "Affect" ),
	EYE_CONTACT( "eyeContact", "Eye Contact" ),
	APPEARANCE( "appearance", "Appearance" ),
	SPEECH( "speech", "Speech" ),
	NEXT_APPOINTMENT( "nextAppt", "Next Appointment" ),
	COLLATERAL_CONTACT_TYPES( "collateralContacts", "Collateral Contacts" ),
	REFERRAL_TYPES( "referrals", "Referrals" );
	
	private final String key;
	private final String displayName;
	
	ConfigKey( String key, String displayName )
	{
		this.key = key;
		this.displayName = displayName;
	}
	
	public String getKey()
	{
		return key;
	}
	
	public String getDisplayName()
	{
		return displayName;
	}
	
	/**
	 * Get ConfigKey by its key string
	 * 
	 * @param key The key string to search for
	 * @return The matching ConfigKey, or null if not found
	 */
	public static ConfigKey fromKey( String key )
	{
		for( ConfigKey configKey : ConfigKey.values() )
		{
			if( configKey.getKey().equals( key ) )
			{
				return configKey;
			}
		}
		return null;
	}
	
	/**
	 * Get ConfigKey by its display name
	 * 
	 * @param displayName The display name to search for
	 * @return The matching ConfigKey, or null if not found
	 */
	public static ConfigKey fromDisplayName( String displayName )
	{
		for( ConfigKey configKey : ConfigKey.values() )
		{
			if( configKey.getDisplayName().equals( displayName ) )
			{
				return configKey;
			}
		}
		return null;
	}
}