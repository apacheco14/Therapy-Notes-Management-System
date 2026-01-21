package com.alexpacheco.therapynotes.controller;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import com.alexpacheco.therapynotes.controller.enums.LogLevel;
import com.alexpacheco.therapynotes.install.SetupConfigurationManager;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * Manages PIN security for application access control. Uses PBKDF2 with SHA-256 for secure password hashing.
 * 
 * Security features: - Salted hashing (unique per installation) - High iteration count (310,000 per OWASP 2023 guidelines) - Failed attempt
 * tracking with lockout - Timing-safe comparison
 */
public class PinManager
{
	// PBKDF2 Configuration (OWASP 2023 recommendations)
	private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
	private static final int ITERATIONS = 310000;
	private static final int KEY_LENGTH = 256;
	private static final int SALT_LENGTH = 32;
	
	// Lockout configuration
	private static final int MAX_FAILED_ATTEMPTS = 5;
	private static final int LOCKOUT_MINUTES = 15;
	
	// Configuration keys
	private static final String KEY_PIN_ENABLED = "security.pin.enabled";
	private static final String KEY_PIN_HASH = "security.pin.hash";
	private static final String KEY_PIN_SALT = "security.pin.salt";
	private static final String KEY_PIN_HINT = "security.pin.hint";
	private static final String KEY_FAILED_ATTEMPTS = "security.failed.attempts";
	private static final String KEY_LOCKOUT_UNTIL = "security.lockout.until";
	
	private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
	
	/**
	 * Check if PIN protection is enabled.
	 */
	public static boolean isPinEnabled()
	{
		String enabled = SetupConfigurationManager.getValue( KEY_PIN_ENABLED, "false" );
		return "true".equalsIgnoreCase( enabled );
	}
	
	/**
	 * Set up a new PIN. Generates a new salt and stores the hash.
	 * 
	 * @param pin  The PIN to set (will be cleared after hashing)
	 * @param hint Optional hint for PIN recovery (can be null)
	 * @throws SecurityException if hashing fails
	 */
	public static void setupPin( char[] pin, String hint )
	{
		try
		{
			// Generate a unique salt for this installation
			byte[] salt = generateSalt();
			
			// Hash the PIN
			byte[] hash = hashPin( pin, salt );
			
			// Clear the PIN from memory
			clearCharArray( pin );
			
			// Store as Base64
			String saltBase64 = Base64.getEncoder().encodeToString( salt );
			String hashBase64 = Base64.getEncoder().encodeToString( hash );
			
			// Save to configuration
			SetupConfigurationManager.setValue( KEY_PIN_ENABLED, "true" );
			SetupConfigurationManager.setValue( KEY_PIN_SALT, saltBase64 );
			SetupConfigurationManager.setValue( KEY_PIN_HASH, hashBase64 );
			
			if( hint != null && !hint.trim().isEmpty() )
			{
				SetupConfigurationManager.setValue( KEY_PIN_HINT, hint.trim() );
			}
			
			// Reset any failed attempts
			resetFailedAttempts();
			
		}
		catch( Exception e )
		{
			throw new SecurityException( "Failed to set up PIN: " + e.getMessage(), e );
		}
	}
	
	/**
	 * Verify a PIN against the stored hash. Tracks failed attempts and enforces lockout.
	 * 
	 * @param pin The PIN to verify (will be cleared after verification)
	 * @return VerificationResult containing success status and any messages
	 */
	public static VerificationResult verifyPin( char[] pin )
	{
		try
		{
			// Check for lockout
			if( isLockedOut() )
			{
				LocalDateTime lockoutUntil = getLockoutUntil();
				long minutesRemaining = java.time.Duration.between( LocalDateTime.now(), lockoutUntil ).toMinutes() + 1;
				
				clearCharArray( pin );
				return new VerificationResult( false,
						"Too many failed attempts. Please wait " + minutesRemaining + " minute(s) before trying again.", true );
			}
			
			// Get stored values
			String saltBase64 = SetupConfigurationManager.getValue( KEY_PIN_SALT );
			String storedHashBase64 = SetupConfigurationManager.getValue( KEY_PIN_HASH );
			
			if( saltBase64 == null || storedHashBase64 == null )
			{
				clearCharArray( pin );
				AppController.logToDatabase( LogLevel.WARN, "PinManager", "PIN verification attempted but PIN not configured" );
				return new VerificationResult( false, "PIN not configured.", false );
			}
			
			byte[] salt = Base64.getDecoder().decode( saltBase64 );
			byte[] storedHash = Base64.getDecoder().decode( storedHashBase64 );
			
			// Hash the provided PIN with the same salt
			byte[] providedHash = hashPin( pin, salt );
			
			// Clear PIN from memory
			clearCharArray( pin );
			
			// Timing-safe comparison
			boolean matches = constantTimeEquals( storedHash, providedHash );
			
			if( matches )
			{
				resetFailedAttempts();
				AppController.logToDatabase( LogLevel.INFO, "PinManager", "PIN verification passed" );
				return new VerificationResult( true, null, false );
			}
			else
			{
				int attempts = incrementFailedAttempts();
				int remaining = MAX_FAILED_ATTEMPTS - attempts;
				
				if( remaining <= 0 )
				{
					setLockout();
					AppController.logToDatabase( LogLevel.WARN, "PinManager", "Incorrect PIN. Account locked." );
					return new VerificationResult( false, "Too many failed attempts. Account locked for " + LOCKOUT_MINUTES + " minutes.",
							true );
				}
				else
				{
					AppController.logToDatabase( LogLevel.WARN, "PinManager", "Incorrect PIN. " + remaining + " attempt(s) remaining." );
					return new VerificationResult( false, "Incorrect PIN. " + remaining + " attempt(s) remaining.", false );
				}
			}
			
		}
		catch( Exception e )
		{
			clearCharArray( pin );
			AppController.logException( "PinManager", e );
			return new VerificationResult( false, "Verification error: " + e.getMessage(), false );
		}
	}
	
