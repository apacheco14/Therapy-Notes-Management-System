package com.alexpacheco.therapynotes.controller.errorhandling;

import java.time.LocalDateTime;

public class ErrorDetails
{
	private LocalDateTime timestamp;
	private String message;
	private int code;
	
	public ErrorDetails(String message, int code)
	{
		this.timestamp = LocalDateTime.now();
		this.message = message;
		this.code = code;
	}
	
	// Getters
	public LocalDateTime getTimestamp()
	{
		return timestamp;
	}
	
	public String getMessage()
	{
		return message;
	}
	
	public int getCode()
	{
		return code;
	}
}