package com.alexpacheco.therapynotes.view.components;

import java.util.HashMap;
import java.util.Set;

import javax.swing.JComboBox;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

import com.alexpacheco.therapynotes.controller.AppController;
import com.alexpacheco.therapynotes.controller.errorhandling.exceptions.TherapyAppException;
import com.alexpacheco.therapynotes.util.JavaUtils;

public class Cmb_ClientSelection extends JComboBox<String>
{
	private static final long serialVersionUID = -769502544357364914L;
	private HashMap<String, Integer> clientMap;
	private boolean includeBlankOption;
	
	public Cmb_ClientSelection( boolean includeBlankOption )
	{
		this.includeBlankOption = includeBlankOption;
		this.clientMap = new HashMap<>();
		
		// Initial load
		populateClients();
		
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
				// No action needed
			}
			
			@Override
			public void popupMenuCanceled( PopupMenuEvent e )
			{
				// No action needed
			}
		} );
	}
	
	private void populateClients()
	{
		// Store current selection
		String currentSelection = (String) getSelectedItem();
		
		removeAllItems();
		clientMap.clear();
		
		if( includeBlankOption )
		{
			addItem( "" );
		}
		
		try
		{
			clientMap = AppController.getClientMap();
			Set<String> clients = clientMap.keySet();
			for( String client : clients )
			{
				addItem( client );
			}
		}
		catch( TherapyAppException e )
		{
			AppController.showBasicErrorPopup( e, "Error loading client list:" );
		}
		
		// Restore selection if it still exists
		if( currentSelection != null && !currentSelection.isEmpty() && clientMap.containsKey( currentSelection ) )
		{
			setSelectedItem( currentSelection );
		}
		else if( includeBlankOption )
		{
			setSelectedIndex( 0 );
		}
		else
		{
			setSelectedIndex( -1 );
		}
	}
	
	public Integer getSelectedClientId()
	{
		String selectedClientName = (String) getSelectedItem();
		if( !JavaUtils.isNullOrEmpty( selectedClientName ) )
		{
			return clientMap.get( selectedClientName );
		}
		return null;
	}
	
	public void selectByClientId( int clientId )
	{
		for( String clientName : clientMap.keySet() )
		{
			if( clientMap.get( clientName ).equals( clientId ) )
			{
				setSelectedItem( clientName );
				return;
			}
		}
	}
	
	public String getClientName( Integer clientId )
	{
		if( clientId == null )
			return null;
		
		for( String clientName : clientMap.keySet() )
		{
			if( clientMap.get( clientName ).equals( clientId ) )
			{
				return clientName;
			}
		}
		
		return null;
	}
}