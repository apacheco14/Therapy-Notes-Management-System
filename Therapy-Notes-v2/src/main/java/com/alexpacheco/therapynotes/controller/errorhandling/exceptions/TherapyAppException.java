package com.alexpacheco.therapynotes.controller.errorhandling.exceptions;

import com.alexpacheco.therapynotes.controller.enums.ErrorCode;
import com.alexpacheco.therapynotes.util.AppLogger;

/**
 * Base exception for all business-related errors in the application.
 */
public class TherapyAppException extends Exception
{
	private static final long serialVersionUID = 5378738738318081859L;
	private final ErrorCode errorCode;
	
	public TherapyAppException( String message, ErrorCode errorCode )
	{
		super( message );
		this.errorCode = errorCode;
		AppLogger.error( message );
	}
	
	public ErrorCode getErrorCode()
	{
		return errorCode;
	}
}