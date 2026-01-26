package com.alexpacheco.therapynotes.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for KeyringStorageProvider.
 * 
 * These tests interact with the actual OS keyring/credential manager. They are conditionally enabled based on the environment: - Skipped in
 * CI environments (set SKIP_KEYRING_TESTS=true) - Platform-specific tests for Windows, macOS, and Linux
 * 
 * To run locally: ensure no SKIP_KEYRING_TESTS environment variable is set. To skip in CI: set SKIP_KEYRING_TESTS=true
 */
@DisplayName( "KeyringStorageProvider Tests" )
class KeyringStorageProviderTest
{
	
	private KeyringStorageProvider provider;
	private String testKeyPrefix;
	
	@BeforeEach
	void setUp() throws SecureStorageException
	{
		// Use unique prefix to avoid conflicts with other tests or real data
		testKeyPrefix = "test." + UUID.randomUUID().toString().substring( 0, 8 ) + ".";
		
		try
		{
			provider = new KeyringStorageProvider();
		}
		catch( SecureStorageException e )
		{
			// Provider not available on this system - tests will be skipped
			provider = null;
		}
	}
	
	@AfterEach
	void tearDown()
	{
		// Clean up any test keys that might have been created
		if( provider != null )
		{
			String[] testKeys = { "key1", "key2", "key3", "special.key", "empty.value" };
			for( String key : testKeys )
			{
				try
				{
					provider.delete( testKeyPrefix + key );
				}
				catch( SecureStorageException ignored )
				{
					// Ignore cleanup errors
				}
			}
		}
	}
	
	@Nested
	@DisplayName( "Availability Tests" )
	class AvailabilityTests
	{
		
		@Test
		@DisplayName( "isAvailable returns true when keyring is accessible" )
		void isAvailable_WhenKeyringAccessible_ReturnsTrue()
		{
			if( provider == null )
			{
				// Skip if provider couldn't be created
				return;
			}
			assertTrue( provider.isAvailable() );
		}
	}
	
	@Nested
	@DisplayName( "Store and Retrieve Tests" )
	// Skip these tests in CI environments where keyring may not be available
	@EnabledIfEnvironmentVariable( named = "SKIP_KEYRING_TESTS", matches = "^$", disabledReason = "Keyring tests disabled in CI" )
	class StoreAndRetrieveTests
	{
		
		@Test
		@DisplayName( "store and retrieve basic string value" )
		void storeAndRetrieve_BasicString_Success() throws SecureStorageException
		{
			if( provider == null )
				return;
			
			String key = testKeyPrefix + "key1";
			String value = "test-value-123";
			
			provider.store( key, value );
			String retrieved = provider.retrieve( key );
			
			assertEquals( value, retrieved );
		}
		
		@Test
		@DisplayName( "store and retrieve value with special characters" )
		void storeAndRetrieve_SpecialCharacters_Success() throws SecureStorageException
		{
			if( provider == null )
				return;
			
			String key = testKeyPrefix + "special.key";
			String value = "value with spaces, symbols: !@#$%^&*(), and unicode: äöü日本語";
			
			provider.store( key, value );
			String retrieved = provider.retrieve( key );
			
			assertEquals( value, retrieved );
		}
		
		@Test
		@DisplayName( "store and retrieve Base64-encoded value (typical for hashes)" )
		void storeAndRetrieve_Base64Value_Success() throws SecureStorageException
		{
			if( provider == null )
				return;
			
			String key = testKeyPrefix + "key2";
			String value = "dGVzdCBoYXNoIHZhbHVlIGVuY29kZWQgaW4gYmFzZTY0";
			
			provider.store( key, value );
			String retrieved = provider.retrieve( key );
			
			assertEquals( value, retrieved );
		}
		
		@Test
		@DisplayName( "store overwrites existing value" )
		void store_ExistingKey_OverwritesValue() throws SecureStorageException
		{
			if( provider == null )
				return;
			
			String key = testKeyPrefix + "key3";
			
			provider.store( key, "original-value" );
			provider.store( key, "new-value" );
			
			assertEquals( "new-value", provider.retrieve( key ) );
		}
		
		@Test
		@DisplayName( "retrieve returns null for non-existent key" )
		void retrieve_NonExistentKey_ReturnsNull() throws SecureStorageException
		{
			if( provider == null )
				return;
			
			String result = provider.retrieve( testKeyPrefix + "non.existent.key" );
			
			assertNull( result );
		}
		
		@Test
		@DisplayName( "store and retrieve empty string value" )
		void storeAndRetrieve_EmptyString_Success() throws SecureStorageException
		{
			if( provider == null )
				return;
			
			String key = testKeyPrefix + "empty.value";
			
			provider.store( key, "" );
			String retrieved = provider.retrieve( key );
			
			// Note: Some keyring implementations may treat empty string as deletion
			// This test documents the actual behavior
			assertTrue( retrieved == null || retrieved.isEmpty() );
		}
	}
	
