/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.java.nio.fs.k8s;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import com.google.common.io.ByteSource;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.channels.SeekableByteChannel;

public class SeekableInMemoryByteChannel implements SeekableByteChannel {

    private static final String ENCODING = System.getProperty("file.encoding", StandardCharsets.UTF_8.name());

    private int position;
    private int capacity = Integer.MAX_VALUE;
    private boolean open;
    protected byte[] contents;

    public SeekableInMemoryByteChannel(int capacity) {
        this();
        this.capacity = capacity;
    }
    public SeekableInMemoryByteChannel() {
        this.open = true;
        synchronized (this) {
            this.position = 0;
            this.contents = new byte[0];
        }
    }

    @Override
    public boolean isOpen() {
        return this.open;
    }

    @Override
    public void close() {
        this.open = false;
        this.truncate(0);
    }

    @Override
    public int read(final ByteBuffer destination) {
        this.checkClosed();
        if (destination == null) {
            throw new IllegalArgumentException("Destination buffer must be supplied");
        }

        final int spaceInBuffer = destination.remaining();
        final int numBytesRemainingInContent;
        final int numBytesToRead;
        synchronized (this) {
            numBytesRemainingInContent = this.contents.length - this.position;
            if (numBytesRemainingInContent <= 0) {
                return -1;
            }
            numBytesToRead = numBytesRemainingInContent >= spaceInBuffer ? spaceInBuffer : numBytesRemainingInContent;
            destination.put(this.contents, this.position, numBytesToRead);
            this.position += numBytesToRead;
        }
        return numBytesToRead;
    }

    @Override
    public int write(final ByteBuffer source) {
        this.checkClosed();
        if (source == null) {
            throw new IllegalArgumentException("Source buffer must be supplied");
        }
        
        final int totalBytes = source.remaining();
        if (totalBytes > capacity || this.position + totalBytes > capacity) {
            throw new IOException("Reached maximum capacity of [" + capacity + "] bytes.");
        }
        
        final byte[] readContents = new byte[totalBytes];
        source.get(readContents, source.position(), readContents.length);

        synchronized (this) {
            this.contents = this.concat(this.contents, readContents, this.position);
            this.position += totalBytes;
            /**
             * Channel content will be overwritten by new source completely 
             * from existing position. Old trailing content will be cleared.
             */
            truncate(this.position()); 
        }

        return totalBytes;
    }

    @Override
    public long position() {
        synchronized (this) {
            return this.position;
        }
    }

    @Override
    public SeekableByteChannel position(final long newPosition) {
        if (newPosition > Integer.MAX_VALUE || newPosition < 0) {
            throw new IllegalArgumentException("Valid position for this channel is between 0 and " + Integer.MAX_VALUE);
        }
        synchronized (this) {
            this.position = (int) newPosition;
        }
        return this;
    }

    @Override
    public long size() {
        synchronized (this) {
            return this.contents.length;
        }
    }

    @Override
    public SeekableByteChannel truncate(final long size) {
        if (size < 0 || size > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("This implementation permits a size of 0 to " + Integer.MAX_VALUE + " inclusive");
        }
        synchronized (this) {
            final int newSize = (int) size;
            final int currentSize = (int) this.size();
            if (this.position > newSize) {
                this.position = newSize;
            }
            if (currentSize > newSize) {
                final byte[] newContents = new byte[newSize];
                System.arraycopy(this.contents, 0, newContents, 0, newSize);
                this.contents = newContents;
            }
        }
        return this;
    }

    @Override
    public String toString() {
        return convert(getContent());
    }

    public InputStream getContent() {
        final byte[] copy;
        synchronized (this) {
            final int length = this.contents.length;
            copy = new byte[length];
            System.arraycopy(this.contents, 0, copy, 0, this.contents.length);
        }
        return new ByteArrayInputStream(copy);
    }
    
    private byte[] concat(final byte[] input1, final byte[] input2, final int position) {
        assert input1 != null : "Input 1 must be specified";
        assert input2 != null : "Input 2 must be specified";
        assert position >= 0 : "Position must be 0 or higher";
        /**
         * Allocate a new array of enough space (either current size or position + input2.length, 
         * whichever is greater)
         */
        final int newSize = position + input2.length < input1.length ? input1.length : position + input2.length;
        final byte[] merged = new byte[newSize];
        System.arraycopy(input1, 0, merged, 0, input1.length);
        System.arraycopy(input2, 0, merged, position, input2.length);
        return merged;
    }

    private void checkClosed() {
        if (!this.isOpen()) {
            throw new IOException("Channel closed.");
        }
    }
    
    private String convert(InputStream is) {
        try (InputStream bis = is) {
            return new ByteSource() {
    
                @Override
                public InputStream openStream() throws java.io.IOException {
                    return bis;
                }
            }.asCharSource(Charset.forName(ENCODING)).read();
        } catch (java.io.IOException e) {
            throw new IOException(e.getMessage());
        } 
    }
}
