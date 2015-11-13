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

package org.uberfire.java.nio.fs.file;

import java.io.File;
import java.net.URI;

import org.junit.Test;
import org.uberfire.java.nio.base.GeneralPathImpl;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;
import org.uberfire.java.nio.fs.file.SimpleWindowsFileStore;
import org.uberfire.java.nio.fs.file.SimpleWindowsFileSystem;

import static org.fest.assertions.api.Assertions.*;
import static org.uberfire.java.nio.fs.file.SimpleFileSystemProvider.OSType.*;

public class SimpleFileSystemProviderWindowsTest {

    final File[]                   roots      = new File[]{ new File( "c:\\" ), new File( "a:\\" ) };
    final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider( roots, WINDOWS );

    @Test
    public void simpleStateTest() {
        assertThat( fsProvider ).isNotNull();
        assertThat( fsProvider.getScheme() ).isNotEmpty().isEqualTo( "file" );

        final Path path = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), "c:\\path\\to\\file.txt", false );

        assertThat( path.getFileSystem() ).isNotNull().isInstanceOf( SimpleWindowsFileSystem.class );
    }

    @Test
    public void checkGetFileStore() {
        final Path path = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), "c:\\path\\to\\file.txt", false );

        assertThat( fsProvider.getFileStore( path ) ).isNotNull().isInstanceOf( SimpleWindowsFileStore.class );
        assertThat( fsProvider.getFileStore( path ).name() ).isNotNull().isEqualTo( "c:\\" );
    }

}
