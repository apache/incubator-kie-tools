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

package org.uberfire.io;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.uberfire.commons.lifecycle.PriorityDisposableRegistry;
import org.uberfire.io.impl.IOServiceDotFileImpl;
import org.uberfire.java.nio.base.AbstractBasicFileAttributeView;
import org.uberfire.java.nio.base.AbstractPath;
import org.uberfire.java.nio.base.AttrHolder;
import org.uberfire.java.nio.base.NeedsPreloadedAttrs;
import org.uberfire.java.nio.channels.SeekableByteChannel;
import org.uberfire.java.nio.file.FileAlreadyExistsException;
import org.uberfire.java.nio.file.OpenOption;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.StandardOpenOption;
import org.uberfire.java.nio.file.attribute.BasicFileAttributeView;
import org.uberfire.java.nio.file.attribute.BasicFileAttributes;
import org.uberfire.java.nio.file.attribute.FileAttribute;
import org.uberfire.java.nio.file.attribute.FileTime;

import static org.junit.Assert.*;
import static org.uberfire.java.nio.base.dotfiles.DotFileUtils.*;

/**
 *
 */
public abstract class CommonIOServiceDotFileTest {

    protected final Date dateValue = new Date();

    protected static final List<File> tempFiles = new ArrayList<File>();

    @Test
    public void testFile() throws IOException {
        final Path path = getFilePath();
        ioService().write( path, "ooooo!", Collections.<OpenOption>emptySet(), new FileAttribute<Object>() {
                               @Override
                               public String name() {
                                   return "custom";
                               }

                               @Override
                               public Object value() {
                                   return dateValue;
                               }
                           }, new FileAttribute<String>() {
                               @Override
                               public String name() {
                                   return "int.hello";
                               }

                               @Override
                               public String value() {
                                   return "world";
                               }
                           }, new FileAttribute<Integer>() {
                               @Override
                               public String name() {
                                   return "int";
                               }

                               @Override
                               public Integer value() {
                                   return 10;
                               }
                           }
                         );

        Map<String, Object> attrs = ioService().readAttributes( path );

        assertEquals( testFileAttrSize1(), attrs.size() );
        assertTrue( attrs.containsKey( "int.hello" ) );
        assertTrue( attrs.containsKey( "custom" ) );
        assertTrue( attrs.containsKey( "int" ) );

        assertEquals( 10, attrs.get( "int" ) );
        assertEquals( dateValue, attrs.get( "custom" ) );
        assertEquals( "world", attrs.get( "int.hello" ) );

        if ( path instanceof AttrHolder ) {
            ( (AttrHolder) path ).getAttrStorage().clear();
        }

        attrs = ioService().readAttributes( path );

        assertEquals( 10, attrs.get( "int" ) );
        assertEquals( dateValue, attrs.get( "custom" ) );
        assertEquals( "world", attrs.get( "int.hello" ) );

        final Map<String, Object> attrsValue = ioService().readAttributes( path );

        assertEquals( testFileAttrSize2(), attrsValue.size() );

        ioService().setAttributes( path, new FileAttribute<Object>() {
            @Override
            public String name() {
                return "my_new_key";
            }

            @Override
            public Object value() {
                return null;
            }
        } );

        final Map<String, Object> attrsValue2 = ioService().readAttributes( path );

        assertEquals( testFileAttrSize3(), attrsValue2.size() );
        assertFalse( attrsValue2.containsKey( "my_new_key" ) );

        ioService().delete( path );

        ioService().write( path, "ooooo!" );

        final Map<String, Object> attrsClean = ioService().readAttributes( path );

        assertEquals( testFileAttrSize4(), attrsClean.size() );
    }

    protected abstract int testFileAttrSize4();

    protected abstract int testFileAttrSize3();

    protected abstract int testFileAttrSize2();

    protected abstract int testFileAttrSize1();

