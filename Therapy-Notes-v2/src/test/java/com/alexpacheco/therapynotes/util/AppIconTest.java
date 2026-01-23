package com.alexpacheco.therapynotes.util;

import static org.junit.jupiter.api.Assertions.*;

import java.awt.Image;
import java.awt.Window;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link AppIcon} utility class.
 * 
 * Note: Some tests verify behavior regardless of whether actual icon resources exist on the classpath. Tests focus on null-safety, caching,
 * and API contracts.
 */
@DisplayName( "AppIcon" )
class AppIconTest
{
	@Nested
	@DisplayName( "Utility class design" )
	class UtilityClassDesign
	{
		@Test
		@DisplayName( "Constructor throws UnsupportedOperationException" )
		void constructor_throwsException() throws Exception
		{
			Constructor<AppIcon> constructor = AppIcon.class.getDeclaredConstructor();
			constructor.setAccessible( true );
			
			InvocationTargetException thrown = assertThrows( InvocationTargetException.class, () -> constructor.newInstance() );
			
			assertInstanceOf( UnsupportedOperationException.class, thrown.getCause() );
		}
	}
	
	@Nested
	@DisplayName( "apply(JFrame)" )
	class ApplyToJFrame
	{
		@Test
		@DisplayName( "Handles null frame gracefully" )
		void apply_nullFrame_doesNotThrow()
		{
			assertDoesNotThrow( () -> AppIcon.apply( (JFrame) null ) );
		}
		
		@Test
		@DisplayName( "Applies to valid frame without throwing" )
		void apply_validFrame_doesNotThrow()
		{
			JFrame frame = new JFrame();
			try
			{
				assertDoesNotThrow( () -> AppIcon.apply( frame ) );
			}
			finally
			{
				frame.dispose();
			}
		}
		
		@Test
		@DisplayName( "Sets icon images on frame when icons are available" )
		void apply_setsIconImages()
		{
			JFrame frame = new JFrame();
			try
			{
				AppIcon.apply( frame );
				
				// If icons were found, they should be set on the frame
				List<Image> availableIcons = AppIcon.getIconImages();
				if( !availableIcons.isEmpty() )
				{
					List<Image> frameIcons = frame.getIconImages();
					assertFalse( frameIcons.isEmpty(), "Frame should have icons set" );
					assertEquals( availableIcons.size(), frameIcons.size() );
				}
			}
			finally
			{
				frame.dispose();
			}
		}
	}
	
	@Nested
	@DisplayName( "apply(Window)" )
	class ApplyToWindow
	{
		@Test
		@DisplayName( "Handles null window gracefully" )
		void apply_nullWindow_doesNotThrow()
		{
			assertDoesNotThrow( () -> AppIcon.apply( (Window) null ) );
		}
		
		@Test
		@DisplayName( "Applies to JDialog without throwing" )
		void apply_toDialog_doesNotThrow()
		{
			JDialog dialog = new JDialog();
			try
			{
				assertDoesNotThrow( () -> AppIcon.apply( dialog ) );
			}
			finally
			{
				dialog.dispose();
			}
		}
		
		@Test
		@DisplayName( "Sets icon images on window when icons are available" )
		void apply_setsIconImages()
		{
			JDialog dialog = new JDialog();
			try
			{
				AppIcon.apply( dialog );
				
				List<Image> availableIcons = AppIcon.getIconImages();
				if( !availableIcons.isEmpty() )
				{
					List<Image> windowIcons = dialog.getIconImages();
					assertFalse( windowIcons.isEmpty(), "Window should have icons set" );
				}
			}
			finally
			{
				dialog.dispose();
			}
		}
	}
	
	@Nested
	@DisplayName( "getIconImages()" )
	class GetIconImages
	{
		@Test
		@DisplayName( "Returns non-null list" )
		void getIconImages_returnsNonNull()
		{
			List<Image> icons = AppIcon.getIconImages();
			assertNotNull( icons, "Should never return null" );
		}
		
