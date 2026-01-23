package com.alexpacheco.therapynotes.view.components;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.JCheckBox;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.alexpacheco.therapynotes.model.entities.assessmentoptions.AssessmentOption;
import com.alexpacheco.therapynotes.model.entities.assessmentoptions.AssessmentOptionFactory;
import com.alexpacheco.therapynotes.model.entities.assessmentoptions.AssessmentOptionType;

/**
 * Unit tests for {@link AssessmentOptionComponent}.
 * 
 * Uses a concrete test implementation since AssessmentOptionComponent is abstract.
 */
@DisplayName( "AssessmentOptionComponent" )
class AssessmentOptionComponentTest
{
	private static final int TEST_ID = 42;
	private static final String TEST_NAME = "Test Option";
	private static final String TEST_DESCRIPTION = "Test description for tooltip";
	private static final AssessmentOptionType TEST_TYPE = AssessmentOptionType.SYMPTOMS;
	
	/**
	 * Concrete implementation for testing purposes.
	 */
	private static class TestAssessmentOptionComponent extends AssessmentOptionComponent
	{
		private static final long serialVersionUID = 1L;
		
		public TestAssessmentOptionComponent( AssessmentOption option )
		{
			super( option );
		}
		
		@Override
		protected void initializeButton()
		{
			button = new JCheckBox();
		}
	}
	
	/**
	 * Creates an AssessmentOption with an ID using the factory.
	 */
	private AssessmentOption createOption( int id, String name, String description )
	{
		return AssessmentOptionFactory.createAssessmentOption( id, name, description, TEST_TYPE );
	}
	
	/**
	 * Creates an AssessmentOption without an ID using the factory (for testing null ID validation).
	 */
	private AssessmentOption createOptionWithoutId( String name, String description )
	{
		return AssessmentOptionFactory.createAssessmentOption( name, description, TEST_TYPE );
	}
	
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
	@DisplayName( "Constructor validation" )
	class ConstructorValidation
	{
		@Test
		@DisplayName( "Throws IllegalArgumentException for null AssessmentOption" )
		void throwsForNullOption()
		{
			runOnEDT( () ->
			{
				IllegalArgumentException ex = assertThrows( IllegalArgumentException.class,
						() -> new TestAssessmentOptionComponent( null ) );
				assertEquals( "AssessmentOption cannot be null", ex.getMessage() );
			} );
		}
		
		@Test
		@DisplayName( "Throws IllegalArgumentException for AssessmentOption with null ID" )
		void throwsForNullId()
		{
			runOnEDT( () ->
			{
				AssessmentOption optionWithNullId = createOptionWithoutId( TEST_NAME, TEST_DESCRIPTION );
				
				IllegalArgumentException ex = assertThrows( IllegalArgumentException.class,
						() -> new TestAssessmentOptionComponent( optionWithNullId ) );
				assertEquals( "AssessmentOption must have an ID", ex.getMessage() );
			} );
		}
		
		@Test
		@DisplayName( "Accepts valid AssessmentOption" )
		void acceptsValidOption()
		{
			runOnEDT( () ->
			{
				AssessmentOption validOption = createOption( TEST_ID, TEST_NAME, TEST_DESCRIPTION );
				
				assertDoesNotThrow( () -> new TestAssessmentOptionComponent( validOption ) );
			} );
		}
	}
	
	@Nested
	@DisplayName( "Component setup" )
	class ComponentSetup
	{
		private TestAssessmentOptionComponent component;
		private AssessmentOption option;
		
		@BeforeEach
		void setUp()
		{
			runOnEDT( () ->
			{
				option = createOption( TEST_ID, TEST_NAME, TEST_DESCRIPTION );
				component = new TestAssessmentOptionComponent( option );
			} );
		}
		
		@Test
		@DisplayName( "Button text is set to option name" )
		void buttonTextIsOptionName()
		{
			runOnEDT( () ->
			{
				assertEquals( TEST_NAME, component.getButton().getText() );
			} );
		}
		
		@Test
		@DisplayName( "Tooltip is set to description when present" )
		void tooltipSetWhenDescriptionPresent()
		{
			runOnEDT( () ->
			{
				assertEquals( TEST_DESCRIPTION, component.getButton().getToolTipText() );
			} );
		}
		
