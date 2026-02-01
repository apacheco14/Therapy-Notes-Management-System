package com.alexpacheco.therapynotes.view.screens;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;

import com.alexpacheco.therapynotes.controller.AppController;

import java.awt.*;

/**
 * Panel displaying the Help Index / User Guide documentation. Provides comprehensive documentation for all application features.
 */
public class Pnl_HelpIndex extends JPanel
{
	private static final long serialVersionUID = -809216100843108450L;
	
	public Pnl_HelpIndex()
	{
		setLayout( new BorderLayout() );
		setBackground( AppController.getBackgroundColor() );
		
		JEditorPane helpText = new JEditorPane();
		helpText.setContentType( "text/html" );
		helpText.setEditable( false );
		helpText.setFont( new Font( "Arial", Font.PLAIN, 12 ) );
		helpText.setBackground( AppController.getBackgroundColor() );
		
		String helpContent = buildHelpContent();
		
		helpText.setText( helpContent );
		helpText.setCaretPosition( 0 );
		
		helpText.addHyperlinkListener( e ->
		{
			if( e.getEventType() == HyperlinkEvent.EventType.ACTIVATED )
			{
				String ref = e.getDescription();
				if( ref != null && ref.startsWith( "#" ) )
				{
					String anchor = ref.substring( 1 );
					SwingUtilities.invokeLater( () ->
					{
						helpText.scrollToReference( anchor );
					} );
				}
			}
		} );
		
		JScrollPane scrollPane = new JScrollPane( helpText );
		scrollPane.setBorder( BorderFactory.createEmptyBorder() );
		scrollPane.getViewport().setBackground( AppController.getBackgroundColor() );
		add( scrollPane, BorderLayout.CENTER );
	}
	
	/**
	 * Builds the complete HTML help content.
	 */
	private String buildHelpContent()
	{
		String titleColor = colorToHex( AppController.getTitleColor() );
		String subtitleColor = colorToHex( AppController.getSubtitleColor() );
		String bgColor = colorToHex( AppController.getBackgroundColor() );
		
		StringBuilder sb = new StringBuilder();
		
		// HTML Header and Styles
		sb.append( "<html><head><style>" );
		sb.append( "body { font-family: Arial, sans-serif; font-size: 12px; margin: 15px; background-color: " ).append( bgColor )
				.append( "; }" );
		sb.append( "h2.section-header { color: " ).append( titleColor )
				.append( "; font-size: 16px; margin-top: 25px; margin-bottom: 5px; " );
		sb.append( "border-bottom: 2px solid " ).append( titleColor ).append( "; padding-bottom: 5px; }" );
		sb.append( "h3.feature-header { color: " ).append( subtitleColor )
				.append( "; font-size: 13px; margin-top: 12px; margin-bottom: 5px; margin-left: 15px; text-decoration: underline; }" );
		sb.append( "h4.subsection-header { font-size: 12px; font-weight: bold; margin-top: 8px; margin-bottom: 3px; margin-left: 30px; }" );
		sb.append( ".quick-links { background-color: #ffffff; padding: 12px; border: 1px solid " ).append( subtitleColor )
				.append( "; margin-bottom: 15px; }" );
		sb.append( ".quick-links a { margin: 0 3px; }" );
		sb.append( ".tip-box { background-color: #e7f3ff; border: 1px solid #b3d9ff; padding: 10px; margin: 8px 30px; }" );
		sb.append( ".tip-label { font-weight: bold; }" );
		sb.append( "ul { margin-top: 3px; margin-bottom: 3px; margin-left: 30px; padding-left: 20px; }" );
		sb.append( "ul ul { margin-left: 0px; margin-top: 3px; margin-bottom: 3px; }" );
		sb.append( "li { margin-bottom: 4px; line-height: 1.4; }" );
		sb.append( "p { margin-left: 30px; margin-top: 5px; margin-bottom: 5px; line-height: 1.5; }" );
		sb.append( "</style></head><body>" );
		
		// Quick Links Bar (Alphabetical)
		sb.append( buildQuickLinks() );
		
		// Notes Section
		sb.append( buildNotesSection() );
		
		// Clients Section
		sb.append( buildClientsSection() );
		
		// Contacts Section
		sb.append( buildContactsSection() );
		
		// Settings Section
		sb.append( buildSettingsSection() );
		
		// Help Section
		sb.append( buildHelpSection() );
		
		// Logging Section
		sb.append( buildLoggingSection() );
		
		sb.append( "</body></html>" );
		
		return sb.toString();
	}
	
