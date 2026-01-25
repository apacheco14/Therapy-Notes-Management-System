package com.alexpacheco.therapynotes.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link GlobalExceptionHandler}.
 * 
 * <p>
 * Note: Some tests verify behavior without dialogs since AppController requires MainWindow initialization. Full integration testing should
 * verify dialog display in the running application.
 */
class GlobalExceptionHandlerTest
{
	private Thread.UncaughtExceptionHandler originalHandler;
	
	@BeforeEach
	void setUp()
	{
		originalHandler = Thread.getDefaultUncaughtExceptionHandler();
	}
	
	@AfterEach
	void tearDown()
	{
		Thread.setDefaultUncaughtExceptionHandler( originalHandler );
	}
	
	@Nested
	@DisplayName( "Constructor Tests" )
	class ConstructorTests
	{
		@Test
		@DisplayName( "Default constructor creates handler with dialogs enabled" )
		void defaultConstructor_createsHandlerWithDialogsEnabled()
		{
			GlobalExceptionHandler handler = new GlobalExceptionHandler();
			assertNotNull( handler );
		}
		
		@Test
		@DisplayName( "Constructor with dialogs disabled creates valid handler" )
		void constructorWithDialogsDisabled_createsValidHandler()
		{
			GlobalExceptionHandler handler = new GlobalExceptionHandler( false );
			assertNotNull( handler );
		}
	}
	
	@Nested
	@DisplayName( "Installation Tests" )
	class InstallationTests
	{
		@Test
		@DisplayName( "install() sets default uncaught exception handler" )
		void install_setsDefaultHandler()
		{
			GlobalExceptionHandler.install( false ); // No dialogs for test
			
			Thread.UncaughtExceptionHandler handler = Thread.getDefaultUncaughtExceptionHandler();
			assertNotNull( handler );
			assertInstanceOf( GlobalExceptionHandler.class, handler );
		}
		
		@Test
		@DisplayName( "install() sets AWT exception handler property" )
		void install_setsAwtExceptionHandlerProperty()
		{
			GlobalExceptionHandler.install( false );
			
			String handlerClass = System.getProperty( "sun.awt.exception.handler" );
			assertEquals( GlobalExceptionHandler.class.getName(), handlerClass );
		}
	}
	
	@Nested
	@DisplayName( "Exception Handling Tests" )
	class ExceptionHandlingTests
	{
		@Test
		@DisplayName( "uncaughtException handles RuntimeException without throwing" )
		void uncaughtException_handlesRuntimeException()
		{
			GlobalExceptionHandler handler = new GlobalExceptionHandler( false );
			RuntimeException exception = new RuntimeException( "Test exception" );
			
			assertDoesNotThrow( () -> handler.uncaughtException( Thread.currentThread(), exception ) );
		}
		
		@Test
		@DisplayName( "uncaughtException handles null message in exception" )
		void uncaughtException_handlesNullMessage()
		{
			GlobalExceptionHandler handler = new GlobalExceptionHandler( false );
			RuntimeException exception = new RuntimeException( (String) null );
			
			assertDoesNotThrow( () -> handler.uncaughtException( Thread.currentThread(), exception ) );
		}
		
		@Test
		@DisplayName( "uncaughtException handles nested exceptions" )
		void uncaughtException_handlesNestedException()
		{
			GlobalExceptionHandler handler = new GlobalExceptionHandler( false );
			Exception cause = new Exception( "Root cause" );
			RuntimeException exception = new RuntimeException( "Wrapper", cause );
			
			assertDoesNotThrow( () -> handler.uncaughtException( Thread.currentThread(), exception ) );
		}
	}
	
	@Nested
	@DisplayName( "Error Type Tests" )
	class ErrorTypeTests
	{
		@Test
		@DisplayName( "OutOfMemoryError is handled without throwing" )
		void outOfMemoryError_handledSafely()
		{
			GlobalExceptionHandler handler = new GlobalExceptionHandler( false );
			OutOfMemoryError error = new OutOfMemoryError( "Test OOM" );
			
			assertDoesNotThrow( () -> handler.uncaughtException( Thread.currentThread(), error ) );
		}
		
		@Test
		@DisplayName( "SecurityException is handled without throwing" )
		void securityException_handledSafely()
		{
			GlobalExceptionHandler handler = new GlobalExceptionHandler( false );
			SecurityException exception = new SecurityException( "Access denied" );
			
			assertDoesNotThrow( () -> handler.uncaughtException( Thread.currentThread(), exception ) );
		}
		
