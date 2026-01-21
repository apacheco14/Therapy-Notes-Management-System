package main.java.com.alexpacheco.therapynotes.util.export;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;

import main.java.com.alexpacheco.therapynotes.controller.AppController;
import main.java.com.alexpacheco.therapynotes.controller.enums.ErrorCode;
import main.java.com.alexpacheco.therapynotes.controller.enums.LogLevel;
import main.java.com.alexpacheco.therapynotes.controller.errorhandling.exceptions.TherapyAppException;
import main.java.com.alexpacheco.therapynotes.model.entities.CollateralContact;
import main.java.com.alexpacheco.therapynotes.model.entities.Note;
import main.java.com.alexpacheco.therapynotes.model.entities.Referral;

/**
 * Exports therapy progress notes to PDF format. Creates professionally formatted documents with all
 * note sections including client info, session details, symptoms, narrative, mental status, and
 * administrative information.
 */
public class NotePdfExporter extends AbstractNoteExporter
{
	// Singleton instance for non-static access to inherited methods
	private static final NotePdfExporter INSTANCE = new NotePdfExporter();
	
	// Derived constants from parent class
	private static final float CONTENT_WIDTH = PAGE_WIDTH_POINTS - (2 * MARGIN_POINTS);
	
	// Colors converted from parent RGB constants
	private static final Color HEADER_TEXT_COLOR = toAwtColor(HEADER_TEXT_RGB);
	private static final Color LABEL_TEXT_COLOR = toAwtColor(LABEL_TEXT_RGB);
	private static final Color CERTIFIED_COLOR = toAwtColor(CERTIFIED_RGB);
	private static final Color TABLE_HEADER_BG = toAwtColor(TABLE_HEADER_BG_RGB);
	private static final Color BORDER_COLOR = toAwtColor(BORDER_RGB);
	
	// Fonts (initialized during export)
	private static PDFont fontRegular;
	private static PDFont fontBold;
	private static PDFont fontItalic;
	
	// ===== Abstract Method Implementations =====
	
	@Override
	protected String getFileExtension()
	{
		return "pdf";
	}
	
	@Override
	protected String getFileTypeDescription()
	{
		return "PDF Documents (*.pdf)";
	}
	
	@Override
	protected void doExport(Note note, String outputPath) throws TherapyAppException
	{
		exportToPdf(note, outputPath);
	}
	
	// ===== Public Static Methods =====
	
