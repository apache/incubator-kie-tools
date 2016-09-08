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

package org.uberfire.java.nio.fs.jgit.util;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;
import org.jboss.byteman.contrib.bmunit.BMScript;
import org.jboss.byteman.contrib.bmunit.BMUnitConfig;
import org.jboss.byteman.contrib.bmunit.BMUnitRunner;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.fs.jgit.AbstractTestInfra;

import static org.junit.Assert.*;
import static org.uberfire.java.nio.fs.jgit.util.JGitUtil.*;

@RunWith(BMUnitRunner.class)
@BMUnitConfig(loadDirectory = "target/test-classes", debug = true) // set "debug=true to see debug output
public class ConcurrentJGitUtilTest extends AbstractTestInfra {

    @BeforeClass
    public static void setup() {
        JGitUtil.setRetryTimes( 5 );
    }

    @Test
    @BMScript(value = "byteman/retry/resolve_path.btm")
    public void testRetryResolvePath() throws IOException {
        final File parentFolder = createTempDirectory();
        final File gitFolder = new File( parentFolder, "mytest.git" );

        final Git git = JGitUtil.newRepository( gitFolder, true );

        commit( git, "master", "name", "name@example.com", "1st commit", null, new Date(), false, new HashMap<String, File>() {
            {
                put( "path/to/file1.txt", tempFile( "temp2222" ) );
            }
        } );
        commit( git, "master", "name", "name@example.com", "2nd commit", null, new Date(), false, new HashMap<String, File>() {
            {
                put( "path/to/file2.txt", tempFile( "temp2222" ) );
            }
        } );

        try {
            assertNotNull( resolvePath( git, "master", "path/to/file1.txt" ) );
            assertNotNull( resolvePath( git, "master", "path/to/file1.txt" ) );
            assertNotNull( resolvePath( git, "master", "path/to/file1.txt" ) );
            assertNotNull( resolvePath( git, "master", "path/to/file1.txt" ) );
        } catch ( Exception ex ) {
            fail();
        }

        try {
            resolvePath( git, "master", "path/to/file1.txt" );
            fail( "forced to fail!" );
        } catch ( RuntimeException ex ) {
        }
    }

    @Test
    @BMScript(value = "byteman/retry/resolve_inputstream.btm")
    public void testRetryResolveInputStream() throws IOException {

        final File parentFolder = createTempDirectory();
        final File gitFolder = new File( parentFolder, "mytest.git" );

        final Git git = JGitUtil.newRepository( gitFolder, true );

        commit( git, "master", "name", "name@example.com", "1st commit", null, new Date(), false, new HashMap<String, File>() {
            {
                put( "path/to/file1.txt", tempFile( "temp2222" ) );
            }
        } );
        commit( git, "master", "name", "name@example.com", "2nd commit", null, new Date(), false, new HashMap<String, File>() {
            {
                put( "path/to/file2.txt", tempFile( "temp2222" ) );
            }
        } );

        try {
            assertNotNull( resolveInputStream( git, "master", "path/to/file1.txt" ) );
            assertNotNull( resolveInputStream( git, "master", "path/to/file1.txt" ) );
            assertNotNull( resolveInputStream( git, "master", "path/to/file1.txt" ) );
            assertNotNull( resolveInputStream( git, "master", "path/to/file1.txt" ) );
        } catch ( Exception ex ) {
            fail();
        }

        try {
            assertNotNull( resolveInputStream( git, "master", "path/to/file1.txt" ) );
            fail( "forced to fail!" );
        } catch ( NoSuchFileException ex ) {
        }
    }

    @Test
    @BMScript(value = "byteman/retry/list_path_content.btm")
    public void testRetryListPathContent() throws IOException {

        final File parentFolder = createTempDirectory();
        final File gitFolder = new File( parentFolder, "mytest.git" );

        final Git git = JGitUtil.newRepository( gitFolder, true );

        commit( git, "master", "name", "name@example.com", "1st commit", null, new Date(), false, new HashMap<String, File>() {
            {
                put( "path/to/file1.txt", tempFile( "temp2222" ) );
            }
        } );
        commit( git, "master", "name", "name@example.com", "2nd commit", null, new Date(), false, new HashMap<String, File>() {
            {
                put( "path/to/file2.txt", tempFile( "temp2222" ) );
            }
        } );

        try {
            assertNotNull( listPathContent( git, "master", "path/to/" ) );
            assertNotNull( listPathContent( git, "master", "path/to/" ) );
            assertNotNull( listPathContent( git, "master", "path/to/" ) );
            assertNotNull( listPathContent( git, "master", "path/to/" ) );
        } catch ( Exception ex ) {
            fail();
        }

        try {
            assertNotNull( listPathContent( git, "master", "path/to/" ) );
            fail( "forced to fail!" );
        } catch ( RuntimeException ex ) {
        }
    }

