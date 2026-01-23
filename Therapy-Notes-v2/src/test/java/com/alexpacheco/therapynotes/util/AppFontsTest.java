package com.alexpacheco.therapynotes.util;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Font;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link AppFonts} utility class.
 * 
 * Tests cover font creation, caching behavior, component styling, and UIManager integration.
 */
@DisplayName( "AppFonts" )
class AppFontsTest
{
	// Expected sizes (matching the private constants in AppFonts)
	private static final int EXPECTED_TITLE_SIZE = 26;
	private static final int EXPECTED_TEXT_FIELD_SIZE = 14;
	private static final int EXPECTED_LABEL_SIZE = 13;
	private static final int EXPECTED_HEADER_SIZE = 16;
	private static final int EXPECTED_SMALL_SIZE = 11;
	
	@Nested
	@DisplayName( "Font getter methods" )
	class FontGetters
	{
		@Test
		@DisplayName( "getScreenTitleFont() returns non-null font" )
		void getScreenTitleFont_returnsNonNull()
		{
			Font font = AppFonts.getScreenTitleFont();
			assertNotNull( font );
		}
		
		@Test
		@DisplayName( "getScreenTitleFont() returns bold font with correct size" )
		void getScreenTitleFont_hasCorrectProperties()
		{
			Font font = AppFonts.getScreenTitleFont();
			assertEquals( EXPECTED_TITLE_SIZE, font.getSize() );
			assertTrue( font.isBold(), "Title font should be bold" );
		}
		
		@Test
		@DisplayName( "getTextFieldFont() returns non-null font" )
		void getTextFieldFont_returnsNonNull()
		{
			Font font = AppFonts.getTextFieldFont();
			assertNotNull( font );
		}
		
		@Test
		@DisplayName( "getTextFieldFont() returns plain font with correct size" )
		void getTextFieldFont_hasCorrectProperties()
		{
			Font font = AppFonts.getTextFieldFont();
			assertEquals( EXPECTED_TEXT_FIELD_SIZE, font.getSize() );
			assertTrue( font.isPlain(), "Text field font should be plain" );
		}
		
		@Test
		@DisplayName( "getTextFieldBoldFont() returns bold font with correct size" )
		void getTextFieldBoldFont_hasCorrectProperties()
		{
			Font font = AppFonts.getTextFieldBoldFont();
			assertEquals( EXPECTED_TEXT_FIELD_SIZE, font.getSize() );
			assertTrue( font.isBold(), "Text field bold font should be bold" );
		}
		
		@Test
		@DisplayName( "getLabelFont() returns plain font with correct size" )
		void getLabelFont_hasCorrectProperties()
		{
			Font font = AppFonts.getLabelFont();
			assertEquals( EXPECTED_LABEL_SIZE, font.getSize() );
			assertTrue( font.isPlain(), "Label font should be plain" );
		}
		
		@Test
		@DisplayName( "getLabelBoldFont() returns bold font with correct size" )
		void getLabelBoldFont_hasCorrectProperties()
		{
			Font font = AppFonts.getLabelBoldFont();
			assertEquals( EXPECTED_LABEL_SIZE, font.getSize() );
			assertTrue( font.isBold(), "Label bold font should be bold" );
		}
		
		@Test
		@DisplayName( "getHeaderFont() returns bold font with correct size" )
		void getHeaderFont_hasCorrectProperties()
		{
			Font font = AppFonts.getHeaderFont();
			assertEquals( EXPECTED_HEADER_SIZE, font.getSize() );
			assertTrue( font.isBold(), "Header font should be bold" );
		}
		
		@Test
		@DisplayName( "getSmallFont() returns plain font with correct size" )
		void getSmallFont_hasCorrectProperties()
		{
			Font font = AppFonts.getSmallFont();
			assertEquals( EXPECTED_SMALL_SIZE, font.getSize() );
			assertTrue( font.isPlain(), "Small font should be plain" );
		}
	}
	