	/**
	 * Builds the quick links navigation bar.
	 */
	private String buildQuickLinks()
	{
		StringBuilder sb = new StringBuilder();
		sb.append( "<div class='quick-links'>" );
		sb.append( "<b>Quick Links:</b> " );
		sb.append( "<a href='#about'>About</a> | " );
		sb.append( "<a href='#configure-assessment'>Configure Assessment Options</a> | " );
		sb.append( "<a href='#edit-client'>Edit Client</a> | " );
		sb.append( "<a href='#edit-contact'>Edit Contact</a> | " );
		sb.append( "<a href='#export-logs'>Export Logs</a> | " );
		sb.append( "<a href='#export-notes'>Export Notes</a> | " );
		sb.append( "<a href='#help-index'>Help Index</a> | " );
		sb.append( "<a href='#new-client'>New Client</a> | " );
		sb.append( "<a href='#new-contact'>New Contact</a> | " );
		sb.append( "<a href='#new-note'>New Note</a> | " );
		sb.append( "<a href='#open-note'>Open Note</a> | " );
		sb.append( "<a href='#preferences'>Preferences</a> | " );
		sb.append( "<a href='#view-client'>View Client</a> | " );
		sb.append( "<a href='#view-logs'>View Logs</a>" );
		sb.append( "</div>" );
		return sb.toString();
	}
	
	/**
	 * Builds the Notes section content.
	 */
	private String buildNotesSection()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append( "<h2 class='section-header'>\uD83D\uDCDD Notes</h2>" );
		
		// New Note
		sb.append( "<a name='new-note'></a>" );
		sb.append( "<h3 class='feature-header'>New Note</h3>" );
		sb.append( "<p>Create a new therapy session note with five main sections: Session Information, Clinical Symptoms, " );
		sb.append( "Narrative, Mental Status, and Post-Session Administrative. Fields marked with an asterisk (*) are required; " );
		sb.append( "required fields can be configured in Preferences.</p>" );
		
		// Session Information
		sb.append( "<h4 class='subsection-header'>Session Information</h4>" );
		sb.append( "<ul>" );
		sb.append( "<li><b>Client</b> (required): Client selection dropdown with autocomplete functionality. Begin typing a client code " );
		sb.append( "or name to filter the available options. When a client is selected, the Date of Birth field " );
		sb.append( "is automatically populated. If enabled in Preferences, the Session Number and Diagnosis " );
		sb.append( "fields can also be auto-filled based on the client's previous visits.</li>" );
		sb.append( "<li><b>Date of Birth</b>: Read-only field that displays the selected client's date of birth.</li>" );
		sb.append( "<li><b>Diagnosis</b>: ICD-10 diagnosis field with autocomplete functionality. Begin typing a diagnosis code " );
		sb.append( "or description to filter the available options.</li>" );
		sb.append( "<li><b>Appointment Date</b>: Select the session date using the date picker. This field can be configured " );
		sb.append( "to default to today's date in Preferences.</li>" );
		sb.append( "<li><b>Session Number</b>: Numeric field for the session number. If enabled in Preferences, this will " );
		sb.append( "auto-increment based on the client's last recorded session.</li>" );
		sb.append( "<li><b>Length of Session</b>: Free text field to record the session duration.</li>" );
		sb.append( "<li><b>Appointment Comment</b>: Optional field for notes about the appointment itself.</li>" );
		sb.append( "<li><b>Virtual Appointment</b>: Checkbox to indicate whether the session was conducted virtually. " );
		sb.append( "The default value can be configured in Preferences.</li>" );
		sb.append( "</ul>" );
		
		// Tip Box
		sb.append( "<div class='tip-box'>" );
		sb.append( "<span class='tip-label'>\uD83D\uDCA1 TIP:</span> Many default values and required fields can be customized " );
		sb.append( "in the Preferences screen to match your clinical workflow." );
		sb.append( "</div>" );
		
