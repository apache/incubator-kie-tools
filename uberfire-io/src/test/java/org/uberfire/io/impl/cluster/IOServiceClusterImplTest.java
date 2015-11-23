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

package org.uberfire.io.impl.cluster;

import java.net.URI;
import java.util.Arrays;

import org.junit.Test;
import org.uberfire.commons.cluster.ClusterService;
import org.uberfire.io.impl.IOServiceLockable;
import org.uberfire.io.lock.BatchLockControl;
import org.uberfire.java.nio.base.FileSystemId;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Option;
import org.uberfire.java.nio.file.Path;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class IOServiceClusterImplTest {

    @Test
    public void testFileSystemToCheckProxyIssuesWithExtraInterfaces() {
        final FileSystem mockedFS = mock( FileSystem.class );
        final FileSystem mockedFSId = mock( FileSystem.class, withSettings().extraInterfaces( FileSystemId.class ) );

        final Path rootPath = mock( Path.class );

        when( mockedFS.getRootDirectories() ).thenReturn( Arrays.asList( rootPath ) );
        when( mockedFSId.getRootDirectories() ).thenReturn( Arrays.asList( rootPath ) );

        when( rootPath.getFileSystem() ).thenReturn( mockedFSId );
        when( rootPath.toUri() ).thenReturn( URI.create( "jgit://myrepo" ) );

        when( ( (FileSystemId) mockedFSId ).id() ).thenReturn( "my-fsid" );

        final ClusterService clusterService = mock( ClusterService.class );
        final IOServiceLockable serviceLockable = mock( IOServiceLockable.class );
        final BatchLockControl batchLockControl = mock( BatchLockControl.class );

        when( serviceLockable.getFileSystems() ).thenReturn( Arrays.asList( mockedFSId, mockedFS ) );
        when( batchLockControl.getHoldCount() ).thenReturn( 0 );
        when( serviceLockable.getLockControl() ).thenReturn( batchLockControl );

        {
            final IOServiceClusterImpl ioServiceCluster = new TestWrapper( clusterService, serviceLockable );

            assertEquals( 0, ioServiceCluster.batchFileSystems.size() );

            ioServiceCluster.startBatch( mockedFS );

            assertEquals( 1, ioServiceCluster.batchFileSystems.size() );

            assertTrue( ioServiceCluster.batchFileSystems.contains( ( (FileSystemId) mockedFSId ).id() ) );

            ioServiceCluster.endBatch();

            verify( serviceLockable, times( 1 ) ).endBatch();

            assertEquals( 0, ioServiceCluster.batchFileSystems.size() );

            verify( clusterService, times( 1 ) ).unlock();
        }

        {
            final IOServiceClusterImpl ioServiceCluster = new TestWrapper( clusterService, serviceLockable );

            assertEquals( 0, ioServiceCluster.batchFileSystems.size() );

            ioServiceCluster.startBatch( new FileSystem[]{ mockedFS }, mock( Option.class ) );

            assertEquals( 1, ioServiceCluster.batchFileSystems.size() );

            assertTrue( ioServiceCluster.batchFileSystems.contains( ( (FileSystemId) mockedFSId ).id() ) );

            ioServiceCluster.endBatch();

            verify( serviceLockable, times( 2 ) ).endBatch();

            assertEquals( 0, ioServiceCluster.batchFileSystems.size() );

            verify( clusterService, times( 2 ) ).unlock();
        }

        {
            final IOServiceClusterImpl ioServiceCluster = new TestWrapper( clusterService, serviceLockable );

            assertEquals( 0, ioServiceCluster.batchFileSystems.size() );

            ioServiceCluster.startBatch( mockedFS, mock( Option.class ) );

            assertEquals( 1, ioServiceCluster.batchFileSystems.size() );

            assertTrue( ioServiceCluster.batchFileSystems.contains( ( (FileSystemId) mockedFSId ).id() ) );

            ioServiceCluster.endBatch();

            verify( serviceLockable, times( 3 ) ).endBatch();

            assertEquals( 0, ioServiceCluster.batchFileSystems.size() );

            verify( clusterService, times( 3 ) ).unlock();
        }
    }

    private class TestWrapper extends IOServiceClusterImpl {

        public TestWrapper( final ClusterService clusterService,
                            final IOServiceLockable service ) {
            this.clusterService = clusterService;
            this.service = service;
        }
    }
}
