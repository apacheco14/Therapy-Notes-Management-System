package main.java.com.alexpacheco.therapynotes.controller.errorhandling.exceptions;

import main.java.com.alexpacheco.therapynotes.controller.enums.ErrorCode;

/**
 * Specifically for when a requested record is not found.
 */
public class ResourceNotFoundException extends TherapyAppException
{
	private static final long serialVersionUID = 7402294919339070941L;
	
	public ResourceNotFoundException(String message)
	{
		super(message, ErrorCode.NOT_FOUND);
	}
}
