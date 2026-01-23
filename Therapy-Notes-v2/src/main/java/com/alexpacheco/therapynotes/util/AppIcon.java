package com.alexpacheco.therapynotes.util;

import java.awt.Image;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import java.net.URL;

/**
 * Utility class for loading and applying the application icon. Provides icons at multiple resolutions for optimal display across different
 * contexts (title bar, taskbar, alt-tab, etc.).
 */
public final class AppIcon
{
	// Icon resource path (relative to classpath)
	private static final String ICON_BASE_PATH = "/icons/app_icon";
	
	// Standard sizes for multi-resolution icon support
	private static final int[] ICON_SIZES = { 16, 32, 48, 64, 128, 256 };
	
	// Cached icon images
	private static List<Image> iconImages;
	private static Image primaryIcon;
	
	// Prevent instantiation
	private AppIcon()
	{
		throw new UnsupportedOperationException( "Utility class cannot be instantiated" );
	}
	
	/**
	 * Applies the application icon to a JFrame. Uses multiple icon sizes for best appearance at different resolutions.
	 * 
	 * @param frame The frame to apply the icon to
	 */
	public static void apply( JFrame frame )
	{
		if( frame == null )
		{
			return;
		}
		
		List<Image> icons = getIconImages();
		if( !icons.isEmpty() )
		{
			frame.setIconImages( icons );
		}
	}
	
	/**
	 * Applies the application icon to any Window.
	 * 
	 * @param window The window to apply the icon to
	 */
	public static void apply( Window window )
	{
		if( window == null )
		{
			return;
		}
		
		List<Image> icons = getIconImages();
		if( !icons.isEmpty() )
		{
			window.setIconImages( icons );
		}
	}
	
	/**
	 * Returns the primary application icon (typically 64x64 or largest available). Useful for About dialogs or other single-icon contexts.
	 * 
	 * @return The primary icon image, or null if not found
	 */
	public static Image getPrimaryIcon()
	{
		if( primaryIcon == null )
		{
			// Try to load 64px first, then fall back to any available
			primaryIcon = loadIcon( 256 );
			if( primaryIcon == null )
			{
				List<Image> icons = getIconImages();
				if( !icons.isEmpty() )
				{
					primaryIcon = icons.get( icons.size() - 1 ); // Largest available
				}
			}
		}
		return primaryIcon;
	}
	
	/**
	 * Returns all available icon images at different resolutions. Images are cached after first load.
	 * 
	 * @return List of icon images, empty if none found
	 */
	public static List<Image> getIconImages()
	{
		if( iconImages == null )
		{
			iconImages = new ArrayList<>();
			
			for( int size : ICON_SIZES )
			{
				Image icon = loadIcon( size );
				if( icon != null )
				{
					iconImages.add( icon );
				}
			}
			
			// If no sized icons found, try loading a single default icon
			if( iconImages.isEmpty() )
			{
				Image defaultIcon = loadIcon( "app_icon.png" );
				if( defaultIcon != null )
				{
					iconImages.add( defaultIcon );
				}
			}
		}
		return iconImages;
	}
	
	/**
	 * Loads an icon at the specified size.
	 * 
	 * @param size The icon size (e.g., 16, 32, 64)
	 * @return The loaded image, or null if not found
	 */
	private static Image loadIcon( int size )
	{
		String resourcePath = ICON_BASE_PATH + "_" + size + ".png";
		return loadIcon( resourcePath );
	}
	
	/**
	 * Loads an icon from the specified resource path.
	 * 
	 * @param resourcePath Path relative to classpath
	 * @return The loaded image, or null if not found
	 */
	private static Image loadIcon( String resourcePath )
	{
		// Ensure path starts with /
		if( !resourcePath.startsWith( "/" ) )
		{
			resourcePath = "/icons/" + resourcePath;
		}
		
		URL iconUrl = AppIcon.class.getResource( resourcePath );
		if( iconUrl != null )
		{
			return new ImageIcon( iconUrl ).getImage();
		}
		return null;
	}
	
	/**
	 * Returns an ImageIcon for use in Swing components (buttons, labels, etc.).
	 * 
	 * @param size Desired icon size
	 * @return ImageIcon at requested size, or null if not found
	 */
	public static ImageIcon getImageIcon( int size )
	{
		Image image = loadIcon( size );
		return image != null ? new ImageIcon( image ) : null;
	}
}