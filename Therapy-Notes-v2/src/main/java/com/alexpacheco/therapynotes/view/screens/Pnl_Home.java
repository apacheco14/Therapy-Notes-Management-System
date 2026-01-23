package com.alexpacheco.therapynotes.view.screens;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.alexpacheco.therapynotes.controller.AppController;
import com.alexpacheco.therapynotes.util.AppFonts;
import com.alexpacheco.therapynotes.util.AppIcon;

/**
 * Home screen panel displaying the application icon and title. Can be used as a welcome/landing screen or dashboard header.
 */
public class Pnl_Home extends JPanel
{
	private static final long serialVersionUID = -6573309009119411416L;
	private static final String APP_TITLE = "Notes Management System";
	private static final String APP_SUBTITLE = "";
	
	private static final Color TITLE_COLOR = new Color( 74, 144, 164 ); // Matches icon color #4A90A4
	private static final Color SUBTITLE_COLOR = new Color( 100, 116, 129 );
	
	private static final int ICON_SIZE = 256;
	
	private JLabel lblIcon;
	private JLabel lblTitle;
	private JLabel lblSubtitle;
	private JPanel contentPanel;
	
	public Pnl_Home()
	{
		initComponents();
	}
	
	private void initComponents()
	{
		setLayout( new BorderLayout() );
		setBackground( AppController.getBackgroundColor() );
		
		// Header panel with icon and title
		JPanel headerPanel = createHeaderPanel();
		add( headerPanel, BorderLayout.NORTH );
		
		// Content panel for additional content (can be customized)
		contentPanel = new JPanel();
		contentPanel.setBackground( AppController.getBackgroundColor() );
		contentPanel.setLayout( new BorderLayout() );
		add( contentPanel, BorderLayout.CENTER );
	}
	
	private JPanel createHeaderPanel()
	{
		JPanel headerPanel = new JPanel();
		headerPanel.setLayout( new BoxLayout( headerPanel, BoxLayout.Y_AXIS ) );
		headerPanel.setBackground( AppController.getBackgroundColor() );
		headerPanel.setBorder( BorderFactory.createEmptyBorder( 40, 20, 30, 20 ) );
		
		// Icon
		lblIcon = new JLabel();
		lblIcon.setAlignmentX( CENTER_ALIGNMENT );
		loadIcon();
		headerPanel.add( lblIcon );
		
		headerPanel.add( Box.createVerticalStrut( 20 ) );
		
		// Title
		lblTitle = new JLabel( APP_TITLE );
		lblTitle.setFont( AppFonts.getScreenTitleFont() );
		lblTitle.setForeground( TITLE_COLOR );
		lblTitle.setAlignmentX( CENTER_ALIGNMENT );
		headerPanel.add( lblTitle );
		
		headerPanel.add( Box.createVerticalStrut( 8 ) );
		
		// Subtitle
		lblSubtitle = new JLabel( APP_SUBTITLE );
		lblSubtitle.setFont( AppFonts.createFont( Font.PLAIN, 14 ) );
		lblSubtitle.setForeground( SUBTITLE_COLOR );
		lblSubtitle.setAlignmentX( CENTER_ALIGNMENT );
		headerPanel.add( lblSubtitle );
		
		return headerPanel;
	}
	
	private void loadIcon()
	{
		Image icon = AppIcon.getPrimaryIcon();
		if( icon != null )
		{
			// Scale to desired display size
			Image scaledIcon = icon.getScaledInstance( ICON_SIZE, ICON_SIZE, Image.SCALE_SMOOTH );
			lblIcon.setIcon( new ImageIcon( scaledIcon ) );
		}
		else
		{
			// Fallback: show placeholder
			lblIcon.setText( "[Icon]" );
			lblIcon.setPreferredSize( new Dimension( ICON_SIZE, ICON_SIZE ) );
			lblIcon.setHorizontalAlignment( SwingConstants.CENTER );
		}
	}
	
	/**
	 * Returns the content panel for adding additional components below the header. Use this to add navigation buttons, recent items, quick
	 * actions, etc.
	 * 
	 * @return The content panel
	 */
	public JPanel getContentPanel()
	{
		return contentPanel;
	}
	
	/**
	 * Sets a custom title.
	 * 
	 * @param title The new title text
	 */
	public void setTitle( String title )
	{
		lblTitle.setText( title );
	}
	
	/**
	 * Sets a custom subtitle.
	 * 
	 * @param subtitle The new subtitle text
	 */
	public void setSubtitle( String subtitle )
	{
		lblSubtitle.setText( subtitle );
	}
	
	/**
	 * Sets a custom icon size and reloads the icon.
	 * 
	 * @param size The icon size in pixels
	 */
	public void setIconSize( int size )
	{
		Image icon = AppIcon.getPrimaryIcon();
		if( icon != null )
		{
			Image scaledIcon = icon.getScaledInstance( size, size, Image.SCALE_SMOOTH );
			lblIcon.setIcon( new ImageIcon( scaledIcon ) );
		}
	}
}