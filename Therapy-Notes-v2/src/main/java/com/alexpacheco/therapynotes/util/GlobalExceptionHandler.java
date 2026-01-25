package com.alexpacheco.therapynotes.util;

import java.lang.Thread.UncaughtExceptionHandler;

import javax.swing.SwingUtilities;

import com.alexpacheco.therapynotes.controller.AppController;

/**
 * Global exception handler for uncaught exceptions throughout the application.
 * 
 * <p>
 * This handler ensures that all uncaught exceptions are:
 * <ul>
 * <li>Logged via {@link AppLogger} for troubleshooting and audit purposes</li>
 * <li>Presented to the user via {@link AppController} in a friendly manner</li>
 * <li>Handled gracefully without exposing sensitive information</li>
 * </ul>
 * 
 * <p>
 * Usage:
 * 
 * <pre>
 * // Install as the default handler for all threads (call early in main)
 * GlobalExceptionHandler.install();
 * </pre>
 * 
 * @since 1.1.0
 */
public class GlobalExceptionHandler implements UncaughtExceptionHandler
{
	private static final String ERROR_MESSAGE = "An unexpected error has occurred. The error has been logged.\n\n"
			+ "If this problem persists, please contact support.";
	private static final String CRITICAL_ERROR_MESSAGE = "A critical error has occurred and the application may be unstable.\n\n"
			+ "It is recommended to save your work and restart the application.";
	
	private final boolean showDialogs;
	
	/**
	 * Creates a new GlobalExceptionHandler with default settings. Dialogs will be shown via AppController.
	 */
	public GlobalExceptionHandler()
	{
		this( true );
	}
	
	/**
	 * Creates a new GlobalExceptionHandler with configurable dialog display.
	 * 
	 * @param showDialogs whether to show error dialogs to the user
	 */
	public GlobalExceptionHandler( boolean showDialogs )
	{
		this.showDialogs = showDialogs;
	}
	
	/**
	 * Installs this handler as the default uncaught exception handler for all threads, including the AWT Event Dispatch Thread.
	 */
	public static void install()
	{
		install( true );
	}
	
	/**
	 * Installs this handler as the default uncaught exception handler for all threads, including the AWT Event Dispatch Thread.
	 * 
	 * @param showDialogs whether to show error dialogs to the user
	 */
	public static void install( boolean showDialogs )
	{
		GlobalExceptionHandler handler = new GlobalExceptionHandler( showDialogs );
		
		// Set as default handler for all threads
		Thread.setDefaultUncaughtExceptionHandler( handler );
		
		// Also handle exceptions on the EDT
		// This property ensures AWT exceptions are also caught
		System.setProperty( "sun.awt.exception.handler", GlobalExceptionHandler.class.getName() );
		
		AppLogger.info( "Global exception handler installed" );
	}
	
	@Override
	public void uncaughtException( Thread thread, Throwable throwable )
	{
		try
		{
			logException( thread, throwable );
			
			if( showDialogs )
			{
				showErrorDialog( throwable );
			}
		}
		catch( Exception e )
		{
			// Last resort - don't let the handler itself throw
			System.err.println( "Error in GlobalExceptionHandler: " + e.getMessage() );
			e.printStackTrace();
		}
	}
	
	/**
	 * Logs the exception with thread context via AppLogger.
	 * 
	 * @param thread    the thread where the exception occurred
	 * @param throwable the uncaught exception
	 */
	private void logException( Thread thread, Throwable throwable )
	{
		String threadInfo = formatThreadInfo( thread );
		String message = String.format( "Uncaught exception in %s", threadInfo );
		
		AppLogger.error( message, throwable );
	}
	
	/**
	 * Formats thread information for logging.
	 * 
	 * @param thread the thread to format
	 * @return formatted thread information string
	 */
	private String formatThreadInfo( Thread thread )
	{
		return String.format( "Thread[name=%s, id=%d, priority=%d, state=%s]", thread.getName(), thread.getId(), thread.getPriority(),
				thread.getState() );
	}
	
	/**
	 * Shows an appropriate error dialog to the user via AppController. Ensures the dialog is shown on the EDT.
	 * 
	 * @param throwable the exception that occurred
	 */
	private void showErrorDialog( Throwable throwable )
	{
		boolean isCritical = isCriticalError( throwable );
		String message = isCritical ? CRITICAL_ERROR_MESSAGE : ERROR_MESSAGE;
		
		// Ensure dialog is shown on EDT
		if( SwingUtilities.isEventDispatchThread() )
		{
			AppController.showBasicErrorPopup( message );
		}
		else
		{
			SwingUtilities.invokeLater( () -> AppController.showBasicErrorPopup( message ) );
		}
	}
	
	/**
	 * Determines if the throwable represents a critical error.
	 * 
	 * @param throwable the exception to evaluate
	 * @return true if the error is critical
	 */
	private boolean isCriticalError( Throwable throwable )
	{
		return throwable instanceof Error || throwable instanceof SecurityException || isMemoryRelated( throwable );
	}
	
	/**
	 * Checks if the throwable is memory-related.
	 * 
	 * @param throwable the exception to check
	 * @return true if memory-related
	 */
	private boolean isMemoryRelated( Throwable throwable )
	{
		if( throwable instanceof OutOfMemoryError )
		{
			return true;
		}
		
		String message = throwable.getMessage();
		if( message != null )
		{
			String lowerMessage = message.toLowerCase();
			return lowerMessage.contains( "memory" ) || lowerMessage.contains( "heap" );
		}
		
		return false;
	}
	
	/**
	 * Handler method required for sun.awt.exception.handler property. This method is called reflectively by the AWT exception handling
	 * mechanism.
	 * 
	 * @param throwable the exception that occurred on the EDT
	 */
	public void handle( Throwable throwable )
	{
		uncaughtException( Thread.currentThread(), throwable );
	}
}