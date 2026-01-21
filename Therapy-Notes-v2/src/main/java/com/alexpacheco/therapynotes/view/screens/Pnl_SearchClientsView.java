package com.alexpacheco.therapynotes.view.screens;

import javax.swing.*;

import com.alexpacheco.therapynotes.controller.AppController;
import com.alexpacheco.therapynotes.controller.enums.Screens;
import com.alexpacheco.therapynotes.controller.errorhandling.exceptions.TherapyAppException;

import java.awt.*;

public class Pnl_SearchClientsView extends Pnl_SearchClients
{
	private static final long serialVersionUID = -1234567890123456789L;
	private Pnl_ViewClientDetails clientDetailsPanel;
	
	public Pnl_SearchClientsView(CardLayout cardLayout, JPanel mainPanel, Pnl_ViewClientDetails clientDetailsPanel)
	{
		super(cardLayout, mainPanel);
		this.clientDetailsPanel = clientDetailsPanel;
		
	}
	
	@Override
	protected String getRowLevelButtonTitle()
	{
		return "View More";
	}
	
	@Override
	protected void doRowLevelAction(Integer clientId)
	{
		try
		{
			clientDetailsPanel.loadClientDetails(clientId);
		}
		catch (TherapyAppException e)
		{
			AppController.showBasicErrorPopup(e, "Error loading client details:");
			parentCardLayout.show(parentPanel, Screens.SEARCH_CLIENTS_VIEW.getPanelName());
		}
	}
}