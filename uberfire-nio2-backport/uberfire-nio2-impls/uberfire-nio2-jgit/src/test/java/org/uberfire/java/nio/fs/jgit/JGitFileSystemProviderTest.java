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
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.junit.Ignore;
import org.junit.Test;
import org.uberfire.commons.data.Pair;
import org.uberfire.java.nio.base.NotImplementedException;
import org.uberfire.java.nio.base.options.CommentedOption;
import org.uberfire.java.nio.file.DirectoryNotEmptyException;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.FileStore;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.FileSystemNotFoundException;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.NotDirectoryException;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.StandardWatchEventKind;
import org.uberfire.java.nio.file.WatchEvent;
import org.uberfire.java.nio.file.WatchKey;
import org.uberfire.java.nio.file.WatchService;
import org.uberfire.java.nio.file.attribute.BasicFileAttributeView;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.java.nio.file.attribute.FileTime;
import org.uberfire.java.nio.fs.jgit.util.JGitUtil;
import org.uberfire.java.nio.fs.jgit.util.JGitUtil.*;

import static org.fest.assertions.api.Assertions.*;
import static org.uberfire.java.nio.file.StandardDeleteOption.*;
import static org.uberfire.java.nio.fs.jgit.util.JGitUtil.*;

public class JGitFileSystemProviderTest extends AbstractTestInfra {

    private int gitDaemonPort;

    @Override
    public Map<String, String> getGitPreferences() {
        Map<String, String> gitPrefs = super.getGitPreferences();
        gitPrefs.put( "org.uberfire.nio.git.daemon.enabled", "true" );
        gitDaemonPort = findFreePort();
        gitPrefs.put( "org.uberfire.nio.git.daemon.port", String.valueOf( gitDaemonPort ) );
        return gitPrefs;
    }

    @Test
    @Ignore
    public void testDaemob() throws InterruptedException {
        final URI newRepo = URI.create( "git://repo-name" );

        final Map<String, ?> env = new HashMap<String, Object>() {{
            put( "init", Boolean.TRUE );
        }};

        FileSystem fs = provider.newFileSystem( newRepo, env );

        WatchService ws = null;
        ws = fs.newWatchService();
        final Path path = fs.getRootDirectories().iterator().next();
        path.register( ws, StandardWatchEventKind.ENTRY_CREATE, StandardWatchEventKind.ENTRY_MODIFY, StandardWatchEventKind.ENTRY_DELETE, StandardWatchEventKind.ENTRY_RENAME );

        final WatchKey k = ws.take();

        final List<WatchEvent<?>> events = k.pollEvents();
        for ( WatchEvent object : events ) {
            if ( object.kind() == StandardWatchEventKind.ENTRY_MODIFY ) {
                System.out.println( "Modify: " + object.context().toString() );
            }
            if ( object.kind() == StandardWatchEventKind.ENTRY_RENAME ) {
                System.out.println( "Rename: " + object.context().toString() );
            }
            if ( object.kind() == StandardWatchEventKind.ENTRY_DELETE ) {
                System.out.println( "Delete: " + object.context().toString() );
            }
            if ( object.kind() == StandardWatchEventKind.ENTRY_CREATE ) {
                System.out.println( "Created: " + object.context().toString() );
            }
        }
    }

    @Test
    public void testNewFileSystem() {
        final URI newRepo = URI.create( "git://repo-name" );

        final FileSystem fs = provider.newFileSystem( newRepo, EMPTY_ENV );

        assertThat( fs ).isNotNull();

        final DirectoryStream<Path> stream = provider.newDirectoryStream( provider.getPath( newRepo ), null );
        assertThat( stream ).isNotNull().hasSize( 0 );

        try {
            provider.newFileSystem( newRepo, EMPTY_ENV );
            failBecauseExceptionWasNotThrown( FileSystemAlreadyExistsException.class );
        } catch ( final Exception ex ) {
        }

        provider.newFileSystem( URI.create( "git://repo-name2" ), EMPTY_ENV );
    }

    @Test
    public void testNewFileSystemInited() {
        final URI newRepo = URI.create( "git://init-repo-name" );

        final Map<String, ?> env = new HashMap<String, Object>() {{
            put( "init", Boolean.TRUE );
        }};

        final FileSystem fs = provider.newFileSystem( newRepo, env );

        assertThat( fs ).isNotNull();

        final DirectoryStream<Path> stream = provider.newDirectoryStream( provider.getPath( newRepo ), null );
        assertThat( stream ).isNotNull().hasSize( 1 );
    }

    @Test
    public void testInvalidURINewFileSystem() {
        final URI newRepo = URI.create( "git:///repo-name" );

        try {
            provider.newFileSystem( newRepo, EMPTY_ENV );
            failBecauseExceptionWasNotThrown( IllegalArgumentException.class );
        } catch ( final IllegalArgumentException ex ) {
            assertThat( ex.getMessage() ).isEqualTo( "Parameter named 'uri' is invalid, missing host repository!" );
        }
    }

    @Test
    public void testNewFileSystemClone() throws IOException {

        final URI originRepo = URI.create( "git://my-simple-test-origin-name" );

        final JGitFileSystem origin = (JGitFileSystem) provider.newFileSystem( originRepo, new HashMap<String, Object>() {{
            put( "listMode", "ALL" );
        }} );

        commit( origin.gitRepo(), "master", "user1", "user1@example.com", "commitx", null, null, false, new HashMap<String, File>() {{
            put( "file.txt", tempFile( "temp" ) );
        }} );

        final URI newRepo = URI.create( "git://my-repo-name" );

        final Map<String, Object> env = new HashMap<String, Object>() {{
            put( JGitFileSystemProvider.GIT_ENV_KEY_DEFAULT_REMOTE_NAME, "git://localhost:" + gitDaemonPort + "/my-simple-test-origin-name" );
            put( "listMode", "ALL" );
        }};

        final FileSystem fs = provider.newFileSystem( newRepo, env );

        assertThat( fs ).isNotNull();

        assertThat( fs.getRootDirectories() ).hasSize( 2 );

        assertThat( fs.getPath( "file.txt" ).toFile() ).isNotNull().exists();

        commit( origin.gitRepo(), "master", "user1", "user1@example.com", "commitx", null, null, false, new HashMap<String, File>() {{
            put( "fileXXXXX.txt", tempFile( "temp" ) );
        }} );

        provider.getFileSystem( URI.create( "git://my-repo-name?sync=git://localhost:" + gitDaemonPort + "/my-simple-test-origin-name&force" ) );

        assertThat( fs ).isNotNull();

        assertThat( fs.getRootDirectories() ).hasSize( 3 );

        for ( final Path root : fs.getRootDirectories() ) {
            if ( root.toAbsolutePath().toUri().toString().contains( "upstream" ) ) {
                assertThat( provider.newDirectoryStream( root, null ) ).isNotEmpty().hasSize( 2 );
            } else if ( root.toAbsolutePath().toUri().toString().contains( "origin" ) ) {
                assertThat( provider.newDirectoryStream( root, null ) ).isNotEmpty().hasSize( 1 );
            } else {
                assertThat( provider.newDirectoryStream( root, null ) ).isNotEmpty().hasSize( 2 );
            }
        }

        commit( origin.gitRepo(), "master", "user1", "user1@example.com", "commitx", null, null, false, new HashMap<String, File>() {{
            put( "fileYYYY.txt", tempFile( "tempYYYY" ) );
        }} );

        provider.getFileSystem( URI.create( "git://my-repo-name?sync=git://localhost:" + gitDaemonPort + "/my-simple-test-origin-name&force" ) );

        assertThat( fs.getRootDirectories() ).hasSize( 3 );

        for ( final Path root : fs.getRootDirectories() ) {
            if ( root.toAbsolutePath().toUri().toString().contains( "upstream" ) ) {
                assertThat( provider.newDirectoryStream( root, null ) ).isNotEmpty().hasSize( 3 );
            } else if ( root.toAbsolutePath().toUri().toString().contains( "origin" ) ) {
                assertThat( provider.newDirectoryStream( root, null ) ).isNotEmpty().hasSize( 1 );
            } else {
                assertThat( provider.newDirectoryStream( root, null ) ).isNotEmpty().hasSize( 3 );
            }
        }
    }

