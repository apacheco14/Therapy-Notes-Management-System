package com.alexpacheco.therapynotes.util.export;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;

import org.apache.poi.xwpf.usermodel.Borders;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.TableRowAlign;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageMar;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPageSz;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSectPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STPageOrientation;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STTblWidth;

import com.alexpacheco.therapynotes.controller.AppController;
import com.alexpacheco.therapynotes.controller.enums.ErrorCode;
import com.alexpacheco.therapynotes.controller.enums.LogLevel;
import com.alexpacheco.therapynotes.controller.errorhandling.exceptions.TherapyAppException;
import com.alexpacheco.therapynotes.model.entities.CollateralContact;
import com.alexpacheco.therapynotes.model.entities.Note;
import com.alexpacheco.therapynotes.model.entities.Referral;

/**
 * Exports therapy progress notes to Microsoft Word (DOCX) format. Creates professionally formatted
 * documents with all note sections including client info, session details, symptoms, narrative,
 * mental status, and administrative information.
 */
public class NoteDocxExporter extends AbstractNoteExporter
{
	// Singleton instance for non-static access to inherited methods
	private static final NoteDocxExporter INSTANCE = new NoteDocxExporter();
	
	// Font name
	private static final String FONT_NAME = PREFERRED_FONT;
	
	// Font sizes in half-points (multiply pt by 2)
	private static final int TITLE_FONT_SIZE_HP = (int) (TITLE_FONT_SIZE * 2);
	private static final int SECTION_FONT_SIZE_HP = (int) (SECTION_FONT_SIZE * 2);
	private static final int BODY_FONT_SIZE_HP = (int) (BODY_FONT_SIZE * 2);
	private static final int SMALL_FONT_SIZE_HP = (int) (SMALL_FONT_SIZE * 2);
	private static final int FOOTER_FONT_SIZE_HP = (int) (FOOTER_FONT_SIZE * 2);
	
	// Colors as hex strings (for POI)
	private static final String HEADER_TEXT_COLOR_HEX = toHexString(HEADER_TEXT_RGB);
	private static final String LABEL_TEXT_COLOR_HEX = toHexString(LABEL_TEXT_RGB);
	private static final String CERTIFIED_COLOR_HEX = toHexString(CERTIFIED_RGB);
	private static final String TABLE_HEADER_BG_HEX = toHexString(TABLE_HEADER_BG_RGB);
	
	// ===== Abstract Method Implementations =====
	
	@Override
	protected String getFileExtension()
	{
		return "docx";
	}
	
	@Override
	protected String getFileTypeDescription()
	{
		return "Word Documents (*.docx)";
	}
	
	@Override
	protected void doExport(Note note, String outputPath) throws TherapyAppException
	{
		exportToDocx(note, outputPath);
	}
	
	// ===== Public Static Methods =====
	
	/**
	 * Exports a single note to a DOCX file.
	 * 
	 * @param note       The note to export
	 * @param outputPath The path where the document should be saved
	 * @throws TherapyAppException If export fails
	 */
	public static void exportToDocx(Note note, String outputPath) throws TherapyAppException
	{
		if (note == null)
		{
			throw new TherapyAppException("Cannot export null note", ErrorCode.REQ_MISSING);
		}
		
		// Prepare all display data
		NoteExportData data = prepareExportData(note);
		
		try (XWPFDocument document = new XWPFDocument())
		{
			// Set up page size and margins (US Letter)
			setupPageLayout(document);
			
			// Build document sections
			buildDocument(document, data);
			
			// Save to file
			File outputFile = new File(outputPath);
			outputFile.getParentFile().mkdirs();
			
			try (FileOutputStream out = new FileOutputStream(outputFile))
			{
				document.write(out);
			}
			AppController.logToDatabase(LogLevel.INFO, "NoteDocxExporter", "Note ID " + note.getNoteId() + " exported to " + outputFile);
		}
		catch (IOException e)
		{
			throw new TherapyAppException("Failed to export note to DOCX: " + e.getMessage(), ErrorCode.DB_ERROR);
		}
	}
	
