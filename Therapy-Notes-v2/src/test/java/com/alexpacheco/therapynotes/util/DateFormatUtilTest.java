package com.alexpacheco.therapynotes.util;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.alexpacheco.therapynotes.controller.exceptions.TherapyAppException;

/**
 * Unit tests for {@link DateFormatUtil}.
 */
@DisplayName( "DateFormatUtil" )
class DateFormatUtilTest
{
	@Nested
	@DisplayName( "toSqliteString(LocalDateTime)" )
	class ToSqliteStringLocalDateTime
	{
		@Test
		@DisplayName( "Formats LocalDateTime to SQLite string format" )
		void formatsCorrectly()
		{
			LocalDateTime dt = LocalDateTime.of( 2026, Month.JANUARY, 6, 14, 30, 45 );
			assertEquals( "2026-01-06 14:30:45", DateFormatUtil.toSqliteString( dt ) );
		}
		
		@Test
		@DisplayName( "Returns null when input is null" )
		void nullInput_returnsNull()
		{
			assertNull( DateFormatUtil.toSqliteString( (LocalDateTime) null ) );
		}
		
		@Test
		@DisplayName( "Pads single-digit month and day with zeros" )
		void padsSingleDigits()
		{
			LocalDateTime dt = LocalDateTime.of( 2026, Month.MARCH, 5, 9, 5, 3 );
			assertEquals( "2026-03-05 09:05:03", DateFormatUtil.toSqliteString( dt ) );
		}
		
		@Test
		@DisplayName( "Handles midnight correctly" )
		void midnight()
		{
			LocalDateTime dt = LocalDateTime.of( 2026, Month.JANUARY, 1, 0, 0, 0 );
			assertEquals( "2026-01-01 00:00:00", DateFormatUtil.toSqliteString( dt ) );
		}
		
		@Test
		@DisplayName( "Handles end of day correctly" )
		void endOfDay()
		{
			LocalDateTime dt = LocalDateTime.of( 2026, Month.DECEMBER, 31, 23, 59, 59 );
			assertEquals( "2026-12-31 23:59:59", DateFormatUtil.toSqliteString( dt ) );
		}
		
		@Test
		@DisplayName( "Handles leap year date" )
		void leapYearDate()
		{
			LocalDateTime dt = LocalDateTime.of( 2024, Month.FEBRUARY, 29, 12, 0, 0 );
			assertEquals( "2024-02-29 12:00:00", DateFormatUtil.toSqliteString( dt ) );
		}
	}
	
	@Nested
	@DisplayName( "toSqliteString(Date)" )
	class ToSqliteStringDate
	{
		@Test
		@DisplayName( "Returns null when input is null" )
		void nullInput_returnsNull()
		{
			assertNull( DateFormatUtil.toSqliteString( (Date) null ) );
		}
		
		@Test
		@DisplayName( "Converts Date to SQLite string format" )
		void formatsCorrectly()
		{
			// Create a Date for January 6, 2026 at 2:30:45 PM
			Calendar cal = Calendar.getInstance();
			cal.set( 2026, Calendar.JANUARY, 6, 14, 30, 45 );
			cal.set( Calendar.MILLISECOND, 0 );
			Date date = cal.getTime();
			
			String result = DateFormatUtil.toSqliteString( date );
			assertEquals( "2026-01-06 14:30:45", result );
		}
		
		@Test
		@DisplayName( "Handles midnight correctly" )
		void midnight()
		{
			Calendar cal = Calendar.getInstance();
			cal.set( 2026, Calendar.JANUARY, 1, 0, 0, 0 );
			cal.set( Calendar.MILLISECOND, 0 );
			Date date = cal.getTime();
			
			String result = DateFormatUtil.toSqliteString( date );
			assertEquals( "2026-01-01 00:00:00", result );
		}
	}
	
	@Nested
	@DisplayName( "toSimpleString(Date)" )
	class ToSimpleString
	{
		@Test
		@DisplayName( "Returns null when input is null" )
		void nullInput_returnsNull()
		{
			assertNull( DateFormatUtil.toSimpleString( null ) );
		}
		
		@Test
		@DisplayName( "Formats Date to MM/dd/yyyy format" )
		void formatsCorrectly()
		{
			Calendar cal = Calendar.getInstance();
			cal.set( 2026, Calendar.JANUARY, 6 );
			Date date = cal.getTime();
			
			assertEquals( "01/06/2026", DateFormatUtil.toSimpleString( date ) );
		}
		
		@Test
		@DisplayName( "Pads single-digit month and day with zeros" )
		void padsSingleDigits()
		{
			Calendar cal = Calendar.getInstance();
			cal.set( 2026, Calendar.MARCH, 5 );
			Date date = cal.getTime();
			
			assertEquals( "03/05/2026", DateFormatUtil.toSimpleString( date ) );
		}
		