    @Test
    public void testNewFileSystemCloneAndPush() throws IOException {

        final URI originRepo = URI.create( "git://my-simple-test-origin-repo" );

        final JGitFileSystem origin = (JGitFileSystem) provider.newFileSystem( originRepo, new HashMap<String, Object>() {{
            put( "listMode", "ALL" );
        }} );

        commit( origin.gitRepo(), "master", "user1", "user1@example.com", "commitx", null, null, false, new HashMap<String, File>() {{
            put( "file.txt", tempFile( "temp" ) );
        }} );

        final URI newRepo = URI.create( "git://my-repo" );

        final Map<String, Object> env = new HashMap<String, Object>() {{
            put( JGitFileSystemProvider.GIT_ENV_KEY_DEFAULT_REMOTE_NAME, "git://localhost:" + gitDaemonPort + "/my-simple-test-origin-repo" );
            put( "listMode", "ALL" );
        }};

        final FileSystem fs = provider.newFileSystem( newRepo, env );

        assertThat( fs ).isNotNull();

        assertThat( fs.getRootDirectories() ).hasSize( 2 );

        assertThat( fs.getPath( "file.txt" ).toFile() ).isNotNull().exists();

        commit( ( (JGitFileSystem) fs ).gitRepo(), "master", "user1", "user1@example.com", "commitx", null, null, false, new HashMap<String, File>() {{
            put( "fileXXXXX.txt", tempFile( "temp" ) );
        }} );

        final URI newRepo2 = URI.create( "git://my-repo2" );

        final Map<String, Object> env2 = new HashMap<String, Object>() {{
            put( JGitFileSystemProvider.GIT_ENV_KEY_DEFAULT_REMOTE_NAME, "git://localhost:" + gitDaemonPort + "/my-simple-test-origin-repo" );
            put( "listMode", "ALL" );
        }};

        final FileSystem fs2 = provider.newFileSystem( newRepo2, env2 );

        commit( origin.gitRepo(), "user-branch", "user1", "user1@example.com", "commitx", null, null, false, new HashMap<String, File>() {{
            put( "file1UserBranch.txt", tempFile( "tempX" ) );
        }} );

        provider.getFileSystem( URI.create( "git://my-repo2?sync=git://localhost:" + gitDaemonPort + "/my-simple-test-origin-repo&force" ) );

        assertThat( fs2.getRootDirectories() ).hasSize( 5 );

        final List<String> rootURIs1 = new ArrayList<String>() {{
            add( "git://master@my-repo2/" );
            add( "git://user-branch@my-repo2/" );
            add( "git://origin/master@my-repo2/" );
            add( "git://upstream/master@my-repo2/" );
            add( "git://upstream/user-branch@my-repo2/" );
        }};

        final List<String> rootURIs2 = new ArrayList<String>() {{
            add( "git://master@my-repo2/" );
            add( "git://user-branch@my-repo2/" );
            add( "git://user-branch-2@my-repo2/" );
            add( "git://origin/master@my-repo2/" );
            add( "git://upstream/master@my-repo2/" );
            add( "git://upstream/user-branch@my-repo2/" );
            add( "git://upstream/user-branch-2@my-repo2/" );
        }};

        final Set<String> rootURIs = new HashSet<String>();
        for ( final Path root : fs2.getRootDirectories() ) {
            rootURIs.add( root.toUri().toString() );
        }

        rootURIs.removeAll( rootURIs1 );

        assertThat( rootURIs ).isEmpty();

        commit( origin.gitRepo(), "user-branch-2", "user1", "user1@example.com", "commitx", null, null, false, new HashMap<String, File>() {{
            put( "file2UserBranch.txt", tempFile( "tempX" ) );
        }} );

        provider.getFileSystem( URI.create( "git://my-repo2?sync=git://localhost:" + gitDaemonPort + "/my-simple-test-origin-repo&force" ) );

        assertThat( fs2.getRootDirectories() ).hasSize( 7 );

        for ( final Path root : fs2.getRootDirectories() ) {
            rootURIs.add( root.toUri().toString() );
        }

        rootURIs.removeAll( rootURIs2 );

        assertThat( rootURIs ).isEmpty();
    }

    @Test
    public void testNewFileSystemCloneAndRescan() throws IOException {

        final URI originRepo = URI.create( "git://my-simple-test-origin-name" );

        final JGitFileSystem origin = (JGitFileSystem) provider.newFileSystem( originRepo, new HashMap<String, Object>() {{
            put( "listMode", "ALL" );
        }} );

        commit( origin.gitRepo(), "master", "user1", "user1@example.com", "commitx", null, null, false, new HashMap<String, File>() {{
            put( "file.txt", tempFile( "temp" ) );
        }} );

        final URI newRepo = URI.create( "git://my-repo-name" );

        final Map<String, Object> env = new HashMap<String, Object>() {{
            put( JGitFileSystemProvider.GIT_ENV_KEY_DEFAULT_REMOTE_NAME, "git://localhost:" + gitDaemonPort + "/my-simple-test-origin-name" );
        }};

        final FileSystem fs = provider.newFileSystem( newRepo, env );

        assertThat( fs ).isNotNull();

        assertThat( fs.getRootDirectories() ).hasSize( 1 );

        provider.rescanForExistingRepositories();

        final FileSystem fs2 = provider.getFileSystem( newRepo );

        assertThat( fs2 ).isNotNull();

        assertThat( fs2.getRootDirectories() ).hasSize( 1 );
    }

    @Test
    public void testGetFileSystem() {
        final URI newRepo = URI.create( "git://new-repo-name" );

        final FileSystem fs = provider.newFileSystem( newRepo, EMPTY_ENV );

        assertThat( fs ).isNotNull();

        assertThat( provider.getFileSystem( newRepo ) ).isEqualTo( fs );
        assertThat( provider.getFileSystem( URI.create( "git://master@new-repo-name" ) ) ).isEqualTo( fs );
        assertThat( provider.getFileSystem( URI.create( "git://branch@new-repo-name" ) ) ).isEqualTo( fs );

        assertThat( provider.getFileSystem( URI.create( "git://branch@new-repo-name?fetch" ) ) ).isEqualTo( fs );
    }

