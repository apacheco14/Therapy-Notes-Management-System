package com.alexpacheco.therapynotes.security;

/**
 * Platform-agnostic interface for secure credential storage. Implementations use OS-native secure storage mechanisms.
 */
public interface SecureStorageProvider
{
	
	void store( String key, String value ) throws SecureStorageException;
	
	String retrieve( String key ) throws SecureStorageException;
	
	void delete( String key ) throws SecureStorageException;
	
	boolean exists( String key );
	
	boolean isAvailable();
}