	/**
	 * Exports a note to a DOCX with a file chooser dialog.
	 * 
	 * @param note            The note to export
	 * @param outputDirectory The default directory
	 * @param filename        The default filename
	 * @return The path to the created file, or null if cancelled
	 * @throws TherapyAppException If export fails
	 */
	public static String exportToDocx(Note note, File outputDirectory, String filename) throws TherapyAppException
	{
		return INSTANCE.exportWithFileChooser(note, outputDirectory, filename);
	}
	
	// ===== Page Setup =====
	
	/**
	 * Sets up the page layout with US Letter size and 1-inch margins.
	 */
	private static void setupPageLayout(XWPFDocument document)
	{
		CTSectPr sectPr = document.getDocument().getBody().addNewSectPr();
		
		// Page size - US Letter
		CTPageSz pageSize = sectPr.addNewPgSz();
		pageSize.setW(BigInteger.valueOf(PAGE_WIDTH_TWIPS));
		pageSize.setH(BigInteger.valueOf(PAGE_HEIGHT_TWIPS));
		pageSize.setOrient(STPageOrientation.PORTRAIT);
		
		// Margins - 1 inch all around
		CTPageMar margins = sectPr.addNewPgMar();
		margins.setTop(BigInteger.valueOf(MARGIN_TWIPS));
		margins.setBottom(BigInteger.valueOf(MARGIN_TWIPS));
		margins.setLeft(BigInteger.valueOf(MARGIN_TWIPS));
		margins.setRight(BigInteger.valueOf(MARGIN_TWIPS));
	}
	
	// ===== Document Building =====
	
	/**
	 * Builds all document sections using pre-processed export data.
	 */
	private static void buildDocument(XWPFDocument document, NoteExportData data)
	{
		addDocumentHeader(document, data);
		addClientInfoSection(document, data);
		addSessionInfoSection(document, data);
		addNarrativeSection(document, data);
		addSymptomsSection(document, data);
		addMentalStatusSection(document, data);
		addCollateralContactsSection(document, data);
		addReferralsSection(document, data);
		addFollowUpSection(document, data);
		addDocumentFooter(document, data);
	}
	
	/**
	 * Adds the document title and certification status.
	 */
	private static void addDocumentHeader(XWPFDocument document, NoteExportData data)
	{
		// Title
		XWPFParagraph titlePara = document.createParagraph();
		titlePara.setAlignment(ParagraphAlignment.CENTER);
		titlePara.setSpacingAfter(100);
		
		XWPFRun titleRun = titlePara.createRun();
		titleRun.setText("THERAPY PROGRESS NOTE");
		titleRun.setBold(true);
		titleRun.setFontSize(TITLE_FONT_SIZE_HP / 2);
		titleRun.setFontFamily(FONT_NAME);
		titleRun.setColor(HEADER_TEXT_COLOR_HEX);
		
		// Certification status
		if (data.isCertified)
		{
			XWPFParagraph certPara = document.createParagraph();
			certPara.setAlignment(ParagraphAlignment.CENTER);
			certPara.setSpacingAfter(200);
			
			XWPFRun certRun = certPara.createRun();
			certRun.setText(data.certificationText);
			certRun.setItalic(true);
			certRun.setFontSize(SMALL_FONT_SIZE_HP / 2);
			certRun.setFontFamily(FONT_NAME);
			certRun.setColor(CERTIFIED_COLOR_HEX);
		}
	}
	
	/**
	 * Adds the client information section.
	 */
	private static void addClientInfoSection(XWPFDocument document, NoteExportData data)
	{
		addSectionHeading(document, "Client Information");
		
		addLabelValuePair(document, "Client", data.clientName);
		addLabelValuePair(document, "Client Code", data.clientCode);
		addLabelValuePair(document, "Date of Birth", data.dateOfBirth);
	}
	
	/**
	 * Adds the session information section.
	 */
	private static void addSessionInfoSection(XWPFDocument document, NoteExportData data)
	{
		addSectionHeading(document, "Session Information");
		
		addLabelValuePair(document, "Appointment Date", data.apptDate);
		addLabelValuePair(document, "Session Type", data.sessionType);
		addLabelValuePair(document, "Session Number", data.sessionNumber);
		addLabelValuePair(document, "Session Length", data.sessionLength);
		addLabelValuePair(document, "Diagnosis", data.diagnosis);
		
		if (data.hasApptComment)
		{
			addLabelValuePair(document, "Appointment Comment", data.apptComment);
		}
	}
	