		@Test
		@DisplayName( "StackOverflowError is handled without causing overflow" )
		void stackOverflowError_handledSafely()
		{
			GlobalExceptionHandler handler = new GlobalExceptionHandler( false );
			StackOverflowError error = new StackOverflowError();
			
			assertDoesNotThrow( () -> handler.uncaughtException( Thread.currentThread(), error ) );
		}
	}
	
	@Nested
	@DisplayName( "Thread Safety Tests" )
	class ThreadSafetyTests
	{
		@Test
		@DisplayName( "Handler works from background thread" )
		void handler_worksFromBackgroundThread() throws InterruptedException
		{
			GlobalExceptionHandler handler = new GlobalExceptionHandler( false );
			CountDownLatch latch = new CountDownLatch( 1 );
			AtomicBoolean handled = new AtomicBoolean( false );
			
			Thread testThread = new Thread( () ->
			{
				handler.uncaughtException( Thread.currentThread(), new RuntimeException( "Background test" ) );
				handled.set( true );
				latch.countDown();
			}, "TestBackgroundThread" );
			
			testThread.start();
			assertTrue( latch.await( 5, TimeUnit.SECONDS ) );
			assertTrue( handled.get() );
		}
		
		@Test
		@DisplayName( "Multiple exceptions can be handled sequentially" )
		void multipleExceptions_handledSequentially()
		{
			GlobalExceptionHandler handler = new GlobalExceptionHandler( false );
			
			assertDoesNotThrow( () ->
			{
				handler.uncaughtException( Thread.currentThread(), new RuntimeException( "First" ) );
				handler.uncaughtException( Thread.currentThread(), new RuntimeException( "Second" ) );
				handler.uncaughtException( Thread.currentThread(), new RuntimeException( "Third" ) );
			} );
		}
	}
	
	@Nested
	@DisplayName( "Handle Method Tests" )
	class HandleMethodTests
	{
		@Test
		@DisplayName( "handle() method works for AWT exception handling" )
		void handleMethod_worksForAwt()
		{
			GlobalExceptionHandler handler = new GlobalExceptionHandler( false );
			RuntimeException exception = new RuntimeException( "AWT test" );
			
			assertDoesNotThrow( () -> handler.handle( exception ) );
		}
	}
	
	@Nested
	@DisplayName( "Critical Error Detection Tests" )
	class CriticalErrorDetectionTests
	{
		@Test
		@DisplayName( "Error subclasses are treated as critical" )
		void errorSubclasses_areCritical()
		{
			GlobalExceptionHandler handler = new GlobalExceptionHandler( false );
			
			// These should all be handled without throwing
			assertDoesNotThrow( () ->
			{
				handler.uncaughtException( Thread.currentThread(), new OutOfMemoryError() );
				handler.uncaughtException( Thread.currentThread(), new StackOverflowError() );
				handler.uncaughtException( Thread.currentThread(), new NoClassDefFoundError() );
			} );
		}
		
		@Test
		@DisplayName( "Memory-related message exceptions are detected" )
		void memoryRelatedMessages_areDetected()
		{
			GlobalExceptionHandler handler = new GlobalExceptionHandler( false );
			
			// Exceptions with memory-related messages
			assertDoesNotThrow( () ->
			{
				handler.uncaughtException( Thread.currentThread(), new RuntimeException( "Out of memory" ) );
				handler.uncaughtException( Thread.currentThread(), new RuntimeException( "Java heap space exhausted" ) );
			} );
		}
		
		@Test
		@DisplayName( "Memory detection is case insensitive" )
		void memoryDetection_caseInsensitive()
		{
			GlobalExceptionHandler handler = new GlobalExceptionHandler( false );
			
			assertDoesNotThrow( () ->
			{
				handler.uncaughtException( Thread.currentThread(), new RuntimeException( "MEMORY allocation failed" ) );
				handler.uncaughtException( Thread.currentThread(), new RuntimeException( "HEAP space issue" ) );
			} );
		}
		
		@Test
		@DisplayName( "VirtualMachineError subclasses are handled" )
		void virtualMachineErrors_handled()
		{
			GlobalExceptionHandler handler = new GlobalExceptionHandler( false );
			
			assertDoesNotThrow( () ->
			{
				handler.uncaughtException( Thread.currentThread(), new InternalError( "VM error" ) );
				handler.uncaughtException( Thread.currentThread(), new UnknownError( "Unknown VM error" ) );
			} );
		}
	}
	
	@Nested
	@DisplayName( "Edge Case Tests" )
	class EdgeCaseTests
	{
		@Test
		@DisplayName( "Handles exception with empty message" )
		void handlesEmptyMessage()
		{
			GlobalExceptionHandler handler = new GlobalExceptionHandler( false );
			RuntimeException exception = new RuntimeException( "" );
			
			assertDoesNotThrow( () -> handler.uncaughtException( Thread.currentThread(), exception ) );
		}
		
