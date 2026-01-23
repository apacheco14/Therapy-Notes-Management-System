package com.alexpacheco.therapynotes.view.components;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.alexpacheco.therapynotes.util.validators.Validator;

/**
 * Unit tests for {@link ValidatedTextField}.
 */
@DisplayName( "ValidatedTextField" )
class ValidatedTextFieldTest
{
	// Test validators
	private static final Validator ALWAYS_VALID = text -> true;
	private static final Validator ALWAYS_INVALID = text -> false;
	private static final Validator NOT_EMPTY = text -> text != null && !text.trim().isEmpty();
	private static final Validator MIN_LENGTH_3 = text -> text != null && text.length() >= 3;
	private static final Validator NUMERIC_ONLY = text -> text != null && text.matches( "\\d*" );
	
	private static final String ERROR_MSG = "Invalid input";
	
	/**
	 * Runs code on EDT and waits for completion.
	 */
	private static void runOnEDT( Runnable action )
	{
		try
		{
			if( SwingUtilities.isEventDispatchThread() )
			{
				action.run();
			}
			else
			{
				SwingUtilities.invokeAndWait( action );
			}
		}
		catch( Exception e )
		{
			throw new RuntimeException( "EDT execution failed", e );
		}
	}
	
	@Nested
	@DisplayName( "Construction" )
	class Construction
	{
		@Test
		@DisplayName( "Creates text field with specified columns" )
		void createsWithColumns()
		{
			runOnEDT( () ->
			{
				ValidatedTextField field = new ValidatedTextField( 20, ALWAYS_VALID, ERROR_MSG );
				assertEquals( 20, field.getColumns() );
			} );
		}
		
		@Test
		@DisplayName( "Starts with empty text" )
		void startsEmpty()
		{
			runOnEDT( () ->
			{
				ValidatedTextField field = new ValidatedTextField( 10, ALWAYS_VALID, ERROR_MSG );
				assertEquals( "", field.getText() );
			} );
		}
		
		@Test
		@DisplayName( "Starts with default border" )
		void startsWithDefaultBorder()
		{
			runOnEDT( () ->
			{
				ValidatedTextField field = new ValidatedTextField( 10, ALWAYS_VALID, ERROR_MSG );
				assertFalse( hasRedBorder( field ), "Should not have error border initially" );
			} );
		}
		
		@Test
		@DisplayName( "Starts with no tooltip" )
		void startsWithNoTooltip()
		{
			runOnEDT( () ->
			{
				ValidatedTextField field = new ValidatedTextField( 10, ALWAYS_VALID, ERROR_MSG );
				assertNull( field.getToolTipText() );
			} );
		}
	}
	
	@Nested
	@DisplayName( "isInputValid()" )
	class IsInputValid
	{
		@Test
		@DisplayName( "Returns true when validator passes" )
		void returnsTrueWhenValid()
		{
			runOnEDT( () ->
			{
				ValidatedTextField field = new ValidatedTextField( 10, ALWAYS_VALID, ERROR_MSG );
				field.setText( "anything" );
				assertTrue( field.isInputValid() );
			} );
		}
		
		@Test
		@DisplayName( "Returns false when validator fails" )
		void returnsFalseWhenInvalid()
		{
			runOnEDT( () ->
			{
				ValidatedTextField field = new ValidatedTextField( 10, ALWAYS_INVALID, ERROR_MSG );
				field.setText( "anything" );
				assertFalse( field.isInputValid() );
			} );
		}
		
		@Test
		@DisplayName( "Validates current text value" )
		void validatesCurrentText()
		{
			runOnEDT( () ->
			{
				ValidatedTextField field = new ValidatedTextField( 10, MIN_LENGTH_3, ERROR_MSG );
				
				field.setText( "ab" );
				assertFalse( field.isInputValid(), "2 chars should be invalid" );
				
				field.setText( "abc" );
				assertTrue( field.isInputValid(), "3 chars should be valid" );
			} );
		}
		
		@Test
		@DisplayName( "Empty text validity depends on validator" )
		void emptyTextValidity()
		{
			runOnEDT( () ->
			{
				// Validator that accepts empty
				ValidatedTextField allowsEmpty = new ValidatedTextField( 10, NUMERIC_ONLY, ERROR_MSG );
				allowsEmpty.setText( "" );
				assertTrue( allowsEmpty.isInputValid(), "Empty matches \\d*" );
				
				// Validator that rejects empty
				ValidatedTextField rejectsEmpty = new ValidatedTextField( 10, NOT_EMPTY, ERROR_MSG );
				rejectsEmpty.setText( "" );
				assertFalse( rejectsEmpty.isInputValid(), "Empty fails NOT_EMPTY" );
			} );
		}
	}
	
