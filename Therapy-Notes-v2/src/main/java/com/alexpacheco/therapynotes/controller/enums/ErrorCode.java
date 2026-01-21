package com.alexpacheco.therapynotes.controller.enums;

public enum ErrorCode
{
	REQ_MISSING("Required Element Missing", 98),
	DATE_PARSE("Date Parse Error", 99),
	NOT_FOUND("Not Found", 404), // 404 is the HTTP status for Not Found
	CONFLICT("Database Conflict", 409), // 409 is the HTTP status for Conflict
	DB_ERROR("Database Error", 500);
	
	private String name;
	private int number;
	
	ErrorCode(String name, int number)
	{
		this.name = name;
		this.number = number;
	}

	public String getName()
	{
		return name;
	}

	public int getNumber()
	{
		return number;
	}
}
