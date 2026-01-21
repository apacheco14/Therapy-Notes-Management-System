package com.alexpacheco.therapynotes.view.tablemodels;

import java.util.ArrayList;
import java.util.List;

import com.alexpacheco.therapynotes.model.entities.assessmentoptions.AssessmentOption;

public class ConfigOptionsTableModel extends ViewOnlyTableModel
{
	private static final long serialVersionUID = 1409895960055128681L;
	public static final int COL_ID = 0;
	public static final int COL_NAME = 1;
	public static final int COL_DESCRIPTION = 2;
	
	public ConfigOptionsTableModel()
	{
		super(new String[] { "Id", "Name", "Description" }, 0);
	}
	
	@Override
	public boolean isCellEditable(int row, int column)
	{
		return column == COL_DESCRIPTION;
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex)
	{
		if (columnIndex == COL_ID)
		{
			return Integer.class;
		}
		return String.class;
	}
	
	public void addOption(Integer id, String name, String description)
	{
		addRow(new Object[] { id, name, description });
	}
	
	public Integer getIdAt(int row)
	{
		return (Integer) getValueAt(row, COL_ID);
	}
	
	public String getNameAt(int row)
	{
		return (String) getValueAt(row, COL_NAME);
	}
	
	public String getDescriptionAt(int row)
	{
		return (String) getValueAt(row, COL_DESCRIPTION);
	}
	
	public void loadOptions(List<AssessmentOption> options)
	{
		setRowCount(0); // Clear existing data
		
		for (AssessmentOption option : options)
		{
			addRow(new Object[] { option.getId(), option.getName(), option.getDescription() });
		}
	}
	
	public void sortByName()
	{
		List<Object[]> rows = new ArrayList<>();
		for (int i = 0; i < getRowCount(); i++)
		{
			rows.add(new Object[] { getValueAt(i, COL_NAME), getValueAt(i, COL_DESCRIPTION) });
		}
		
		rows.sort((a, b) -> ((String) a[0]).compareToIgnoreCase((String) b[0]));
		
		setRowCount(0);
		for (Object[] row : rows)
		{
			addRow(row);
		}
	}
}