	@Nested
	@DisplayName( "Font caching" )
	class FontCaching
	{
		@Test
		@DisplayName( "getScreenTitleFont() returns same instance on repeated calls" )
		void screenTitleFont_isCached()
		{
			Font first = AppFonts.getScreenTitleFont();
			Font second = AppFonts.getScreenTitleFont();
			assertSame( first, second, "Should return cached instance" );
		}
		
		@Test
		@DisplayName( "getTextFieldFont() returns same instance on repeated calls" )
		void textFieldFont_isCached()
		{
			Font first = AppFonts.getTextFieldFont();
			Font second = AppFonts.getTextFieldFont();
			assertSame( first, second, "Should return cached instance" );
		}
		
		@Test
		@DisplayName( "getTextFieldBoldFont() returns same instance on repeated calls" )
		void textFieldBoldFont_isCached()
		{
			Font first = AppFonts.getTextFieldBoldFont();
			Font second = AppFonts.getTextFieldBoldFont();
			assertSame( first, second, "Should return cached instance" );
		}
		
		@Test
		@DisplayName( "getLabelFont() returns same instance on repeated calls" )
		void labelFont_isCached()
		{
			Font first = AppFonts.getLabelFont();
			Font second = AppFonts.getLabelFont();
			assertSame( first, second, "Should return cached instance" );
		}
		
		@Test
		@DisplayName( "getLabelBoldFont() returns same instance on repeated calls" )
		void labelBoldFont_isCached()
		{
			Font first = AppFonts.getLabelBoldFont();
			Font second = AppFonts.getLabelBoldFont();
			assertSame( first, second, "Should return cached instance" );
		}
		
		@Test
		@DisplayName( "getHeaderFont() returns same instance on repeated calls" )
		void headerFont_isCached()
		{
			Font first = AppFonts.getHeaderFont();
			Font second = AppFonts.getHeaderFont();
			assertSame( first, second, "Should return cached instance" );
		}
		
		@Test
		@DisplayName( "getSmallFont() returns same instance on repeated calls" )
		void smallFont_isCached()
		{
			Font first = AppFonts.getSmallFont();
			Font second = AppFonts.getSmallFont();
			assertSame( first, second, "Should return cached instance" );
		}
	}
	
	@Nested
	@DisplayName( "createFont()" )
	class CreateFont
	{
		@Test
		@DisplayName( "Creates plain font with specified size" )
		void createFont_plain()
		{
			Font font = AppFonts.createFont( Font.PLAIN, 20 );
			assertNotNull( font );
			assertEquals( 20, font.getSize() );
			assertTrue( font.isPlain() );
		}
		
		@Test
		@DisplayName( "Creates bold font with specified size" )
		void createFont_bold()
		{
			Font font = AppFonts.createFont( Font.BOLD, 18 );
			assertNotNull( font );
			assertEquals( 18, font.getSize() );
			assertTrue( font.isBold() );
		}
		
		@Test
		@DisplayName( "Creates italic font with specified size" )
		void createFont_italic()
		{
			Font font = AppFonts.createFont( Font.ITALIC, 12 );
			assertNotNull( font );
			assertEquals( 12, font.getSize() );
			assertTrue( font.isItalic() );
		}
		
		@Test
		@DisplayName( "Creates bold italic font with specified size" )
		void createFont_boldItalic()
		{
			Font font = AppFonts.createFont( Font.BOLD | Font.ITALIC, 16 );
			assertNotNull( font );
			assertEquals( 16, font.getSize() );
			assertTrue( font.isBold(), "Should be bold" );
			assertTrue( font.isItalic(), "Should be italic" );
		}
		
		@Test
		@DisplayName( "Creates different font instances for different parameters" )
		void createFont_returnsNewInstances()
		{
			Font font1 = AppFonts.createFont( Font.PLAIN, 12 );
			Font font2 = AppFonts.createFont( Font.PLAIN, 12 );
			// createFont should create new instances (not cached)
			assertNotSame( font1, font2, "createFont should return new instances" );
			assertEquals( font1, font2, "Fonts with same params should be equal" );
		}
		
