package com.alexpacheco.therapynotes.view.components;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link Cmb_ICD10Diagnosis}.
 */
@DisplayName( "Cmb_ICD10Diagnosis" )
class Cmb_ICD10DiagnosisTest
{
	private Cmb_ICD10Diagnosis comboBox;
	private List<String> testCodes;
	
	// Sample ICD-10 codes for testing
	private static final String CODE_DEPRESSION_MILD = "F32.0 - Major depressive disorder, single episode, mild";
	private static final String CODE_DEPRESSION_MODERATE = "F32.1 - Major depressive disorder, single episode, moderate";
	private static final String CODE_ANXIETY = "F41.1 - Generalized anxiety disorder";
	private static final String CODE_PTSD = "F43.10 - Post-traumatic stress disorder, unspecified";
	private static final String CODE_ADHD = "F90.0 - Attention-deficit hyperactivity disorder, predominantly inattentive type";
	
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
	
	/**
	 * Gets the editor text field from the combo box.
	 */
	private JTextField getEditorField( Cmb_ICD10Diagnosis combo )
	{
		return (JTextField) combo.getEditor().getEditorComponent();
	}
	
	@BeforeEach
	void setUp()
	{
		testCodes = Arrays.asList( CODE_DEPRESSION_MILD, CODE_DEPRESSION_MODERATE, CODE_ANXIETY, CODE_PTSD, CODE_ADHD );
		
		runOnEDT( () ->
		{
			comboBox = new Cmb_ICD10Diagnosis( testCodes );
		} );
	}
	
	@Nested
	@DisplayName( "Construction" )
	class Construction
	{
		@Test
		@DisplayName( "Is editable" )
		void isEditable()
		{
			runOnEDT( () ->
			{
				assertTrue( comboBox.isEditable() );
			} );
		}
		
		@Test
		@DisplayName( "Has blank option as first element" )
		void hasBlankOption()
		{
			runOnEDT( () ->
			{
				assertEquals( "", comboBox.getItemAt( 0 ) );
			} );
		}
		
		@Test
		@DisplayName( "Contains all codes plus blank option" )
		void containsAllCodes()
		{
			runOnEDT( () ->
			{
				assertEquals( testCodes.size() + 1, comboBox.getItemCount() );
			} );
		}
		
		@Test
		@DisplayName( "Codes appear in order after blank option" )
		void codesInOrder()
		{
			runOnEDT( () ->
			{
				for( int i = 0; i < testCodes.size(); i++ )
				{
					assertEquals( testCodes.get( i ), comboBox.getItemAt( i + 1 ) );
				}
			} );
		}
		
		@Test
		@DisplayName( "Handles null code list gracefully" )
		void handlesNullCodeList()
		{
			runOnEDT( () ->
			{
				Cmb_ICD10Diagnosis nullCombo = new Cmb_ICD10Diagnosis( null );
				assertEquals( 0, nullCombo.getItemCount() );
			} );
		}
		
		@Test
		@DisplayName( "Handles empty code list" )
		void handlesEmptyCodeList()
		{
			runOnEDT( () ->
			{
				Cmb_ICD10Diagnosis emptyCombo = new Cmb_ICD10Diagnosis( Collections.emptyList() );
				assertEquals( 1, emptyCombo.getItemCount() ); // Just blank option
				assertEquals( "", emptyCombo.getItemAt( 0 ) );
			} );
		}
		
		@Test
		@DisplayName( "Handles single code" )
		void handlesSingleCode()
		{
			runOnEDT( () ->
			{
				Cmb_ICD10Diagnosis singleCombo = new Cmb_ICD10Diagnosis( Arrays.asList( CODE_ANXIETY ) );
				assertEquals( 2, singleCombo.getItemCount() );
				assertEquals( "", singleCombo.getItemAt( 0 ) );
				assertEquals( CODE_ANXIETY, singleCombo.getItemAt( 1 ) );
			} );
		}
	}
	