		@Test
		@DisplayName( "Tooltip is null when description is null" )
		void tooltipNullWhenDescriptionNull()
		{
			runOnEDT( () ->
			{
				AssessmentOption noDesc = createOption( TEST_ID, TEST_NAME, null );
				TestAssessmentOptionComponent comp = new TestAssessmentOptionComponent( noDesc );
				
				assertNull( comp.getButton().getToolTipText() );
			} );
		}
		
		@Test
		@DisplayName( "Tooltip is null when description is empty" )
		void tooltipNullWhenDescriptionEmpty()
		{
			runOnEDT( () ->
			{
				AssessmentOption emptyDesc = createOption( TEST_ID, TEST_NAME, "" );
				TestAssessmentOptionComponent comp = new TestAssessmentOptionComponent( emptyDesc );
				
				assertNull( comp.getButton().getToolTipText() );
			} );
		}
		
		@Test
		@DisplayName( "Tooltip is null when description is whitespace" )
		void tooltipNullWhenDescriptionWhitespace()
		{
			runOnEDT( () ->
			{
				AssessmentOption whitespaceDesc = createOption( TEST_ID, TEST_NAME, "   " );
				TestAssessmentOptionComponent comp = new TestAssessmentOptionComponent( whitespaceDesc );
				
				assertNull( comp.getButton().getToolTipText() );
			} );
		}
		
		@Test
		@DisplayName( "Action command is set to option ID as string" )
		void actionCommandIsOptionId()
		{
			runOnEDT( () ->
			{
				assertEquals( String.valueOf( TEST_ID ), component.getButton().getActionCommand() );
			} );
		}
		
		@Test
		@DisplayName( "Button is added to component" )
		void buttonAddedToComponent()
		{
			runOnEDT( () ->
			{
				assertTrue( component.getComponentCount() > 0 );
				assertNotNull( component.getButton() );
			} );
		}
	}
	
	@Nested
	@DisplayName( "Getters" )
	class Getters
	{
		private TestAssessmentOptionComponent component;
		private AssessmentOption option;
		
		@BeforeEach
		void setUp()
		{
			runOnEDT( () ->
			{
				option = createOption( TEST_ID, TEST_NAME, TEST_DESCRIPTION );
				component = new TestAssessmentOptionComponent( option );
			} );
		}
		
		@Test
		@DisplayName( "getAssessmentOption returns the option" )
		void getAssessmentOptionReturnsOption()
		{
			runOnEDT( () ->
			{
				assertSame( option, component.getAssessmentOption() );
			} );
		}
		
		@Test
		@DisplayName( "getAssessmentOptionId returns the ID" )
		void getAssessmentOptionIdReturnsId()
		{
			runOnEDT( () ->
			{
				assertEquals( TEST_ID, component.getAssessmentOptionId() );
			} );
		}
		
		@Test
		@DisplayName( "getAssessmentOptionName returns the name" )
		void getAssessmentOptionNameReturnsName()
		{
			runOnEDT( () ->
			{
				assertEquals( TEST_NAME, component.getAssessmentOptionName() );
			} );
		}
		
		@Test
		@DisplayName( "getButton returns the button" )
		void getButtonReturnsButton()
		{
			runOnEDT( () ->
			{
				assertNotNull( component.getButton() );
				assertInstanceOf( JCheckBox.class, component.getButton() );
			} );
		}
	}
	
	@Nested
	@DisplayName( "Selection state" )
	class SelectionState
	{
		private TestAssessmentOptionComponent component;
		
		@BeforeEach
		void setUp()
		{
			runOnEDT( () ->
			{
				AssessmentOption option = createOption( TEST_ID, TEST_NAME, TEST_DESCRIPTION );
				component = new TestAssessmentOptionComponent( option );
			} );
		}
		
		@Test
		@DisplayName( "isSelected returns false initially" )
		void isSelectedFalseInitially()
		{
			runOnEDT( () ->
			{
				assertFalse( component.isSelected() );
			} );
		}
		