    @Test
    public void testDirectory() throws IOException {
        final Path path = getDirectoryPath();
        ioService().createDirectory( path, new FileAttribute<Object>() {
                                         @Override
                                         public String name() {
                                             return "custom";
                                         }

                                         @Override
                                         public Object value() {
                                             return dateValue;
                                         }
                                     }, new FileAttribute<String>() {
                                         @Override
                                         public String name() {
                                             return "int.hello";
                                         }

                                         @Override
                                         public String value() {
                                             return "world";
                                         }
                                     }, new FileAttribute<Integer>() {
                                         @Override
                                         public String name() {
                                             return "int";
                                         }

                                         @Override
                                         public Integer value() {
                                             return 10;
                                         }
                                     }
                                   );

        Map<String, Object> attrs = ioService().readAttributes( path );

        assertEquals( testDirectoryAttrSize1(), attrs.size() );
        assertTrue( attrs.containsKey( "int.hello" ) );
        assertTrue( attrs.containsKey( "custom" ) );
        assertTrue( attrs.containsKey( "int" ) );

        assertEquals( 10, attrs.get( "int" ) );
        assertEquals( dateValue, attrs.get( "custom" ) );
        assertEquals( "world", attrs.get( "int.hello" ) );

        if ( path instanceof AttrHolder ) {
            ( (AttrHolder) path ).getAttrStorage().clear();
        }

        attrs = ioService().readAttributes( path );

        assertEquals( 10, attrs.get( "int" ) );
        assertEquals( dateValue, attrs.get( "custom" ) );
        assertEquals( "world", attrs.get( "int.hello" ) );

        ioService().delete( path );

        ioService().createDirectory( path );

        final Map<String, Object> attrsClean = ioService().readAttributes( path );

        assertEquals( testDirectoryAttrSize4(), attrsClean.size() );
    }

    protected abstract int testDirectoryAttrSize4();

    protected abstract int testDirectoryAttrSize3();

    protected abstract int testDirectoryAttrSize2();

    protected abstract int testDirectoryAttrSize1();

    @Test
    public void testDelete() throws IOException {
        final Path dir = getDirectoryPath();

        ioService().createDirectory( dir, new FileAttribute<Object>() {
                                         @Override
                                         public String name() {
                                             return "custom";
                                         }

                                         @Override
                                         public Object value() {
                                             return dateValue;
                                         }
                                     }, new FileAttribute<String>() {
                                         @Override
                                         public String name() {
                                             return "int.hello";
                                         }

                                         @Override
                                         public String value() {
                                             return "world";
                                         }
                                     }, new FileAttribute<Integer>() {
                                         @Override
                                         public String name() {
                                             return "int";
                                         }

                                         @Override
                                         public Integer value() {
                                             return 10;
                                         }
                                     }
                                   );
        assertTrue( ioService().exists( dir ) );
        assertTrue( ioService().exists( dot( dir ) ) );

        ioService().delete( dir );

        assertFalse( ioService().exists( dir ) );
        assertFalse( ioService().exists( dot( dir ) ) );

        final Path file = getFilePath();

        ioService().write( file, "ooooo!", Collections.<OpenOption>emptySet(), new FileAttribute<Object>() {
            @Override
            public String name() {
                return "custom";
            }

            @Override
            public Object value() {
                return dateValue;
            }
        } );

        assertTrue( ioService().exists( file ) );
        assertTrue( ioService().exists( dot( file ) ) );

        assertFalse( ( (AttrHolder) file ).getAttrStorage().getContent().isEmpty() );

        ioService().delete( file );

        assertTrue( ( (AttrHolder) file ).getAttrStorage().getContent().isEmpty() );

        assertFalse( ioService().exists( file ) );
        assertFalse( ioService().exists( dot( file ) ) );
    }

    @Test
    public void testCopyFile() {
        final Path sfile = getFilePath();
        final Path tfile = getTargetPath();

        ioService().deleteIfExists( sfile );
        ioService().deleteIfExists( tfile );

        ioService().write( sfile, "wow", Collections.<OpenOption>emptySet(), new FileAttribute<Object>() {
            @Override
            public String name() {
                return "custom";
            }

            @Override
            public Object value() {
                return dateValue;
            }
        } );

        assertTrue( ioService().exists( sfile ) );
        assertTrue( ioService().exists( dot( sfile ) ) );
        assertFalse( ioService().exists( tfile ) );
        assertFalse( ioService().exists( dot( tfile ) ) );

        ioService().copy( sfile, tfile );

        assertTrue( ioService().exists( sfile ) );
        assertTrue( ioService().exists( dot( sfile ) ) );
        assertTrue( ioService().exists( tfile ) );
        assertTrue( ioService().exists( dot( tfile ) ) );
    }

    @Test
    public void createDirectories() {
        final Path dir = getComposedDirectoryPath();

        assertFalse( ioService().exists( dir ) );

        ioService().createDirectories( dir, new FileAttribute<Object>() {
            @Override
            public String name() {
                return "custom";
            }

            @Override
            public Object value() {
                return dateValue;
            }
        } );

        assertTrue( ioService().exists( dir ) );

        assertTrue( ioService().exists( dir.getParent() ) );
        assertNotNull( ioService().exists( dir.getParent().getFileName() ) );

        Map<String, Object> attrs = ioService().readAttributes( dir );

        assertEquals( createDirectoriesAttrSize(), attrs.size() );

        ioService().delete( dir );

        ioService().exists( dir.getParent() );
    }