	@Nested
	@DisplayName( "getDiagnosis()" )
	class GetDiagnosis
	{
		@Test
		@DisplayName( "Returns empty string when nothing selected" )
		void returnsEmptyWhenNothingSelected()
		{
			runOnEDT( () ->
			{
				comboBox.setSelectedItem( "" );
				assertEquals( "", comboBox.getDiagnosis() );
			} );
		}
		
		@Test
		@DisplayName( "Returns empty string when null selected" )
		void returnsEmptyWhenNullSelected()
		{
			runOnEDT( () ->
			{
				comboBox.setSelectedItem( null );
				assertEquals( "", comboBox.getDiagnosis() );
			} );
		}
		
		@Test
		@DisplayName( "Returns selected code when valid code selected" )
		void returnsSelectedCode()
		{
			runOnEDT( () ->
			{
				comboBox.setSelectedItem( CODE_ANXIETY );
				assertEquals( CODE_ANXIETY, comboBox.getDiagnosis() );
			} );
		}
		
		@Test
		@DisplayName( "Returns each test code correctly" )
		void returnsEachTestCode()
		{
			runOnEDT( () ->
			{
				for( String code : testCodes )
				{
					comboBox.setSelectedItem( code );
					assertEquals( code, comboBox.getDiagnosis() );
				}
			} );
		}
		
		@Test
		@DisplayName( "Returns editor text for 'No matching codes found' placeholder" )
		void returnsEditorTextForNoResultsPlaceholder()
		{
			runOnEDT( () ->
			{
				JTextField editor = getEditorField( comboBox );
				comboBox.setSelectedItem( "No matching codes found" );
				editor.setText( "XYZ123" ); // Set AFTER to simulate real user typing
				
				assertEquals( "XYZ123", comboBox.getDiagnosis() );
			} );
		}
		
		@Test
		@DisplayName( "Returns empty for 'No matching codes found' when editor is empty" )
		void returnsEmptyForNoResultsWhenEditorEmpty()
		{
			runOnEDT( () ->
			{
				JTextField editor = getEditorField( comboBox );
				comboBox.setSelectedItem( "No matching codes found" );
				editor.setText( "" ); // Set AFTER to simulate real user typing
				
				assertEquals( "", comboBox.getDiagnosis() );
			} );
		}
		
		@Test
		@DisplayName( "Handles case-insensitive match" )
		void handlesCaseInsensitiveMatch()
		{
			runOnEDT( () ->
			{
				comboBox.setSelectedItem( CODE_ANXIETY.toLowerCase() );
				assertEquals( CODE_ANXIETY, comboBox.getDiagnosis() );
			} );
		}
		
		@Test
		@DisplayName( "Handles uppercase match" )
		void handlesUppercaseMatch()
		{
			runOnEDT( () ->
			{
				comboBox.setSelectedItem( CODE_ANXIETY.toUpperCase() );
				assertEquals( CODE_ANXIETY, comboBox.getDiagnosis() );
			} );
		}
		
		@Test
		@DisplayName( "Returns editor text for unrecognized input" )
		void returnsEditorTextForUnrecognizedInput()
		{
			runOnEDT( () ->
			{
				JTextField editor = getEditorField( comboBox );
				String customText = "Custom diagnosis text";
				editor.setText( customText );
				comboBox.setSelectedItem( customText );
				
				assertEquals( customText, comboBox.getDiagnosis() );
			} );
		}
	}
	
	@Nested
	@DisplayName( "setDiagnosis()" )
	class SetDiagnosis
	{
		@Test
		@DisplayName( "Sets empty for null input" )
		void setsEmptyForNull()
		{
			runOnEDT( () ->
			{
				comboBox.setDiagnosis( null );
				assertEquals( "", comboBox.getSelectedItem() );
			} );
		}
		
		@Test
		@DisplayName( "Sets empty for empty string input" )
		void setsEmptyForEmptyString()
		{
			runOnEDT( () ->
			{
				comboBox.setDiagnosis( "" );
				assertEquals( "", comboBox.getSelectedItem() );
			} );
		}
		
		@Test
		@DisplayName( "Sets empty for whitespace-only input" )
		void setsEmptyForWhitespace()
		{
			runOnEDT( () ->
			{
				comboBox.setDiagnosis( "   " );
				assertEquals( "", comboBox.getSelectedItem() );
			} );
		}
		