		@Test
		@DisplayName( "setSelected(true) selects the component" )
		void setSelectedTrueSelectsComponent()
		{
			runOnEDT( () ->
			{
				component.setSelected( true );
				assertTrue( component.isSelected() );
				assertTrue( component.getButton().isSelected() );
			} );
		}
		
		@Test
		@DisplayName( "setSelected(false) deselects the component" )
		void setSelectedFalseDeselectsComponent()
		{
			runOnEDT( () ->
			{
				component.setSelected( true );
				component.setSelected( false );
				assertFalse( component.isSelected() );
				assertFalse( component.getButton().isSelected() );
			} );
		}
		
		@Test
		@DisplayName( "isSelected reflects button state" )
		void isSelectedReflectsButtonState()
		{
			runOnEDT( () ->
			{
				component.getButton().setSelected( true );
				assertTrue( component.isSelected() );
				
				component.getButton().setSelected( false );
				assertFalse( component.isSelected() );
			} );
		}
	}
	
	@Nested
	@DisplayName( "Enabled state" )
	class EnabledState
	{
		private TestAssessmentOptionComponent component;
		
		@BeforeEach
		void setUp()
		{
			runOnEDT( () ->
			{
				AssessmentOption option = createOption( TEST_ID, TEST_NAME, TEST_DESCRIPTION );
				component = new TestAssessmentOptionComponent( option );
			} );
		}
		
		@Test
		@DisplayName( "Component is enabled initially" )
		void enabledInitially()
		{
			runOnEDT( () ->
			{
				assertTrue( component.isEnabled() );
				assertTrue( component.getButton().isEnabled() );
			} );
		}
		
		@Test
		@DisplayName( "setEnabled(false) disables both component and button" )
		void setEnabledFalseDisablesBoth()
		{
			runOnEDT( () ->
			{
				component.setEnabled( false );
				assertFalse( component.isEnabled() );
				assertFalse( component.getButton().isEnabled() );
			} );
		}
		
		@Test
		@DisplayName( "setEnabled(true) enables both component and button" )
		void setEnabledTrueEnablesBoth()
		{
			runOnEDT( () ->
			{
				component.setEnabled( false );
				component.setEnabled( true );
				assertTrue( component.isEnabled() );
				assertTrue( component.getButton().isEnabled() );
			} );
		}
	}
	
	@Nested
	@DisplayName( "Action listeners" )
	class ActionListeners
	{
		private TestAssessmentOptionComponent component;
		
		@BeforeEach
		void setUp()
		{
			runOnEDT( () ->
			{
				AssessmentOption option = createOption( TEST_ID, TEST_NAME, TEST_DESCRIPTION );
				component = new TestAssessmentOptionComponent( option );
			} );
		}
		
		@Test
		@DisplayName( "addActionListener adds listener to button" )
		void addActionListenerAddsToButton()
		{
			runOnEDT( () ->
			{
				AtomicBoolean called = new AtomicBoolean( false );
				ActionListener listener = e -> called.set( true );
				
				component.addActionListener( listener );
				component.getButton().doClick();
				
				assertTrue( called.get() );
			} );
		}
		
		@Test
		@DisplayName( "removeActionListener removes listener from button" )
		void removeActionListenerRemovesFromButton()
		{
			runOnEDT( () ->
			{
				AtomicInteger callCount = new AtomicInteger( 0 );
				ActionListener listener = e -> callCount.incrementAndGet();
				
				component.addActionListener( listener );
				component.getButton().doClick();
				assertEquals( 1, callCount.get() );
				
				component.removeActionListener( listener );
				component.getButton().doClick();
				assertEquals( 1, callCount.get() ); // Still 1, not 2
			} );
		}
		
		@Test
		@DisplayName( "Action event contains option ID as action command" )
		void actionEventContainsOptionId()
		{
			runOnEDT( () ->
			{
				AtomicBoolean verified = new AtomicBoolean( false );
				
				component.addActionListener( e ->
				{
					assertEquals( String.valueOf( TEST_ID ), e.getActionCommand() );
					verified.set( true );
				} );
				
				component.getButton().doClick();
				assertTrue( verified.get() );
			} );
		}
		