		@Test
		@DisplayName( "Handles double-digit month and day" )
		void doubleDigits()
		{
			Calendar cal = Calendar.getInstance();
			cal.set( 2026, Calendar.DECEMBER, 25 );
			Date date = cal.getTime();
			
			assertEquals( "12/25/2026", DateFormatUtil.toSimpleString( date ) );
		}
	}
	
	@Nested
	@DisplayName( "toDateFileNameString(LocalDateTime)" )
	class ToDateFileNameString
	{
		@Test
		@DisplayName( "Returns null when input is null" )
		void nullInput_returnsNull()
		{
			assertNull( DateFormatUtil.toDateFileNameString( null ) );
		}
		
		@Test
		@DisplayName( "Formats LocalDateTime to yyyy-MM-dd format" )
		void formatsCorrectly()
		{
			LocalDateTime dt = LocalDateTime.of( 2026, Month.JANUARY, 6, 14, 30, 45 );
			assertEquals( "2026-01-06", DateFormatUtil.toDateFileNameString( dt ) );
		}
		
		@Test
		@DisplayName( "Pads single-digit month and day with zeros" )
		void padsSingleDigits()
		{
			LocalDateTime dt = LocalDateTime.of( 2026, Month.MARCH, 5, 9, 5, 3 );
			assertEquals( "2026-03-05", DateFormatUtil.toDateFileNameString( dt ) );
		}
		
		@Test
		@DisplayName( "Ignores time component" )
		void ignoresTime()
		{
			LocalDateTime dt1 = LocalDateTime.of( 2026, Month.JANUARY, 6, 0, 0, 0 );
			LocalDateTime dt2 = LocalDateTime.of( 2026, Month.JANUARY, 6, 23, 59, 59 );
			
			assertEquals( DateFormatUtil.toDateFileNameString( dt1 ), DateFormatUtil.toDateFileNameString( dt2 ) );
		}
	}
	
	@Nested
	@DisplayName( "toLocalDateTime(String)" )
	class ToLocalDateTime
	{
		@Test
		@DisplayName( "Parses valid SQLite string correctly" )
		void parsesCorrectly() throws TherapyAppException
		{
			String input = "2026-01-06 14:30:45";
			LocalDateTime expected = LocalDateTime.of( 2026, Month.JANUARY, 6, 14, 30, 45 );
			
			assertEquals( expected, DateFormatUtil.toLocalDateTime( input ) );
		}
		
		@Test
		@DisplayName( "Returns null for null input" )
		void nullInput_returnsNull() throws TherapyAppException
		{
			assertNull( DateFormatUtil.toLocalDateTime( null ) );
		}
		
		@Test
		@DisplayName( "Returns null for empty string" )
		void emptyString_returnsNull() throws TherapyAppException
		{
			assertNull( DateFormatUtil.toLocalDateTime( "" ) );
		}
		
		@Test
		@DisplayName( "Returns null for whitespace-only string" )
		void whitespaceOnly_returnsNull() throws TherapyAppException
		{
			assertNull( DateFormatUtil.toLocalDateTime( "   " ) );
		}
		
		@Test
		@DisplayName( "Parses midnight correctly" )
		void parsesMidnight() throws TherapyAppException
		{
			LocalDateTime expected = LocalDateTime.of( 2026, Month.JANUARY, 1, 0, 0, 0 );
			assertEquals( expected, DateFormatUtil.toLocalDateTime( "2026-01-01 00:00:00" ) );
		}
		
		@Test
		@DisplayName( "Parses end of day correctly" )
		void parsesEndOfDay() throws TherapyAppException
		{
			LocalDateTime expected = LocalDateTime.of( 2026, Month.DECEMBER, 31, 23, 59, 59 );
			assertEquals( expected, DateFormatUtil.toLocalDateTime( "2026-12-31 23:59:59" ) );
		}
		
		@ParameterizedTest( name = "Throws exception for invalid format: \"{0}\"" )
		@ValueSource( strings = { "01/06/2026 14:30:00", // US date format
				"2026-01-06T14:30:00", // ISO format with T
				"2026/01/06 14:30:00", // Wrong separator
				"Not a date", // Plain text
				"2026-01-06", // Missing time
				"14:30:00", // Missing date
				"2026-1-6 14:30:00", // Missing zero padding
				"2026-01-06 14:30", // Missing seconds
				"2026-01-06 2:30:00 PM" // 12-hour format with AM/PM
		} )
		void invalidFormat_throwsException( String input )
		{
			TherapyAppException ex = assertThrows( TherapyAppException.class, () -> DateFormatUtil.toLocalDateTime( input ) );
			
			assertTrue( ex.getMessage().contains( "Date parse error" ) );
		}
		
