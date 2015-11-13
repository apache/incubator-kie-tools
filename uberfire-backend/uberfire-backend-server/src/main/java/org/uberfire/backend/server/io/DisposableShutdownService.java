/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

package org.uberfire.backend.server.io;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.uberfire.commons.async.SimpleAsyncExecutorService;
import org.uberfire.commons.cluster.ClusterService;
import org.uberfire.commons.lifecycle.Disposable;
import org.uberfire.commons.lifecycle.PriorityDisposable;
import org.uberfire.commons.lifecycle.PriorityDisposableRegistry;
import org.uberfire.java.nio.file.api.FileSystemProviders;
import org.uberfire.java.nio.file.spi.FileSystemProvider;

public class DisposableShutdownService implements ServletContextListener {

    @Override
    public void contextInitialized( final ServletContextEvent sce ) {
    }

    @Override
    public void contextDestroyed( final ServletContextEvent sce ) {
        ClusterService clusterService = null;

        final List<PriorityDisposable> disposables = new ArrayList<PriorityDisposable>( PriorityDisposableRegistry.getDisposables() );
        for ( final PriorityDisposable disposable : disposables ) {
            if ( disposable instanceof ClusterService ) {
                clusterService = (ClusterService) disposable;
            }
        }

        if ( clusterService != null ) {
            disposables.remove( clusterService );
            clusterService.lock();
        }

        sort( disposables );

        for ( final PriorityDisposable disposable : disposables ) {
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

        PriorityDisposableRegistry.clear();
    }

    void sort( final List<PriorityDisposable> disposables ) {
        Collections.sort( disposables, new Comparator<PriorityDisposable>() {
            @Override
            public int compare( final PriorityDisposable o1,
                                final PriorityDisposable o2 ) {
                return ( o2.priority() < o1.priority() ) ? -1 : ( ( o2.priority() == o1.priority() ) ? 0 : 1 );
            }
        } );
    }
}