		@Test
		@DisplayName( "Multiple listeners can be added" )
		void multipleListenersCanBeAdded()
		{
			runOnEDT( () ->
			{
				AtomicInteger count1 = new AtomicInteger( 0 );
				AtomicInteger count2 = new AtomicInteger( 0 );
				
				component.addActionListener( e -> count1.incrementAndGet() );
				component.addActionListener( e -> count2.incrementAndGet() );
				
				component.getButton().doClick();
				
				assertEquals( 1, count1.get() );
				assertEquals( 1, count2.get() );
			} );
		}
	}
	
	@Nested
	@DisplayName( "Edge cases" )
	class EdgeCases
	{
		@Test
		@DisplayName( "Works with ID of zero" )
		void worksWithZeroId()
		{
			runOnEDT( () ->
			{
				AssessmentOption zeroIdOption = createOption( 0, TEST_NAME, TEST_DESCRIPTION );
				TestAssessmentOptionComponent comp = new TestAssessmentOptionComponent( zeroIdOption );
				
				assertEquals( Integer.valueOf( 0 ), comp.getAssessmentOptionId() );
				assertEquals( "0", comp.getButton().getActionCommand() );
			} );
		}
		
		@Test
		@DisplayName( "Works with negative ID" )
		void worksWithNegativeId()
		{
			runOnEDT( () ->
			{
				AssessmentOption negativeIdOption = createOption( -1, TEST_NAME, TEST_DESCRIPTION );
				TestAssessmentOptionComponent comp = new TestAssessmentOptionComponent( negativeIdOption );
				
				assertEquals( Integer.valueOf( -1 ), comp.getAssessmentOptionId() );
				assertEquals( "-1", comp.getButton().getActionCommand() );
			} );
		}
		
		@Test
		@DisplayName( "Works with empty name" )
		void worksWithEmptyName()
		{
			runOnEDT( () ->
			{
				AssessmentOption emptyNameOption = createOption( TEST_ID, "", TEST_DESCRIPTION );
				TestAssessmentOptionComponent comp = new TestAssessmentOptionComponent( emptyNameOption );
				
				assertEquals( "", comp.getAssessmentOptionName() );
				assertEquals( "", comp.getButton().getText() );
			} );
		}
		
		@Test
		@DisplayName( "Works with null name" )
		void worksWithNullName()
		{
			runOnEDT( () ->
			{
				AssessmentOption nullNameOption = createOption( TEST_ID, null, TEST_DESCRIPTION );
				TestAssessmentOptionComponent comp = new TestAssessmentOptionComponent( nullNameOption );
				
				assertNull( comp.getAssessmentOptionName() );
			} );
		}
		
		@Test
		@DisplayName( "Works with very long name" )
		void worksWithLongName()
		{
			runOnEDT( () ->
			{
				String longName = "A".repeat( 500 );
				AssessmentOption longNameOption = createOption( TEST_ID, longName, TEST_DESCRIPTION );
				TestAssessmentOptionComponent comp = new TestAssessmentOptionComponent( longNameOption );
				
				assertEquals( longName, comp.getAssessmentOptionName() );
				assertEquals( longName, comp.getButton().getText() );
			} );
		}
		
		@Test
		@DisplayName( "Works with special characters in name" )
		void worksWithSpecialCharsInName()
		{
			runOnEDT( () ->
			{
				String specialName = "Test <>&\"' Option™";
				AssessmentOption specialOption = createOption( TEST_ID, specialName, TEST_DESCRIPTION );
				TestAssessmentOptionComponent comp = new TestAssessmentOptionComponent( specialOption );
				
				assertEquals( specialName, comp.getButton().getText() );
			} );
		}
		
		@Test
		@DisplayName( "Works with large ID" )
		void worksWithLargeId()
		{
			runOnEDT( () ->
			{
				AssessmentOption largeIdOption = createOption( Integer.MAX_VALUE, TEST_NAME, TEST_DESCRIPTION );
				TestAssessmentOptionComponent comp = new TestAssessmentOptionComponent( largeIdOption );
				
				assertEquals( Integer.valueOf( Integer.MAX_VALUE ), comp.getAssessmentOptionId() );
				assertEquals( String.valueOf( Integer.MAX_VALUE ), comp.getButton().getActionCommand() );
			} );
		}
	}
}