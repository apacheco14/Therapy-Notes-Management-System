package com.alexpacheco.therapynotes.util.validators;

public class PhoneNumberValidator
{
	public static boolean isValidUSPhone( String input )
	{
		if( input == null || input.isEmpty() )
		{
			return true;
		}
		
		String digits = input.replaceAll( "\\D", "" );
		return digits.length() == 10 || ( digits.length() == 11 && digits.startsWith( "1" ) );
	}
}