    protected abstract int createDirectoriesAttrSize();

    @Test
    public void testDeleteIfExistis() throws IOException {
        final Path dir = getDirectoryPath();

        ioService().deleteIfExists( dir );

        ioService().createDirectory( dir, new FileAttribute<Object>() {
                                         @Override
                                         public String name() {
                                             return "custom";
                                         }

                                         @Override
                                         public Object value() {
                                             return dateValue;
                                         }
                                     }, new FileAttribute<String>() {
                                         @Override
                                         public String name() {
                                             return "int.hello";
                                         }

                                         @Override
                                         public String value() {
                                             return "world";
                                         }
                                     }, new FileAttribute<Integer>() {
                                         @Override
                                         public String name() {
                                             return "int";
                                         }

                                         @Override
                                         public Integer value() {
                                             return 10;
                                         }
                                     }
                                   );

        assertTrue( ioService().deleteIfExists( dir ) );
        assertFalse( ioService().deleteIfExists( dir ) );

        final Path file = getFilePath();

        ioService().deleteIfExists( file );

        ioService().write( file, "ooooo!", Collections.<OpenOption>emptySet(), new FileAttribute<Object>() {
            @Override
            public String name() {
                return "custom";
            }

            @Override
            public Object value() {
                return dateValue;
            }
        } );

        assertFalse( ( (AttrHolder) file ).getAttrStorage().getContent().isEmpty() );

        assertTrue( ioService().deleteIfExists( file ) );

        assertTrue( ( (AttrHolder) file ).getAttrStorage().getContent().isEmpty() );

        assertFalse( ioService().deleteIfExists( file ) );
    }

    @Test
    public void testReadNewByteChannel() throws IOException {
        final Path file = getFilePath();
        ioService().deleteIfExists( file );
        assertFalse( ioService().exists( file ) );
        String content = "sample content";
        ioService.write( file, content );
        assertTrue( ioService().exists( file ) );

        final SeekableByteChannel sbc = ioService().newByteChannel( file, StandardOpenOption.READ );
        String readContent = readSbc( sbc );

        assertEquals( content, readContent );

        ioService().delete( file );

    }

    @Test
    public void testNewByteChannel() throws IOException {
        final Path file = getFilePath();

        ioService().deleteIfExists( file );

        assertFalse( ioService().exists( file ) );

        final SeekableByteChannel sbc = ioService().newByteChannel( file, Collections.<OpenOption>emptySet(), new FileAttribute<Object>() {
            @Override
            public String name() {
                return "custom";
            }

            @Override
            public Object value() {
                return dateValue;
            }
        } );

        sbc.write( ByteBuffer.wrap( "helloWorld!".getBytes() ) );
        sbc.close();

        assertTrue( ioService().exists( file ) );

        Map<String, Object> attrs = ioService().readAttributes( file );

        assertEquals( testNewByteChannelAttrSize(), attrs.size() );

        try {
            ioService().newByteChannel( file, Collections.<OpenOption>emptySet() );
            fail( "FileAlreadyExistsException expected" );
        } catch ( FileAlreadyExistsException ex ) {
        }

        ioService().delete( file );

        ioService().newByteChannel( file, Collections.<OpenOption>emptySet() ).close();

        assertTrue( ioService().deleteIfExists( file ) );
    }

    protected abstract int testNewByteChannelAttrSize();

    @Test
    public void testGetAttribute() {
        final Path file = getFilePath();

        ioService().deleteIfExists( file );

        ioService().write( file, "ooooo!", Collections.<OpenOption>emptySet(), new FileAttribute<Object>() {
            @Override
            public String name() {
                return "dcore.author";
            }

            @Override
            public Object value() {
                return "AuthorName";
            }
        } );

        assertNotNull( ioService().getAttribute( file, "dcore:dcore.author" ) );
        assertNull( ioService().getAttribute( file, "dcore:dcore.not_here" ) );
        assertNotNull( ioService().getAttribute( file, "dcore.author" ) );
        assertNull( ioService().getAttribute( file, "something" ) );

        ( (AttrHolder) file ).getAttrStorage().clear();

        assertNotNull( ioService().getAttribute( file, "dcore:dcore.author" ) );
        assertNull( ioService().getAttribute( file, "dcore:dcore.not_here" ) );
        assertNotNull( ioService().getAttribute( file, "dcore.author" ) );
        assertNull( ioService().getAttribute( file, "something" ) );
    }

