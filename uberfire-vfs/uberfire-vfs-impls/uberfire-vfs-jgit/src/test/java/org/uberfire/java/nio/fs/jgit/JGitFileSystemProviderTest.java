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
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.junit.Test;
import org.kie.commons.data.Pair;
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
import org.uberfire.java.nio.file.attribute.BasicFileAttributeView;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.java.nio.file.attribute.FileTime;
import org.uberfire.java.nio.fs.base.NotImplementedException;
import org.uberfire.java.nio.fs.jgit.util.JGitUtil;

import static org.fest.assertions.api.Assertions.*;
import static org.uberfire.java.nio.fs.jgit.util.JGitUtil.*;

public class JGitFileSystemProviderTest extends AbstractTestInfra {

    private static final JGitFileSystemProvider PROVIDER = new JGitFileSystemProvider();

    @Test
    public void testNewFileSystem() {
        final URI newRepo = URI.create( "git://repo-name" );

        final FileSystem fs = PROVIDER.newFileSystem( newRepo, EMPTY_ENV );

        assertThat( fs ).isNotNull();

        final DirectoryStream<Path> stream = PROVIDER.newDirectoryStream( PROVIDER.getPath( newRepo ), null );
        assertThat( stream ).isNotNull().hasSize( 0 );

        try {
            PROVIDER.newFileSystem( newRepo, EMPTY_ENV );
            failBecauseExceptionWasNotThrown( FileSystemAlreadyExistsException.class );
        } catch ( final Exception ex ) {
        }

        PROVIDER.newFileSystem( URI.create( "git://repo-name2" ), EMPTY_ENV );
    }

    @Test
    public void testNewFileSystemInited() {
        final URI newRepo = URI.create( "git://init-repo-name" );

        final Map<String, ?> env = new HashMap<String, Object>() {{
            put( "init", Boolean.TRUE );
        }};

        final FileSystem fs = PROVIDER.newFileSystem( newRepo, env );

        assertThat( fs ).isNotNull();

        final DirectoryStream<Path> stream = PROVIDER.newDirectoryStream( PROVIDER.getPath( newRepo ), null );
        assertThat( stream ).isNotNull().hasSize( 1 );
    }

    @Test
    public void testInvalidURINewFileSystem() {
        final URI newRepo = URI.create( "git:///repo-name" );

        try {
            PROVIDER.newFileSystem( newRepo, EMPTY_ENV );
            failBecauseExceptionWasNotThrown( IllegalArgumentException.class );
        } catch ( final IllegalArgumentException ex ) {
            assertThat( ex.getMessage() ).isEqualTo( "Parameter named 'uri' is invalid, missing host repository!" );
        }
    }

    @Test
    public void testNewFileSystemClone() throws IOException {

        final File parentFolder = createTempDirectory();
        final File gitFolder = new File( parentFolder, "mytest.git" );

        final Git origin = JGitUtil.newRepository( gitFolder );

        commit( origin, "master", "file.txt", tempFile( "temp" ), "user1", "user1@example.com", "commitx", null, null );

        final URI newRepo = URI.create( "git://my-repo-name" );

        final Map<String, Object> env = new HashMap<String, Object>() {{
            put( JGitFileSystemProvider.GIT_DEFAULT_REMOTE_NAME, origin.getRepository().getDirectory().toString() );
        }};

        final FileSystem fs = PROVIDER.newFileSystem( newRepo, env );

        assertThat( fs ).isNotNull();

        assertThat( fs.getRootDirectories() ).hasSize( 2 );

        commit( origin, "XmasterX", "fileXXXXX.txt", tempFile( "temp" ), "user1", "user1@example.com", "commitx", null, null );

        PROVIDER.getFileSystem( URI.create( "git://my-repo-name?fetch" ) );

        assertThat( fs ).isNotNull();

        assertThat( fs.getRootDirectories() ).hasSize( 3 );
    }

    @Test
    public void testGetFileSystem() {
        final URI newRepo = URI.create( "git://new-repo-name" );

        final FileSystem fs = PROVIDER.newFileSystem( newRepo, EMPTY_ENV );

        assertThat( fs ).isNotNull();

        assertThat( PROVIDER.getFileSystem( newRepo ) ).isEqualTo( fs );
        assertThat( PROVIDER.getFileSystem( URI.create( "git://master@new-repo-name" ) ) ).isEqualTo( fs );
        assertThat( PROVIDER.getFileSystem( URI.create( "git://branch@new-repo-name" ) ) ).isEqualTo( fs );

        assertThat( PROVIDER.getFileSystem( URI.create( "git://branch@new-repo-name?fetch" ) ) ).isEqualTo( fs );
    }

    @Test
    public void testInvalidURIGetFileSystem() {
        final URI newRepo = URI.create( "git:///new-repo-name" );

        try {
            PROVIDER.getFileSystem( newRepo );
            failBecauseExceptionWasNotThrown( IllegalArgumentException.class );
        } catch ( final IllegalArgumentException ex ) {
            assertThat( ex.getMessage() ).isEqualTo( "Parameter named 'uri' is invalid, missing host repository!" );
        }

    }