		// Clinical Symptoms
		sb.append( "<h4 class='subsection-header'>Clinical Symptoms</h4>" );
		sb.append( "<ul>" );
		sb.append( "<li>This section displays checkboxes arranged in a grid format, allowing you to select multiple symptoms.</li>" );
		sb.append( "<li>Select all symptoms present in the past week that demonstrate medical necessity for the session.</li>" );
		sb.append( "<li>The available symptom options can be customized via Configure Assessment Options.</li>" );
		sb.append( "</ul>" );
		
		// Narrative
		sb.append( "<h4 class='subsection-header'>Narrative</h4>" );
		sb.append( "<ul>" );
		sb.append( "<li>Large text area for detailed session documentation.</li>" );
		sb.append( "<li>Supports word wrap for easier reading and editing of longer entries.</li>" );
		sb.append( "</ul>" );
		
		// Mental Status
		sb.append( "<h4 class='subsection-header'>Mental Status</h4>" );
		sb.append( "<ul>" );
		sb.append( "<li>Four assessment categories: <b>Appearance</b>, <b>Speech</b>, <b>Affect</b>, and <b>Eye Contact</b>.</li>" );
		sb.append( "<li>Each category uses radio buttons for single selection—only one option can be selected per category.</li>" );
		sb.append( "<li>To deselect a radio button, simply click the currently selected option again.</li>" );
		sb.append( "<li>Each category includes a comment field for additional observations or notes.</li>" );
		sb.append( "<li>The available options for each category can be customized via Configure Assessment Options.</li>" );
		sb.append( "</ul>" );
		
		// Post-Session Administrative
		sb.append( "<h4 class='subsection-header'>Post-Session Administrative</h4>" );
		sb.append( "<ul>" );
		sb.append( "<li><b>Referrals</b>: Uses checkboxes for multiple selections. Select all referrals made during or after " );
		sb.append( "the session. Includes a notes field for additional details.</li>" );
		sb.append( "<li><b>Collateral Contacts</b>: Uses checkboxes for multiple selections. Select all types of collateral " );
		sb.append( "contacts made. Includes a notes field for additional details.</li>" );
		sb.append( "<li><b>Next Appointment</b>: Uses radio buttons for single selection (click again to deselect). " );
		sb.append( "Includes a notes field for scheduling details.</li>" );
		sb.append( "</ul>" );
		
		// Certification
		sb.append( "<h4 class='subsection-header'>Certification</h4>" );
		sb.append( "<ul>" );
		sb.append( "<li>Check the certification box to confirm that the note information is accurate to the best of your knowledge.</li>" );
		sb.append( "<li>When the checkbox is selected, a timestamp is automatically recorded showing when certification occurred.</li>" );
		sb.append( "<li>The timestamp is editable and uses the format: MM/DD/YYYY HH:MM:SS AM/PM</li>" );
		sb.append( "<li>Unchecking the certification box will clear the timestamp.</li>" );
		sb.append( "</ul>" );
		
		// Export Tip Box
		sb.append( "<div class='tip-box'>" );
		sb.append( "<span class='tip-label'>\uD83D\uDCA1 TIP:</span> After saving your note, use the \"Export to DOCX\" or " );
		sb.append( "\"Export to PDF\" buttons in the footer to generate a formatted document for your records." );
		sb.append( "</div>" );
		
		// Open Note
		sb.append( "<a name='open-note'></a>" );
		sb.append( "<h3 class='feature-header'>Open Note</h3>" );
		sb.append( "<p>Search for and open existing therapy notes from the database. The search screen provides flexible " );
		sb.append( "filtering options:</p>" );
		sb.append( "<ul>" );
		sb.append( "<li><b>Client Selection</b>: Optionally filter notes by a specific client.</li>" );
		sb.append( "<li><b>Date Range</b>: Optionally filter notes within a start and end date.</li>" );
		sb.append( "<li>Both filters are optional—leave them blank to search all notes, or combine them to narrow your results.</li>" );
		sb.append( "</ul>" );
		
		// Export Notes
		sb.append( "<a name='export-notes'></a>" );
		sb.append( "<h3 class='feature-header'>Export Notes</h3>" );
		sb.append( "<p>Bulk export therapy notes to external document formats for record-keeping or sharing.</p>" );
		sb.append( "<ul>" );
		sb.append( "<li><b>Export Format</b>: Choose between PDF or DOCX format.</li>" );
		sb.append( "<li><b>Export Scope</b>: Select which notes to export—all notes, notes for a specific client, " );
		sb.append( "or notes within a specified date range.</li>" );
		sb.append( "<li><b>Output Folder</b>: Choose the destination folder where exported files will be saved.</li>" );
		sb.append( "<li><b>File Naming</b>: Select a naming convention for the exported files.</li>" );
		sb.append( "</ul>" );
		
