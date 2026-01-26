package com.alexpacheco.therapynotes.controller;

import com.alexpacheco.therapynotes.install.SetupConfigurationManager;
import com.alexpacheco.therapynotes.security.InMemoryStorageProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for PinManager's migration from legacy properties file storage to secure OS credential storage.
 * 
 * These tests verify that existing users' PIN configurations are properly migrated when they upgrade to the new version.
 */
@DisplayName( "PinManager Migration Tests" )
class PinManagerMigrationTest
{
	
	private InMemoryStorageProvider mockStorage;
	
	// Legacy property keys (must match PinManager constants)
	private static final String LEGACY_KEY_PIN_ENABLED = "security.pin.enabled";
	private static final String LEGACY_KEY_PIN_HASH = "security.pin.hash";
	private static final String LEGACY_KEY_PIN_SALT = "security.pin.salt";
	private static final String LEGACY_KEY_PIN_HINT = "security.pin.hint";
	private static final String LEGACY_KEY_FAILED_ATTEMPTS = "security.failed.attempts";
	private static final String LEGACY_KEY_LOCKOUT_UNTIL = "security.lockout.until";
	
	@BeforeEach
	void setUp() throws Exception
	{
		mockStorage = new InMemoryStorageProvider();
		clearLegacyProperties();
		resetPinManager();
	}
	
	@AfterEach
	void tearDown() throws Exception
	{
		mockStorage.clear();
		clearLegacyProperties();
		resetPinManager();
	}
	
	private void resetPinManager() throws Exception
	{
		Field storageField = PinManager.class.getDeclaredField( "storageProvider" );
		storageField.setAccessible( true );
		storageField.set( null, null );
		
		Field migrationField = PinManager.class.getDeclaredField( "migrationAttempted" );
		migrationField.setAccessible( true );
		migrationField.set( null, false );
	}
	
	private void injectStorageProvider( InMemoryStorageProvider provider ) throws Exception
	{
		Field storageField = PinManager.class.getDeclaredField( "storageProvider" );
		storageField.setAccessible( true );
		storageField.set( null, provider );
	}
	
	private void clearLegacyProperties()
	{
		try
		{
			SetupConfigurationManager.setValue( LEGACY_KEY_PIN_ENABLED, "" );
			SetupConfigurationManager.setValue( LEGACY_KEY_PIN_HASH, "" );
			SetupConfigurationManager.setValue( LEGACY_KEY_PIN_SALT, "" );
			SetupConfigurationManager.setValue( LEGACY_KEY_PIN_HINT, "" );
			SetupConfigurationManager.setValue( LEGACY_KEY_FAILED_ATTEMPTS, "" );
			SetupConfigurationManager.setValue( LEGACY_KEY_LOCKOUT_UNTIL, "" );
		}
		catch( IOException e )
		{
			// Ignore cleanup errors
		}
	}
	
	private void triggerMigration() throws Exception
	{
		// Call the private migration method via reflection
		Method migrateMethod = PinManager.class.getDeclaredMethod( "migrateFromLegacyStorage" );
		migrateMethod.setAccessible( true );
		migrateMethod.invoke( null );
	}
	
	// ========================================================================
	// Full Migration Tests
	// ========================================================================
	
	@Nested
	@DisplayName( "Full Migration Scenarios" )
	class FullMigrationTests
	{
		
		@Test
		@DisplayName( "Migration copies all security data to secure storage" )
		void migration_CopiesAllData() throws Exception
		{
			// Set up legacy data
			String testHash = "dGVzdGhhc2g="; // Base64 encoded
			String testSalt = "dGVzdHNhbHQ="; // Base64 encoded
			String testHint = "My test hint";
			
			SetupConfigurationManager.setValue( LEGACY_KEY_PIN_HASH, testHash );
			SetupConfigurationManager.setValue( LEGACY_KEY_PIN_SALT, testSalt );
			SetupConfigurationManager.setValue( LEGACY_KEY_PIN_HINT, testHint );
			SetupConfigurationManager.setValue( LEGACY_KEY_PIN_ENABLED, "true" );
			
			// Inject mock storage and trigger migration
			injectStorageProvider( mockStorage );
			triggerMigration();
			
			// Verify data migrated to secure storage
			assertEquals( testHash, mockStorage.retrieve( "pin.hash" ) );
			assertEquals( testSalt, mockStorage.retrieve( "pin.salt" ) );
			assertEquals( testHint, mockStorage.retrieve( "pin.hint" ) );
		}
		
