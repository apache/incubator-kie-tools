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

package org.uberfire.java.nio.fs.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.java.nio.base.GeneralPathImpl;
import org.uberfire.java.nio.channels.SeekableByteChannel;
import org.uberfire.java.nio.file.DirectoryNotEmptyException;
import org.uberfire.java.nio.file.DirectoryStream;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.FileSystemAlreadyExistsException;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.NotDirectoryException;
import org.uberfire.java.nio.file.NotLinkException;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static org.fest.assertions.api.Assertions.*;
import static org.uberfire.java.nio.file.StandardDeleteOption.*;

public class SimpleFileSystemProviderTest {

    @Before
    @After
    public void cleanup() {
        new File( System.getProperty( "user.dir" ) + "/temp" ).delete();
        new File( System.getProperty( "user.dir" ) + "/temp2" ).delete();
        new File( System.getProperty( "user.dir" ) + "/xxxxxx" ).delete();
    }

    @Test
    public void simpleStateTest() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        assertThat( fsProvider ).isNotNull();
        assertThat( fsProvider.getScheme() ).isNotEmpty().isEqualTo( "file" );

        assertThat( fsProvider.isDefault() ).isFalse();
        fsProvider.forceAsDefault();
        assertThat( fsProvider.isDefault() ).isTrue();
    }

    @Test
    public void validateGetPath() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();
        final URI uri = URI.create( "file:///path/to/file.txt" );

        final Path path = fsProvider.getPath( uri );

        assertThat( path ).isNotNull();
        assertThat( path.isAbsolute() ).isTrue();
        assertThat( path.getFileSystem() ).isEqualTo( fsProvider.getFileSystem( uri ) );
        assertThat( path.getFileSystem().provider() ).isEqualTo( fsProvider );

        assertThat( path.toString() ).isEqualTo( "/path/to/file.txt" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void getPathNull() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();
        fsProvider.getPath( null );
    }

    @Test(expected = IllegalStateException.class)
    public void getPathInvalidScheme() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();
        fsProvider.getPath( URI.create( "http:///path/to/file.txt" ) );
    }

    @Test(expected = FileSystemAlreadyExistsException.class)
    public void newFileSystemCantCreateURI() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        fsProvider.newFileSystem( URI.create( "file:///" ), new HashMap<String, Object>() );
    }

    @Test(expected = FileSystemAlreadyExistsException.class)
    public void newFileSystemCantCreatePath() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();
        final URI uri = URI.create( "file:///" );
        final Path path = GeneralPathImpl.create( fsProvider.getFileSystem( uri ), uri.getPath(), false );

        fsProvider.newFileSystem( path, new HashMap<String, Object>() );
    }

    @Test
    public void checkNewInputStream() throws IOException {
        final File temp = File.createTempFile( "foo", "bar" );
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final Path path = GeneralPathImpl.newFromFile( fsProvider.getFileSystem( URI.create( "file:///" ) ), temp );

        final InputStream stream = fsProvider.newInputStream( path );

        assertThat( stream ).isNotNull();
        stream.close();
    }

    @Test(expected = NoSuchFileException.class)
    public void inputStreamFileDoesntExists() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final Path path = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), "/path/to/file.txt", false );

        fsProvider.newInputStream( path );
    }

    @Test(expected = IllegalArgumentException.class)
    public void inputStreamNull() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        fsProvider.newInputStream( null );
    }

    @Test
    public void checkNewOutputStream() throws IOException {
        final File temp = File.createTempFile( "foo", "bar" );
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final Path path = GeneralPathImpl.newFromFile( fsProvider.getFileSystem( URI.create( "file:///" ) ), temp );

        final OutputStream stream = fsProvider.newOutputStream( path );

        assertThat( stream ).isNotNull();
        stream.close();
    }

    @Test(expected = org.uberfire.java.nio.IOException.class)
    public void outputStreamFileDoesntExists() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final Path path = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), "/path/to/file.txt", false );

        fsProvider.newOutputStream( path );
    }

    @Test(expected = org.uberfire.java.nio.IOException.class)
    public void outputStreamOnDirectory() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final Path path = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), "/", false );

        fsProvider.newOutputStream( path );
    }

    @Test(expected = IllegalArgumentException.class)
    public void outputStreamNull() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        fsProvider.newOutputStream( null );
    }

    @Test
    public void checkNewFileChannel() throws IOException {
        final File temp = File.createTempFile( "foo", "bar" );
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final Path path = GeneralPathImpl.newFromFile( fsProvider.getFileSystem( URI.create( "file:///" ) ), temp );

        final FileChannel stream = fsProvider.newFileChannel( path, null );

        assertThat( stream ).isNotNull();
        stream.close();
    }

    @Test(expected = org.uberfire.java.nio.IOException.class)
    public void fileChannelFileDoesntExists() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final Path path = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), "/path/to/file.txt", false );

        fsProvider.newFileChannel( path, null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void fileChannelNull() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        fsProvider.newFileChannel( null, null );
    }

    @Test
    public void checkNewByteChannelToCreateFile() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final String userBasedPath = System.getProperty( "user.dir" ) + "/byte_some_file_here.txt";

        final Path path = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), userBasedPath, false );
        assertThat( path.toFile().exists() ).isFalse();

        final SeekableByteChannel channel = fsProvider.newByteChannel( path, null );

        assertThat( channel ).isNotNull();
        assertThat( path.toFile().exists() ).isTrue();
        path.toFile().delete();
        assertThat( path.toFile().exists() ).isFalse();
    }

    @Test(expected = FileAlreadyExistsException.class)
    public void newByteChannelFileAlreadyExists() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final File tempFile = File.createTempFile( "foo", "bar" );
        final Path path = GeneralPathImpl.newFromFile( fsProvider.getFileSystem( URI.create( "file:///" ) ), tempFile );

        assertThat( path.toFile().exists() ).isTrue();
        assertThat( path.toFile() ).isEqualTo( tempFile );

        fsProvider.newByteChannel( path, null );
    }

    @Test(expected = org.uberfire.java.nio.IOException.class)
    public void newByteChannelInvalidPath() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final String userBasedPath = System.getProperty( "user.dir" ) + "path/to/some_file_here.txt";

        final Path path = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), userBasedPath, false );
        assertThat( path.toFile().exists() ).isFalse();

        fsProvider.newByteChannel( path, null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void newByteChannelNull() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        fsProvider.newByteChannel( null, null );
    }

    @Test(expected = UnsupportedOperationException.class)
    public void newAsynchronousFileChannelUnsupportedOp() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final Path path = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), "/path/to/file.txt", false );

        fsProvider.newAsynchronousFileChannel( path, null, null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void newAsynchronousFileChannelNull() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        fsProvider.newAsynchronousFileChannel( null, null, null );
    }

    @Test
    public void seekableByteChannel() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final String userBasedPath = System.getProperty( "user.dir" ) + "/my_byte_some_file_here.txt";

        final Path path = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), userBasedPath, false );
        assertThat( path.toFile().exists() ).isFalse();

        final SeekableByteChannel channel = fsProvider.newByteChannel( path, null );

        assertThat( channel ).isNotNull();
        assertThat( path.toFile().exists() ).isTrue();

        assertThat( channel.isOpen() ).isTrue();

        channel.close();

        assertThat( channel.isOpen() ).isFalse();

