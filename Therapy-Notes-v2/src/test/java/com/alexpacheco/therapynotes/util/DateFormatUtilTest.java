package test.java.com.alexpacheco.therapynotes.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import main.java.com.alexpacheco.therapynotes.controller.errorhandling.exceptions.TherapyAppException;
import main.java.com.alexpacheco.therapynotes.util.DateFormatUtil;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Date;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.*;

public class DateFormatUtilTest
{
	@Test
	@DisplayName("toString should format LocalDateTime to SQLite string format")
	public void testToString()
	{
		// Setup: January 6, 2026 at 2:30:45 PM
		LocalDateTime dt = LocalDateTime.of(2026, Month.JANUARY, 6, 14, 30, 45);
		String expected = "2026-01-06 14:30:45";
		
		assertEquals(expected, DateFormatUtil.toSqliteString(dt));
	}
	
	@Test
	@DisplayName("toString should return null when input is null")
	public void testToStringNull()
	{
		assertNull(DateFormatUtil.toSqliteString((LocalDateTime) null));
		assertNull(DateFormatUtil.toSqliteString((Date) null));
	}
	
	@Test
	@DisplayName("toLocalDateTime should parse valid SQLite string correctly")
	public void testToLocalDateTime() throws TherapyAppException
	{
		String input = "2026-01-06 14:30:45";
		LocalDateTime expected = LocalDateTime.of(2026, Month.JANUARY, 6, 14, 30, 45);
		
		LocalDateTime result = DateFormatUtil.toLocalDateTime(input);
		
		assertNotNull(result);
		assertEquals(expected, result);
	}
	
	@Test
	@DisplayName("toLocalDateTime should return null for null or empty input")
	public void testToLocalDateTimeEmpty() throws TherapyAppException
	{
		assertNull(DateFormatUtil.toLocalDateTime((String) null));
		assertNull(DateFormatUtil.toLocalDateTime(""));
		assertNull(DateFormatUtil.toLocalDateTime("   "));
	}
	
	@Test
	@DisplayName("toLocalDateTime should throw exception invalid format")
	public void testToLocalDateTimeInvalidFormat()
	{
		assertThrows(TherapyAppException.class, () ->
		{
			// Using common incorrect formats
			DateFormatUtil.toLocalDateTime("01/06/2026 14:30:00");
			DateFormatUtil.toLocalDateTime("2026-01-06T14:30:00"); // ISO format with T
			DateFormatUtil.toLocalDateTime("Not a date");
		});
	}
	
	@Test
	@DisplayName("now should return current time in correct SQLite string length")
	public void testNow() throws TherapyAppException
	{
		String currentTime = DateFormatUtil.now();
		
		// Check that it matches the expected pattern: YYYY-MM-DD HH:MM:SS (19 characters)
		assertNotNull(currentTime);
		assertEquals(19, currentTime.length());
		
		// Verify it can be parsed back into a LocalDateTime without error
		assertNotNull(DateFormatUtil.toLocalDateTime(currentTime));
	}
}