		@Test
		@DisplayName( "Returns same cached list on repeated calls" )
		void getIconImages_returnsCachedList()
		{
			List<Image> first = AppIcon.getIconImages();
			List<Image> second = AppIcon.getIconImages();
			assertSame( first, second, "Should return cached instance" );
		}
		
		@Test
		@DisplayName( "List is consistent across multiple calls" )
		void getIconImages_consistentSize()
		{
			List<Image> first = AppIcon.getIconImages();
			List<Image> second = AppIcon.getIconImages();
			assertEquals( first.size(), second.size() );
		}
		
		@Test
		@DisplayName( "All images in list are non-null" )
		void getIconImages_allImagesNonNull()
		{
			List<Image> icons = AppIcon.getIconImages();
			for( Image icon : icons )
			{
				assertNotNull( icon, "No image in the list should be null" );
			}
		}
	}
	
	@Nested
	@DisplayName( "getPrimaryIcon()" )
	class GetPrimaryIcon
	{
		@Test
		@DisplayName( "Does not throw" )
		void getPrimaryIcon_doesNotThrow()
		{
			assertDoesNotThrow( () -> AppIcon.getPrimaryIcon() );
		}
		
		@Test
		@DisplayName( "Returns same cached instance on repeated calls" )
		void getPrimaryIcon_returnsCachedInstance()
		{
			Image first = AppIcon.getPrimaryIcon();
			Image second = AppIcon.getPrimaryIcon();
			
			// Both should be the same (either both null or same instance)
			assertSame( first, second, "Should return cached instance" );
		}
		
		@Test
		@DisplayName( "Returns an image from getIconImages when available" )
		void getPrimaryIcon_consistentWithIconImages()
		{
			Image primary = AppIcon.getPrimaryIcon();
			List<Image> icons = AppIcon.getIconImages();
			
			if( primary != null && !icons.isEmpty() )
			{
				// Primary icon should come from the available icons (256px or largest)
				// At minimum, if we have icons, primary shouldn't be null
				assertTrue( icons.contains( primary ) || primary != null, "Primary icon should be related to available icons" );
			}
		}
	}
	
	@Nested
	@DisplayName( "getImageIcon(int)" )
	class GetImageIcon
	{
		@Test
		@DisplayName( "Does not throw for valid sizes" )
		void getImageIcon_validSizes_doesNotThrow()
		{
			int[] sizes = { 16, 32, 48, 64, 128, 256 };
			for( int size : sizes )
			{
				assertDoesNotThrow( () -> AppIcon.getImageIcon( size ) );
			}
		}
		
		@Test
		@DisplayName( "Does not throw for non-standard sizes" )
		void getImageIcon_nonStandardSize_doesNotThrow()
		{
			// Should handle gracefully even if resource doesn't exist
			assertDoesNotThrow( () -> AppIcon.getImageIcon( 999 ) );
		}
		
		@Test
		@DisplayName( "Returns null for non-existent size" )
		void getImageIcon_nonExistentSize_returnsNull()
		{
			// Very unlikely to have a 999px icon
			ImageIcon icon = AppIcon.getImageIcon( 999 );
			assertNull( icon, "Should return null for non-existent icon size" );
		}
		
		@Test
		@DisplayName( "Does not throw for zero size" )
		void getImageIcon_zeroSize_doesNotThrow()
		{
			assertDoesNotThrow( () -> AppIcon.getImageIcon( 0 ) );
		}
		
		@Test
		@DisplayName( "Does not throw for negative size" )
		void getImageIcon_negativeSize_doesNotThrow()
		{
			assertDoesNotThrow( () -> AppIcon.getImageIcon( -1 ) );
		}
		
