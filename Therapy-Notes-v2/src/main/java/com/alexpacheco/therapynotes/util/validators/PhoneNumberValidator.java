package main.java.com.alexpacheco.therapynotes.util.validators;

import main.java.com.alexpacheco.therapynotes.util.JavaUtils;

public class PhoneNumberValidator
{
	public static boolean isValidUSPhone(String input)
	{
		if (JavaUtils.isNullOrEmpty(input))
		{
			return true;
		}
		
		String digits = input.replaceAll("\\D", "");
		return digits.length() == 10 || (digits.length() == 11 && digits.startsWith("1"));
	}
}
