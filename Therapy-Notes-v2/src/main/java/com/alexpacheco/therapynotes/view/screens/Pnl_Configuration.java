package com.alexpacheco.therapynotes.view.screens;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.alexpacheco.therapynotes.controller.AppController;
import com.alexpacheco.therapynotes.controller.enums.ConfigKey;
import com.alexpacheco.therapynotes.controller.exceptions.TherapyAppException;
import com.alexpacheco.therapynotes.model.entities.assessmentoptions.AssessmentOption;
import com.alexpacheco.therapynotes.model.entities.assessmentoptions.AssessmentOptionFactory;
import com.alexpacheco.therapynotes.model.entities.assessmentoptions.AssessmentOptionType;
import com.alexpacheco.therapynotes.util.AppFonts;
import com.alexpacheco.therapynotes.util.JavaUtils;
import com.alexpacheco.therapynotes.view.components.Pnl_ConfigurationOption;
import com.alexpacheco.therapynotes.view.components.ScrollablePanel;
import com.alexpacheco.therapynotes.view.components.Pnl_ConfigurationCategory;

/**
 * Configuration panel for managing assessment options using a tabbed interface. Options are organized into three tabs: Symptoms, Mental
 * Status, and Post-Session Admin.
 */
public class Pnl_Configuration extends JPanel implements Pnl_ConfigurationOption.ConfigurationOptionListener
{
	private static final long serialVersionUID = 1L;
	
	// Tab indices
	private static final int TAB_SYMPTOMS = 0;
	private static final int TAB_MENTAL_STATUS = 1;
	private static final int TAB_POST_SESSION_ADMIN = 2;
	
	// Number of columns for the symptoms grid
	private static final int SYMPTOMS_COLUMN_COUNT = 4;
	
	// Tab category mappings
	private static final ConfigKey[] SYMPTOMS_CATEGORIES = { ConfigKey.SYMPTOMS };
	private static final ConfigKey[] MENTAL_STATUS_CATEGORIES = { ConfigKey.APPEARANCE, ConfigKey.SPEECH, ConfigKey.AFFECT,
			ConfigKey.EYE_CONTACT };
	private static final ConfigKey[] POST_SESSION_ADMIN_CATEGORIES = { ConfigKey.REFERRAL_TYPES, ConfigKey.COLLATERAL_CONTACT_TYPES,
			ConfigKey.NEXT_APPOINTMENT };
	
	private JTabbedPane tabbedPane;
	
	// Scroll panes for each tab (to reset scroll position)
	private JScrollPane symptomsScrollPane;
	private JScrollPane mentalStatusScrollPane;
	private JScrollPane postSessionAdminScrollPane;
	
	// Category panels mapped by ConfigKey
	private Map<ConfigKey, Pnl_ConfigurationCategory> categoryPanels;
	
	// For symptoms tab, we have multiple column panels sharing the same data
	private List<Pnl_ConfigurationCategory> symptomColumnPanels;
	
	// In-memory storage of options (for holding changes until save)
	private Map<ConfigKey, List<AssessmentOption>> optionsData;
	
	// Track next temporary ID for new options (negative to distinguish from DB IDs)
	private int nextTempId = -1;
	
	public Pnl_Configuration()
	{
		categoryPanels = new HashMap<>();
		symptomColumnPanels = new ArrayList<>();
		optionsData = new EnumMap<>( ConfigKey.class );
		
		initializeUI();
		loadAllOptions();
	}
	
	private void initializeUI()
	{
		setLayout( new BorderLayout() );
		setBackground( AppController.getBackgroundColor() );
		
		// Title
		JLabel titleLabel = new JLabel( "Configure Assessment Options", SwingConstants.CENTER );
		titleLabel.setFont( AppFonts.getScreenTitleFont() );
		titleLabel.setForeground( AppController.getTitleColor() );
		titleLabel.setBorder( BorderFactory.createEmptyBorder( 20, 0, 20, 0 ) );
		
		// Tabbed pane with larger font
		tabbedPane = new JTabbedPane();
		tabbedPane.setFont( AppFonts.getTextFieldFont() );
		tabbedPane.addTab( "Clinical Symptoms", createSymptomsTab() );
		tabbedPane.addTab( "Mental Status", createMentalStatusTab() );
		tabbedPane.addTab( "Post-Session Admin", createPostSessionAdminTab() );
		tabbedPane.setBackground( AppController.getBackgroundColor() );
		
		add( titleLabel, BorderLayout.NORTH );
		add( tabbedPane, BorderLayout.CENTER );
		add( createButtonPanel(), BorderLayout.SOUTH );
	}
	