		@Test
		@DisplayName( "Selects exact match" )
		void selectsExactMatch()
		{
			runOnEDT( () ->
			{
				comboBox.setDiagnosis( CODE_PTSD );
				assertEquals( CODE_PTSD, comboBox.getSelectedItem() );
			} );
		}
		
		@Test
		@DisplayName( "Selects case-insensitive match" )
		void selectsCaseInsensitiveMatch()
		{
			runOnEDT( () ->
			{
				comboBox.setDiagnosis( CODE_PTSD.toLowerCase() );
				assertEquals( CODE_PTSD, comboBox.getSelectedItem() );
			} );
		}
		
		@Test
		@DisplayName( "Prefers exact match over case-insensitive" )
		void prefersExactMatch()
		{
			runOnEDT( () ->
			{
				comboBox.setDiagnosis( CODE_ADHD );
				assertEquals( CODE_ADHD, comboBox.getSelectedItem() );
			} );
		}
		
		@Test
		@DisplayName( "Sets unmatched text directly for legacy data" )
		void setsUnmatchedTextDirectly()
		{
			runOnEDT( () ->
			{
				String legacyCode = "Z99.99 - Some legacy code no longer in list";
				comboBox.setDiagnosis( legacyCode );
				assertEquals( legacyCode, comboBox.getSelectedItem() );
			} );
		}
		
		@Test
		@DisplayName( "Round-trip: setDiagnosis then getDiagnosis" )
		void roundTrip()
		{
			runOnEDT( () ->
			{
				for( String code : testCodes )
				{
					comboBox.setDiagnosis( code );
					assertEquals( code, comboBox.getDiagnosis() );
				}
			} );
		}
	}
	
	@Nested
	@DisplayName( "clear()" )
	class Clear
	{
		@Test
		@DisplayName( "Clears selected item" )
		void clearsSelectedItem()
		{
			runOnEDT( () ->
			{
				comboBox.setDiagnosis( CODE_DEPRESSION_MILD );
				comboBox.clear();
				assertEquals( "", comboBox.getSelectedItem() );
			} );
		}
		
		@Test
		@DisplayName( "Clears editor text" )
		void clearsEditorText()
		{
			runOnEDT( () ->
			{
				JTextField editor = getEditorField( comboBox );
				comboBox.setDiagnosis( CODE_DEPRESSION_MODERATE );
				comboBox.clear();
				assertEquals( "", editor.getText() );
			} );
		}
		
		@Test
		@DisplayName( "getDiagnosis returns empty after clear" )
		void getDiagnosisReturnsEmptyAfterClear()
		{
			runOnEDT( () ->
			{
				comboBox.setDiagnosis( CODE_ANXIETY );
				comboBox.clear();
				assertEquals( "", comboBox.getDiagnosis() );
			} );
		}
		
		@Test
		@DisplayName( "Can set new value after clear" )
		void canSetValueAfterClear()
		{
			runOnEDT( () ->
			{
				comboBox.setDiagnosis( CODE_DEPRESSION_MILD );
				comboBox.clear();
				comboBox.setDiagnosis( CODE_PTSD );
				assertEquals( CODE_PTSD, comboBox.getDiagnosis() );
			} );
		}
		
		@Test
		@DisplayName( "Multiple clears are safe" )
		void multipleClearsAreSafe()
		{
			runOnEDT( () ->
			{
				comboBox.clear();
				comboBox.clear();
				comboBox.clear();
				assertEquals( "", comboBox.getDiagnosis() );
			} );
		}
	}
	
	@Nested
	@DisplayName( "Edge cases" )
	class EdgeCases
	{
		@Test
		@DisplayName( "Handles special characters in diagnosis" )
		void handlesSpecialCharacters()
		{
			runOnEDT( () ->
			{
				String specialCode = "F99.9 - Diagnosis with special chars: <>&\"'";
				Cmb_ICD10Diagnosis specialCombo = new Cmb_ICD10Diagnosis( Arrays.asList( specialCode ) );
				
				specialCombo.setDiagnosis( specialCode );
				assertEquals( specialCode, specialCombo.getDiagnosis() );
			} );
		}
		
