/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.URIish;
import org.fest.assertions.core.Condition;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.fs.jgit.util.commands.Mirror;

import static org.eclipse.jgit.api.ListBranchCommand.ListMode.*;
import static org.fest.assertions.api.Assertions.*;
import static org.uberfire.java.nio.fs.jgit.util.JGitUtil.*;

public class JGitMirrorTest extends AbstractTestInfra {

    public static final String TARGET_GIT = "test/target.git";
    public static final String ORIGIN = "https://github.com/uberfire/uberfire-website";
    private static Logger logger = LoggerFactory.getLogger( JGitMirrorTest.class );

    @Test
    public void testToHTTPMirrorSuccess() throws IOException, GitAPIException {
        final File parentFolder = createTempDirectory();
        final File directory = new File( parentFolder, TARGET_GIT );
        new Mirror( directory, ORIGIN, CredentialsProvider.getDefault() ).execute();

        final Git cloned = Git.open( directory );

        assertThat( cloned ).isNotNull();

        assertThat( branchList( cloned, ALL ) ).is( new Condition<List<Ref>>() {
            @Override
            public boolean matches( final List<Ref> refs ) {
                return refs.size() > 0;
            }
        } );

        assertThat( branchList( cloned, ALL ).get( 0 ).getName() ).isEqualTo( "refs/heads/master" );
        assertThat( branchList( cloned, ALL ).get( 2 ).getName() ).isEqualTo( "refs/remotes/origin/master" );

        URIish remoteUri = cloned.remoteList().call().get( 0 ).getURIs().get( 0 );
        String remoteUrl = remoteUri.getScheme() + "://" + remoteUri.getHost() + remoteUri.getPath();
        assertThat( remoteUrl ).isEqualTo( ORIGIN );

    }

    @Test
    public void testEmptyCredentials() throws IOException, GitAPIException {
        final File parentFolder = createTempDirectory();
        final File directory = new File( parentFolder, TARGET_GIT );
        new Mirror( directory, ORIGIN, null ).execute();

        final Git cloned = Git.open( directory );

        assertThat( cloned ).isNotNull();

        assertThat( branchList( cloned, ALL ) ).is( new Condition<List<Ref>>() {
            @Override
            public boolean matches( final List<Ref> refs ) {
                return refs.size() > 0;
            }
        } );

        assertThat( branchList( cloned, ALL ).get( 0 ).getName() ).isEqualTo( "refs/heads/master" );
        assertThat( branchList( cloned, ALL ).get( 2 ).getName() ).isEqualTo( "refs/remotes/origin/master" );

        URIish remoteUri = cloned.remoteList().call().get( 0 ).getURIs().get( 0 );
        String remoteUrl = remoteUri.getScheme() + "://" + remoteUri.getHost() + remoteUri.getPath();
        assertThat( remoteUrl ).isEqualTo( ORIGIN );

    }

    @Test
    public void testBadUrl() throws IOException, GitAPIException {
        final File parentFolder = createTempDirectory();
        final File directory = new File( parentFolder, TARGET_GIT );
        try {
            new Mirror( directory, ORIGIN + "sssss", CredentialsProvider.getDefault() ).execute();
            fail( "If got here the test is wrong because the ORIGIN does no exist" );
        } catch ( RuntimeException ex ) {
            assertThat( ex ).isNotNull();
            logger.info( ex.getMessage(), ex );
        }

    }

}