    @Test
    @BMScript(value = "byteman/retry/check_path.btm")
    public void testRetryCheckPath() throws IOException {

        final File parentFolder = createTempDirectory();
        final File gitFolder = new File( parentFolder, "mytest.git" );

        final Git git = JGitUtil.newRepository( gitFolder, true );

        commit( git, "master", "name", "name@example.com", "1st commit", null, new Date(), false, new HashMap<String, File>() {
            {
                put( "path/to/file1.txt", tempFile( "temp2222" ) );
            }
        } );
        commit( git, "master", "name", "name@example.com", "2nd commit", null, new Date(), false, new HashMap<String, File>() {
            {
                put( "path/to/file2.txt", tempFile( "temp2222" ) );
            }
        } );

        try {
            assertNotNull( checkPath( git, "master", "path/to/file2.txt" ) );
            assertNotNull( checkPath( git, "master", "path/to/file2.txt" ) );
            assertNotNull( checkPath( git, "master", "path/to/file2.txt" ) );
            assertNotNull( checkPath( git, "master", "path/to/file2.txt" ) );
        } catch ( Exception ex ) {
            fail();
        }

        try {
            assertNotNull( checkPath( git, "master", "path/to/file2.txt" ) );
            fail( "forced to fail!" );
        } catch ( RuntimeException ex ) {
        }
    }

    @Test
    @BMScript(value = "byteman/retry/get_last_commit.btm")
    public void testRetryGetLastCommit() throws IOException {

        final File parentFolder = createTempDirectory();
        final File gitFolder = new File( parentFolder, "mytest.git" );

        final Git git = JGitUtil.newRepository( gitFolder, true );

        commit( git, "master", "name", "name@example.com", "1st commit", null, new Date(), false, new HashMap<String, File>() {
            {
                put( "path/to/file1.txt", tempFile( "temp2222" ) );
            }
        } );
        commit( git, "master", "name", "name@example.com", "2nd commit", null, new Date(), false, new HashMap<String, File>() {
            {
                put( "path/to/file2.txt", tempFile( "temp2222" ) );
            }
        } );

        try {
            assertNotNull( getLastCommit( git, "master" ) );
            assertNotNull( getLastCommit( git, "master" ) );
            assertNotNull( getLastCommit( git, "master" ) );
            assertNotNull( getLastCommit( git, "master" ) );
        } catch ( Exception ex ) {
            fail();
        }

        try {
            assertNotNull( getLastCommit( git, "master" ) );
            fail( "forced to fail!" );
        } catch ( RuntimeException ex ) {
        }
    }

    @Test
    @BMScript(value = "byteman/retry/get_commits.btm")
    public void testRetryGetCommits() throws IOException {

        final File parentFolder = createTempDirectory();
        final File gitFolder = new File( parentFolder, "mytest.git" );

        final Git git = JGitUtil.newRepository( gitFolder, true );

        commit( git, "master", "name", "name@example.com", "1st commit", null, new Date(), false, new HashMap<String, File>() {
            {
                put( "path/to/file1.txt", tempFile( "temp2222" ) );
            }
        } );
        commit( git, "master", "name", "name@example.com", "2nd commit", null, new Date(), false, new HashMap<String, File>() {
            {
                put( "path/to/file2.txt", tempFile( "temp2222" ) );
            }
        } );

        final RevCommit commit = getLastCommit( git, "master" );
        try {
            assertNotNull( getCommits( git, "master", null, commit ) );
            assertNotNull( getCommits( git, "master", null, commit ) );
            assertNotNull( getCommits( git, "master", null, commit ) );
        } catch ( Exception ex ) {
            fail();
        }

        try {
            assertNotNull( getCommits( git, "master", null, commit ) );
            fail( "forced to fail!" );
        } catch ( RuntimeException ex ) {
        }
    }
}