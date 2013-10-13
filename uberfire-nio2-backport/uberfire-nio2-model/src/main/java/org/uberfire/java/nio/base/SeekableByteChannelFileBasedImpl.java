package org.uberfire.java.nio.base;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.channels.SeekableByteChannel;

import static org.uberfire.commons.validation.Preconditions.*;

/**
 *
 */
public class SeekableByteChannelFileBasedImpl
        implements SeekableByteChannel {

    private final FileChannel channel;

    public SeekableByteChannelFileBasedImpl( final FileChannel channel ) {
        this.channel = checkNotNull( "channel", channel );
    }

    @Override
    public long position() throws IOException {
        try {
            return channel.position();
        } catch ( java.io.IOException e ) {
            throw new IOException( e );
        }
    }

    @Override
    public SeekableByteChannel position( final long newPosition ) throws IOException {
        try {
            channel.position( newPosition );
            return this;
        } catch ( java.io.IOException e ) {
            throw new IOException( e );
        }
    }

    @Override
    public long size() throws IOException {
        try {
            return channel.size();
        } catch ( java.io.IOException e ) {
            throw new IOException( e );
        }
    }

    @Override
    public SeekableByteChannel truncate( final long size ) throws IOException {
        try {
            channel.truncate( size );
            return this;
        } catch ( java.io.IOException e ) {
            throw new IOException( e );
        }
    }

    @Override
    public int read( final ByteBuffer dst ) throws java.io.IOException {
        try {
            return channel.read( dst );
        } catch ( java.io.IOException e ) {
            throw new IOException( e );
        }
    }

    @Override
    public int write( final ByteBuffer src ) throws java.io.IOException {
        try {
            return channel.write( src );
        } catch ( java.io.IOException e ) {
            throw new IOException( e );
        }
    }

    @Override
    public boolean isOpen() {
        return channel.isOpen();
    }

    @Override
    public void close() throws java.io.IOException {
        try {
            channel.close();
        } catch ( java.io.IOException e ) {
            throw new IOException( e );
        }
    }
}
