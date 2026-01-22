package com.alexpacheco.therapynotes.controller.exceptions;

import com.alexpacheco.therapynotes.controller.enums.ErrorCode;

public class MissingRequiredElementException extends TherapyAppException
{
	private static final long serialVersionUID = 3093684981540161448L;

	public MissingRequiredElementException(String message)
	{
		super(message, ErrorCode.REQ_MISSING);
	}
	
}