    @Test
    public void testInvalidURIGetFileSystem() {
        final URI newRepo = URI.create( "git:///new-repo-name" );

        try {
            provider.getFileSystem( newRepo );
            failBecauseExceptionWasNotThrown( IllegalArgumentException.class );
        } catch ( final IllegalArgumentException ex ) {
            assertThat( ex.getMessage() ).isEqualTo( "Parameter named 'uri' is invalid, missing host repository!" );
        }

    }

    @Test
    public void testGetPath() {
        final URI newRepo = URI.create( "git://new-get-repo-name" );

        provider.newFileSystem( newRepo, EMPTY_ENV );

        final Path path = provider.getPath( URI.create( "git://master@new-get-repo-name/home" ) );

        assertThat( path ).isNotNull();
        assertThat( path.getRoot().toString() ).isEqualTo( "/" );
        assertThat( path.getRoot().toRealPath().toUri().toString() ).isEqualTo( "git://master@new-get-repo-name/" );
        assertThat( path.toString() ).isEqualTo( "/home" );

        final Path pathRelative = provider.getPath( URI.create( "git://master@new-get-repo-name/:home" ) );
        assertThat( pathRelative ).isNotNull();
        assertThat( pathRelative.toRealPath().toUri().toString() ).isEqualTo( "git://master@new-get-repo-name/:home" );
        assertThat( pathRelative.getRoot().toString() ).isEqualTo( "" );
        assertThat( pathRelative.toString() ).isEqualTo( "home" );
    }

    @Test
    public void testInvalidURIGetPath() {
        final URI uri = URI.create( "git:///master@new-get-repo-name/home" );

        try {
            provider.getPath( uri );
            failBecauseExceptionWasNotThrown( IllegalArgumentException.class );
        } catch ( final IllegalArgumentException ex ) {
            assertThat( ex.getMessage() ).isEqualTo( "Parameter named 'uri' is invalid, missing host repository!" );
        }
    }

    @Test
    public void testGetComplexPath() {
        final URI newRepo = URI.create( "git://new-complex-get-repo-name" );

        provider.newFileSystem( newRepo, EMPTY_ENV );

        final Path path = provider.getPath( URI.create( "git://origin/master@new-complex-get-repo-name/home" ) );

        assertThat( path ).isNotNull();
        assertThat( path.getRoot().toString() ).isEqualTo( "/" );
        assertThat( path.toString() ).isEqualTo( "/home" );

        final Path pathRelative = provider.getPath( URI.create( "git://origin/master@new-complex-get-repo-name/:home" ) );
        assertThat( pathRelative ).isNotNull();
        assertThat( pathRelative.getRoot().toString() ).isEqualTo( "" );
        assertThat( pathRelative.toString() ).isEqualTo( "home" );
    }

    @Test
    public void testInputStream() throws IOException {
        final File parentFolder = createTempDirectory();
        final File gitFolder = new File( parentFolder, "mytest.git" );

        final Git origin = JGitUtil.newRepository( gitFolder, true );

        commit( origin, "master", "user", "user@example.com", "commit message", null, null, false, new HashMap<String, File>() {{
            put( "myfile.txt", tempFile( "temp\n.origin\n.content" ) );
        }} );

        final URI newRepo = URI.create( "git://inputstream-test-repo" );

        final Map<String, Object> env = new HashMap<String, Object>() {{
            put( JGitFileSystemProvider.GIT_ENV_KEY_DEFAULT_REMOTE_NAME, origin.getRepository().getDirectory().toString() );
        }};

        final FileSystem fs = provider.newFileSystem( newRepo, env );

        assertThat( fs ).isNotNull();

        final Path path = provider.getPath( URI.create( "git://origin/master@inputstream-test-repo/myfile.txt" ) );

        final InputStream inputStream = provider.newInputStream( path );
        assertThat( inputStream ).isNotNull();

        final String content = new Scanner( inputStream ).useDelimiter( "\\A" ).next();

        inputStream.close();

        assertThat( content ).isNotNull().isEqualTo( "temp\n.origin\n.content" );
    }

    @Test
    public void testInputStream2() throws IOException {

        final File parentFolder = createTempDirectory();
        final File gitFolder = new File( parentFolder, "mytest.git" );

        final Git origin = JGitUtil.newRepository( gitFolder, true );

        commit( origin, "master", "user", "user@example.com", "commit message", null, null, false, new HashMap<String, File>() {{
            put( "path/to/file/myfile.txt", tempFile( "temp\n.origin\n.content" ) );
        }} );

        final URI newRepo = URI.create( "git://xinputstream-test-repo" );

        final Map<String, Object> env = new HashMap<String, Object>() {{
            put( JGitFileSystemProvider.GIT_ENV_KEY_DEFAULT_REMOTE_NAME, origin.getRepository().getDirectory().toString() );
        }};

        final FileSystem fs = provider.newFileSystem( newRepo, env );

        assertThat( fs ).isNotNull();

        final Path path = provider.getPath( URI.create( "git://origin/master@xinputstream-test-repo/path/to/file/myfile.txt" ) );

        final InputStream inputStream = provider.newInputStream( path );
        assertThat( inputStream ).isNotNull();

        final String content = new Scanner( inputStream ).useDelimiter( "\\A" ).next();

        inputStream.close();

        assertThat( content ).isNotNull().isEqualTo( "temp\n.origin\n.content" );
    }

    @Test(expected = NoSuchFileException.class)
    public void testInputStream3() throws IOException {

        final File parentFolder = createTempDirectory();
        final File gitFolder = new File( parentFolder, "mytest.git" );

        final Git origin = JGitUtil.newRepository( gitFolder, true );

        commit( origin, "master", "user", "user@example.com", "commit message", null, null, false, new HashMap<String, File>() {{
            put( "path/to/file/myfile.txt", tempFile( "temp\n.origin\n.content" ) );
        }} );

        final URI newRepo = URI.create( "git://xxinputstream-test-repo" );

        final Map<String, Object> env = new HashMap<String, Object>() {{
            put( JGitFileSystemProvider.GIT_ENV_KEY_DEFAULT_REMOTE_NAME, origin.getRepository().getDirectory().toString() );
        }};

        final FileSystem fs = provider.newFileSystem( newRepo, env );

        assertThat( fs ).isNotNull();

        final Path path = provider.getPath( URI.create( "git://origin/master@xxinputstream-test-repo/path/to" ) );

        provider.newInputStream( path );
    }