		@Test
		@DisplayName( "Handles edge case sizes" )
		void createFont_edgeSizes()
		{
			Font tiny = AppFonts.createFont( Font.PLAIN, 1 );
			assertEquals( 1, tiny.getSize() );
			
			Font large = AppFonts.createFont( Font.PLAIN, 100 );
			assertEquals( 100, large.getSize() );
		}
	}
	
	@Nested
	@DisplayName( "applyTextFieldFont()" )
	class ApplyTextFieldFont
	{
		@Test
		@DisplayName( "Applies font to JTextField" )
		void applyTextFieldFont_toTextField()
		{
			JTextField textField = new JTextField();
			AppFonts.applyTextFieldFont( textField );
			assertEquals( AppFonts.getTextFieldFont(), textField.getFont() );
		}
		
		@Test
		@DisplayName( "Applies font to JTextArea" )
		void applyTextFieldFont_toTextArea()
		{
			JTextArea textArea = new JTextArea();
			AppFonts.applyTextFieldFont( textArea );
			assertEquals( AppFonts.getTextFieldFont(), textArea.getFont() );
		}
		
		@Test
		@DisplayName( "Handles null component gracefully" )
		void applyTextFieldFont_nullSafe()
		{
			// Should not throw
			assertDoesNotThrow( () -> AppFonts.applyTextFieldFont( (JTextField) null ) );
		}
		
		@Test
		@DisplayName( "Applies font to multiple components via varargs" )
		void applyTextFieldFont_multipleComponents()
		{
			JTextField field1 = new JTextField();
			JTextField field2 = new JTextField();
			JComboBox<String> combo = new JComboBox<>();
			
			AppFonts.applyTextFieldFont( field1, field2, combo );
			
			Font expected = AppFonts.getTextFieldFont();
			assertEquals( expected, field1.getFont() );
			assertEquals( expected, field2.getFont() );
			assertEquals( expected, combo.getFont() );
		}
		
		@Test
		@DisplayName( "Varargs handles null elements gracefully" )
		void applyTextFieldFont_varargs_nullSafe()
		{
			JTextField field = new JTextField();
			// Should not throw when array contains null
			assertDoesNotThrow( () -> AppFonts.applyTextFieldFont( field, null, new JTextField() ) );
			assertEquals( AppFonts.getTextFieldFont(), field.getFont() );
		}
		
		@Test
		@DisplayName( "Varargs handles empty array" )
		void applyTextFieldFont_emptyArray()
		{
			assertDoesNotThrow( () -> AppFonts.applyTextFieldFont() );
		}
	}
	
	@Nested
	@DisplayName( "applyLabelFont()" )
	class ApplyLabelFont
	{
		@Test
		@DisplayName( "Applies label font to component" )
		void applyLabelFont_appliesFont()
		{
			JLabel label = new JLabel();
			AppFonts.applyLabelFont( label );
			assertEquals( AppFonts.getLabelFont(), label.getFont() );
		}
		
		@Test
		@DisplayName( "Handles null component gracefully" )
		void applyLabelFont_nullSafe()
		{
			assertDoesNotThrow( () -> AppFonts.applyLabelFont( null ) );
		}
	}
	
	@Nested
	@DisplayName( "applyHeaderFont()" )
	class ApplyHeaderFont
	{
		@Test
		@DisplayName( "Applies header font to component" )
		void applyHeaderFont_appliesFont()
		{
			JLabel header = new JLabel();
			AppFonts.applyHeaderFont( header );
			assertEquals( AppFonts.getHeaderFont(), header.getFont() );
		}
		
		@Test
		@DisplayName( "Handles null component gracefully" )
		void applyHeaderFont_nullSafe()
		{
			assertDoesNotThrow( () -> AppFonts.applyHeaderFont( null ) );
		}
	}
	
