package com.alexpacheco.therapynotes.security;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * In-memory implementation of SecureStorageProvider for testing purposes. This implementation stores credentials in a ConcurrentHashMap and
 * does not persist data between test runs.
 * 
 * NOT FOR PRODUCTION USE - provides no actual security.
 */
public class InMemoryStorageProvider implements SecureStorageProvider
{
	
	private final Map<String, String> storage = new ConcurrentHashMap<>();
	private boolean available = true;
	private boolean simulateFailures = false;
	
	@Override
	public boolean isAvailable()
	{
		return available;
	}
	
	/**
	 * Set availability for testing failure scenarios.
	 */
	public void setAvailable( boolean available )
	{
		this.available = available;
	}
	
	/**
	 * Enable/disable simulated failures for testing error handling.
	 */
	public void setSimulateFailures( boolean simulateFailures )
	{
		this.simulateFailures = simulateFailures;
	}
	
	@Override
	public void store( String key, String value ) throws SecureStorageException
	{
		if( simulateFailures )
		{
			throw new SecureStorageException( "Simulated storage failure" );
		}
		if( key == null )
		{
			throw new SecureStorageException( "Key cannot be null" );
		}
		if( value == null )
		{
			storage.remove( key );
		}
		else
		{
			storage.put( key, value );
		}
	}
	
	@Override
	public String retrieve( String key ) throws SecureStorageException
	{
		if( simulateFailures )
		{
			throw new SecureStorageException( "Simulated retrieval failure" );
		}
		if( key == null )
		{
			throw new SecureStorageException( "Key cannot be null" );
		}
		return storage.get( key );
	}
	
	@Override
	public void delete( String key ) throws SecureStorageException
	{
		if( simulateFailures )
		{
			throw new SecureStorageException( "Simulated deletion failure" );
		}
		if( key == null )
		{
			throw new SecureStorageException( "Key cannot be null" );
		}
		storage.remove( key );
	}
	
	@Override
	public boolean exists( String key )
	{
		if( key == null )
		{
			return false;
		}
		return storage.containsKey( key );
	}
	
	/**
	 * Clear all stored data. Useful for test cleanup.
	 */
	public void clear()
	{
		storage.clear();
	}
	
	/**
	 * Get the number of stored entries. Useful for test assertions.
	 */
	public int size()
	{
		return storage.size();
	}
}