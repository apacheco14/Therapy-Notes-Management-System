package main.java.com.alexpacheco.therapynotes.util.export;

import java.awt.Color;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import main.java.com.alexpacheco.therapynotes.controller.errorhandling.exceptions.TherapyAppException;
import main.java.com.alexpacheco.therapynotes.model.entities.Client;
import main.java.com.alexpacheco.therapynotes.model.entities.CollateralContact;
import main.java.com.alexpacheco.therapynotes.model.entities.Note;
import main.java.com.alexpacheco.therapynotes.model.entities.Referral;
import main.java.com.alexpacheco.therapynotes.model.entities.Symptom;
import main.java.com.alexpacheco.therapynotes.util.DateFormatUtil;
import main.java.com.alexpacheco.therapynotes.util.JavaUtils;

/**
 * Abstract base class for exporting therapy progress notes to various document formats.
 * Provides shared constants, color definitions, date formatters, and file chooser functionality.
 * Subclasses implement the actual document generation for specific formats (DOCX, PDF, etc.).
 */
public abstract class AbstractNoteExporter
{
	// ===== Page Dimensions (US Letter) =====
	
	/** Page width in points (8.5 inches) */
	protected static final float PAGE_WIDTH_POINTS = 612f;
	
	/** Page height in points (11 inches) */
	protected static final float PAGE_HEIGHT_POINTS = 792f;
	
	/** Page width in twips/DXA (8.5 inches) - for OOXML formats */
	protected static final int PAGE_WIDTH_TWIPS = 12240;
	
	/** Page height in twips/DXA (11 inches) - for OOXML formats */
	protected static final int PAGE_HEIGHT_TWIPS = 15840;
	
	/** Standard margin in points (1 inch) */
	protected static final float MARGIN_POINTS = 72f;
	
	/** Standard margin in twips/DXA (1 inch) - for OOXML formats */
	protected static final int MARGIN_TWIPS = 1440;
	
	// ===== Color Definitions =====
	
	/** Header/section title text color - dark blue */
	protected static final int HEADER_TEXT_RGB = 0x1A5276;
	
	/** Label text color - gray */
	protected static final int LABEL_TEXT_RGB = 0x5D6D7E;
	
	/** Certified status color - green */
	protected static final int CERTIFIED_RGB = 0x27AE60;
	
	/** Table header background color - light blue */
	protected static final int TABLE_HEADER_BG_RGB = 0xE8F4F8;
	
	/** Table/divider border color - light gray */
	protected static final int BORDER_RGB = 0xBDC3C7;
	
	// ===== Font Sizes =====
	
	/** Title font size in points */
	protected static final float TITLE_FONT_SIZE = 18f;
	
	/** Section heading font size in points */
	protected static final float SECTION_FONT_SIZE = 13f;
	
	/** Body text font size in points */
	protected static final float BODY_FONT_SIZE = 11f;
	
	/** Small text font size in points (tables, notes) */
	protected static final float SMALL_FONT_SIZE = 10f;
	
	/** Footer font size in points */
	protected static final float FOOTER_FONT_SIZE = 9f;
	
	// ===== Spacing =====
	
	/** Space before a new section */
	protected static final float SECTION_SPACING = 20f;
	
	/** Space between paragraphs */
	protected static final float PARAGRAPH_SPACING = 12f;
	
	/** Line height multiplier */
	protected static final float LINE_SPACING = 1.4f;
	
	// ===== Date Formatters =====
	
	/** Full date/time format for timestamps */
	protected static final DateTimeFormatter DATE_TIME_FORMATTER = 
			DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' h:mm a");
	
	/** Appointment date format (includes day of week) */
	protected static final DateTimeFormatter APPT_DATE_FORMATTER = 
			DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
	
	/** Simple date format */
	protected static final DateTimeFormatter DATE_FORMATTER = 
			DateTimeFormatter.ofPattern("MMMM d, yyyy");
	
	// ===== Preferred Font =====
	
	/** Preferred font family name */
	protected static final String PREFERRED_FONT = "Arial";
	
	/** Fallback font for PDF (closest standard font to Arial) */
	protected static final String PDF_FALLBACK_FONT = "Helvetica";
	
