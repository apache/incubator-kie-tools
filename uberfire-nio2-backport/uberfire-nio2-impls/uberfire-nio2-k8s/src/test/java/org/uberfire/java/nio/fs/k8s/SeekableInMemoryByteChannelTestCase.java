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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.java.nio.IOException;


public class SeekableInMemoryByteChannelTestCase {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(SeekableInMemoryByteChannelTestCase.class.getName());

    private static final String CONTENTS_SMALLER_BUFFER = "Small";
    private static final String CONTENTS_BIGGER_BUFFER = "Large............";

    private static final String UTF8 = "UTF-8";

    private SeekableInMemoryByteChannel channel;

    private ByteBuffer smallerBuffer;
    private ByteBuffer biggerBuffer;

    @Before
    public void init() throws UnsupportedEncodingException {
        this.channel = new SeekableInMemoryByteChannel();
        smallerBuffer = ByteBuffer.wrap(CONTENTS_SMALLER_BUFFER.getBytes(UTF8));
        biggerBuffer = ByteBuffer.wrap(CONTENTS_BIGGER_BUFFER.getBytes(UTF8));
    }

    @After
    public void closeChannel() throws IOException {
        if (this.channel.isOpen()) {
            this.channel.close();
        }
    }

    @Test(expected = org.uberfire.java.nio.IOException.class)
    public void readAfterCloseThrowsException() throws IOException {
        this.channel.close();
        this.channel.read(ByteBuffer.wrap(new byte[] {}));
    }

    @Test(expected = org.uberfire.java.nio.IOException.class)
    public void writeAfterCloseThrowsException() throws IOException {
        this.channel.close();
        this.channel.write(ByteBuffer.wrap(new byte[] {}));
    }

    @Test
    public void isOpenTrue() throws IOException {
        Assert.assertTrue("Channel should report open before it's closed", this.channel.isOpen());
    }

    @Test
    public void isOpenFalseAfterClose() throws IOException {
        this.channel.close();
        Assert.assertFalse("Channel should report not open after close", this.channel.isOpen());
    }

    @Test
    public void positionInit0() throws IOException {
        Assert.assertEquals("Channel should init to position 0", 0, this.channel.position());
    }

    @Test
    public void sizeInit0() throws IOException {
        Assert.assertEquals("Channel should init to size 0", 0, this.channel.size());
    }

