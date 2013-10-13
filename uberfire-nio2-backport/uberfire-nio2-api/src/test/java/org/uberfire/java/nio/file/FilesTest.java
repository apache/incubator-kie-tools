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

package org.uberfire.java.nio.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.junit.Test;
import org.uberfire.java.nio.base.BasicFileAttributesImpl;
import org.uberfire.java.nio.base.NotImplementedException;
import org.uberfire.java.nio.channels.SeekableByteChannel;
import org.uberfire.java.nio.file.attribute.BasicFileAttributeView;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.java.nio.fs.file.BaseSimpleFileStore;
import org.uberfire.java.nio.fs.jgit.JGitFileStore;

import static org.fest.assertions.api.Assertions.*;

public class FilesTest extends AbstractBaseTest {

    @Test
    public void newIOStreams() throws IOException {
        final Path dir = newTempDir();

        final OutputStream out = Files.newOutputStream( dir.resolve( "file.txt" ) );
        assertThat( out ).isNotNull();

        out.write( "content".getBytes() );
        out.close();

        final InputStream in = Files.newInputStream( dir.resolve( "file.txt" ) );

        assertThat( in ).isNotNull();

        final StringBuilder sb = new StringBuilder();
        while ( true ) {
            int i = in.read();
            if ( i == -1 ) {
                break;
            }
            sb.append( (char) i );
        }
        assertThat( sb.toString() ).isEqualTo( "content" );
    }

    @Test(expected = NoSuchFileException.class)
    public void newInputStreamNonExistent() {
        Files.newInputStream( Paths.get( "/path/to/some/file.txt" ) );
    }

    @Test(expected = NoSuchFileException.class)
    public void newInputStreamOnDir() {
        final Path dir = newTempDir();
        Files.newInputStream( dir );
    }

    @Test(expected = IllegalArgumentException.class)
    public void newInputStreamNull() {
        Files.newInputStream( null );
    }

    @Test(expected = org.uberfire.java.nio.IOException.class)
    public void newOutputStreamOnExistent() {
        final Path dir = newTempDir();
        Files.newOutputStream( dir );
    }

    @Test(expected = IllegalArgumentException.class)
    public void newOutpurStreamNull() {
        Files.newOutputStream( null );
    }

    @Test
    public void newByteChannel() throws IOException {
        final SeekableByteChannel sbc = Files.newByteChannel( newTempDir().resolve( "file.temp.txt" ), new HashSet<OpenOption>() );
        assertThat( sbc ).isNotNull();
        sbc.close();

        final SeekableByteChannel sbc2 = Files.newByteChannel( newTempDir().resolve( "file.temp2.txt" ) );
        assertThat( sbc ).isNotNull();
        sbc.close();
    }