	// ===== Color Utility Methods =====
	
	/**
	 * Converts an RGB integer to a hex string (for OOXML formats).
	 * 
	 * @param rgb The RGB color value
	 * @return Hex string without # prefix (e.g., "1A5276")
	 */
	protected static String toHexString(int rgb)
	{
		return String.format("%06X", rgb & 0xFFFFFF);
	}
	
	/**
	 * Converts an RGB integer to a java.awt.Color (for PDF/Graphics).
	 * 
	 * @param rgb The RGB color value
	 * @return Color object
	 */
	protected static Color toAwtColor(int rgb)
	{
		return new Color(rgb);
	}
	
	/**
	 * Extracts the red component from an RGB integer.
	 */
	protected static int getRed(int rgb)
	{
		return (rgb >> 16) & 0xFF;
	}
	
	/**
	 * Extracts the green component from an RGB integer.
	 */
	protected static int getGreen(int rgb)
	{
		return (rgb >> 8) & 0xFF;
	}
	
	/**
	 * Extracts the blue component from an RGB integer.
	 */
	protected static int getBlue(int rgb)
	{
		return rgb & 0xFF;
	}
	
	// ===== File Export Methods =====
	
	/**
	 * Returns the file extension for this exporter (without the dot).
	 * 
	 * @return File extension (e.g., "docx", "pdf")
	 */
	protected abstract String getFileExtension();
	
	/**
	 * Returns the file type description for the file chooser.
	 * 
	 * @return Description (e.g., "Word Documents (*.docx)")
	 */
	protected abstract String getFileTypeDescription();
	
	/**
	 * Returns the dialog title for the file chooser.
	 * 
	 * @return Dialog title
	 */
	protected String getDialogTitle()
	{
		return "Save Progress Note";
	}
	
	/**
	 * Exports the note to the specified output path.
	 * Subclasses implement the actual document generation.
	 * 
	 * @param note       The note to export
	 * @param outputPath The full path where the file should be saved
	 * @throws TherapyAppException If export fails
	 */
	protected abstract void doExport(Note note, String outputPath) throws TherapyAppException;
	
	/**
	 * Exports a note with a file chooser dialog to let the user select the save location.
	 * This method is shared by all exporters - only the abstract methods differ.
	 * 
	 * @param note            The note to export
	 * @param outputDirectory The default directory
	 * @param filename        The default filename (should include appropriate extension)
	 * @return The path to the created file, or null if the user cancelled
	 * @throws TherapyAppException If export fails
	 */
	protected String exportWithFileChooser(Note note, File outputDirectory, String filename) throws TherapyAppException
	{
		File defaultFile = new File(outputDirectory, filename);
		
		// Create file chooser with defaults
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle(getDialogTitle());
		fileChooser.setSelectedFile(defaultFile);
		fileChooser.setFileFilter(new FileNameExtensionFilter(getFileTypeDescription(), getFileExtension()));
		
		// Show save dialog
		int result = fileChooser.showSaveDialog(null);
		
		if (result != JFileChooser.APPROVE_OPTION)
		{
			return null; // User cancelled
		}
		
		File selectedFile = fileChooser.getSelectedFile();
		
		// Ensure correct extension
		String outputPath = selectedFile.getAbsolutePath();
		String extension = "." + getFileExtension();
		if (!outputPath.toLowerCase().endsWith(extension))
		{
			outputPath += extension;
		}
		
		// Check for overwrite
		File finalFile = new File(outputPath);
		if (finalFile.exists())
		{
			int overwrite = JOptionPane.showConfirmDialog(
					null,
					"The file \"" + finalFile.getName() + "\" already exists.\nDo you want to replace it?",
					"Confirm Overwrite",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE);
			
			if (overwrite != JOptionPane.YES_OPTION)
			{
				return null; // User chose not to overwrite
			}
		}
		
		// Perform the actual export
		doExport(note, outputPath);
		return outputPath;
	}
	
	// ===== Text Utility Methods =====
	
	/**
	 * Returns the value or a default string if null/empty.
	 * 
	 * @param value        The value to check
	 * @param defaultValue The default to return if value is null/empty
	 * @return The value or default
	 */
	protected static String getOrDefault(String value, String defaultValue)
	{
		return JavaUtils.isNullOrEmpty(value) ? defaultValue : value;
	}
	
