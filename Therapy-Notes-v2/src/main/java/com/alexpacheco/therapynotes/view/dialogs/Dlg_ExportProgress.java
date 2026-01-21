package com.alexpacheco.therapynotes.view.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

/**
 * A dialog that displays export progress with a progress bar and status message.
 */
public class Dlg_ExportProgress extends JDialog
{
	private static final long serialVersionUID = -1898845895274949276L;
	private JProgressBar progressBar;
	private JLabel statusLabel;
	private JLabel countLabel;
	
	/**
	 * Creates a new Dlg_ExportProgress.
	 * 
	 * @param parent The parent frame
	 */
	public Dlg_ExportProgress(Frame parent)
	{
		super(parent, "Exporting Notes", true);
		initializeComponents();
		layoutComponents();
		configureDialog();
	}
	
	/**
	 * Initializes all UI components.
	 */
	private void initializeComponents()
	{
		progressBar = new JProgressBar(0, 100);
		progressBar.setStringPainted(true);
		progressBar.setPreferredSize(new Dimension(350, 25));
		
		statusLabel = new JLabel("Preparing export...", SwingConstants.CENTER);
		statusLabel.setFont(statusLabel.getFont().deriveFont(Font.PLAIN, 12f));
		
		countLabel = new JLabel(" ", SwingConstants.CENTER);
		countLabel.setFont(countLabel.getFont().deriveFont(Font.BOLD, 14f));
	}
	
	/**
	 * Lays out all components.
	 */
	private void layoutComponents()
	{
		JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
		contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
		
		JPanel centerPanel = new JPanel(new BorderLayout(5, 10));
		centerPanel.add(countLabel, BorderLayout.NORTH);
		centerPanel.add(progressBar, BorderLayout.CENTER);
		centerPanel.add(statusLabel, BorderLayout.SOUTH);
		
		contentPanel.add(centerPanel, BorderLayout.CENTER);
		
		setContentPane(contentPanel);
	}
	
	/**
	 * Configures dialog properties.
	 */
	private void configureDialog()
	{
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setResizable(false);
		pack();
		setLocationRelativeTo(getParent());
	}
	
	/**
	 * Updates the progress bar and count label.
	 * 
	 * @param current The current item number (1-based)
	 * @param total   The total number of items
	 */
	public void setProgress(int current, int total)
	{
		int percentage = (int) ((current / (double) total) * 100);
		progressBar.setValue(percentage);
		countLabel.setText("Exporting note " + current + " of " + total);
		setStatus("Exporting notes...");
	}
	
	/**
	 * Updates the status message.
	 * 
	 * @param message The status message to display
	 */
	public void setStatus(String message)
	{
		statusLabel.setText(message);
	}
	
	/**
	 * Sets the progress bar to indeterminate mode for unknown duration tasks.
	 * 
	 * @param indeterminate true for indeterminate mode, false for determinate
	 */
	public void setIndeterminate(boolean indeterminate)
	{
		progressBar.setIndeterminate(indeterminate);
		if (indeterminate)
		{
			progressBar.setStringPainted(false);
		}
		else
		{
			progressBar.setStringPainted(true);
		}
	}
}