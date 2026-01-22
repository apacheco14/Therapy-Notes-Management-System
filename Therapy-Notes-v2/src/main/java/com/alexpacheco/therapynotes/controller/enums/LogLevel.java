package com.alexpacheco.therapynotes.controller.enums;

public enum LogLevel
{
	TRACE( 1, "FINE" ),
	INFO( 2, "INFO" ),
	WARN( 3, "WARNING" ),
	ERROR( 4, "SEVERE" );
	
	private int level;
	private String dbCode;
	
	LogLevel( int level, String dbCode )
	{
		this.level = level;
		this.dbCode = dbCode;
	}
	
	public int getLevel()
	{
		return level;
	}
	
	public String getDbCode()
	{
		return dbCode;
	}
}
