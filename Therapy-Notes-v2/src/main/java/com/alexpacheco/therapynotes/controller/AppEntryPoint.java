package com.alexpacheco.therapynotes.controller;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.alexpacheco.therapynotes.controller.enums.LogLevel;
import com.alexpacheco.therapynotes.controller.errorhandling.exceptions.TherapyAppException;
import com.alexpacheco.therapynotes.install.SetupConfigurationManager;
import com.alexpacheco.therapynotes.install.SetupWizardDialog;

public class AppEntryPoint
{
	public static void main( String[] args )
	{
		AppController.initializeSession();
		
		SwingUtilities.invokeLater( () ->
		{
			_showSetupIfNeeded();
			_initializeDatabase();
			
			AppController.logToDatabase( LogLevel.INFO, "AppEntryPoint", "Application started" );
			AppController.logToDatabase( LogLevel.INFO, "AppEntryPoint",
					"DB configured at: " + SetupConfigurationManager.loadConfiguration().getDatabasePath() );
			
			DatabaseInitializer.cleanupOldLogs( 90 );
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
