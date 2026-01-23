package com.alexpacheco.therapynotes.view;

import javax.swing.*;

import com.alexpacheco.therapynotes.controller.Exporter;
import com.alexpacheco.therapynotes.controller.PinManager;
import com.alexpacheco.therapynotes.controller.enums.Screens;
import com.alexpacheco.therapynotes.model.entities.Note;
import com.alexpacheco.therapynotes.util.AppIcon;
import com.alexpacheco.therapynotes.util.AppLogger;
import com.alexpacheco.therapynotes.view.dialogs.Dlg_AddPin;
import com.alexpacheco.therapynotes.view.dialogs.Dlg_ChangePin;
import com.alexpacheco.therapynotes.view.screens.Pnl_About;
import com.alexpacheco.therapynotes.view.screens.Pnl_Configuration;
import com.alexpacheco.therapynotes.view.screens.Pnl_ExportNotes;
import com.alexpacheco.therapynotes.view.screens.Pnl_HelpIndex;
import com.alexpacheco.therapynotes.view.screens.Pnl_Home;
import com.alexpacheco.therapynotes.view.screens.Pnl_NewEditClient;
import com.alexpacheco.therapynotes.view.screens.Pnl_NewEditContact;
import com.alexpacheco.therapynotes.view.screens.Pnl_NewEditNote;
import com.alexpacheco.therapynotes.view.screens.Pnl_Preferences;
import com.alexpacheco.therapynotes.view.screens.Pnl_SearchClientsEdit;
import com.alexpacheco.therapynotes.view.screens.Pnl_SearchClientsView;
import com.alexpacheco.therapynotes.view.screens.Pnl_SearchContact;
import com.alexpacheco.therapynotes.view.screens.Pnl_SearchNote;
import com.alexpacheco.therapynotes.view.screens.Pnl_ViewClientDetails;
import com.alexpacheco.therapynotes.view.screens.Pnl_ViewLogs;

