package com.alexpacheco.therapynotes.view.components;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.alexpacheco.therapynotes.util.validators.Validator;

import java.awt.*;
import java.util.function.Consumer;

public class ValidatedTextField extends JTextField
{
	private static final long serialVersionUID = -8873779094292369942L;
	private final Validator validator;
	private final String errorMessage;
	private Consumer<Boolean> validityListener;
	
	private final Border defaultBorder;
	private final Border errorBorder;
	
	public ValidatedTextField( int columns, Validator validator, String errorMessage )
	{
		super( null, null, columns );
		this.validator = validator;
		this.errorMessage = errorMessage;
		this.defaultBorder = getBorder();
		this.errorBorder = BorderFactory.createCompoundBorder( BorderFactory.createLineBorder( Color.RED ), defaultBorder );
		
		getDocument().addDocumentListener( new DocumentListener()
		{
			@Override
			public void insertUpdate( DocumentEvent e )
			{
				validateField();
			}
			
			@Override
			public void removeUpdate( DocumentEvent e )
			{
				validateField();
			}
			
			@Override
			public void changedUpdate( DocumentEvent e )
			{
				validateField();
			}
		} );
	}
	
	private void validateField()
	{
		String text = getText();
		boolean valid = validator.isValid( text );
		
		setBorder( valid || text.isBlank() ? defaultBorder : errorBorder );
		setToolTipText( valid || text.isBlank() ? null : errorMessage );
		
		if( validityListener != null )
		{
			validityListener.accept( valid );
		}
	}
	
	/** Allows external components (e.g., buttons) to react to validity */
	public void setValidityListener( Consumer<Boolean> listener )
	{
		this.validityListener = listener;
	}
	
	/** Explicit validity check (useful on form submit) */
	public boolean isInputValid()
	{
		return validator.isValid( getText() );
	}
}