    @Test
    public void testGetAttributeView() {
        final Path file = getFilePath();

        ioService().deleteIfExists( file );

        ioService().write( file, "ooooo!", Collections.<OpenOption>emptySet(), new FileAttribute<Object>() {
            @Override
            public String name() {
                return "dcore.author";
            }

            @Override
            public Object value() {
                return "AuthorName";
            }
        } );

        assertNotNull( ioService().getFileAttributeView( file, BasicFileAttributeView.class ) );
        assertNull( ioService().getFileAttributeView( file, MyAttrsView.class ) );
        assertNotNull( ioService().getFileAttributeView( file, XDublinCoreView.class ) );

        final DublinCoreAttributes attr = ioService().getFileAttributeView( file, XDublinCoreView.class ).readAttributes();
        assertEquals( "AuthorName", attr.getAuthor() );

        ( (AttrHolder) file ).getAttrStorage().clear();

        assertNotNull( ioService().getFileAttributeView( file, BasicFileAttributeView.class ) );
        assertNull( ioService().getFileAttributeView( file, MyAttrsView.class ) );
        assertNotNull( ioService().getFileAttributeView( file, XDublinCoreView.class ) );
    }

    public abstract Path getFilePath();

    public abstract Path getTargetPath();

    public abstract Path getDirectoryPath();

    public abstract Path getComposedDirectoryPath();

    public static File createTempDirectory()
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

    private String readSbc( SeekableByteChannel sbc ) {
        ByteBuffer byteBuffer = ByteBuffer.allocate( 100 );
        StringBuilder content = new StringBuilder();
        byteBuffer.clear();
        try {
            while ( ( sbc.read( byteBuffer ) ) > 0 ) {
                byteBuffer.flip();
                content.append( new String( byteBuffer.array(),0, byteBuffer.remaining()) );
                byteBuffer.compact();
            }
        } catch ( IOException e ) {
            e.printStackTrace();
        }
        return content.toString();
    }

    private static interface MyAttrsView extends BasicFileAttributeView {

    }

    public static class XDublinCoreView extends AbstractBasicFileAttributeView<AbstractPath>
            implements NeedsPreloadedAttrs {

        private BasicFileAttributes attrs = null;

        public XDublinCoreView( final AbstractPath path ) {
            super( path );
        }

        @Override
        public <T extends BasicFileAttributes> T readAttributes() throws org.uberfire.java.nio.IOException {
            if ( attrs == null ) {
                final BasicFileAttributes basicAtts = ( (BasicFileAttributeView) path.getAttrView( BasicFileAttributeView.class ) ).readAttributes();
                attrs = new DublinCoreAttributes( basicAtts, (String) path.getAttrStorage().getContent().get( "dcore.author" ) );
            }
            return (T) attrs;
        }

        @Override
        public Class<? extends BasicFileAttributeView>[] viewTypes() {
            return new Class[]{ XDublinCoreView.class };
        }
    }

    public static class DublinCoreAttributes implements BasicFileAttributes {

        private final BasicFileAttributes attributes;
        private final String author;

        private DublinCoreAttributes( final BasicFileAttributes attributes,
                                      final String author ) {
            this.attributes = attributes;
            this.author = author;
        }

        public String getAuthor() {
            return author;
        }

        @Override
        public FileTime lastModifiedTime() {
            return attributes.lastModifiedTime();
        }

        @Override
        public FileTime lastAccessTime() {
            return attributes.lastAccessTime();
        }

        @Override
        public FileTime creationTime() {
            return attributes.creationTime();
        }

        @Override
        public boolean isRegularFile() {
            return attributes.isRegularFile();
        }

        @Override
        public boolean isDirectory() {
            return attributes.isDirectory();
        }

        @Override
        public boolean isSymbolicLink() {
            return attributes.isSymbolicLink();
        }

        @Override
        public boolean isOther() {
            return attributes.isOther();
        }

        @Override
        public long size() {
            return attributes.size();
        }

        @Override
        public Object fileKey() {
            return attributes.fileKey();
        }
    }

    @AfterClass
    @BeforeClass
    public static void cleanup() {
        for ( final File tempFile : tempFiles ) {
            FileUtils.deleteQuietly( tempFile );
        }
    }

    protected static IOService ioService = null;

    public IOService ioService() {
        if ( ioService == null ) {
            ioService = new IOServiceDotFileImpl();
            assertTrue( PriorityDisposableRegistry.getDisposables().contains( ioService ) );
        }
        return ioService;
    }

}