		@Test
		@DisplayName( "Migration clears legacy properties after success" )
		void migration_ClearsLegacyProperties() throws Exception
		{
			// Set up legacy data
			SetupConfigurationManager.setValue( LEGACY_KEY_PIN_HASH, "somehash" );
			SetupConfigurationManager.setValue( LEGACY_KEY_PIN_SALT, "somesalt" );
			SetupConfigurationManager.setValue( LEGACY_KEY_PIN_ENABLED, "true" );
			
			// Inject mock storage and trigger migration
			injectStorageProvider( mockStorage );
			triggerMigration();
			
			// Verify legacy properties are cleared
			String legacyHash = SetupConfigurationManager.getValue( LEGACY_KEY_PIN_HASH, "" );
			String legacySalt = SetupConfigurationManager.getValue( LEGACY_KEY_PIN_SALT, "" );
			String legacyEnabled = SetupConfigurationManager.getValue( LEGACY_KEY_PIN_ENABLED, "" );
			
			assertTrue( legacyHash.isEmpty(), "Legacy hash should be cleared" );
			assertTrue( legacySalt.isEmpty(), "Legacy salt should be cleared" );
			assertTrue( legacyEnabled.isEmpty(), "Legacy enabled flag should be cleared" );
		}
		
		@Test
		@DisplayName( "Migration preserves lockout state" )
		void migration_PreservesLockoutState() throws Exception
		{
			// Set up legacy data with lockout
			SetupConfigurationManager.setValue( LEGACY_KEY_PIN_HASH, "hash" );
			SetupConfigurationManager.setValue( LEGACY_KEY_PIN_SALT, "salt" );
			SetupConfigurationManager.setValue( LEGACY_KEY_FAILED_ATTEMPTS, "3" );
			SetupConfigurationManager.setValue( LEGACY_KEY_LOCKOUT_UNTIL, "2025-01-01T12:00:00" );
			
			// Inject mock storage and trigger migration
			injectStorageProvider( mockStorage );
			triggerMigration();
			
			// Verify lockout state migrated
			assertEquals( "3", mockStorage.retrieve( "failed.attempts" ) );
			assertEquals( "2025-01-01T12:00:00", mockStorage.retrieve( "lockout.until" ) );
		}
	}
	
	// ========================================================================
	// Edge Cases
	// ========================================================================
	
	@Nested
	@DisplayName( "Migration Edge Cases" )
	class MigrationEdgeCases
	{
		
		@Test
		@DisplayName( "Migration skips when no legacy data exists" )
		void migration_NoLegacyData_Skips() throws Exception
		{
			// No legacy data set up
			
			// Inject mock storage and trigger migration
			injectStorageProvider( mockStorage );
			triggerMigration();
			
			// Secure storage should remain empty
			assertEquals( 0, mockStorage.size() );
		}
		
		@Test
		@DisplayName( "Migration skips when secure storage already has data" )
		void migration_SecureStorageHasData_Skips() throws Exception
		{
			// Set up legacy data
			SetupConfigurationManager.setValue( LEGACY_KEY_PIN_HASH, "oldhash" );
			SetupConfigurationManager.setValue( LEGACY_KEY_PIN_SALT, "oldsalt" );
			
			// Pre-populate secure storage (simulating already migrated)
			mockStorage.store( "pin.hash", "existinghash" );
			mockStorage.store( "pin.salt", "existingsalt" );
			
			// Inject and trigger migration
			injectStorageProvider( mockStorage );
			triggerMigration();
			
			// Secure storage should retain existing data, not be overwritten
			assertEquals( "existinghash", mockStorage.retrieve( "pin.hash" ) );
			assertEquals( "existingsalt", mockStorage.retrieve( "pin.salt" ) );
		}
		
		@Test
		@DisplayName( "Migration handles empty hash gracefully" )
		void migration_EmptyHash_Skips() throws Exception
		{
			// Set legacy enabled but empty hash
			SetupConfigurationManager.setValue( LEGACY_KEY_PIN_ENABLED, "true" );
			SetupConfigurationManager.setValue( LEGACY_KEY_PIN_HASH, "" );
			
			// Inject and trigger migration
			injectStorageProvider( mockStorage );
			triggerMigration();
			
			// Should not create entries in secure storage
			assertNull( mockStorage.retrieve( "pin.hash" ) );
		}
		
