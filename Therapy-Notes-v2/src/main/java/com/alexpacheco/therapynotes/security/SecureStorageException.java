package com.alexpacheco.therapynotes.security;

public class SecureStorageException extends Exception
{
	private static final long serialVersionUID = 381305815632905462L;
	
	public SecureStorageException( String message )
	{
		super( message );
	}
	
	public SecureStorageException( String message, Throwable cause )
	{
		super( message, cause );
	}
}