	/**
	 * Change the PIN. Requires verification of current PIN.
	 * 
	 * @param currentPin Current PIN for verification
	 * @param newPin     New PIN to set
	 * @param newHint    New hint (can be null to keep existing)
	 * @return true if PIN was changed successfully
	 */
	public static boolean changePin( char[] currentPin, char[] newPin, String newHint )
	{
		VerificationResult result = verifyPin( currentPin );
		
		if( !result.isSuccess() )
		{
			clearCharArray( newPin );
			return false;
		}
		
		setupPin( newPin, newHint );
		return true;
	}
	
	/**
	 * Remove PIN protection (requires verification).
	 * 
	 * @param currentPin Current PIN for verification
	 * @return true if PIN was removed
	 */
	public static boolean removePin( char[] currentPin )
	{
		VerificationResult result = verifyPin( currentPin );
		
		if( !result.isSuccess() )
		{
			return false;
		}
		
		try
		{
			SetupConfigurationManager.setValue( KEY_PIN_ENABLED, "false" );
			SetupConfigurationManager.setValue( KEY_PIN_HASH, "" );
			SetupConfigurationManager.setValue( KEY_PIN_SALT, "" );
			SetupConfigurationManager.setValue( KEY_PIN_HINT, "" );
			resetFailedAttempts();
			return true;
		}
		catch( Exception e )
		{
			return false;
		}
	}
	
	/**
	 * Get the PIN hint if one was set.
	 */
	public static String getPinHint()
	{
		return SetupConfigurationManager.getValue( KEY_PIN_HINT, "" );
	}
	
	/**
	 * Check if account is currently locked out.
	 */
	public static boolean isLockedOut()
	{
		LocalDateTime lockoutUntil = getLockoutUntil();
		return lockoutUntil != null && LocalDateTime.now().isBefore( lockoutUntil );
	}
	
	/**
	 * Get remaining lockout time in minutes, or 0 if not locked.
	 */
	public static long getLockoutMinutesRemaining()
	{
		LocalDateTime lockoutUntil = getLockoutUntil();
		if( lockoutUntil == null || LocalDateTime.now().isAfter( lockoutUntil ) )
		{
			return 0;
		}
		return java.time.Duration.between( LocalDateTime.now(), lockoutUntil ).toMinutes() + 1;
	}
	
	/**
	 * Evaluate PIN strength (for UI feedback during setup).
	 * 
	 * @param pin The PIN to evaluate
	 * @return PinStrength enum value
	 */
	public static PinStrength evaluateStrength( char[] pin )
	{
		if( pin == null || pin.length == 0 )
		{
			return PinStrength.NONE;
		}
		
		int length = pin.length;
		boolean hasDigits = false;
		boolean hasLetters = false;
		boolean hasSpecial = false;
		boolean hasSequential = false;
		boolean hasRepeating = false;
		
		for( int i = 0; i < length; i++ )
		{
			char c = pin[i];
			if( Character.isDigit( c ) )
				hasDigits = true;
			else if( Character.isLetter( c ) )
				hasLetters = true;
			else
				hasSpecial = true;
			
			// Check for sequential (123, abc)
			if( i > 0 && pin[i] == pin[i - 1] + 1 )
				hasSequential = true;
			
			// Check for repeating (111, aaa)
			if( i > 0 && pin[i] == pin[i - 1] )
				hasRepeating = true;
		}
		
		// Common weak PINs
		String pinStr = new String( pin );
		if( isCommonPin( pinStr ) )
		{
			return PinStrength.WEAK;
		}
		
		// Scoring
		int score = 0;
		
		// Length scoring
		if( length >= 4 )
			score += 1;
		if( length >= 6 )
			score += 1;
		if( length >= 8 )
			score += 1;
		
		// Complexity scoring
		if( hasDigits && hasLetters )
			score += 1;
		if( hasSpecial )
			score += 1;
		
		// Penalties
		if( hasSequential )
			score -= 1;
		if( hasRepeating )
			score -= 1;
		
		if( score <= 1 )
			return PinStrength.WEAK;
		if( score <= 2 )
			return PinStrength.FAIR;
		if( score <= 3 )
			return PinStrength.GOOD;
		return PinStrength.STRONG;
	}
	