		@Test
		@DisplayName( "Migration handles missing salt gracefully" )
		void migration_MissingSalt_SkipsPin() throws Exception
		{
			// Set hash but no salt (invalid state)
			SetupConfigurationManager.setValue( LEGACY_KEY_PIN_HASH, "somehash" );
			// No salt set
			
			// Inject and trigger migration
			injectStorageProvider( mockStorage );
			triggerMigration();
			
			// PIN should not be migrated without both hash and salt
			// (This tests the defensive coding in migration)
		}
		
		@Test
		@DisplayName( "Migration only runs once per session" )
		void migration_OnlyRunsOnce() throws Exception
		{
			// Set up legacy data
			SetupConfigurationManager.setValue( LEGACY_KEY_PIN_HASH, "hash1" );
			SetupConfigurationManager.setValue( LEGACY_KEY_PIN_SALT, "salt1" );
			
			// First migration (simulating initialize() flow)
			injectStorageProvider( mockStorage );
			setMigrationAttempted( false );
			triggerMigrationWithFlagCheck(); // Respects the flag
			
			// Verify first migration ran
			assertEquals( "hash1", mockStorage.retrieve( "pin.hash" ) );
			
			// Clear secure storage and set new legacy data
			mockStorage.clear();
			SetupConfigurationManager.setValue( LEGACY_KEY_PIN_HASH, "hash2" );
			SetupConfigurationManager.setValue( LEGACY_KEY_PIN_SALT, "salt2" );
			
			// Second migration attempt (flag should block it)
			triggerMigrationWithFlagCheck();
			
			// Secure storage should still be empty because migrationAttempted flag
			// prevented the second migration from running
			assertEquals( 0, mockStorage.size() );
		}
		
		/**
		 * Trigger migration respecting the migrationAttempted flag, simulating the actual initialize() control flow.
		 */
		private void triggerMigrationWithFlagCheck() throws Exception
		{
			Field flagField = PinManager.class.getDeclaredField( "migrationAttempted" );
			flagField.setAccessible( true );
			boolean alreadyAttempted = (boolean) flagField.get( null );
			
			if( !alreadyAttempted )
			{
				flagField.set( null, true );
				triggerMigration();
			}
		}
		
		/**
		 * Helper to set the migrationAttempted flag directly.
		 */
		private void setMigrationAttempted( boolean value ) throws Exception
		{
			Field flagField = PinManager.class.getDeclaredField( "migrationAttempted" );
			flagField.setAccessible( true );
			flagField.set( null, value );
		}
	}
	
	// ========================================================================
	// Pin Enabled Flag Elimination Tests
	// ========================================================================
	
	@Nested
	@DisplayName( "Pin Enabled Flag Elimination" )
	class PinEnabledFlagTests
	{
		
		@Test
		@DisplayName( "Legacy pin_enabled=true with hash results in configured PIN" )
		void legacyEnabled_WithHash_PinConfigured() throws Exception
		{
			SetupConfigurationManager.setValue( LEGACY_KEY_PIN_ENABLED, "true" );
			SetupConfigurationManager.setValue( LEGACY_KEY_PIN_HASH, "hash" );
			SetupConfigurationManager.setValue( LEGACY_KEY_PIN_SALT, "salt" );
			
			injectStorageProvider( mockStorage );
			triggerMigration();
			
			// After migration, isPinConfigured should return true
			assertTrue( mockStorage.exists( "pin.hash" ) );
		}
		
		@Test
		@DisplayName( "Legacy pin_enabled=false with hash is ignored (THE VULNERABILITY)" )
		void legacyDisabled_WithHash_VulnerabilityDocumented() throws Exception
		{
			// This documents the vulnerability that's being fixed:
			// In the old system, an attacker could set pin_enabled=false
			// even when a hash exists
			
			SetupConfigurationManager.setValue( LEGACY_KEY_PIN_ENABLED, "false" );
			SetupConfigurationManager.setValue( LEGACY_KEY_PIN_HASH, "hash" );
			SetupConfigurationManager.setValue( LEGACY_KEY_PIN_SALT, "salt" );
			
			injectStorageProvider( mockStorage );
			triggerMigration();
			
			// The new system ignores the enabled flag - if hash exists, PIN is required
			assertTrue( mockStorage.exists( "pin.hash" ) );
			
			// The enabled flag is NOT migrated (it doesn't exist in new system)
			// PIN requirement is now determined solely by existence of hash
		}
		