	@Nested
	@DisplayName( "setUIFonts()" )
	class SetUIFonts
	{
		@Test
		@DisplayName( "Sets UIManager defaults for common components" )
		void setUIFonts_setsDefaults()
		{
			AppFonts.setUIFonts();
			
			assertEquals( AppFonts.getLabelFont(), UIManager.getFont( "Button.font" ) );
			assertEquals( AppFonts.getLabelFont(), UIManager.getFont( "Label.font" ) );
			assertEquals( AppFonts.getTextFieldFont(), UIManager.getFont( "TextField.font" ) );
			assertEquals( AppFonts.getTextFieldFont(), UIManager.getFont( "TextArea.font" ) );
			assertEquals( AppFonts.getLabelFont(), UIManager.getFont( "CheckBox.font" ) );
			assertEquals( AppFonts.getLabelFont(), UIManager.getFont( "RadioButton.font" ) );
			assertEquals( AppFonts.getLabelFont(), UIManager.getFont( "ComboBox.font" ) );
			assertEquals( AppFonts.getLabelFont(), UIManager.getFont( "Menu.font" ) );
			assertEquals( AppFonts.getLabelFont(), UIManager.getFont( "MenuItem.font" ) );
			assertEquals( AppFonts.getLabelFont(), UIManager.getFont( "Table.font" ) );
			assertEquals( AppFonts.getTextFieldFont(), UIManager.getFont( "TableHeader.font" ) );
			assertEquals( AppFonts.getLabelFont(), UIManager.getFont( "TitledBorder.font" ) );
		}
	}
	
	@Nested
	@DisplayName( "Utility class design" )
	class UtilityClassDesign
	{
		@Test
		@DisplayName( "Constructor throws UnsupportedOperationException" )
		void constructor_throwsException() throws Exception
		{
			Constructor<AppFonts> constructor = AppFonts.class.getDeclaredConstructor();
			constructor.setAccessible( true );
			
			InvocationTargetException thrown = assertThrows( InvocationTargetException.class, () -> constructor.newInstance() );
			
			assertInstanceOf( UnsupportedOperationException.class, thrown.getCause() );
		}
	}
	
	@Nested
	@DisplayName( "Font family consistency" )
	class FontFamilyConsistency
	{
		@Test
		@DisplayName( "All fonts use the same family" )
		void allFonts_useSameFamily()
		{
			String family = AppFonts.getTextFieldFont().getFamily();
			
			assertEquals( family, AppFonts.getScreenTitleFont().getFamily() );
			assertEquals( family, AppFonts.getTextFieldBoldFont().getFamily() );
			assertEquals( family, AppFonts.getLabelFont().getFamily() );
			assertEquals( family, AppFonts.getLabelBoldFont().getFamily() );
			assertEquals( family, AppFonts.getHeaderFont().getFamily() );
			assertEquals( family, AppFonts.getSmallFont().getFamily() );
		}
		
		@Test
		@DisplayName( "Created fonts use the same family as standard fonts" )
		void createdFonts_useSameFamily()
		{
			String expectedFamily = AppFonts.getTextFieldFont().getFamily();
			Font created = AppFonts.createFont( Font.PLAIN, 20 );
			assertEquals( expectedFamily, created.getFamily() );
		}
	}
	
	@Nested
	@DisplayName( "Font size hierarchy" )
	class FontSizeHierarchy
	{
		@Test
		@DisplayName( "Font sizes follow expected hierarchy" )
		void fontSizes_followHierarchy()
		{
			// Title should be largest
			assertTrue( AppFonts.getScreenTitleFont().getSize() > AppFonts.getHeaderFont().getSize(),
					"Title should be larger than header" );
			
			// Header should be larger than text field
			assertTrue( AppFonts.getHeaderFont().getSize() > AppFonts.getTextFieldFont().getSize(),
					"Header should be larger than text field" );
			
			// Text field should be larger than or equal to label
			assertTrue( AppFonts.getTextFieldFont().getSize() >= AppFonts.getLabelFont().getSize(), "Text field should be >= label size" );
			
			// Label should be larger than small
			assertTrue( AppFonts.getLabelFont().getSize() > AppFonts.getSmallFont().getSize(), "Label should be larger than small" );
		}
	}
}