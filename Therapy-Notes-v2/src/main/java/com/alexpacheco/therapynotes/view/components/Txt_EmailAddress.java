package main.java.com.alexpacheco.therapynotes.view.components;

import main.java.com.alexpacheco.therapynotes.util.validators.EmailValidator;

public class Txt_EmailAddress extends ValidatedTextField
{
	private static final long serialVersionUID = 648382937520984223L;
	
	public Txt_EmailAddress()
	{
		super(50, EmailValidator::isValidEmailAddress, "Enter a valid email address");
	}
}
