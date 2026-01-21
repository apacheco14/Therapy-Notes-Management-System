package main.java.com.alexpacheco.therapynotes.view.tablemodels;

import javax.swing.table.DefaultTableModel;

public class ClientSearchResultsTableModel extends DefaultTableModel
{
	private static final long serialVersionUID = -6053461557464702571L;
	
	public static final int COL_ID = 0;
	public static final int COL_CLIENT_CODE = 1;
	public static final int COL_FIRST_NAME = 2;
	public static final int COL_LAST_NAME = 3;
	public static final int COL_STATUS = 4;
	public static final int COL_BUTTON = 5;
	
	public ClientSearchResultsTableModel()
	{
		super(new String[] { "Client ID", "Client Code", "First Name", "Last Name", "Status", "Action" }, 0);
	}
	
	@Override
	public boolean isCellEditable(int row, int column)
	{
		return column == COL_BUTTON; // Only button column is editable
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
	
	public Integer getClientIdAt(int row)
	{
		return (Integer) getValueAt(row, COL_ID);
	}
}
