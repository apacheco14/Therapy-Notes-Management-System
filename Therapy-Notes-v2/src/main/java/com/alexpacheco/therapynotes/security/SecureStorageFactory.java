package com.alexpacheco.therapynotes.security;

import com.alexpacheco.therapynotes.util.AppLogger;

/**
 * Factory for creating the appropriate SecureStorageProvider. Tests provider availability and provides fallback strategy.
 */
public class SecureStorageFactory
{
	private static SecureStorageProvider instance;
	private static SecureStorageException initializationError;
	
	/**
	 * Get or create the secure storage provider singleton.
	 * 
	 * @throws SecureStorageException if no secure storage is available
	 */
	public static synchronized SecureStorageProvider getInstance() throws SecureStorageException
	{
		if( instance != null )
		{
			return instance;
		}
		
		if( initializationError != null )
		{
			throw initializationError;
		}
		
		try
		{
			KeyringStorageProvider provider = new KeyringStorageProvider();
			
			// Verify it actually works with a test write/read/delete
			if( testProvider( provider ) )
			{
				instance = provider;
				AppLogger.info( "Secure storage initialized using OS credential manager" );
				return instance;
			}
			else
			{
				throw new SecureStorageException( "Secure storage connectivity test failed" );
			}
			
		}
		catch( SecureStorageException e )
		{
			initializationError = e;
			AppLogger.error( "Failed to initialize secure storage: " + e.getMessage(), e );
			throw e;
		}
	}
	
	/**
	 * Check if secure storage is available without throwing.
	 */
	public static boolean isAvailable()
	{
		try
		{
			getInstance();
			return true;
		}
		catch( SecureStorageException e )
		{
			return false;
		}
	}
	
	private static boolean testProvider( SecureStorageProvider provider )
	{
		String testKey = "connectivity.test";
		String testValue = java.util.UUID.randomUUID().toString();
		
		try
		{
			provider.store( testKey, testValue );
			String retrieved = provider.retrieve( testKey );
			provider.delete( testKey );
			
			return testValue.equals( retrieved );
		}
		catch( Exception e )
		{
			AppLogger.warning( "Secure storage test failed: " + e.getMessage() );
			return false;
		}
	}
	
	/**
	 * Reset for testing purposes only.
	 */
	static void resetForTesting()
	{
		instance = null;
		initializationError = null;
	}
}