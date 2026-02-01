package com.alexpacheco.therapynotes.util;

import static org.junit.jupiter.api.Assertions.*;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class JavaUtilsTest
{
	@Test
	@DisplayName( "isNullOrEmpty should detect null, empty, and non-empty strings" )
	public void testIsNullOrEmpty()
	{
		assertTrue( JavaUtils.isNullOrEmpty( null ), "Should be true for null" );
		assertTrue( JavaUtils.isNullOrEmpty( "" ), "Should be true for empty string" );
		assertTrue( JavaUtils.isNullOrEmpty( " " ), "Should be true for space character" );
		assertTrue( JavaUtils.isNullOrEmpty( "	" ), "Should be true for tab character" );
		assertFalse( JavaUtils.isNullOrEmpty( "text" ), "Should be false for actual content" );
	}
	
	@Test
	@DisplayName( "convertBitToBoolean should map 0 to false and others to true" )
	public void testConvertBitToBoolean()
	{
		assertFalse( JavaUtils.convertBitToBoolean( 0 ), "0 must be false" );
		assertTrue( JavaUtils.convertBitToBoolean( 1 ), "1 must be true" );
		
		// Testing "truthy" behavior for non-standard bits
		assertTrue( JavaUtils.convertBitToBoolean( -1 ), "Negative values should be true" );
		assertTrue( JavaUtils.convertBitToBoolean( 99 ), "High values should be true" );
	}
	
	@Test
	@DisplayName( "convertBooleanToBit should map boolean back to 0 or 1" )
	public void testConvertBooleanToBit()
	{
		assertEquals( 0, JavaUtils.convertBooleanToBit( false ), "false must result in 0" );
		assertEquals( 1, JavaUtils.convertBooleanToBit( true ), "true must result in 1" );
	}
	
	@Nested
	@DisplayName( "removeAllButtonsFromGroup" )
	class RemoveAllButtonsFromGroup
	{
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
		
		private int getButtonCount( ButtonGroup group )
		{
			int count = 0;
			var elements = group.getElements();
			while( elements.hasMoreElements() )
			{
				elements.nextElement();
				count++;
			}
			return count;
		}
		
		@Test
		@DisplayName( "Should not throw on empty group" )
		void emptyGroup_doesNotThrow()
		{
			runOnEDT( () ->
			{
				ButtonGroup group = new ButtonGroup();
				assertDoesNotThrow( () -> JavaUtils.removeAllButtonsFromGroup( group ) );
				assertEquals( 0, getButtonCount( group ) );
			} );
		}
		
		@Test
		@DisplayName( "Should remove single button from group" )
		void singleButton_removed()
		{
			runOnEDT( () ->
			{
				ButtonGroup group = new ButtonGroup();
				group.add( new JRadioButton( "Option 1" ) );
				assertEquals( 1, getButtonCount( group ) );
				
				JavaUtils.removeAllButtonsFromGroup( group );
				
				assertEquals( 0, getButtonCount( group ) );
			} );
		}
		
		@Test
		@DisplayName( "Should remove all buttons from group" )
		void multipleButtons_allRemoved()
		{
			runOnEDT( () ->
			{
				ButtonGroup group = new ButtonGroup();
				group.add( new JRadioButton( "Option 1" ) );
				group.add( new JRadioButton( "Option 2" ) );
				group.add( new JRadioButton( "Option 3" ) );
				assertEquals( 3, getButtonCount( group ) );
				
				JavaUtils.removeAllButtonsFromGroup( group );
				
				assertEquals( 0, getButtonCount( group ) );
			} );
		}
		
		@Test
		@DisplayName( "Should clear selection after removal" )
		void selectedButton_selectionCleared()
		{
			runOnEDT( () ->
			{
				ButtonGroup group = new ButtonGroup();
				JRadioButton button1 = new JRadioButton( "Option 1" );
				JRadioButton button2 = new JRadioButton( "Option 2" );
				group.add( button1 );
				group.add( button2 );
				button1.setSelected( true );
				assertNotNull( group.getSelection() );
				
				JavaUtils.removeAllButtonsFromGroup( group );
				
				assertNull( group.getSelection() );
			} );
		}
		
		@Test
		@DisplayName( "Should handle mixed button types" )
		void mixedButtonTypes_allRemoved()
		{
			runOnEDT( () ->
			{
				ButtonGroup group = new ButtonGroup();
				group.add( new JRadioButton( "Radio" ) );
				group.add( new JToggleButton( "Toggle" ) );
				group.add( new JCheckBox( "Check" ) );
				assertEquals( 3, getButtonCount( group ) );
				
				JavaUtils.removeAllButtonsFromGroup( group );
				
				assertEquals( 0, getButtonCount( group ) );
			} );
		}
		
		@Test
		@DisplayName( "Should allow group reuse after removal" )
		void afterRemoval_groupCanBeReused()
		{
			runOnEDT( () ->
			{
				ButtonGroup group = new ButtonGroup();
				group.add( new JRadioButton( "Old" ) );
				JavaUtils.removeAllButtonsFromGroup( group );
				
				JRadioButton newButton1 = new JRadioButton( "New 1" );
				JRadioButton newButton2 = new JRadioButton( "New 2" );
				group.add( newButton1 );
				group.add( newButton2 );
				
				assertEquals( 2, getButtonCount( group ) );
				
				// Verify mutual exclusion still works
				newButton1.setSelected( true );
				newButton2.setSelected( true );
				assertFalse( newButton1.isSelected() );
				assertTrue( newButton2.isSelected() );
			} );
		}
		
		@Test
		@DisplayName( "Should be safe to call multiple times" )
		void multipleCalls_safe()
		{
			runOnEDT( () ->
			{
				ButtonGroup group = new ButtonGroup();
				group.add( new JRadioButton( "Option 1" ) );
				
				JavaUtils.removeAllButtonsFromGroup( group );
				JavaUtils.removeAllButtonsFromGroup( group );
				JavaUtils.removeAllButtonsFromGroup( group );
				
				assertEquals( 0, getButtonCount( group ) );
			} );
		}
	}
}