//        try {
//            channel.position();
//            fail( "method not implemented - exception expected!" );
//        } catch ( NotImplementedException ex ) {
//        }
//
//        try {
//            channel.position( 1L );
//            fail( "method not implemented - exception expected!" );
//        } catch ( NotImplementedException ex ) {
//        }
//
//        try {
//            channel.size();
//            fail( "method not implemented - exception expected!" );
//        } catch ( NotImplementedException ex ) {
//        }
//
//        try {
//            channel.truncate( 1L );
//            fail( "method not implemented - exception expected!" );
//        } catch ( NotImplementedException ex ) {
//        }
//
//        try {
//            channel.read( null );
//            fail( "method not implemented - exception expected!" );
//        } catch ( NotImplementedException ex ) {
//        }
//
//        try {
//            channel.write( null );
//            fail( "method not implemented - exception expected!" );
//        } catch ( NotImplementedException ex ) {
//        }

        path.toFile().delete();
        assertThat( path.toFile().exists() ).isFalse();
    }

    @Test
    public void checkCreateDirectory() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final String userBasedPath = System.getProperty( "user.dir" ) + "/temp";

        final Path path = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), userBasedPath, false );
        path.toFile().delete();
        assertThat( path.toFile().exists() ).isFalse();

        fsProvider.createDirectory( path );

        assertThat( path.toFile().exists() ).isTrue();
        path.toFile().delete();
        assertThat( path.toFile().exists() ).isFalse();
    }

    @Test(expected = FileAlreadyExistsException.class)
    public void checkCreateDirectoryAlreadyExists() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final String userBasedPath = System.getProperty( "user.dir" ) + "/temp";

        final Path path = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), userBasedPath, false );
        assertThat( path.toFile().exists() ).isFalse();

        fsProvider.createDirectory( path );
        assertThat( path.toFile().exists() ).isTrue();
        fsProvider.createDirectory( path );
    }

    @Test(expected = IllegalArgumentException.class)
    public void createDirectoryNull() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        fsProvider.createDirectory( null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void createSymbolicLinkNull1() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        fsProvider.createSymbolicLink( null, null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void createSymbolicLinkNull2() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final Path path = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), "/path/to/file.txt", false );

        fsProvider.createSymbolicLink( path, null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void createSymbolicLinkNull3() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final Path path = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), "/path/to/file.txt", false );

        fsProvider.createSymbolicLink( null, path );
    }

    @Test(expected = IllegalStateException.class)
    public void createSymbolicLinkSame() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final Path path = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), "/path/to/file.txt", false );

        fsProvider.createSymbolicLink( path, path );
    }

    @Test(expected = IllegalStateException.class)
    public void createSymbolicLinkTargetMustExists() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();
        final Path path = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), "/path/to/file.txt", false );
        final Path path2 = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), "/path/to/file2.txt", false );

        fsProvider.createSymbolicLink( path, path2 );
    }

    @Test(expected = UnsupportedOperationException.class)
    public void createSymbolicLinkUnsupportedOp() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();
        final Path path = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), "/path/to/file.txt", false );

        final File tempFile = File.createTempFile( "foo", "bar" );
        final Path path2 = GeneralPathImpl.newFromFile( fsProvider.getFileSystem( URI.create( "file:///" ) ), tempFile );

        fsProvider.createSymbolicLink( path, path2 );
    }

    @Test(expected = IllegalArgumentException.class)
    public void createLinkNull1() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        fsProvider.createLink( null, null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void createLinkNull2() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final Path path = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), "/path/to/file.txt", false );

        fsProvider.createLink( path, null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void createLinkNull3() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final Path path = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), "/path/to/file.txt", false );

        fsProvider.createLink( null, path );
    }

    @Test(expected = IllegalStateException.class)
    public void createLinkSame() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final Path path = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), "/path/to/file.txt", false );

        fsProvider.createLink( path, path );
    }

    @Test(expected = UnsupportedOperationException.class)
    public void createLinkUnsupportedOp() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();
        final Path path = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), "/path/to/file.txt", false );

        final File tempFile = File.createTempFile( "foo", "bar" );
        final Path path2 = GeneralPathImpl.newFromFile( fsProvider.getFileSystem( URI.create( "file:///" ) ), tempFile );

        fsProvider.createLink( path, path2 );
    }

    @Test
    public void checkDelete() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final File tempFile = File.createTempFile( "foo", "bar" );
        final Path path = GeneralPathImpl.newFromFile( fsProvider.getFileSystem( URI.create( "file:///" ) ), tempFile );

        assertThat( path.toFile().exists() ).isTrue();
        fsProvider.delete( path );
        assertThat( path.toFile().exists() ).isFalse();
    }

    @Test(expected = NoSuchFileException.class)
    public void checkDeleteNonExistent() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final Path path = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), "/path/to/file.txt", false );

        assertThat( path.toFile().exists() ).isFalse();
        fsProvider.delete( path );
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkDeleteNull() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        fsProvider.delete( null );
    }

    @Test
    public void checkDeleteIfExists() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final File tempFile = File.createTempFile( "foo", "bar" );
        final Path path = GeneralPathImpl.newFromFile( fsProvider.getFileSystem( URI.create( "file:///" ) ), tempFile );

        assertThat( path.toFile().exists() ).isTrue();
        assertThat( fsProvider.deleteIfExists( path ) ).isTrue();
        assertThat( path.toFile().exists() ).isFalse();
    }

    @Test
    public void checkDeleteIfExistsNonExistent() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final Path path = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), "/path/to/file.txt", false );

        assertThat( path.toFile().exists() ).isFalse();
        assertThat( fsProvider.deleteIfExists( path ) ).isFalse();
        assertThat( path.toFile().exists() ).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkDeleteIfExistsNull() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        fsProvider.deleteIfExists( null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void readSymbolicLinkNull() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        fsProvider.readSymbolicLink( null );
    }

    @Test(expected = NotLinkException.class)
    public void readSymbolicLinkNotLink() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();
        final Path path = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), "/path/to/file.txt", false );

        fsProvider.readSymbolicLink( path );
    }

    @Test(expected = UnsupportedOperationException.class)
    public void readSymbolicLinkUnsupportedOp() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final File tempFile = File.createTempFile( "foo", "bar" );
        final Path path = GeneralPathImpl.newFromFile( fsProvider.getFileSystem( URI.create( "file:///" ) ), tempFile );

        fsProvider.readSymbolicLink( path );
    }

    @Test
    public void checkIsSameFile() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();
        final Path path = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), "/path/to/file.txt", false );

        final Path path2 = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), "/path/to/file.txt", false );
        assertThat( fsProvider.isSameFile( path, path2 ) ).isTrue();

        final Path path3 = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), "path/to/file.txt", false );
        assertThat( fsProvider.isSameFile( path, path3 ) ).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void sameFileNull1() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        fsProvider.isSameFile( null, null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void sameFileNull2() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();
        final Path path = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), "/path/to/file.txt", false );

        fsProvider.isSameFile( path, null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void sameFileNull3() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();
        final Path path = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), "/path/to/file.txt", false );

        fsProvider.isSameFile( null, path );
    }

    @Test
    public void checkCopyDir() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final String userSourcePath = System.getProperty( "user.dir" ) + "/temp";
        final String userDestPath = System.getProperty( "user.dir" ) + "/temp2";

        final Path source = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), userSourcePath, false );
        final Path dest = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), userDestPath, false );
        fsProvider.createDirectory( source );

        fsProvider.copy( source, dest );

        assertThat( dest.toFile().exists() ).isTrue();
        assertThat( source.toFile().exists() ).isTrue();

        source.toFile().delete();
        dest.toFile().delete();
    }

    @Test
    public void checkCopyFile() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final String userSourcePath = System.getProperty( "user.dir" ) + "/temp.txt";
        final String userDestPath = System.getProperty( "user.dir" ) + "/temp2.txt";

        final Path source = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), userSourcePath, false );
        final Path dest = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), userDestPath, false );
        final OutputStream stream = fsProvider.newOutputStream( source );
        stream.write( 'a' );
        stream.close();

        fsProvider.copy( source, dest );

        assertThat( dest.toFile().exists() ).isTrue();
        assertThat( source.toFile().exists() ).isTrue();
        assertThat( dest.toFile().length() ).isEqualTo( source.toFile().length() );

        source.toFile().delete();
        dest.toFile().delete();
    }

    @Test
    public void copyFileInvalidSourceAndTarget() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final String userSourcePath = System.getProperty( "user.dir" ) + "/temp";
        final String userDestPath = System.getProperty( "user.dir" ) + "/temp2";

        final Path source = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), userSourcePath, false );
        final Path dest = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), userDestPath, false );

        fsProvider.createDirectory( source );

        final Path sourceFile = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), userSourcePath + "/file.txt", false );
        final OutputStream stream = fsProvider.newOutputStream( sourceFile );
        stream.write( 'a' );
        stream.close();

        try {
            fsProvider.copy( source, dest );
            fail( "source isn't empty" );
        } catch ( Exception ex ) {
        }

        sourceFile.toFile().delete();
        fsProvider.copy( source, dest );

        try {
            fsProvider.copy( source, dest );
            fail( "dest already exists" );
        } catch ( Exception ex ) {
        }

        dest.toFile().delete();
        source.toFile().delete();

        try {
            fsProvider.copy( source, dest );
            fail( "source doesn't exists" );
        } catch ( Exception ex ) {

        } finally {
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void copyFileNull1() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final String userPath = System.getProperty( "user.dir" ) + "/temp";
        final Path path = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), userPath, false );

        fsProvider.copy( path, null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void copyFileNull2() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final String userPath = System.getProperty( "user.dir" ) + "/temp";
        final Path path = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), userPath, false );

        fsProvider.copy( null, path );
    }

    @Test(expected = IllegalArgumentException.class)
    public void copyFileNull3() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        fsProvider.copy( null, null );
    }

    @Test
    public void checkMoveDir() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final String userSourcePath = System.getProperty( "user.dir" ) + "/temp";
        final String userDestPath = System.getProperty( "user.dir" ) + "/temp2";

        final Path source = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), userSourcePath, false );
        final Path dest = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), userDestPath, false );
        fsProvider.createDirectory( source );

        fsProvider.move( source, dest );

        assertThat( source.toFile().exists() ).isFalse();
        assertThat( dest.toFile().exists() ).isTrue();

        dest.toFile().delete();
    }

    @Test
    public void checkMoveFile() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final String userSourcePath = System.getProperty( "user.dir" ) + "/temp.txt";
        final String userDestPath = System.getProperty( "user.dir" ) + "/temp2.txt";

        final Path source = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), userSourcePath, false );
        final Path dest = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), userDestPath, false );
        final OutputStream stream = fsProvider.newOutputStream( source );
        stream.write( 'a' );
        stream.close();

        long lenght = source.toFile().length();
        fsProvider.move( source, dest );

        assertThat( dest.toFile().exists() ).isTrue();
        assertThat( source.toFile().exists() ).isFalse();
        assertThat( dest.toFile().length() ).isEqualTo( lenght );

        dest.toFile().delete();
    }

    @Test
    public void moveFileInvalidSourceAndTarget() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final String userSourcePath = System.getProperty( "user.dir" ) + "/temp";
        final String userDestPath = System.getProperty( "user.dir" ) + "/temp2";

        final Path source = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), userSourcePath, false );
        final Path dest = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), userDestPath, false );

        fsProvider.createDirectory( source );

        final Path sourceFile = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), userSourcePath + "/file.txt", false );
        final OutputStream stream = fsProvider.newOutputStream( sourceFile );
        stream.write( 'a' );
        stream.close();

        try {
            fsProvider.move( source, dest );
            fail( "source isn't empty" );
        } catch ( Exception ex ) {
        }

        sourceFile.toFile().delete();
        fsProvider.copy( source, dest );

        try {
            fsProvider.move( source, dest );
            fail( "dest already exists" );
        } catch ( Exception ex ) {
        }

        dest.toFile().delete();
        source.toFile().delete();

        try {
            fsProvider.move( source, dest );
            fail( "source doesn't exists" );
        } catch ( Exception ex ) {

        } finally {
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void moveFileNull1() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final String userPath = System.getProperty( "user.dir" ) + "/temp";
        final Path path = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), userPath, false );

        fsProvider.move( path, null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void moveFileNull2() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final String userPath = System.getProperty( "user.dir" ) + "/temp";
        final Path path = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), userPath, false );

        fsProvider.move( null, path );
    }

    @Test(expected = IllegalArgumentException.class)
    public void moveFileNull3() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        fsProvider.move( null, null );
    }

    @Test
    public void checkNewDirectoryStream() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final String userSourcePath = System.getProperty( "user.dir" ) + "/temp";

        final Path dir = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), userSourcePath, false );
        FileUtils.deleteDirectory( dir.toFile() );
        fsProvider.createDirectory( dir );

        final DirectoryStream<Path> stream = fsProvider.newDirectoryStream( dir, new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept( Path entry ) throws org.uberfire.java.nio.IOException {
                return true;
            }
        } );

        assertThat( stream ).hasSize( 0 );

        try {
            stream.iterator().next();
            fail( "can't navigate to next on empty iterator" );
        } catch ( NoSuchElementException ex ) {
        }

        final File tempFile = File.createTempFile( "foo", "bar", dir.toFile() );
        final Path path = GeneralPathImpl.newFromFile( fsProvider.getFileSystem( URI.create( "file:///" ) ), tempFile );

        final DirectoryStream<Path> stream2 = fsProvider.newDirectoryStream( dir, new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept( Path entry ) throws org.uberfire.java.nio.IOException {
                return true;
            }
        } );

        assertThat( stream2 ).hasSize( 1 );

        final Iterator<Path> iterator = stream2.iterator();
        iterator.next();
        try {
            iterator.remove();
            fail( "can't remove elements" );
        } catch ( UnsupportedOperationException ex ) {
        }

        stream2.close();

        try {
            stream2.close();
            fail( "stram already closed" );
        } catch ( Exception ex ) {
        }

        final File tempFile2 = File.createTempFile( "bar", "foo", dir.toFile() );
        final Path path2 = GeneralPathImpl.newFromFile( fsProvider.getFileSystem( URI.create( "file:///" ) ), tempFile2 );

        final DirectoryStream<Path> stream3 = fsProvider.newDirectoryStream( dir, new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept( Path entry ) throws org.uberfire.java.nio.IOException {
                return true;
            }
        } );

        assertThat( stream3 ).hasSize( 2 ).contains( path, path2 );

        stream3.close();

        try {
            stream3.iterator().next();
            fail( "can't interact in an already closed stream" );
        } catch ( Exception ex ) {
        }

        final DirectoryStream<Path> stream4 = fsProvider.newDirectoryStream( dir, new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept( final Path entry ) throws org.uberfire.java.nio.IOException {
                if ( entry.getFileName().toString().startsWith( "foo" ) ) {
                    return true;
                }
                return false;
            }
        } );

        assertThat( stream4 ).hasSize( 1 ).contains( path );

        FileUtils.deleteDirectory( dir.toFile() );
    }

    @Test
    public void checkDeleteNonEmptyDir() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final String userSourcePath = System.getProperty( "user.dir" ) + "/temp";

        final Path dir = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), userSourcePath, false );
        FileUtils.deleteDirectory( dir.toFile() );
        fsProvider.createDirectory( dir );

        File.createTempFile( "foo", "bar", dir.toFile() );
        File.createTempFile( "bar", "foo", dir.toFile() );
        File.createTempFile( "bar", "foo", dir.toFile() );
        fsProvider.createDirectory( dir.resolve( "other_dir" ) );

        final DirectoryStream<Path> stream5 = fsProvider.newDirectoryStream( dir, new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept( final Path entry ) throws org.uberfire.java.nio.IOException {
                return true;
            }
        } );

        assertThat( stream5 ).hasSize( 4 ).contains( dir.resolve( "other_dir" ) );

        try {
            fsProvider.delete( dir );
            fail( "must throw error" );
        } catch ( final DirectoryNotEmptyException expection ) {
        }

        fsProvider.delete( dir, NON_EMPTY_DIRECTORIES );

        assertThat( dir.toFile().exists() ).isEqualTo( false );
    }

    @Test(expected = NotDirectoryException.class)
    public void newDirectoryStreamInvalidDir() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final String userSourcePath = System.getProperty( "user.dir" ) + "/xxxxxx";

        final Path dir = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), userSourcePath, false );

        fsProvider.newDirectoryStream( dir, new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept( Path entry ) throws org.uberfire.java.nio.IOException {
                return true;
            }
        } );
    }

    @Test(expected = IllegalArgumentException.class)
    public void newDirectoryStreamNull1() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        fsProvider.newDirectoryStream( null, new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept( Path entry ) throws org.uberfire.java.nio.IOException {
                return true;
            }
        } );
    }

    @Test(expected = IllegalArgumentException.class)
    public void newDirectoryStreamNull2() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final String userSourcePath = System.getProperty( "user.dir" ) + "/xxxxxx";

        final Path dir = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), userSourcePath, false );

        fsProvider.newDirectoryStream( dir, null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void newDirectoryStreamNull3() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final String userSourcePath = System.getProperty( "user.dir" ) + "/xxxxxx";

        final Path dir = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), userSourcePath, false );

        fsProvider.newDirectoryStream( null, null );
    }
}