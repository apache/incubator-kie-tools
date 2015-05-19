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
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.util.FS_POSIX_Java6;
import org.eclipse.jgit.util.Hook;
import org.eclipse.jgit.util.ProcessResult;
import org.junit.Test;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;

import static org.fest.assertions.api.Assertions.*;

public class JGitFileSystemProviderHookTest extends AbstractTestInfra {

    @Override
    public Map<String, String> getGitPreferences() {
        Map<String, String> gitPrefs = super.getGitPreferences();
        gitPrefs.put( "org.uberfire.nio.git.daemon.enabled", "true" );
        int gitDaemonPort = findFreePort();
        gitPrefs.put( "org.uberfire.nio.git.daemon.port", String.valueOf( gitDaemonPort ) );

        try {
            final File myTemp = createTempDirectory();
            gitPrefs.put( "org.uberfire.nio.git.hooks", myTemp.getAbsolutePath() );
            {
                final PrintWriter writer = new PrintWriter( new File( myTemp, "post-commit" ), "UTF-8" );
                writer.println( "# something" );
                writer.close();
            }

            {
                final PrintWriter writer = new PrintWriter( new File( myTemp, "pre-commit" ), "UTF-8" );
                writer.println( "# something" );
                writer.close();
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        return gitPrefs;
    }

    @Test
    public void testInstalledHook() {
        final URI newRepo = URI.create( "git://hook-repo-name" );

        final FileSystem fs = provider.newFileSystem( newRepo, EMPTY_ENV );

        assertThat( fs ).isNotNull();

        if ( fs instanceof JGitFileSystem ) {
            File[] hooks = new File( ( (JGitFileSystem) fs ).gitRepo().getRepository().getDirectory(), "hooks" ).listFiles();
            assertThat( hooks ).isNotEmpty().isNotNull();
            assertThat( hooks.length ).isEqualTo( 2 );

            boolean foundPreCommitHook = false;
            boolean foundPostCommitHook = false;
            for ( File hook : hooks ) {
                if ( hook.getName().equals( "pre-commit" ) ) {
                    foundPreCommitHook = hook.canExecute();
                } else if ( hook.getName().equals( "post-commit" ) ) {
                    foundPostCommitHook = hook.canExecute();
                }
            }
            assertThat( foundPreCommitHook ).isTrue();
            assertThat( foundPostCommitHook ).isTrue();
        }
    }

    @Test
    public void testExecutedPostCommitHook() throws IOException {
        final URI newRepo = URI.create( "git://hook-repo-name-executed" );

        final AtomicBoolean hookExecuted = new AtomicBoolean( false );
        final FileSystem fs = provider.newFileSystem( newRepo, EMPTY_ENV );

        provider.setDetectedFS( new FS_POSIX_Java6() {
            @Override
            public ProcessResult runIfPresent( Repository repox,
                                               Hook hook,
                                               String[] args ) throws JGitInternalException {
                if ( hook.equals( Hook.POST_COMMIT ) ) {
                    hookExecuted.set( true );
                }
                return null;
            }
        } );

        assertThat( fs ).isNotNull();

        final Path path = provider.getPath( URI.create( "git://user_branch@hook-repo-name-executed/some/path/myfile.txt" ) );

        final OutputStream outStream = provider.newOutputStream( path );
        assertThat( outStream ).isNotNull();
        outStream.write( "my cool content".getBytes() );
        outStream.close();

        final InputStream inStream = provider.newInputStream( path );

        final String content = new Scanner( inStream ).useDelimiter( "\\A" ).next();

        inStream.close();

        assertThat( content ).isNotNull().isEqualTo( "my cool content" );

        assertThat( hookExecuted.get() ).isTrue();
    }

    @Test
    public void preCommitHookNotSupported() throws IOException {
        final URI newRepo = URI.create( "git://hook-repo-name-executed-pre-commit" );

        final AtomicBoolean hookExecuted = new AtomicBoolean( false );
        final FileSystem fs = provider.newFileSystem( newRepo, EMPTY_ENV );

        provider.setDetectedFS( new FS_POSIX_Java6() {
            @Override
            public ProcessResult runIfPresent( Repository repox,
                                               Hook hook,
                                               String[] args ) throws JGitInternalException {
                if ( hook.equals( Hook.PRE_COMMIT ) ) {
                    hookExecuted.set( true );
                }
                return null;
            }
        } );

        assertThat( fs ).isNotNull();

        final Path path = provider.getPath( URI.create( "git://user_branch@hook-repo-name-executed-pre-commit/some/path/myfile.txt" ) );

        final OutputStream outStream = provider.newOutputStream( path );
        assertThat( outStream ).isNotNull();
        outStream.write( "my cool content".getBytes() );
        outStream.close();

        final InputStream inStream = provider.newInputStream( path );

        final String content = new Scanner( inStream ).useDelimiter( "\\A" ).next();

        inStream.close();

        assertThat( content ).isNotNull().isEqualTo( "my cool content" );

        assertThat( hookExecuted.get() ).isFalse();
    }
}
