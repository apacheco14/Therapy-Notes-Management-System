package main.java.com.alexpacheco.therapynotes.install;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

/**
 * Database Location step - allows user to choose where client data is stored.
 */
public class Pnl_SetupDatabaseLocation extends AbstractSetupStepPanel
{
	private static final long serialVersionUID = -3344003119654638524L;
	private JRadioButton rbDefaultLocation;
	private JRadioButton rbCustomLocation;
	private JTextField txtCustomPath;
	private JButton btnBrowse;
	private JLabel lblDefaultPath;
	private JLabel lblSpaceInfo;
	
	private String defaultDatabasePath;
	
	public Pnl_SetupDatabaseLocation( SetupConfiguration config )
	{
		super( config );
	}
	
	private static String computeDefaultPath()
	{
		String userHome = System.getProperty( "user.home" );
		String appDataDir;
		
		String os = System.getProperty( "os.name" ).toLowerCase();
		if( os.contains( "win" ) )
		{
			appDataDir = System.getenv( "APPDATA" );
			if( appDataDir == null )
			{
				appDataDir = userHome + File.separator + "AppData" + File.separator + "Roaming";
			}
		}
		else if( os.contains( "mac" ) )
		{
			appDataDir = userHome + File.separator + "Library" + File.separator + "Application Support";
		}
		else
		{
			appDataDir = userHome + File.separator + ".local" + File.separator + "share";
		}
		
		return appDataDir + File.separator + "TherapyNotes" + File.separator + "data";
	}
	
	@Override
	protected String getStepTitle()
	{
		return "Database Location";
	}
	
	@Override
	protected String getStepDescription()
	{
		return "Choose where your client data will be stored. The default location works well for "
				+ "most users. Choose a custom location if you need to store data on a specific drive " + "or network location.";
	}
	
	@Override
	protected void buildContent( JPanel container )
	{
		// Initialize the default path (must be done here due to super() calling buildContent)
		defaultDatabasePath = computeDefaultPath();
		
		ButtonGroup locationGroup = new ButtonGroup();
		
		// Default Location Option
		rbDefaultLocation = new JRadioButton( "Use default location (recommended)" );
		rbDefaultLocation.setFont( rbDefaultLocation.getFont().deriveFont( Font.BOLD, 13f ) );
		rbDefaultLocation.setSelected( true );
		rbDefaultLocation.setAlignmentX( Component.LEFT_ALIGNMENT );
		locationGroup.add( rbDefaultLocation );
		
		container.add( rbDefaultLocation );
		container.add( createVerticalSpacer( 5 ) );
		
		// Show default path
		JPanel defaultPathPanel = new JPanel( new BorderLayout() );
		defaultPathPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
		defaultPathPanel.setBorder( new EmptyBorder( 0, 28, 0, 0 ) );
		defaultPathPanel.setMaximumSize( new Dimension( Integer.MAX_VALUE, 25 ) );
		
		lblDefaultPath = new JLabel( defaultDatabasePath );
		lblDefaultPath.setFont( lblDefaultPath.getFont().deriveFont( 11f ) );
		lblDefaultPath.setForeground( new Color( 100, 100, 100 ) );
		defaultPathPanel.add( lblDefaultPath, BorderLayout.WEST );
		
		container.add( defaultPathPanel );
		container.add( createVerticalSpacer( 20 ) );
		
		// Custom Location Option
		rbCustomLocation = new JRadioButton( "Choose custom location" );
		rbCustomLocation.setFont( rbCustomLocation.getFont().deriveFont( Font.BOLD, 13f ) );
		rbCustomLocation.setAlignmentX( Component.LEFT_ALIGNMENT );
		locationGroup.add( rbCustomLocation );
		
		container.add( rbCustomLocation );
		container.add( createVerticalSpacer( 10 ) );
		
		// Custom path selection
		JPanel customPathPanel = new JPanel( new BorderLayout( 10, 0 ) );
		customPathPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
		customPathPanel.setBorder( new EmptyBorder( 0, 28, 0, 0 ) );
		customPathPanel.setMaximumSize( new Dimension( Integer.MAX_VALUE, 35 ) );
		
		txtCustomPath = new JTextField();
		txtCustomPath.setEnabled( false );
		
		btnBrowse = new JButton( "Browse..." );
		btnBrowse.setEnabled( false );
		btnBrowse.setPreferredSize( new Dimension( 100, 28 ) );
		
		customPathPanel.add( txtCustomPath, BorderLayout.CENTER );
		customPathPanel.add( btnBrowse, BorderLayout.EAST );
		
		container.add( customPathPanel );
		container.add( createVerticalSpacer( 25 ) );
		
		// Storage info panel
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout( new BoxLayout( infoPanel, BoxLayout.Y_AXIS ) );
		infoPanel.setAlignmentX( Component.LEFT_ALIGNMENT );
		infoPanel.setBackground( new Color( 240, 248, 255 ) );
		infoPanel.setBorder( BorderFactory.createCompoundBorder( BorderFactory.createLineBorder( new Color( 173, 216, 230 ), 1 ),
				new EmptyBorder( 15, 15, 15, 15 ) ) );
		infoPanel.setMaximumSize( new Dimension( Integer.MAX_VALUE, 120 ) );
		
		JLabel infoTitle = new JLabel( "Storage Information" );
		infoTitle.setFont( infoTitle.getFont().deriveFont( Font.BOLD, 12f ) );
		infoTitle.setAlignmentX( Component.LEFT_ALIGNMENT );
		
		lblSpaceInfo = new JLabel();
		lblSpaceInfo.setFont( lblSpaceInfo.getFont().deriveFont( 11f ) );
		lblSpaceInfo.setAlignmentX( Component.LEFT_ALIGNMENT );
		updateSpaceInfo( defaultDatabasePath );
		
		JLabel encryptionNote = new JLabel(
				"<html><body style='width: 400px'>" + "<b>Security:</b> Client data is stored in an unencrypted SQLite database. "
						+ "For additional protection, consider storing on an encrypted drive." + "</body></html>" );
		encryptionNote.setFont( encryptionNote.getFont().deriveFont( 11f ) );
		encryptionNote.setAlignmentX( Component.LEFT_ALIGNMENT );
		encryptionNote.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
		
		infoPanel.add( infoTitle );
		infoPanel.add( Box.createVerticalStrut( 8 ) );
		infoPanel.add( lblSpaceInfo );
		infoPanel.add( encryptionNote );
		
		container.add( infoPanel );
		
		// Event handlers
		setupEventHandlers();
		
		// Load existing values
		loadExistingValues();
	}
	