    @Test
    public void testGetPath() {
        final URI newRepo = URI.create( "git://new-get-repo-name" );

        PROVIDER.newFileSystem( newRepo, EMPTY_ENV );

        final Path path = PROVIDER.getPath( URI.create( "git://master@new-get-repo-name/home" ) );

        assertThat( path ).isNotNull();
        assertThat( path.getRoot().toString() ).isEqualTo( "/" );
        assertThat( path.getRoot().toUri().toString() ).isEqualTo( "git://master@new-get-repo-name/" );
        assertThat( path.toString() ).isEqualTo( "/home" );

        final Path pathRelative = PROVIDER.getPath( URI.create( "git://master@new-get-repo-name/:home" ) );
        assertThat( pathRelative ).isNotNull();
        assertThat( pathRelative.toUri().toString() ).isEqualTo( "git://master@new-get-repo-name/:home" );
        assertThat( pathRelative.getRoot().toString() ).isEqualTo( "" );
        assertThat( pathRelative.toString() ).isEqualTo( "home" );
    }

    @Test
    public void testInvalidURIGetPath() {
        final URI uri = URI.create( "git:///master@new-get-repo-name/home" );

        try {
            PROVIDER.getPath( uri );
            failBecauseExceptionWasNotThrown( IllegalArgumentException.class );
        } catch ( final IllegalArgumentException ex ) {
            assertThat( ex.getMessage() ).isEqualTo( "Parameter named 'uri' is invalid, missing host repository!" );
        }
    }

    @Test
    public void testGetComplexPath() {
        final URI newRepo = URI.create( "git://new-complex-get-repo-name" );

        PROVIDER.newFileSystem( newRepo, EMPTY_ENV );

        final Path path = PROVIDER.getPath( URI.create( "git://origin/master@new-complex-get-repo-name/home" ) );

        assertThat( path ).isNotNull();
        assertThat( path.getRoot().toString() ).isEqualTo( "/" );
        assertThat( path.toString() ).isEqualTo( "/home" );

        final Path pathRelative = PROVIDER.getPath( URI.create( "git://origin/master@new-complex-get-repo-name/:home" ) );
        assertThat( pathRelative ).isNotNull();
        assertThat( pathRelative.getRoot().toString() ).isEqualTo( "" );
        assertThat( pathRelative.toString() ).isEqualTo( "home" );
    }

    @Test
    public void testInputStream() throws IOException {
        final File parentFolder = createTempDirectory();
        final File gitFolder = new File( parentFolder, "mytest.git" );

        final Git origin = JGitUtil.newRepository( gitFolder );

        commit( origin, "master", "myfile.txt", tempFile( "temp\n.origin\n.content" ), "user", "user@example.com", "commit message", null, null );

        final URI newRepo = URI.create( "git://inputstream-test-repo" );

        final Map<String, Object> env = new HashMap<String, Object>() {{
            put( JGitFileSystemProvider.GIT_DEFAULT_REMOTE_NAME, origin.getRepository().getDirectory().toString() );
        }};

        final FileSystem fs = PROVIDER.newFileSystem( newRepo, env );

        assertThat( fs ).isNotNull();

        final Path path = PROVIDER.getPath( URI.create( "git://origin/master@inputstream-test-repo/myfile.txt" ) );

        final InputStream inputStream = PROVIDER.newInputStream( path );
        assertThat( inputStream ).isNotNull();

        final String content = new Scanner( inputStream ).useDelimiter( "\\A" ).next();

        inputStream.close();

        assertThat( content ).isNotNull().isEqualTo( "temp\n.origin\n.content" );
    }

    @Test
    public void testInputStream2() throws IOException {

        final File parentFolder = createTempDirectory();
        final File gitFolder = new File( parentFolder, "mytest.git" );

        final Git origin = JGitUtil.newRepository( gitFolder );

        commit( origin, "master", "path/to/file/myfile.txt", tempFile( "temp\n.origin\n.content" ), "user", "user@example.com", "commit message", null, null );

        final URI newRepo = URI.create( "git://xinputstream-test-repo" );

        final Map<String, Object> env = new HashMap<String, Object>() {{
            put( JGitFileSystemProvider.GIT_DEFAULT_REMOTE_NAME, origin.getRepository().getDirectory().toString() );
        }};

        final FileSystem fs = PROVIDER.newFileSystem( newRepo, env );

        assertThat( fs ).isNotNull();

        final Path path = PROVIDER.getPath( URI.create( "git://origin/master@xinputstream-test-repo/path/to/file/myfile.txt" ) );

        final InputStream inputStream = PROVIDER.newInputStream( path );
        assertThat( inputStream ).isNotNull();

        final String content = new Scanner( inputStream ).useDelimiter( "\\A" ).next();

        inputStream.close();

        assertThat( content ).isNotNull().isEqualTo( "temp\n.origin\n.content" );
    }

    @Test(expected = NoSuchFileException.class)
    public void testInputStream3() throws IOException {

        final File parentFolder = createTempDirectory();
        final File gitFolder = new File( parentFolder, "mytest.git" );

        final Git origin = JGitUtil.newRepository( gitFolder );

        commit( origin, "master", "path/to/file/myfile.txt", tempFile( "temp\n.origin\n.content" ), "user", "user@example.com", "commit message", null, null );

        final URI newRepo = URI.create( "git://xxinputstream-test-repo" );

        final Map<String, Object> env = new HashMap<String, Object>() {{
            put( JGitFileSystemProvider.GIT_DEFAULT_REMOTE_NAME, origin.getRepository().getDirectory().toString() );
        }};

        final FileSystem fs = PROVIDER.newFileSystem( newRepo, env );

        assertThat( fs ).isNotNull();

        final Path path = PROVIDER.getPath( URI.create( "git://origin/master@xxinputstream-test-repo/path/to" ) );

        PROVIDER.newInputStream( path );
    }

