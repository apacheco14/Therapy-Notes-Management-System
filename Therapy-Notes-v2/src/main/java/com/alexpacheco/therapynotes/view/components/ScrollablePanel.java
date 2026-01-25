package com.alexpacheco.therapynotes.view.components;

import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import javax.swing.SwingConstants;

import com.alexpacheco.therapynotes.controller.AppController;

/**
 * A JPanel that implements Scrollable to track the viewport width. This ensures the panel shrinks when the viewport shrinks, preventing
 * horizontal overflow.
 */
public class ScrollablePanel extends JPanel implements Scrollable
{
	private static final long serialVersionUID = 1L;
	
	public ScrollablePanel()
	{
		super();
		setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
		setOpaque( true );
		setBackground( AppController.getBackgroundColor() );
	}
	
	public ScrollablePanel( LayoutManager layout )
	{
		super( layout );
		setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
		setOpaque( true );
		setBackground( AppController.getBackgroundColor() );
	}
	
	@Override
	public Dimension getPreferredScrollableViewportSize()
	{
		return getPreferredSize();
	}
	
	@Override
	public int getScrollableUnitIncrement( Rectangle visibleRect, int orientation, int direction )
	{
		return 16;
	}
	
	@Override
	public int getScrollableBlockIncrement( Rectangle visibleRect, int orientation, int direction )
	{
		return orientation == SwingConstants.VERTICAL ? visibleRect.height : visibleRect.width;
	}
	
	@Override
	public boolean getScrollableTracksViewportWidth()
	{
		// Always match viewport width - this is the key to preventing horizontal overflow
		return true;
	}
	
	@Override
	public boolean getScrollableTracksViewportHeight()
	{
		// Don't track height - allow vertical scrolling
		return false;
	}
}
