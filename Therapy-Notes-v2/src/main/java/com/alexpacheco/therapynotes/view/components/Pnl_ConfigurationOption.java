package com.alexpacheco.therapynotes.view.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;

import com.alexpacheco.therapynotes.model.entities.assessmentoptions.AssessmentOption;
import com.alexpacheco.therapynotes.util.AppFonts;
import com.alexpacheco.therapynotes.util.JavaUtils;

/**
 * A panel displaying a single configuration option with a two-line layout: name in bold on top, description in smaller gray text below.
 */
public class Pnl_ConfigurationOption extends JPanel
{
	private static final long serialVersionUID = 1L;
	
	private static final Color BACKGROUND_COLOR = Color.WHITE;
	private static final Color DESCRIPTION_COLOR = Color.GRAY;
	private static final int PADDING = 10;
	
	private AssessmentOption option;
	private ConfigurationOptionListener listener;
	
	/**
	 * Listener interface for edit and delete actions on configuration options.
	 */
	public interface ConfigurationOptionListener
	{
		void onEditRequested( AssessmentOption option );
		
		void onDeleteRequested( AssessmentOption option );
	}
	
	/**
	 * Creates a new configuration option panel.
	 * 
	 * @param option   The assessment option to display
	 * @param listener Listener for edit/delete actions
	 */
	public Pnl_ConfigurationOption( AssessmentOption option, ConfigurationOptionListener listener )
	{
		this.option = option;
		this.listener = listener;
		
		initializeUI();
		setupContextMenu();
	}
	
	private void initializeUI()
	{
		setLayout( new BorderLayout() );
		setBorder( BorderFactory.createEmptyBorder( PADDING, PADDING, PADDING, PADDING ) );
		setBackground( BACKGROUND_COLOR );
		setOpaque( true );
		
		// Name label - bold
		JLabel nameLabel = new JLabel( option.getName() );
		nameLabel.setFont( AppFonts.getLabelFont().deriveFont( Font.BOLD ) );
		nameLabel.setOpaque( false );
		
		// Description text area - smaller, gray, wrapping
		JTextArea descriptionArea = createDescriptionArea();
		
		// Layout
		JPanel contentPanel = new JPanel( new BorderLayout( 0, 2 ) );
		contentPanel.setOpaque( false );
		contentPanel.add( nameLabel, BorderLayout.NORTH );
		contentPanel.add( descriptionArea, BorderLayout.CENTER );
		
		add( contentPanel, BorderLayout.CENTER );
	}
	
	@Override
	public Dimension getMaximumSize()
	{
		// Constrain maximum height to preferred height to prevent unnecessary expansion
		// Width can expand to fill column
		Dimension pref = getPreferredSize();
		return new Dimension( Integer.MAX_VALUE, pref.height );
	}
	
	private JTextArea createDescriptionArea()
	{
		String description = option.getDescription();
		JTextArea textArea = new JTextArea();
		
		if( JavaUtils.isNullOrEmpty( description ) )
		{
			textArea.setText( " " ); // Maintain minimum height
		}
		else
		{
			textArea.setText( description );
		}
		
		textArea.setFont( AppFonts.getSmallFont().deriveFont( Font.ITALIC ) );
		textArea.setForeground( DESCRIPTION_COLOR );
		textArea.setBackground( BACKGROUND_COLOR );
		textArea.setOpaque( false );
		textArea.setEditable( false );
		textArea.setFocusable( false );
		textArea.setLineWrap( true );
		textArea.setWrapStyleWord( true );
		textArea.setBorder( null );
		
		return textArea;
	}
	
	private void setupContextMenu()
	{
		JPopupMenu contextMenu = new JPopupMenu();
		
		JMenuItem editItem = new JMenuItem( "Edit Description" );
		editItem.addActionListener( e ->
		{
			if( listener != null )
			{
				listener.onEditRequested( option );
			}
		} );
		
		JMenuItem deleteItem = new JMenuItem( "Delete" );
		deleteItem.addActionListener( e ->
		{
			if( listener != null )
			{
				listener.onDeleteRequested( option );
			}
		} );
		deleteItem.setEnabled( false );
		
		contextMenu.add( editItem );
		contextMenu.add( deleteItem );
		
		addMouseListener( new MouseAdapter()
		{
			@Override
			public void mousePressed( MouseEvent e )
			{
				showPopupIfTriggered( e );
			}
			
			@Override
			public void mouseReleased( MouseEvent e )
			{
				showPopupIfTriggered( e );
			}
			
			private void showPopupIfTriggered( MouseEvent e )
			{
				if( e.isPopupTrigger() )
				{
					contextMenu.show( Pnl_ConfigurationOption.this, e.getX(), e.getY() );
				}
			}
		} );
	}
	
	/**
	 * Gets the assessment option displayed by this panel.
	 * 
	 * @return The assessment option
	 */
	public AssessmentOption getOption()
	{
		return option;
	}
	
	/**
	 * Updates the displayed option.
	 * 
	 * @param option The new option data
	 */
	public void updateOption( AssessmentOption option )
	{
		this.option = option;
		removeAll();
		initializeUI();
		setupContextMenu();
		revalidate();
		repaint();
	}
}