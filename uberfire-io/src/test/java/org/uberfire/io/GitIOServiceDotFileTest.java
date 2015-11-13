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

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.attribute.FileAttribute;

import static org.junit.Assert.*;

/**
 *
 */
public class GitIOServiceDotFileTest extends CommonIOExceptionsServiceDotFileTest {

    @Override
    protected int testFileAttrSize4() {
        return 9;
    }

    @Override
    protected int testFileAttrSize3() {
        return 12;
    }

    @Override
    protected int testFileAttrSize2() {
        return 12;
    }

    @Override
    protected int testFileAttrSize1() {
        return 12;
    }

    @Override
    protected int testDirectoryAttrSize4() {
        return 9;
    }

    @Override
    protected int testDirectoryAttrSize3() {
        return 12;
    }

    @Override
    protected int testDirectoryAttrSize2() {
        return 13;
    }

    @Override
    protected int testDirectoryAttrSize1() {
        return 12;
    }

    @Override
    protected int createDirectoriesAttrSize() {
        return 10;
    }

    @Override
    protected int testNewByteChannelAttrSize() {
        return 10;
    }

    @Test
    public void testGetFileSystems() {

        final URI newRepo = URI.create( "git://" + new Date().getTime() + "-repo-test" );
        ioService().newFileSystem( newRepo, new HashMap<String, Object>() );

        final URI newRepo2 = URI.create( "git://" + new Date().getTime() + "-repo2-test" );
        ioService().newFileSystem( newRepo2, new HashMap<String, Object>() );

        final URI newRepo3 = URI.create( "git://" + new Date().getTime() + "-repo3-test" );
        ioService().newFileSystem( newRepo3, new HashMap<String, Object>() );

        final Iterator<FileSystem> iterator = ioService.getFileSystems().iterator();

        assertNotNull( iterator );

        assertTrue( iterator.hasNext() );
        assertNotNull( iterator.next() );

        assertTrue( iterator.hasNext() );
        assertNotNull( iterator.next() );

        assertTrue( iterator.hasNext() );
        assertNotNull( iterator.next() );

        assertTrue( iterator.hasNext() );
        assertNotNull( iterator.next() );

        assertFalse( iterator.hasNext() );

    }

    @Test
    public void testRoot() throws IOException {
        final Path path = getRootPath();

        ioService().setAttributes( path, new FileAttribute<Object>() {
            @Override
            public String name() {
                return "my_new_key";
            }

            @Override
            public Object value() {
                return "value";
            }
        } );

        final Map<String, Object> attrsValue = ioService().readAttributes( path );

        assertEquals( 9, attrsValue.size() );
        assertTrue( attrsValue.containsKey( "my_new_key" ) );

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

        assertEquals( 8, attrsValue2.size() );
        assertFalse( attrsValue2.containsKey( "my_new_key" ) );
    }

    @Override
    public Path getFilePath() {

        final Path file = ioService().get( URI.create( "git://repo-test/myfile" + new Random( 10L ).nextInt() + ".txt" ) );
        ioService().deleteIfExists( file );

        return file;
    }

    @Override
    public Path getTargetPath() {
        final Path file = ioService().get( URI.create( "git://repo-test/myTargetFile" + new Random( 10L ).nextInt() + ".txt" ) );
        ioService().deleteIfExists( file );

        return file;
    }

    @Override
    public Path getDirectoryPath() {
        final Path dir = ioService().get( URI.create( "git://repo-test/someDir" + new Random( 10L ).nextInt() ) );
        ioService().deleteIfExists( dir );

        return dir;
    }

    @Override
    public Path getComposedDirectoryPath() {
        return ioService().get( URI.create( "git://repo-test/path/to/someNewRandom" + new Random( 10L ).nextInt() ) );
    }

    private Path getRootPath() {
        return ioService().get( URI.create( "git://repo-test/" ) );
    }

    private static boolean created = false;

    @Before
    public void setup() throws IOException {
        if ( !created ) {
            final String path = createTempDirectory().getAbsolutePath();
            System.setProperty( "org.uberfire.nio.git.dir", path );
            System.out.println( ".niogit: " + path );

            final URI newRepo = URI.create( "git://repo-test" );

            try {
                ioService().newFileSystem( newRepo, new HashMap<String, Object>() );
            } catch ( final Exception ex ) {
            } finally {
                created = true;
            }
        }
    }

}
