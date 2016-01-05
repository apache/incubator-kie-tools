/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.java.nio.base;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;

import org.junit.Test;
import org.uberfire.java.nio.channels.SeekableByteChannel;

import static org.junit.Assert.*;

/**
 *
 */
public class SeekableByteChannelTest {

    @Test
    public void test() throws IOException {
        final File tempFile = File.createTempFile( "foo", "bar" );

        final SeekableByteChannel sbc = new SeekableByteChannelFileBasedImpl( new RandomAccessFile( tempFile, "rw" ).getChannel() );

        assertTrue( sbc.isOpen() );
        sbc.write( ByteBuffer.wrap( "CONTENT\n?!".getBytes() ) );

        sbc.position( 0L );
        ByteBuffer buffer = ByteBuffer.allocate( 10 );
        sbc.read( buffer );
        assertEquals( "CONTENT\n?!", new String( buffer.array() ) );

        sbc.close();
        assertFalse( sbc.isOpen() );

    }

    @Test
    public void testPosition() throws IOException {
        final File tempFile = File.createTempFile( "foo", "bar" );

        final SeekableByteChannel sbc = new SeekableByteChannelFileBasedImpl( new RandomAccessFile( tempFile, "rw" ).getChannel() );

        assertTrue( sbc.isOpen() );
        sbc.write( ByteBuffer.wrap( "CONTENT\n?!".getBytes() ) );

        assertEquals( 10L, sbc.position() );
        assertEquals( 10L, sbc.size() );

        sbc.position( 1L );
        ByteBuffer buffer = ByteBuffer.allocate( 8 );
        sbc.read( buffer );
        assertEquals( "ONTENT\n?", new String( buffer.array() ) );

        assertEquals( 9L, sbc.position() );

        sbc.position( 0L );
        sbc.truncate( 2L );
        ByteBuffer buffer3 = ByteBuffer.allocate( 2 );
        sbc.read( buffer3 );
        assertEquals( "CO", new String( buffer3.array() ) );

        sbc.close();
        assertFalse( sbc.isOpen() );

    }

}