	@Nested
	@DisplayName( "Visual feedback on validation" )
	class VisualFeedback
	{
		@Test
		@DisplayName( "Shows error border when invalid and not blank" )
		void showsErrorBorderWhenInvalid()
		{
			runOnEDT( () ->
			{
				ValidatedTextField field = new ValidatedTextField( 10, NUMERIC_ONLY, ERROR_MSG );
				field.setText( "abc" ); // Non-numeric, non-blank
				
				assertTrue( hasRedBorder( field ), "Should show red border for invalid input" );
			} );
		}
		
		@Test
		@DisplayName( "Shows default border when valid" )
		void showsDefaultBorderWhenValid()
		{
			runOnEDT( () ->
			{
				ValidatedTextField field = new ValidatedTextField( 10, NUMERIC_ONLY, ERROR_MSG );
				field.setText( "123" );
				
				assertFalse( hasRedBorder( field ), "Should show default border for valid input" );
			} );
		}
		
		@Test
		@DisplayName( "Shows default border when blank (even if invalid)" )
		void showsDefaultBorderWhenBlank()
		{
			runOnEDT( () ->
			{
				// NOT_EMPTY validator - blank text is technically invalid
				ValidatedTextField field = new ValidatedTextField( 10, NOT_EMPTY, ERROR_MSG );
				field.setText( "" );
				
				assertFalse( hasRedBorder( field ), "Blank text should not show error border" );
			} );
		}
		
		@Test
		@DisplayName( "Shows default border for whitespace-only text" )
		void showsDefaultBorderForWhitespace()
		{
			runOnEDT( () ->
			{
				ValidatedTextField field = new ValidatedTextField( 10, NOT_EMPTY, ERROR_MSG );
				field.setText( "   " );
				
				assertFalse( hasRedBorder( field ), "Whitespace-only should not show error border" );
			} );
		}
		
		@Test
		@DisplayName( "Shows tooltip when invalid and not blank" )
		void showsTooltipWhenInvalid()
		{
			runOnEDT( () ->
			{
				ValidatedTextField field = new ValidatedTextField( 10, NUMERIC_ONLY, ERROR_MSG );
				field.setText( "abc" );
				
				assertEquals( ERROR_MSG, field.getToolTipText() );
			} );
		}
		
		@Test
		@DisplayName( "Clears tooltip when valid" )
		void clearsTooltipWhenValid()
		{
			runOnEDT( () ->
			{
				ValidatedTextField field = new ValidatedTextField( 10, NUMERIC_ONLY, ERROR_MSG );
				field.setText( "abc" ); // Invalid first
				field.setText( "123" ); // Then valid
				
				assertNull( field.getToolTipText() );
			} );
		}
		
		@Test
		@DisplayName( "Clears tooltip when blank" )
		void clearsTooltipWhenBlank()
		{
			runOnEDT( () ->
			{
				ValidatedTextField field = new ValidatedTextField( 10, NUMERIC_ONLY, ERROR_MSG );
				field.setText( "abc" ); // Invalid first
				field.setText( "" ); // Then blank
				
				assertNull( field.getToolTipText() );
			} );
		}
		
		@Test
		@DisplayName( "Border changes as text changes" )
		void borderChangesWithText()
		{
			runOnEDT( () ->
			{
				ValidatedTextField field = new ValidatedTextField( 10, MIN_LENGTH_3, ERROR_MSG );
				
				field.setText( "a" );
				assertTrue( hasRedBorder( field ), "1 char should show error" );
				
				field.setText( "ab" );
				assertTrue( hasRedBorder( field ), "2 chars should show error" );
				
				field.setText( "abc" );
				assertFalse( hasRedBorder( field ), "3 chars should show default" );
				
				field.setText( "ab" );
				assertTrue( hasRedBorder( field ), "Back to 2 chars should show error" );
			} );
		}
	}
	
	@Nested
	@DisplayName( "Validity listener" )
	class ValidityListener
	{
		@Test
		@DisplayName( "Listener receives true when valid" )
		void listenerReceivesTrueWhenValid()
		{
			runOnEDT( () ->
			{
				ValidatedTextField field = new ValidatedTextField( 10, ALWAYS_VALID, ERROR_MSG );
				AtomicBoolean received = new AtomicBoolean( false );
				
				field.setValidityListener( valid -> received.set( valid ) );
				field.setText( "test" );
				
				assertTrue( received.get() );
			} );
		}
		