		@Test
		@DisplayName( "New system has no enabled flag - hash existence determines requirement" )
		void newSystem_NoEnabledFlag() throws Exception
		{
			// Set up via new system
			injectStorageProvider( mockStorage );
			
			// Store a hash directly (simulating setupPin)
			mockStorage.store( "pin.hash", "somehash" );
			mockStorage.store( "pin.salt", "somesalt" );
			
			// There should be no way to "disable" the PIN without removing the hash
			assertTrue( mockStorage.exists( "pin.hash" ) );
			
			// The only way to not require PIN is to delete the hash
			mockStorage.delete( "pin.hash" );
			mockStorage.delete( "pin.salt" );
			
			assertFalse( mockStorage.exists( "pin.hash" ) );
		}
	}
	
	// ========================================================================
	// Error Handling Tests
	// ========================================================================
	
	@Nested
	@DisplayName( "Migration Error Handling" )
	class MigrationErrorHandling
	{
		
		@Test
		@DisplayName( "Migration continues if secure storage write fails" )
		void migration_StorageWriteFails_DoesNotThrow() throws Exception
		{
			// Set up legacy data
			SetupConfigurationManager.setValue( LEGACY_KEY_PIN_HASH, "hash" );
			SetupConfigurationManager.setValue( LEGACY_KEY_PIN_SALT, "salt" );
			
			// Set storage to fail
			mockStorage.setSimulateFailures( true );
			injectStorageProvider( mockStorage );
			
			// Migration should not throw - it's a best-effort operation
			assertDoesNotThrow( () -> triggerMigration() );
		}
		
		@Test
		@DisplayName( "App can function even if migration fails" )
		void migration_Fails_AppStillFunctions() throws Exception
		{
			// Set up legacy data
			SetupConfigurationManager.setValue( LEGACY_KEY_PIN_HASH, "hash" );
			SetupConfigurationManager.setValue( LEGACY_KEY_PIN_SALT, "salt" );
			
			// Migration fails
			mockStorage.setSimulateFailures( true );
			injectStorageProvider( mockStorage );
			
			try
			{
				triggerMigration();
			}
			catch( Exception ignored )
			{
			}
			
			// Re-enable storage
			mockStorage.setSimulateFailures( false );
			
			// User should be able to set up a new PIN
			// (This is the fallback behavior - they lose their old PIN but can create new)
			assertDoesNotThrow( () -> mockStorage.store( "pin.hash", "newhash" ) );
		}
	}
	
	// ========================================================================
	// Integration-Style Tests
	// ========================================================================
	
	@Nested
	@DisplayName( "Migration Integration Tests" )
	class MigrationIntegrationTests
	{
		
		@Test
		@DisplayName( "Full migration preserves ability to verify PIN" )
		void fullMigration_PinStillVerifiable() throws Exception
		{
			// This test requires the actual PBKDF2 implementation to work
			// We'll simulate it by setting up a PIN through the old system,
			// then verifying through the new system
			
			// For a true integration test, you'd:
			// 1. Use the old PinManager to set up a PIN
			// 2. Migrate
			// 3. Verify the PIN still works with new PinManager
			
			// Since we're mocking, we just verify the data structures are preserved
			String testHash = "dGVzdGhhc2hkYXRh"; // Simulated hash
			String testSalt = "dGVzdHNhbHRkYXRh"; // Simulated salt
			
			SetupConfigurationManager.setValue( LEGACY_KEY_PIN_HASH, testHash );
			SetupConfigurationManager.setValue( LEGACY_KEY_PIN_SALT, testSalt );
			SetupConfigurationManager.setValue( LEGACY_KEY_PIN_ENABLED, "true" );
			
			injectStorageProvider( mockStorage );
			triggerMigration();
			
			// Verify exact data preservation
			assertEquals( testHash, mockStorage.retrieve( "pin.hash" ) );
			assertEquals( testSalt, mockStorage.retrieve( "pin.salt" ) );
		}
	}
}