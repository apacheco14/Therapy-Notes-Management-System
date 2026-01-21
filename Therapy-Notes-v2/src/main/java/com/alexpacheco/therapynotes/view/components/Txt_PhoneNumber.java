package com.alexpacheco.therapynotes.view.components;

import com.alexpacheco.therapynotes.util.validators.PhoneNumberValidator;

public class Txt_PhoneNumber extends ValidatedTextField
{
	private static final long serialVersionUID = 2716773724821246058L;

	public Txt_PhoneNumber()
	{
		super(20, PhoneNumberValidator::isValidUSPhone, "Enter a valid 10-digit phone number");
	}
	
}