	/**
	 * Adds the narrative section.
	 */
	private static void addNarrativeSection(XWPFDocument document, NoteExportData data)
	{
		addSectionHeading(document, "Session Narrative");
		
		if (!data.hasNarrative)
		{
			addBodyText(document, "No narrative recorded.", true);
		}
		else
		{
			String[] paragraphs = data.narrative.split("\n");
			for (String para : paragraphs)
			{
				if (!para.trim().isEmpty())
				{
					addBodyText(document, para.trim(), false);
				}
			}
		}
	}
	
	/**
	 * Adds the symptoms section.
	 */
	private static void addSymptomsSection(XWPFDocument document, NoteExportData data)
	{
		addSectionHeading(document, "Presenting Symptoms");
		addBodyText(document, data.symptomsText, !data.hasSymptoms);
	}
	
	/**
	 * Adds the mental status section as a table.
	 */
	private static void addMentalStatusSection(XWPFDocument document, NoteExportData data)
	{
		addSectionHeading(document, "Mental Status Assessment");
		
		String[][] tableData = data.getMentalStatusTableData();
		
		// Create table
		XWPFTable table = document.createTable(tableData.length, 3);
		table.setTableAlignment(TableRowAlign.LEFT);
		
		// Set table width to 100%
		CTTblWidth tableWidth = table.getCTTbl().getTblPr().addNewTblW();
		tableWidth.setType(STTblWidth.PCT);
		tableWidth.setW(BigInteger.valueOf(5000)); // 5000 = 100% in PCT
		
		// Column widths (approximate percentages: 25%, 30%, 45%)
		int[] colWidths = { 1250, 1500, 2250 }; // Sum = 5000 (100%)
		
		for (int row = 0; row < tableData.length; row++)
		{
			XWPFTableRow tableRow = table.getRow(row);
			
			for (int col = 0; col < tableData[row].length; col++)
			{
				XWPFTableCell cell = tableRow.getCell(col);
				
				// Set cell width
				CTTblWidth cellWidth = cell.getCTTc().addNewTcPr().addNewTcW();
				cellWidth.setType(STTblWidth.PCT);
				cellWidth.setW(BigInteger.valueOf(colWidths[col]));
				
				// Header row styling
				if (row == 0)
				{
					CTShd shading = cell.getCTTc().getTcPr().addNewShd();
					shading.setVal(STShd.CLEAR);
					shading.setFill(TABLE_HEADER_BG_HEX);
				}
				
				// Cell text
				XWPFParagraph cellPara = cell.getParagraphs().get(0);
				cellPara.setSpacingBefore(50);
				cellPara.setSpacingAfter(50);
				
				XWPFRun cellRun = cellPara.createRun();
				cellRun.setText(tableData[row][col] != null ? tableData[row][col] : "");
				cellRun.setFontFamily(FONT_NAME);
				cellRun.setFontSize(SMALL_FONT_SIZE_HP / 2);
				
				if (row == 0)
				{
					cellRun.setBold(true);
				}
			}
		}
		
		// Add spacing after table
		XWPFParagraph spacer = document.createParagraph();
		spacer.setSpacingAfter(200);
	}
	
	/**
	 * Adds the collateral contacts section.
	 */
	private static void addCollateralContactsSection(XWPFDocument document, NoteExportData data)
	{
		if (!data.hasCollateralContacts)
		{
			return;
		}
		
		addSectionHeading(document, "Collateral Contacts");
		
		for (CollateralContact contact : data.collateralContacts)
		{
			XWPFParagraph para = document.createParagraph();
			para.setSpacingAfter(50);
			
			XWPFRun run = para.createRun();
			run.setText(getCollateralContactDisplayName(contact));
			run.setBold(true);
			run.setFontFamily(FONT_NAME);
			run.setFontSize(BODY_FONT_SIZE_HP / 2);
		}
		
		if (data.hasCollateralContactComment)
		{
			addBodyText(document, data.collateralContactComment, false);
		}
	}
	