		return sb.toString();
	}
	
	/**
	 * Builds the Clients section content.
	 */
	private String buildClientsSection()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append( "<h2 class='section-header'>\uD83D\uDC64 Clients</h2>" );
		
		// New Client
		sb.append( "<a name='new-client'></a>" );
		sb.append( "<h3 class='feature-header'>New Client</h3>" );
		sb.append( "<p>Add a new client to the system by entering their information:</p>" );
		sb.append( "<ul>" );
		sb.append( "<li><b>First Name</b>: Client's first name.</li>" );
		sb.append( "<li><b>Last Name</b>: Client's last name.</li>" );
		sb.append( "<li><b>Client Code</b>: A unique identifier for the client, used for reference throughout the application. " );
		sb.append( "Note that the Client Code serves as a unique identifier and cannot be changed.</li>" );
		sb.append( "<li><b>Date of Birth</b>: Client's date of birth.</li>" );
		sb.append( "<li><b>Inactive</b>: Checkbox used to hide the Client from application lookups.</li>" );
		sb.append( "<li><b>Email</b>: Client's email address.</li>" );
		sb.append( "<li><b>Phone Number</b>: Client's phone number.</li>" );
		sb.append( "</ul>" );
		
		// Edit Client
		sb.append( "<a name='edit-client'></a>" );
		sb.append( "<h3 class='feature-header'>Edit Client</h3>" );
		sb.append( "<p>Modify existing client information including first name, last name, date of birth, email address, " );
		sb.append( "and phone number. Use this screen to keep client records current and accurate. Note that the Client Code " );
		sb.append( "serves as a unique identifier and cannot be changed.</p>" );
		
		// View Client
		sb.append( "<a name='view-client'></a>" );
		sb.append( "<h3 class='feature-header'>View Client</h3>" );
		sb.append( "<p>Display a read-only summary of client information. This screen also shows all emergency contacts " );
		sb.append( "associated with the client and a list of session notes, providing a quick overview of the client's " );
		sb.append( "record and their designated contacts.</p>" );
		
		return sb.toString();
	}
	
	/**
	 * Builds the Contacts section content.
	 */
	private String buildContactsSection()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append( "<h2 class='section-header'>\uD83D\uDCDE Contacts</h2>" );
		
		// New Contact
		sb.append( "<a name='new-contact'></a>" );
		sb.append( "<h3 class='feature-header'>New Contact</h3>" );
		sb.append( "<p>Create a new emergency contact record linked to a specific client. Emergency contacts are individuals " );
		sb.append( "who may be contacted regarding the client when necessary.</p>" );
		sb.append( "<ul>" );
		sb.append( "<li><b>First Name</b>: Contact's first name.</li>" );
		sb.append( "<li><b>Last Name</b>: Contact's last name.</li>" );
		sb.append( "<li><b>Linked Client</b>: The client this contact is associated with.</li>" );
		sb.append( "<li><b>Email</b>: Contact's email address.</li>" );
		sb.append( "<li><b>Phone Number</b>: Contact's phone number.</li>" );
		sb.append( "</ul>" );
		
		// Edit Contact
		sb.append( "<a name='edit-contact'></a>" );
		sb.append( "<h3 class='feature-header'>Edit Contact</h3>" );
		sb.append( "<p>Update existing emergency contact information including name, email address, and phone " );
		sb.append( "number. Use this screen to maintain accurate emergency contact details for your clients.</p>" );
		
		return sb.toString();
	}
	
	/**
	 * Builds the Settings section content.
	 */
	private String buildSettingsSection()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append( "<h2 class='section-header'>\u2699\uFE0F Settings</h2>" );
		
		// Preferences
		sb.append( "<a name='preferences'></a>" );
		sb.append( "<h3 class='feature-header'>Preferences</h3>" );
		sb.append( "<p>Configure application-wide settings to streamline your workflow:</p>" );
		sb.append( "<ul>" );
		sb.append( "<li><b>Required Fields</b>: Specify which fields must be completed before saving a note, client, or contact.</li>" );
		sb.append( "<li><b>Default Values</b>: Set default values for commonly used fields, such as:" );
		sb.append( "<ul>" );
		sb.append( "<li>Default Appointment Date to today's date</li>" );
		sb.append( "<li>Auto-populate Session Number from client's previous session</li>" );
		sb.append( "<li>Auto-populate Diagnosis from client's previous session</li>" );
		sb.append( "<li>Default Virtual Appointment checkbox state</li>" );
		sb.append( "</ul></li>" );
		sb.append( "</ul>" );
		sb.append( "<p>These preferences help reduce repetitive data entry and ensure consistency across your documentation.</p>" );
		
		// Configure Assessment Options
		sb.append( "<a name='configure-assessment'></a>" );
		sb.append( "<h3 class='feature-header'>Configure Assessment Options</h3>" );
		sb.append( "<p>Customize the assessment options available in notes. This allows you to " );
		sb.append( "tailor the application to your specific clinical workflow and terminology. " );
		sb.append( "Any assessment options that have been used in a note will not be able to be deleted.</p>" );
		sb.append( "<ul>" );
		sb.append( "<li>Clinical Symptoms</li>" );
		sb.append( "<li>Appearance</li>" );
		sb.append( "<li>Speech</li>" );
		sb.append( "<li>Affect</li>" );
		sb.append( "<li>Eye Contact</li>" );
		sb.append( "<li>Referrals</li>" );
		sb.append( "<li>Collateral Contacts</li>" );
		sb.append( "<li>Next Appointment</li>" );
		sb.append( "</ul>" );
		
		return sb.toString();
	}
	
	/**
	 * Builds the Help section content.
	 */
	private String buildHelpSection()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append( "<h2 class='section-header'>\u2753 Help</h2>" );
		
		// About
		sb.append( "<a name='about'></a>" );
		sb.append( "<h3 class='feature-header'>About</h3>" );
		sb.append( "<p>View information about the application including the version number, purpose, and developer information. " );
		sb.append( "This screen provides details about the current release and application credits.</p>" );
		
		// Help Index
		sb.append( "<a name='help-index'></a>" );
		sb.append( "<h3 class='feature-header'>Help Index</h3>" );
		sb.append( "<p>Access this help documentation for guidance on using all application features. Use the Quick Links " );
		sb.append( "at the top of the page to jump directly to specific topics. This documentation covers all major features " );
		sb.append( "including notes, clients, contacts, settings, and system logging.</p>" );
		
		return sb.toString();
	}
	
	/**
	 * Builds the Logging section content.
	 */
	private String buildLoggingSection()
	{
		StringBuilder sb = new StringBuilder();
		
		sb.append( "<h2 class='section-header'>\uD83D\uDCCB Logging</h2>" );
		
		// View Logs
		sb.append( "<a name='view-logs'></a>" );
		sb.append( "<h3 class='feature-header'>View Logs</h3>" );
		sb.append( "<p>View application logs directly within the application. The log viewer displays:</p>" );
		sb.append( "<ul>" );
		sb.append( "<li><b>Informational/Trace Messages</b>: General application activity and operations.</li>" );
		sb.append( "<li><b>Error Logs</b>: Any errors or exceptions that have occurred.</li>" );
		sb.append( "</ul>" );
		sb.append( "<p>This is useful for troubleshooting issues, reviewing recent application activity, or diagnosing " );
		sb.append( "unexpected behavior.</p>" );
		
		// Export Logs
		sb.append( "<a name='export-logs'></a>" );
		sb.append( "<h3 class='feature-header'>Export Logs</h3>" );
		sb.append( "<p>Export application logs to a CSV file for external review, record-keeping, or sharing with " );
		sb.append( "technical support.</p>" );
		sb.append( "<ul>" );
		sb.append( "<li><b>Output Folder</b>: Choose the destination folder where the CSV log file will be saved.</li>" );
		sb.append( "<li>The exported file contains timestamped entries of application activity and any errors.</li>" );
		sb.append( "</ul>" );
		
		return sb.toString();
	}
	
	/**
	 * Converts a Color object to a hex string for use in HTML/CSS.
	 * 
	 * @param color The Color to convert
	 * @return Hex string in format #RRGGBB
	 */
	private String colorToHex( Color color )
	{
		return String.format( "#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue() );
	}
}