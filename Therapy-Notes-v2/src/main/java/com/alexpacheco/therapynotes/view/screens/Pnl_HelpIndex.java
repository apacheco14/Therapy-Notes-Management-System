package main.java.com.alexpacheco.therapynotes.view.screens;
import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
public class Pnl_HelpIndex extends JPanel
{
	private static final long serialVersionUID = -809216100843108450L;
	public Pnl_HelpIndex()
	{
		setLayout(new BorderLayout());
		
		JEditorPane helpText = new JEditorPane();
		helpText.setContentType("text/html");
		helpText.setEditable(false);
		helpText.setFont(new Font("Arial", Font.PLAIN, 12));
		
		String helpContent = "<html><body style='font-family: Arial; font-size: 12px; margin: 10px;'>"
//				+ "<h2>PROGRESS NOTES APP DOCUMENTATION</h2>"
				+ "<p style='background-color: #f0f0f0; padding: 10px; border: 1px solid #ccc;'>" + "<b>Quick Links:</b> "
				+ "<a href='#newnote'>New Note</a> | " + "<a href='#opennote'>Open Note</a> | " + "<a href='#exportnotes'>Export Notes</a> | "
				+ "<a href='#newclient'>New Client</a> | " + "<a href='#editclient'>Edit Client</a> | " + "<a href='#viewclient'>View Client</a> | "
				+ "<a href='#newcontact'>New Contact</a> | " + "<a href='#editcontact'>Edit Contact</a> | "
				+ "<a href='#preferences'>Preferences</a> | " + "<a href='#configuration'>Configure Assessment Options</a> | "
				+ "<a href='#about'>About</a> | " + "<a href='#helpindex'>Help Index</a> | "
				+ "<a href='#viewlogs'>View Logs</a> | " + "<a href='#exportlogs'>Export Logs</a>" + "</p>" + "<hr>"
				
				+ "<h2>Notes</h2>"
				+ "<p><a name='newnote'></a><b>New Note:</b> Create a new therapy session note with five main sections: session info, clinical symptoms, narrative, mental status, and post-session administrative. "
				+ "Each section captures specific aspects of the therapy session for comprehensive documentation.</p>"
				+ "<p><a name='opennote'></a><b>Open Note:</b> Search for and open existing therapy notes from the database. "
				+ "The search screen allows filtering by client selection and date range, with both criteria being optional to provide flexible lookup options.</p>"
				+ "<p><a name='exportnotes'></a><b>Export Notes:</b> Bulk export therapy notes to PDF or DOCX format. "
				+ "Users can choose to export all notes, notes for a specific client, or notes within a certain date range. "
				+ "Additional options allow selection of the output folder and file naming convention for exported files.</p>"
				
				+ "<h2>Clients</h2>"
				+ "<p><a name='newclient'></a><b>New Client:</b> Add a new client to the system by entering their first name, last name, client code (unique identifier), date of birth, email address, and phone number. "
				+ "The client code serves as a unique identifier for referencing the client throughout the application.</p>"
				+ "<p><a name='editclient'></a><b>Edit Client:</b> Modify existing client information including first name, last name, date of birth, email addresses, and phone numbers. "
				+ "Use this screen to keep client records current and accurate.</p>"
				+ "<p><a name='viewclient'></a><b>View Client:</b> Display a read-only summary of client information. "
				+ "This screen also shows all emergency contacts associated with the selected client.</p>"
				
				+ "<h2>Contacts</h2>"
				+ "<p><a name='newcontact'></a><b>New Contact:</b> Create a new emergency contact record linked to a specific client. "
				+ "Required information includes first name, last name, the linked client, email addresses, and phone numbers.</p>"
				+ "<p><a name='editcontact'></a><b>Edit Contact:</b> Update existing emergency contact information including name, email addresses, and phone numbers. "
				+ "Use this screen to maintain accurate emergency contact details for clients.</p>"
				
				+ "<h2>Settings</h2>"
				+ "<p><a name='preferences'></a><b>Preferences:</b> Configure application-wide settings including which fields are required and their default values. "
				+ "These preferences help streamline data entry by pre-populating commonly used values and enforcing required fields.</p>"
				+ "<p><a name='configuration'></a><b>Configure Assessment Options:</b> Customize the dropdown options available in therapy notes for items such as affect, clinical symptoms, referrals, and other assessment categories. "
				+ "This allows the application to be tailored to your specific clinical workflow and terminology.</p>"
				
				+ "<h2>Help</h2>"
				+ "<p><a name='about'></a><b>About:</b> View information about the application including version number, purpose, and developer information.</p>"
				+ "<p><a name='helpindex'></a><b>Help Index:</b> Access this help documentation for guidance on using all application features. "
				+ "Use the quick links at the top to jump directly to specific topics.</p>"
				+ "<p><a name='viewlogs'></a><b>View Logs:</b> View application logs including informational/trace messages and error logs. "
				+ "This is useful for troubleshooting issues or reviewing application activity.</p>"
				+ "<p><a name='exportlogs'></a><b>Export Logs:</b> Export application logs to a CSV file for external review or record-keeping. "
				+ "Users can select the destination folder for the exported log file.</p>"
				
				+ "</body></html>";
		
		helpText.setText(helpContent);
		helpText.setCaretPosition(0);
		
		helpText.addHyperlinkListener(e ->
		{
			if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
			{
				String ref = e.getDescription();
				if (ref != null && ref.startsWith("#"))
				{
					String anchor = ref.substring(1);
					SwingUtilities.invokeLater(() ->
					{
						helpText.scrollToReference(anchor);
					});
				}
			}
		});
		
		JScrollPane scrollPane = new JScrollPane(helpText);
		add(scrollPane, BorderLayout.CENTER);
	}
}