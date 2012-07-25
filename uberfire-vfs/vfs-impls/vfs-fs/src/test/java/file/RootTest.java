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

import java.net.URI;

import org.junit.Test;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.Paths;

import static org.junit.Assert.*;

public class RootTest {

    @Test
    public void testWindowsRootPath() {
        final Path path = Paths.get(URI.create("file:///c:/path/to/file.txt"));
        System.out.println("testWindowsRootPath:path: " + path.toString());
        assertNotNull(path);
        assertNotNull(path.getRoot());
        assertEquals("c:", path.getRoot().toString());
    }

    @Test
    public void testUnixRootPath() {
        final Path path = Paths.get(URI.create("file:///path/to/file.txt"));
        System.out.println("testUnixRootPath:path: " + path.toString());
        assertNotNull(path);
        assertNotNull(path.getRoot());
        assertEquals("/", path.getRoot().toString());
    }

    @Test
    public void testDefaultWindowsRootPath() {
        final Path path = Paths.get(URI.create("default:///c:/path/to/file.txt"));
        System.out.println("testDefaultWindowsRootPath:path: " + path.toString());
        assertNotNull(path);
        assertNotNull(path.getRoot());
        assertEquals("c:", path.getRoot().toString());
    }

    @Test
    public void testDefaultUnixRootPath() {
        final Path path = Paths.get(URI.create("default:///path/to/file.txt"));
        System.out.println("testDefaultUnixRootPath:path: " + path.toString());
        assertNotNull(path);
        assertNotNull(path.getRoot());
        assertEquals("/", path.getRoot().toString());
    }

    @Test
    public void testWindowsDirectRootedPath() {
        final Path path = Paths.get("c:\\path\\to\\file.txt");
        System.out.println("testWindowsDirectRootedPath:path: " + path.toString());
        assertNotNull(path);
        assertNotNull(path.getRoot());
        assertEquals("c:", path.getRoot().toString());
    }

    @Test
    public void testWindows2DirectRootedPath() {
        final Path path = Paths.get("c:/path/to/file.txt");
        System.out.println("testWindows2DirectRootedPath:path: " + path.toString());
        assertNotNull(path);
        assertNotNull(path.getRoot());
        assertEquals("c:", path.getRoot().toString());
    }

    @Test
    public void testUnixDirectRootedPath() {
        final Path path = Paths.get("/path/to/file.txt");
        System.out.println("testUnixDirectRootedPath:path: " + path.toString());
        assertNotNull(path);
        assertNotNull(path.getRoot());
        assertEquals("/", path.getRoot().toString());
    }

    @Test
    public void testNonRootedComposedPath() {
        final Path path = Paths.get("some", "path", "to", "file.txt");
        System.out.println("testNonRootedComposedPath:path: " + path.toString());
        assertNotNull(path);
        assertNull(path.getRoot());
    }

    @Test
    public void testNonRootedPath() {
        final Path path = Paths.get("some/path/to/file.txt");
        System.out.println("testNonRootedPath:path: " + path.toString());
        assertNotNull(path);
        assertNull(path.getRoot());
    }

    @Test
    public void testNonRootedWindowsPath() {
        final Path path = Paths.get("some\\path\\to\\file.txt");
        System.out.println("testNonRootedWindowsPath:path: " + path.toString());
        assertNotNull(path);
        assertNull(path.getRoot());
    }

}
