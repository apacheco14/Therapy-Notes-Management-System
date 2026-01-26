package com.alexpacheco.therapynotes.security;

import com.github.javakeyring.Keyring;
import com.github.javakeyring.PasswordAccessException;
import com.github.javakeyring.BackendNotSupportedException;

/**
 * SecureStorageProvider implementation using OS-native credential storage. - Windows: Credential Manager (DPAPI encryption) - macOS:
 * Keychain - Linux: Secret Service (GNOME Keyring / KWallet)
 */
public class KeyringStorageProvider implements SecureStorageProvider
{
	
	private static final String SERVICE_NAME = "TherapyNotes";
	
	private final Keyring keyring;
	
	public KeyringStorageProvider() throws SecureStorageException
	{
		try
		{
			this.keyring = Keyring.create();
		}
		catch( BackendNotSupportedException e )
		{
			throw new SecureStorageException( "Secure storage not available on this system: " + e.getMessage(), e );
		}
	}
	
	@Override
	public boolean isAvailable()
	{
		return keyring != null;
	}
	
	@Override
	public void store( String key, String value ) throws SecureStorageException
	{
		try
		{
			// java-keyring doesn't have an "update" method, so delete first if exists
			try
			{
				keyring.deletePassword( SERVICE_NAME, key );
			}
			catch( PasswordAccessException ignored )
			{
				// Key didn't exist, that's fine
			}
			
			keyring.setPassword( SERVICE_NAME, key, value );
		}
		catch( PasswordAccessException e )
		{
			throw new SecureStorageException( "Failed to store credential: " + e.getMessage(), e );
		}
	}
	
	@Override
	public String retrieve( String key ) throws SecureStorageException
	{
		try
		{
			return keyring.getPassword( SERVICE_NAME, key );
		}
		catch( PasswordAccessException e )
		{
			// Key not found returns null, not exception
			return null;
		}
	}
	
	@Override
	public void delete( String key ) throws SecureStorageException
	{
		try
		{
			keyring.deletePassword( SERVICE_NAME, key );
		}
		catch( PasswordAccessException e )
		{
			// Ignore if not found - delete is idempotent
		}
	}
	
	@Override
	public boolean exists( String key )
	{
		try
		{
			return keyring.getPassword( SERVICE_NAME, key ) != null;
		}
		catch( PasswordAccessException e )
		{
			return false;
		}
	}
}