	/**
	 * Exports a single note to a PDF file.
	 * 
	 * @param note       The note to export
	 * @param outputPath The path where the document should be saved
	 * @throws TherapyAppException If export fails
	 */
	public static void exportToPdf(Note note, String outputPath) throws TherapyAppException
	{
		if (note == null)
		{
			throw new TherapyAppException("Cannot export null note", ErrorCode.REQ_MISSING);
		}
		
		// Prepare all display data
		NoteExportData data = prepareExportData(note);
		
		try (PDDocument document = new PDDocument())
		{
			// Initialize fonts
			fontRegular = new PDType1Font(Standard14Fonts.FontName.HELVETICA);
			fontBold = new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD);
			fontItalic = new PDType1Font(Standard14Fonts.FontName.HELVETICA_OBLIQUE);
			
			// Create PDF content builder and build document
			PdfContentBuilder builder = new PdfContentBuilder(document);
			buildDocument(builder, data);
			builder.close();
			
			// Save to file
			File outputFile = new File(outputPath);
			outputFile.getParentFile().mkdirs();
			document.save(outputFile);
			AppController.logToDatabase(LogLevel.INFO, "NotePdfExporter", "Note ID " + note.getNoteId() + " exported to " + outputFile);
		}
		catch (IOException e)
		{
			throw new TherapyAppException("Failed to export note to PDF: " + e.getMessage(), ErrorCode.DB_ERROR);
		}
	}
	
	/**
	 * Exports a note to a PDF with a file chooser dialog.
	 * 
	 * @param note            The note to export
	 * @param outputDirectory The default directory
	 * @param filename        The default filename
	 * @return The path to the created file, or null if cancelled
	 * @throws TherapyAppException If export fails
	 */
	public static String exportToPdf(Note note, File outputDirectory, String filename) throws TherapyAppException
	{
		return INSTANCE.exportWithFileChooser(note, outputDirectory, filename);
	}
	
	// ===== Document Building =====
	
	/**
	 * Builds all document sections using pre-processed export data.
	 */
	private static void buildDocument(PdfContentBuilder builder, NoteExportData data) throws IOException
	{
		addDocumentHeader(builder, data);
		addClientInfoSection(builder, data);
		addSessionInfoSection(builder, data);
		addNarrativeSection(builder, data);
		addSymptomsSection(builder, data);
		addMentalStatusSection(builder, data);
		addCollateralContactsSection(builder, data);
		addReferralsSection(builder, data);
		addFollowUpSection(builder, data);
		addDocumentFooter(builder, data);
	}
	
	/**
	 * Adds the document title and certification status.
	 */
	private static void addDocumentHeader(PdfContentBuilder builder, NoteExportData data) throws IOException
	{
		// Title
		builder.setFont(fontBold, TITLE_FONT_SIZE);
		builder.setColor(HEADER_TEXT_COLOR);
		builder.addCenteredText("THERAPY PROGRESS NOTE");
		builder.addVerticalSpace(10);
		
		// Certification status
		if (data.isCertified)
		{
			builder.setFont(fontItalic, SMALL_FONT_SIZE);
			builder.setColor(CERTIFIED_COLOR);
			builder.addCenteredText(data.certificationText);
		}
		
		builder.addVerticalSpace(SECTION_SPACING);
	}
	
	/**
	 * Adds the client information section.
	 */
	private static void addClientInfoSection(PdfContentBuilder builder, NoteExportData data) throws IOException
	{
		addSectionHeading(builder, "Client Information");
		
		addLabelValuePair(builder, "Client", data.clientName);
		addLabelValuePair(builder, "Client Code", data.clientCode);
		addLabelValuePair(builder, "Date of Birth", data.dateOfBirth);
	}
	
	/**
	 * Adds the session information section.
	 */
	private static void addSessionInfoSection(PdfContentBuilder builder, NoteExportData data) throws IOException
	{
		addSectionHeading(builder, "Session Information");
		
		addLabelValuePair(builder, "Appointment Date", data.apptDate);
		addLabelValuePair(builder, "Session Type", data.sessionType);
		addLabelValuePair(builder, "Session Number", data.sessionNumber);
		addLabelValuePair(builder, "Session Length", data.sessionLength);
		addLabelValuePair(builder, "Diagnosis", data.diagnosis);
		
		if (data.hasApptComment)
		{
			addLabelValuePair(builder, "Appointment Comment", data.apptComment);
		}
	}
	
	/**
	 * Adds the narrative section.
	 */
	private static void addNarrativeSection(PdfContentBuilder builder, NoteExportData data) throws IOException
	{
		addSectionHeading(builder, "Session Narrative");
		
		if (!data.hasNarrative)
		{
			builder.setFont(fontItalic, BODY_FONT_SIZE);
			builder.setColor(LABEL_TEXT_COLOR);
			builder.addWrappedText("No narrative recorded.");
		}
		else
		{
			builder.setFont(fontRegular, BODY_FONT_SIZE);
			builder.setColor(Color.BLACK);
			
			String[] paragraphs = data.narrative.split("\n");
			for (String para : paragraphs)
			{
				if (!para.trim().isEmpty())
				{
					builder.addWrappedText(para.trim());
					builder.addVerticalSpace(PARAGRAPH_SPACING);
				}
			}
		}
	}
	
	/**
	 * Adds the symptoms section.
	 */
	private static void addSymptomsSection(PdfContentBuilder builder, NoteExportData data) throws IOException
	{
		addSectionHeading(builder, "Presenting Symptoms");
		
		if (!data.hasSymptoms)
		{
			builder.setFont(fontItalic, BODY_FONT_SIZE);
			builder.setColor(LABEL_TEXT_COLOR);
		}
		else
		{
			builder.setFont(fontRegular, BODY_FONT_SIZE);
			builder.setColor(Color.BLACK);
		}
		
		builder.addWrappedText(data.symptomsText);
	}
	
	/**
	 * Adds the mental status section as a table.
	 */
	private static void addMentalStatusSection(PdfContentBuilder builder, NoteExportData data) throws IOException
	{
		addSectionHeading(builder, "Mental Status Assessment");
		
		float[] columnWidths = { CONTENT_WIDTH * 0.25f, CONTENT_WIDTH * 0.30f, CONTENT_WIDTH * 0.45f };
		builder.addTable(data.getMentalStatusTableData(), columnWidths, fontBold, fontRegular, SMALL_FONT_SIZE, TABLE_HEADER_BG,
				BORDER_COLOR);
		
		builder.addVerticalSpace(PARAGRAPH_SPACING);
	}
	
	/**
	 * Adds the collateral contacts section.
	 */
	private static void addCollateralContactsSection(PdfContentBuilder builder, NoteExportData data) throws IOException
	{
		if (!data.hasCollateralContacts)
		{
			return;
		}
		
		addSectionHeading(builder, "Collateral Contacts");
		
		for (CollateralContact contact : data.collateralContacts)
		{
			builder.setFont(fontBold, BODY_FONT_SIZE);
			builder.setColor(Color.BLACK);
			builder.addText(getCollateralContactDisplayName(contact));
			builder.addVerticalSpace(6);
		}
		
		if (data.hasCollateralContactComment)
		{
			builder.setFont(fontRegular, BODY_FONT_SIZE);
			builder.setColor(Color.BLACK);
			builder.addWrappedText(data.collateralContactComment);
		}
	}
	
	/**
	 * Adds the referrals section.
	 */
	private static void addReferralsSection(PdfContentBuilder builder, NoteExportData data) throws IOException
	{
		if (!data.hasReferrals)
		{
			return;
		}
		
		addSectionHeading(builder, "Referrals Made");
		
		for (Referral referral : data.referrals)
		{
			builder.setFont(fontBold, BODY_FONT_SIZE);
			builder.setColor(Color.BLACK);
			builder.addText(getReferralDisplayName(referral));
			builder.addVerticalSpace(6);
		}
		
		if (data.hasReferralComment)
		{
			builder.setFont(fontRegular, BODY_FONT_SIZE);
			builder.setColor(Color.BLACK);
			builder.addWrappedText(data.referralComment);
		}
	}
	
	/**
	 * Adds the follow-up section.
	 */
	private static void addFollowUpSection(PdfContentBuilder builder, NoteExportData data) throws IOException
	{
		addSectionHeading(builder, "Follow-Up");
		
		addLabelValuePair(builder, "Next Appointment", data.nextAppt);
		
		if (data.hasNextApptComment)
		{
			addLabelValuePair(builder, "Comment", data.nextApptComment);
		}
	}
	
	/**
	 * Adds the document footer.
	 */
	private static void addDocumentFooter(PdfContentBuilder builder, NoteExportData data) throws IOException
	{
		builder.addVerticalSpace(SECTION_SPACING);
		
		// Divider line
		builder.setColor(BORDER_COLOR);
		builder.addHorizontalLine();
		builder.addVerticalSpace(10);
		
		builder.setFont(fontRegular, FOOTER_FONT_SIZE);
		builder.setColor(LABEL_TEXT_COLOR);
		builder.addText(data.getFooterText());
	}
	
	// ===== Helper Methods =====
	
	/**
	 * Adds a section heading.
	 */
	private static void addSectionHeading(PdfContentBuilder builder, String text) throws IOException
	{
		builder.addVerticalSpace(SECTION_SPACING);
		builder.setFont(fontBold, SECTION_FONT_SIZE);
		builder.setColor(HEADER_TEXT_COLOR);
		builder.addTextWithUnderline(text);
		builder.addVerticalSpace(10);
	}
	
	/**
	 * Adds a label-value pair.
	 */
	private static void addLabelValuePair(PdfContentBuilder builder, String label, String value) throws IOException
	{
		builder.setFont(fontBold, BODY_FONT_SIZE);
		builder.setColor(LABEL_TEXT_COLOR);
		float labelWidth = builder.getTextWidth(label + ": ");
		builder.addText(label + ": ");
		
		builder.setFont(fontRegular, BODY_FONT_SIZE);
		builder.setColor(Color.BLACK);
		builder.addTextAtOffset(value != null ? value : "Not specified", labelWidth);
		builder.addVerticalSpace(6);
	}
	
	// ===== Inner Class: PDF Content Builder =====
	
	/**
	 * Helper class to manage PDF content stream and pagination.
	 */
	private static class PdfContentBuilder
	{
		private final PDDocument document;
		private PDPage currentPage;
		private PDPageContentStream contentStream;
		private float currentY;
		private PDFont currentFont;
		private float currentFontSize;
		private Color currentColor;
		
		public PdfContentBuilder(PDDocument document) throws IOException
		{
			this.document = document;
			this.currentFont = fontRegular;
			this.currentFontSize = BODY_FONT_SIZE;
			this.currentColor = Color.BLACK;
			newPage();
		}
		
		/**
		 * Creates a new page and content stream.
		 */
		public void newPage() throws IOException
		{
			if (contentStream != null)
			{
				contentStream.close();
			}
			
			currentPage = new PDPage(PDRectangle.LETTER);
			document.addPage(currentPage);
			contentStream = new PDPageContentStream(document, currentPage);
			currentY = PAGE_HEIGHT_POINTS - MARGIN_POINTS;
		}
		
		/**
		 * Checks if we need a new page and creates one if necessary.
		 */
		private void checkPageBreak(float requiredSpace) throws IOException
		{
			if (currentY - requiredSpace < MARGIN_POINTS)
			{
				newPage();
			}
		}
		
		/**
		 * Sets the current font.
		 */
		public void setFont(PDFont font, float size) throws IOException
		{
			this.currentFont = font;
			this.currentFontSize = size;
		}
		
		/**
		 * Sets the current color.
		 */
		public void setColor(Color color)
		{
			this.currentColor = color;
		}
		
		/**
		 * Gets the width of text in current font.
		 */
		public float getTextWidth(String text) throws IOException
		{
			return currentFont.getStringWidth(text) / 1000 * currentFontSize;
		}
		
		/**
		 * Adds simple text at current position.
		 */
		public void addText(String text) throws IOException
		{
			checkPageBreak(currentFontSize * LINE_SPACING);
			
			contentStream.beginText();
			contentStream.setFont(currentFont, currentFontSize);
			contentStream.setNonStrokingColor(currentColor);
			contentStream.newLineAtOffset(MARGIN_POINTS, currentY);
			contentStream.showText(text);
			contentStream.endText();
			
			currentY -= currentFontSize * LINE_SPACING;
		}
		
		/**
		 * Adds text at a horizontal offset (for label-value pairs on same line).
		 */
		public void addTextAtOffset(String text, float offset) throws IOException
		{
			// Move back up since we want to continue on same line
			currentY += currentFontSize * LINE_SPACING;
			
			contentStream.beginText();
			contentStream.setFont(currentFont, currentFontSize);
			contentStream.setNonStrokingColor(currentColor);
			contentStream.newLineAtOffset(MARGIN_POINTS + offset, currentY);
			contentStream.showText(text);
			contentStream.endText();
			
			currentY -= currentFontSize * LINE_SPACING;
		}
		
		/**
		 * Adds centered text.
		 */
		public void addCenteredText(String text) throws IOException
		{
			checkPageBreak(currentFontSize * LINE_SPACING);
			
			float textWidth = getTextWidth(text);
			float x = (PAGE_WIDTH_POINTS - textWidth) / 2;
			
			contentStream.beginText();
			contentStream.setFont(currentFont, currentFontSize);
			contentStream.setNonStrokingColor(currentColor);
			contentStream.newLineAtOffset(x, currentY);
			contentStream.showText(text);
			contentStream.endText();
			
			currentY -= currentFontSize * LINE_SPACING;
		}
		
		/**
		 * Adds text with underline.
		 */
		public void addTextWithUnderline(String text) throws IOException
		{
			checkPageBreak(currentFontSize * LINE_SPACING);
			
			float textWidth = getTextWidth(text);
			
			contentStream.beginText();
			contentStream.setFont(currentFont, currentFontSize);
			contentStream.setNonStrokingColor(currentColor);
			contentStream.newLineAtOffset(MARGIN_POINTS, currentY);
			contentStream.showText(text);
			contentStream.endText();
			
			// Draw underline
			float underlineY = currentY - 2;
			contentStream.setStrokingColor(currentColor);
			contentStream.setLineWidth(0.5f);
			contentStream.moveTo(MARGIN_POINTS, underlineY);
			contentStream.lineTo(MARGIN_POINTS + textWidth, underlineY);
			contentStream.stroke();
			
			currentY -= currentFontSize * LINE_SPACING;
		}
		
		/**
		 * Adds wrapped text that handles line breaks.
		 */
		public void addWrappedText(String text) throws IOException
		{
			List<String> lines = wrapText(text, CONTENT_WIDTH);
			
			for (String line : lines)
			{
				checkPageBreak(currentFontSize * LINE_SPACING);
				
				contentStream.beginText();
				contentStream.setFont(currentFont, currentFontSize);
				contentStream.setNonStrokingColor(currentColor);
				contentStream.newLineAtOffset(MARGIN_POINTS, currentY);
				contentStream.showText(line);
				contentStream.endText();
				
				currentY -= currentFontSize * LINE_SPACING;
			}
		}
		
		/**
		 * Wraps text to fit within a given width.
		 */
		private List<String> wrapText(String text, float maxWidth) throws IOException
		{
			List<String> lines = new java.util.ArrayList<>();
			String[] words = text.split(" ");
			StringBuilder currentLine = new StringBuilder();
			
			for (String word : words)
			{
				String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
				float testWidth = getTextWidth(testLine);
				
				if (testWidth > maxWidth && currentLine.length() > 0)
				{
					lines.add(currentLine.toString());
					currentLine = new StringBuilder(word);
				}
				else
				{
					currentLine = new StringBuilder(testLine);
				}
			}
			
			if (currentLine.length() > 0)
			{
				lines.add(currentLine.toString());
			}
			
			return lines;
		}
		
		/**
		 * Adds vertical space.
		 */
		public void addVerticalSpace(float space) throws IOException
		{
			currentY -= space;
			if (currentY < MARGIN_POINTS)
			{
				newPage();
			}
		}
		
		/**
		 * Adds a horizontal line.
		 */
		public void addHorizontalLine() throws IOException
		{
			contentStream.setStrokingColor(currentColor);
			contentStream.setLineWidth(0.5f);
			contentStream.moveTo(MARGIN_POINTS, currentY);
			contentStream.lineTo(PAGE_WIDTH_POINTS - MARGIN_POINTS, currentY);
			contentStream.stroke();
		}
		
		/**
		 * Adds a table.
		 */
		public void addTable(String[][] data, float[] columnWidths, PDFont headerFont, PDFont bodyFont, float fontSize, Color headerBgColor,
				Color borderColor) throws IOException
		{
			float rowHeight = fontSize * 2.5f;
			float tableHeight = data.length * rowHeight;
			
			checkPageBreak(tableHeight);
			
			float startX = MARGIN_POINTS;
			float startY = currentY;
			
			for (int row = 0; row < data.length; row++)
			{
				float cellX = startX;
				float cellY = startY - (row * rowHeight);
				
				// Check for page break mid-table
				if (cellY - rowHeight < MARGIN_POINTS)
				{
					newPage();
					startY = currentY;
					cellY = startY - (row * rowHeight);
				}
				
				for (int col = 0; col < data[row].length; col++)
				{
					float cellWidth = columnWidths[col];
					
					// Draw cell background for header
					if (row == 0)
					{
						contentStream.setNonStrokingColor(headerBgColor);
						contentStream.addRect(cellX, cellY - rowHeight, cellWidth, rowHeight);
						contentStream.fill();
					}
					
					// Draw cell border
					contentStream.setStrokingColor(borderColor);
					contentStream.setLineWidth(0.5f);
					contentStream.addRect(cellX, cellY - rowHeight, cellWidth, rowHeight);
					contentStream.stroke();
					
					// Draw cell text
					String cellText = data[row][col];
					if (cellText == null)
					{
						cellText = "";
					}
					
					// Truncate if too long
					PDFont cellFont = row == 0 ? headerFont : bodyFont;
					float maxTextWidth = cellWidth - 10;
					while (cellFont.getStringWidth(cellText) / 1000 * fontSize > maxTextWidth && cellText.length() > 0)
					{
						cellText = cellText.substring(0, cellText.length() - 1);
					}
					
					contentStream.beginText();
					contentStream.setFont(cellFont, fontSize);
					contentStream.setNonStrokingColor(Color.BLACK);
					contentStream.newLineAtOffset(cellX + 5, cellY - rowHeight + (rowHeight - fontSize) / 2);
					contentStream.showText(cellText);
					contentStream.endText();
					
					cellX += cellWidth;
				}
			}
			
			currentY = startY - (data.length * rowHeight);
		}
		
		/**
		 * Closes the content stream.
		 */
		public void close() throws IOException
		{
			if (contentStream != null)
			{
				contentStream.close();
			}
		}
	}
}