    @Test(expected = NoSuchFileException.class)
    public void testInputStreamNoSuchFile() throws IOException {

        final File parentFolder = createTempDirectory();
        final File gitFolder = new File( parentFolder, "mytest.git" );

        final Git origin = JGitUtil.newRepository( gitFolder, true );

        commit( origin, "master", "user1", "user1@example.com", "commitx", null, null, false, new HashMap<String, File>() {{
            put( "file.txt", tempFile( "temp.origin.content.2" ) );
        }} );

        final URI newRepo = URI.create( "git://inputstream-not-exists-test-repo" );

        final Map<String, Object> env = new HashMap<String, Object>() {{
            put( JGitFileSystemProvider.GIT_ENV_KEY_DEFAULT_REMOTE_NAME, origin.getRepository().getDirectory().toString() );
        }};

        final FileSystem fs = provider.newFileSystem( newRepo, env );

        assertThat( fs ).isNotNull();

        final Path path = provider.getPath( URI.create( "git://origin/master@inputstream-not-exists-test-repo/temp.txt" ) );

        provider.newInputStream( path );
    }

    @Test
    public void testNewOutputStream() throws Exception {
        final File parentFolder = createTempDirectory();
        final File gitFolder = new File( parentFolder, "mytest.git" );

        final Git origin = JGitUtil.newRepository( gitFolder, true );

        commit( origin, "master", "user", "user@example.com", "commit message", null, null, false, new HashMap<String, File>() {{
            put( "myfile.txt", tempFile( "temp\n.origin\n.content" ) );
        }} );
        commit( origin, "user_branch", "user", "user@example.com", "commit message", null, null, false, new HashMap<String, File>() {{
            put( "path/to/some/file/myfile.txt", tempFile( "some\n.content\nhere" ) );
        }} );

        final URI newRepo = URI.create( "git://outstream-test-repo" );

        final Map<String, Object> env = new HashMap<String, Object>() {{
            put( JGitFileSystemProvider.GIT_ENV_KEY_DEFAULT_REMOTE_NAME, origin.getRepository().getDirectory().toString() );
        }};

        final FileSystem fs = provider.newFileSystem( newRepo, env );

        assertThat( fs ).isNotNull();

        final Path path = provider.getPath( URI.create( "git://user_branch@outstream-test-repo/some/path/myfile.txt" ) );

        final OutputStream outStream = provider.newOutputStream( path );
        assertThat( outStream ).isNotNull();
        outStream.write( "my cool content".getBytes() );
        outStream.close();

        final InputStream inStream = provider.newInputStream( path );

        final String content = new Scanner( inStream ).useDelimiter( "\\A" ).next();

        inStream.close();

        assertThat( content ).isNotNull().isEqualTo( "my cool content" );

        try {
            provider.newOutputStream( provider.getPath( URI.create( "git://user_branch@outstream-test-repo/some/path/" ) ) );
            failBecauseExceptionWasNotThrown( org.uberfire.java.nio.IOException.class );
        } catch ( Exception e ) {
        }
    }

    @Test
    public void testNewOutputStreamWithJGitOp() throws Exception {
        final File parentFolder = createTempDirectory();
        final File gitFolder = new File( parentFolder, "mytest.git" );

        final Git origin = JGitUtil.newRepository( gitFolder, true );

        commit( origin, "master", "user", "user@example.com", "commit message", null, null, false, new HashMap<String, File>() {{
            put( "myfile.txt", tempFile( "temp\n.origin\n.content" ) );
        }} );
        commit( origin, "user_branch", "user", "user@example.com", "commit message", null, null, false, new HashMap<String, File>() {{
            put( "path/to/some/file/myfile.txt", tempFile( "some\n.content\nhere" ) );
        }} );

        final URI newRepo = URI.create( "git://outstreamwithop-test-repo" );

        final Map<String, Object> env = new HashMap<String, Object>() {{
            put( JGitFileSystemProvider.GIT_ENV_KEY_DEFAULT_REMOTE_NAME, origin.getRepository().getDirectory().toString() );
        }};

        final FileSystem fs = provider.newFileSystem( newRepo, env );

        assertThat( fs ).isNotNull();

        final SimpleDateFormat formatter = new SimpleDateFormat( "dd/MM/yyyy" );

        final CommentedOption op = new CommentedOption( "User Tester", "user.tester@example.com", "omg, is it the end?", formatter.parse( "31/12/2012" ) );

        final Path path = provider.getPath( URI.create( "git://user_branch@outstreamwithop-test-repo/some/path/myfile.txt" ) );

        final OutputStream outStream = provider.newOutputStream( path, op );
        assertThat( outStream ).isNotNull();
        outStream.write( "my cool content".getBytes() );
        outStream.close();

        final InputStream inStream = provider.newInputStream( path );

        final String content = new Scanner( inStream ).useDelimiter( "\\A" ).next();

        inStream.close();

        assertThat( content ).isNotNull().isEqualTo( "my cool content" );
    }

    @Test(expected = FileSystemNotFoundException.class)
    public void testGetPathFileSystemNotExisting() {
        provider.getPath( URI.create( "git://master@not-exists-get-repo-name/home" ) );
    }

    @Test(expected = FileSystemNotFoundException.class)
    public void testGetFileSystemNotExisting() {
        final URI newRepo = URI.create( "git://not-new-repo-name" );

        provider.getFileSystem( newRepo );
    }

    @Test
    public void testDelete() throws IOException {
        final URI newRepo = URI.create( "git://delete1-test-repo" );
        provider.newFileSystem( newRepo, EMPTY_ENV );

        final Path path = provider.getPath( URI.create( "git://user_branch@delete1-test-repo/path/to/myfile.txt" ) );

        final OutputStream outStream = provider.newOutputStream( path );
        assertThat( outStream ).isNotNull();
        outStream.write( "my cool content".getBytes() );
        outStream.close();

        provider.newInputStream( path ).close();

        try {
            provider.delete( provider.getPath( URI.create( "git://user_branch@delete1-test-repo/non_existent_path" ) ) );
            failBecauseExceptionWasNotThrown( NoSuchFileException.class );
        } catch ( NoSuchFileException ex ) {
        }

        try {
            provider.delete( provider.getPath( URI.create( "git://user_branch@delete1-test-repo/path/to/" ) ) );
            failBecauseExceptionWasNotThrown( DirectoryNotEmptyException.class );
        } catch ( DirectoryNotEmptyException ex ) {
        }

        provider.delete( path );

        try {
            provider.newFileSystem( newRepo, EMPTY_ENV );
            failBecauseExceptionWasNotThrown( FileSystemAlreadyExistsException.class );
        } catch ( FileSystemAlreadyExistsException e ) {
        }

        final Path fsPath = path.getFileSystem().getPath( null );
        provider.delete( fsPath );
        assertThat( fsPath.getFileSystem().isOpen() ).isEqualTo( false );

        final URI newRepo2 = URI.create( "git://delete1-test-repo" );
        provider.newFileSystem( newRepo2, EMPTY_ENV );
    }

