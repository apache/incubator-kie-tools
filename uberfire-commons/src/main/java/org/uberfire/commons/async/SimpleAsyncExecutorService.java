package org.uberfire.commons.async;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.ejb.Asynchronous;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.naming.InitialContext;

import static javax.ejb.TransactionAttributeType.*;

@Singleton
@Startup
@TransactionAttribute(NOT_SUPPORTED)
public class SimpleAsyncExecutorService {

    private final ExecutorService executorService;

    private static SimpleAsyncExecutorService instance;
    private static SimpleAsyncExecutorService unmanagedInstance;

    private final AtomicBoolean hasAlreadyShutdown = new AtomicBoolean( false );

    public static synchronized SimpleAsyncExecutorService getDefaultInstance() {
        if ( instance == null ) {
            SimpleAsyncExecutorService _executorManager = null;
            try {
                _executorManager = InitialContext.doLookup( "java:module/SimpleAsyncExecutorService" );
            } catch ( final Exception ignored ) {
            }

            if ( _executorManager == null ) {
                instance = new SimpleAsyncExecutorService( false );
            } else {
                instance = _executorManager;
            }
        }

        return instance;
    }

    public static synchronized SimpleAsyncExecutorService getUnmanagedInstance() {
        if ( instance != null && instance.executorService != null ) {
            return instance;
        } else if ( unmanagedInstance == null ) {
            unmanagedInstance = new SimpleAsyncExecutorService( false );
        }
        return unmanagedInstance;
    }

    public static synchronized void shutdownInstances() {
        if ( unmanagedInstance != null ) {
            unmanagedInstance.shutdown();
        }
        if ( instance != null && instance.executorService != null ) {
            instance.shutdown();
        }
    }

    public SimpleAsyncExecutorService() {
        executorService = null;
    }

    public SimpleAsyncExecutorService( boolean notEJB ) {
        executorService = Executors.newCachedThreadPool( new DescriptiveThreadFactory() );
    }

    @Asynchronous
    public void execute( final Runnable r ) {
        if ( executorService != null ) {
            executorService.execute( r );
        } else {
            r.run();
        }
    }

    private void shutdown() {
        if ( !hasAlreadyShutdown.getAndSet( true ) && executorService != null ) {

            executorService.shutdown(); // Disable new tasks from being submitted
            try {
                // Wait a while for existing tasks to terminate
                if ( !executorService.awaitTermination( 60, TimeUnit.SECONDS ) ) {
                    executorService.shutdownNow(); // Cancel currently executing tasks
                    // Wait a while for tasks to respond to being cancelled
                    if ( !executorService.awaitTermination( 60, TimeUnit.SECONDS ) ) {
                        System.err.println( "Pool did not terminate" );
                    }
                }
            } catch ( InterruptedException ie ) {
                // (Re-)Cancel if current thread also interrupted
                executorService.shutdownNow();
                // Preserve interrupt status
                Thread.currentThread().interrupt();
            }

            executorService.shutdown();
        }
    }
}
