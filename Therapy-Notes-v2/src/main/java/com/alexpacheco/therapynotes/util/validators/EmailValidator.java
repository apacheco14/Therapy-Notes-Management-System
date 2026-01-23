package com.alexpacheco.therapynotes.util.validators;

public class EmailValidator
{
	public static boolean isValidEmailAddress( String input )
	{
		if( input == null || input.isEmpty() )
		{
			return true;
		}
		
		return input.matches( "^[\\w]([\\w.-]*[\\w])?@[\\w]([\\w.-]*[\\w])?\\.[A-Za-z]{2,}$" );
	}
}
