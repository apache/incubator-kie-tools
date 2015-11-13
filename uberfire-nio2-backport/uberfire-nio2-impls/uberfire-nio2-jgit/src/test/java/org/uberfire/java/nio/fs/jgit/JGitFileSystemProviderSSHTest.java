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

package org.uberfire.java.nio.fs.jgit;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.transport.CredentialsProvider;
import org.junit.Test;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.security.FileSystemAuthenticator;
import org.uberfire.java.nio.security.FileSystemAuthorizer;
import org.uberfire.java.nio.security.FileSystemUser;

import static org.junit.Assert.*;
import static org.uberfire.java.nio.fs.jgit.util.JGitUtil.*;

public class JGitFileSystemProviderSSHTest extends AbstractTestInfra {

    private int gitSSHPort;

    @Override
    public Map<String, String> getGitPreferences() {
        Map<String, String> gitPrefs = super.getGitPreferences();

        gitPrefs.put( "org.uberfire.nio.git.ssh.enabled", "true" );
        gitSSHPort = findFreePort();
        gitPrefs.put( "org.uberfire.nio.git.ssh.port", String.valueOf( gitSSHPort ) );

        return gitPrefs;
    }

    @Test
    public void testSSHPostReceiveHook() throws IOException {
        //Setup Authorization/Authentication
        provider.setAuthenticator( new FileSystemAuthenticator() {
            @Override
            public FileSystemUser authenticate( final String username,
                                                final String password ) {
                return new FileSystemUser() {
                    @Override
                    public String getName() {
                        return "admin";
                    }
                };
            }
        } );
        provider.setAuthorizer( new FileSystemAuthorizer() {
            @Override
            public boolean authorize( final FileSystem fs,
                                      final FileSystemUser fileSystemUser ) {
                return true;
            }
        } );

        CredentialsProvider.setDefault( new UsernamePasswordCredentialsProvider( "admin",
                                                                                 "" ) );

        //Setup origin
        final URI originRepo = URI.create( "git://repo" );
        final JGitFileSystem origin = (JGitFileSystem) provider.newFileSystem( originRepo,
                                                                               new HashMap<String, Object>() {{
                                                                                   put( "listMode", "ALL" );
                                                                               }} );

        //Write a file to origin that we won't amend in the clone
        commit( origin.gitRepo(), "master", "user1", "user1@example.com", "commitx", null, null, false, new HashMap<String, File>() {{
            put( "file-name.txt", tempFile( "temp1" ) );
        }} );

        //Setup clone
        JGitFileSystem clone;
        clone = (JGitFileSystem) provider.newFileSystem( URI.create( "git://repo-clone" ),
                                                         new HashMap<String, Object>() {{
                                                             put( "init", "true" );
                                                             put( "origin", "ssh://admin@localhost:" + gitSSHPort + "/repo" );
                                                         }} );

        assertNotNull( clone );

        //Push clone back to origin
        provider.getFileSystem( URI.create( "git://repo-clone?push=ssh://admin@localhost:" + gitSSHPort + "/repo" ) );
    }

}
