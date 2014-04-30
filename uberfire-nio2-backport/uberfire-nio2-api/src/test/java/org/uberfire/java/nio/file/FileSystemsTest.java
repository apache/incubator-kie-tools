/*
 * Copyright 2012 JBoss Inc
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

package org.uberfire.java.nio.file;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.java.nio.fs.file.BaseSimpleFileSystem;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProvider;
import org.uberfire.java.nio.fs.jgit.JGitPathImpl;

import static org.fest.assertions.api.Assertions.*;

public class FileSystemsTest {

    @Before
    @After
    public void cleanup() throws IOException {
        FileUtils.deleteDirectory( new File( JGitFileSystemProvider.REPOSITORIES_CONTAINER_DIR ) );
    }

    @Test
    public void testGetDefault() {
        assertThat( FileSystems.getDefault() ).isNotNull().isInstanceOf( BaseSimpleFileSystem.class );
    }

    @Test
    public void testGetFileSystemByURI() {
        assertThat( FileSystems.getFileSystem( URI.create( "default:///" ) ) ).isNotNull().isInstanceOf( BaseSimpleFileSystem.class );
        assertThat( FileSystems.getFileSystem( URI.create( "file:///" ) ) ).isNotNull().isInstanceOf( BaseSimpleFileSystem.class );
    }

    @Test
    public void testNewFileSystem() {

        final Map<String, Object> env = new HashMap<String, Object>( 2 );
        env.put( "userName", "user" );
        env.put( "password", "pass" );

        final FileSystem fs = FileSystems.newFileSystem( URI.create( "git://my-test" ), env );

        assertThat( fs ).isNotNull();

        final FileSystem newFS = FileSystems.newFileSystem( JGitPathImpl.create( (JGitFileSystem) fs, "new_test", "my-other-test", false ), null );

        assertThat( newFS ).isNotNull();
    }

    @Test(expected = FileSystemAlreadyExistsException.class)
    public void testNewOnExistingFileSystem() {

        final Map<String, Object> env = new HashMap<String, Object>( 2 );
        env.put( "userName", "user" );
        env.put( "password", "pass" );

        FileSystems.newFileSystem( URI.create( "git://test" ), env );

        FileSystems.newFileSystem( URI.create( "git://test" ), env );
    }

    @Test(expected = IllegalArgumentException.class)
    public void getFileSystemNull() {
        FileSystems.getFileSystem( null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void newFileSystemNull1() {
        final Map<String, ?> emptyMap = Collections.emptyMap();
        FileSystems.newFileSystem( null, emptyMap );
    }

    @Test(expected = IllegalArgumentException.class)
    public void newFileSystemNull2() {
        FileSystems.newFileSystem( URI.create( "jgit:///test" ), null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void newFileSystemNull3() {
        FileSystems.newFileSystem( (URI) null, null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void newFileSystemNull4() {
        FileSystems.newFileSystem( (Path) null, null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void newFileSystemNull5() {
        FileSystems.newFileSystem( URI.create( "jgit:///test" ), null, null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void newFileSystemNull6() {
        FileSystems.newFileSystem( URI.create( "jgit:///test" ), null, null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void newFileSystemNull7() {
        FileSystems.newFileSystem( null, null, null );
    }

}