    @Test
    public void testDeleteBranch() throws IOException {
        final URI newRepo = URI.create( "git://delete-branch-test-repo" );
        provider.newFileSystem( newRepo, EMPTY_ENV );

        final Path path = provider.getPath( URI.create( "git://user_branch@delete-branch-test-repo/path/to/myfile.txt" ) );

        final OutputStream outStream = provider.newOutputStream( path );
        assertThat( outStream ).isNotNull();
        outStream.write( "my cool content".getBytes() );
        outStream.close();

        provider.newInputStream( path ).close();

        provider.delete( provider.getPath( URI.create( "git://user_branch@delete-branch-test-repo" ) ) );

        try {
            provider.delete( provider.getPath( URI.create( "git://user_branch@delete-branch-test-repo" ) ) );
            failBecauseExceptionWasNotThrown( NoSuchFileException.class );
        } catch ( NoSuchFileException ex ) {
        }

        try {
            provider.delete( provider.getPath( URI.create( "git://some_user_branch@delete-branch-test-repo" ) ) );
            failBecauseExceptionWasNotThrown( NoSuchFileException.class );
        } catch ( NoSuchFileException ex ) {
        }
    }

    @Test
    public void testDeleteIfExists() throws IOException {
        final URI newRepo = URI.create( "git://deleteifexists1-test-repo" );
        provider.newFileSystem( newRepo, EMPTY_ENV );

        final Path path = provider.getPath( URI.create( "git://user_branch@deleteifexists1-test-repo/path/to/myfile.txt" ) );

        final OutputStream outStream = provider.newOutputStream( path );
        assertThat( outStream ).isNotNull();
        outStream.write( "my cool content".getBytes() );
        outStream.close();

        provider.newInputStream( path ).close();

        assertThat( provider.deleteIfExists( provider.getPath( URI.create( "git://user_branch@deleteifexists1-test-repo/non_existent_path" ) ) ) ).isFalse();

        try {
            provider.deleteIfExists( provider.getPath( URI.create( "git://user_branch@deleteifexists1-test-repo/path/to/" ) ) );
            failBecauseExceptionWasNotThrown( DirectoryNotEmptyException.class );
        } catch ( DirectoryNotEmptyException ex ) {
        }

        assertThat( provider.deleteIfExists( path ) ).isTrue();
    }

    @Test
    public void testDeleteBranchIfExists() throws IOException {
        final URI newRepo = URI.create( "git://deletebranchifexists1-test-repo" );
        provider.newFileSystem( newRepo, EMPTY_ENV );

        final Path path = provider.getPath( URI.create( "git://user_branch@deletebranchifexists1-test-repo/path/to/myfile.txt" ) );

        final OutputStream outStream = provider.newOutputStream( path );
        assertThat( outStream ).isNotNull();
        outStream.write( "my cool content".getBytes() );
        outStream.close();

        provider.newInputStream( path ).close();

        assertThat( provider.deleteIfExists( provider.getPath( URI.create( "git://user_branch@deletebranchifexists1-test-repo" ) ) ) ).isTrue();

        assertThat( provider.deleteIfExists( provider.getPath( URI.create( "git://not_user_branch@deletebranchifexists1-test-repo" ) ) ) ).isFalse();

        assertThat( provider.deleteIfExists( provider.getPath( URI.create( "git://user_branch@deletebranchifexists1-test-repo" ) ) ) ).isFalse();

    }

    @Test
    public void testIsHidden() throws IOException {
        final URI newRepo = URI.create( "git://ishidden-test-repo" );
        provider.newFileSystem( newRepo, EMPTY_ENV );

        final Path path = provider.getPath( URI.create( "git://user_branch@ishidden-test-repo/path/to/.myfile.txt" ) );

        final OutputStream outStream = provider.newOutputStream( path );
        assertThat( outStream ).isNotNull();
        outStream.write( "my cool content".getBytes() );
        outStream.close();

        final Path path2 = provider.getPath( URI.create( "git://user_branch@ishidden-test-repo/path/to/myfile.txt" ) );

        final OutputStream outStream2 = provider.newOutputStream( path2 );
        assertThat( outStream2 ).isNotNull();
        outStream2.write( "my cool content".getBytes() );
        outStream2.close();

        assertThat( provider.isHidden( provider.getPath( URI.create( "git://user_branch@ishidden-test-repo/path/to/.myfile.txt" ) ) ) ).isTrue();

        assertThat( provider.isHidden( provider.getPath( URI.create( "git://user_branch@ishidden-test-repo/path/to/myfile.txt" ) ) ) ).isFalse();

        assertThat( provider.isHidden( provider.getPath( URI.create( "git://user_branch@ishidden-test-repo/path/to/non_existent/.myfile.txt" ) ) ) ).isTrue();

        assertThat( provider.isHidden( provider.getPath( URI.create( "git://user_branch@ishidden-test-repo/path/to/non_existent/myfile.txt" ) ) ) ).isFalse();

        assertThat( provider.isHidden( provider.getPath( URI.create( "git://user_branch@ishidden-test-repo/" ) ) ) ).isFalse();

        assertThat( provider.isHidden( provider.getPath( URI.create( "git://user_branch@ishidden-test-repo/some" ) ) ) ).isFalse();
    }

    @Test
    public void testIsSameFile() throws IOException {
        final URI newRepo = URI.create( "git://issamefile-test-repo" );
        provider.newFileSystem( newRepo, EMPTY_ENV );

        final Path path = provider.getPath( URI.create( "git://master@issamefile-test-repo/path/to/myfile1.txt" ) );

        final OutputStream outStream = provider.newOutputStream( path );
        outStream.write( "my cool content".getBytes() );
        outStream.close();

        final Path path2 = provider.getPath( URI.create( "git://user_branch@issamefile-test-repo/path/to/myfile2.txt" ) );

        final OutputStream outStream2 = provider.newOutputStream( path2 );
        outStream2.write( "my cool content".getBytes() );
        outStream2.close();

        final Path path3 = provider.getPath( URI.create( "git://user_branch@issamefile-test-repo/path/to/myfile3.txt" ) );

        final OutputStream outStream3 = provider.newOutputStream( path3 );
        outStream3.write( "my cool content".getBytes() );
        outStream3.close();

        assertThat( provider.isSameFile( path, path2 ) ).isTrue();

        assertThat( provider.isSameFile( path, path3 ) ).isTrue();
    }

    @Test
    public void testCreateDirectory() throws Exception {
        final URI newRepo = URI.create( "git://xcreatedir-test-repo" );
        provider.newFileSystem( newRepo, EMPTY_ENV );

        final JGitPathImpl path = (JGitPathImpl) provider.getPath( URI.create( "git://master@xcreatedir-test-repo/some/path/to/" ) );

        final Pair<PathType, ObjectId> result = JGitUtil.checkPath( path.getFileSystem().gitRepo(), path.getRefTree(), path.getPath() );
        assertThat( result.getK1() ).isEqualTo( PathType.NOT_FOUND );

        provider.createDirectory( path );

        final Pair<PathType, ObjectId> resultAfter = JGitUtil.checkPath( path.getFileSystem().gitRepo(), path.getRefTree(), path.getPath() );
        assertThat( resultAfter.getK1() ).isEqualTo( PathType.DIRECTORY );

        try {
            provider.createDirectory( path );
            failBecauseExceptionWasNotThrown( FileAlreadyExistsException.class );
        } catch ( FileAlreadyExistsException e ) {
        }
    }

