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

import static org.fest.assertions.api.Assertions.*;
import static org.uberfire.java.nio.fs.jgit.util.JGitUtil.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.uberfire.java.nio.file.FileSystem;

public class JGitFileSystemProviderEncodingTest extends AbstractTestInfra {

    private int gitDaemonPort;

    @Override
    public Map<String, String> getGitPreferences() {
        Map<String, String> gitPrefs = super.getGitPreferences();
        gitPrefs.put("org.uberfire.nio.git.daemon.enabled", "true");
        // use different port for every test -> easy to run tests in parallel
        gitDaemonPort = findFreePort();
        gitPrefs.put("org.uberfire.nio.git.daemon.port", String.valueOf(gitDaemonPort));
        return gitPrefs;
    }

    @Test
    public void test() throws IOException {
        final URI originRepo = URI.create( "git://encoding-origin-name" );

        final JGitFileSystem origin = (JGitFileSystem) provider.newFileSystem( originRepo, new HashMap<String, Object>() {{
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
            put( JGitFileSystemProvider.GIT_ENV_KEY_DEFAULT_REMOTE_NAME, "git://localhost:" + gitDaemonPort + "/encoding-origin-name" );
            put( "listMode", "ALL" );
        }};

        final FileSystem fs = provider.newFileSystem( newRepo, env );

        assertThat( fs ).isNotNull();

        fs.getPath( "file+name.txt" ).toUri();

        provider.getPath( fs.getPath( "file+name.txt" ).toUri() );

        assertThat( provider.getPath( fs.getPath( "file+name.txt" ).toUri() ) ).isEqualTo( fs.getPath( "file+name.txt" ) );

        assertThat( provider.getPath( fs.getPath( "file name.txt" ).toUri() ) ).isEqualTo( fs.getPath( "file name.txt" ) );

        assertThat( fs.getPath( "file.txt" ).toUri() );

        assertThat( provider.getPath( fs.getPath( "file.txt" ).toUri() ) ).isEqualTo( fs.getPath( "file.txt" ) );
    }

}