		@Test
		@DisplayName( "Throws exception for invalid date values" )
		void invalidDateValues_throwsException()
		{
			// Invalid month
			assertThrows( TherapyAppException.class, () -> DateFormatUtil.toLocalDateTime( "2026-13-01 00:00:00" ) );
			
			// Invalid day
			assertThrows( TherapyAppException.class, () -> DateFormatUtil.toLocalDateTime( "2026-01-32 00:00:00" ) );
			
			// Invalid hour
			assertThrows( TherapyAppException.class, () -> DateFormatUtil.toLocalDateTime( "2026-01-01 25:00:00" ) );
		}
	}
	
	@Nested
	@DisplayName( "toDate(LocalDateTime)" )
	class ToDate
	{
		@Test
		@DisplayName( "Returns null when input is null" )
		void nullInput_returnsNull()
		{
			assertNull( DateFormatUtil.toDate( null ) );
		}
		
		@Test
		@DisplayName( "Converts LocalDateTime to Date correctly" )
		void convertsCorrectly()
		{
			LocalDateTime ldt = LocalDateTime.of( 2026, Month.JANUARY, 6, 14, 30, 45 );
			Date result = DateFormatUtil.toDate( ldt );
			
			assertNotNull( result );
			
			// Convert back to LocalDateTime to verify
			LocalDateTime converted = result.toInstant().atZone( ZoneId.systemDefault() ).toLocalDateTime();
			
			assertEquals( ldt, converted );
		}
		
		@Test
		@DisplayName( "Preserves time components" )
		void preservesTimeComponents()
		{
			LocalDateTime ldt = LocalDateTime.of( 2026, Month.MARCH, 15, 9, 5, 33 );
			Date result = DateFormatUtil.toDate( ldt );
			
			Calendar cal = Calendar.getInstance();
			cal.setTime( result );
			
			assertEquals( 2026, cal.get( Calendar.YEAR ) );
			assertEquals( Calendar.MARCH, cal.get( Calendar.MONTH ) );
			assertEquals( 15, cal.get( Calendar.DAY_OF_MONTH ) );
			assertEquals( 9, cal.get( Calendar.HOUR_OF_DAY ) );
			assertEquals( 5, cal.get( Calendar.MINUTE ) );
			assertEquals( 33, cal.get( Calendar.SECOND ) );
		}
	}
	
	@Nested
	@DisplayName( "now()" )
	class Now
	{
		@Test
		@DisplayName( "Returns non-null result" )
		void returnsNonNull()
		{
			assertNotNull( DateFormatUtil.now() );
		}
		
		@Test
		@DisplayName( "Returns string in correct format (19 characters)" )
		void correctLength()
		{
			String result = DateFormatUtil.now();
			assertEquals( 19, result.length(), "SQLite datetime should be 19 chars: YYYY-MM-DD HH:MM:SS" );
		}
		
		@Test
		@DisplayName( "Result matches expected pattern" )
		void matchesPattern()
		{
			String result = DateFormatUtil.now();
			assertTrue( result.matches( "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}" ), "Should match YYYY-MM-DD HH:MM:SS pattern" );
		}
		
		@Test
		@DisplayName( "Result can be parsed back to LocalDateTime" )
		void canBeParsedBack() throws TherapyAppException
		{
			String now = DateFormatUtil.now();
			LocalDateTime parsed = DateFormatUtil.toLocalDateTime( now );
			
			assertNotNull( parsed );
		}
		
		@Test
		@DisplayName( "Returns approximately current time" )
		void returnsCurrentTime() throws TherapyAppException
		{
			LocalDateTime before = LocalDateTime.now().minusSeconds( 1 );
			String nowStr = DateFormatUtil.now();
			LocalDateTime after = LocalDateTime.now().plusSeconds( 1 );
			
			LocalDateTime result = DateFormatUtil.toLocalDateTime( nowStr );
			
			assertTrue( !result.isBefore( before ) && !result.isAfter( after ), "Result should be within 1 second of actual current time" );
		}
	}
	
	@Nested
	@DisplayName( "Round-trip conversions" )
	class RoundTrip
	{
		@Test
		@DisplayName( "LocalDateTime -> String -> LocalDateTime preserves value" )
		void localDateTimeRoundTrip() throws TherapyAppException
		{
			LocalDateTime original = LocalDateTime.of( 2026, Month.JUNE, 15, 10, 30, 45 );
			
			String asString = DateFormatUtil.toSqliteString( original );
			LocalDateTime restored = DateFormatUtil.toLocalDateTime( asString );
			
			assertEquals( original, restored );
		}
		
		@Test
		@DisplayName( "LocalDateTime -> Date -> String -> LocalDateTime preserves value" )
		void mixedRoundTrip() throws TherapyAppException
		{
			LocalDateTime original = LocalDateTime.of( 2026, Month.JUNE, 15, 10, 30, 45 );
			
			Date asDate = DateFormatUtil.toDate( original );
			String asString = DateFormatUtil.toSqliteString( asDate );
			LocalDateTime restored = DateFormatUtil.toLocalDateTime( asString );
			
			assertEquals( original, restored );
		}
	}
}