    @Test
    public void testCheckAccess() throws Exception {
        final URI newRepo = URI.create( "git://checkaccess-test-repo" );
        provider.newFileSystem( newRepo, EMPTY_ENV );

        final Path path = provider.getPath( URI.create( "git://master@checkaccess-test-repo/path/to/myfile1.txt" ) );

        final OutputStream outStream = provider.newOutputStream( path );
        outStream.write( "my cool content".getBytes() );
        outStream.close();

        provider.checkAccess( path );

        final Path path_to_dir = provider.getPath( URI.create( "git://master@checkaccess-test-repo/path/to" ) );

        provider.checkAccess( path_to_dir );

        final Path path_not_exists = provider.getPath( URI.create( "git://master@checkaccess-test-repo/path/to/some.txt" ) );

        try {
            provider.checkAccess( path_not_exists );
            failBecauseExceptionWasNotThrown( NoSuchFileException.class );
        } catch ( NoSuchFileException e ) {
        }
    }

    @Test
    public void testGetFileStore() throws Exception {
        final URI newRepo = URI.create( "git://filestore-test-repo" );
        provider.newFileSystem( newRepo, EMPTY_ENV );

        final Path path = provider.getPath( URI.create( "git://master@filestore-test-repo/path/to/myfile1.txt" ) );

        final OutputStream outStream = provider.newOutputStream( path );
        outStream.write( "my cool content".getBytes() );
        outStream.close();

        final FileStore fileStore = provider.getFileStore( path );

        assertThat( fileStore ).isNotNull();

        assertThat( fileStore.getAttribute( "readOnly" ) ).isEqualTo( Boolean.FALSE );
    }

    @Test
    public void testNewDirectoryStream() throws IOException {
        final URI newRepo = URI.create( "git://dirstream-test-repo" );
        provider.newFileSystem( newRepo, EMPTY_ENV );

        final Path path = provider.getPath( URI.create( "git://master@dirstream-test-repo/myfile1.txt" ) );

        final OutputStream outStream = provider.newOutputStream( path );
        outStream.write( "my cool content".getBytes() );
        outStream.close();

        final Path path2 = provider.getPath( URI.create( "git://user_branch@dirstream-test-repo/other/path/myfile2.txt" ) );

        final OutputStream outStream2 = provider.newOutputStream( path2 );
        outStream2.write( "my cool content".getBytes() );
        outStream2.close();

        final Path path3 = provider.getPath( URI.create( "git://user_branch@dirstream-test-repo/myfile3.txt" ) );

        final OutputStream outStream3 = provider.newOutputStream( path3 );
        outStream3.write( "my cool content".getBytes() );
        outStream3.close();

        final DirectoryStream<Path> stream1 = provider.newDirectoryStream( provider.getPath( URI.create( "git://user_branch@dirstream-test-repo/" ) ), null );

        assertThat( stream1 ).isNotNull().hasSize( 2 ).contains( path3, provider.getPath( URI.create( "git://user_branch@dirstream-test-repo/other" ) ) );

        final DirectoryStream<Path> stream2 = provider.newDirectoryStream( provider.getPath( URI.create( "git://user_branch@dirstream-test-repo/other" ) ), null );

        assertThat( stream2 ).isNotNull().hasSize( 1 ).contains( provider.getPath( URI.create( "git://user_branch@dirstream-test-repo/other/path" ) ) );

        final DirectoryStream<Path> stream3 = provider.newDirectoryStream( provider.getPath( URI.create( "git://user_branch@dirstream-test-repo/other/path" ) ), null );

        assertThat( stream3 ).isNotNull().hasSize( 1 ).contains( path2 );

        final DirectoryStream<Path> stream4 = provider.newDirectoryStream( provider.getPath( URI.create( "git://master@dirstream-test-repo/" ) ), null );

        assertThat( stream4 ).isNotNull().hasSize( 1 ).contains( path );

        try {
            provider.newDirectoryStream( path, null );
            failBecauseExceptionWasNotThrown( NotDirectoryException.class );
        } catch ( NotDirectoryException ex ) {
        }
        final Path crazyPath = provider.getPath( URI.create( "git://master@dirstream-test-repo/crazy/path/here" ) );
        try {
            provider.newDirectoryStream( crazyPath, null );
            failBecauseExceptionWasNotThrown( NotDirectoryException.class );
        } catch ( NotDirectoryException ex ) {
        }

        provider.createDirectory( crazyPath );

        assertThat( provider.newDirectoryStream( crazyPath, null ) ).isNotNull().hasSize( 1 );
    }

    @Test
    public void testDeleteNonEmptyDirectory() throws IOException {
        final URI newRepo = URI.create( "git://delete-non-empty-test-repo" );
        provider.newFileSystem( newRepo, EMPTY_ENV );

        final Path dir = provider.getPath( URI.create( "git://master@delete-non-empty-test-repo/other/path" ) );

        final Path _root = provider.getPath( URI.create( "git://master@delete-non-empty-test-repo/myfile1.txt" ) );

        final OutputStream outRootStream = provider.newOutputStream( _root );
        outRootStream.write( "my cool content".getBytes() );
        outRootStream.close();

        final Path path = provider.getPath( URI.create( "git://master@delete-non-empty-test-repo/other/path/myfile1.txt" ) );

        final OutputStream outStream = provider.newOutputStream( path );
        outStream.write( "my cool content".getBytes() );
        outStream.close();

        final Path path2 = provider.getPath( URI.create( "git://master@delete-non-empty-test-repo/other/path/myfile2.txt" ) );

        final OutputStream outStream2 = provider.newOutputStream( path2 );
        outStream2.write( "my cool content".getBytes() );
        outStream2.close();

        final Path path3 = provider.getPath( URI.create( "git://master@delete-non-empty-test-repo/other/path/myfile3.txt" ) );

        final OutputStream outStream3 = provider.newOutputStream( path3 );
        outStream3.write( "my cool content".getBytes() );
        outStream3.close();

        final Path dir1 = provider.getPath( URI.create( "git://master@delete-non-empty-test-repo/other/path/dir" ) );

        provider.createDirectory( dir1 );

        final DirectoryStream<Path> stream3 = provider.newDirectoryStream( dir, null );

        assertThat( stream3 ).isNotNull().hasSize( 4 );

        try {
            provider.delete( dir );
            fail( "dir not empty" );
        } catch ( final DirectoryNotEmptyException ignore ) {
        }

        try {
            final CommentedOption op = new CommentedOption( "User Tester", "user.tester@example.com", "omg, erase dir!" );

            provider.delete( dir, NON_EMPTY_DIRECTORIES, op );
        } catch ( final DirectoryNotEmptyException ignore ) {
            fail( "dir should be deleted!" );
        }

        assertThat( provider.exists( dir ) ).isEqualTo( false );
    }