    @Test(expected = NoSuchFileException.class)
    public void testInputStreamNoSuchFile() throws IOException {

        final File parentFolder = createTempDirectory();
        final File gitFolder = new File( parentFolder, "mytest.git" );

        final Git origin = JGitUtil.newRepository( gitFolder );

        commit( origin, "master", "file.txt", tempFile( "temp.origin.content.2" ), "user1", "user1@example.com", "commitx", null, null );

        final URI newRepo = URI.create( "git://inputstream-not-exists-test-repo" );

        final Map<String, Object> env = new HashMap<String, Object>() {{
            put( JGitFileSystemProvider.GIT_DEFAULT_REMOTE_NAME, origin.getRepository().getDirectory().toString() );
        }};

        final FileSystem fs = PROVIDER.newFileSystem( newRepo, env );

        assertThat( fs ).isNotNull();

        final Path path = PROVIDER.getPath( URI.create( "git://origin/master@inputstream-not-exists-test-repo/temp.txt" ) );

        PROVIDER.newInputStream( path );
    }

    @Test
    public void testNewOutputStream() throws Exception {
        final File parentFolder = createTempDirectory();
        final File gitFolder = new File( parentFolder, "mytest.git" );

        final Git origin = JGitUtil.newRepository( gitFolder );

        commit( origin, "master", "myfile.txt", tempFile( "temp\n.origin\n.content" ), "user", "user@example.com", "commit message", null, null );
        commit( origin, "user_branch", "path/to/some/file/myfile.txt", tempFile( "some\n.content\nhere" ), "user", "user@example.com", "commit message", null, null );

        final URI newRepo = URI.create( "git://outstream-test-repo" );

        final Map<String, Object> env = new HashMap<String, Object>() {{
            put( JGitFileSystemProvider.GIT_DEFAULT_REMOTE_NAME, origin.getRepository().getDirectory().toString() );
        }};

        final FileSystem fs = PROVIDER.newFileSystem( newRepo, env );

        assertThat( fs ).isNotNull();

        final Path path = PROVIDER.getPath( URI.create( "git://user_branch@outstream-test-repo/some/path/myfile.txt" ) );

        final OutputStream outStream = PROVIDER.newOutputStream( path );
        assertThat( outStream ).isNotNull();
        outStream.write( "my cool content".getBytes() );
        outStream.close();

        final InputStream inStream = PROVIDER.newInputStream( path );

        final String content = new Scanner( inStream ).useDelimiter( "\\A" ).next();

        inStream.close();

        assertThat( content ).isNotNull().isEqualTo( "my cool content" );

        try {
            PROVIDER.newOutputStream( PROVIDER.getPath( URI.create( "git://user_branch@outstream-test-repo/some/path/" ) ) );
            failBecauseExceptionWasNotThrown( org.uberfire.java.nio.IOException.class );
        } catch ( Exception e ) {
        }
    }

    @Test
    public void testNewOutputStreamWithJGitOp() throws Exception {
        final File parentFolder = createTempDirectory();
        final File gitFolder = new File( parentFolder, "mytest.git" );

        final Git origin = JGitUtil.newRepository( gitFolder );

        commit( origin, "master", "myfile.txt", tempFile( "temp\n.origin\n.content" ), "user", "user@example.com", "commit message", null, null );
        commit( origin, "user_branch", "path/to/some/file/myfile.txt", tempFile( "some\n.content\nhere" ), "user", "user@example.com", "commit message", null, null );

        final URI newRepo = URI.create( "git://outstreamwithop-test-repo" );

        final Map<String, Object> env = new HashMap<String, Object>() {{
            put( JGitFileSystemProvider.GIT_DEFAULT_REMOTE_NAME, origin.getRepository().getDirectory().toString() );
        }};

        final FileSystem fs = PROVIDER.newFileSystem( newRepo, env );

        assertThat( fs ).isNotNull();

        final SimpleDateFormat formatter = new SimpleDateFormat( "dd/MM/yyyy" );

        final JGitOp op = new JGitOp( "User Tester", "user.tester@example.com", "omg, is it the end?", formatter.parse( "31/12/2012" ) );

        final Path path = PROVIDER.getPath( URI.create( "git://user_branch@outstreamwithop-test-repo/some/path/myfile.txt" ) );

        final OutputStream outStream = PROVIDER.newOutputStream( path, op );
        assertThat( outStream ).isNotNull();
        outStream.write( "my cool content".getBytes() );
        outStream.close();

        final InputStream inStream = PROVIDER.newInputStream( path );

        final String content = new Scanner( inStream ).useDelimiter( "\\A" ).next();

        inStream.close();

        assertThat( content ).isNotNull().isEqualTo( "my cool content" );
    }

    @Test(expected = FileSystemNotFoundException.class)
    public void testGetPathFileSystemNotExisting() {
        PROVIDER.getPath( URI.create( "git://master@not-exists-get-repo-name/home" ) );
    }

