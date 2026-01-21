package main.java.com.alexpacheco.therapynotes.view.tablemodels;

public class ContactsTableModel extends ViewOnlyTableModel
{
	private static final long serialVersionUID = 6652656709489716796L;

	public ContactsTableModel()
	{
		super(new String[] { "First Name", "Last Name", "Emergency Contact", "Email 1", "Email 2", "Email 3", "Phone 1", "Phone 2",
				"Phone 3" }, 0);
	}
}