	/**
	 * Creates the Symptoms tab with a 4-column grid layout. All columns share the same symptoms data, distributed evenly.
	 */
	private JScrollPane createSymptomsTab()
	{
		ScrollablePanel contentPanel = new ScrollablePanel( new GridLayout( 1, SYMPTOMS_COLUMN_COUNT, 10, 0 ) );
		
		// Create 4 column panels for symptoms
		// First column gets the header, others are headerless
		for( int i = 0; i < SYMPTOMS_COLUMN_COUNT; i++ )
		{
			String headerText = ( i == 0 ) ? "Symptoms" : " "; // Non-breaking space for alignment
			Pnl_ConfigurationCategory columnPanel = new Pnl_ConfigurationCategory( headerText, this );
			symptomColumnPanels.add( columnPanel );
			contentPanel.add( columnPanel );
		}
		
		symptomsScrollPane = new JScrollPane( contentPanel );
		symptomsScrollPane.getVerticalScrollBar().setUnitIncrement( 16 );
		symptomsScrollPane.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
		symptomsScrollPane.setBorder( null );
		symptomsScrollPane.getViewport().setBackground( AppController.getBackgroundColor() );
		
		return symptomsScrollPane;
	}
	
	/**
	 * Creates the Mental Status tab with 4 columns: Appearance, Speech, Affect, Eye Contact
	 */
	private JScrollPane createMentalStatusTab()
	{
		ScrollablePanel contentPanel = new ScrollablePanel( new GridLayout( 1, MENTAL_STATUS_CATEGORIES.length, 10, 0 ) );
		
		for( ConfigKey configKey : MENTAL_STATUS_CATEGORIES )
		{
			Pnl_ConfigurationCategory categoryPanel = new Pnl_ConfigurationCategory( configKey.getDisplayName(), this );
			categoryPanels.put( configKey, categoryPanel );
			contentPanel.add( categoryPanel );
		}
		
		mentalStatusScrollPane = new JScrollPane( contentPanel );
		mentalStatusScrollPane.getVerticalScrollBar().setUnitIncrement( 16 );
		mentalStatusScrollPane.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
		mentalStatusScrollPane.setBorder( null );
		mentalStatusScrollPane.getViewport().setBackground( AppController.getBackgroundColor() );
		
		return mentalStatusScrollPane;
	}
	
