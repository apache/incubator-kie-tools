package org.uberfire.commons.async;

import static javax.ejb.TransactionAttributeType.*;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.ejb.Asynchronous;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.TransactionAttribute;
import javax.naming.InitialContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@Startup
@TransactionAttribute(NOT_SUPPORTED)
public class SimpleAsyncExecutorService {

    private static final Logger LOG = LoggerFactory.getLogger( SimpleAsyncExecutorService.class );

    private static final Integer AWAIT_TERMINATION_TIMEOUT = Integer.parseInt(System.getProperty("org.uberfire.watcher.quitetimeout", "3"));

    private static Object lock = new Object();

    private final ExecutorService executorService;

    private static SimpleAsyncExecutorService instance;
    private static SimpleAsyncExecutorService unmanagedInstance;

    private final AtomicBoolean hasAlreadyShutdown = new AtomicBoolean( false );

    private final Set<Future<?>> jobs = new CopyOnWriteArraySet<Future<?>>();

    public static  SimpleAsyncExecutorService getDefaultInstance() {
        synchronized (lock) {
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
        }

        return instance;
    }

    public static SimpleAsyncExecutorService getUnmanagedInstance() {
        synchronized (lock) {
            if ( instance != null && instance.executorService != null ) {
                return instance;
            } else if ( unmanagedInstance == null ) {
                unmanagedInstance = new SimpleAsyncExecutorService( false );
            }
        return unmanagedInstance;
        }
    }

    public static void shutdownInstances() {
        synchronized (lock) {
            if ( unmanagedInstance != null ) {
                unmanagedInstance.shutdown();
            }
            if ( instance != null && instance.executorService != null ) {
                instance.shutdown();
        }
        }
    }

    public SimpleAsyncExecutorService() {
        executorService = null;
    }

    public SimpleAsyncExecutorService( boolean notEJB ) {
        executorService = Executors.newCachedThreadPool( new DescriptiveThreadFactory() );
    }

    @Asynchronous
    @Lock(LockType.READ)
    public void execute( final Runnable r ) {
        if ( executorService != null ) {
            jobs.add( executorService.submit( r ) );
        } else {
            r.run();
        }
    }

    private void shutdown() {
        if ( !hasAlreadyShutdown.getAndSet( true ) && executorService != null ) {

            for ( final Future<?> job : jobs ) {
                if ( !job.isCancelled() && !job.isDone() ) {
                    job.cancel( true );
                }
            }

            executorService.shutdown(); // Disable new tasks from being submitted
            try {
                // Wait a while for existing tasks to terminate
                if ( !executorService.awaitTermination( AWAIT_TERMINATION_TIMEOUT, TimeUnit.SECONDS ) ) {
                    executorService.shutdownNow(); // Cancel currently executing tasks
                    // Wait a while for tasks to respond to being cancelled
                    if ( !executorService.awaitTermination( AWAIT_TERMINATION_TIMEOUT, TimeUnit.SECONDS ) ) {
                        LOG.error( "Thread pool did not terminate." );
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