    @Test(expected = FileAlreadyExistsException.class)
    public void newByteChannelFileAlreadyExists() {
        Files.newByteChannel( Files.createTempFile( "foo", "bar" ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void newByteChannelNull() {
        Files.newByteChannel( null );
    }

    @Test
    public void createFile() throws IOException {
        final Path path = Files.createFile( newTempDir().resolve( "file.temp.txt" ) );

        assertThat( path ).isNotNull();
        assertThat( path.toFile().exists() ).isTrue();
    }

    @Test(expected = FileAlreadyExistsException.class)
    public void createFileAlreadyExists() {
        Files.createFile( Files.createTempFile( "foo", "bar" ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void createFileNull() {
        Files.createFile( null );
    }

    @Test
    public void createDirectory() {
        final Path path = newTempDir();

        final Path dir = Files.createDirectory( path.resolve( "myNewDir" ) );

        assertThat( dir ).isNotNull();
        assertThat( dir.toFile().exists() ).isTrue();
        assertThat( dir.toFile().isDirectory() ).isTrue();

        final Path file = Files.createFile( dir.resolve( "new.file.txt" ) );
        assertThat( file ).isNotNull();
        assertThat( file.toFile().exists() ).isTrue();
        assertThat( file.toFile().isDirectory() ).isFalse();
        assertThat( file.toFile().isFile() ).isTrue();
    }

    @Test(expected = FileAlreadyExistsException.class)
    public void createDirectoryFileAlreadyExists() {
        Files.createDirectory( newTempDir() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void createDirectoryNull() {
        Files.createDirectory( null );
    }

    @Test
    public void createDirectories() {
        final Path path = newTempDir();

        final Path dir = Files.createDirectories( path.resolve( "myNewDir/mysubDir1/mysubDir2" ) );

        assertThat( dir ).isNotNull();
        assertThat( dir.toFile().exists() ).isTrue();
        assertThat( dir.toFile().isDirectory() ).isTrue();

        final Path file = Files.createFile( dir.resolve( "new.file.txt" ) );
        assertThat( file ).isNotNull();
        assertThat( file.toFile().exists() ).isTrue();
        assertThat( file.toFile().isDirectory() ).isFalse();
        assertThat( file.toFile().isFile() ).isTrue();
    }

    @Test(expected = FileAlreadyExistsException.class)
    public void createDirectoriesFileAlreadyExists() {
        Files.createDirectories( newTempDir() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void createDirectoriesNull() {
        Files.createDirectories( null );
    }

    @Test
    public void delete() {
        final Path path = Files.createFile( newTempDir().resolve( "file.temp.txt" ) );

        assertThat( path ).isNotNull();
        assertThat( path.toFile().exists() ).isTrue();

        Files.delete( path );

        assertThat( path ).isNotNull();
        assertThat( path.toFile().exists() ).isFalse();

        final Path dir = newTempDir();

        assertThat( dir ).isNotNull();
        assertThat( dir.toFile().exists() ).isTrue();

        Files.delete( dir );

        assertThat( dir ).isNotNull();
        assertThat( dir.toFile().exists() ).isFalse();
    }

    @Test(expected = DirectoryNotEmptyException.class)
    public void deleteDirectoryNotEmpty() {
        final Path dir = newTempDir();
        Files.createFile( dir.resolve( "file.temp.txt" ) );

        Files.delete( dir );
    }

    @Test(expected = NoSuchFileException.class)
    public void deleteNoSuchFileException() {
        Files.delete( newTempDir().resolve( "file.temp.txt" ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteNull() {
        Files.delete( null );
    }

    @Test
    public void deleteIfExists() {
        final Path path = Files.createFile( newTempDir().resolve( "file.temp.txt" ) );

        assertThat( path ).isNotNull();
        assertThat( path.toFile().exists() ).isTrue();

        assertThat( Files.deleteIfExists( path ) ).isTrue();

        assertThat( path ).isNotNull();
        assertThat( path.toFile().exists() ).isFalse();

        final Path dir = newTempDir();

        assertThat( dir ).isNotNull();
        assertThat( dir.toFile().exists() ).isTrue();

        assertThat( Files.deleteIfExists( dir ) ).isTrue();

        assertThat( dir ).isNotNull();
        assertThat( dir.toFile().exists() ).isFalse();

        assertThat( Files.deleteIfExists( newTempDir().resolve( "file.temp.txt" ) ) ).isFalse();
    }

    @Test(expected = DirectoryNotEmptyException.class)
    public void deleteIfExistsDirectoryNotEmpty() {
        final Path dir = newTempDir();
        Files.createFile( dir.resolve( "file.temp.txt" ) );

        Files.deleteIfExists( dir );
    }

    @Test(expected = IllegalArgumentException.class)
    public void deleteIfExistsNull() {
        Files.deleteIfExists( null );
    }

    @Test
    public void createTempFile() {
        final Path tempFile = Files.createTempFile( null, null );
        assertThat( tempFile ).isNotNull();
        assertThat( tempFile.getFileName().toString() ).endsWith( "tmp" );
        assertThat( tempFile.toFile().exists() ).isTrue();

        final Path tempFile2 = Files.createTempFile( "", "" );
        assertThat( tempFile2 ).isNotNull();
        assertThat( tempFile2.getFileName().toString() ).endsWith( "tmp" );
        assertThat( tempFile2.toFile().exists() ).isTrue();

        final Path tempFile3 = Files.createTempFile( "foo", "bar" );
        assertThat( tempFile3 ).isNotNull();
        assertThat( tempFile3.toFile().exists() ).isTrue();
        assertThat( tempFile3.getFileName().toString() ).startsWith( "foo" ).endsWith( ".bar" );

        final Path tempFile4 = Files.createTempFile( "", "bar" );
        assertThat( tempFile4 ).isNotNull();
        assertThat( tempFile4.toFile().exists() ).isTrue();
        assertThat( tempFile4.getFileName().toString() ).endsWith( ".bar" );

        final Path tempFile5 = Files.createTempFile( "", ".bar" );
        assertThat( tempFile5 ).isNotNull();
        assertThat( tempFile5.toFile().exists() ).isTrue();
        assertThat( tempFile5.getFileName().toString() ).endsWith( ".bar" );

        final Path tempFile6 = Files.createTempFile( "", "bar.temp" );
        assertThat( tempFile6 ).isNotNull();
        assertThat( tempFile6.toFile().exists() ).isTrue();
        assertThat( tempFile6.getFileName().toString() ).endsWith( ".bar.temp" );

        final Path tempFile7 = Files.createTempFile( "", ".bar.temp" );
        assertThat( tempFile7 ).isNotNull();
        assertThat( tempFile7.toFile().exists() ).isTrue();
        assertThat( tempFile7.getFileName().toString() ).endsWith( ".bar.temp" );
    }

    @Test
    public void createTempFileInsideDir() {
        final Path dir = newTempDir();

        assertThat( dir.toFile().list() ).isNotNull().isEmpty();

        final Path tempFile = Files.createTempFile( dir, null, null );

        assertThat( tempFile ).isNotNull();
        assertThat( tempFile.getFileName().toString() ).endsWith( "tmp" );
        assertThat( tempFile.toFile().exists() ).isTrue();

        assertThat( dir.toFile().list() ).isNotNull().isNotEmpty();
    }

    @Test(expected = NoSuchFileException.class)
    public void createTempFileNoSuchFile() {
        Files.createTempFile( Paths.get( "/path/to/" ), null, null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTempFileNull() {
        Files.createTempFile( (Path) null, null, null );
    }

    @Test
    public void createTempDirectory() {
        final Path tempFile = Files.createTempDirectory( null );
        assertThat( tempFile ).isNotNull();
        assertThat( tempFile.toFile().exists() ).isTrue();
        assertThat( tempFile.toFile().isDirectory() ).isTrue();

        final Path tempFile2 = Files.createTempDirectory( "" );
        assertThat( tempFile2 ).isNotNull();
        assertThat( tempFile2.toFile().exists() ).isTrue();
        assertThat( tempFile2.toFile().isDirectory() ).isTrue();

        final Path tempFile3 = Files.createTempDirectory( "foo" );
        assertThat( tempFile3 ).isNotNull();
        assertThat( tempFile3.toFile().exists() ).isTrue();
        assertThat( tempFile3.getFileName().toString() ).startsWith( "foo" );
        assertThat( tempFile3.toFile().isDirectory() ).isTrue();
    }

    @Test
    public void createTempDirectoryInsideDir() {
        final Path dir = newTempDir();

        assertThat( dir.toFile().list() ).isNotNull().isEmpty();

        final Path tempFile = Files.createTempDirectory( dir, null );

        assertThat( tempFile ).isNotNull();
        assertThat( tempFile.toFile().exists() ).isTrue();
        assertThat( tempFile.toFile().isDirectory() ).isTrue();

        assertThat( dir.toFile().list() ).isNotNull().isNotEmpty();
    }

    @Test(expected = NoSuchFileException.class)
    public void createTempDirectoryNoSuchFile() {
        Files.createTempDirectory( Paths.get( "/path/to/" ), null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void createTempDirectoryNull() {
        Files.createTempDirectory( (Path) null, null );
    }

    @Test
    public void copyDir() {
        final Path source = newTempDir();
        final Path dest = newDirToClean();

        assertThat( source.toFile().exists() ).isTrue();
        assertThat( dest.toFile().exists() ).isFalse();

        Files.copy( source, dest );

        assertThat( dest.toFile().exists() ).isTrue();
        assertThat( source.toFile().exists() ).isTrue();
    }

    @Test(expected = DirectoryNotEmptyException.class)
    public void copyDirDirectoryNotEmptyException() {
        final Path source = newTempDir();
        final Path dest = newDirToClean();
        Files.createTempFile( source, "foo", "bar" );

        Files.copy( source, dest );
    }

    @Test
    public void copyFile() throws IOException {
        final Path dir = newTempDir();

        final Path source = dir.resolve( "temp.txt" );
        final Path dest = dir.resolve( "result.txt" );

        final OutputStream stream = Files.newOutputStream( source );
        stream.write( 'a' );
        stream.close();

        Files.copy( source, dest );

        assertThat( dest.toFile().exists() ).isTrue();
        assertThat( source.toFile().exists() ).isTrue();
        assertThat( dest.toFile().length() ).isEqualTo( source.toFile().length() );
    }

    @Test
    public void copyFileInvalidSourceAndTarget() throws IOException {
        final Path source = newTempDir();
        final Path dest = newTempDir().resolve( "other" );

        final Path sourceFile = source.resolve( "file.txt" );
        final OutputStream stream = Files.newOutputStream( sourceFile );
        stream.write( 'a' );
        stream.close();

        try {
            Files.copy( source, dest );
            fail( "source isn't empty" );
        } catch ( Exception ex ) {
        }

        sourceFile.toFile().delete();
        Files.copy( source, dest );

        try {
            Files.copy( source, dest );
            fail( "dest already exists" );
        } catch ( Exception ex ) {
        }

        dest.toFile().delete();
        source.toFile().delete();

        try {
            Files.copy( source, dest );
            fail( "source doesn't exists" );
        } catch ( Exception ex ) {

        } finally {
        }
    }

    @Test(expected = UnsupportedOperationException.class)
    public void copyDifferentProviders() {
        final Map<String, Object> env = new HashMap<String, Object>( 2 );
        env.put( "userName", "user" );
        env.put( "password", "pass" );
        final URI uri = URI.create( "git://test" + System.currentTimeMillis() );
        FileSystems.newFileSystem( uri, env );

        Files.copy( Paths.get( uri ), newTempDir() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void copyNull1() throws IOException {
        Files.copy( newTempDir(), (Path) null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void copyNull2() throws IOException {
        Files.copy( (Path) null, Paths.get( "/temp" ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void copyNull3() throws IOException {
        Files.copy( (Path) null, (Path) null );
    }

    @Test
    public void moveDir() {
        final Path source = newTempDir();
        final Path dest = newTempDir().resolve( "other" );

        Files.move( source, dest );

        assertThat( source.toFile().exists() ).isFalse();
        assertThat( dest.toFile().exists() ).isTrue();
    }

    @Test
    public void moveFile() throws IOException {
        final Path dir = newTempDir();
        final Path source = dir.resolve( "fileSource.txt" );
        final Path dest = dir.resolve( "fileDest.txt" );
        final OutputStream stream = Files.newOutputStream( source );
        stream.write( 'a' );
        stream.close();

        long lenght = source.toFile().length();
        Files.move( source, dest );

        assertThat( dest.toFile().exists() ).isTrue();
        assertThat( source.toFile().exists() ).isFalse();
        assertThat( dest.toFile().length() ).isEqualTo( lenght );
    }

    @Test
    public void moveFileInvalidSourceAndTarget() throws IOException {
        final Path source = newTempDir();
        final Path dest = newTempDir().resolve( "other" );

        final Path sourceFile = source.resolve( "file.txt" );
        final OutputStream stream = Files.newOutputStream( sourceFile );
        stream.write( 'a' );
        stream.close();

        try {
            Files.move( source, dest );
            fail( "source isn't empty" );
        } catch ( Exception ex ) {
        }

        sourceFile.toFile().delete();
        Files.copy( source, dest );

        try {
            Files.move( source, dest );
            fail( "dest already exists" );
        } catch ( Exception ex ) {
        }

        dest.toFile().delete();
        source.toFile().delete();

        try {
            Files.move( source, dest );
            fail( "source doesn't exists" );
        } catch ( Exception ex ) {

        } finally {
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void moveNull1() throws IOException {
        Files.move( newTempDir(), null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void moveNull2() throws IOException {
        Files.move( null, newTempDir() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void moveNull3() throws IOException {
        Files.move( null, null );
    }

    @Test(expected = UnsupportedOperationException.class)
    public void moveDifferentProviders() {
        final Map<String, Object> env = new HashMap<String, Object>( 2 );
        env.put( "userName", "user" );
        env.put( "password", "pass" );
        FileSystems.newFileSystem( URI.create( "git://testXXXXXXX" ), env );

        Files.move( Paths.get( URI.create( "git://testXXXXXXX" ) ), newTempDir() );
    }

    @Test
    public void getFileStore() {
        assertThat( Files.getFileStore( Paths.get( "/some/file" ) ) ).isNotNull().isInstanceOf( BaseSimpleFileStore.class );

        final Map<String, Object> env = new HashMap<String, Object>( 2 );
        env.put( "userName", "user" );
        env.put( "password", "pass" );
        final String repoName = "git://testXXXXXXX" + System.currentTimeMillis();
        final URI uri = URI.create( repoName );
        FileSystems.newFileSystem( uri, env );

        assertThat( Files.getFileStore( Paths.get( uri ) ) ).isNotNull().isInstanceOf( JGitFileStore.class );

        final URI fetch = URI.create( repoName + "?fetch" );
        FileSystems.getFileSystem( fetch );
    }

    @Test(expected = IllegalArgumentException.class)
    public void getFileStoreNull() {
        Files.getFileStore( null );
    }

    @Test(expected = FileSystemNotFoundException.class)
    public void getFileStoreN() {
        final URI uri = URI.create( "nothing:///testXXXXXXX" );

        Files.getFileStore( Paths.get( uri ) );
    }

    @Test
    public void getFileAttributeViewGeneral() throws IOException {
        final Path path = Files.createTempFile( null, null );

        final BasicFileAttributeView view = Files.getFileAttributeView( path, BasicFileAttributeView.class );
        assertThat( view ).isNotNull();
        assertThat( view.readAttributes() ).isNotNull();
        assertThat( view.readAttributes().isRegularFile() ).isTrue();
        assertThat( view.readAttributes().isDirectory() ).isFalse();
        assertThat( view.readAttributes().isSymbolicLink() ).isFalse();
        assertThat( view.readAttributes().isOther() ).isFalse();
        assertThat( view.readAttributes().size() ).isEqualTo( 0L );
    }

    @Test
    public void getFileAttributeViewBasic() throws IOException {
        final Path path = Files.createTempFile( null, null );

        final BasicFileAttributeView view = Files.getFileAttributeView( path, BasicFileAttributeView.class );
        assertThat( view ).isNotNull();
        assertThat( view.readAttributes() ).isNotNull();
        assertThat( view.readAttributes().isRegularFile() ).isTrue();
        assertThat( view.readAttributes().isDirectory() ).isFalse();
        assertThat( view.readAttributes().isSymbolicLink() ).isFalse();
        assertThat( view.readAttributes().isOther() ).isFalse();
        assertThat( view.readAttributes().size() ).isEqualTo( 0L );
    }

    @Test
    public void getFileAttributeViewInvalidView() throws IOException {
        final Path path = Files.createTempFile( "foo", "bar" );

        assertThat( Files.getFileAttributeView( path, MyAttrsView.class ) ).isNull();
    }

    @Test(expected = NoSuchFileException.class)
    public void getFileAttributeViewNoSuchFileException() throws IOException {
        final Path path = Paths.get( "/path/to/file.txt" );

        Files.getFileAttributeView( path, BasicFileAttributeView.class );
    }

    @Test(expected = IllegalArgumentException.class)
    public void getFileAttributeViewNull1() throws IOException {
        Files.getFileAttributeView( null, MyAttrsView.class );
    }

    @Test(expected = IllegalArgumentException.class)
    public void getFileAttributeViewNull2() throws IOException {
        final Path path = Paths.get( "/path/to/file.txt" );
        Files.getFileAttributeView( path, null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void getFileAttributeViewNull3() throws IOException {
        Files.getFileAttributeView( null, null );
    }

    @Test
    public void readAttributesGeneral() throws IOException {
        final Path path = Files.createTempFile( "foo", "bar" );

        final BasicFileAttributesImpl attrs = Files.readAttributes( path, BasicFileAttributesImpl.class );
        assertThat( attrs ).isNotNull();
        assertThat( attrs.isRegularFile() ).isTrue();
        assertThat( attrs.isDirectory() ).isFalse();
        assertThat( attrs.isSymbolicLink() ).isFalse();
        assertThat( attrs.isOther() ).isFalse();
        assertThat( attrs.size() ).isEqualTo( 0L );
    }

    @Test
    public void readAttributesBasic() throws IOException {
        final Path path = Files.createTempFile( "foo", "bar" );

        final BasicFileAttributes attrs = Files.readAttributes( path, BasicFileAttributes.class );

        assertThat( attrs ).isNotNull();
        assertThat( attrs.isRegularFile() ).isTrue();
        assertThat( attrs.isDirectory() ).isFalse();
        assertThat( attrs.isSymbolicLink() ).isFalse();
        assertThat( attrs.isOther() ).isFalse();
        assertThat( attrs.size() ).isEqualTo( 0L );
    }

    @Test(expected = NoSuchFileException.class)
    public void readAttributesNonExistentFile() throws IOException {
        final Path path = Paths.get( "/path/to/file.txt" );

        Files.readAttributes( path, BasicFileAttributes.class );
    }

    @Test
    public void readAttributesInvalid() throws IOException {
        final Path path = Files.createTempFile( "foo", "bar" );

        assertThat( Files.readAttributes( path, MyAttrs.class ) ).isNull();
    }

    @Test(expected = IllegalArgumentException.class)
    public void readAttributesNull1() throws IOException {
        Files.readAttributes( null, MyAttrs.class );
    }

    @Test(expected = IllegalArgumentException.class)
    public void readAttributesNull2() throws IOException {
        final Path path = Paths.get( "/path/to/file.txt" );
        Files.readAttributes( path, (Class<MyAttrs>) null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void readAttributesNull3() throws IOException {
        Files.readAttributes( null, (Class<MyAttrs>) null );
    }

    @Test
    public void readAttributesMap() throws IOException {
        final Path path = Files.createTempFile( "foo", "bar" );

        assertThat( Files.readAttributes( path, "*" ) ).isNotNull().hasSize( 9 );
        assertThat( Files.readAttributes( path, "basic:*" ) ).isNotNull().hasSize( 9 );
        assertThat( Files.readAttributes( path, "basic:isRegularFile" ) ).isNotNull().hasSize( 1 );
        assertThat( Files.readAttributes( path, "basic:isRegularFile,isDirectory" ) ).isNotNull().hasSize( 2 );
        assertThat( Files.readAttributes( path, "basic:isRegularFile,isDirectory,someThing" ) ).isNotNull().hasSize( 2 );
        assertThat( Files.readAttributes( path, "basic:someThing" ) ).isNotNull().hasSize( 0 );

        assertThat( Files.readAttributes( path, "isRegularFile" ) ).isNotNull().hasSize( 1 );
        assertThat( Files.readAttributes( path, "isRegularFile,isDirectory" ) ).isNotNull().hasSize( 2 );
        assertThat( Files.readAttributes( path, "isRegularFile,isDirectory,someThing" ) ).isNotNull().hasSize( 2 );
        assertThat( Files.readAttributes( path, "someThing" ) ).isNotNull().hasSize( 0 );

        try {
            Files.readAttributes( path, ":someThing" );
            fail( "undefined view" );
        } catch ( IllegalArgumentException ex ) {
        }

        try {
            Files.readAttributes( path, "advanced:isRegularFile" );
            fail( "undefined view" );
        } catch ( UnsupportedOperationException ex ) {
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void readAttributesMapNull1() throws IOException {
        Files.readAttributes( null, "*" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void readAttributesMapNull2() throws IOException {
        final Path path = Files.createTempFile( "foo", "bar" );

        Files.readAttributes( path, (String) null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void readAttributesMapNull3() throws IOException {
        Files.readAttributes( null, (String) null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void readAttributesMapEmpty() throws IOException {
        final Path path = Files.createTempFile( "foo", "bar" );

        Files.readAttributes( path, "" );
    }

    @Test(expected = NoSuchFileException.class)
    public void readAttributesMapNoSuchFileException() throws IOException {
        final Path path = Paths.get( "/path/to/file.txt" );

        Files.readAttributes( path, "*" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void setAttributeNull1() throws IOException {
        final Path path = Files.createTempFile( "foo", "bar" );

        Files.setAttribute( path, null, null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void setAttributeNull2() throws IOException {
        Files.setAttribute( null, "some", null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void setAttributeNull3() throws IOException {
        Files.setAttribute( null, null, null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void setAttributeEmpty() throws IOException {
        final Path path = Files.createTempFile( "foo", "bar" );

        Files.setAttribute( path, "", null );
    }

    @Test(expected = IllegalStateException.class)
    public void setAttributeInvalidAttr() throws IOException {
        final Path path = Files.createTempFile( "foo", "bar" );

        Files.setAttribute( path, "myattr", null );
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setAttributeInvalidView() throws IOException {
        final Path path = Files.createTempFile( "foo", "bar" );

        Files.setAttribute( path, "advanced:isRegularFile", null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void setAttributeInvalidView2() throws IOException {
        final Path path = Files.createTempFile( "foo", "bar" );

        Files.setAttribute( path, ":isRegularFile", null );
    }

    @Test(expected = NotImplementedException.class)
    public void setAttributeNotImpl() throws IOException {
        final Path path = Files.createTempFile( "foo", "bar" );

        Files.setAttribute( path, "isRegularFile", null );
    }

    @Test
    public void readAttribute() throws IOException {
        final Path path = Files.createTempFile( "foo", "bar" );

        assertThat( Files.getAttribute( path, "basic:isRegularFile" ) ).isNotNull();
        assertThat( Files.getAttribute( path, "basic:someThing" ) ).isNull();

        assertThat( Files.getAttribute( path, "isRegularFile" ) ).isNotNull();
        assertThat( Files.getAttribute( path, "someThing" ) ).isNull();
    }

    @Test(expected = IllegalArgumentException.class)
    public void readAttributeInvalid() throws IOException {
        final Path path = Files.createTempFile( "foo", "bar" );

        assertThat( Files.getAttribute( path, "*" ) ).isNotNull();
    }

    @Test(expected = IllegalArgumentException.class)
    public void readAttributeInvalid2() throws IOException {
        final Path path = Files.createTempFile( "foo", "bar" );

        assertThat( Files.getAttribute( path, "isRegularFile,isDirectory" ) ).isNull();
    }

    @Test(expected = NoSuchFileException.class)
    public void readAttributeInvalid3() throws IOException {
        final Path path = Paths.get( "/path/to/file.txt" );

        Files.getAttribute( path, "isRegularFile" );
    }

    @Test
    public void getLastModifiedTime() throws IOException {
        final Path path = Files.createTempFile( "foo", "bar" );

        assertThat( Files.getLastModifiedTime( path ) ).isNotNull();
    }

    @Test(expected = NoSuchFileException.class)
    public void getLastModifiedTimeNoSuchFileException() throws IOException {
        final Path path = Paths.get( "/path/to/file" );

        Files.getLastModifiedTime( path );
    }

    @Test(expected = IllegalArgumentException.class)
    public void getLastModifiedTimeNull() throws IOException {
        Files.getLastModifiedTime( null );
    }

    @Test(expected = NotImplementedException.class)
    public void setLastModifiedTime() throws IOException {
        final Path path = Files.createTempFile( "foo", "bar" );

        Files.setLastModifiedTime( path, null );
    }

    @Test(expected = NoSuchFileException.class)
    public void setLastModifiedTimeNoSuchFileException() throws IOException {
        final Path path = Paths.get( "/path/to/file" );

        Files.setLastModifiedTime( path, null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void setLastModifiedTimeNull() throws IOException {
        Files.setLastModifiedTime( null, null );
    }

    @Test(expected = NotImplementedException.class)
    public void setLastModifiedTimeNull2() throws IOException {
        final Path path = Files.createTempFile( "foo", "bar" );
        Files.setLastModifiedTime( path, null );
    }

    @Test
    public void size() throws IOException {
        final Path path = Files.createTempFile( "foo", "bar" );

        assertThat( Files.size( path ) ).isNotNull().isEqualTo( 0L );

        final Path sourceFile = newTempDir().resolve( "file.txt" );
        final OutputStream stream = Files.newOutputStream( sourceFile );
        stream.write( 'a' );
        stream.close();

        assertThat( Files.size( sourceFile ) ).isNotNull().isEqualTo( 1L );
    }

    @Test(expected = NoSuchFileException.class)
    public void sizeNoSuchFileException() throws IOException {
        final Path path = Paths.get( "/path/to/file" );

        Files.size( path );
    }

    @Test(expected = IllegalArgumentException.class)
    public void sizeNull() throws IOException {
        Files.size( null );
    }

    @Test
    public void exists() throws IOException {
        final Path path = Files.createTempFile( "foo", "bar" );

        assertThat( Files.exists( path ) ).isTrue();
        assertThat( Files.exists( newTempDir() ) ).isTrue();
        assertThat( Files.exists( Paths.get( "/some/path/here" ) ) ).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void existsNull() throws IOException {
        Files.exists( null );
    }

    @Test
    public void notExists() throws IOException {
        final Path path = Files.createTempFile( "foo", "bar" );

        assertThat( Files.notExists( path ) ).isFalse();
        assertThat( Files.notExists( newTempDir() ) ).isFalse();
        assertThat( Files.notExists( Paths.get( "/some/path/here" ) ) ).isTrue();
        assertThat( Files.notExists( newTempDir().resolve( "some.text" ) ) ).isTrue();
    }

    @Test(expected = IllegalArgumentException.class)
    public void notExistsNull() throws IOException {
        Files.notExists( null );
    }

    @Test
    public void isSameFile() throws IOException {
        final Path path = Files.createTempFile( "foo", "bar" );

        assertThat( Files.isSameFile( path, Paths.get( path.toString() ) ) ).isTrue();
        assertThat( Files.isSameFile( path, Files.createTempFile( "foo", "bar" ) ) ).isFalse();
        assertThat( Files.isSameFile( newTempDir(), newTempDir() ) ).isFalse();

        final Path dir = newTempDir();
        assertThat( Files.isSameFile( dir, Paths.get( dir.toString() ) ) ).isTrue();

        assertThat( Files.isSameFile( Paths.get( "/path/to/some/place" ), Paths.get( "/path/to/some/place" ) ) ).isTrue();
        assertThat( Files.isSameFile( Paths.get( "/path/to/some/place" ), Paths.get( "/path/to/some/place/a" ) ) ).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void isSameFileNull1() throws IOException {
        final Path path = Files.createTempFile( "foo", "bar" );

        Files.isSameFile( path, null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void isSameFileNull2() throws IOException {
        final Path path = Files.createTempFile( "foo", "bar" );

        Files.isSameFile( null, path );
    }

    @Test(expected = IllegalArgumentException.class)
    public void isSameFileNull3() throws IOException {
        Files.isSameFile( null, null );
    }

    @Test
    public void isHidden() throws IOException {
        final Path path = Files.createTempFile( "foo", "bar" );

        assertThat( Files.isHidden( path ) ).isFalse();
        assertThat( Files.isHidden( newTempDir() ) ).isFalse();
        assertThat( Files.isHidden( Paths.get( "/some/file" ) ) ).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void isHiddenNull() throws IOException {
        Files.isHidden( null );
    }

    @Test
    public void isReadable() throws IOException {
        final Path path = Files.createTempFile( "foo", "bar" );

        assertThat( Files.isReadable( path ) ).isTrue();
        assertThat( Files.isReadable( newTempDir() ) ).isTrue();
        assertThat( Files.isReadable( Paths.get( "/some/file" ) ) ).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void isReadableNull() throws IOException {
        Files.isReadable( null );
    }

    @Test
    public void isWritable() throws IOException {
        final Path path = Files.createTempFile( "foo", "bar" );

        assertThat( Files.isWritable( path ) ).isTrue();
        assertThat( Files.isWritable( newTempDir() ) ).isTrue();
        assertThat( Files.isWritable( Paths.get( "/some/file" ) ) ).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void isWritableNull() throws IOException {
        Files.isWritable( null );
    }

    @Test
    public void isExecutable() throws IOException {
        final Path path = Files.createTempFile( "foo", "bar" );

        assertThat( Files.isExecutable( path ) ).isFalse();
        assertThat( Files.isExecutable( newTempDir() ) ).isTrue();
        assertThat( Files.isExecutable( Paths.get( "/some/file" ) ) ).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void isExecutableNull() throws IOException {
        Files.isExecutable( null );
    }

    @Test
    public void isSymbolicLink() throws IOException {
        final Path path = Files.createTempFile( "foo", "bar" );

        assertThat( Files.isSymbolicLink( path ) ).isFalse();
        assertThat( Files.isSymbolicLink( newTempDir() ) ).isFalse();
        assertThat( Files.isSymbolicLink( Paths.get( "/some/file" ) ) ).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void isSymbolicLinkNull() throws IOException {
        Files.isSymbolicLink( null );
    }

    @Test
    public void isDirectory() throws IOException {
        final Path path = Files.createTempFile( "foo", "bar" );

        assertThat( Files.isDirectory( path ) ).isFalse();
        assertThat( Files.isDirectory( newTempDir() ) ).isTrue();
        assertThat( Files.isDirectory( Paths.get( "/some/file" ) ) ).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void isDirectoryNull() throws IOException {
        Files.isSymbolicLink( null );
    }

    @Test
    public void isRegularFile() throws IOException {
        final Path path = Files.createTempFile( "foo", "bar" );

        assertThat( Files.isRegularFile( path ) ).isTrue();
        assertThat( Files.isRegularFile( newTempDir() ) ).isFalse();
        assertThat( Files.isRegularFile( Paths.get( "/some/file" ) ) ).isFalse();
    }

    @Test(expected = IllegalArgumentException.class)
    public void isRegularFileNull() throws IOException {
        Files.isRegularFile( null );
    }

    private static interface MyAttrsView extends BasicFileAttributeView {

    }

    private static interface MyAttrs extends BasicFileAttributes {

    }
}