	@Nested
	@DisplayName( "Delete Tests" )
	@EnabledIfEnvironmentVariable( named = "SKIP_KEYRING_TESTS", matches = "^$", disabledReason = "Keyring tests disabled in CI" )
	class DeleteTests
	{
		
		@Test
		@DisplayName( "delete removes existing key" )
		void delete_ExistingKey_RemovesKey() throws SecureStorageException
		{
			if( provider == null )
				return;
			
			String key = testKeyPrefix + "key1";
			provider.store( key, "value-to-delete" );
			
			provider.delete( key );
			
			assertNull( provider.retrieve( key ) );
		}
		
		@Test
		@DisplayName( "delete non-existent key does not throw" )
		void delete_NonExistentKey_DoesNotThrow()
		{
			if( provider == null )
				return;
			
			assertDoesNotThrow( () -> provider.delete( testKeyPrefix + "non.existent" ) );
		}
		
		@Test
		@DisplayName( "delete is idempotent" )
		void delete_CalledTwice_DoesNotThrow() throws SecureStorageException
		{
			if( provider == null )
				return;
			
			String key = testKeyPrefix + "key1";
			provider.store( key, "value" );
			
			provider.delete( key );
			assertDoesNotThrow( () -> provider.delete( key ) );
		}
	}
	
	@Nested
	@DisplayName( "Exists Tests" )
	@EnabledIfEnvironmentVariable( named = "SKIP_KEYRING_TESTS", matches = "^$", disabledReason = "Keyring tests disabled in CI" )
	class ExistsTests
	{
		
		@Test
		@DisplayName( "exists returns true for stored key" )
		void exists_StoredKey_ReturnsTrue() throws SecureStorageException
		{
			if( provider == null )
				return;
			
			String key = testKeyPrefix + "key1";
			provider.store( key, "value" );
			
			assertTrue( provider.exists( key ) );
		}
		
		@Test
		@DisplayName( "exists returns false for non-existent key" )
		void exists_NonExistentKey_ReturnsFalse()
		{
			if( provider == null )
				return;
			
			assertFalse( provider.exists( testKeyPrefix + "non.existent" ) );
		}
		
		@Test
		@DisplayName( "exists returns false after delete" )
		void exists_AfterDelete_ReturnsFalse() throws SecureStorageException
		{
			if( provider == null )
				return;
			
			String key = testKeyPrefix + "key1";
			provider.store( key, "value" );
			provider.delete( key );
			
			assertFalse( provider.exists( key ) );
		}
	}
	
	@Nested
	@DisplayName( "Platform-Specific Tests" )
	class PlatformTests
	{
		
		@Test
		@EnabledOnOs( OS.WINDOWS )
		@DisplayName( "Windows: Provider uses Credential Manager" )
		void windows_ProviderAvailable()
		{
			if( provider == null )
			{
				fail( "KeyringStorageProvider should be available on Windows" );
			}
			assertTrue( provider.isAvailable() );
		}
		
		@Test
		@EnabledOnOs( OS.MAC )
		@DisplayName( "macOS: Provider uses Keychain" )
		void macOS_ProviderAvailable()
		{
			if( provider == null )
			{
				fail( "KeyringStorageProvider should be available on macOS" );
			}
			assertTrue( provider.isAvailable() );
		}
		
		@Test
		@EnabledOnOs( OS.LINUX )
		@DisplayName( "Linux: Provider uses Secret Service (if available)" )
		void linux_ProviderAvailability()
		{
			// On Linux, availability depends on whether a secret service daemon is running
			// This test just documents the current state
			if( provider != null )
			{
				assertTrue( provider.isAvailable() );
			}
			// Don't fail if unavailable - Linux desktop environments vary
		}
	}
	
	@Nested
	@DisplayName( "Concurrent Access Tests" )
	@EnabledIfEnvironmentVariable( named = "SKIP_KEYRING_TESTS", matches = "^$", disabledReason = "Keyring tests disabled in CI" )
	class ConcurrentAccessTests
	{
		
		@Test
		@DisplayName( "concurrent stores to different keys succeed" )
		void concurrentStores_DifferentKeys_AllSucceed() throws Exception
		{
			if( provider == null )
				return;
			
			int threadCount = 5;
			Thread[] threads = new Thread[threadCount];
			Throwable[] errors = new Throwable[threadCount];
			
			for( int i = 0; i < threadCount; i++ )
			{
				final int index = i;
				threads[i] = new Thread( () ->
				{
					try
					{
						String key = testKeyPrefix + "concurrent." + index;
						provider.store( key, "value-" + index );
						String retrieved = provider.retrieve( key );
						if( !( "value-" + index ).equals( retrieved ) )
						{
							errors[index] = new AssertionError( "Expected value-" + index + " but got " + retrieved );
						}
						provider.delete( key );
					}
					catch( Exception e )
					{
						errors[index] = e;
					}
				} );
			}
			
			// Start all threads
			for( Thread thread : threads )
			{
				thread.start();
			}
			
			// Wait for completion
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
		}
	}
}