package com.alexpacheco.therapynotes.install;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.alexpacheco.therapynotes.util.AppFonts;

import java.awt.*;

/**
 * Practice Information step - collects basic practice details.
 */
public class Pnl_SetupPracticeInfo extends AbstractSetupStepPanel
{
	private static final long serialVersionUID = -2927895707788347275L;
	private JTextField txtPracticeName;
	private JTextField txtPractitionerName;
	private JTextField txtLicenseNumber;
	private JTextField txtPhone;
	private JTextField txtEmail;
	private JTextArea txtAddress;
	
	public Pnl_SetupPracticeInfo( SetupConfiguration config )
	{
		super( config );
	}
	
	@Override
	protected String getStepTitle()
	{
		return "Practice Information";
	}
	
	@Override
	protected String getStepDescription()
	{
		return "Enter your practice details. This information may appear on exported documents and reports.";
	}
	
	@Override
	protected void buildContent( JPanel container )
	{
		// Practice Name (required)
		txtPracticeName = new JTextField( 30 );
		txtPracticeName.setToolTipText( "The name of your therapy practice" );
		JPanel practiceNamePanel = createVerticalFieldPanel( "Practice Name", txtPracticeName );
		container.add( practiceNamePanel );
		container.add( createVerticalSpacer( 10 ) );
		
		// Practitioner Name
		txtPractitionerName = new JTextField( 30 );
		txtPractitionerName.setToolTipText( "Your name as it should appear on documents" );
		JPanel practitionerPanel = createVerticalFieldPanel( "Practitioner Name", txtPractitionerName );
		container.add( practitionerPanel );
		container.add( createVerticalSpacer( 10 ) );
		
		// License Number
		txtLicenseNumber = new JTextField( 20 );
		txtLicenseNumber.setToolTipText( "Your professional license number (e.g., LCSW, LPC, PhD)" );
		JPanel licensePanel = createVerticalFieldPanel( "License/Credentials", txtLicenseNumber );
		container.add( licensePanel );
		container.add( createVerticalSpacer( 10 ) );
		
		// Contact Information Section
		JLabel contactHeader = new JLabel( "Contact Information" );
		contactHeader.setFont( contactHeader.getFont().deriveFont( Font.BOLD, 14f ) );
		contactHeader.setForeground( new Color( 70, 70, 70 ) );
		contactHeader.setAlignmentX( Component.LEFT_ALIGNMENT );
		contactHeader.setBorder( new EmptyBorder( 10, 0, 10, 0 ) );
		container.add( contactHeader );
		
		// Phone and Email in a row
		JPanel contactRow = new JPanel( new GridLayout( 1, 2, 20, 0 ) );
		contactRow.setAlignmentX( Component.LEFT_ALIGNMENT );
		contactRow.setMaximumSize( new Dimension( Integer.MAX_VALUE, 55 ) );
		
		JPanel phonePanel = new JPanel();
		phonePanel.setLayout( new BoxLayout( phonePanel, BoxLayout.Y_AXIS ) );
		JLabel phoneLabel = new JLabel( "Phone" );
		phoneLabel.setFont( phoneLabel.getFont().deriveFont( Font.BOLD, 12f ) );
		phoneLabel.setAlignmentX( Component.LEFT_ALIGNMENT );
		txtPhone = new JTextField( 15 );
		txtPhone.setMaximumSize( new Dimension( Integer.MAX_VALUE, 30 ) );
		txtPhone.setAlignmentX( Component.LEFT_ALIGNMENT );
		phonePanel.add( phoneLabel );
		phonePanel.add( Box.createVerticalStrut( 5 ) );
		phonePanel.add( txtPhone );
		
		JPanel emailPanel = new JPanel();
		emailPanel.setLayout( new BoxLayout( emailPanel, BoxLayout.Y_AXIS ) );
		JLabel emailLabel = new JLabel( "Email" );
		emailLabel.setFont( emailLabel.getFont().deriveFont( Font.BOLD, 12f ) );
		emailLabel.setAlignmentX( Component.LEFT_ALIGNMENT );
		txtEmail = new JTextField( 20 );
		txtEmail.setMaximumSize( new Dimension( Integer.MAX_VALUE, 30 ) );
		txtEmail.setAlignmentX( Component.LEFT_ALIGNMENT );
		emailPanel.add( emailLabel );
		emailPanel.add( Box.createVerticalStrut( 5 ) );
		emailPanel.add( txtEmail );
		
		contactRow.add( phonePanel );
		contactRow.add( emailPanel );
		container.add( contactRow );
		container.add( createVerticalSpacer( 10 ) );
		
		// Address
		JPanel addressPanel = new JPanel();
		addressPanel.setLayout( new BoxLayout( addressPanel, BoxLayout.Y_AXIS ) );
		addressPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
		
		JLabel addressLabel = new JLabel( "Practice Address" );
		addressLabel.setFont( addressLabel.getFont().deriveFont( Font.BOLD, 12f ) );
		addressLabel.setAlignmentX( Component.LEFT_ALIGNMENT );
		
		txtAddress = new JTextArea( 3, 30 );
		txtAddress.setLineWrap( true );
		txtAddress.setWrapStyleWord( true );
		JScrollPane addressScroll = new JScrollPane( txtAddress );
		addressScroll.setAlignmentX( Component.LEFT_ALIGNMENT );
		addressScroll.setMaximumSize( new Dimension( Integer.MAX_VALUE, 80 ) );
		
		addressPanel.add( addressLabel );
		addressPanel.add( Box.createVerticalStrut( 5 ) );
		addressPanel.add( addressScroll );
		
		container.add( addressPanel );
		
		AppFonts.applyTextFieldFont( txtPracticeName, txtPractitionerName, txtLicenseNumber, txtPhone, txtEmail, txtAddress );
		
		// Required fields note
		container.add( createVerticalSpacer( 10 ) );
		JLabel requiredNote = new JLabel( "* Required fields" );
		requiredNote.setFont( AppFonts.getSmallFont().deriveFont( Font.ITALIC ) );
		requiredNote.setForeground( Color.GRAY );
		requiredNote.setAlignmentX( Component.LEFT_ALIGNMENT );
		container.add( requiredNote );
		
		// Load existing values if any
		loadExistingValues();
	}
	
