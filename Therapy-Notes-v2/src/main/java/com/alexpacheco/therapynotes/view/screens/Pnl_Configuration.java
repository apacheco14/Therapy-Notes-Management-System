package main.java.com.alexpacheco.therapynotes.view.screens;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumnModel;

import main.java.com.alexpacheco.therapynotes.controller.AppController;
import main.java.com.alexpacheco.therapynotes.controller.enums.ConfigKey;
import main.java.com.alexpacheco.therapynotes.controller.errorhandling.exceptions.TherapyAppException;
import main.java.com.alexpacheco.therapynotes.model.entities.assessmentoptions.AssessmentOption;
import main.java.com.alexpacheco.therapynotes.model.entities.assessmentoptions.AssessmentOptionFactory;
import main.java.com.alexpacheco.therapynotes.model.entities.assessmentoptions.AssessmentOptionType;
import main.java.com.alexpacheco.therapynotes.util.JavaUtils;
import main.java.com.alexpacheco.therapynotes.view.tablemodels.ConfigOptionsTableModel;

public class Pnl_Configuration extends JPanel
{
	private static final long serialVersionUID = 3497512709852662129L;
	
	// Map to store table models by ConfigKey for refreshing after add
	private Map<ConfigKey, ConfigOptionsTableModel> tableModels;
	
