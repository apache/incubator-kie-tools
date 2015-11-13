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
import java.net.URI;

import org.junit.Test;
import org.uberfire.java.nio.base.BasicFileAttributesImpl;
import org.uberfire.java.nio.base.GeneralPathImpl;
import org.uberfire.java.nio.base.NotImplementedException;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.attribute.BasicFileAttributeView;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static org.fest.assertions.api.Assertions.*;
import static org.uberfire.java.nio.file.AccessMode.*;

public class SimpleFileSystemProviderAttrsRelatedTest {

    @Test
    public void checkIsHidden() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();
        final Path path = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), "/path/to/file.txt", false );

        assertThat( fsProvider.isHidden( path ) ).isFalse();

        final File tempFile = File.createTempFile( "foo", "bar" );
        final Path path2 = GeneralPathImpl.newFromFile( fsProvider.getFileSystem( URI.create( "file:///" ) ), tempFile );

        assertThat( fsProvider.isHidden( path2 ) ).isEqualTo( tempFile.isHidden() );
    }

    @Test(expected = IllegalArgumentException.class)
    public void isHiddenNull() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        fsProvider.isHidden( null );
    }

    @Test
    public void checkAccess() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();
        final Path path = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), "/path/to/file.txt", false );

        try {
            fsProvider.checkAccess( path, WRITE );
            fail( "can't have write access on non existent file" );
        } catch ( Exception ex ) {
        }

        try {
            fsProvider.checkAccess( path, READ );
            fail( "can't have read access on non existent file" );
        } catch ( Exception ex ) {
        }

        try {
            fsProvider.checkAccess( path, EXECUTE );
            fail( "can't have execute access on non existent file" );
        } catch ( Exception ex ) {
        }

        final File tempFile = File.createTempFile( "foo", "bar" );
        final Path path2 = GeneralPathImpl.newFromFile( fsProvider.getFileSystem( URI.create( "file:///" ) ), tempFile );

        try {
            fsProvider.checkAccess( path2, WRITE );
        } catch ( Exception ex ) {
            fail( "write access should be ok" );
        }

        tempFile.setWritable( false );

        try {
            fsProvider.checkAccess( path2, WRITE );
            fail( "can't have write access on file" );
        } catch ( Exception ex ) {
        }

        tempFile.setWritable( true );

        try {
            fsProvider.checkAccess( path2, READ );
        } catch ( Exception ex ) {
            fail( "read access should be ok" );
        }

        tempFile.setReadable( false );

        try {
            fsProvider.checkAccess( path2, READ );
            fail( "can't have read access on file" );
        } catch ( Exception ex ) {
        }

        tempFile.setReadable( true );

        try {
            fsProvider.checkAccess( path2, EXECUTE );
            fail( "can't have execute access on file" );
        } catch ( Exception ex ) {
        }

        tempFile.setExecutable( true );

        try {
            fsProvider.checkAccess( path2, EXECUTE );
        } catch ( Exception ex ) {
            fail( "execute access should be ok" );
        }

        try {
            fsProvider.checkAccess( path2, READ, WRITE, EXECUTE );
        } catch ( Exception ex ) {
            fail( "all access should be ok" );
        }

    }

    @Test(expected = IllegalArgumentException.class)
    public void checkAccessNull1() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        fsProvider.checkAccess( null, null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkAccessNull2() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();
        final Path path = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), "/path/to/file.txt", false );

        fsProvider.checkAccess( path, null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkAccessNull3() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        fsProvider.checkAccess( null, READ );
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkAccessNull4() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();
        final File tempFile = File.createTempFile( "foo", "bar" );
        final Path path = GeneralPathImpl.newFromFile( fsProvider.getFileSystem( URI.create( "file:///" ) ), tempFile );

        fsProvider.checkAccess( path, null, READ );
    }

    @Test
    public void checkGetFileStore() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();
        final Path path = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), "/path/to/file.txt", false );

        assertThat( fsProvider.getFileStore( path ) ).isNotNull();
        assertThat( fsProvider.getFileSystem( path.toUri() ).getFileStores() ).isNotNull().contains( fsProvider.getFileStore( path ) );
    }

    @Test(expected = IllegalArgumentException.class)
    public void getFileStoreNull() {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        fsProvider.getFileStore( null );
    }

    @Test
    public void checkGetFileAttributeViewGeneral() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final File tempFile = File.createTempFile( "foo", "bar" );
        final Path path = GeneralPathImpl.newFromFile( fsProvider.getFileSystem( URI.create( "file:///" ) ), tempFile );

        final BasicFileAttributeView view = fsProvider.getFileAttributeView( path, BasicFileAttributeView.class );
        assertThat( view ).isNotNull();
        assertThat( view.readAttributes() ).isNotNull();
        assertThat( view.readAttributes().isRegularFile() ).isTrue();
        assertThat( view.readAttributes().isDirectory() ).isFalse();
        assertThat( view.readAttributes().isSymbolicLink() ).isFalse();
        assertThat( view.readAttributes().isOther() ).isFalse();
        assertThat( view.readAttributes().size() ).isEqualTo( 0L );
    }

    @Test
    public void checkGetFileAttributeViewBasic() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final File tempFile = File.createTempFile( "foo", "bar" );
        final Path path = GeneralPathImpl.newFromFile( fsProvider.getFileSystem( URI.create( "file:///" ) ), tempFile );

        final BasicFileAttributeView view = fsProvider.getFileAttributeView( path, BasicFileAttributeView.class );
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
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final File tempFile = File.createTempFile( "foo", "bar" );
        final Path path = GeneralPathImpl.newFromFile( fsProvider.getFileSystem( URI.create( "file:///" ) ), tempFile );

        assertThat( fsProvider.getFileAttributeView( path, MyAttrsView.class ) ).isNull();
    }

    @Test(expected = IllegalArgumentException.class)
    public void getFileAttributeViewNull1() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        fsProvider.getFileAttributeView( null, MyAttrsView.class );
    }

    @Test(expected = IllegalArgumentException.class)
    public void getFileAttributeViewNull2() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final Path path = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), "/path/to/file.txt", false );
        fsProvider.getFileAttributeView( path, null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void getFileAttributeViewNull3() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        fsProvider.getFileAttributeView( null, null );
    }

    @Test
    public void checkReadAttributesGeneral() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final File tempFile = File.createTempFile( "foo", "bar" );
        final Path path = GeneralPathImpl.newFromFile( fsProvider.getFileSystem( URI.create( "file:///" ) ), tempFile );

        final BasicFileAttributesImpl attrs = fsProvider.readAttributes( path, BasicFileAttributesImpl.class );
        assertThat( attrs ).isNotNull();
        assertThat( attrs.isRegularFile() ).isTrue();
        assertThat( attrs.isDirectory() ).isFalse();
        assertThat( attrs.isSymbolicLink() ).isFalse();
        assertThat( attrs.isOther() ).isFalse();
        assertThat( attrs.size() ).isEqualTo( 0L );
    }

    @Test
    public void checkReadAttributesBasic() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final File tempFile = File.createTempFile( "foo", "bar" );
        final Path path = GeneralPathImpl.newFromFile( fsProvider.getFileSystem( URI.create( "file:///" ) ), tempFile );

        final BasicFileAttributes attrs = fsProvider.readAttributes( path, BasicFileAttributes.class );

        assertThat( attrs ).isNotNull();
        assertThat( attrs.isRegularFile() ).isTrue();
        assertThat( attrs.isDirectory() ).isFalse();
        assertThat( attrs.isSymbolicLink() ).isFalse();
        assertThat( attrs.isOther() ).isFalse();
        assertThat( attrs.size() ).isEqualTo( 0L );
    }

    @Test(expected = NoSuchFileException.class)
    public void readAttributesNonExistentFile() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final Path path = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), "/path/to/file.txt", false );

        fsProvider.readAttributes( path, BasicFileAttributes.class );
    }

    @Test
    public void readAttributesInvalid() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final File tempFile = File.createTempFile( "foo", "bar" );
        final Path path = GeneralPathImpl.newFromFile( fsProvider.getFileSystem( URI.create( "file:///" ) ), tempFile );

        assertThat( fsProvider.readAttributes( path, MyAttrs.class ) ).isNull();
    }

    @Test(expected = IllegalArgumentException.class)
    public void readAttributesNull1() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        fsProvider.readAttributes( null, MyAttrs.class );
    }

    @Test(expected = IllegalArgumentException.class)
    public void readAttributesNull2() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final Path path = GeneralPathImpl.create( fsProvider.getFileSystem( URI.create( "file:///" ) ), "/path/to/file.txt", false );
        fsProvider.readAttributes( path, (Class<MyAttrs>) null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void readAttributesNull3() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        fsProvider.readAttributes( null, (Class<MyAttrs>) null );
    }

    @Test
    public void checkReadAttributesMap() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final File tempFile = File.createTempFile( "foo", "bar" );
        final Path path = GeneralPathImpl.newFromFile( fsProvider.getFileSystem( URI.create( "file:///" ) ), tempFile );

        assertThat( fsProvider.readAttributes( path, "*" ) ).isNotNull().hasSize( 9 );
        assertThat( fsProvider.readAttributes( path, "basic:*" ) ).isNotNull().hasSize( 9 );
        assertThat( fsProvider.readAttributes( path, "basic:isRegularFile" ) ).isNotNull().hasSize( 1 );
        assertThat( fsProvider.readAttributes( path, "basic:isRegularFile,isDirectory" ) ).isNotNull().hasSize( 2 );
        assertThat( fsProvider.readAttributes( path, "basic:isRegularFile,isDirectory,someThing" ) ).isNotNull().hasSize( 2 );
        assertThat( fsProvider.readAttributes( path, "basic:someThing" ) ).isNotNull().hasSize( 0 );

        assertThat( fsProvider.readAttributes( path, "isRegularFile" ) ).isNotNull().hasSize( 1 );
        assertThat( fsProvider.readAttributes( path, "isRegularFile,isDirectory" ) ).isNotNull().hasSize( 2 );
        assertThat( fsProvider.readAttributes( path, "isRegularFile,isDirectory,someThing" ) ).isNotNull().hasSize( 2 );
        assertThat( fsProvider.readAttributes( path, "someThing" ) ).isNotNull().hasSize( 0 );

        try {
            fsProvider.readAttributes( path, ":someThing" );
            fail( "undefined view" );
        } catch ( IllegalArgumentException ex ) {
        }

        try {
            fsProvider.readAttributes( path, "advanced:isRegularFile" );
            fail( "undefined view" );
        } catch ( UnsupportedOperationException ex ) {
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void readAttributesMapNull1() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        fsProvider.readAttributes( null, "*" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void readAttributesMapNull2() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final File tempFile = File.createTempFile( "foo", "bar" );
        final Path path = GeneralPathImpl.newFromFile( fsProvider.getFileSystem( URI.create( "file:///" ) ), tempFile );

        fsProvider.readAttributes( path, (String) null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void readAttributesMapNull3() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        fsProvider.readAttributes( null, (String) null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void readAttributesMapEmpty() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final File tempFile = File.createTempFile( "foo", "bar" );
        final Path path = GeneralPathImpl.newFromFile( fsProvider.getFileSystem( URI.create( "file:///" ) ), tempFile );

        fsProvider.readAttributes( path, "" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void setAttributeNull1() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final File tempFile = File.createTempFile( "foo", "bar" );
        final Path path = GeneralPathImpl.newFromFile( fsProvider.getFileSystem( URI.create( "file:///" ) ), tempFile );

        fsProvider.setAttribute( path, null, null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void setAttributeNull2() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        fsProvider.setAttribute( null, "some", null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void setAttributeNull3() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        fsProvider.setAttribute( null, null, null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void setAttributeEmpty() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final File tempFile = File.createTempFile( "foo", "bar" );
        final Path path = GeneralPathImpl.newFromFile( fsProvider.getFileSystem( URI.create( "file:///" ) ), tempFile );

        fsProvider.setAttribute( path, "", null );
    }

    @Test(expected = IllegalStateException.class)
    public void setAttributeInvalidAttr() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final File tempFile = File.createTempFile( "foo", "bar" );
        final Path path = GeneralPathImpl.newFromFile( fsProvider.getFileSystem( URI.create( "file:///" ) ), tempFile );

        fsProvider.setAttribute( path, "myattr", null );
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setAttributeInvalidView() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final File tempFile = File.createTempFile( "foo", "bar" );
        final Path path = GeneralPathImpl.newFromFile( fsProvider.getFileSystem( URI.create( "file:///" ) ), tempFile );

        fsProvider.setAttribute( path, "advanced:isRegularFile", null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void setAttributeInvalidView2() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final File tempFile = File.createTempFile( "foo", "bar" );
        final Path path = GeneralPathImpl.newFromFile( fsProvider.getFileSystem( URI.create( "file:///" ) ), tempFile );

        fsProvider.setAttribute( path, ":isRegularFile", null );
    }

    @Test(expected = NotImplementedException.class)
    public void setAttributeNotImpl() throws IOException {
        final SimpleFileSystemProvider fsProvider = new SimpleFileSystemProvider();

        final File tempFile = File.createTempFile( "foo", "bar" );
        final Path path = GeneralPathImpl.newFromFile( fsProvider.getFileSystem( URI.create( "file:///" ) ), tempFile );

        fsProvider.setAttribute( path, "isRegularFile", null );
    }

    private static interface MyAttrsView extends BasicFileAttributeView {

    }

    private static interface MyAttrs extends BasicFileAttributes {

    }

}