    @Test(expected = FileSystemNotFoundException.class)
    public void testGetFileSystemNotExisting() {
        final URI newRepo = URI.create( "git://not-new-repo-name" );

        PROVIDER.getFileSystem( newRepo );
    }

    @Test
    public void testDelete() throws IOException {
        final URI newRepo = URI.create( "git://delete1-test-repo" );
        PROVIDER.newFileSystem( newRepo, EMPTY_ENV );

        final Path path = PROVIDER.getPath( URI.create( "git://user_branch@delete1-test-repo/path/to/myfile.txt" ) );

        final OutputStream outStream = PROVIDER.newOutputStream( path );
        assertThat( outStream ).isNotNull();
        outStream.write( "my cool content".getBytes() );
        outStream.close();

        PROVIDER.newInputStream( path ).close();

        try {
            PROVIDER.delete( PROVIDER.getPath( URI.create( "git://user_branch@delete1-test-repo/non_existent_path" ) ) );
            failBecauseExceptionWasNotThrown( NoSuchFileException.class );
        } catch ( NoSuchFileException ex ) {
        }

        try {
            PROVIDER.delete( PROVIDER.getPath( URI.create( "git://user_branch@delete1-test-repo/path/to/" ) ) );
            failBecauseExceptionWasNotThrown( DirectoryNotEmptyException.class );
        } catch ( DirectoryNotEmptyException ex ) {
        }

        PROVIDER.delete( path );
    }

    @Test
    public void testDeleteBranch() throws IOException {
        final URI newRepo = URI.create( "git://delete-branch-test-repo" );
        PROVIDER.newFileSystem( newRepo, EMPTY_ENV );

        final Path path = PROVIDER.getPath( URI.create( "git://user_branch@delete-branch-test-repo/path/to/myfile.txt" ) );

        final OutputStream outStream = PROVIDER.newOutputStream( path );
        assertThat( outStream ).isNotNull();
        outStream.write( "my cool content".getBytes() );
        outStream.close();

        PROVIDER.newInputStream( path ).close();

        PROVIDER.delete( PROVIDER.getPath( URI.create( "git://user_branch@delete-branch-test-repo" ) ) );

        try {
            PROVIDER.delete( PROVIDER.getPath( URI.create( "git://user_branch@delete-branch-test-repo" ) ) );
            failBecauseExceptionWasNotThrown( NoSuchFileException.class );
        } catch ( NoSuchFileException ex ) {
        }

        try {
            PROVIDER.delete( PROVIDER.getPath( URI.create( "git://some_user_branch@delete-branch-test-repo" ) ) );
            failBecauseExceptionWasNotThrown( NoSuchFileException.class );
        } catch ( NoSuchFileException ex ) {
        }
    }

    @Test
    public void testDeleteIfExists() throws IOException {
        final URI newRepo = URI.create( "git://deleteifexists1-test-repo" );
        PROVIDER.newFileSystem( newRepo, EMPTY_ENV );

        final Path path = PROVIDER.getPath( URI.create( "git://user_branch@deleteifexists1-test-repo/path/to/myfile.txt" ) );

        final OutputStream outStream = PROVIDER.newOutputStream( path );
        assertThat( outStream ).isNotNull();
        outStream.write( "my cool content".getBytes() );
        outStream.close();

        PROVIDER.newInputStream( path ).close();

        assertThat( PROVIDER.deleteIfExists( PROVIDER.getPath( URI.create( "git://user_branch@deleteifexists1-test-repo/non_existent_path" ) ) ) ).isFalse();

        try {
            PROVIDER.deleteIfExists( PROVIDER.getPath( URI.create( "git://user_branch@deleteifexists1-test-repo/path/to/" ) ) );
            failBecauseExceptionWasNotThrown( DirectoryNotEmptyException.class );
        } catch ( DirectoryNotEmptyException ex ) {
        }

        assertThat( PROVIDER.deleteIfExists( path ) ).isTrue();
    }

    @Test
    public void testDeleteBranchIfExists() throws IOException {
        final URI newRepo = URI.create( "git://deletebranchifexists1-test-repo" );
        PROVIDER.newFileSystem( newRepo, EMPTY_ENV );

        final Path path = PROVIDER.getPath( URI.create( "git://user_branch@deletebranchifexists1-test-repo/path/to/myfile.txt" ) );

        final OutputStream outStream = PROVIDER.newOutputStream( path );
        assertThat( outStream ).isNotNull();
        outStream.write( "my cool content".getBytes() );
        outStream.close();

        PROVIDER.newInputStream( path ).close();

        assertThat( PROVIDER.deleteIfExists( PROVIDER.getPath( URI.create( "git://user_branch@deletebranchifexists1-test-repo" ) ) ) ).isTrue();

        assertThat( PROVIDER.deleteIfExists( PROVIDER.getPath( URI.create( "git://not_user_branch@deletebranchifexists1-test-repo" ) ) ) ).isFalse();

        assertThat( PROVIDER.deleteIfExists( PROVIDER.getPath( URI.create( "git://user_branch@deletebranchifexists1-test-repo" ) ) ) ).isFalse();

    }

