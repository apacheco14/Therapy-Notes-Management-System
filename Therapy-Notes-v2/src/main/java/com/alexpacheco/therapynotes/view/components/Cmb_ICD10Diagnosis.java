package com.alexpacheco.therapynotes.view.components;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.alexpacheco.therapynotes.controller.AppController;
import com.alexpacheco.therapynotes.util.AppLogger;
import com.alexpacheco.therapynotes.util.JavaUtils;

/**
 * A searchable combo box for selecting ICD-10 diagnosis codes. Displays codes in the format "&lt;code with decimal&gt; - &lt;short
 * description&gt;". Filters the dropdown list as the user types.
 */
public class Cmb_ICD10Diagnosis extends JComboBox<String>
{
	private static final long serialVersionUID = -5231436798831352426L;
	private List<String> allCodes;
	private DefaultComboBoxModel<String> model;
	private JTextField editorField;
	private boolean isFiltering = false;
	
	/**
	 * Creates a new ICD-10 diagnosis combo box. Loads codes from AppController.getIcd10Codes().
	 */
	public Cmb_ICD10Diagnosis()
	{
		this( AppController.getIcd10Codes() );
	}
	
	/**
	 * Package-private constructor for testing.
	 * 
	 * @param codes List of ICD-10 codes to populate
	 */
	Cmb_ICD10Diagnosis( List<String> codes )
	{
		this.allCodes = new ArrayList<>();
		this.model = new DefaultComboBoxModel<>();
		
		setModel( model );
		setEditable( true );
		
		editorField = (JTextField) getEditor().getEditorComponent();
		
		setCodes( codes );
		attachListeners();
	}
	
	/**
	 * Sets the codes
	 */
	private void setCodes( List<String> codes )
	{
		if( codes != null )
		{
			allCodes.clear();
			allCodes.addAll( codes );
			
			model.removeAllElements();
			model.addElement( "" ); // Blank option
			for( String code : allCodes )
			{
				model.addElement( code );
			}
		}
	}
	
	private void loadCodes()
	{
		try
		{
			setCodes( AppController.getIcd10Codes() );
		}
		catch( Exception e )
		{
			AppLogger.warning( "Failed to load ICD-10 codes: " + e.getMessage() );
		}
	}
	
	/**
	 * Attaches event listeners for filtering functionality.
	 */
	private void attachListeners()
	{
		editorField.addKeyListener( new KeyAdapter()
		{
			@Override
			public void keyReleased( KeyEvent e )
			{
				int keyCode = e.getKeyCode();
				
				// Ignore navigation and action keys
				if( keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_ENTER
						|| keyCode == KeyEvent.VK_ESCAPE || keyCode == KeyEvent.VK_LEFT || keyCode == KeyEvent.VK_RIGHT
						|| keyCode == KeyEvent.VK_SHIFT || keyCode == KeyEvent.VK_CONTROL || keyCode == KeyEvent.VK_ALT )
				{
					return;
				}
				
				filterCodes();
			}
		} );
		
		editorField.addFocusListener( new FocusAdapter()
		{
			@Override
			public void focusGained( FocusEvent e )
			{
				if( editorField.getText().isEmpty() )
				{
					showPopup();
				}
			}
		} );
	}
	
	/**
	 * Filters the dropdown list based on the current text in the editor.
	 */
	private void filterCodes()
	{
		if( isFiltering )
		{
			return;
		}
		
		isFiltering = true;
		
		SwingUtilities.invokeLater( () ->
		{
			try
			{
				String originalText = editorField.getText();
				String searchText = editorField.getText().toLowerCase().trim();
				int caretPosition = editorField.getCaretPosition();
				
				model.removeAllElements();
				
				if( searchText.isEmpty() )
				{
					// Show all codes when search is empty
					model.addElement( "" );
					for( String code : allCodes )
					{
						model.addElement( code );
					}
				}
				else
				{
					// Filter codes that contain the search text
					for( String code : allCodes )
					{
						if( code.toLowerCase().contains( searchText ) )
						{
							model.addElement( code );
						}
					}
					
					// Add a "no results" indicator if nothing matches
					if( model.getSize() == 0 )
					{
						model.addElement( "No matching codes found" );
					}
				}
				
				// Restore the typed text and caret position
				editorField.setText( originalText );
				editorField.setCaretPosition( Math.min( caretPosition, originalText.length() ) );
				
				// Show the filtered dropdown
				if( model.getSize() > 0 )
				{
					showPopup();
				}
			}
			finally
			{
				isFiltering = false;
			}
		} );
	}
	
	/**
	 * Returns the selected diagnosis in the format "&lt;code&gt; - &lt;description&gt;".
	 * 
	 * @return The selected diagnosis string, or empty string if none selected
	 */
	public String getDiagnosis()
	{
		Object selected = getSelectedItem();
		
		if( selected == null )
		{
			return "";
		}
		
		String value = selected.toString().trim();
		
		// Check if it's the "no results" placeholder
		if( "No matching codes found".equals( value ) )
		{
			return JavaUtils.isNullOrEmpty( editorField.getText() ) ? "" : editorField.getText();
		}
		
		// Verify it's a valid code from our list
		if( allCodes.contains( value ) )
		{
			return value;
		}
		
		// Check if user typed something that exactly matches a code
		for( String code : allCodes )
		{
			if( code.equalsIgnoreCase( value ) )
			{
				return code;
			}
		}
		
		return JavaUtils.isNullOrEmpty( editorField.getText() ) ? "" : editorField.getText();
	}
	
	/**
	 * Sets the selected diagnosis by matching the provided string.
	 * 
	 * @param diagnosis The diagnosis string to select
	 */
	public void setDiagnosis( String diagnosis )
	{
		if( JavaUtils.isNullOrEmpty( diagnosis ) )
		{
			setSelectedItem( "" );
			return;
		}
		
		// Try to find exact match
		for( String code : allCodes )
		{
			if( code.equals( diagnosis ) )
			{
				setSelectedItem( code );
				return;
			}
		}
		
		// Try case-insensitive match
		for( String code : allCodes )
		{
			if( code.equalsIgnoreCase( diagnosis ) )
			{
				setSelectedItem( code );
				return;
			}
		}
		
		// If no match found, set the text anyway (for editing existing notes)
		setSelectedItem( diagnosis );
	}
	
	/**
	 * Refreshes the list of ICD-10 codes from the controller.
	 */
	public void refreshCodes()
	{
		String currentSelection = getDiagnosis();
		loadCodes();
		setDiagnosis( currentSelection );
	}
	
	/**
	 * Clears the selection.
	 */
	public void clear()
	{
		setSelectedItem( "" );
		editorField.setText( "" );
	}
}