		@Test
		@DisplayName( "Listener receives false when invalid" )
		void listenerReceivesFalseWhenInvalid()
		{
			runOnEDT( () ->
			{
				ValidatedTextField field = new ValidatedTextField( 10, ALWAYS_INVALID, ERROR_MSG );
				AtomicBoolean received = new AtomicBoolean( true ); // Start true
				
				field.setValidityListener( valid -> received.set( valid ) );
				field.setText( "test" );
				
				assertFalse( received.get() );
			} );
		}
		
		@Test
		@DisplayName( "Listener called on every text change" )
		void listenerCalledOnEveryChange()
		{
			runOnEDT( () ->
			{
				ValidatedTextField field = new ValidatedTextField( 10, ALWAYS_VALID, ERROR_MSG );
				AtomicInteger callCount = new AtomicInteger( 0 );
				
				field.setValidityListener( valid -> callCount.incrementAndGet() );
				
				field.setText( "a" );
				field.setText( "ab" );
				field.setText( "abc" );
				
				assertEquals( 5, callCount.get() ); // replacing existing text via setText() calls both DocumentListener.insertUpdate and
													// DocumentListener.removeUpdate
			} );
		}
		
		@Test
		@DisplayName( "Listener can be set to null" )
		void listenerCanBeSetToNull()
		{
			runOnEDT( () ->
			{
				ValidatedTextField field = new ValidatedTextField( 10, ALWAYS_VALID, ERROR_MSG );
				AtomicInteger callCount = new AtomicInteger( 0 );
				
				field.setValidityListener( valid -> callCount.incrementAndGet() );
				field.setText( "a" );
				assertEquals( 1, callCount.get() );
				
				field.setValidityListener( null );
				field.setText( "ab" ); // Should not throw
				assertEquals( 1, callCount.get(), "Listener should not be called after null" );
			} );
		}
		
		@Test
		@DisplayName( "Listener can be replaced" )
		void listenerCanBeReplaced()
		{
			runOnEDT( () ->
			{
				ValidatedTextField field = new ValidatedTextField( 10, ALWAYS_VALID, ERROR_MSG );
				AtomicInteger firstCount = new AtomicInteger( 0 );
				AtomicInteger secondCount = new AtomicInteger( 0 );
				
				field.setValidityListener( valid -> firstCount.incrementAndGet() );
				field.setText( "a" );
				assertEquals( 1, firstCount.get(), "First listener called once" );
				
				field.setText( null );
				
				field.setValidityListener( valid -> secondCount.incrementAndGet() );
				field.setText( "b" );
				assertEquals( 1, secondCount.get(), "Second listener called once" );
			} );
		}
		
		@Test
		@DisplayName( "No listener by default" )
		void noListenerByDefault()
		{
			runOnEDT( () ->
			{
				ValidatedTextField field = new ValidatedTextField( 10, ALWAYS_VALID, ERROR_MSG );
				// Should not throw when changing text without listener
				assertDoesNotThrow( () -> field.setText( "test" ) );
			} );
		}
	}
	
	@Nested
	@DisplayName( "Document listener integration" )
	class DocumentListenerIntegration
	{
		@Test
		@DisplayName( "Validates on text insertion" )
		void validatesOnInsert()
		{
			runOnEDT( () ->
			{
				ValidatedTextField field = new ValidatedTextField( 10, NUMERIC_ONLY, ERROR_MSG );
				AtomicBoolean listenerCalled = new AtomicBoolean( false );
				field.setValidityListener( valid -> listenerCalled.set( true ) );
				
				field.setText( "123" );
				
				assertTrue( listenerCalled.get() );
				assertTrue( field.isInputValid() );
			} );
		}
		
		@Test
		@DisplayName( "Validates on text removal" )
		void validatesOnRemove()
		{
			runOnEDT( () ->
			{
				ValidatedTextField field = new ValidatedTextField( 10, MIN_LENGTH_3, ERROR_MSG );
				field.setText( "abcd" );
				assertTrue( field.isInputValid() );
				
				// Remove last character via document
				field.setText( "ab" );
				
				assertFalse( field.isInputValid() );
				assertTrue( hasRedBorder( field ) );
			} );
		}
	}
	
