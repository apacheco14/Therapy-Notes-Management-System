package main.java.com.alexpacheco.therapynotes.view.tablemodels;

import javax.swing.table.DefaultTableModel;

public class NoteSearchResultsTableModel extends DefaultTableModel
{
	private static final long serialVersionUID = -3586553653196059678L;
	public static final int COL_ID = 0;
    public static final int COL_CLIENT = 1;
    public static final int COL_APPT_DATE = 2;
    public static final int COL_STATUS = 3;
    public static final int COL_OPEN_BUTTON = 4;
    
	public NoteSearchResultsTableModel()
	{
		super(new String[] { "Note ID", "Client", "Appointment Date", "Appointment Note", "Open" }, 0);
	}
	
	@Override
	public boolean isCellEditable(int row, int column)
	{
		return column == COL_OPEN_BUTTON; // Only Open button column is editable
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
	
	public Integer getNoteIdAt(int row)
    {
        return (Integer) getValueAt(row, COL_ID);
    }
}
