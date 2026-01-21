package main.java.com.alexpacheco.therapynotes.controller.errorhandling.exceptions;

import main.java.com.alexpacheco.therapynotes.controller.AppController;
import main.java.com.alexpacheco.therapynotes.controller.enums.ErrorCode;
import main.java.com.alexpacheco.therapynotes.controller.enums.LogLevel;

/**
 * Base exception for all business-related errors in the application.
 */
public class TherapyAppException extends Exception
{
	private static final long serialVersionUID = 5378738738318081859L;
	private final ErrorCode errorCode;
	
	public TherapyAppException(String message, ErrorCode errorCode)
	{
		super(message);
		this.errorCode = errorCode;
		AppController.logToDatabase(LogLevel.ERROR, "TherapyAppException", message);
	}
	
	public ErrorCode getErrorCode()
	{
		return errorCode;
	}
}