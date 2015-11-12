/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.uberfire.commons.async;

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

import static javax.ejb.TransactionAttributeType.*;

@Singleton
@Startup
@TransactionAttribute(NOT_SUPPORTED)
public class SimpleAsyncExecutorService implements DisposableExecutor {

    private static final Logger LOG = LoggerFactory.getLogger( SimpleAsyncExecutorService.class );

    private static final Integer AWAIT_TERMINATION_TIMEOUT = Integer.parseInt( System.getProperty( "org.uberfire.watcher.quitetimeout", "3" ) );

    private static final Object lock = new Object();

    private static final AtomicBoolean isEJB = new AtomicBoolean( false );

    private final ExecutorService executorService;

    private static DisposableExecutor defaultInstance;
    private static DisposableExecutor managedInstance;
    private static DisposableExecutor unmanagedInstance;

    private final AtomicBoolean hasAlreadyShutdown = new AtomicBoolean( false );

    private final Set<Future<?>> jobs = new CopyOnWriteArraySet<Future<?>>();

    public static DisposableExecutor getDefaultInstance() {
        synchronized ( lock ) {
            if ( defaultInstance == null ) {

                DisposableExecutor _executorManager = null;
                try {
                    _executorManager = InitialContext.doLookup( "java:module/SimpleAsyncExecutorService" );
                    isEJB.set( true );
                } catch ( final Exception e ) {
                    LOG.warn( "Unable to instantiate EJB Asynchronous Bean. Falling back to Executors' CachedThreadPool.",
                              e );
                }

                if ( _executorManager == null ) {
                    if ( unmanagedInstance == null ) {
                        unmanagedInstance = new SimpleAsyncExecutorService( false );
                    }
                    defaultInstance = unmanagedInstance;
                } else {
                    if ( managedInstance == null ) {
                        managedInstance = _executorManager;
                    }
                    defaultInstance = managedInstance;
                }
            }
        }

        return defaultInstance;
    }

    public static DisposableExecutor getUnmanagedInstance() {
        synchronized ( lock ) {
            if ( unmanagedInstance == null ) {
                unmanagedInstance = new SimpleAsyncExecutorService( false );
            }
            return unmanagedInstance;
        }
    }

    public static void shutdownInstances() {
        synchronized ( lock ) {
            if ( !isEJB.get() && managedInstance != null ) {
                managedInstance.dispose();
            }
            if ( unmanagedInstance != null ) {
                unmanagedInstance.dispose();
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
    @Override
    public void execute( final Runnable r ) {
        if ( executorService != null ) {
            jobs.add( executorService.submit( r ) );
        } else {
            r.run();
        }
    }

    @Override
    public void dispose() {
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