    @Test
    public void testIsHidden() throws IOException {
        final URI newRepo = URI.create( "git://ishidden-test-repo" );
        PROVIDER.newFileSystem( newRepo, EMPTY_ENV );

        final Path path = PROVIDER.getPath( URI.create( "git://user_branch@ishidden-test-repo/path/to/.myfile.txt" ) );

        final OutputStream outStream = PROVIDER.newOutputStream( path );
        assertThat( outStream ).isNotNull();
        outStream.write( "my cool content".getBytes() );
        outStream.close();

        final Path path2 = PROVIDER.getPath( URI.create( "git://user_branch@ishidden-test-repo/path/to/myfile.txt" ) );

        final OutputStream outStream2 = PROVIDER.newOutputStream( path2 );
        assertThat( outStream2 ).isNotNull();
        outStream2.write( "my cool content".getBytes() );
        outStream2.close();

        assertThat( PROVIDER.isHidden( PROVIDER.getPath( URI.create( "git://user_branch@ishidden-test-repo/path/to/.myfile.txt" ) ) ) ).isTrue();

        assertThat( PROVIDER.isHidden( PROVIDER.getPath( URI.create( "git://user_branch@ishidden-test-repo/path/to/myfile.txt" ) ) ) ).isFalse();

        assertThat( PROVIDER.isHidden( PROVIDER.getPath( URI.create( "git://user_branch@ishidden-test-repo/path/to/non_existent/.myfile.txt" ) ) ) ).isTrue();

        assertThat( PROVIDER.isHidden( PROVIDER.getPath( URI.create( "git://user_branch@ishidden-test-repo/path/to/non_existent/myfile.txt" ) ) ) ).isFalse();

        assertThat( PROVIDER.isHidden( PROVIDER.getPath( URI.create( "git://user_branch@ishidden-test-repo/" ) ) ) ).isFalse();

        assertThat( PROVIDER.isHidden( PROVIDER.getPath( URI.create( "git://user_branch@ishidden-test-repo/some" ) ) ) ).isFalse();
    }

    @Test
    public void testIsSameFile() throws IOException {
        final URI newRepo = URI.create( "git://issamefile-test-repo" );
        PROVIDER.newFileSystem( newRepo, EMPTY_ENV );

        final Path path = PROVIDER.getPath( URI.create( "git://master@issamefile-test-repo/path/to/myfile1.txt" ) );

        final OutputStream outStream = PROVIDER.newOutputStream( path );
        outStream.write( "my cool content".getBytes() );
        outStream.close();

        final Path path2 = PROVIDER.getPath( URI.create( "git://user_branch@issamefile-test-repo/path/to/myfile2.txt" ) );

        final OutputStream outStream2 = PROVIDER.newOutputStream( path2 );
        outStream2.write( "my cool content".getBytes() );
        outStream2.close();

        final Path path3 = PROVIDER.getPath( URI.create( "git://user_branch@issamefile-test-repo/path/to/myfile3.txt" ) );

        final OutputStream outStream3 = PROVIDER.newOutputStream( path3 );
        outStream3.write( "my cool content".getBytes() );
        outStream3.close();

        assertThat( PROVIDER.isSameFile( path, path2 ) ).isTrue();

        assertThat( PROVIDER.isSameFile( path, path3 ) ).isTrue();
    }

    @Test
    public void testCreateDirectory() throws Exception {
        final URI newRepo = URI.create( "git://xcreatedir-test-repo" );
        PROVIDER.newFileSystem( newRepo, EMPTY_ENV );

        final JGitPathImpl path = (JGitPathImpl) PROVIDER.getPath( URI.create( "git://master@xcreatedir-test-repo/some/path/to/" ) );

        final Pair<PathType, ObjectId> result = JGitUtil.checkPath( path.getFileSystem().gitRepo(), path.getRefTree(), path.getPath() );
        assertThat( result.getK1() ).isEqualTo( PathType.NOT_FOUND );

        PROVIDER.createDirectory( path );

        final Pair<PathType, ObjectId> resultAfter = JGitUtil.checkPath( path.getFileSystem().gitRepo(), path.getRefTree(), path.getPath() );
        assertThat( resultAfter.getK1() ).isEqualTo( PathType.DIRECTORY );

        try {
            PROVIDER.createDirectory( path );
            failBecauseExceptionWasNotThrown( FileAlreadyExistsException.class );
        } catch ( FileAlreadyExistsException e ) {
        }
    }

    @Test
    public void testCheckAccess() throws Exception {
        final URI newRepo = URI.create( "git://checkaccess-test-repo" );
        PROVIDER.newFileSystem( newRepo, EMPTY_ENV );

        final Path path = PROVIDER.getPath( URI.create( "git://master@checkaccess-test-repo/path/to/myfile1.txt" ) );

        final OutputStream outStream = PROVIDER.newOutputStream( path );
        outStream.write( "my cool content".getBytes() );
        outStream.close();

        PROVIDER.checkAccess( path );

        final Path path_to_dir = PROVIDER.getPath( URI.create( "git://master@checkaccess-test-repo/path/to" ) );

        PROVIDER.checkAccess( path_to_dir );

        final Path path_not_exists = PROVIDER.getPath( URI.create( "git://master@checkaccess-test-repo/path/to/some.txt" ) );

        try {
            PROVIDER.checkAccess( path_not_exists );
            failBecauseExceptionWasNotThrown( NoSuchFileException.class );
        } catch ( NoSuchFileException e ) {
        }
    }

