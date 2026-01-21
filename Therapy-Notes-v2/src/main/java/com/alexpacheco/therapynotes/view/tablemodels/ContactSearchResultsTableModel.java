package main.java.com.alexpacheco.therapynotes.view.tablemodels;

import javax.swing.table.DefaultTableModel;

public class ContactSearchResultsTableModel extends DefaultTableModel
{
	private static final long serialVersionUID = 2363836677806210263L;
	public static final int COL_ID = 0;
    public static final int COL_FIRST_NAME = 1;
    public static final int COL_LAST_NAME = 2;
    public static final int COL_LINKED_CLIENT = 3;
    public static final int COL_EMERGENCY_CONTACT = 4;
    public static final int COL_EMAIL = 5;
    public static final int COL_PHONE = 6;
    public static final int COL_EDIT_BUTTON = 7;
	
	public ContactSearchResultsTableModel()
	{
		super(new String[] { "Contact ID", "First Name", "Last Name", "Linked Client", "Emergency Contact", "Email", "Phone", "Edit" }, 0);
	}
	
	@Override
	public boolean isCellEditable(int row, int column)
	{
		return column == COL_EDIT_BUTTON; // Only Edit button column is editable
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
	
	public Integer getContactIdAt(int row)
    {
        return (Integer) getValueAt(row, COL_ID);
    }
}
