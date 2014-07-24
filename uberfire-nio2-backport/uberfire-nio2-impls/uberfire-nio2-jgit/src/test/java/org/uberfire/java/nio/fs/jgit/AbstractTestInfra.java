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

import static org.uberfire.java.nio.fs.jgit.util.JGitUtil.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.util.FileUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

public abstract class AbstractTestInfra {

    protected static final Map<String, Object> EMPTY_ENV = Collections.emptyMap();

    private static final List<File> tempFiles = new ArrayList<File>();

    protected JGitFileSystemProvider provider;

    @Before
    public void createGitFsProvider() {
        provider = new JGitFileSystemProvider( Collections.<String, String>emptyMap() );
    }

    @After
    public void destroyGitFsProvider() throws IOException {
        if ( provider == null ) {
            // this would mean that setup failed. no need to clean up.
            return;
        }

        provider.shutdown();

        if ( provider.getGitRepoContainerDir() != null && provider.getGitRepoContainerDir().exists() ) {
            FileUtils.delete( provider.getGitRepoContainerDir(), FileUtils.RECURSIVE );
        }
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

    protected Git setupGit() throws IOException, GitAPIException {
        return setupGit( createTempDirectory() );
    }

    protected Git setupGit( final File tempDir ) throws IOException, GitAPIException {

        final Git git = Git.init().setBare( true ).setDirectory( tempDir ).call();

        commit( git, "master", "name", "name@example.com", "cool1", null, null, false, new HashMap<String, File>() {{
            put( "file1.txt", tempFile( "content" ) );
            put( "file2.txt", tempFile( "content2" ) );
        }} );

        return git;
    }

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

    public File tempFile( final String content ) throws IOException {
        final File file = File.createTempFile( "bar", "foo" );
        final OutputStream out = new FileOutputStream( file );

        if ( content != null && !content.isEmpty() ) {
            out.write( content.getBytes() );
            out.flush();
        }

        out.close();
        return file;
    }

    public PersonIdent getAuthor() {
        return new PersonIdent( "user", "user@example.com" );
    }

}