	/**
	 * Formats a client's full name from first and last name.
	 * 
	 * @param firstName The first name (may be null)
	 * @param lastName  The last name (may be null)
	 * @return The formatted full name, or "Unknown" if both are empty
	 */
	protected static String formatClientName(String firstName, String lastName)
	{
		String first = firstName != null ? firstName : "";
		String last = lastName != null ? lastName : "";
		String fullName = (first + " " + last).trim();
		return fullName.isEmpty() ? "Unknown" : fullName;
	}
	
	// ===== Data Preparation =====
	
	/**
	 * Prepares all display values from a Note entity.
	 * Centralizes all "what to display" decisions so subclasses only handle rendering.
	 * 
	 * @param note The note to prepare for export
	 * @return A NoteExportData object containing all pre-processed display values
	 */
	protected static NoteExportData prepareExportData(Note note)
	{
		NoteExportData data = new NoteExportData();
		
		// Certification
		data.isCertified = note.getCertifiedDate() != null;
		data.certificationText = data.isCertified
				? "I certify that this information is accurate to the best of my knowledge. "
						+ note.getCertifiedDate().format(DATE_FORMATTER)
				: null;
		
		// Client Information
		Client client = note.getClient();
		if (client != null)
		{
			data.clientName = formatClientName(client.getFirstName(), client.getLastName());
			data.clientCode = getOrDefault(client.getClientCode(), "N/A");
			data.dateOfBirth = client.getDateOfBirth() != null 
					? DateFormatUtil.toSimpleString(client.getDateOfBirth()) 
					: "Not specified";
		}
		else
		{
			data.clientName = "Unknown";
			data.clientCode = "N/A";
			data.dateOfBirth = "Not specified";
		}
		
		// Session Information
		data.apptDate = note.getApptDateTime() != null 
				? note.getApptDateTime().format(APPT_DATE_FORMATTER) 
				: "Not specified";
		data.sessionType = note.isVirtualAppt() ? "Virtual/Telehealth" : "In-Person";
		data.sessionNumber = note.getSessionNumber() != null 
				? String.valueOf(note.getSessionNumber()) 
				: "Not specified";
		data.sessionLength = getOrDefault(note.getSessionLength(), "Not specified");
		data.diagnosis = getOrDefault(note.getDiagnosis(), "Not specified");
		data.apptComment = note.getApptComment();
		data.hasApptComment = !JavaUtils.isNullOrEmpty(note.getApptComment());
		
		// Narrative
		data.narrative = note.getNarrative();
		data.hasNarrative = !JavaUtils.isNullOrEmpty(note.getNarrative());
		
		// Symptoms
		data.symptoms = note.getSymptoms();
		data.hasSymptoms = data.symptoms != null && !data.symptoms.isEmpty();
		if (data.hasSymptoms)
		{
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < data.symptoms.size(); i++)
			{
				Symptom symptom = data.symptoms.get(i);
				String symptomName = symptom.getSymptomName();
				if (JavaUtils.isNullOrEmpty(symptomName))
				{
					symptomName = "Symptom #" + symptom.getSymptomId();
				}
				if (i > 0)
				{
					sb.append(", ");
				}
				sb.append(symptomName);
			}
			data.symptomsText = sb.toString();
		}
		else
		{
			data.symptomsText = "No symptoms recorded.";
		}
		
		// Mental Status Assessment
		data.appearanceValue = note.getAppearance() != null ? note.getAppearance().getName() : "Not assessed";
		data.appearanceComment = getOrDefault(note.getAppearanceComment(), "-");
		data.speechValue = note.getSpeech() != null ? note.getSpeech().getName() : "Not assessed";
		data.speechComment = getOrDefault(note.getSpeechComment(), "-");
		data.affectValue = note.getAffect() != null ? note.getAffect().getName() : "Not assessed";
		data.affectComment = getOrDefault(note.getAffectComment(), "-");
		data.eyeContactValue = note.getEyeContact() != null ? note.getEyeContact().getName() : "Not assessed";
		data.eyeContactComment = getOrDefault(note.getEyeContactComment(), "-");
		
