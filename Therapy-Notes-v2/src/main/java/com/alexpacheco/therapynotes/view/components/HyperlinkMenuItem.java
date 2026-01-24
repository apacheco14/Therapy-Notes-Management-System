package com.alexpacheco.therapynotes.view.components;

import java.awt.Desktop;
import java.net.URI;

import javax.swing.JMenuItem;
import com.alexpacheco.therapynotes.controller.AppController;
import com.alexpacheco.therapynotes.controller.enums.ErrorCode;
import com.alexpacheco.therapynotes.controller.exceptions.TherapyAppException;
import com.alexpacheco.therapynotes.util.AppLogger;

public class HyperlinkMenuItem extends JMenuItem
{
	private static final long serialVersionUID = 3872133909657832994L;
	private String url;
	
	public HyperlinkMenuItem( String text, String url )
	{
		super( text );
		this.url = url;
		
		addActionListener( e -> _openLink() );
	}
	
	private void _openLink()
	{
		if( Desktop.isDesktopSupported() )
		{
			Desktop desktop = Desktop.getDesktop();
			try
			{
				desktop.browse( new URI( url ) );
			}
			catch( Exception e )
			{
				AppLogger.error( "Could not open the link: " + url, e );
				AppController.showBasicErrorPopup( new TherapyAppException( e.getMessage(), ErrorCode.NOT_FOUND ),
						"Could not open the link." );
			}
		}
		else
		{
			AppLogger.warning( "Desktop browsing is not supported on this platform." );
			AppController.showBasicErrorPopup( "Desktop browsing is not supported on this platform." );
		}
	}
}
