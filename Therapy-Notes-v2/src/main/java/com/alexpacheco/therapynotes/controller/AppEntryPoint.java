package com.alexpacheco.therapynotes.controller;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.alexpacheco.therapynotes.controller.exceptions.TherapyAppException;
import com.alexpacheco.therapynotes.install.SetupConfigurationManager;
import com.alexpacheco.therapynotes.install.SetupWizardDialog;
import com.alexpacheco.therapynotes.util.AppLogger;

public class AppEntryPoint
{
	public static void main( String[] args )
	{
		SwingUtilities.invokeLater( () ->
		{
			_showSetupIfNeeded();
			_initializeDatabase();
			
			AppLogger.info( "DB configured at: " + SetupConfigurationManager.loadConfiguration().getDatabasePath() );
			
			_initializeDefaultPreferences();
			
			AppController.launchMainWindow();
		} );
	}
	
	private static void _showSetupIfNeeded()
	{
		if( !SetupConfigurationManager.isSetupComplete() )
		{
			try
			{
				UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
			}
			catch( Exception e )
			{
			}
			
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
	
	private static void _initializeDatabase()
	{
		try
		{
			DatabaseInitializer.initDb();
		}
		catch( TherapyAppException e )
		{
			AppController.showBasicErrorPopup( e, "Critical database initialization error:" );
			AppLogger.logShutdown();
			System.exit( 0 );
		}
	}
	
	private static void _initializeDefaultPreferences()
	{
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
