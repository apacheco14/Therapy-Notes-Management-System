package com.alexpacheco.therapynotes.view.components;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.alexpacheco.therapynotes.controller.AppController;
import com.alexpacheco.therapynotes.model.entities.assessmentoptions.AssessmentOption;
import com.alexpacheco.therapynotes.util.AppFonts;

/**
 * A panel displaying a category of configuration options with a bold header and a vertical list of option items below.
 */
public class Pnl_ConfigurationCategory extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private static final String EMPTY_MESSAGE = "No options configured";
	private static final int HEADER_BOTTOM_PADDING = 10;
	private static final int ITEM_SPACING = 8;
	
	private String categoryName;
	private JPanel itemsPanel;
	private JLabel emptyLabel;
	private List<Pnl_ConfigurationOption> optionPanels;
	private Pnl_ConfigurationOption.ConfigurationOptionListener optionListener;
	
	/**
	 * Creates a new configuration category panel.
	 * 
	 * @param categoryName   The name to display as the header
	 * @param optionListener Listener for option edit/delete actions
	 */
	public Pnl_ConfigurationCategory( String categoryName, Pnl_ConfigurationOption.ConfigurationOptionListener optionListener )
	{
		this.categoryName = categoryName;
		this.optionListener = optionListener;
		this.optionPanels = new ArrayList<>();
		
		initializeUI();
	}
	
	/**
	 * Creates a blank placeholder panel.
	 */
	public Pnl_ConfigurationCategory()
	{
		setLayout( new BorderLayout() );
		setBorder( BorderFactory.createEmptyBorder( 10, 5, 10, 5 ) );
		setOpaque( false );
	}
	
	private void initializeUI()
	{
		setLayout( new BorderLayout() );
		setBorder( BorderFactory.createEmptyBorder( 10, 5, 10, 5 ) );
		setOpaque( false );
		
		// Header label
		JLabel headerLabel = new JLabel( categoryName );
		headerLabel.setFont( AppFonts.getHeaderFont() );
		headerLabel.setForeground( AppController.getSubtitleColor() );
		headerLabel.setBorder( BorderFactory.createEmptyBorder( 0, 0, HEADER_BOTTOM_PADDING, 0 ) );
		
		// Items panel with vertical layout
		itemsPanel = new JPanel();
		itemsPanel.setLayout( new BoxLayout( itemsPanel, BoxLayout.Y_AXIS ) );
		itemsPanel.setOpaque( false );
		
		// Empty state label
		emptyLabel = new JLabel( EMPTY_MESSAGE );
		emptyLabel.setFont( AppFonts.getLabelFont().deriveFont( Font.ITALIC ) );
		emptyLabel.setForeground( AppController.getTitleColor() );
		
		add( headerLabel, BorderLayout.NORTH );
		add( itemsPanel, BorderLayout.CENTER );
		
		showEmptyState();
	}
	
	/**
	 * Loads options into this category panel.
	 * 
	 * @param options The list of options to display
	 */
	public void loadOptions( List<AssessmentOption> options )
	{
		itemsPanel.removeAll();
		optionPanels.clear();
		
		if( options == null || options.isEmpty() )
		{
			showEmptyState();
		}
		else
		{
			for( int i = 0; i < options.size(); i++ )
			{
				AssessmentOption option = options.get( i );
				Pnl_ConfigurationOption optionPanel = new Pnl_ConfigurationOption( option, optionListener );
				optionPanels.add( optionPanel );
				
				itemsPanel.add( optionPanel );
				
				// Add spacing between items (but not after the last one)
				if( i < options.size() - 1 )
				{
					itemsPanel.add( Box.createVerticalStrut( ITEM_SPACING ) );
				}
			}
			
			// Add glue at the bottom to push items to the top
			itemsPanel.add( Box.createVerticalGlue() );
		}
		
		revalidate();
		repaint();
	}
	
	private void showEmptyState()
	{
		itemsPanel.removeAll();
		itemsPanel.add( emptyLabel );
		itemsPanel.add( Box.createVerticalGlue() );
	}
	
	/**
	 * Adds a single option to this category.
	 * 
	 * @param option The option to add
	 */
	public void addOption( AssessmentOption option )
	{
		// Remove glue and empty label if present
		if( optionPanels.isEmpty() )
		{
			itemsPanel.removeAll();
		}
		else
		{
			// Remove the glue at the end
			int componentCount = itemsPanel.getComponentCount();
			if( componentCount > 0 )
			{
				itemsPanel.remove( componentCount - 1 );
			}
			// Add spacing before new item
			itemsPanel.add( Box.createVerticalStrut( ITEM_SPACING ) );
		}
		
		Pnl_ConfigurationOption optionPanel = new Pnl_ConfigurationOption( option, optionListener );
		optionPanels.add( optionPanel );
		itemsPanel.add( optionPanel );
		itemsPanel.add( Box.createVerticalGlue() );
		
		revalidate();
		repaint();
	}
	
	/**
	 * Removes an option from this category.
	 * 
	 * @param option The option to remove
	 */
	public void removeOption( AssessmentOption option )
	{
		Pnl_ConfigurationOption toRemove = null;
		
		for( Pnl_ConfigurationOption panel : optionPanels )
		{
			if( panel.getOption().equals( option ) )
			{
				toRemove = panel;
				break;
			}
		}
		
		if( toRemove != null )
		{
			optionPanels.remove( toRemove );
			
			// Rebuild the items panel
			List<AssessmentOption> remainingOptions = new ArrayList<>();
			for( Pnl_ConfigurationOption panel : optionPanels )
			{
				remainingOptions.add( panel.getOption() );
			}
			loadOptions( remainingOptions );
		}
	}
	
	/**
	 * Updates an existing option in this category.
	 * 
	 * @param option The option with updated data
	 */
	public void updateOption( AssessmentOption option )
	{
		for( Pnl_ConfigurationOption panel : optionPanels )
		{
			if( panel.getOption().getId() == option.getId() )
			{
				panel.updateOption( option );
				break;
			}
		}
	}
	
	/**
	 * Gets all options currently displayed in this category.
	 * 
	 * @return List of assessment options
	 */
	public List<AssessmentOption> getOptions()
	{
		List<AssessmentOption> options = new ArrayList<>();
		for( Pnl_ConfigurationOption panel : optionPanels )
		{
			options.add( panel.getOption() );
		}
		return options;
	}
	
	/**
	 * Gets the category name.
	 * 
	 * @return The category name
	 */
	public String getCategoryName()
	{
		return categoryName;
	}
}