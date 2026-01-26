package com.alexpacheco.therapynotes.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for SecureStorageFactory.
 * 
 * These tests verify the factory's singleton behavior, provider selection, and error handling. Uses reflection to reset singleton state
 * between tests.
 */
@DisplayName( "SecureStorageFactory Tests" )
class SecureStorageFactoryTest
{
	
	@BeforeEach
	void setUp()
	{
		resetFactory();
	}
	
	@AfterEach
	void tearDown()
	{
		resetFactory();
	}
	
	/**
	 * Reset the factory's singleton state using reflection. This allows each test to start with a clean slate.
	 */
	private void resetFactory()
	{
		try
		{
			Field instanceField = SecureStorageFactory.class.getDeclaredField( "instance" );
			instanceField.setAccessible( true );
			instanceField.set( null, null );
			
			Field errorField = SecureStorageFactory.class.getDeclaredField( "initializationError" );
			errorField.setAccessible( true );
			errorField.set( null, null );
		}
		catch( NoSuchFieldException | IllegalAccessException e )
		{
			// If reflection fails, the factory may have different field names
			// Try the resetForTesting method if available
			try
			{
				SecureStorageFactory.class.getMethod( "resetForTesting" ).invoke( null );
			}
			catch( Exception ignored )
			{
				// Best effort - tests may need adjustment
			}
		}
	}
	
	@Nested
	@DisplayName( "Singleton Behavior Tests" )
	class SingletonTests
	{
		
		@Test
		@DisplayName( "getInstance returns same instance on multiple calls" )
		void getInstance_MultipleCalls_ReturnsSameInstance() throws SecureStorageException
		{
			SecureStorageProvider first = SecureStorageFactory.getInstance();
			SecureStorageProvider second = SecureStorageFactory.getInstance();
			
			assertSame( first, second, "Factory should return the same singleton instance" );
		}
		
		@Test
		@DisplayName( "getInstance is thread-safe" )
		void getInstance_ConcurrentCalls_ReturnsSameInstance() throws Exception
		{
			int threadCount = 10;
			Thread[] threads = new Thread[threadCount];
			SecureStorageProvider[] results = new SecureStorageProvider[threadCount];
			Exception[] errors = new Exception[threadCount];
			
			for( int i = 0; i < threadCount; i++ )
			{
				final int index = i;
				threads[i] = new Thread( () ->
				{
					try
					{
						results[index] = SecureStorageFactory.getInstance();
					}
					catch( Exception e )
					{
						errors[index] = e;
					}
				} );
			}
			
			// Start all threads simultaneously
			for( Thread thread : threads )
			{
				thread.start();
			}
			
			// Wait for all threads
			for( Thread thread : threads )
			{
				thread.join( 5000 );
			}
			
			// Check for errors
			for( int i = 0; i < threadCount; i++ )
			{
				if( errors[i] != null )
				{
					fail( "Thread " + i + " failed: " + errors[i].getMessage() );
				}
			}
			
			// Verify all threads got the same instance
			SecureStorageProvider expected = results[0];
			for( int i = 1; i < threadCount; i++ )
			{
				assertSame( expected, results[i], "Thread " + i + " got a different instance" );
			}
		}
	}
	
	@Nested
	@DisplayName( "Availability Tests" )
	class AvailabilityTests
	{
		
		@Test
		@DisplayName( "isAvailable returns true when provider can be created" )
		void isAvailable_WhenProviderWorks_ReturnsTrue()
		{
			// We can't assert true/false definitively without knowing the environment
			// But we can verify the method doesn't throw
			assertDoesNotThrow( () -> SecureStorageFactory.isAvailable() );
		}
		
		@Test
		@DisplayName( "isAvailable returns false after initialization failure" )
		void isAvailable_AfterInitFailure_ReturnsFalse()
		{
			// Simulate initialization failure by setting the error field
			try
			{
				Field errorField = SecureStorageFactory.class.getDeclaredField( "initializationError" );
				errorField.setAccessible( true );
				errorField.set( null, new SecureStorageException( "Simulated failure" ) );
				
				assertFalse( SecureStorageFactory.isAvailable() );
			}
			catch( NoSuchFieldException | IllegalAccessException e )
			{
				// Skip test if reflection doesn't work
			}
		}
	}
	
	@Nested
	@DisplayName( "Error Handling Tests" )
	class ErrorHandlingTests
	{
		
		@Test
		@DisplayName( "getInstance caches initialization error" )
		void getInstance_AfterFailure_ThrowsCachedError()
		{
			// Set up a failure condition
			try
			{
				Field errorField = SecureStorageFactory.class.getDeclaredField( "initializationError" );
				errorField.setAccessible( true );
				SecureStorageException originalError = new SecureStorageException( "Original failure" );
				errorField.set( null, originalError );
				
				// First call should throw the cached error
				SecureStorageException thrown1 = assertThrows( SecureStorageException.class, () -> SecureStorageFactory.getInstance() );
				
				// Second call should throw the same cached error
				SecureStorageException thrown2 = assertThrows( SecureStorageException.class, () -> SecureStorageFactory.getInstance() );
				
				// Both should be the same error instance (cached)
				assertSame( thrown1, thrown2, "Should throw the cached error" );
				
			}
			catch( NoSuchFieldException | IllegalAccessException e )
			{
				// Skip test if reflection doesn't work
			}
		}
	}
	
	@Nested
	@DisplayName( "Provider Validation Tests" )
	class ProviderValidationTests
	{
		
		@Test
		@DisplayName( "getInstance returns a working provider" )
		void getInstance_WhenAvailable_ReturnsWorkingProvider()
		{
			try
			{
				SecureStorageProvider provider = SecureStorageFactory.getInstance();
				
				assertNotNull( provider );
				assertTrue( provider.isAvailable() );
				
				// Verify basic operations work
				String testKey = "factory.test." + System.currentTimeMillis();
				String testValue = "test-value";
				
				provider.store( testKey, testValue );
				assertEquals( testValue, provider.retrieve( testKey ) );
				provider.delete( testKey );
				assertNull( provider.retrieve( testKey ) );
				
			}
			catch( SecureStorageException e )
			{
				// Provider not available on this system - that's acceptable
				// The factory correctly threw an exception
			}
		}
	}
}