	// Private helper methods
	
	private static byte[] generateSalt()
	{
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[SALT_LENGTH];
		random.nextBytes( salt );
		return salt;
	}
	
	private static byte[] hashPin( char[] pin, byte[] salt ) throws NoSuchAlgorithmException, InvalidKeySpecException
	{
		PBEKeySpec spec = new PBEKeySpec( pin, salt, ITERATIONS, KEY_LENGTH );
		try
		{
			SecretKeyFactory factory = SecretKeyFactory.getInstance( ALGORITHM );
			return factory.generateSecret( spec ).getEncoded();
		}
		finally
		{
			spec.clearPassword();
		}
	}
	
	private static boolean constantTimeEquals( byte[] a, byte[] b )
	{
		if( a.length != b.length )
		{
			return false;
		}
		
		int result = 0;
		for( int i = 0; i < a.length; i++ )
		{
			result |= a[i] ^ b[i];
		}
		return result == 0;
	}
	
	private static void clearCharArray( char[] array )
	{
		if( array != null )
		{
			java.util.Arrays.fill( array, '\0' );
		}
	}
	
	private static boolean isCommonPin( String pin )
	{
		String[] commonPins = { "0000", "1111", "2222", "3333", "4444", "5555", "6666", "7777", "8888", "9999", "1234", "4321", "1212",
				"2121", "0123", "3210", "1357", "2468", "0000", "1234", "password", "pass", "admin", "login" };
		
		String lowerPin = pin.toLowerCase();
		for( String common : commonPins )
		{
			if( lowerPin.equals( common ) )
			{
				return true;
			}
		}
		return false;
	}
	
	private static int getFailedAttempts()
	{
		String attempts = SetupConfigurationManager.getValue( KEY_FAILED_ATTEMPTS, "0" );
		try
		{
			return Integer.parseInt( attempts );
		}
		catch( NumberFormatException e )
		{
			return 0;
		}
	}
	
	private static int incrementFailedAttempts()
	{
		int attempts = getFailedAttempts() + 1;
		try
		{
			SetupConfigurationManager.setValue( KEY_FAILED_ATTEMPTS, String.valueOf( attempts ) );
		}
		catch( Exception e )
		{
			AppController.logException( "PinManager", e );
		}
		return attempts;
	}
	
	private static void resetFailedAttempts()
	{
		try
		{
			SetupConfigurationManager.setValue( KEY_FAILED_ATTEMPTS, "0" );
			SetupConfigurationManager.setValue( KEY_LOCKOUT_UNTIL, "" );
		}
		catch( Exception e )
		{
			AppController.logException( "PinManager", e );
		}
	}
	
	private static void setLockout()
	{
		LocalDateTime lockoutUntil = LocalDateTime.now().plusMinutes( LOCKOUT_MINUTES );
		try
		{
			SetupConfigurationManager.setValue( KEY_LOCKOUT_UNTIL, lockoutUntil.format( DATETIME_FORMAT ) );
		}
		catch( Exception e )
		{
			AppController.logException( "PinManager", e );
		}
	}
	
	private static LocalDateTime getLockoutUntil()
	{
		String lockoutStr = SetupConfigurationManager.getValue( KEY_LOCKOUT_UNTIL, "" );
		if( lockoutStr == null || lockoutStr.isEmpty() )
		{
			return null;
		}
		try
		{
			return LocalDateTime.parse( lockoutStr, DATETIME_FORMAT );
		}
		catch( Exception e )
		{
			AppController.logException( "PinManager", e );
			return null;
		}
	}
	
	/**
	 * PIN strength levels for UI feedback.
	 */
	public enum PinStrength
	{
		NONE( "", java.awt.Color.GRAY ),
		WEAK( "Weak", new java.awt.Color( 244, 67, 54 ) ),
		FAIR( "Fair", new java.awt.Color( 255, 152, 0 ) ),
		GOOD( "Good", new java.awt.Color( 139, 195, 74 ) ),
		STRONG( "Strong", new java.awt.Color( 76, 175, 80 ) );
		
		private final String label;
		private final java.awt.Color color;
		
		PinStrength( String label, java.awt.Color color )
		{
			this.label = label;
			this.color = color;
		}
		
		public String getLabel()
		{
			return label;
		}
		
		public java.awt.Color getColor()
		{
			return color;
		}
	}
	
	/**
	 * Result of PIN verification attempt.
	 */
	public static class VerificationResult
	{
		private final boolean success;
		private final String message;
		private final boolean lockedOut;
		
		public VerificationResult( boolean success, String message, boolean lockedOut )
		{
			this.success = success;
			this.message = message;
			this.lockedOut = lockedOut;
		}
		
		public boolean isSuccess()
		{
			return success;
		}
		
		public String getMessage()
		{
			return message;
		}
		
		public boolean isLockedOut()
		{
			return lockedOut;
		}
	}
}