package com.alexpacheco.therapynotes.controller.enums;

public enum Screens
{
	HOME( "Home", false ),
	NEW_EDIT_NOTE( "NewEditNote", true ),
	SEARCH_NOTES( "SearchNotes", false ),
	BULK_EXPORT_NOTES( "BulkExportNotes", false ),
	NEW_EDIT_CLIENT( "NewEditClient", true ),
	SEARCH_CLIENTS_EDIT( "SearchClientsEdit", false ),
	SEARCH_CLIENTS_VIEW( "SearchClientsView", false ),
	CLIENT_DETAILS( "ClientDetails", false ),
	NEW_EDIT_CONTACT( "NewEditContact", true ),
	SEARCH_CONTACTS( "SearchContacts", false ),
	PREFERENCES( "Preferences", true ),
	CONFIG( "Configuration", true ),
	HELP( "HelpIndex", false ),
	ABOUT( "About", false ),
	VIEW_LOGS( "ViewLogs", false );
	
	private String panelName;
	private boolean editPanel;
	
	Screens( String panelName )
	{
		this.panelName = panelName;
		this.editPanel = false;
	}
	
	Screens( String panelName, boolean editPanel )
	{
		this.panelName = panelName;
		this.editPanel = editPanel;
	}
	
	public String getPanelName()
	{
		return panelName;
	}
	
	public boolean isEditPanel()
	{
		return editPanel;
	}
}