    @Test
    public void testFilteredNewDirectoryStream() throws IOException {
        final URI newRepo = URI.create( "git://filter-dirstream-test-repo" );
        provider.newFileSystem( newRepo, EMPTY_ENV );

        final Path path = provider.getPath( URI.create( "git://master@filter-dirstream-test-repo/myfile1.txt" ) );

        final OutputStream outStream = provider.newOutputStream( path );
        outStream.write( "my cool content".getBytes() );
        outStream.close();

        final Path path2 = provider.getPath( URI.create( "git://user_branch@filter-dirstream-test-repo/other/path/myfile2.txt" ) );

        final OutputStream outStream2 = provider.newOutputStream( path2 );
        outStream2.write( "my cool content".getBytes() );
        outStream2.close();

        final Path path3 = provider.getPath( URI.create( "git://user_branch@filter-dirstream-test-repo/myfile3.txt" ) );

        final OutputStream outStream3 = provider.newOutputStream( path3 );
        outStream3.write( "my cool content".getBytes() );
        outStream3.close();

        final Path path4 = provider.getPath( URI.create( "git://user_branch@filter-dirstream-test-repo/myfile4.xxx" ) );

        final OutputStream outStream4 = provider.newOutputStream( path4 );
        outStream4.write( "my cool content".getBytes() );
        outStream4.close();

        final DirectoryStream<Path> stream1 = provider.newDirectoryStream( provider.getPath( URI.create( "git://user_branch@filter-dirstream-test-repo/" ) ), new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept( final Path entry ) throws org.uberfire.java.nio.IOException {
                if ( entry.toString().endsWith( ".xxx" ) ) {
                    return true;
                }
                return false;
            }
        } );

        assertThat( stream1 ).isNotNull().hasSize( 1 ).contains( path4 );

