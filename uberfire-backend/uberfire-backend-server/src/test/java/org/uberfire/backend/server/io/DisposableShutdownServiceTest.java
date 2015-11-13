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
import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.uberfire.commons.async.SimpleAsyncExecutorService;
import org.uberfire.commons.cluster.ClusterService;
import org.uberfire.commons.lifecycle.PriorityDisposable;
import org.uberfire.commons.lifecycle.PriorityDisposableRegistry;
import org.uberfire.java.nio.file.api.FileSystemProviders;
import org.uberfire.java.nio.file.spi.FileSystemProvider;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProvider;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PriorityDisposableRegistry.class,
        SimpleAsyncExecutorService.class,
        FileSystemProviders.class })
public class DisposableShutdownServiceTest {

    @Test
    public void testGeneralStatic() {
        mockStatic( PriorityDisposableRegistry.class );
        mockStatic( SimpleAsyncExecutorService.class );
        mockStatic( FileSystemProviders.class );

        final JGitFileSystemProvider disposableProvider = mock( JGitFileSystemProvider.class );

        when( FileSystemProviders.installedProviders() ).thenReturn( Arrays.asList( mock( FileSystemProvider.class ), disposableProvider ) );

        new DisposableShutdownService().contextDestroyed( null );

        verify( disposableProvider, times( 1 ) ).dispose();

        PowerMockito.verifyStatic();
        SimpleAsyncExecutorService.shutdownInstances();

        PowerMockito.verifyStatic();
        PriorityDisposableRegistry.clear();
    }

    @Test
    public void testCluster() {
        mockStatic( PriorityDisposableRegistry.class );
        mockStatic( SimpleAsyncExecutorService.class );
        mockStatic( FileSystemProviders.class );

        final ClusterService clusterService = mock( ClusterService.class );

        when( FileSystemProviders.installedProviders() ).thenReturn( Collections.<FileSystemProvider>emptyList() );
        when( PriorityDisposableRegistry.getDisposables() ).thenReturn( Arrays.asList( mock( PriorityDisposable.class ), clusterService ) );

        new DisposableShutdownService().contextDestroyed( null );

        verify( clusterService, times( 1 ) ).lock();
        verify( clusterService, times( 1 ) ).unlock();
        verify( clusterService, times( 1 ) ).dispose();
    }

    @Test
    public void testDisposables() {
        mockStatic( PriorityDisposableRegistry.class );
        mockStatic( SimpleAsyncExecutorService.class );
        mockStatic( FileSystemProviders.class );

        final PriorityDisposable priorityDisposable1 = mock( PriorityDisposable.class );
        final PriorityDisposable priorityDisposable2 = mock( PriorityDisposable.class );
        final PriorityDisposable priorityDisposable3 = mock( PriorityDisposable.class );

        when( FileSystemProviders.installedProviders() ).thenReturn( Collections.<FileSystemProvider>emptyList() );
        when( PriorityDisposableRegistry.getDisposables() ).thenReturn( Arrays.asList( priorityDisposable1, priorityDisposable2, priorityDisposable3 ) );

        new DisposableShutdownService().contextDestroyed( null );

        verify( priorityDisposable1, times( 1 ) ).dispose();
        verify( priorityDisposable2, times( 1 ) ).dispose();
        verify( priorityDisposable3, times( 1 ) ).dispose();
    }

    @Test
    public void testSort() {
        final PriorityDisposable priorityDisposable1 = mock( PriorityDisposable.class );
        Mockito.when( priorityDisposable1.priority() ).thenReturn( -1 );
        final PriorityDisposable priorityDisposable2 = mock( PriorityDisposable.class );
        Mockito.when( priorityDisposable2.priority() ).thenReturn( 0 );
        final PriorityDisposable priorityDisposable3 = mock( PriorityDisposable.class );
        Mockito.when( priorityDisposable3.priority() ).thenReturn( 10 );
        final PriorityDisposable priorityDisposable4 = mock( PriorityDisposable.class );
        Mockito.when( priorityDisposable4.priority() ).thenReturn( 11 );

        final ArrayList<PriorityDisposable> disposables = new ArrayList<PriorityDisposable>();
        disposables.add( priorityDisposable3 );
        disposables.add( priorityDisposable2 );
        disposables.add( priorityDisposable4 );
        disposables.add( priorityDisposable1 );

        assertEquals( priorityDisposable3, disposables.get( 0 ) );
        assertEquals( priorityDisposable2, disposables.get( 1 ) );
        assertEquals( priorityDisposable4, disposables.get( 2 ) );
        assertEquals( priorityDisposable1, disposables.get( 3 ) );

        new DisposableShutdownService().sort( disposables );

        assertEquals( 4, disposables.size() );
        assertEquals( priorityDisposable4, disposables.get( 0 ) );
        assertEquals( priorityDisposable3, disposables.get( 1 ) );
        assertEquals( priorityDisposable2, disposables.get( 2 ) );
        assertEquals( priorityDisposable1, disposables.get( 3 ) );
    }

}
