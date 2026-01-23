package com.alexpacheco.therapynotes.util;

import java.awt.Font;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.text.JTextComponent;

/**
 * Centralized font definitions for consistent UI styling across the application. Provides standard fonts for text entry fields and other UI
 * components.
 */
public final class AppFonts
{
	// Font family - using a cross-platform safe font
	private static final String DEFAULT_FAMILY = "Segoe UI";
	private static final String FALLBACK_FAMILY = Font.SANS_SERIF;
	
	// Standard sizes
	private static final int TEXT_FIELD_SIZE = 14;
	private static final int LABEL_SIZE = 13;
	private static final int HEADER_SIZE = 16;
	private static final int SMALL_SIZE = 11;
	
	// Cached font instances for reuse
	private static Font textFieldFont;
	private static Font textFieldBoldFont;
	private static Font labelFont;
	private static Font labelBoldFont;
	private static Font headerFont;
	private static Font smallFont;
	
	// Prevent instantiation
	private AppFonts()
	{
		throw new UnsupportedOperationException( "Utility class cannot be instantiated" );
	}
	
	/**
	 * Set standard fonts across UI
	 */
	public static void setUIFonts()
	{
		UIManager.put( "Button.font", getLabelFont() );
		UIManager.put( "Label.font", getLabelFont() );
		UIManager.put( "TextField.font", getTextFieldFont() );
		UIManager.put( "TextArea.font", getTextFieldFont() );
		UIManager.put( "CheckBox.font", getLabelFont() );
		UIManager.put( "RadioButton.font", getLabelFont() );
		UIManager.put( "ComboBox.font", getLabelFont() );
		UIManager.put( "Menu.font", getLabelFont() );
		UIManager.put( "MenuItem.font", getLabelFont() );
		UIManager.put( "Table.font", getLabelFont() );
		UIManager.put( "TableHeader.font", getTextFieldFont() );
		UIManager.put( "TitledBorder.font", getLabelFont() );
	}
	
	/**
	 * Returns the standard font for text entry fields (JTextField, JTextArea, JPasswordField). Uses Segoe UI 14pt plain, with fallback to
	 * system sans-serif.
	 */
	public static Font getTextFieldFont()
	{
		if( textFieldFont == null )
		{
			textFieldFont = createFont( Font.PLAIN, TEXT_FIELD_SIZE );
		}
		return textFieldFont;
	}
	
	/**
	 * Returns a bold variant of the text field font.
	 */
	public static Font getTextFieldBoldFont()
	{
		if( textFieldBoldFont == null )
		{
			textFieldBoldFont = createFont( Font.BOLD, TEXT_FIELD_SIZE );
		}
		return textFieldBoldFont;
	}
	
	/**
	 * Returns the standard font for labels.
	 */
	public static Font getLabelFont()
	{
		if( labelFont == null )
		{
			labelFont = createFont( Font.PLAIN, LABEL_SIZE );
		}
		return labelFont;
	}
	
	/**
	 * Returns a bold variant of the label font.
	 */
	public static Font getLabelBoldFont()
	{
		if( labelBoldFont == null )
		{
			labelBoldFont = createFont( Font.BOLD, LABEL_SIZE );
		}
		return labelBoldFont;
	}
	
	/**
	 * Returns the standard font for headers and titles.
	 */
	public static Font getHeaderFont()
	{
		if( headerFont == null )
		{
			headerFont = createFont( Font.BOLD, HEADER_SIZE );
		}
		return headerFont;
	}
	
	/**
	 * Returns a smaller font for captions, hints, or secondary text.
	 */
	public static Font getSmallFont()
	{
		if( smallFont == null )
		{
			smallFont = createFont( Font.PLAIN, SMALL_SIZE );
		}
		return smallFont;
	}
	
	/**
	 * Creates a custom font with the specified style and size. Uses the application's default font family.
	 * 
	 * @param style Font.PLAIN, Font.BOLD, Font.ITALIC, or Font.BOLD | Font.ITALIC
	 * @param size  Font size in points
	 * @return A new Font instance
	 */
	public static Font createFont( int style, int size )
	{
		Font font = new Font( DEFAULT_FAMILY, style, size );
		// Check if the font family was found, fall back if necessary
		if( !font.getFamily().equalsIgnoreCase( DEFAULT_FAMILY ) )
		{
			font = new Font( FALLBACK_FAMILY, style, size );
		}
		return font;
	}
	
	/**
	 * Applies the standard text field font to a JTextComponent.
	 * 
	 * @param component The text component to style
	 */
	public static void applyTextFieldFont( JTextComponent component )
	{
		if( component != null )
		{
			component.setFont( getTextFieldFont() );
		}
	}
	
	/**
	 * Applies the standard text field font to multiple components at once. Supports JTextComponent, JComboBox, and JSpinner.
	 * 
	 * @param components The components to style
	 */
	public static void applyTextFieldFont( JComponent... components )
	{
		Font font = getTextFieldFont();
		for( JComponent component : components )
		{
			if( component != null )
			{
				component.setFont( font );
			}
		}
	}
	
	/**
	 * Applies the label font to a component.
	 * 
	 * @param component The component to style
	 */
	public static void applyLabelFont( JComponent component )
	{
		if( component != null )
		{
			component.setFont( getLabelFont() );
		}
	}
	
	/**
	 * Applies the header font to a component.
	 * 
	 * @param component The component to style
	 */
	public static void applyHeaderFont( JComponent component )
	{
		if( component != null )
		{
			component.setFont( getHeaderFont() );
		}
	}
}