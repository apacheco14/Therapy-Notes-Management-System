package com.alexpacheco.therapynotes.controller.exceptions;

import com.alexpacheco.therapynotes.controller.enums.ErrorCode;

/**
 * Specifically for data integrity and constraint violations.
 */
public class ResourceConflictException extends TherapyAppException
{
	private static final long serialVersionUID = 7369941491000222569L;

	public ResourceConflictException(String message)
	{
		super(message, ErrorCode.CONFLICT);
	}
}