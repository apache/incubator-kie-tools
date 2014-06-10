package org.uberfire.commons.async;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.naming.InitialContext;

import static javax.ejb.TransactionAttributeType.*;

@Stateless
@TransactionAttribute(NOT_SUPPORTED)
public class SimpleAsyncExecutorService {

    private final ExecutorService executorService;

    private static SimpleAsyncExecutorService instance;
    private static SimpleAsyncExecutorService unmanagedInstance;

    private final AtomicBoolean hasAlreadyShutdown = new AtomicBoolean( false );

    public static SimpleAsyncExecutorService getDefaultInstance() {
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

    public static SimpleAsyncExecutorService getUnmanagedInstance() {
        if ( instance != null && instance.executorService != null ) {
            return instance;
        } else if ( unmanagedInstance == null ) {
            unmanagedInstance = new SimpleAsyncExecutorService( false );
        }
        return unmanagedInstance;
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

    public void shutdown() {
        if ( !hasAlreadyShutdown.getAndSet( true ) && executorService != null ) {
            executorService.shutdown();
        }
    }

    public List<Runnable> shutdownNow() {
        if ( !hasAlreadyShutdown.getAndSet( true ) && executorService != null ) {
            return executorService.shutdownNow();
        }
        return Collections.emptyList();
    }
}
