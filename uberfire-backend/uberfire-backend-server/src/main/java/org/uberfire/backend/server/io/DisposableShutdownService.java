package org.uberfire.backend.server.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.annotation.PostConstruct;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.uberfire.commons.async.SimpleAsyncExecutorService;
import org.uberfire.commons.cluster.ClusterService;
import org.uberfire.commons.cluster.ClusterServiceFactory;
import org.uberfire.commons.lifecycle.Disposable;
import org.uberfire.commons.lifecycle.PriorityDisposable;
import org.uberfire.java.nio.file.api.FileSystemProviders;
import org.uberfire.java.nio.file.spi.FileSystemProvider;

public class DisposableShutdownService implements ServletContextListener {

    @Inject
    private Instance<PriorityDisposable> disposables;

    @Inject
    @Named("clusterServiceFactory")
    private ClusterServiceFactory clusterServiceFactory;

    private ClusterService clusterService = null;

    @PostConstruct
    public void init() {
        if ( clusterServiceFactory != null ) {
            //TODO: hack that should be changed soon;
            clusterService = clusterServiceFactory.build( null );
        }
    }

    @Override
    public void contextInitialized( ServletContextEvent sce ) {

    }

    @Override
    public void contextDestroyed( final ServletContextEvent sce ) {
        final ArrayList<PriorityDisposable> collection = new ArrayList<PriorityDisposable>();
        for ( final PriorityDisposable disposable : disposables ) {
            collection.add( disposable );
        }

        Collections.sort( collection, new Comparator<PriorityDisposable>() {
            @Override
            public int compare( final PriorityDisposable o1,
                                final PriorityDisposable o2 ) {
                return ( o2.priority() < o1.priority() ) ? -1 : ( ( o2.priority() == o1.priority() ) ? 0 : 1 );
            }
        } );

        if ( clusterService != null ) {
            clusterService.lock();
        }

        for ( final PriorityDisposable disposable : collection ) {
            disposable.dispose();
        }

        SimpleAsyncExecutorService.shutdownInstances();

        for ( final FileSystemProvider fileSystemProvider : FileSystemProviders.installedProviders() ) {
            if ( fileSystemProvider instanceof Disposable ) {
                ( (Disposable) fileSystemProvider ).dispose();
            }
        }

        if ( clusterService != null ) {
            clusterService.unlock();
            clusterService.dispose();
        }
    }
}