    @Test
    public void testGetFileStore() throws Exception {
        final URI newRepo = URI.create( "git://filestore-test-repo" );
        PROVIDER.newFileSystem( newRepo, EMPTY_ENV );

        final Path path = PROVIDER.getPath( URI.create( "git://master@filestore-test-repo/path/to/myfile1.txt" ) );

        final OutputStream outStream = PROVIDER.newOutputStream( path );
        outStream.write( "my cool content".getBytes() );
        outStream.close();

        final FileStore fileStore = PROVIDER.getFileStore( path );

        assertThat( fileStore ).isNotNull();

        assertThat( fileStore.getAttribute( "readOnly" ) ).isEqualTo( Boolean.FALSE );
    }

    @Test
    public void testNewDirectoryStream() throws IOException {
        final URI newRepo = URI.create( "git://dirstream-test-repo" );
        PROVIDER.newFileSystem( newRepo, EMPTY_ENV );

        final Path path = PROVIDER.getPath( URI.create( "git://master@dirstream-test-repo/myfile1.txt" ) );

        final OutputStream outStream = PROVIDER.newOutputStream( path );
        outStream.write( "my cool content".getBytes() );
        outStream.close();

        final Path path2 = PROVIDER.getPath( URI.create( "git://user_branch@dirstream-test-repo/other/path/myfile2.txt" ) );

        final OutputStream outStream2 = PROVIDER.newOutputStream( path2 );
        outStream2.write( "my cool content".getBytes() );
        outStream2.close();

        final Path path3 = PROVIDER.getPath( URI.create( "git://user_branch@dirstream-test-repo/myfile3.txt" ) );

        final OutputStream outStream3 = PROVIDER.newOutputStream( path3 );
        outStream3.write( "my cool content".getBytes() );
        outStream3.close();

        final DirectoryStream<Path> stream1 = PROVIDER.newDirectoryStream( PROVIDER.getPath( URI.create( "git://user_branch@dirstream-test-repo/" ) ), null );

        assertThat( stream1 ).isNotNull().hasSize( 2 ).contains( path3, PROVIDER.getPath( URI.create( "git://user_branch@dirstream-test-repo/other" ) ) );

        final DirectoryStream<Path> stream2 = PROVIDER.newDirectoryStream( PROVIDER.getPath( URI.create( "git://user_branch@dirstream-test-repo/other" ) ), null );

        assertThat( stream2 ).isNotNull().hasSize( 1 ).contains( PROVIDER.getPath( URI.create( "git://user_branch@dirstream-test-repo/other/path" ) ) );

        final DirectoryStream<Path> stream3 = PROVIDER.newDirectoryStream( PROVIDER.getPath( URI.create( "git://user_branch@dirstream-test-repo/other/path" ) ), null );

        assertThat( stream3 ).isNotNull().hasSize( 1 ).contains( path2 );

        final DirectoryStream<Path> stream4 = PROVIDER.newDirectoryStream( PROVIDER.getPath( URI.create( "git://master@dirstream-test-repo/" ) ), null );

        assertThat( stream4 ).isNotNull().hasSize( 1 ).contains( path );

        try {
            PROVIDER.newDirectoryStream( path, null );
            failBecauseExceptionWasNotThrown( NotDirectoryException.class );
        } catch ( NotDirectoryException ex ) {
        }
        final Path crazyPath = PROVIDER.getPath( URI.create( "git://master@dirstream-test-repo/crazy/path/here" ) );
        try {
            PROVIDER.newDirectoryStream( crazyPath, null );
            failBecauseExceptionWasNotThrown( NotDirectoryException.class );
        } catch ( NotDirectoryException ex ) {
        }

        PROVIDER.createDirectory( crazyPath );

        assertThat( PROVIDER.newDirectoryStream( crazyPath, null ) ).isNotNull().hasSize( 1 );
    }

    @Test
    public void testGetFileAttributeView() throws IOException {
        final URI newRepo = URI.create( "git://getfileattriview-test-repo" );
        PROVIDER.newFileSystem( newRepo, EMPTY_ENV );

        final Path path = PROVIDER.getPath( URI.create( "git://master@getfileattriview-test-repo/myfile1.txt" ) );

        final OutputStream outStream = PROVIDER.newOutputStream( path );
        outStream.write( "my cool content".getBytes() );
        outStream.close();

        final Path path2 = PROVIDER.getPath( URI.create( "git://user_branch@getfileattriview-test-repo/other/path/myfile2.txt" ) );

        final OutputStream outStream2 = PROVIDER.newOutputStream( path2 );
        outStream2.write( "my cool content".getBytes() );
        outStream2.close();

        final Path path3 = PROVIDER.getPath( URI.create( "git://user_branch@getfileattriview-test-repo/myfile3.txt" ) );

        final OutputStream outStream3 = PROVIDER.newOutputStream( path3 );
        outStream3.write( "my cool content".getBytes() );
        outStream3.close();

        final BasicFileAttributeView attrs = PROVIDER.getFileAttributeView( path3, BasicFileAttributeView.class );

        assertThat( attrs.readAttributes().isDirectory() ).isFalse();
        assertThat( attrs.readAttributes().isRegularFile() ).isTrue();
        assertThat( attrs.readAttributes().creationTime() ).isNotNull();
        assertThat( attrs.readAttributes().lastModifiedTime() ).isNotNull();
        assertThat( attrs.readAttributes().size() ).isEqualTo( 15L );

        try {
            PROVIDER.getFileAttributeView( PROVIDER.getPath( URI.create( "git://user_branch@getfileattriview-test-repo/not_exists.txt" ) ), BasicFileAttributeView.class );
            failBecauseExceptionWasNotThrown( NoSuchFileException.class );
        } catch ( Exception e ) {
        }

        assertThat( PROVIDER.getFileAttributeView( path3, MyInvalidFileAttributeView.class ) ).isNull();

        final Path rootPath = PROVIDER.getPath( URI.create( "git://user_branch@getfileattriview-test-repo/" ) );

        final BasicFileAttributeView attrsRoot = PROVIDER.getFileAttributeView( rootPath, BasicFileAttributeView.class );

        assertThat( attrsRoot.readAttributes().isDirectory() ).isTrue();
        assertThat( attrsRoot.readAttributes().isRegularFile() ).isFalse();
        assertThat( attrsRoot.readAttributes().creationTime() ).isNotNull();
        assertThat( attrsRoot.readAttributes().lastModifiedTime() ).isNotNull();
        assertThat( attrsRoot.readAttributes().size() ).isEqualTo( -1L );
    }

