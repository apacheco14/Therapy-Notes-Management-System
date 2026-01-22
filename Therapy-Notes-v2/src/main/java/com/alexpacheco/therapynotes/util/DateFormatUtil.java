package com.alexpacheco.therapynotes.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

import com.alexpacheco.therapynotes.controller.enums.ErrorCode;
import com.alexpacheco.therapynotes.controller.exceptions.TherapyAppException;

public class DateFormatUtil
{
	// SQLite standard format: YYYY-MM-DD HH:MM:SS
	private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
	private static final DateTimeFormatter SQLITE_FORMATTER = DateTimeFormatter.ofPattern( DATE_PATTERN );
	private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern( "yyyy-MM-dd" );
	
	public static String toSimpleString( Date date )
	{
		if( date == null )
			return null;
		
		return new java.text.SimpleDateFormat( "MM/dd/yyyy" ).format( date );
	}
	
	/**
	 * Converts a LocalDateTime object to a SQLite-compatible String. Example: 2026-01-06T14:30:00 -> "2026-01-06 14:30:00"
	 */
	public static String toSqliteString( LocalDateTime dateTime )
	{
		if( dateTime == null )
		{
			return null;
		}
		return dateTime.format( SQLITE_FORMATTER );
	}
	
	public static String toDateFileNameString( LocalDateTime dateTime )
	{
		if( dateTime == null )
		{
			return null;
		}
		return dateTime.format( DATE_FORMATTER );
	}
	
	public static String toSqliteString( Date date )
	{
		if( date == null )
			return null;
		
		return LocalDateTime.ofInstant( date.toInstant(), ZoneId.systemDefault() ).format( SQLITE_FORMATTER );
	}
	
	/**
	 * Parses a SQLite timestamp String back into a LocalDateTime object. Example: "2026-01-06 14:30:00" -> LocalDateTime object
	 * 
	 * @throws TherapyAppException
	 */
	public static LocalDateTime toLocalDateTime( String timestamp ) throws TherapyAppException
	{
		if( JavaUtils.isNullOrEmpty( timestamp ) )
		{
			return null;
		}
		try
		{
			return LocalDateTime.parse( timestamp, SQLITE_FORMATTER );
		}
		catch( DateTimeParseException e )
		{
			AppLogger.error( "Date parse error: " + timestamp + " is not in format " + DATE_PATTERN, e );
			throw new TherapyAppException( "Date parse error: " + timestamp + " is not in format " + DATE_PATTERN, ErrorCode.DATE_PARSE );
		}
	}
	
	/**
	 * Returns the current system time formatted for SQLite. Useful for setting 'insert_date' manually if needed.
	 */
	public static String now()
	{
		return LocalDateTime.now().format( SQLITE_FORMATTER );
	}
	
	public static Date toDate( LocalDateTime localDateTime )
	{
		if( localDateTime == null )
			return null;
		
		return Date.from( localDateTime.atZone( ZoneId.systemDefault() ).toInstant() );
	}
}