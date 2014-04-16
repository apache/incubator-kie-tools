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

package org.uberfire.java.nio.fs.jgit;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uberfire.java.nio.file.FileSystem;

import static org.fest.assertions.api.Assertions.*;
import static org.uberfire.java.nio.fs.jgit.util.JGitUtil.*;

public class JGitFileSystemProviderEncodingTest extends AbstractTestInfra {

    private static final JGitFileSystemProvider PROVIDER = JGitFileSystemProvider.getInstance();

    @BeforeClass
    public static void setup() throws IOException {
        PROVIDER.buildAndStartDaemon();
    }

    @AfterClass
    public static void tearDown() throws IOException {
        PROVIDER.forceStopDaemon();
    }

    @Test
    public void test() throws IOException {
        final URI originRepo = URI.create( "git://encoding-origin-name" );

        final JGitFileSystem origin = (JGitFileSystem) PROVIDER.newFileSystem( originRepo, new HashMap<String, Object>() {{
            put( "listMode", "ALL" );
        }} );

        commit( origin.gitRepo(), "master", "user1", "user1@example.com", "commitx", null, null, false, new HashMap<String, File>() {{
            put( "file-name.txt", tempFile( "temp1" ) );
        }} );

        commit( origin.gitRepo(), "master", "user1", "user1@example.com", "commitx", null, null, false, new HashMap<String, File>() {{
            put( "file+name.txt", tempFile( "temp2" ) );
        }} );

        commit( origin.gitRepo(), "master", "user1", "user1@example.com", "commitx", null, null, false, new HashMap<String, File>() {{
            put( "file name.txt", tempFile( "temp3" ) );
        }} );

        final URI newRepo = URI.create( "git://my-encoding-repo-name" );

        final Map<String, Object> env = new HashMap<String, Object>() {{
            put( JGitFileSystemProvider.GIT_DEFAULT_REMOTE_NAME, "git://localhost:9418/encoding-origin-name" );
            put( "listMode", "ALL" );
        }};

        final FileSystem fs = PROVIDER.newFileSystem( newRepo, env );

        assertThat( fs ).isNotNull();

        fs.getPath( "file+name.txt" ).toUri();

        PROVIDER.getPath( fs.getPath( "file+name.txt" ).toUri() );

        assertThat( PROVIDER.getPath( fs.getPath( "file+name.txt" ).toUri() ) ).isEqualTo( fs.getPath( "file+name.txt" ) );

        assertThat( PROVIDER.getPath( fs.getPath( "file name.txt" ).toUri() ) ).isEqualTo( fs.getPath( "file name.txt" ) );

        assertThat( fs.getPath( "file.txt" ).toUri() );

        assertThat( PROVIDER.getPath( fs.getPath( "file.txt" ).toUri() ) ).isEqualTo( fs.getPath( "file.txt" ) );
    }

}