    @Test
    public void testReadAttributes() throws IOException {
        final URI newRepo = URI.create( "git://readattrs-test-repo" );
        PROVIDER.newFileSystem( newRepo, EMPTY_ENV );

        final Path path = PROVIDER.getPath( URI.create( "git://master@readattrs-test-repo/myfile1.txt" ) );

        final OutputStream outStream = PROVIDER.newOutputStream( path );
        outStream.write( "my cool content".getBytes() );
        outStream.close();

        final Path path2 = PROVIDER.getPath( URI.create( "git://user_branch@readattrs-test-repo/other/path/myfile2.txt" ) );

        final OutputStream outStream2 = PROVIDER.newOutputStream( path2 );
        outStream2.write( "my cool content".getBytes() );
        outStream2.close();

        final Path path3 = PROVIDER.getPath( URI.create( "git://user_branch@readattrs-test-repo/myfile3.txt" ) );

        final OutputStream outStream3 = PROVIDER.newOutputStream( path3 );
        outStream3.write( "my cool content".getBytes() );
        outStream3.close();

        final BasicFileAttributes attrs = PROVIDER.readAttributes( path3, BasicFileAttributes.class );

        assertThat( attrs.isDirectory() ).isFalse();
        assertThat( attrs.isRegularFile() ).isTrue();
        assertThat( attrs.creationTime() ).isNotNull();
        assertThat( attrs.lastModifiedTime() ).isNotNull();
        assertThat( attrs.size() ).isEqualTo( 15L );

        try {
            PROVIDER.readAttributes( PROVIDER.getPath( URI.create( "git://user_branch@readattrs-test-repo/not_exists.txt" ) ), BasicFileAttributes.class );
            failBecauseExceptionWasNotThrown( NoSuchFileException.class );
        } catch ( NoSuchFileException e ) {
        }

        assertThat( PROVIDER.readAttributes( path3, MyAttrs.class ) ).isNull();

        final Path rootPath = PROVIDER.getPath( URI.create( "git://user_branch@readattrs-test-repo/" ) );

        final BasicFileAttributes attrsRoot = PROVIDER.readAttributes( rootPath, BasicFileAttributes.class );

        assertThat( attrsRoot.isDirectory() ).isTrue();
        assertThat( attrsRoot.isRegularFile() ).isFalse();
        assertThat( attrsRoot.creationTime() ).isNotNull();
        assertThat( attrsRoot.lastModifiedTime() ).isNotNull();
        assertThat( attrsRoot.size() ).isEqualTo( -1L );
    }