	private void loadExistingValues()
	{
		if( config.getPracticeName() != null )
		{
			txtPracticeName.setText( config.getPracticeName() );
		}
		if( config.getPractitionerName() != null )
		{
			txtPractitionerName.setText( config.getPractitionerName() );
		}
		if( config.getLicenseNumber() != null )
		{
			txtLicenseNumber.setText( config.getLicenseNumber() );
		}
		if( config.getPhone() != null )
		{
			txtPhone.setText( config.getPhone() );
		}
		if( config.getEmail() != null )
		{
			txtEmail.setText( config.getEmail() );
		}
		if( config.getAddress() != null )
		{
			txtAddress.setText( config.getAddress() );
		}
	}
	
	@Override
	public boolean validateStep()
	{
//		if( txtPracticeName.getText().trim().isEmpty() )
//		{
//			showValidationError( "Please enter your practice name." );
//			txtPracticeName.requestFocus();
//			return false;
//		}
//		
//		if( txtPractitionerName.getText().trim().isEmpty() )
//		{
//			showValidationError( "Please enter the practitioner name." );
//			txtPractitionerName.requestFocus();
//			return false;
//		}
		
		// Validate email format if provided
		String email = txtEmail.getText().trim();
		if( !email.isEmpty() && !isValidEmail( email ) )
		{
			showValidationError( "Please enter a valid email address." );
			txtEmail.requestFocus();
			return false;
		}
		
		return true;
	}
	
	private boolean isValidEmail( String email )
	{
		return email.matches( "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$" );
	}
	
	@Override
	public void saveStepData()
	{
		config.setPracticeName( txtPracticeName.getText().trim() );
		config.setPractitionerName( txtPractitionerName.getText().trim() );
		config.setLicenseNumber( txtLicenseNumber.getText().trim() );
		config.setPhone( txtPhone.getText().trim() );
		config.setEmail( txtEmail.getText().trim() );
		config.setAddress( txtAddress.getText().trim() );
	}
}