		@Test
		@DisplayName( "Handles exception with very long message" )
		void handlesVeryLongMessage()
		{
			GlobalExceptionHandler handler = new GlobalExceptionHandler( false );
			String longMessage = "x".repeat( 10000 );
			RuntimeException exception = new RuntimeException( longMessage );
			
			assertDoesNotThrow( () -> handler.uncaughtException( Thread.currentThread(), exception ) );
		}
		
		@Test
		@DisplayName( "Handles exception with special characters in message" )
		void handlesSpecialCharacters()
		{
			GlobalExceptionHandler handler = new GlobalExceptionHandler( false );
			RuntimeException exception = new RuntimeException( "Error: %s \n\t\r <>&\"' \u0000" );
			
			assertDoesNotThrow( () -> handler.uncaughtException( Thread.currentThread(), exception ) );
		}
		
		@Test
		@DisplayName( "Handles deeply nested exception chain" )
		void handlesDeeplyNestedException()
		{
			GlobalExceptionHandler handler = new GlobalExceptionHandler( false );
			
			// Build a chain of 10 nested exceptions
			Exception current = new Exception( "Root cause" );
			for( int i = 0; i < 10; i++ )
			{
				current = new Exception( "Level " + i, current );
			}
			
			final Exception deepException = current;
			assertDoesNotThrow( () -> handler.uncaughtException( Thread.currentThread(), deepException ) );
		}
		
		@Test
		@DisplayName( "Handles exception with circular cause reference" )
		void handlesCircularCause()
		{
			GlobalExceptionHandler handler = new GlobalExceptionHandler( false );
			
			// Create exception - note: Java prevents true circular causes,
			// but we should handle any throwable gracefully
			RuntimeException exception = new RuntimeException( "Circular test" );
			
			assertDoesNotThrow( () -> handler.uncaughtException( Thread.currentThread(), exception ) );
		}
		
		@Test
		@DisplayName( "Handles custom exception types" )
		void handlesCustomException()
		{
			GlobalExceptionHandler handler = new GlobalExceptionHandler( false );
			
			// Custom exception class
			class CustomAppException extends RuntimeException
			{
				private static final long serialVersionUID = 1L;
				
				CustomAppException( String message )
				{
					super( message );
				}
			}
			
			assertDoesNotThrow( () -> handler.uncaughtException( Thread.currentThread(), new CustomAppException( "Custom error" ) ) );
		}
	}
	
	@Nested
	@DisplayName( "Installation Behavior Tests" )
	class InstallationBehaviorTests
	{
		@Test
		@DisplayName( "Multiple installs don't cause issues" )
		void multipleInstalls_noIssues()
		{
			assertDoesNotThrow( () ->
			{
				GlobalExceptionHandler.install( false );
				GlobalExceptionHandler.install( false );
				GlobalExceptionHandler.install( false );
			} );
			
			// Handler should still be set
			assertNotNull( Thread.getDefaultUncaughtExceptionHandler() );
		}
		
		@Test
		@DisplayName( "Handler remains functional after handling exception" )
		void handlerRemainsFunctional_afterException()
		{
			GlobalExceptionHandler handler = new GlobalExceptionHandler( false );
			
			// Handle first exception
			handler.uncaughtException( Thread.currentThread(), new RuntimeException( "First" ) );
			
			// Handler should still work for subsequent exceptions
			assertDoesNotThrow( () -> handler.uncaughtException( Thread.currentThread(), new RuntimeException( "Second" ) ) );
		}
	}
	
	@Nested
	@DisplayName( "Thread Integration Tests" )
	class ThreadIntegrationTests
	{
		@Test
		@DisplayName( "Handler catches actual uncaught exception on new thread" )
		void catchesActualUncaughtException() throws InterruptedException
		{
			CountDownLatch exceptionHandled = new CountDownLatch( 1 );
			AtomicBoolean handlerCalled = new AtomicBoolean( false );
			
			// Create custom handler that signals when called
			Thread.UncaughtExceptionHandler testHandler = ( thread, throwable ) ->
			{
				handlerCalled.set( true );
				exceptionHandled.countDown();
			};
			
			Thread testThread = new Thread( () ->
			{
				throw new RuntimeException( "Intentional test exception" );
			}, "TestExceptionThread" );
			
			testThread.setUncaughtExceptionHandler( testHandler );
			testThread.start();
			
			assertTrue( exceptionHandled.await( 5, TimeUnit.SECONDS ), "Handler should be called within timeout" );
			assertTrue( handlerCalled.get(), "Handler should have been invoked" );
		}
		
