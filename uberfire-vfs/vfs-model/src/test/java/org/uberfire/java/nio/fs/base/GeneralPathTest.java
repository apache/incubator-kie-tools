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

package org.uberfire.java.nio.fs.base;

import java.io.File;
import java.net.URI;

import org.junit.Test;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.spi.FileSystemProvider;

import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GeneralPathTest {

    private static final String DEFAULT_PATH = new File("").getAbsolutePath() + "/";

    final FileSystem fs = mock(FileSystem.class);

    @Test
    public void testSimpleAbsoluteUnix() {
        when(fs.getSeparator()).thenReturn("/");

        final Path path = GeneralPathImpl.create(fs, "/path/to/file.txt", false);

        assertThat(path).isNotNull();
        assertThat(path.isAbsolute()).isTrue();
        assertThat(path.toAbsolutePath()).isEqualTo(path);
        assertThat(path.toString()).isEqualTo("/path/to/file.txt");

        assertThat(path.getFileSystem()).isNotNull().isEqualTo(fs);

        assertThat(path.getFileName()).isNotNull();
        assertThat(path.getFileName().toString()).isEqualTo("file.txt");

        assertThat(path.getNameCount()).isEqualTo(3);

        assertThat(path.getName(0)).isNotNull();
        assertThat(path.getName(0).toString()).isEqualTo("path");

        assertThat(path.getName(1)).isNotNull();
        assertThat(path.getName(1).toString()).isEqualTo("to");

        assertThat(path.getName(2)).isNotNull().isEqualTo(path.getFileName());

        assertThat(path.subpath(0, 1)).isNotNull();
        assertThat(path.subpath(0, 1).toString()).isEqualTo("path");

        assertThat(path.subpath(1, 2)).isNotNull();
        assertThat(path.subpath(1, 2).toString()).isEqualTo("to");

        assertThat(path.subpath(0, 2)).isNotNull();
        assertThat(path.subpath(0, 2).toString()).isEqualTo("path/to");

        assertThat(path.subpath(1, 3)).isNotNull();
        assertThat(path.subpath(1, 3).toString()).isEqualTo("to/file.txt");

        int i = 0;
        for (final Path currentPath : path) {
            assertThat(currentPath).isEqualTo(path.getName(i));
            i++;
        }

        assertThat(path.getParent()).isNotNull();
        assertThat(path.getParent().toString()).isEqualTo("/path/to");

        assertThat(path.getParent().getParent()).isNotNull();
        assertThat(path.getParent().getParent().toString()).isEqualTo("/path");

        assertThat(path.getParent().getParent().getParent()).isNotNull();
        assertThat(path.getParent().getParent().getParent().toString()).isEqualTo("/");

        assertThat(path.getRoot().getParent()).isNull();

        assertThat(path.getRoot()).isNotNull();
        assertThat(path.getRoot().toString()).isNotNull().isEqualTo("/");
    }

    @Test
    public void testSimpleRootUnix() {
        when(fs.getSeparator()).thenReturn("/");

        final Path path = GeneralPathImpl.create(fs, "/", false);

        assertThat(path).isNotNull();
        assertThat(path.isAbsolute()).isTrue();
        assertThat(path.toString()).isEqualTo("/");
        assertThat(path.getFileName()).isNull();

        assertThat(path.getNameCount()).isEqualTo(0);

        assertThat(path.getRoot()).isNotNull().isEqualTo(path);

        try {
            path.getName(0);
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    public void testSimpleRelativeUnix() {
        when(fs.getSeparator()).thenReturn("/");

        final Path path = GeneralPathImpl.create(fs, "path/to/file.txt", false);

        assertThat(path).isNotNull();
        assertThat(path.isAbsolute()).isFalse();
        assertThat(path.toString()).isEqualTo("path/to/file.txt");

        assertThat(path.getFileSystem()).isNotNull().isEqualTo(fs);

        assertThat(path.getFileName()).isNotNull();
        assertThat(path.getFileName().toString()).isEqualTo("file.txt");

        assertThat(path.getNameCount()).isEqualTo(3);

        assertThat(path.getName(0)).isNotNull();
        assertThat(path.getName(0).toString()).isEqualTo("path");

        assertThat(path.getName(1)).isNotNull();
        assertThat(path.getName(1).toString()).isEqualTo("to");

        assertThat(path.getName(2)).isNotNull().isEqualTo(path.getFileName());

        assertThat(path.subpath(0, 1)).isNotNull();
        assertThat(path.subpath(0, 1).toString()).isEqualTo("path");

        assertThat(path.subpath(1, 2)).isNotNull();
        assertThat(path.subpath(1, 2).toString()).isEqualTo("to");

        assertThat(path.subpath(0, 2)).isNotNull();
        assertThat(path.subpath(0, 2).toString()).isEqualTo("path/to");

        assertThat(path.subpath(1, 3)).isNotNull();
        assertThat(path.subpath(1, 3).toString()).isEqualTo("to/file.txt");

        int i = 0;
        for (final Path currentPath : path) {
            assertThat(currentPath).isEqualTo(path.getName(i));
            i++;
        }

        assertThat(path.getParent()).isNotNull();
        assertThat(path.getParent().toString()).isEqualTo("path/to");

        assertThat(path.getParent().getParent()).isNotNull();
        assertThat(path.getParent().getParent().toString()).isEqualTo("path");

        assertThat(path.getParent().getParent().getParent()).isNull();

        assertThat(path.getRoot()).isNull();

        assertThat(path.toAbsolutePath()).isNotNull();
        assertThat(path.toAbsolutePath().toString()).isEqualTo(DEFAULT_PATH + "path/to/file.txt");
    }

    @Test
    public void testSimpleAbsoluteWindows() {
        when(fs.getSeparator()).thenReturn("\\");

        final Path path = GeneralPathImpl.create(fs, "c:\\path\\to\\file.txt", false);

        assertThat(path).isNotNull();
        assertThat(path.isAbsolute()).isTrue();
        assertThat(path.toAbsolutePath()).isEqualTo(path);
        assertThat(path.toString()).isEqualTo("c:\\path\\to\\file.txt");

        assertThat(path.getFileSystem()).isNotNull().isEqualTo(fs);

        assertThat(path.getFileName()).isNotNull();
        assertThat(path.getFileName().toString()).isEqualTo("file.txt");

        assertThat(path.getNameCount()).isEqualTo(3);

        assertThat(path.getName(0)).isNotNull();
        assertThat(path.getName(0).toString()).isEqualTo("path");

        assertThat(path.getName(1)).isNotNull();
        assertThat(path.getName(1).toString()).isEqualTo("to");

        assertThat(path.getName(2)).isNotNull().isEqualTo(path.getFileName());

        assertThat(path.subpath(0, 1)).isNotNull();
        assertThat(path.subpath(0, 1).toString()).isEqualTo("path");

        assertThat(path.subpath(1, 2)).isNotNull();
        assertThat(path.subpath(1, 2).toString()).isEqualTo("to");

        assertThat(path.subpath(0, 2)).isNotNull();
        assertThat(path.subpath(0, 2).toString()).isEqualTo("path\\to");

        assertThat(path.subpath(1, 3)).isNotNull();
        assertThat(path.subpath(1, 3).toString()).isEqualTo("to\\file.txt");

        int i = 0;
        for (final Path currentPath : path) {
            assertThat(currentPath).isEqualTo(path.getName(i));
            i++;
        }

        assertThat(path.getParent()).isNotNull();
        assertThat(path.getParent().toString()).isEqualTo("c:\\path\\to");

        assertThat(path.getParent().getParent()).isNotNull();
        assertThat(path.getParent().getParent().toString()).isEqualTo("c:\\path");

        assertThat(path.getParent().getParent().getParent()).isNotNull();
        assertThat(path.getParent().getParent().getParent().toString()).isEqualTo("c:\\");

        assertThat(path.getRoot().getParent()).isNull();

        assertThat(path.getRoot()).isNotNull();
        assertThat(path.getRoot().toString()).isNotNull().isEqualTo("c:\\");
    }

    @Test
    public void testSimpleRootWindows() {
        when(fs.getSeparator()).thenReturn("\\");

        final Path path = GeneralPathImpl.create(fs, "c:\\", false);

        assertThat(path).isNotNull();
        assertThat(path.isAbsolute()).isTrue();
        assertThat(path.toString()).isEqualTo("c:\\");
        assertThat(path.getFileName()).isNull();

        assertThat(path.getNameCount()).isEqualTo(0);

        assertThat(path.getRoot()).isNotNull().isEqualTo(path);

        try {
            path.getName(0);
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (Exception e) {
            assertThat(e).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Test
    public void testSimpleRelativeWindows() {
        when(fs.getSeparator()).thenReturn("\\");

        final Path path = GeneralPathImpl.create(fs, "path\\to\\file.txt", false);

        assertThat(path).isNotNull();
        assertThat(path.isAbsolute()).isFalse();
        assertThat(path.toString()).isEqualTo("path\\to\\file.txt");

        assertThat(path.getFileSystem()).isNotNull().isEqualTo(fs);

        assertThat(path.getFileName()).isNotNull();
        assertThat(path.getFileName().toString()).isEqualTo("file.txt");

        assertThat(path.getNameCount()).isEqualTo(3);

        assertThat(path.getName(0)).isNotNull();
        assertThat(path.getName(0).toString()).isEqualTo("path");

        assertThat(path.getName(1)).isNotNull();
        assertThat(path.getName(1).toString()).isEqualTo("to");

        assertThat(path.getName(2)).isNotNull().isEqualTo(path.getFileName());

        assertThat(path.subpath(0, 1)).isNotNull();
        assertThat(path.subpath(0, 1).toString()).isEqualTo("path");

        assertThat(path.subpath(1, 2)).isNotNull();
        assertThat(path.subpath(1, 2).toString()).isEqualTo("to");

        assertThat(path.subpath(0, 2)).isNotNull();
        assertThat(path.subpath(0, 2).toString()).isEqualTo("path\\to");

        assertThat(path.subpath(1, 3)).isNotNull();
        assertThat(path.subpath(1, 3).toString()).isEqualTo("to\\file.txt");

        int i = 0;
        for (final Path currentPath : path) {
            assertThat(currentPath).isEqualTo(path.getName(i));
            i++;
        }

        assertThat(path.getParent()).isNotNull();
        assertThat(path.getParent().toString()).isEqualTo("path\\to");

        assertThat(path.getParent().getParent()).isNotNull();
        assertThat(path.getParent().getParent().toString()).isEqualTo("path");

        assertThat(path.getParent().getParent().getParent()).isNull();

        assertThat(path.toAbsolutePath()).isNotNull();
        assertThat(path.toAbsolutePath().toString()).isEqualTo("C:" + DEFAULT_PATH.replaceAll("/", "\\\\") + "path\\to\\file.txt");

        assertThat(path.getRoot()).isNull();
    }

    @Test
    public void testToFile() throws Exception {
        when(fs.getSeparator()).thenReturn("/");

        final File file = File.createTempFile("foo", "bar");
        final Path path = GeneralPathImpl.newFromFile(fs, file);

        assertThat(path).isNotNull();
        assertThat(path.toString()).isEqualTo(file.getAbsolutePath());

        assertThat(path.toFile()).isEqualTo(file);
    }

    @Test
    public void testAbsloluteSimpleToURIUnix() throws Exception {
        final FileSystemProvider fsprovider = mock(FileSystemProvider.class);
        when(fsprovider.isDefault()).thenReturn(false);
        when(fsprovider.getScheme()).thenReturn("file");
        when(fs.provider()).thenReturn(fsprovider);

        when(fs.getSeparator()).thenReturn("/");

        final Path path = GeneralPathImpl.create(fs, "/path/to/file.txt", false);
        final URI uri = path.toUri();

        assertThat(path).isNotNull();
        assertThat(uri).isNotNull();
        assertThat(uri.toString()).isEqualTo("file:///path/to/file.txt");
    }

    @Test
    public void testAbsoluteToURIUnix() throws Exception {
        final FileSystemProvider fsprovider = mock(FileSystemProvider.class);
        when(fsprovider.isDefault()).thenReturn(true);
        when(fsprovider.getScheme()).thenReturn("file");
        when(fs.provider()).thenReturn(fsprovider);

        when(fs.getSeparator()).thenReturn("/");

        final Path path = GeneralPathImpl.create(fs, "/path/to/file.txt", false);
        final URI uri = path.toUri();

        assertThat(path).isNotNull();
        assertThat(uri).isNotNull();
        assertThat(uri.toString()).isEqualTo("default:///path/to/file.txt");

        final Path realPath = path.toRealPath();
        assertThat(realPath).isNotNull();
        assertThat(realPath.toRealPath()).isEqualTo(realPath);
        assertThat(realPath.toUri()).isNotNull();
        assertThat(realPath.toUri().toString()).isEqualTo("file:///path/to/file.txt");
    }

    @Test
    public void testRelativeToURIUnix() throws Exception {
        final FileSystemProvider fsprovider = mock(FileSystemProvider.class);
        when(fsprovider.isDefault()).thenReturn(true);
        when(fsprovider.getScheme()).thenReturn("file");
        when(fs.provider()).thenReturn(fsprovider);

        when(fs.getSeparator()).thenReturn("/");

        final Path path = GeneralPathImpl.create(fs, "path/to/file.txt", false);
        final URI uri = path.toUri();

        assertThat(path).isNotNull();
        assertThat(uri).isNotNull();

        assertThat(uri.toString()).isEqualTo("default://" + DEFAULT_PATH + "path/to/file.txt");

        assertThat(path.toRealPath().toUri().toString()).isEqualTo("file://" + DEFAULT_PATH + "path/to/file.txt");
    }

    @Test
    public void testAbsoluteToURIWindows() throws Exception {
        final FileSystemProvider fsprovider = mock(FileSystemProvider.class);
        when(fsprovider.isDefault()).thenReturn(true);
        when(fsprovider.getScheme()).thenReturn("file");
        when(fs.provider()).thenReturn(fsprovider);

        when(fs.getSeparator()).thenReturn("\\");

        final Path path = GeneralPathImpl.create(fs, "c:\\path\\to\\file.txt", false);
        final URI uri = path.toUri();

        assertThat(path).isNotNull();
        assertThat(uri).isNotNull();
        assertThat(uri.toString()).isEqualTo("default:///c:/path/to/file.txt");

        final Path realPath = path.toRealPath();

        assertThat(realPath).isNotNull();
        assertThat(realPath.toRealPath()).isEqualTo(realPath);
        assertThat(realPath.toUri()).isNotNull();
        assertThat(realPath.toUri().toString()).isEqualTo("file:///c:/path/to/file.txt");
    }

    @Test
    public void testRelativeToURIWindows() throws Exception {
        final FileSystemProvider fsprovider = mock(FileSystemProvider.class);
        when(fsprovider.isDefault()).thenReturn(true);
        when(fsprovider.getScheme()).thenReturn("file");
        when(fs.provider()).thenReturn(fsprovider);

        when(fs.getSeparator()).thenReturn("\\");

        final Path path = GeneralPathImpl.create(fs, "path\\to\\file.txt", false);
        final URI uri = path.toUri();

        assertThat(path).isNotNull();
        assertThat(uri).isNotNull();

        assertThat(uri.toString()).isEqualTo("default:///" + "C:" + DEFAULT_PATH + "path/to/file.txt");

        assertThat(path.toRealPath().toUri().toString()).isEqualTo("file:///" + "C:" + DEFAULT_PATH + "path/to/file.txt");
    }

}