	/**
	 * Adds the referrals section.
	 */
	private static void addReferralsSection(XWPFDocument document, NoteExportData data)
	{
		if (!data.hasReferrals)
		{
			return;
		}
		
		addSectionHeading(document, "Referrals Made");
		
		for (Referral referral : data.referrals)
		{
			XWPFParagraph para = document.createParagraph();
			para.setSpacingAfter(50);
			
			XWPFRun run = para.createRun();
			run.setText(getReferralDisplayName(referral));
			run.setBold(true);
			run.setFontFamily(FONT_NAME);
			run.setFontSize(BODY_FONT_SIZE_HP / 2);
		}
		
		if (data.hasReferralComment)
		{
			addBodyText(document, data.referralComment, false);
		}
	}
	
	/**
	 * Adds the follow-up section.
	 */
	private static void addFollowUpSection(XWPFDocument document, NoteExportData data)
	{
		addSectionHeading(document, "Follow-Up");
		
		addLabelValuePair(document, "Next Appointment", data.nextAppt);
		
		if (data.hasNextApptComment)
		{
			addLabelValuePair(document, "Comment", data.nextApptComment);
		}
	}
	
	/**
	 * Adds the document footer.
	 */
	private static void addDocumentFooter(XWPFDocument document, NoteExportData data)
	{
		// Divider line (using border on paragraph)
		XWPFParagraph dividerPara = document.createParagraph();
		dividerPara.setSpacingBefore(400);
		dividerPara.setBorderTop(Borders.SINGLE);
		
		// Footer text
		XWPFParagraph footerPara = document.createParagraph();
		footerPara.setSpacingBefore(100);
		
		XWPFRun footerRun = footerPara.createRun();
		footerRun.setText(data.getFooterText());
		footerRun.setFontFamily(FONT_NAME);
		footerRun.setFontSize(FOOTER_FONT_SIZE_HP / 2);
		footerRun.setColor(LABEL_TEXT_COLOR_HEX);
	}
	
	// ===== Helper Methods =====
	
	/**
	 * Adds a section heading with underline.
	 */
	private static void addSectionHeading(XWPFDocument document, String text)
	{
		XWPFParagraph para = document.createParagraph();
		para.setSpacingBefore(400);
		para.setSpacingAfter(100);
		
		XWPFRun run = para.createRun();
		run.setText(text);
		run.setBold(true);
		run.setUnderline(UnderlinePatterns.SINGLE);
		run.setFontFamily(FONT_NAME);
		run.setFontSize(SECTION_FONT_SIZE_HP / 2);
		run.setColor(HEADER_TEXT_COLOR_HEX);
	}
	
	/**
	 * Adds a label-value pair on a single line.
	 */
	private static void addLabelValuePair(XWPFDocument document, String label, String value)
	{
		XWPFParagraph para = document.createParagraph();
		para.setSpacingAfter(50);
		
		// Label (bold, gray)
		XWPFRun labelRun = para.createRun();
		labelRun.setText(label + ": ");
		labelRun.setBold(true);
		labelRun.setFontFamily(FONT_NAME);
		labelRun.setFontSize(BODY_FONT_SIZE_HP / 2);
		labelRun.setColor(LABEL_TEXT_COLOR_HEX);
		
		// Value (regular, black)
		XWPFRun valueRun = para.createRun();
		valueRun.setText(value != null ? value : "Not specified");
		valueRun.setFontFamily(FONT_NAME);
		valueRun.setFontSize(BODY_FONT_SIZE_HP / 2);
		valueRun.setColor("000000");
	}
	
	/**
	 * Adds body text, optionally italicized for placeholder text.
	 */
	private static void addBodyText(XWPFDocument document, String text, boolean isPlaceholder)
	{
		XWPFParagraph para = document.createParagraph();
		para.setSpacingAfter(150);
		
		XWPFRun run = para.createRun();
		run.setText(text);
		run.setFontFamily(FONT_NAME);
		run.setFontSize(BODY_FONT_SIZE_HP / 2);
		
		if (isPlaceholder)
		{
			run.setItalic(true);
			run.setColor(LABEL_TEXT_COLOR_HEX);
		}
		else
		{
			run.setColor("000000");
		}
	}
}