package com.alexpacheco.therapynotes.view.screens;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.alexpacheco.therapynotes.controller.AppController;
import com.alexpacheco.therapynotes.util.AppFonts;
import com.alexpacheco.therapynotes.util.AppLogger;

import java.awt.*;

public class Pnl_About extends JPanel
{
	private static final long serialVersionUID = 3991758258996483521L;
	
	public Pnl_About()
	{
		setLayout( new BorderLayout() );
		setBackground( AppController.getBackgroundColor() );
		
		JPanel contentPanel = new JPanel();
		contentPanel.setLayout( new BoxLayout( contentPanel, BoxLayout.Y_AXIS ) );
		contentPanel.setBorder( BorderFactory.createEmptyBorder( 50, 50, 50, 50 ) );
		contentPanel.setBackground( AppController.getBackgroundColor() );
		
		JLabel titleLabel = new JLabel( "Notes Management System" );
		titleLabel.setFont( AppFonts.getScreenTitleFont() );
		titleLabel.setAlignmentX( Component.CENTER_ALIGNMENT );
		
		JLabel versionLabel = new JLabel( "Version 1.0.0" );
		versionLabel.setFont( AppFonts.getHeaderFont() );
		versionLabel.setAlignmentX( Component.CENTER_ALIGNMENT );
		
		JLabel emptySpace1 = new JLabel( " " );
		emptySpace1.setAlignmentX( Component.CENTER_ALIGNMENT );
		
		JLabel purposeLabel = new JLabel( "A comprehensive application for managing progress notes," );
		purposeLabel.setFont( new Font( "Arial", Font.PLAIN, 14 ) );
		purposeLabel.setAlignmentX( Component.CENTER_ALIGNMENT );
		
		JLabel purposeLabel2 = new JLabel( "client information, and contact records in a user-friendly interface." );
		purposeLabel2.setFont( new Font( "Arial", Font.PLAIN, 14 ) );
		purposeLabel2.setAlignmentX( Component.CENTER_ALIGNMENT );
		
		JLabel emptySpace2 = new JLabel( " " );
		emptySpace2.setAlignmentX( Component.CENTER_ALIGNMENT );
		
		JLabel descriptionLabel = new JLabel( "This product includes software developed by other open source projects" );
		descriptionLabel.setFont( new Font( "Arial", Font.PLAIN, 14 ) );
		descriptionLabel.setAlignmentX( Component.CENTER_ALIGNMENT );
		
		JLabel descriptionLabel2 = new JLabel( "including the Apache Software Foundation, https://www.apache.org/." );
		descriptionLabel2.setFont( new Font( "Arial", Font.PLAIN, 14 ) );
		descriptionLabel2.setAlignmentX( Component.CENTER_ALIGNMENT );
		
		JLabel emptySpace3 = new JLabel( " " );
		emptySpace3.setAlignmentX( Component.CENTER_ALIGNMENT );
		
		JLabel copyrightLabel = new JLabel( "© 2026 Alex Pacheco. All Rights Reserved." );
		copyrightLabel.setFont( new Font( "Arial", Font.PLAIN, 10 ) );
		copyrightLabel.setAlignmentX( Component.CENTER_ALIGNMENT );
		
		JLabel aiDisclaimerLabel = new JLabel( "Developed with the support of Claude and other AI tools" );
		aiDisclaimerLabel.setFont( new Font( "Arial", Font.PLAIN, 10 ) );
		aiDisclaimerLabel.setAlignmentX( Component.CENTER_ALIGNMENT );
		
		JLabel sessionIdLabel = new JLabel( "Session ID: " + AppLogger.getSessionId() );
		sessionIdLabel.setFont( new Font( "Arial", Font.PLAIN, 10 ) );
		sessionIdLabel.setHorizontalAlignment( SwingConstants.RIGHT );
		sessionIdLabel.setBorder( new EmptyBorder( 1, 2, 1, 2 ) );
		sessionIdLabel.setAlignmentX( Component.RIGHT_ALIGNMENT );
		
		contentPanel.add( Box.createVerticalGlue() );
		contentPanel.add( titleLabel );
		contentPanel.add( Box.createRigidArea( new Dimension( 0, 10 ) ) );
		contentPanel.add( versionLabel );
		contentPanel.add( Box.createRigidArea( new Dimension( 0, 30 ) ) );
		contentPanel.add( purposeLabel );
		contentPanel.add( purposeLabel2 );
		contentPanel.add( Box.createRigidArea( new Dimension( 0, 20 ) ) );
		contentPanel.add( descriptionLabel );
		contentPanel.add( descriptionLabel2 );
		contentPanel.add( Box.createRigidArea( new Dimension( 0, 30 ) ) );
		contentPanel.add( copyrightLabel );
		contentPanel.add( aiDisclaimerLabel );
		contentPanel.add( Box.createVerticalGlue() );
		
		add( contentPanel, BorderLayout.CENTER );
		add( sessionIdLabel, BorderLayout.SOUTH );
	}
}