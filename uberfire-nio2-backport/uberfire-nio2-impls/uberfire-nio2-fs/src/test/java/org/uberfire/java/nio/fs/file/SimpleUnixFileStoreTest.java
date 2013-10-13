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

package org.uberfire.java.nio.fs.file;

import java.io.File;

import org.junit.Test;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.FileStore;
import org.uberfire.java.nio.file.attribute.BasicFileAttributeView;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.java.nio.file.attribute.FileAttributeView;
import org.uberfire.java.nio.file.attribute.FileStoreAttributeView;
import org.uberfire.java.nio.file.attribute.FileTime;
import org.uberfire.java.nio.fs.file.SimpleUnixFileStore;

import static org.fest.assertions.api.Assertions.*;

public class SimpleUnixFileStoreTest {

    @Test
    public void simpleTests() {
        final FileStore fileStore = new SimpleUnixFileStore( null );

        assertThat( fileStore.name() ).isNotNull().isEqualTo( "/" );
        assertThat( fileStore.type() ).isNull();
        assertThat( fileStore.isReadOnly() ).isFalse();
        assertThat( fileStore.getTotalSpace() ).isEqualTo( File.listRoots()[ 0 ].getTotalSpace() );
        assertThat( fileStore.getUsableSpace() ).isEqualTo( File.listRoots()[ 0 ].getUsableSpace() );

        assertThat( fileStore.supportsFileAttributeView( BasicFileAttributeView.class ) ).isTrue();
        assertThat( fileStore.supportsFileAttributeView( MyFileAttributeView.class ) ).isFalse();
        assertThat( fileStore.supportsFileAttributeView( MyAlsoInvalidFileAttributeView.class ) ).isFalse();
        assertThat( fileStore.supportsFileAttributeView( "basic" ) ).isTrue();
        assertThat( fileStore.supportsFileAttributeView( "any" ) ).isFalse();
        assertThat( fileStore.supportsFileAttributeView( BasicFileAttributeView.class.getName() ) ).isFalse();
        assertThat( fileStore.supportsFileAttributeView( MyAlsoInvalidFileAttributeView.class.getName() ) ).isFalse();
        assertThat( fileStore.getFileStoreAttributeView( FileStoreAttributeView.class ) ).isNull();

        assertThat( fileStore.getAttribute( "name" ) ).isNotNull().isEqualTo( fileStore.name() );
        assertThat( fileStore.getAttribute( "totalSpace" ) ).isNotNull().isEqualTo( fileStore.getTotalSpace() );
        assertThat( fileStore.getAttribute( "usableSpace" ) ).isNotNull().isEqualTo( fileStore.getUsableSpace() );
        assertThat( fileStore.getAttribute( "readOnly" ) ).isNotNull().isEqualTo( fileStore.isReadOnly() );
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getUnallocatedSpaceUnsupportedOp() {
        new SimpleUnixFileStore( null ).getUnallocatedSpace();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getAttributeUnsupportedOp() {
        new SimpleUnixFileStore( null ).getAttribute( "someValueHere" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void supportsFileAttributeViewNull1() {
        new SimpleUnixFileStore( null ).supportsFileAttributeView( (Class<? extends FileAttributeView>) null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void supportsFileAttributeViewNull2() {
        new SimpleUnixFileStore( null ).supportsFileAttributeView( (String) null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void supportsFileAttributeViewEmpty() {
        new SimpleUnixFileStore( null ).supportsFileAttributeView( "" );
    }

    @Test(expected = IllegalArgumentException.class)
    public void getFileStoreAttributeViewNull() {
        new SimpleUnixFileStore( null ).getFileStoreAttributeView( null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAttributeNull() {
        new SimpleUnixFileStore( null ).getAttribute( null );
    }

    @Test(expected = IllegalArgumentException.class)
    public void getAttributeEmpty() {
        new SimpleUnixFileStore( null ).getAttribute( "" );
    }

    private static class MyFileAttributeView implements FileAttributeView {

        @Override
        public String name() {
            return null;
        }
    }

    private static class MyAlsoInvalidFileAttributeView implements BasicFileAttributeView {

        @Override
        public BasicFileAttributes readAttributes() throws IOException {
            return null;
        }

        @Override
        public void setTimes( FileTime lastModifiedTime,
                              FileTime lastAccessTime,
                              FileTime createTime ) throws IOException {

        }

        @Override
        public String name() {
            return null;
        }
    }
}
