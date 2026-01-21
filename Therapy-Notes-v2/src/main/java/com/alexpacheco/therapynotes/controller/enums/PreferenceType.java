package com.alexpacheco.therapynotes.controller.enums;

public enum PreferenceType
{
	STRING("STRING"),
	BOOLEAN("BOOLEAN"),
	INTEGER("INTEGER"),
	DOUBLE("DOUBLE"),
	DATE("DATE");
	
	private final String dbValue;
	
	PreferenceType(String dbValue)
	{
		this.dbValue = dbValue;
	}
	
	public String getDbValue()
	{
		return dbValue;
	}
	
	/**
	 * Get PreferenceType from database string value
	 * 
	 * @param dbValue The database string value
	 * @return The corresponding PreferenceType, or STRING if not found
	 */
	public static PreferenceType fromDbValue(String dbValue)
	{
		for (PreferenceType type : PreferenceType.values())
		{
			if (type.dbValue.equals(dbValue))
			{
				return type;
			}
		}
		return STRING;
	}
}