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

package file;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

import static java.nio.charset.Charset.*;
import static org.uberfire.java.nio.file.Files.*;
import static org.junit.Assert.*;

public class SimpleUsageTest {

    @Test
    public void readAllLinesNonExistentFile() throws IOException {
        final Path file = Paths.get("default:///path/to/file.txt");
        assertTrue(notExists(file));
        assertFalse(exists(file));
        try {
            readAllLines(file, defaultCharset());
            fail("didn't throw exception. expected expception: NoSuchFileException");
        } catch (NoSuchFileException ex) {
        } catch (Exception e) {
            fail("should raise: NoSuchFileException");
        }
    }

    @Test
    public void fileDoesNotExists() throws IOException {
        final Path file = Paths.get("default:///path/to/file.txt");
        assertFalse(exists(file));
        assertTrue(notExists(file));
    }

    @Test
    public void readAllLines_EmptyContent() throws IOException {
        final Path file = createTempFile("foo", null);
        assertFalse(notExists(file));
        assertTrue(exists(file));
        try {
            final List<String> content = readAllLines(file, defaultCharset());
            assertNotNull(content);
            assertEquals(0, content.size());
        } catch (NoSuchFileException ex) {
            fail("shouldn't raise exception.");
        }
    }

    @Test
    public void writeLines() throws IOException {
        final String content2Write = "my content here \\o/";
        final Path file = createTempFile("foo", null);
        assertFalse(notExists(file));
        assertTrue(exists(file));
        try {

            write(file, content2Write, Charset.forName("UTF-8"));

            final List<String> content = readAllLines(file, defaultCharset());
            assertNotNull(content);
            assertEquals(1, content.size());
            assertEquals(content2Write, content.get(0));
            assertTrue(exists(file));
        } catch (NoSuchFileException ex) {
            fail("shouldn't raise exception.");
        }
    }

    @Test
    public void whitespaceOnPath() throws IOException, URISyntaxException {
        final Path file = Paths.get("/path", "to some folder", "file.txt");
        assertEquals("default:///path/to%20some%20folder/file.txt", file.toUri().toString());

        final Path file2 = Paths.get("default:///path/to%20some%20folder/file.txt");
        assertEquals("default:///path/to%20some%20folder/file.txt", file2.toUri().toString());

        final Path file3 = Paths.get("C:\\Users\\JLIU\\.android");
        assertEquals("C:\\Users\\JLIU\\.android", file3.toString());
        assertEquals("default:///C:/Users/JLIU/.android", file3.toUri().toString());
    }

    @Test
    public void testSomething() {
        Iterator<Path> stream = newDirectoryStream(Paths.get(System.getProperty("user.home"))).iterator();
        while (stream.hasNext()) {
            final Path next = stream.next();
            System.out.println(next.getFileName().toString());
        }
    }

}
