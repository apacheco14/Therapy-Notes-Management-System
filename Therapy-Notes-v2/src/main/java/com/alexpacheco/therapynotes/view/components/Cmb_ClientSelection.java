package com.alexpacheco.therapynotes.view.components;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.alexpacheco.therapynotes.controller.AppController;
import com.alexpacheco.therapynotes.controller.exceptions.TherapyAppException;
import com.alexpacheco.therapynotes.util.JavaUtils;

/**
 * A searchable combo box for selecting clients. Displays client names and filters the dropdown list as the user types. Unlike free-text
 * fields, this component only allows selection of valid clients from the list.
 */
public class Cmb_ClientSelection extends JComboBox<String>
{
	private static final long serialVersionUID = -769502544357364914L;
	
	private static final String NO_MATCHES_TEXT = "No matching clients found";
	
	private HashMap<String, Integer> clientMap;
	private List<String> allClientNames;
	private DefaultComboBoxModel<String> model;
	private JTextField editorField;
	private boolean includeBlankOption;
	private boolean isFiltering = false;
	private boolean suppressValidation = false;
	private String lastValidSelection = "";
	
	/**
	 * Creates a new searchable client selection combo box.
	 * 
	 * @param includeBlankOption Whether to include a blank option at the top of the list
	 */
	public Cmb_ClientSelection( boolean includeBlankOption )
	{
		this.includeBlankOption = includeBlankOption;
		this.clientMap = new HashMap<>();
		this.allClientNames = new ArrayList<>();
		this.model = new DefaultComboBoxModel<>();
		
		setModel( model );
		setEditable( true );
		
		editorField = (JTextField) getEditor().getEditorComponent();
		
		// Initial load
		populateClients();
		attachListeners();
	}
	
	/**
	 * Attaches event listeners for filtering and validation functionality.
	 */
	private void attachListeners()
	{
		// Filter as user types
		editorField.addKeyListener( new KeyAdapter()
		{
			@Override
			public void keyReleased( KeyEvent e )
			{
				int keyCode = e.getKeyCode();
				
				// Ignore navigation and action keys
				if( keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_ENTER
						|| keyCode == KeyEvent.VK_ESCAPE || keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_RIGHT
						|| keyCode == KeyEvent.VK_SHIFT || keyCode == KeyEvent.VK_CONTROL || keyCode == KeyEvent.VK_ALT )
				{
					return;
				}
				
				filterClients();
			}
		} );
		
		// Show popup on focus if empty
		editorField.addFocusListener( new FocusAdapter()
		{
			@Override
			public void focusGained( FocusEvent e )
			{
				if( editorField.getText().isEmpty() )
				{
					showPopup();
				}
			}
			
			@Override
			public void focusLost( FocusEvent e )
			{
				// When focus is lost, validate and restore to last valid selection if needed
				validateAndRestoreSelection();
			}
		} );
		
		// Refresh when dropdown is opened
		addPopupMenuListener( new PopupMenuListener()
		{
			@Override
			public void popupMenuWillBecomeVisible( PopupMenuEvent e )
			{
				populateClients();
			}
			
			@Override
			public void popupMenuWillBecomeInvisible( PopupMenuEvent e )
			{
				// Validate selection when popup closes
				SwingUtilities.invokeLater( () -> validateAndRestoreSelection() );
			}
			
			@Override
			public void popupMenuCanceled( PopupMenuEvent e )
			{
				// Restore last valid selection on cancel
				SwingUtilities.invokeLater( () -> restoreLastValidSelection() );
			}
		} );
	}
	