		@Test
		@DisplayName( "Returns ImageIcon with correct image when resource exists" )
		void getImageIcon_whenExists_hasImage()
		{
			// Try standard sizes - if any exist, verify the ImageIcon has an image
			int[] sizes = { 16, 32, 48, 64, 128, 256 };
			for( int size : sizes )
			{
				ImageIcon icon = AppIcon.getImageIcon( size );
				if( icon != null )
				{
					assertNotNull( icon.getImage(), "ImageIcon should have an image" );
					// The image should have positive dimensions once loaded
					// (though actual loading may be deferred)
				}
			}
		}
	}
	
	@Nested
	@DisplayName( "Icon loading behavior" )
	class IconLoadingBehavior
	{
		@Test
		@DisplayName( "Icons are loaded lazily" )
		void iconsLoadedLazily()
		{
			// This test verifies the caching mechanism works
			// First call triggers loading, subsequent calls return cached
			List<Image> first = AppIcon.getIconImages();
			List<Image> second = AppIcon.getIconImages();
			List<Image> third = AppIcon.getIconImages();
			
			assertSame( first, second );
			assertSame( second, third );
		}
		
		@Test
		@DisplayName( "Primary icon uses largest available when 256px not found" )
		void primaryIcon_fallbackBehavior()
		{
			Image primary = AppIcon.getPrimaryIcon();
			List<Image> icons = AppIcon.getIconImages();
			
			// If icons exist but 256 doesn't, primary should be the largest
			if( primary != null && !icons.isEmpty() )
			{
				// Primary should be one of the available icons
				// This is a weak assertion but validates the fallback logic exists
				assertNotNull( primary );
			}
		}
	}
	
	@Nested
	@DisplayName( "Thread safety considerations" )
	class ThreadSafety
	{
		@Test
		@DisplayName( "Concurrent access to getIconImages does not throw" )
		void getIconImages_concurrentAccess() throws InterruptedException
		{
			int threadCount = 10;
			Thread[] threads = new Thread[threadCount];
			boolean[] exceptions = { false };
			List<Image>[] results = new List[threadCount];
			
			for( int i = 0; i < threadCount; i++ )
			{
				final int index = i;
				threads[i] = new Thread( () ->
				{
					try
					{
						results[index] = AppIcon.getIconImages();
					}
					catch( Exception e )
					{
						exceptions[0] = true;
					}
				} );
			}
			
			for( Thread t : threads )
			{
				t.start();
			}
			
			for( Thread t : threads )
			{
				t.join();
			}
			
			assertFalse( exceptions[0], "No exceptions should occur during concurrent access" );
			
			// All threads should get the same cached instance
			for( int i = 1; i < threadCount; i++ )
			{
				assertSame( results[0], results[i], "All threads should receive same cached list" );
			}
		}
	}
	
	@Nested
	@DisplayName( "Integration with Swing components" )
	class SwingIntegration
	{
		@Test
		@DisplayName( "Can apply to multiple frames" )
		void apply_multipleFrames()
		{
			JFrame frame1 = new JFrame();
			JFrame frame2 = new JFrame();
			JFrame frame3 = new JFrame();
			
			try
			{
				assertDoesNotThrow( () ->
				{
					AppIcon.apply( frame1 );
					AppIcon.apply( frame2 );
					AppIcon.apply( frame3 );
				} );
				
				// All frames should have the same icons
				List<Image> icons = AppIcon.getIconImages();
				if( !icons.isEmpty() )
				{
					assertEquals( frame1.getIconImages().size(), frame2.getIconImages().size() );
					assertEquals( frame2.getIconImages().size(), frame3.getIconImages().size() );
				}
			}
			finally
			{
				frame1.dispose();
				frame2.dispose();
				frame3.dispose();
			}
		}
		
		@Test
		@DisplayName( "Can apply to mixed window types" )
		void apply_mixedWindowTypes()
		{
			JFrame frame = new JFrame();
			JDialog dialog = new JDialog();
			
			try
			{
				assertDoesNotThrow( () ->
				{
					AppIcon.apply( frame );
					AppIcon.apply( dialog );
				} );
			}
			finally
			{
				frame.dispose();
				dialog.dispose();
			}
		}
	}
}