package main.java.com.alexpacheco.therapynotes.view.tablemodels;

import javax.swing.table.DefaultTableModel;

public abstract class ViewOnlyTableModel extends DefaultTableModel
{
	private static final long serialVersionUID = -2135106728096014280L;

	public ViewOnlyTableModel(Object[] columnNames, int rowCount)
	{
		super(columnNames, rowCount);
	}

	@Override
	public boolean isCellEditable(int row, int column)
	{
		return false;
	}
}