	/**
	 * Populates the combo box with clients from the controller.
	 */
	private void populateClients()
	{
		// Don't repopulate while filtering is in progress
		if( isFiltering || suppressValidation )
		{
			return;
		}
		
		// Store current selection
		String currentSelection = lastValidSelection;
		
		model.removeAllElements();
		clientMap.clear();
		allClientNames.clear();
		
		if( includeBlankOption )
		{
			model.addElement( "" );
		}
		
		try
		{
			clientMap = AppController.getClientMap();
			Set<String> clients = clientMap.keySet();
			for( String client : clients )
			{
				allClientNames.add( client );
				model.addElement( client );
			}
		}
		catch( TherapyAppException e )
		{
			AppController.showBasicErrorPopup( e, "Error loading client list:" );
		}
		
		// Restore selection if it still exists
		if( !JavaUtils.isNullOrEmpty( currentSelection ) && clientMap.containsKey( currentSelection ) )
		{
			setSelectedItem( currentSelection );
			lastValidSelection = currentSelection;
		}
		else if( includeBlankOption )
		{
			setSelectedIndex( 0 );
			lastValidSelection = "";
		}
		else
		{
			setSelectedIndex( -1 );
			lastValidSelection = "";
		}
	}
	
	/**
	 * Filters the dropdown list based on the current text in the editor.
	 */
	private void filterClients()
	{
		if( isFiltering )
		{
			return;
		}
		
		isFiltering = true;
		suppressValidation = true;
		
		SwingUtilities.invokeLater( () ->
		{
			try
			{
				String originalText = editorField.getText();
				String searchText = editorField.getText().toLowerCase().trim();
				int caretPosition = editorField.getCaretPosition();
				
				model.removeAllElements();
				
				if( searchText.isEmpty() )
				{
					// Show all clients when search is empty
					if( includeBlankOption )
					{
						model.addElement( "" );
					}
					for( String client : allClientNames )
					{
						model.addElement( client );
					}
				}
				else
				{
					// Filter clients that contain the search text
					for( String client : allClientNames )
					{
						if( client.toLowerCase().contains( searchText ) )
						{
							model.addElement( client );
						}
					}
					
					// Add a "no results" indicator if nothing matches
					if( model.getSize() == 0 )
					{
						model.addElement( NO_MATCHES_TEXT );
					}
				}
				
				// Restore the typed text and caret position
				editorField.setText( originalText );
				editorField.setCaretPosition( Math.min( caretPosition, originalText.length() ) );
				
				// Show the filtered dropdown
				if( model.getSize() > 0 )
				{
					showPopup();
				}
			}
			finally
			{
				isFiltering = false;
				// Delay clearing suppressValidation to avoid race conditions with popup events
				SwingUtilities.invokeLater( () -> suppressValidation = false );
			}
		} );
	}
	
	/**
	 * Validates the current text and updates the selection. If the text matches a valid client, selects it. If there is exactly one
	 * matching client, auto-selects it. Otherwise, restores the last valid selection.
	 */
	private void validateAndRestoreSelection()
	{
		// Don't validate while filtering is in progress
		if( suppressValidation )
		{
			return;
		}
		
		String currentText = editorField.getText().trim();
		
		// Empty text is valid if blank option is included
		if( currentText.isEmpty() )
		{
			if( includeBlankOption )
			{
				lastValidSelection = "";
				resetToFullList();
				setSelectedItem( "" );
			}
			else
			{
				restoreLastValidSelection();
			}
			return;
		}
		
		// Check for exact match (case-sensitive first)
		if( clientMap.containsKey( currentText ) )
		{
			lastValidSelection = currentText;
			resetToFullList();
			setSelectedItem( currentText );
			return;
		}
		
		// Check for case-insensitive match
		for( String client : allClientNames )
		{
			if( client.equalsIgnoreCase( currentText ) )
			{
				lastValidSelection = client;
				resetToFullList();
				setSelectedItem( client );
				return;
			}
		}
		
		// Check if there's exactly one client matching the search text - auto-select it
		String singleMatch = findSingleMatch( currentText.toLowerCase() );
		if( singleMatch != null )
		{
			lastValidSelection = singleMatch;
			resetToFullList();
			setSelectedItem( singleMatch );
			editorField.setText( singleMatch );
			return;
		}
		
		// No valid match found - restore last valid selection
		restoreLastValidSelection();
	}
	
