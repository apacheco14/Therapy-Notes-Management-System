package main.java.com.alexpacheco.therapynotes.util.validators;

import main.java.com.alexpacheco.therapynotes.util.JavaUtils;

public class EmailValidator
{
	public static boolean isValidEmailAddress(String input)
	{
		if (JavaUtils.isNullOrEmpty(input))
		{
			return true;
		}
		
		return input.matches("^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$");
	}
}
