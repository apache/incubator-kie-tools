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
import java.util.Map;

import org.junit.Test;
import org.uberfire.java.nio.base.FileSystemId;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FileSystemSyncTest {

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

        {
            final FileSystemSyncLock<String> fileSystemSyncLock = new FileSystemSyncLock<String>( "serviceId", mockedFS );
            final Map<String, String> content = fileSystemSyncLock.buildContent();

            assertEquals( "my-fsid", content.get( "fs_id" ) );
        }
        {
            final FileSystemSyncNonLock<String> fileSystemSyncNonLock = new FileSystemSyncNonLock<String>( "serviceId", mockedFS );
            final Map<String, String> content = fileSystemSyncNonLock.buildContent();

            assertEquals( "my-fsid", content.get( "fs_id" ) );
        }

    }

}
