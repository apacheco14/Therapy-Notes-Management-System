package com.alexpacheco.therapynotes.controller;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.alexpacheco.therapynotes.controller.exceptions.TherapyAppException;
import com.alexpacheco.therapynotes.install.SetupConfigurationManager;
import com.alexpacheco.therapynotes.install.SetupWizardDialog;
import com.alexpacheco.therapynotes.security.SecureStorageException;
import com.alexpacheco.therapynotes.util.AppFonts;
import com.alexpacheco.therapynotes.util.AppLogger;
import com.alexpacheco.therapynotes.util.GlobalExceptionHandler;
import com.alexpacheco.therapynotes.view.dialogs.Dlg_PinEntry;

public class AppEntryPoint
{
	public static void main( String[] args )
	{
		GlobalExceptionHandler.install();
		
		SwingUtilities.invokeLater( () ->
		{
			try
			{
				PinManager.initialize();
				showSetupWizard();
				initializeDatabase();
				setLookAndFeel();
				
				if( PinManager.isPinConfigured() )
				{
					if( !Dlg_PinEntry.authenticate( null ) )
					{
						AppLogger.logShutdown();
						System.exit( 0 );
					}
				}
				
				AppController.launchMainWindow();
			}
			catch( SecureStorageException e )
			{
				// Critical failure - cannot secure credentials
				AppLogger.error(
						"Security Initialization Failed. This application requires secure credential storage which is not available on this system.",
						e );
				JOptionPane.showMessageDialog( null,
						"Security Initialization Failed\n\n" + "This application requires secure credential storage which is not\n"
								+ "available on this system.\n\n" + "Technical details: " + e.getMessage(),
						"Security Error", JOptionPane.ERROR_MESSAGE );
				System.exit( 1 );
			}
		} );
	}
	
	private static void showSetupWizard()
	{
		if( !SetupConfigurationManager.isSetupComplete() )
		{
			setLookAndFeel();
			
			boolean setupCompleted = SetupWizardDialog.showIfFirstRun( null );
			
			if( !setupCompleted )
			{
				// User cancelled setup
				int result = JOptionPane.showConfirmDialog( null,
						"Setup was not completed. Would you like to continue with default settings?", "Setup Incomplete",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE );
				
				if( result != JOptionPane.YES_OPTION )
				{
					AppLogger.logShutdown();
					System.exit( 0 );
					return;
				}
			}
		}
	}
	
	private static void setLookAndFeel()
	{
		try
		{
			UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
			AppFonts.setUIFonts();
		}
		catch( Exception e )
		{
			AppLogger.error( "Error setting look and feel.", e );
			AppController.showBasicErrorPopup( "Error setting look and feel." );
		}
	}
	
	private static void initializeDatabase()
	{
		try
		{
			DatabaseInitializer.initDb();
			AppLogger.info( "DB configured at: " + SetupConfigurationManager.loadConfiguration().getDatabasePath() );
		}
		catch( TherapyAppException e )
		{
			AppController.showBasicErrorPopup( e, "Critical database initialization error:" );
			AppLogger.logShutdown();
			System.exit( 1 );
		}
		
		try
		{
			DatabaseInitializer.initializeDefaultPreferences();
		}
		catch( TherapyAppException e )
		{
			AppController.showBasicErrorPopup( e, "Error initializing default preferences:" );
		}
	}
}