import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class MainWindow extends JFrame
{
	private static final long serialVersionUID = 5677463230460003165L;
	private JPanel mainPanel;
	private CardLayout cardLayout;
	private Pnl_SearchClientsView searchClientsViewPanel;
	private Pnl_SearchNote searchNotePanel;
	private Pnl_SearchClientsEdit searchClientsEditPanel;
	private Pnl_SearchContact searchContactPanel;
	private Pnl_Preferences preferencesPanel;
	private Pnl_NewEditNote newEditNotePanel;
	private Pnl_NewEditClient newEditClientPanel;
	private Pnl_NewEditContact newEditContactPanel;
	private Screens currentScreen;
	private JMenuBar menuBar = new JMenuBar();
	private JMenu settingsMenu = new JMenu( "Settings" );
	private JMenuItem addPinItem = new JMenuItem( "Add PIN" );
	private JMenuItem changePinItem = new JMenuItem( "Change PIN" );
	
	public MainWindow()
	{
		setTitle( "Notes Management System" );
		setSize( 1280, 730 );
		setDefaultCloseOperation( JFrame.DO_NOTHING_ON_CLOSE );
		setLocationRelativeTo( null );
		
		addWindowListener( new WindowAdapter()
		{
			@Override
			public void windowClosing( WindowEvent e )
			{
				_handleExit();
			}
		} );
		
		cardLayout = new CardLayout();
		mainPanel = new JPanel( cardLayout );
		
		_initializePanels();
		_createMenuBar();
		
		add( mainPanel );
		
		cardLayout.show( mainPanel, Screens.HOME.getPanelName() );
		currentScreen = Screens.HOME;
		
		AppIcon.apply( this );
	}
	
	private void _initializePanels()
	{
		newEditClientPanel = new Pnl_NewEditClient();
		Pnl_ViewClientDetails clientDetailsPanel = new Pnl_ViewClientDetails( cardLayout, mainPanel );
		searchClientsViewPanel = new Pnl_SearchClientsView( cardLayout, mainPanel, clientDetailsPanel );
		newEditContactPanel = new Pnl_NewEditContact();
		newEditNotePanel = new Pnl_NewEditNote();
		searchNotePanel = new Pnl_SearchNote();
		searchClientsEditPanel = new Pnl_SearchClientsEdit( cardLayout, mainPanel, newEditClientPanel );
		searchContactPanel = new Pnl_SearchContact( cardLayout, mainPanel, newEditContactPanel );
		preferencesPanel = new Pnl_Preferences();
		
		_addScreenPanel( new Pnl_Home(), Screens.HOME );
		_addScreenPanel( newEditNotePanel, Screens.NEW_EDIT_NOTE );
		_addScreenPanel( searchNotePanel, Screens.SEARCH_NOTES );
		_addScreenPanel( new Pnl_ExportNotes(), Screens.BULK_EXPORT_NOTES );
		_addScreenPanel( searchClientsViewPanel, Screens.SEARCH_CLIENTS_VIEW );
		_addScreenPanel( clientDetailsPanel, Screens.CLIENT_DETAILS );
		_addScreenPanel( newEditClientPanel, Screens.NEW_EDIT_CLIENT );
		_addScreenPanel( searchClientsEditPanel, Screens.SEARCH_CLIENTS_EDIT );
		_addScreenPanel( newEditContactPanel, Screens.NEW_EDIT_CONTACT );
		_addScreenPanel( searchContactPanel, Screens.SEARCH_CONTACTS );
		_addScreenPanel( preferencesPanel, Screens.PREFERENCES );
		_addScreenPanel( new Pnl_Configuration(), Screens.CONFIG );
		_addScreenPanel( new Pnl_HelpIndex(), Screens.HELP );
		_addScreenPanel( new Pnl_About(), Screens.ABOUT );
		_addScreenPanel( new Pnl_ViewLogs(), Screens.VIEW_LOGS );
	}
	
	private void _addScreenPanel( JPanel panel, Screens type )
	{
		mainPanel.add( panel, type.getPanelName() );
	}
	
	private JPanel _createPlaceholderPanel( String title )
	{
		JPanel panel = new JPanel( new BorderLayout() );
		JLabel label = new JLabel( title, SwingConstants.CENTER );
		label.setFont( new Font( "Arial", Font.BOLD, 24 ) );
		panel.add( label, BorderLayout.CENTER );
		return panel;
	}
	
	private void _createMenuBar()
	{
		JMenu notesMenu = new JMenu( "Notes" );
		JMenuItem homeItem = new JMenuItem( "Home" );
		JMenuItem newNoteItem = new JMenuItem( "New Note" );
		JMenuItem openNoteItem = new JMenuItem( "Open Note" );
		JMenuItem exportNotesItem = new JMenuItem( "Export Notes" );
		JMenuItem exitItem = new JMenuItem( "Exit" );
		
		homeItem.addActionListener( e -> _showPanel( Screens.HOME, false ) );
		newNoteItem.addActionListener( e -> _showPanel( Screens.NEW_EDIT_NOTE, false ) );
		openNoteItem.addActionListener( e -> _showPanel( Screens.SEARCH_NOTES, false ) );
		exportNotesItem.addActionListener( e -> _showPanel( Screens.BULK_EXPORT_NOTES, false ) );
		exitItem.addActionListener( e -> _handleExit() );
		
		notesMenu.add( homeItem );
		notesMenu.add( newNoteItem );
		notesMenu.add( openNoteItem );
		notesMenu.add( exportNotesItem );
		notesMenu.addSeparator();
		notesMenu.add( exitItem );
		
		JMenu clientsMenu = new JMenu( "Clients" );
		JMenuItem newClientItem = new JMenuItem( "New" );
		JMenuItem editClientItem = new JMenuItem( "Edit" );
		JMenuItem viewClientsItem = new JMenuItem( "View" );
		
		newClientItem.addActionListener( e -> _showPanel( Screens.NEW_EDIT_CLIENT, false ) );
		editClientItem.addActionListener( e -> _showPanel( Screens.SEARCH_CLIENTS_EDIT, false ) );
		viewClientsItem.addActionListener( e -> _showPanel( Screens.SEARCH_CLIENTS_VIEW, false ) );
		
		clientsMenu.add( newClientItem );
		clientsMenu.add( editClientItem );
		clientsMenu.add( viewClientsItem );
		
		JMenu externalContactsMenu = new JMenu( "External Contacts" );
		JMenuItem newContactItem = new JMenuItem( "New" );
		JMenuItem editContactItem = new JMenuItem( "Edit" );
		
		newContactItem.addActionListener( e -> _showPanel( Screens.NEW_EDIT_CONTACT, false ) );
		editContactItem.addActionListener( e -> _showPanel( Screens.SEARCH_CONTACTS, false ) );
		
		externalContactsMenu.add( newContactItem );
		externalContactsMenu.add( editContactItem );
		
		JMenuItem preferencesItem = new JMenuItem( "Preferences" );
		JMenuItem configurationItem = new JMenuItem( "Assessment Options" );
		
		preferencesItem.addActionListener( e -> _showPanel( Screens.PREFERENCES, false ) );
		configurationItem.addActionListener( e -> _showPanel( Screens.CONFIG, false ) );
		addPinItem.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				Dlg_AddPin.show( MainWindow.this );
			}
		} );
		changePinItem.addActionListener( new ActionListener()
		{
			@Override
			public void actionPerformed( ActionEvent e )
			{
				Dlg_ChangePin.show( MainWindow.this );
			}
		} );
		
		settingsMenu.add( preferencesItem );
		settingsMenu.add( configurationItem );
		settingsMenu.add( addPinItem );
		addPinItem.setVisible( !PinManager.isPinEnabled() );
		settingsMenu.add( changePinItem );
		changePinItem.setVisible( PinManager.isPinEnabled() );
		
		JMenu helpMenu = new JMenu( "Help" );
		JMenuItem aboutItem = new JMenuItem( "About" );
		JMenuItem helpIndexItem = new JMenuItem( "Help Index" );
		JMenuItem viewLogsItem = new JMenuItem( "View Logs" );
		JMenuItem exportLogsItem = new JMenuItem( "Export Logs" );
		
		aboutItem.addActionListener( e -> _showPanel( Screens.ABOUT, false ) );
		helpIndexItem.addActionListener( e -> _showPanel( Screens.HELP, false ) );
		viewLogsItem.addActionListener( e -> _showPanel( Screens.VIEW_LOGS, false ) );
		exportLogsItem.addActionListener( new ActionListener()
		{
			
			@Override
			public void actionPerformed( ActionEvent e )
			{
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle( "Export Logs to CSV" );
				fileChooser.setSelectedFile( new File( "therapy_progress_notes_app_logs.csv" ) );
				
				int userSelection = fileChooser.showSaveDialog( mainPanel.getParent() );
				
				if( userSelection == JFileChooser.APPROVE_OPTION )
				{
					File fileToSave = fileChooser.getSelectedFile();
					
					boolean success = Exporter.exportLogsToCSV( fileToSave );
					
					if( success )
					{
						JOptionPane.showMessageDialog( mainPanel.getParent(),
								"Logs successfully exported to:\n" + fileToSave.getAbsolutePath(), "Export Successful",
								JOptionPane.INFORMATION_MESSAGE );
					}
					else
					{
						JOptionPane.showMessageDialog( mainPanel.getParent(), "Failed to export logs. Check console for details.",
								"Export Failed", JOptionPane.ERROR_MESSAGE );
					}
				}
			}
		} );
		
		helpMenu.add( aboutItem );
		helpMenu.add( helpIndexItem );
		helpMenu.addSeparator();
		helpMenu.add( viewLogsItem );
		helpMenu.add( exportLogsItem );
		
		menuBar.add( notesMenu );
		menuBar.add( clientsMenu );
		menuBar.add( externalContactsMenu );
		menuBar.add( settingsMenu );
		menuBar.add( helpMenu );
		
		setJMenuBar( menuBar );
	}
	
	private void _showPanel( Screens panel, boolean skipValidation )
	{
		if( skipValidation )
		{
			_changeScreen( panel );
			return;
		}
		else
		{
			if( currentScreen.isEditPanel() || ( currentScreen == Screens.PREFERENCES && preferencesPanel.hasUnsavedChanges() ) )
			{
				int result = JOptionPane.showConfirmDialog( this,
						"Are you sure you want to leave the current screen? Any unsaved changes will be lost.", "Exit Confirmation",
						JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE );
				
				if( result == JOptionPane.YES_OPTION )
				{
					_changeScreen( panel );
				}
			}
			else
			{
				_changeScreen( panel );
			}
		}
	}
	
	private void _changeScreen( Screens panel )
	{
		AppLogger.logNavigation( currentScreen.getPanelName(), panel.getPanelName() );
		switch( panel )
		{
			case SEARCH_CLIENTS_VIEW:
				searchClientsViewPanel.clearFields();
				searchClientsViewPanel.loadAllClients();
				break;
			case SEARCH_CLIENTS_EDIT:
				searchClientsEditPanel.clearFields();
				searchClientsEditPanel.loadAllClients();
				break;
			case SEARCH_NOTES:
				searchNotePanel.clearFields();
				break;
			case SEARCH_CONTACTS:
				searchContactPanel.clearFields();
				break;
			case NEW_EDIT_NOTE:
				newEditNotePanel.clearForm();
				break;
			case NEW_EDIT_CLIENT:
				newEditClientPanel.clearForm();
				break;
			case NEW_EDIT_CONTACT:
				newEditContactPanel.clearForm();
				break;
			default:
				break;
		}
		cardLayout.show( mainPanel, panel.getPanelName() );
		currentScreen = panel;
	}
	
	private void _handleExit()
	{
		int result = JOptionPane.showConfirmDialog( this, "Are you sure you want to exit?", "Exit Confirmation", JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE );
		
		if( result == JOptionPane.YES_OPTION )
		{
			AppLogger.logShutdown();
			System.exit( 0 );
		}
	}
	
	public void returnHome( boolean skipValidation )
	{
		_showPanel( Screens.HOME, skipValidation );
	}
	
	public void updateMenu()
	{
		addPinItem.setVisible( !PinManager.isPinEnabled() );
		changePinItem.setVisible( PinManager.isPinEnabled() );
		
		settingsMenu.revalidate();
		settingsMenu.repaint();
	}
	
	public void openNote( Note note )
	{
		_changeScreen( Screens.NEW_EDIT_NOTE );
		newEditNotePanel.loadNote( note );
	}
}