        final DirectoryStream<Path> stream2 = provider.newDirectoryStream( provider.getPath( URI.create( "git://master@filter-dirstream-test-repo/" ) ), new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept( final Path entry ) throws org.uberfire.java.nio.IOException {
                return false;
            }
        } );

        assertThat( stream2 ).isNotNull().hasSize( 0 );
    }

    @Test
    public void testGetFileAttributeView() throws IOException {
        final URI newRepo = URI.create( "git://getfileattriview-test-repo" );
        provider.newFileSystem( newRepo, EMPTY_ENV );

        final Path path = provider.getPath( URI.create( "git://master@getfileattriview-test-repo/myfile1.txt" ) );

        final OutputStream outStream = provider.newOutputStream( path );
        outStream.write( "my cool content".getBytes() );
        outStream.close();

        final Path path2 = provider.getPath( URI.create( "git://user_branch@getfileattriview-test-repo/other/path/myfile2.txt" ) );

        final OutputStream outStream2 = provider.newOutputStream( path2 );
        outStream2.write( "my cool content".getBytes() );
        outStream2.close();

        final Path path3 = provider.getPath( URI.create( "git://user_branch@getfileattriview-test-repo/myfile3.txt" ) );

        final OutputStream outStream3 = provider.newOutputStream( path3 );
        outStream3.write( "my cool content".getBytes() );
        outStream3.close();

        final JGitVersionAttributeView attrs = provider.getFileAttributeView( path3, JGitVersionAttributeView.class );

        assertThat( attrs.readAttributes().history().records().size() ).isEqualTo( 1 );
        assertThat( attrs.readAttributes().history().records().get( 0 ).uri() ).isNotNull();

        assertThat( attrs.readAttributes().isDirectory() ).isFalse();
        assertThat( attrs.readAttributes().isRegularFile() ).isTrue();
        assertThat( attrs.readAttributes().creationTime() ).isNotNull();
        assertThat( attrs.readAttributes().lastModifiedTime() ).isNotNull();
        assertThat( attrs.readAttributes().size() ).isEqualTo( 15L );

        try {
            provider.getFileAttributeView( provider.getPath( URI.create( "git://user_branch@getfileattriview-test-repo/not_exists.txt" ) ), BasicFileAttributeView.class );
            failBecauseExceptionWasNotThrown( NoSuchFileException.class );
        } catch ( Exception e ) {
        }

        assertThat( provider.getFileAttributeView( path3, MyInvalidFileAttributeView.class ) ).isNull();

        final Path rootPath = provider.getPath( URI.create( "git://user_branch@getfileattriview-test-repo/" ) );

        final BasicFileAttributeView attrsRoot = provider.getFileAttributeView( rootPath, BasicFileAttributeView.class );

        assertThat( attrsRoot.readAttributes().isDirectory() ).isTrue();
        assertThat( attrsRoot.readAttributes().isRegularFile() ).isFalse();
        assertThat( attrsRoot.readAttributes().creationTime() ).isNotNull();
        assertThat( attrsRoot.readAttributes().lastModifiedTime() ).isNotNull();
        assertThat( attrsRoot.readAttributes().size() ).isEqualTo( -1L );
    }

    @Test
    public void testReadAttributes() throws IOException {
        final URI newRepo = URI.create( "git://readattrs-test-repo" );
        provider.newFileSystem( newRepo, EMPTY_ENV );

        final Path path = provider.getPath( URI.create( "git://master@readattrs-test-repo/myfile1.txt" ) );

        final OutputStream outStream = provider.newOutputStream( path );
        outStream.write( "my cool content".getBytes() );
        outStream.close();

        final Path path2 = provider.getPath( URI.create( "git://user_branch@readattrs-test-repo/other/path/myfile2.txt" ) );

        final OutputStream outStream2 = provider.newOutputStream( path2 );
        outStream2.write( "my cool content".getBytes() );
        outStream2.close();

        final Path path3 = provider.getPath( URI.create( "git://user_branch@readattrs-test-repo/myfile3.txt" ) );

        final OutputStream outStream3 = provider.newOutputStream( path3 );
        outStream3.write( "my cool content".getBytes() );
        outStream3.close();

        final BasicFileAttributes attrs = provider.readAttributes( path3, BasicFileAttributes.class );

        assertThat( attrs.isDirectory() ).isFalse();
        assertThat( attrs.isRegularFile() ).isTrue();
        assertThat( attrs.creationTime() ).isNotNull();
        assertThat( attrs.lastModifiedTime() ).isNotNull();
        assertThat( attrs.size() ).isEqualTo( 15L );

        try {
            provider.readAttributes( provider.getPath( URI.create( "git://user_branch@readattrs-test-repo/not_exists.txt" ) ), BasicFileAttributes.class );
            failBecauseExceptionWasNotThrown( NoSuchFileException.class );
        } catch ( NoSuchFileException e ) {
        }

        assertThat( provider.readAttributes( path3, MyAttrs.class ) ).isNull();

        final Path rootPath = provider.getPath( URI.create( "git://user_branch@readattrs-test-repo/" ) );

        final BasicFileAttributes attrsRoot = provider.readAttributes( rootPath, BasicFileAttributes.class );

        assertThat( attrsRoot.isDirectory() ).isTrue();
        assertThat( attrsRoot.isRegularFile() ).isFalse();
        assertThat( attrsRoot.creationTime() ).isNotNull();
        assertThat( attrsRoot.lastModifiedTime() ).isNotNull();
        assertThat( attrsRoot.size() ).isEqualTo( -1L );
    }

    @Test
    public void testReadAttributesMap() throws IOException {
        final URI newRepo = URI.create( "git://readattrsmap-test-repo" );
        provider.newFileSystem( newRepo, EMPTY_ENV );

        final Path path = provider.getPath( URI.create( "git://master@readattrsmap-test-repo/myfile1.txt" ) );

        final OutputStream outStream = provider.newOutputStream( path );
        outStream.write( "my cool content".getBytes() );
        outStream.close();

        final Path path2 = provider.getPath( URI.create( "git://user_branch@readattrsmap-test-repo/other/path/myfile2.txt" ) );

        final OutputStream outStream2 = provider.newOutputStream( path2 );
        outStream2.write( "my cool content".getBytes() );
        outStream2.close();

        final Path path3 = provider.getPath( URI.create( "git://user_branch@readattrsmap-test-repo/myfile3.txt" ) );

        final OutputStream outStream3 = provider.newOutputStream( path3 );
        outStream3.write( "my cool content".getBytes() );
        outStream3.close();

        assertThat( provider.readAttributes( path, "*" ) ).isNotNull().hasSize( 9 );
        assertThat( provider.readAttributes( path, "basic:*" ) ).isNotNull().hasSize( 9 );
        assertThat( provider.readAttributes( path, "basic:isRegularFile" ) ).isNotNull().hasSize( 1 );
        assertThat( provider.readAttributes( path, "basic:isRegularFile,isDirectory" ) ).isNotNull().hasSize( 2 );
        assertThat( provider.readAttributes( path, "basic:isRegularFile,isDirectory,someThing" ) ).isNotNull().hasSize( 2 );
        assertThat( provider.readAttributes( path, "basic:someThing" ) ).isNotNull().hasSize( 0 );
        assertThat( provider.readAttributes( path, "version:version" ) ).isNotNull().hasSize( 1 );

        assertThat( provider.readAttributes( path, "isRegularFile" ) ).isNotNull().hasSize( 1 );
        assertThat( provider.readAttributes( path, "isRegularFile,isDirectory" ) ).isNotNull().hasSize( 2 );
        assertThat( provider.readAttributes( path, "isRegularFile,isDirectory,someThing" ) ).isNotNull().hasSize( 2 );
        assertThat( provider.readAttributes( path, "someThing" ) ).isNotNull().hasSize( 0 );

        try {
            provider.readAttributes( path, ":someThing" );
            failBecauseExceptionWasNotThrown( IllegalArgumentException.class );
        } catch ( IllegalArgumentException ex ) {
        }

        try {
            provider.readAttributes( path, "advanced:isRegularFile" );
            failBecauseExceptionWasNotThrown( UnsupportedOperationException.class );
        } catch ( UnsupportedOperationException ex ) {
        }

        final Path rootPath = provider.getPath( URI.create( "git://user_branch@readattrsmap-test-repo/" ) );

        assertThat( provider.readAttributes( rootPath, "*" ) ).isNotNull().hasSize( 9 );
        assertThat( provider.readAttributes( rootPath, "basic:*" ) ).isNotNull().hasSize( 9 );
        assertThat( provider.readAttributes( rootPath, "basic:isRegularFile" ) ).isNotNull().hasSize( 1 );
        assertThat( provider.readAttributes( rootPath, "basic:isRegularFile,isDirectory" ) ).isNotNull().hasSize( 2 );
        assertThat( provider.readAttributes( rootPath, "basic:isRegularFile,isDirectory,someThing" ) ).isNotNull().hasSize( 2 );
        assertThat( provider.readAttributes( rootPath, "basic:someThing" ) ).isNotNull().hasSize( 0 );

        assertThat( provider.readAttributes( rootPath, "isRegularFile" ) ).isNotNull().hasSize( 1 );
        assertThat( provider.readAttributes( rootPath, "isRegularFile,isDirectory" ) ).isNotNull().hasSize( 2 );
        assertThat( provider.readAttributes( rootPath, "isRegularFile,isDirectory,someThing" ) ).isNotNull().hasSize( 2 );
        assertThat( provider.readAttributes( rootPath, "someThing" ) ).isNotNull().hasSize( 0 );

        try {
            provider.readAttributes( rootPath, ":someThing" );
            failBecauseExceptionWasNotThrown( IllegalArgumentException.class );
        } catch ( IllegalArgumentException ex ) {
        }

        try {
            provider.readAttributes( rootPath, "advanced:isRegularFile" );
            failBecauseExceptionWasNotThrown( UnsupportedOperationException.class );
        } catch ( UnsupportedOperationException ex ) {
        }

        try {
            provider.readAttributes( provider.getPath( URI.create( "git://user_branch@readattrsmap-test-repo/not_exists.txt" ) ), BasicFileAttributes.class );
            failBecauseExceptionWasNotThrown( NoSuchFileException.class );
        } catch ( NoSuchFileException e ) {
        }
    }

    @Test
    public void testSetAttribute() throws IOException {
        final URI newRepo = URI.create( "git://setattr-test-repo" );
        provider.newFileSystem( newRepo, EMPTY_ENV );

        final Path path = provider.getPath( URI.create( "git://master@setattr-test-repo/myfile1.txt" ) );

        final OutputStream outStream = provider.newOutputStream( path );
        outStream.write( "my cool content".getBytes() );
        outStream.close();

        final Path path2 = provider.getPath( URI.create( "git://user_branch@setattr-test-repo/other/path/myfile2.txt" ) );

        final OutputStream outStream2 = provider.newOutputStream( path2 );
        outStream2.write( "my cool content".getBytes() );
        outStream2.close();

        final Path path3 = provider.getPath( URI.create( "git://user_branch@setattr-test-repo/myfile3.txt" ) );

        final OutputStream outStream3 = provider.newOutputStream( path3 );
        outStream3.write( "my cool content".getBytes() );
        outStream3.close();

        try {
            provider.setAttribute( path3, "basic:isRegularFile", true );
            failBecauseExceptionWasNotThrown( NotImplementedException.class );
        } catch ( NotImplementedException ex ) {
        }

        try {
            provider.setAttribute( path3, "isRegularFile", true );
            failBecauseExceptionWasNotThrown( NotImplementedException.class );
        } catch ( NotImplementedException ex ) {
        }

        try {
            provider.setAttribute( path3, "notExisits", true );
            failBecauseExceptionWasNotThrown( IllegalStateException.class );
        } catch ( IllegalStateException ex ) {
        }

        try {
            provider.setAttribute( path3, "advanced:notExisits", true );
            failBecauseExceptionWasNotThrown( UnsupportedOperationException.class );
        } catch ( UnsupportedOperationException ex ) {
        }

        try {
            provider.setAttribute( path3, ":isRegularFile", true );
            failBecauseExceptionWasNotThrown( IllegalArgumentException.class );
        } catch ( IllegalArgumentException ex ) {
        }

    }

    private static class MyInvalidFileAttributeView implements BasicFileAttributeView {

        @Override
        public BasicFileAttributes readAttributes() throws org.uberfire.java.nio.IOException {
            return null;
        }

        @Override
        public void setTimes( FileTime lastModifiedTime,
                              FileTime lastAccessTime,
                              FileTime createTime ) throws org.uberfire.java.nio.IOException {

        }

        @Override
        public String name() {
            return null;
        }
    }

    private static interface MyAttrs extends BasicFileAttributes {

    }
}