		@Test
		@DisplayName( "Handles very long diagnosis text" )
		void handlesLongText()
		{
			runOnEDT( () ->
			{
				String longCode = "F99.99 - " + "Very long description ".repeat( 20 );
				Cmb_ICD10Diagnosis longCombo = new Cmb_ICD10Diagnosis( Arrays.asList( longCode ) );
				
				longCombo.setDiagnosis( longCode );
				assertEquals( longCode, longCombo.getDiagnosis() );
			} );
		}
		
		@Test
		@DisplayName( "Distinguishes similar codes" )
		void distinguishesSimilarCodes()
		{
			runOnEDT( () ->
			{
				// F32.0 vs F32.1 - very similar codes
				comboBox.setDiagnosis( CODE_DEPRESSION_MILD );
				assertEquals( CODE_DEPRESSION_MILD, comboBox.getDiagnosis() );
				assertNotEquals( CODE_DEPRESSION_MODERATE, comboBox.getDiagnosis() );
				
				comboBox.setDiagnosis( CODE_DEPRESSION_MODERATE );
				assertEquals( CODE_DEPRESSION_MODERATE, comboBox.getDiagnosis() );
				assertNotEquals( CODE_DEPRESSION_MILD, comboBox.getDiagnosis() );
			} );
		}
		
		@Test
		@DisplayName( "Editor field is accessible" )
		void editorFieldIsAccessible()
		{
			runOnEDT( () ->
			{
				JTextField editor = getEditorField( comboBox );
				assertNotNull( editor );
				assertTrue( editor.isEditable() );
			} );
		}
		
		@Test
		@DisplayName( "Direct editor text input is preserved" )
		void directEditorInputPreserved()
		{
			runOnEDT( () ->
			{
				JTextField editor = getEditorField( comboBox );
				editor.setText( "User typed this directly" );
				
				// getDiagnosis should return the editor text for unrecognized input
				String diagnosis = comboBox.getDiagnosis();
				assertEquals( "User typed this directly", diagnosis );
			} );
		}
	}
	
	@Nested
	@DisplayName( "State transitions" )
	class StateTransitions
	{
		@Test
		@DisplayName( "Empty -> Selected -> Empty" )
		void emptySelectedEmpty()
		{
			runOnEDT( () ->
			{
				assertEquals( "", comboBox.getDiagnosis() );
				
				comboBox.setDiagnosis( CODE_ANXIETY );
				assertEquals( CODE_ANXIETY, comboBox.getDiagnosis() );
				
				comboBox.clear();
				assertEquals( "", comboBox.getDiagnosis() );
			} );
		}
		
		@Test
		@DisplayName( "Code A -> Code B -> Code A" )
		void switchBetweenCodes()
		{
			runOnEDT( () ->
			{
				comboBox.setDiagnosis( CODE_DEPRESSION_MILD );
				assertEquals( CODE_DEPRESSION_MILD, comboBox.getDiagnosis() );
				
				comboBox.setDiagnosis( CODE_PTSD );
				assertEquals( CODE_PTSD, comboBox.getDiagnosis() );
				
				comboBox.setDiagnosis( CODE_DEPRESSION_MILD );
				assertEquals( CODE_DEPRESSION_MILD, comboBox.getDiagnosis() );
			} );
		}
		
		@Test
		@DisplayName( "Valid code -> Custom text -> Valid code" )
		void validCustomValid()
		{
			runOnEDT( () ->
			{
				comboBox.setDiagnosis( CODE_ADHD );
				assertEquals( CODE_ADHD, comboBox.getDiagnosis() );
				
				comboBox.setDiagnosis( "Custom legacy code" );
				assertEquals( "Custom legacy code", comboBox.getDiagnosis() );
				
				comboBox.setDiagnosis( CODE_ANXIETY );
				assertEquals( CODE_ANXIETY, comboBox.getDiagnosis() );
			} );
		}
	}
}