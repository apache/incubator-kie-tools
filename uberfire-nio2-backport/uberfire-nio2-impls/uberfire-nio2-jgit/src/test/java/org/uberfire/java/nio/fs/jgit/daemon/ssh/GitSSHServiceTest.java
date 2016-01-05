/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.java.nio.fs.jgit.daemon.ssh;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.sshd.SshServer;
import org.eclipse.jgit.transport.resolver.ReceivePackFactory;
import org.eclipse.jgit.util.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemProvider;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

public class GitSSHServiceTest {

    private static final List<File> tempFiles = new ArrayList<File>();

    protected static File createTempDirectory()
            throws IOException {
        final File temp = File.createTempFile( "temp", Long.toString( System.nanoTime() ) );
        if ( !( temp.delete() ) ) {
            throw new IOException( "Could not delete temp file: " + temp.getAbsolutePath() );
        }

        if ( !( temp.mkdir() ) ) {
            throw new IOException( "Could not create temp directory: " + temp.getAbsolutePath() );
        }

        tempFiles.add( temp );

        return temp;
    }

    @AfterClass
    @BeforeClass
    public static void cleanup() {
        for ( final File tempFile : tempFiles ) {
            try {
                FileUtils.delete( tempFile, FileUtils.RECURSIVE );
            } catch ( IOException e ) {
            }
        }
    }

    @Test
    public void testStartStop() throws Exception {
        final GitSSHService sshService = new GitSSHService();
        final File certDir = createTempDirectory();

        sshService.setup( certDir,
                          null,
                          "10000",
                          mock( ReceivePackFactory.class ),
                          mock( JGitFileSystemProvider.RepositoryResolverImpl.class ) );

        sshService.start();
        assertTrue( sshService.isRunning() );

        sshService.stop();

        assertFalse( sshService.isRunning() );
    }

    @Test
    public void testCheckTimeout() throws Exception {
        final GitSSHService sshService = new GitSSHService();
        final File certDir = createTempDirectory();

        sshService.setup( certDir,
                          null,
                          "10000",
                          mock( ReceivePackFactory.class ),
                          mock( JGitFileSystemProvider.RepositoryResolverImpl.class ) );

        sshService.start();
        assertTrue( sshService.isRunning() );

        assertTrue( "10000".equals( sshService.getSshServer().getProperties().get( SshServer.IDLE_TIMEOUT ) ) );

        sshService.stop();

        assertFalse( sshService.isRunning() );
    }

    @Test
    public void testCheckSetupParameters() throws Exception {
        final GitSSHService sshService = new GitSSHService();
        final File certDir = createTempDirectory();

        try {
            sshService.setup( null,
                              null,
                              "10000",
                              mock( ReceivePackFactory.class ),
                              mock( JGitFileSystemProvider.RepositoryResolverImpl.class ) );
            fail( "has to fail" );
        } catch ( IllegalArgumentException ex ) {
            assertThat( ex.getMessage() ).contains( "'certDir'" );
        }

        try {
            sshService.setup( certDir,
                              null,
                              null,
                              mock( ReceivePackFactory.class ),
                              mock( JGitFileSystemProvider.RepositoryResolverImpl.class ) );
            fail( "has to fail" );
        } catch ( IllegalArgumentException ex ) {
            assertThat( ex.getMessage() ).contains( "'sshIdleTimeout'" );
        }

        try {
            sshService.setup( certDir,
                              null,
                              "",
                              mock( ReceivePackFactory.class ),
                              mock( JGitFileSystemProvider.RepositoryResolverImpl.class ) );
            fail( "has to fail" );
        } catch ( IllegalArgumentException ex ) {
            assertThat( ex.getMessage() ).contains( "'sshIdleTimeout'" );
        }

        try {
            sshService.setup( certDir,
                              null,
                              "100",
                              null,
                              mock( JGitFileSystemProvider.RepositoryResolverImpl.class ) );
            fail( "has to fail" );
        } catch ( IllegalArgumentException ex ) {
            assertThat( ex.getMessage() ).contains( "'receivePackFactory'" );
        }

        try {
            sshService.setup( certDir,
                              null,
                              "100",
                              mock( ReceivePackFactory.class ),
                              null );
            fail( "has to fail" );
        } catch ( IllegalArgumentException ex ) {
            assertThat( ex.getMessage() ).contains( "'repositoryResolver'" );
        }

        try {
            sshService.setup( certDir,
                              null,
                              "10000",
                              mock( ReceivePackFactory.class ),
                              mock( JGitFileSystemProvider.RepositoryResolverImpl.class ) );
        } catch ( IllegalArgumentException ex ) {
            fail( "should not fail" );
        }
    }
}
