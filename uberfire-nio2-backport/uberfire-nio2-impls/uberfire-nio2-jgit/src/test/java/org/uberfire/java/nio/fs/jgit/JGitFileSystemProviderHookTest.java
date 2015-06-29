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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
        gitPrefs.put("org.uberfire.nio.git.daemon.port", String.valueOf(gitDaemonPort));

        try {
            final File hooksDir = createTempDirectory();
            gitPrefs.put("org.uberfire.nio.git.hooks", hooksDir.getAbsolutePath());

            writeMockHook(hooksDir, Hook.POST_COMMIT.getName());
            writeMockHook(hooksDir, Hook.PRE_COMMIT.getName());
        } catch ( IOException e ) {
            e.printStackTrace();
        }

        return gitPrefs;
    }

    @Test
    public void testInstalledHook() {
        final URI newRepo = URI.create("git://hook-repo-name");

        final FileSystem fs = provider.newFileSystem(newRepo, EMPTY_ENV);

        assertThat( fs ).isNotNull();

        if ( fs instanceof JGitFileSystem ) {
            File[] hooks = new File( ( (JGitFileSystem) fs ).gitRepo().getRepository().getDirectory(), "hooks" ).listFiles();
            assertThat( hooks ).isNotEmpty().isNotNull();
            assertThat( hooks.length ).isEqualTo( 2 );

            boolean foundPreCommitHook = false;
            boolean foundPostCommitHook = false;
            for ( File hook : hooks ) {
                if ( hook.getName().equals( Hook.PRE_COMMIT.getName() ) ) {
                    foundPreCommitHook = hook.canExecute();
                } else if ( hook.getName().equals( Hook.POST_COMMIT.getName() ) ) {
                    foundPostCommitHook = hook.canExecute();
                }
            }
            assertThat( foundPreCommitHook ).isTrue();
            assertThat( foundPostCommitHook ).isTrue();
        }
    }

    @Test
    public void testExecutedPostCommitHook() throws IOException {
        testHook("hook-repo-name-executed", Hook.POST_COMMIT, true);
    }

    @Test
    public void testNotSupportedPreCommitHook() throws IOException {
        testHook("hook-repo-name-executed-pre-commit", Hook.PRE_COMMIT, false);
    }

    /**
     * Tests if defined hook was executed or not.
     * @param gitRepoName Name of test git repository that is created for commiting changes.
     * @param testedHook Tested hook. This hook is checked for its execution.
     * @param wasExecuted Expected hook execution state. If true, test expects that defined hook is executed.
     *                    If false, test expects that defined hook is not executed.
     * @throws IOException
     */
    private void testHook(final String gitRepoName, final Hook testedHook, final boolean wasExecuted) throws IOException {
        final URI newRepo = URI.create( "git://" + gitRepoName );

        final AtomicBoolean hookExecuted = new AtomicBoolean( false );
        final FileSystem fs = provider.newFileSystem( newRepo, EMPTY_ENV );

        provider.setDetectedFS( new FS_POSIX_Java6() {
            @Override
            public ProcessResult runIfPresent( Repository repox,
                                               Hook hook,
                                               String[] args ) throws JGitInternalException {
                if ( hook.equals( testedHook ) ) {
                    hookExecuted.set( true );
                }
                return null;
            }
        } );

        assertThat( fs ).isNotNull();

        final Path path = provider.getPath( URI.create( "git://user_branch@" + gitRepoName + "/some/path/myfile.txt" ) );

        final OutputStream outStream = provider.newOutputStream( path );
        assertThat( outStream ).isNotNull();
        outStream.write( "my cool content".getBytes() );
        outStream.close();

        final InputStream inStream = provider.newInputStream( path );

        final String content = new Scanner( inStream ).useDelimiter( "\\A" ).next();

        inStream.close();

        assertThat( content ).isNotNull().isEqualTo( "my cool content" );

        if (wasExecuted) {
            assertThat( hookExecuted.get() ).isTrue();
        } else {
            assertThat( hookExecuted.get() ).isFalse();
        }
    }

    /**
     * Creates mock hook in defined hooks directory.
     * @param hooksDirectory Directory in which mock hook is created.
     * @param hookName Name of the created hook. This is the filename of created hook file.
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    private void writeMockHook(final File hooksDirectory, final String hookName)
            throws FileNotFoundException, UnsupportedEncodingException {
        final PrintWriter writer = new PrintWriter( new File( hooksDirectory, hookName ), "UTF-8" );
        writer.println( "# something" );
        writer.close();
    }
}