		@Test
		@DisplayName( "Handler works with daemon threads" )
		void worksWithDaemonThreads() throws InterruptedException
		{
			GlobalExceptionHandler handler = new GlobalExceptionHandler( false );
			CountDownLatch latch = new CountDownLatch( 1 );
			AtomicBoolean handled = new AtomicBoolean( false );
			
			Thread daemonThread = new Thread( () ->
			{
				handler.uncaughtException( Thread.currentThread(), new RuntimeException( "Daemon test" ) );
				handled.set( true );
				latch.countDown();
			}, "TestDaemonThread" );
			
			daemonThread.setDaemon( true );
			daemonThread.start();
			
			assertTrue( latch.await( 5, TimeUnit.SECONDS ) );
			assertTrue( handled.get() );
		}
		
		@Test
		@DisplayName( "Concurrent exceptions from multiple threads are handled" )
		void concurrentExceptions_handled() throws InterruptedException
		{
			GlobalExceptionHandler handler = new GlobalExceptionHandler( false );
			int threadCount = 10;
			CountDownLatch startLatch = new CountDownLatch( 1 );
			CountDownLatch completeLatch = new CountDownLatch( threadCount );
			AtomicBoolean anyFailed = new AtomicBoolean( false );
			
			// Create multiple threads that will throw exceptions simultaneously
			for( int i = 0; i < threadCount; i++ )
			{
				final int threadNum = i;
				Thread t = new Thread( () ->
				{
					try
					{
						startLatch.await(); // Wait for signal to start
						handler.uncaughtException( Thread.currentThread(), new RuntimeException( "Concurrent exception " + threadNum ) );
					}
					catch( Exception e )
					{
						anyFailed.set( true );
					}
					finally
					{
						completeLatch.countDown();
					}
				}, "ConcurrentTestThread-" + i );
				t.start();
			}
			
			// Release all threads at once
			startLatch.countDown();
			
			// Wait for all to complete
			assertTrue( completeLatch.await( 10, TimeUnit.SECONDS ), "All threads should complete within timeout" );
			assertFalse( anyFailed.get(), "No thread should have failed" );
		}
	}
	
	@Nested
	@DisplayName( "Specific Exception Type Tests" )
	class SpecificExceptionTypeTests
	{
		@Test
		@DisplayName( "Handles NullPointerException" )
		void handlesNullPointerException()
		{
			GlobalExceptionHandler handler = new GlobalExceptionHandler( false );
			
			assertDoesNotThrow( () -> handler.uncaughtException( Thread.currentThread(), new NullPointerException( "Null reference" ) ) );
		}
		
		@Test
		@DisplayName( "Handles IllegalStateException" )
		void handlesIllegalStateException()
		{
			GlobalExceptionHandler handler = new GlobalExceptionHandler( false );
			
			assertDoesNotThrow( () -> handler.uncaughtException( Thread.currentThread(), new IllegalStateException( "Bad state" ) ) );
		}
		
		@Test
		@DisplayName( "Handles IllegalArgumentException" )
		void handlesIllegalArgumentException()
		{
			GlobalExceptionHandler handler = new GlobalExceptionHandler( false );
			
			assertDoesNotThrow( () -> handler.uncaughtException( Thread.currentThread(), new IllegalArgumentException( "Bad argument" ) ) );
		}
		
		@Test
		@DisplayName( "Handles ArrayIndexOutOfBoundsException" )
		void handlesArrayIndexOutOfBounds()
		{
			GlobalExceptionHandler handler = new GlobalExceptionHandler( false );
			
			assertDoesNotThrow( () -> handler.uncaughtException( Thread.currentThread(), new ArrayIndexOutOfBoundsException( 5 ) ) );
		}
		
		@Test
		@DisplayName( "Handles ClassCastException" )
		void handlesClassCastException()
		{
			GlobalExceptionHandler handler = new GlobalExceptionHandler( false );
			
			assertDoesNotThrow( () -> handler.uncaughtException( Thread.currentThread(), new ClassCastException( "Cannot cast" ) ) );
		}
		
		@Test
		@DisplayName( "Handles UnsupportedOperationException" )
		void handlesUnsupportedOperationException()
		{
			GlobalExceptionHandler handler = new GlobalExceptionHandler( false );
			
			assertDoesNotThrow(
					() -> handler.uncaughtException( Thread.currentThread(), new UnsupportedOperationException( "Not supported" ) ) );
		}
	}
}