	/**
	 * Creates the Post-Session Admin tab with 3 columns: Referrals, Collateral Contacts, Next Appointment
	 */
	private JScrollPane createPostSessionAdminTab()
	{
		ScrollablePanel contentPanel = new ScrollablePanel( new GridLayout( 1, POST_SESSION_ADMIN_CATEGORIES.length + 1, 10, 0 ) );
		
		for( ConfigKey configKey : POST_SESSION_ADMIN_CATEGORIES )
		{
			Pnl_ConfigurationCategory categoryPanel = new Pnl_ConfigurationCategory( configKey.getDisplayName(), this );
			categoryPanels.put( configKey, categoryPanel );
			contentPanel.add( categoryPanel );
		}
		Pnl_ConfigurationCategory blankCategoryPanel = new Pnl_ConfigurationCategory();
		contentPanel.add( blankCategoryPanel );
		
		postSessionAdminScrollPane = new JScrollPane( contentPanel );
		postSessionAdminScrollPane.getVerticalScrollBar().setUnitIncrement( 16 );
		postSessionAdminScrollPane.setHorizontalScrollBarPolicy( ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
		postSessionAdminScrollPane.setBorder( null );
		postSessionAdminScrollPane.getViewport().setBackground( AppController.getBackgroundColor() );
		
		return postSessionAdminScrollPane;
	}
	
	/**
	 * Creates the button panel with Cancel, Add Option, and Save buttons.
	 */
	private JPanel createButtonPanel()
	{
		JPanel panel = new JPanel( new FlowLayout( FlowLayout.RIGHT, 10, 10 ) );
		panel.setBorder( BorderFactory.createEmptyBorder( 5, 10, 10, 10 ) );
		
		JButton btnCancel = new JButton( "Cancel" );
		btnCancel.addActionListener( e -> AppController.returnHome( false ) );
		
		JButton btnAddOption = new JButton( "Add Option" );
		btnAddOption.addActionListener( e -> showAddOptionDialog() );
		
		JButton btnSave = new JButton( "Save" );
		btnSave.addActionListener( e -> save() );
		
		panel.add( btnCancel );
		panel.add( btnAddOption );
		panel.add( btnSave );
		
		return panel;
	}
	
	/**
	 * Loads all options from the database into memory and populates the UI.
	 */
	private void loadAllOptions()
	{
		// Load all config keys
		for( ConfigKey configKey : ConfigKey.values() )
		{
			try
			{
				List<AssessmentOption> options = AppController.getConfigOptions( configKey );
				optionsData.put( configKey, new ArrayList<>( options ) );
			}
			catch( TherapyAppException e )
			{
				AppController.showBasicErrorPopup( e, "Error loading options:" );
				optionsData.put( configKey, new ArrayList<>() );
			}
		}
		
		// Populate UI
		refreshAllUI();
		
		// Reset scroll positions to top after layout is complete
		SwingUtilities.invokeLater( () -> resetScrollPositions() );
	}
	
	/**
	 * Resets all scroll panes to scroll to the top.
	 */
	private void resetScrollPositions()
	{
		if( symptomsScrollPane != null )
		{
			symptomsScrollPane.getVerticalScrollBar().setValue( 0 );
		}
		if( mentalStatusScrollPane != null )
		{
			mentalStatusScrollPane.getVerticalScrollBar().setValue( 0 );
		}
		if( postSessionAdminScrollPane != null )
		{
			postSessionAdminScrollPane.getVerticalScrollBar().setValue( 0 );
		}
	}
	
	/**
	 * Refreshes all UI components from the in-memory data.
	 */
	private void refreshAllUI()
	{
		// Refresh symptoms (distributed across columns)
		refreshSymptomsUI();
		
		// Refresh other categories
		for( ConfigKey configKey : MENTAL_STATUS_CATEGORIES )
		{
			refreshCategoryUI( configKey );
		}
		
		for( ConfigKey configKey : POST_SESSION_ADMIN_CATEGORIES )
		{
			refreshCategoryUI( configKey );
		}
	}
	
	/**
	 * Refreshes the symptoms tab by distributing options across the 4 columns.
	 */
	private void refreshSymptomsUI()
	{
		List<AssessmentOption> symptoms = optionsData.get( ConfigKey.SYMPTOMS );
		if( symptoms == null )
		{
			symptoms = new ArrayList<>();
		}
		
		// Distribute symptoms across columns
		int totalItems = symptoms.size();
		int itemsPerColumn = (int) Math.ceil( (double) totalItems / SYMPTOMS_COLUMN_COUNT );
		
		for( int col = 0; col < SYMPTOMS_COLUMN_COUNT; col++ )
		{
			int startIndex = col * itemsPerColumn;
			int endIndex = Math.min( startIndex + itemsPerColumn, totalItems );
			
			List<AssessmentOption> columnItems = new ArrayList<>();
			if( startIndex < totalItems )
			{
				columnItems = symptoms.subList( startIndex, endIndex );
			}
			
			symptomColumnPanels.get( col ).loadOptions( new ArrayList<>( columnItems ) );
		}
	}
	
	/**
	 * Refreshes a single category panel from in-memory data.
	 */
	private void refreshCategoryUI( ConfigKey configKey )
	{
		Pnl_ConfigurationCategory panel = categoryPanels.get( configKey );
		if( panel != null )
		{
			List<AssessmentOption> options = optionsData.get( configKey );
			panel.loadOptions( options != null ? new ArrayList<>( options ) : new ArrayList<>() );
		}
	}
	
	/**
	 * Shows the Add Option dialog scoped to the current tab's categories.
	 */
	private void showAddOptionDialog()
	{
		showOptionDialog( null );
	}
	
	/**
	 * Shows the option dialog for adding or editing.
	 * 
	 * @param existingOption The option to edit, or null for adding new
	 */
	private void showOptionDialog( AssessmentOption existingOption )
	{
		boolean isEdit = existingOption != null;
		String dialogTitle = isEdit ? "Edit Assessment Option" : "Add Assessment Option";
		
		JDialog dialog = new JDialog( (Frame) SwingUtilities.getWindowAncestor( this ), dialogTitle, true );
		dialog.setLayout( new BorderLayout() );
		dialog.setSize( 400, 200 );
		dialog.setLocationRelativeTo( this );
		
		// Get categories for current tab
		ConfigKey[] availableCategories = getCategoriesForCurrentTab();
		
		// Form panel
		JPanel formPanel = new JPanel( new GridBagLayout() );
		formPanel.setBorder( BorderFactory.createEmptyBorder( 20, 20, 20, 20 ) );
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets( 5, 5, 5, 5 );
		
		JTextField nameField = new JTextField( 20 );
		JTextField descriptionField = new JTextField( 20 );
		JComboBox<ConfigKey> optionTypeComboBox = new JComboBox<>( availableCategories );
		
		// Set up combo box renderer
		optionTypeComboBox.setRenderer( ( list, value, index, isSelected, cellHasFocus ) ->
		{
			JLabel label = new JLabel();
			if( value != null )
			{
				label.setText( value.getDisplayName() );
			}
			if( isSelected )
			{
				label.setBackground( list.getSelectionBackground() );
				label.setForeground( list.getSelectionForeground() );
				label.setOpaque( true );
			}
			return label;
		} );
		
		// Pre-populate for edit mode
		if( isEdit )
		{
			nameField.setText( existingOption.getName() );
			nameField.setEnabled( false );
			descriptionField.setText( existingOption.getDescription() );
			
			// Find and select the matching ConfigKey
			ConfigKey matchingKey = findConfigKeyForOption( existingOption );
			if( matchingKey != null )
			{
				optionTypeComboBox.setSelectedItem( matchingKey );
			}
			optionTypeComboBox.setEnabled( false );
		}
		else
		{
			optionTypeComboBox.setSelectedIndex( 0 );
		}
		
		// Layout form fields
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.weightx = 0.0;
		formPanel.add( new JLabel( "Name: *" ), gbc );
		
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		formPanel.add( nameField, gbc );
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.weightx = 0.0;
		formPanel.add( new JLabel( "Description:" ), gbc );
		
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		formPanel.add( descriptionField, gbc );
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.weightx = 0.0;
		formPanel.add( new JLabel( "Option Type: *" ), gbc );
		
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		formPanel.add( optionTypeComboBox, gbc );
		
		// Button panel
		JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
		JButton saveButton = new JButton( isEdit ? "Update" : "Save" );
		JButton cancelButton = new JButton( "Cancel" );
		
		saveButton.addActionListener( e ->
		{
			String name = nameField.getText().trim();
			String description = descriptionField.getText().trim();
			ConfigKey selectedConfigKey = (ConfigKey) optionTypeComboBox.getSelectedItem();
			
			if( JavaUtils.isNullOrEmpty( name ) )
			{
				JOptionPane.showMessageDialog( dialog, "Name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE );
				return;
			}
			
			if( selectedConfigKey == null )
			{
				JOptionPane.showMessageDialog( dialog, "Option Type is required.", "Validation Error", JOptionPane.ERROR_MESSAGE );
				return;
			}
			
			if( isEdit )
			{
				// Update existing option in memory
				updateOptionInMemory( existingOption, name, description );
			}
			else
			{
				// Add new option to memory
				addOptionToMemory( selectedConfigKey, name, description );
			}
			
			dialog.dispose();
		} );
		
		cancelButton.addActionListener( e -> dialog.dispose() );
		
		buttonPanel.add( cancelButton );
		buttonPanel.add( saveButton );
		
		dialog.add( formPanel, BorderLayout.CENTER );
		dialog.add( buttonPanel, BorderLayout.SOUTH );
		
		dialog.setVisible( true );
	}
	
	/**
	 * Gets the categories available for the currently selected tab.
	 */
	private ConfigKey[] getCategoriesForCurrentTab()
	{
		int selectedTab = tabbedPane.getSelectedIndex();
		
		switch( selectedTab )
		{
			case TAB_SYMPTOMS:
				return SYMPTOMS_CATEGORIES;
			case TAB_MENTAL_STATUS:
				return MENTAL_STATUS_CATEGORIES;
			case TAB_POST_SESSION_ADMIN:
				return POST_SESSION_ADMIN_CATEGORIES;
			default:
				return ConfigKey.values();
		}
	}
	
	/**
	 * Finds the ConfigKey that matches the given option's type.
	 */
	private ConfigKey findConfigKeyForOption( AssessmentOption option )
	{
		AssessmentOptionType optionType = option.getOptionType();
		if( optionType == null )
		{
			return null;
		}
		
		for( ConfigKey configKey : ConfigKey.values() )
		{
			if( configKey.getKey().equals( optionType.getDbTypeKey() ) )
			{
				return configKey;
			}
		}
		return null;
	}
	
	/**
	 * Adds a new option to in-memory storage and refreshes the UI.
	 */
	private void addOptionToMemory( ConfigKey configKey, String name, String description )
	{
		// Find matching AssessmentOptionType
		AssessmentOptionType matchingType = findAssessmentOptionType( configKey );
		
		if( matchingType != null )
		{
			AssessmentOption newOption = AssessmentOptionFactory.createAssessmentOption( nextTempId--, name, description, matchingType );
			
			List<AssessmentOption> options = optionsData.get( configKey );
			if( options == null )
			{
				options = new ArrayList<>();
				optionsData.put( configKey, options );
			}
			options.add( newOption );
			
			// Refresh appropriate UI
			if( configKey == ConfigKey.SYMPTOMS )
			{
				refreshSymptomsUI();
			}
			else
			{
				refreshCategoryUI( configKey );
			}
		}
	}
	
	/**
	 * Updates an existing option in memory and refreshes the UI.
	 */
	private void updateOptionInMemory( AssessmentOption existingOption, String newName, String newDescription )
	{
		ConfigKey configKey = findConfigKeyForOption( existingOption );
		if( configKey == null )
		{
			return;
		}
		
		List<AssessmentOption> options = optionsData.get( configKey );
		if( options == null )
		{
			return;
		}
		
		// Find and update the option
		for( int i = 0; i < options.size(); i++ )
		{
			AssessmentOption opt = options.get( i );
			if( opt.getId() == existingOption.getId() )
			{
				AssessmentOption updatedOption = AssessmentOptionFactory.createAssessmentOption( opt.getId(), newName, newDescription,
						opt.getOptionType() );
				options.set( i, updatedOption );
				break;
			}
		}
		
		// Refresh appropriate UI
		if( configKey == ConfigKey.SYMPTOMS )
		{
			refreshSymptomsUI();
		}
		else
		{
			refreshCategoryUI( configKey );
		}
	}
	
	/**
	 * Removes an option from memory and refreshes the UI.
	 */
	private void removeOptionFromMemory( AssessmentOption option )
	{
		ConfigKey configKey = findConfigKeyForOption( option );
		if( configKey == null )
		{
			return;
		}
		
		List<AssessmentOption> options = optionsData.get( configKey );
		if( options != null )
		{
			options.removeIf( opt -> opt.getId() == option.getId() );
		}
		
		// Refresh appropriate UI
		if( configKey == ConfigKey.SYMPTOMS )
		{
			refreshSymptomsUI();
		}
		else
		{
			refreshCategoryUI( configKey );
		}
	}
	
	/**
	 * Finds the AssessmentOptionType matching a ConfigKey.
	 */
	private AssessmentOptionType findAssessmentOptionType( ConfigKey configKey )
	{
		for( AssessmentOptionType type : AssessmentOptionType.values() )
		{
			if( type.getDbTypeKey().equals( configKey.getKey() ) )
			{
				return type;
			}
		}
		return null;
	}
	
	/**
	 * Saves all options to the database.
	 */
	private void save()
	{
		try
		{
			List<AssessmentOption> allOptions = collectAllOptions();
			AppController.saveOptions( allOptions );
			JOptionPane.showMessageDialog( this, "Changes saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE );
			AppController.returnHome( true );
		}
		catch( TherapyAppException e )
		{
			AppController.showBasicErrorPopup( e, "Error saving changes:" );
		}
	}
	
	/**
	 * Collects all options from in-memory storage.
	 */
	private List<AssessmentOption> collectAllOptions()
	{
		List<AssessmentOption> allOptions = new ArrayList<>();
		
		for( Map.Entry<ConfigKey, List<AssessmentOption>> entry : optionsData.entrySet() )
		{
			List<AssessmentOption> options = entry.getValue();
			if( options != null )
			{
				allOptions.addAll( options );
			}
		}
		
		return allOptions;
	}
	
	// ============ ConfigurationOptionListener Implementation ============
	
	@Override
	public void onEditRequested( AssessmentOption option )
	{
		showOptionDialog( option );
	}
	
	@Override
	public void onDeleteRequested( AssessmentOption option )
	{
		int result = JOptionPane.showConfirmDialog( this, "Are you sure you want to delete \"" + option.getName() + "\"?", "Confirm Delete",
				JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE );
		
		if( result == JOptionPane.YES_OPTION )
		{
			// TODO implement delete
			// removeOptionFromMemory( option );
		}
	}
}