    @Test
    public void testReadAttributesMap() throws IOException {
        final URI newRepo = URI.create( "git://readattrsmap-test-repo" );
        PROVIDER.newFileSystem( newRepo, EMPTY_ENV );

        final Path path = PROVIDER.getPath( URI.create( "git://master@readattrsmap-test-repo/myfile1.txt" ) );

        final OutputStream outStream = PROVIDER.newOutputStream( path );
        outStream.write( "my cool content".getBytes() );
        outStream.close();

        final Path path2 = PROVIDER.getPath( URI.create( "git://user_branch@readattrsmap-test-repo/other/path/myfile2.txt" ) );

        final OutputStream outStream2 = PROVIDER.newOutputStream( path2 );
        outStream2.write( "my cool content".getBytes() );
        outStream2.close();

        final Path path3 = PROVIDER.getPath( URI.create( "git://user_branch@readattrsmap-test-repo/myfile3.txt" ) );

        final OutputStream outStream3 = PROVIDER.newOutputStream( path3 );
        outStream3.write( "my cool content".getBytes() );
        outStream3.close();

        assertThat( PROVIDER.readAttributes( path, "*" ) ).isNotNull().hasSize( 9 );
        assertThat( PROVIDER.readAttributes( path, "basic:*" ) ).isNotNull().hasSize( 9 );
        assertThat( PROVIDER.readAttributes( path, "basic:isRegularFile" ) ).isNotNull().hasSize( 1 );
        assertThat( PROVIDER.readAttributes( path, "basic:isRegularFile,isDirectory" ) ).isNotNull().hasSize( 2 );
        assertThat( PROVIDER.readAttributes( path, "basic:isRegularFile,isDirectory,someThing" ) ).isNotNull().hasSize( 2 );
        assertThat( PROVIDER.readAttributes( path, "basic:someThing" ) ).isNotNull().hasSize( 0 );

        assertThat( PROVIDER.readAttributes( path, "isRegularFile" ) ).isNotNull().hasSize( 1 );
        assertThat( PROVIDER.readAttributes( path, "isRegularFile,isDirectory" ) ).isNotNull().hasSize( 2 );
        assertThat( PROVIDER.readAttributes( path, "isRegularFile,isDirectory,someThing" ) ).isNotNull().hasSize( 2 );
        assertThat( PROVIDER.readAttributes( path, "someThing" ) ).isNotNull().hasSize( 0 );

        try {
            PROVIDER.readAttributes( path, ":someThing" );
            failBecauseExceptionWasNotThrown( IllegalArgumentException.class );
        } catch ( IllegalArgumentException ex ) {
        }

        try {
            PROVIDER.readAttributes( path, "advanced:isRegularFile" );
            failBecauseExceptionWasNotThrown( UnsupportedOperationException.class );
        } catch ( UnsupportedOperationException ex ) {
        }

        final Path rootPath = PROVIDER.getPath( URI.create( "git://user_branch@readattrsmap-test-repo/" ) );

        assertThat( PROVIDER.readAttributes( rootPath, "*" ) ).isNotNull().hasSize( 9 );
        assertThat( PROVIDER.readAttributes( rootPath, "basic:*" ) ).isNotNull().hasSize( 9 );
        assertThat( PROVIDER.readAttributes( rootPath, "basic:isRegularFile" ) ).isNotNull().hasSize( 1 );
        assertThat( PROVIDER.readAttributes( rootPath, "basic:isRegularFile,isDirectory" ) ).isNotNull().hasSize( 2 );
        assertThat( PROVIDER.readAttributes( rootPath, "basic:isRegularFile,isDirectory,someThing" ) ).isNotNull().hasSize( 2 );
        assertThat( PROVIDER.readAttributes( rootPath, "basic:someThing" ) ).isNotNull().hasSize( 0 );

        assertThat( PROVIDER.readAttributes( rootPath, "isRegularFile" ) ).isNotNull().hasSize( 1 );
        assertThat( PROVIDER.readAttributes( rootPath, "isRegularFile,isDirectory" ) ).isNotNull().hasSize( 2 );
        assertThat( PROVIDER.readAttributes( rootPath, "isRegularFile,isDirectory,someThing" ) ).isNotNull().hasSize( 2 );
        assertThat( PROVIDER.readAttributes( rootPath, "someThing" ) ).isNotNull().hasSize( 0 );

        try {
            PROVIDER.readAttributes( rootPath, ":someThing" );
            failBecauseExceptionWasNotThrown( IllegalArgumentException.class );
        } catch ( IllegalArgumentException ex ) {
        }

        try {
            PROVIDER.readAttributes( rootPath, "advanced:isRegularFile" );
            failBecauseExceptionWasNotThrown( UnsupportedOperationException.class );
        } catch ( UnsupportedOperationException ex ) {
        }

        try {
            PROVIDER.readAttributes( PROVIDER.getPath( URI.create( "git://user_branch@readattrsmap-test-repo/not_exists.txt" ) ), BasicFileAttributes.class );
            failBecauseExceptionWasNotThrown( NoSuchFileException.class );
        } catch ( NoSuchFileException e ) {
        }
    }

    @Test
    public void testSetAttribute() throws IOException {
        final URI newRepo = URI.create( "git://setattr-test-repo" );
        PROVIDER.newFileSystem( newRepo, EMPTY_ENV );

        final Path path = PROVIDER.getPath( URI.create( "git://master@setattr-test-repo/myfile1.txt" ) );

        final OutputStream outStream = PROVIDER.newOutputStream( path );
        outStream.write( "my cool content".getBytes() );
        outStream.close();

        final Path path2 = PROVIDER.getPath( URI.create( "git://user_branch@setattr-test-repo/other/path/myfile2.txt" ) );

        final OutputStream outStream2 = PROVIDER.newOutputStream( path2 );
        outStream2.write( "my cool content".getBytes() );
        outStream2.close();

        final Path path3 = PROVIDER.getPath( URI.create( "git://user_branch@setattr-test-repo/myfile3.txt" ) );

        final OutputStream outStream3 = PROVIDER.newOutputStream( path3 );
        outStream3.write( "my cool content".getBytes() );
        outStream3.close();

        try {
            PROVIDER.setAttribute( path3, "basic:isRegularFile", true );
            failBecauseExceptionWasNotThrown( NotImplementedException.class );
        } catch ( NotImplementedException ex ) {
        }

        try {
            PROVIDER.setAttribute( path3, "isRegularFile", true );
            failBecauseExceptionWasNotThrown( NotImplementedException.class );
        } catch ( NotImplementedException ex ) {
        }

        try {
            PROVIDER.setAttribute( path3, "notExisits", true );
            failBecauseExceptionWasNotThrown( IllegalStateException.class );
        } catch ( IllegalStateException ex ) {
        }

        try {
            PROVIDER.setAttribute( path3, "advanced:notExisits", true );
            failBecauseExceptionWasNotThrown( UnsupportedOperationException.class );
        } catch ( UnsupportedOperationException ex ) {
        }

        try {
            PROVIDER.setAttribute( path3, ":isRegularFile", true );
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