    @Test(expected = IllegalArgumentException.class)
    public void readRequiresBuffer() throws IOException {
        this.channel.read(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void writeRequiresBuffer() throws IOException {
        this.channel.write(null);
    }

    @Test
    public void read() throws IOException, java.io.IOException {
        this.channel.write(smallerBuffer);
        final int newPosition = 2;
        final byte[] contents = new byte[2];
        // Read 2 bytes from the new position
        final int numBytesRead = this.channel.position(newPosition).read(ByteBuffer.wrap(contents));
        final String expected = "al";
        final String contentsRead = new String(contents, UTF8);
        Assert.assertEquals("Read should report correct number of bytes read", contents.length, numBytesRead);
        Assert.assertEquals("Channel should respect explicit position during reads", expected, contentsRead);
    }

    @Test
    public void getContent() throws IOException, java.io.IOException {
        this.channel.write(smallerBuffer);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(this.channel.getContent()));
        final String contents = reader.readLine();
        Assert.assertEquals("Contents read were not as expected", CONTENTS_SMALLER_BUFFER, contents);
    }

    @Test
    public void readDestinationBiggerThanChannel() throws IOException, java.io.IOException {
        this.channel.write(smallerBuffer);
        final ByteBuffer destination = biggerBuffer;
        Assert.assertTrue("Test setup incorrect, should be trying to read into a buffer greater than our size",
            destination.remaining() > this.channel.size());
        // Read more bytes than we currently have size
        final int numBytesRead = this.channel.position(0).read(destination);
        Assert.assertEquals("Read to a buffer greater than our size should read only up to our size",
            this.channel.size(), numBytesRead);
    }

    @Test
    public void nothingToRead() throws IOException, java.io.IOException {
        this.channel.write(smallerBuffer);
        // Read a byte from a position past the size
        final int numBytesRead = this.channel.position(this.channel.size() + 3).read(ByteBuffer.wrap(new byte[1]));
        Assert.assertEquals("Read on position > size should return -1", -1, numBytesRead);
    }

    @Test
    public void write() throws IOException, java.io.IOException {
        this.channel.write(smallerBuffer);
        final int newPosition = 2;
        final int numBytesWritten = this.channel.position(newPosition).write(ByteBuffer.wrap("DR".getBytes(UTF8)));
        // Read 2 bytes from the new position
        final byte[] contents = new byte[2];
        this.channel.position(newPosition).read(ByteBuffer.wrap(contents));
        final String expected = "DR";
        final String read = new String(contents, UTF8);
        Assert.assertEquals("Write should report correct number of bytes written", 2, numBytesWritten);
        Assert.assertEquals("Channel should respect explicit position during writes", expected, read);
    }

    @Test
    public void writeWithPositionPastSize() throws IOException, java.io.IOException {
        this.channel.write(smallerBuffer);
        smallerBuffer.clear();
        final int gap = 5;
        // Write again, after a gap past the current size
        this.channel.position(this.channel.size() + gap).write(smallerBuffer);
        smallerBuffer.clear();
        Assert.assertEquals("Channel size should be equal to the size of the writes we put in, plus "
            + "the gap when we set the position tpo be greater than the size", smallerBuffer.remaining() * 2 + gap,
            this.channel.size());
    }

    @Test
    public void positionSetPastSize() throws IOException {
        final int newPosition = 30;
        this.channel.position(newPosition);
        Assert.assertEquals("Channel should be able to be set past size", newPosition, this.channel.position());
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativePositionProhibited() throws IOException {
        this.channel.position(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceedMaxIntegerPositionProhibited() throws IOException {
        final long newPosition = Integer.MAX_VALUE + 1L;
        Assert.assertTrue("Didn't set up new position to be out of int bounds", newPosition > Integer.MAX_VALUE);
        this.channel.position(newPosition); // Exception expected
    }

    @Test(expected = IllegalArgumentException.class)
    public void negativeTruncateProhibited() throws IOException {
        this.channel.truncate(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void exceedMaxIntegerTruncateProhibited() throws IOException {
        final long truncateValue = Integer.MAX_VALUE + 1L;
        Assert.assertTrue("Didn't set up new truncate to be out of int bounds", truncateValue > Integer.MAX_VALUE);
        this.channel.truncate(truncateValue); // Exception expected
    }

    @Test
    public void size() throws IOException {
        this.channel.write(smallerBuffer);
        Assert.assertEquals("Channel should report correct size", this.smallerBuffer.clear().remaining(),
            this.channel.size());
    }

    @Test
    public void truncate() throws IOException {
        this.channel.write(smallerBuffer);
        final int newSize = (int) this.channel.size() - 3;
        this.channel.truncate(newSize);
        // Correct size?
        Assert.assertEquals("Channel should report correct size after truncate", newSize, this.channel.size());
        // Correct position?
        Assert.assertEquals("Channel should report adjusted position after truncate", newSize, this.channel.position());
    }

    @Test
    public void truncateLargerThanSizeRepositions() throws IOException {
        this.channel.write(smallerBuffer);
        final int oldSize = (int) this.channel.size();
        final int newSize = oldSize + 3;
        this.channel.truncate(newSize);
        // Size unchanged?
        Assert.assertEquals("Channel should report unchanged size after truncate to bigger value", oldSize,
            this.channel.size());
        // Correct position, beyond size?
        Assert.assertEquals("Channel should report unchanged position after truncate to bigger value", oldSize,
            this.channel.position());
    }

    @Test(expected = IOException.class)
    public void exceedMaxCapacityProhibited() throws IOException {
        this.channel = new SeekableInMemoryByteChannel(5);
        this.channel.write(biggerBuffer); // Exception expected
    }
    
    @Test
    public void emptyContentAfterClose() {
        this.channel.write(smallerBuffer);
        Assert.assertTrue(this.channel.toString().length() > 0);
        this.channel.close();
        Assert.assertTrue(this.channel.toString().length() == 0);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void readFromNullDestination() {
        this.channel.read(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void writeToNullSource() {
        this.channel.write(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidPosition() {
        this.channel.position(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidTruncate() {
        this.channel.truncate(-1);
    }

    @Test(expected = IOException.class)
    public void writeAfterClose() {
        this.channel.close();
        this.channel.write(ByteBuffer.wrap(new byte[0]));
    }

}
