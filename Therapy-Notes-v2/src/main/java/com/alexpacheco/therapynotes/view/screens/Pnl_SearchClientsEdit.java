package com.alexpacheco.therapynotes.view.screens;

import javax.swing.*;

import com.alexpacheco.therapynotes.controller.enums.Screens;

import java.awt.*;

public class Pnl_SearchClientsEdit extends Pnl_SearchClients
{
	private static final long serialVersionUID = -7891234567890123456L;
	private Pnl_NewEditClient editClientPanel;
	
	public Pnl_SearchClientsEdit( CardLayout cardLayout, JPanel mainPanel, Pnl_NewEditClient editClientPanel )
	{
		super( cardLayout, mainPanel );
		this.editClientPanel = editClientPanel;
	}
	
	@Override
	protected String getRowLevelButtonTitle()
	{
		return "Edit";
	}
	
	@Override
	protected void doRowLevelAction( Integer clientId )
	{
		editClientPanel.setEditMode( clientId );
		parentCardLayout.show( parentPanel, Screens.NEW_EDIT_CLIENT.getPanelName() );
	}
}