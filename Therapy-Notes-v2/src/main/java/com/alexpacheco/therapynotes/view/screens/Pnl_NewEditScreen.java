package com.alexpacheco.therapynotes.view.screens;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.alexpacheco.therapynotes.controller.AppController;
import com.alexpacheco.therapynotes.controller.exceptions.TherapyAppException;
import com.alexpacheco.therapynotes.view.components.ScrollablePanel;

public abstract class Pnl_NewEditScreen extends JPanel
{
	private static final long serialVersionUID = -8345704149397614554L;
	
	protected JPanel headerPanel;
	protected ScrollablePanel mainContentPanel;
	protected JScrollPane mainScrollPane;
	protected JPanel footerPanel;
	private JButton saveButton = new JButton( "Save" );
	private JButton cancelButton = new JButton( "Cancel" );
	
	protected boolean isEditMode = false;
	protected Integer entityId = null;
	
	public Pnl_NewEditScreen()
	{
		setLayout( new BorderLayout() );
		
		// Header Panel
		headerPanel = new JPanel();
		headerPanel.setBackground( AppController.getBackgroundColor() );
		initHeaderPanelComponents();
		add( headerPanel, BorderLayout.NORTH );
		
		// Main Panel
		mainContentPanel = new ScrollablePanel();
		mainContentPanel.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
		
		mainScrollPane = new JScrollPane( mainContentPanel );
		mainScrollPane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED );
		mainScrollPane.setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
		mainScrollPane.getVerticalScrollBar().setUnitIncrement( 16 );
		mainScrollPane.getHorizontalScrollBar().setUnitIncrement( 16 );
		mainScrollPane.getViewport().setBackground( AppController.getBackgroundColor() );
		
		initMainPanelComponents();
		add( mainScrollPane, BorderLayout.CENTER );
		
		// Footer Panel
		footerPanel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 10, 10 ) );
		footerPanel.setBorder( BorderFactory.createEmptyBorder( 5, 10, 10, 10 ) );
		
		saveButton = new JButton( "Save" );
		saveButton.addActionListener( e -> onClickSave() );
		
		cancelButton = new JButton( "Cancel" );
		cancelButton.addActionListener( e -> onClickCancel() );
		
		footerPanel.add( cancelButton );
		footerPanel.add( saveButton );
		
		initFooterComponents();
		add( footerPanel, BorderLayout.SOUTH );
		
		clearForm();
	}
	
	protected abstract void initHeaderPanelComponents();
	
	protected abstract void initMainPanelComponents();
	
	protected abstract void initFooterComponents();
	
	/**
	 * Set the panel to edit mode and load data
	 * 
	 * @param entityId The ID of the entity to edit
	 */
	public void setEditMode( Integer entityId )
	{
		this.isEditMode = true;
		this.entityId = entityId;
		toggleTitleLabel();
		disableUneditableFields();
		loadEntityData( entityId );
	}
	
	/**
	 * Set the panel to create mode (default)
	 */
	public void setCreateMode()
	{
		this.isEditMode = false;
		this.entityId = null;
		toggleTitleLabel();
		enableUneditableFields();
		clearForm();
	}
	
	protected void onClickCancel()
	{
		int result = JOptionPane.showConfirmDialog( this, "Are you sure you want to cancel? Any unsaved changes will be lost.",
				"Cancel Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE );
		
		if( result == JOptionPane.YES_OPTION )
		{
			clearForm();
			setCreateMode();
			AppController.returnHome( true );
		}
	}
	
	protected void onClickSave()
	{
		if( !isDataValid() )
		{
			return;
		}
		
		Object entity = collectEntityData();
		if( !isEveryRequiredFieldFilled( entity ) )
		{
			return;
		}
		
		try
		{
			if( isEditMode )
			{
				doEditSave( entity );
				clearForm();
				setCreateMode();
				AppController.returnHome( true );
			}
			else
			{
				doNewSave( entity );
				clearForm();
				setCreateMode();
			}
		}
		catch( TherapyAppException e )
		{
			showSaveError( e );
		}
	}
	
	protected abstract void disableUneditableFields();
	
	protected abstract void enableUneditableFields();
	
	protected abstract void loadEntityData( Integer entityId );
	
	protected abstract void doNewSave( Object entity ) throws TherapyAppException;
	
	protected abstract void doEditSave( Object entity ) throws TherapyAppException;
	
	protected abstract boolean isDataValid();
	
	protected abstract boolean isEveryRequiredFieldFilled( Object entity );
	
	protected abstract Object collectEntityData();
	
	protected abstract void showSaveError( TherapyAppException e );
	
	protected abstract void toggleTitleLabel();
	
	public abstract void refreshLabelsText();
	
	public abstract void clearForm();
}