	public Pnl_Configuration()
	{
		tableModels = new java.util.HashMap<>();
		
		setLayout(new BorderLayout());
		
		JLabel titleLabel = new JLabel("Configure Assessment Options", SwingConstants.CENTER);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
		titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
		
		// Main panel with all sections
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		
		// Create sections
		mainPanel.add(createSection(ConfigKey.SYMPTOMS));
		mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		
		mainPanel.add(createSection(ConfigKey.AFFECT));
		mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		
		mainPanel.add(createSection(ConfigKey.EYE_CONTACT));
		mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		
		mainPanel.add(createSection(ConfigKey.APPEARANCE));
		mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		
		mainPanel.add(createSection(ConfigKey.SPEECH));
		mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		
		mainPanel.add(createSection(ConfigKey.NEXT_APPOINTMENT));
		mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		
		mainPanel.add(createSection(ConfigKey.COLLATERAL_CONTACT_TYPES));
		mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
		
		mainPanel.add(createSection(ConfigKey.REFERRAL_TYPES));
		
		// Wrap in scroll pane
		JScrollPane scrollPane = new JScrollPane(mainPanel);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.setBorder(null);
		
		add(titleLabel, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
		add(createButtonPanel(), BorderLayout.SOUTH);
	}
	
	/**
	 * Create a configuration section with a table
	 * 
	 * @param configKey The ConfigKey enum for this section
	 * @return JPanel containing the section
	 */
	private JPanel createSection(ConfigKey configKey)
	{
		String sectionName = configKey.getDisplayName();
		
		JPanel sectionPanel = new JPanel(new BorderLayout());
		sectionPanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10),
				BorderFactory.createTitledBorder(sectionName)));
		
		// Table for options
		ConfigOptionsTableModel tableModel = new ConfigOptionsTableModel();
		tableModels.put(configKey, tableModel);
		
		JTable table = new JTable(tableModel);
		table.setRowHeight(25);
		table.getTableHeader().setReorderingAllowed(false);
		table.setCellSelectionEnabled(false);
		table.setAutoCreateRowSorter(true);
		
		// Set column widths
		TableColumnModel columnModel = table.getColumnModel();
		columnModel.getColumn(ConfigOptionsTableModel.COL_ID).setMinWidth(0);
		columnModel.getColumn(ConfigOptionsTableModel.COL_ID).setMaxWidth(0);
		columnModel.getColumn(ConfigOptionsTableModel.COL_ID).setWidth(0);
		columnModel.getColumn(ConfigOptionsTableModel.COL_NAME).setPreferredWidth(50);
		columnModel.getColumn(ConfigOptionsTableModel.COL_DESCRIPTION).setPreferredWidth(300);
		
		JScrollPane tableScrollPane = new JScrollPane(table);
		int preferredHeight = ConfigKey.SYMPTOMS.equals(configKey) ? 450 : 150;
		tableScrollPane.setPreferredSize(new Dimension(0, preferredHeight));
		
		sectionPanel.add(tableScrollPane, BorderLayout.CENTER);
		
		// Load existing options
		loadOptions(configKey, tableModel);
		
		return sectionPanel;
	}
	
	/**
	 * Creates the button panel with Cancel and Add Option buttons.
	 * 
	 * @return JPanel containing the buttons
	 */
	private JPanel createButtonPanel()
	{
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
		panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(e -> AppController.returnHome(false));
		
		JButton btnAddOption = new JButton("Add Option");
		btnAddOption.addActionListener(e -> showAddOptionDialog());
		
		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(e -> save());
		
		panel.add(btnCancel);
		panel.add(btnAddOption);
		panel.add(btnSave);
		
		return panel;
	}
	
	/**
	 * Load existing options for a configuration section
	 * 
	 * @param configKey  The ConfigKey enum
	 * @param tableModel The table model to populate
	 */
	private void loadOptions(ConfigKey configKey, ConfigOptionsTableModel tableModel)
	{
		try
		{
			tableModel.loadOptions(AppController.getConfigOptions(configKey));
		}
		catch (TherapyAppException e)
		{
			AppController.showBasicErrorPopup(e, "Error loading options:");
		}
	}
	
	/**
	 * Show dialog to add a new option
	 */
	private void showAddOptionDialog()
	{
		JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Assessment Option", true);
		dialog.setLayout(new BorderLayout());
		dialog.setSize(400, 200);
		dialog.setLocationRelativeTo(this);
		
		// Form panel
		JPanel formPanel = new JPanel(new GridBagLayout());
		formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.insets = new Insets(5, 5, 5, 5);
		
		JTextField nameField = new JTextField(20);
		JTextField descriptionField = new JTextField(20);
		JComboBox<ConfigKey> optionTypeComboBox = new JComboBox<>(ConfigKey.values());
		optionTypeComboBox.setSelectedIndex(-1);
		optionTypeComboBox.setRenderer((list, value, index, isSelected, cellHasFocus) ->
		{
			JLabel label = new JLabel();
			if (value != null)
			{
				label.setText(value.getDisplayName());
			}
			if (isSelected)
			{
				label.setBackground(list.getSelectionBackground());
				label.setForeground(list.getSelectionForeground());
				label.setOpaque(true);
			}
			return label;
		});
		
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.weightx = 0.0;
		formPanel.add(new JLabel("Name: *"), gbc);
		
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		formPanel.add(nameField, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.weightx = 0.0;
		formPanel.add(new JLabel("Description:"), gbc);
		
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		formPanel.add(descriptionField, gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.anchor = GridBagConstraints.EAST;
		gbc.weightx = 0.0;
		formPanel.add(new JLabel("Option Type: *"), gbc);
		
		gbc.gridx = 1;
		gbc.anchor = GridBagConstraints.WEST;
		gbc.weightx = 1.0;
		formPanel.add(optionTypeComboBox, gbc);
		
		// Button panel
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		JButton saveButton = new JButton("Save");
		JButton cancelButton = new JButton("Cancel");
		
		saveButton.addActionListener(e ->
		{
			String name = nameField.getText().trim();
			String description = descriptionField.getText().trim();
			ConfigKey selectedConfigKey = (ConfigKey) optionTypeComboBox.getSelectedItem();
			
			if (JavaUtils.isNullOrEmpty(name))
			{
				JOptionPane.showMessageDialog(dialog, "Name is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			if (selectedConfigKey == null)
			{
				JOptionPane.showMessageDialog(dialog, "Option Type is required.", "Validation Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			try
			{
				AppController.addSingleConfigOption(selectedConfigKey, name, description);
				ConfigOptionsTableModel tableModel = tableModels.get(selectedConfigKey);
				if (tableModel != null)
				{
					loadOptions(selectedConfigKey, tableModel);
				}
				dialog.dispose();
				
				JOptionPane.showMessageDialog(this, "Option added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
			}
			catch (TherapyAppException ex)
			{
				AppController.showBasicErrorPopup(ex, "Error saving option:");
			}
		});
		
		cancelButton.addActionListener(e -> dialog.dispose());
		
		buttonPanel.add(cancelButton);
		buttonPanel.add(saveButton);
		
		dialog.add(formPanel, BorderLayout.CENTER);
		dialog.add(buttonPanel, BorderLayout.SOUTH);
		
		dialog.setVisible(true);
	}
	
	private void save()
	{
		try
		{
			AppController.saveOptions(collectAllOptions());
			JOptionPane.showMessageDialog(this, "Changes saved successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
			AppController.returnHome(true);
		}
		catch (TherapyAppException e)
		{
			AppController.showBasicErrorPopup(e, "Error saving changes:");
		}
	}
	
	/**
	 * Collects all options from all configuration tables into a list of AssessmentOption objects.
	 * 
	 * @return List of AssessmentOption objects from all tables
	 */
	private List<AssessmentOption> collectAllOptions()
	{
		List<AssessmentOption> allOptions = new ArrayList<>();
		
		for (Map.Entry<ConfigKey, ConfigOptionsTableModel> entry : tableModels.entrySet())
		{
			ConfigKey configKey = entry.getKey();
			ConfigOptionsTableModel tableModel = entry.getValue();
			
			// Find matching AssessmentOptionType
			AssessmentOptionType matchingType = null;
			for (AssessmentOptionType type : AssessmentOptionType.values())
			{
				if (type.getDbTypeKey().equals(configKey.getKey()))
				{
					matchingType = type;
					break;
				}
			}
			
			if (matchingType != null)
			{
				for (int row = 0; row < tableModel.getRowCount(); row++)
				{
					AssessmentOption option = AssessmentOptionFactory.createAssessmentOption(tableModel.getIdAt(row),
							tableModel.getNameAt(row), tableModel.getDescriptionAt(row), matchingType);
					allOptions.add(option);
				}
			}
		}
		
		return allOptions;
	}
}