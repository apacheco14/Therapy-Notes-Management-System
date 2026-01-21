package com.alexpacheco.therapynotes.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JavaUtilsTest
{
	@Test
	@DisplayName("isNullOrEmpty should detect null, empty, and non-empty strings")
	public void testIsNullOrEmpty()
	{
		assertTrue(JavaUtils.isNullOrEmpty(null), "Should be true for null");
		assertTrue(JavaUtils.isNullOrEmpty(""), "Should be true for empty string");
		assertTrue(JavaUtils.isNullOrEmpty(" "), "Should be true for space character");
		assertTrue(JavaUtils.isNullOrEmpty("	"), "Should be true for tab character");
		assertFalse(JavaUtils.isNullOrEmpty("text"), "Should be false for actual content");
	}
	
	@Test
	@DisplayName("convertBitToBoolean should map 0 to false and others to true")
	public void testConvertBitToBoolean()
	{
		assertFalse(JavaUtils.convertBitToBoolean(0), "0 must be false");
		assertTrue(JavaUtils.convertBitToBoolean(1), "1 must be true");
		
		// Testing "truthy" behavior for non-standard bits
		assertTrue(JavaUtils.convertBitToBoolean(-1), "Negative values should be true");
		assertTrue(JavaUtils.convertBitToBoolean(99), "High values should be true");
	}
	
	@Test
	@DisplayName("convertBooleanToBit should map boolean back to 0 or 1")
	public void testConvertBooleanToBit()
	{
		assertEquals(0, JavaUtils.convertBooleanToBit(false), "false must result in 0");
		assertEquals(1, JavaUtils.convertBooleanToBit(true), "true must result in 1");
	}
}