	/**
	 * Finds a single matching client for the given search text. Returns the client name if exactly one client matches, otherwise returns
	 * null.
	 * 
	 * @param searchText The lowercase search text to match against
	 * @return The single matching client name, or null if zero or multiple matches
	 */
	private String findSingleMatch( String searchText )
	{
		String match = null;
		int matchCount = 0;
		
		for( String client : allClientNames )
		{
			if( client.toLowerCase().contains( searchText ) )
			{
				match = client;
				matchCount++;
				
				// More than one match - no auto-select
				if( matchCount > 1 )
				{
					return null;
				}
			}
		}
		
		return matchCount == 1 ? match : null;
	}
	
	/**
	 * Restores the combo box to the last valid selection.
	 */
	private void restoreLastValidSelection()
	{
		// Don't restore while filtering is in progress
		if( suppressValidation )
		{
			return;
		}
		
		resetToFullList();
		
		if( !JavaUtils.isNullOrEmpty( lastValidSelection ) && clientMap.containsKey( lastValidSelection ) )
		{
			setSelectedItem( lastValidSelection );
			editorField.setText( lastValidSelection );
		}
		else if( includeBlankOption )
		{
			setSelectedItem( "" );
			editorField.setText( "" );
			lastValidSelection = "";
		}
		else
		{
			setSelectedIndex( -1 );
			editorField.setText( "" );
			lastValidSelection = "";
		}
	}
	
	/**
	 * Resets the model to show all clients (removes any filtering).
	 */
	private void resetToFullList()
	{
		if( isFiltering || suppressValidation )
		{
			return;
		}
		
		model.removeAllElements();
		
		if( includeBlankOption )
		{
			model.addElement( "" );
		}
		
		for( String client : allClientNames )
		{
			model.addElement( client );
		}
	}
	
	/**
	 * Returns the ID of the currently selected client.
	 * 
	 * @return The client ID, or null if no valid client is selected
	 */
	public Integer getSelectedClientId()
	{
		String selectedClientName = getSelectedClientName();
		if( !JavaUtils.isNullOrEmpty( selectedClientName ) )
		{
			return clientMap.get( selectedClientName );
		}
		return null;
	}
	
	/**
	 * Returns the name of the currently selected client. Only returns a value if a valid client is selected.
	 * 
	 * @return The client name, or null if no valid client is selected
	 */
	public String getSelectedClientName()
	{
		Object selected = getSelectedItem();
		
		if( selected == null )
		{
			return null;
		}
		
		String value = selected.toString().trim();
		
		// Ignore the "no results" placeholder
		if( NO_MATCHES_TEXT.equals( value ) )
		{
			return null;
		}
		
		// Empty string is valid (no selection)
		if( value.isEmpty() )
		{
			return null;
		}
		
		// Verify it's a valid client from our map
		if( clientMap.containsKey( value ) )
		{
			return value;
		}
		
		return null;
	}
	
	/**
	 * Selects a client by their ID.
	 * 
	 * @param clientId The ID of the client to select
	 */
	public void selectByClientId( int clientId )
	{
		for( String clientName : clientMap.keySet() )
		{
			if( clientMap.get( clientName ).equals( clientId ) )
			{
				resetToFullList();
				setSelectedItem( clientName );
				lastValidSelection = clientName;
				return;
			}
		}
	}
	
	/**
	 * Gets the client name for a given client ID.
	 * 
	 * @param clientId The client ID to look up
	 * @return The client name, or null if not found
	 */
	public String getClientName( Integer clientId )
	{
		if( clientId == null )
		{
			return null;
		}
		
		for( String clientName : clientMap.keySet() )
		{
			if( clientMap.get( clientName ).equals( clientId ) )
			{
				return clientName;
			}
		}
		
		return null;
	}
	
	/**
	 * Clears the selection.
	 */
	public void clear()
	{
		resetToFullList();
		
		if( includeBlankOption )
		{
			setSelectedItem( "" );
			lastValidSelection = "";
		}
		else
		{
			setSelectedIndex( -1 );
			lastValidSelection = "";
		}
		
		editorField.setText( "" );
	}
	
	/**
	 * Manually refreshes the client list from the controller.
	 */
	public void refreshClients()
	{
		populateClients();
	}
}