		// Collateral Contacts
		data.collateralContacts = note.getCollateralContacts();
		data.hasCollateralContacts = data.collateralContacts != null && !data.collateralContacts.isEmpty();
		data.collateralContactComment = note.getCollateralContactComment();
		data.hasCollateralContactComment = !JavaUtils.isNullOrEmpty(note.getCollateralContactComment());
		
		// Referrals
		data.referrals = note.getReferrals();
		data.hasReferrals = data.referrals != null && !data.referrals.isEmpty();
		data.referralComment = note.getReferralComment();
		data.hasReferralComment = !JavaUtils.isNullOrEmpty(note.getReferralComment());
		
		// Follow-Up
		data.nextAppt = note.getNextAppt() != null ? note.getNextAppt().getName() : "Not specified";
		data.nextApptComment = note.getNextApptComment();
		data.hasNextApptComment = !JavaUtils.isNullOrEmpty(note.getNextApptComment());
		
		// Footer
		data.noteId = note.getNoteId() != null ? String.valueOf(note.getNoteId()) : "N/A";
		data.generatedTimestamp = java.time.LocalDateTime.now().format(DATE_TIME_FORMATTER);
		
		return data;
	}
	
	/**
	 * Gets the display name for a collateral contact, with fallback.
	 */
	protected static String getCollateralContactDisplayName(CollateralContact contact)
	{
		String name = contact.getCollateralContactName();
		if (JavaUtils.isNullOrEmpty(name))
		{
			return "Contact Type #" + contact.getCollateralContactTypeId();
		}
		return name;
	}
	
	/**
	 * Gets the display name for a referral, with fallback.
	 */
	protected static String getReferralDisplayName(Referral referral)
	{
		String name = referral.getReferralName();
		if (JavaUtils.isNullOrEmpty(name))
		{
			return "Referral Type #" + referral.getReferralTypeId();
		}
		return name;
	}
	
	// ===== Data Transfer Object =====
	
	/**
	 * Contains all pre-processed display values for a note export.
	 * Subclasses use this to render without duplicating decision logic.
	 */
	protected static class NoteExportData
	{
		// Certification
		public boolean isCertified;
		public String certificationText;
		
		// Client Information
		public String clientName;
		public String clientCode;
		public String dateOfBirth;
		
		// Session Information
		public String apptDate;
		public String sessionType;
		public String sessionNumber;
		public String sessionLength;
		public String diagnosis;
		public String apptComment;
		public boolean hasApptComment;
		
		// Narrative
		public String narrative;
		public boolean hasNarrative;
		
		// Symptoms
		public List<Symptom> symptoms;
		public boolean hasSymptoms;
		public String symptomsText;
		
		// Mental Status Assessment
		public String appearanceValue;
		public String appearanceComment;
		public String speechValue;
		public String speechComment;
		public String affectValue;
		public String affectComment;
		public String eyeContactValue;
		public String eyeContactComment;
		
		// Collateral Contacts
		public List<CollateralContact> collateralContacts;
		public boolean hasCollateralContacts;
		public String collateralContactComment;
		public boolean hasCollateralContactComment;
		
		// Referrals
		public List<Referral> referrals;
		public boolean hasReferrals;
		public String referralComment;
		public boolean hasReferralComment;
		
		// Follow-Up
		public String nextAppt;
		public String nextApptComment;
		public boolean hasNextApptComment;
		
		// Footer
		public String noteId;
		public String generatedTimestamp;
		
		/**
		 * Builds the mental status table data array.
		 * Useful for both PDF tables and DOCX tables.
		 */
		public String[][] getMentalStatusTableData()
		{
			return new String[][] {
				{ "Category", "Assessment", "Comments" },
				{ "Appearance", appearanceValue, appearanceComment },
				{ "Speech", speechValue, speechComment },
				{ "Affect", affectValue, affectComment },
				{ "Eye Contact", eyeContactValue, eyeContactComment }
			};
		}
		
		public String getFooterText()
		{
			return "Note ID: " + noteId + "  |  Generated: " + generatedTimestamp;
		}
	}
}