	private void setupEventHandlers()
	{
		rbDefaultLocation.addActionListener( e ->
		{
			txtCustomPath.setEnabled( false );
			btnBrowse.setEnabled( false );
			updateSpaceInfo( defaultDatabasePath );
		} );
		
		rbCustomLocation.addActionListener( e ->
		{
			txtCustomPath.setEnabled( true );
			btnBrowse.setEnabled( true );
			if( !txtCustomPath.getText().isEmpty() )
			{
				updateSpaceInfo( txtCustomPath.getText() );
			}
		} );
		
		btnBrowse.addActionListener( e -> browseForLocation() );
		
		txtCustomPath.addActionListener( e ->
		{
			if( !txtCustomPath.getText().isEmpty() )
			{
				updateSpaceInfo( txtCustomPath.getText() );
			}
		} );
	}
	
	private void browseForLocation()
	{
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
		chooser.setDialogTitle( "Select Database Location" );
		
		// Start from current custom path or user home
		String startPath = txtCustomPath.getText().isEmpty() ? System.getProperty( "user.home" ) : txtCustomPath.getText();
		chooser.setCurrentDirectory( new File( startPath ) );
		
		int result = chooser.showOpenDialog( this );
		if( result == JFileChooser.APPROVE_OPTION )
		{
			String selectedPath = chooser.getSelectedFile().getAbsolutePath();
			txtCustomPath.setText( selectedPath );
			updateSpaceInfo( selectedPath );
		}
	}
	
	private void updateSpaceInfo( String path )
	{
		try
		{
			File location = new File( path );
			
			// Navigate up to find an existing parent directory
			while( !location.exists() && location.getParentFile() != null )
			{
				location = location.getParentFile();
			}
			
			if( location.exists() )
			{
				long freeSpace = location.getFreeSpace();
				long totalSpace = location.getTotalSpace();
				
				String freeStr = formatBytes( freeSpace );
				String totalStr = formatBytes( totalSpace );
				
				lblSpaceInfo.setText( String.format( "<html>Available space: <b>%s</b> free of %s total</html>", freeStr, totalStr ) );
			}
			else
			{
				lblSpaceInfo.setText( "Unable to determine available space" );
			}
		}
		catch( Exception e )
		{
			lblSpaceInfo.setText( "Unable to determine available space" );
		}
	}
	
	private String formatBytes( long bytes )
	{
		if( bytes < 1024 )
			return bytes + " B";
		int exp = (int) ( Math.log( bytes ) / Math.log( 1024 ) );
		String pre = "KMGTPE".charAt( exp - 1 ) + "";
		return String.format( "%.1f %sB", bytes / Math.pow( 1024, exp ), pre );
	}
	
	private void loadExistingValues()
	{
		String existingPath = config.getDatabasePath();
		if( existingPath != null && !existingPath.equals( defaultDatabasePath ) )
		{
			rbCustomLocation.setSelected( true );
			txtCustomPath.setEnabled( true );
			btnBrowse.setEnabled( true );
			txtCustomPath.setText( existingPath );
			updateSpaceInfo( existingPath );
		}
	}
	
	@Override
	public boolean validateStep()
	{
		if( rbCustomLocation.isSelected() )
		{
			String customPath = txtCustomPath.getText().trim();
			
			if( customPath.isEmpty() )
			{
				showValidationError( "Please select a location for the database." );
				btnBrowse.requestFocus();
				return false;
			}
			
			File location = new File( customPath );
			
			// Check if path exists or can be created
			if( !location.exists() )
			{
				int result = JOptionPane.showConfirmDialog( this, "The selected directory does not exist. Create it?", "Create Directory",
						JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE );
				
				if( result == JOptionPane.YES_OPTION )
				{
					if( !location.mkdirs() )
					{
						showValidationError( "Unable to create the selected directory. " + "Please check permissions and try again." );
						return false;
					}
				}
				else
				{
					return false;
				}
			}
			
			// Check if location is writable
			if( !location.canWrite() )
			{
				showValidationError( "The selected location is not writable. " + "Please choose a different location." );
				return false;
			}
			
			// Check for sufficient space (minimum 100MB)
			if( location.getFreeSpace() < 100 * 1024 * 1024 )
			{
				showValidationError( "The selected location has less than 100MB of free space. "
						+ "Please choose a location with more available space." );
				return false;
			}
		}
		
		return true;
	}
	
	@Override
	public void saveStepData()
	{
		if( rbDefaultLocation.isSelected() )
		{
			config.setDatabasePath( defaultDatabasePath );
		}
		else
		{
			config.setDatabasePath( txtCustomPath.getText().trim() );
		}
	}
}