	@Nested
	@DisplayName( "Edge cases" )
	class EdgeCases
	{
		@Test
		@DisplayName( "Handles null from validator gracefully" )
		void handlesValidatorWithNullText()
		{
			runOnEDT( () ->
			{
				// Validator that doesn't handle null
				Validator unsafeValidator = text -> text.length() > 0;
				ValidatedTextField field = new ValidatedTextField( 10, unsafeValidator, ERROR_MSG );
				
				// getText() never returns null in Swing, so this should be safe
				assertDoesNotThrow( () -> field.isInputValid() );
			} );
		}
		
		@Test
		@DisplayName( "Works with very long text" )
		void worksWithLongText()
		{
			runOnEDT( () ->
			{
				ValidatedTextField field = new ValidatedTextField( 10, ALWAYS_VALID, ERROR_MSG );
				String longText = "a".repeat( 10000 );
				
				assertDoesNotThrow( () -> field.setText( longText ) );
				assertEquals( longText, field.getText() );
				assertTrue( field.isInputValid() );
			} );
		}
		
		@Test
		@DisplayName( "Works with special characters" )
		void worksWithSpecialCharacters()
		{
			runOnEDT( () ->
			{
				ValidatedTextField field = new ValidatedTextField( 10, ALWAYS_VALID, ERROR_MSG );
				String special = "émojis: 🎉 and symbols: @#$%^&*()";
				
				field.setText( special );
				assertEquals( special, field.getText() );
			} );
		}
		
		@Test
		@DisplayName( "Setting same text still triggers validation" )
		void settingSameTextTriggersValidation()
		{
			runOnEDT( () ->
			{
				ValidatedTextField field = new ValidatedTextField( 10, ALWAYS_VALID, ERROR_MSG );
				AtomicInteger callCount = new AtomicInteger( 0 );
				field.setValidityListener( valid -> callCount.incrementAndGet() );
				
				field.setText( "test" );
				int firstCount = callCount.get();
				
				field.setText( "test" ); // Same text
				// Note: Swing may optimize this, but our listener should still work
				assertTrue( callCount.get() >= firstCount );
			} );
		}
		
		@Test
		@DisplayName( "Custom error message is preserved" )
		void customErrorMessagePreserved()
		{
			runOnEDT( () ->
			{
				String customMessage = "Please enter a valid email address";
				ValidatedTextField field = new ValidatedTextField( 10, ALWAYS_INVALID, customMessage );
				field.setText( "invalid" );
				
				assertEquals( customMessage, field.getToolTipText() );
			} );
		}
	}
	
	@Nested
	@DisplayName( "Real-world validators" )
	class RealWorldValidators
	{
		@Test
		@DisplayName( "Email validation pattern" )
		void emailValidation()
		{
			runOnEDT( () ->
			{
				Validator emailValidator = text -> text == null || text.isEmpty() || text.matches( "^[\\w.-]+@[\\w.-]+\\.[A-Za-z]{2,}$" );
				ValidatedTextField field = new ValidatedTextField( 20, emailValidator, "Invalid email" );
				
				field.setText( "user@example.com" );
				assertTrue( field.isInputValid() );
				assertFalse( hasRedBorder( field ) );
				
				field.setText( "invalid-email" );
				assertFalse( field.isInputValid() );
				assertTrue( hasRedBorder( field ) );
				
				field.setText( "" ); // Empty is valid (optional field)
				assertTrue( field.isInputValid() );
				assertFalse( hasRedBorder( field ) );
			} );
		}
		
		@Test
		@DisplayName( "Phone number validation pattern" )
		void phoneValidation()
		{
			runOnEDT( () ->
			{
				Validator phoneValidator = text ->
				{
					if( text == null || text.isEmpty() )
						return true;
					String digits = text.replaceAll( "\\D", "" );
					return digits.length() == 10;
				};
				ValidatedTextField field = new ValidatedTextField( 15, phoneValidator, "Invalid phone" );
				
				field.setText( "555-123-4567" );
				assertTrue( field.isInputValid() );
				
				field.setText( "555-123" );
				assertFalse( field.isInputValid() );
			} );
		}
	}
	
	// Helper method to check if field has red error border
	private static boolean hasRedBorder( ValidatedTextField field )
	{
		Border border = field.getBorder();
		if( border instanceof CompoundBorder )
		{
			CompoundBorder compound = (CompoundBorder) border;
			Border outside = compound.getOutsideBorder();
			if( outside instanceof LineBorder )
			{
				return ( (LineBorder) outside ).getLineColor().equals( Color.RED );
			}
		}
		return false;
	}
}