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
import java.net.URI;

import org.apache.commons.lang3.SystemUtils;
import org.junit.Test;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.spi.FileSystemProvider;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.uberfire.java.nio.base.GeneralPathImpl.create;

public class GeneralPathTest {

    private static final String DEFAULT_PATH = new File("").getAbsolutePath().replace('\\',
                                                                                      '/') + "/";

    final FileSystem fs = mock(FileSystem.class);

    @Test
    public void testSimpleAbsoluteUnix() {
        when(fs.getSeparator()).thenReturn("/");

        final Path path = create(fs,
                                 "/path/to/file.txt",
                                 false);

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

        assertThat(path.subpath(0,
                                1)).isNotNull();
        assertThat(path.subpath(0,
                                1).toString()).isEqualTo("path");

        assertThat(path.subpath(1,
                                2)).isNotNull();
        assertThat(path.subpath(1,
                                2).toString()).isEqualTo("to");

        assertThat(path.subpath(0,
                                2)).isNotNull();
        assertThat(path.subpath(0,
                                2).toString()).isEqualTo("path/to");

        assertThat(path.subpath(1,
                                3)).isNotNull();
        assertThat(path.subpath(1,
                                3).toString()).isEqualTo("to/file.txt");

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

        final Path path = create(fs,
                                 "/",
                                 false);

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
    public void startsWith() {
        when(fs.getSeparator()).thenReturn("/");

        final Path path = create(fs,
                                 "/path/to/file.txt",
                                 false);

        assertTrue(path.startsWith(create(fs,
                                          "/path",
                                          false)));
        assertTrue(path.startsWith(create(fs,
                                          "/path/to",
                                          false)));
        assertTrue(path.startsWith(create(fs,
                                          "/path/to/file.txt",
                                          false)));
        assertFalse(path.startsWith(create(fs,
                                           "/p/th/to/file.txt",
                                           false)));
        assertFalse(path.startsWith(create(fs,
                                           "/some/other/path/to/file.txt",
                                           false)));
        assertFalse(path.startsWith(create(fs,
                                           "path/to/file.txt",
                                           false)));
        assertFalse(path.startsWith(create(fs,
                                           "path/to",
                                           false)));
        assertFalse(path.startsWith(create(fs,
                                           "to",
                                           false)));
    }

    @Test
    public void startsWithWindows() {
        when(fs.getSeparator()).thenReturn("\\");

        final Path path = create(fs,
                                 "c:\\path\\to\\file.txt",
                                 false);
        final Path unixFormatPath = create(fs,
                                           "/c:/path/to/file.txt",
                                           false);

        assertTrue(path.startsWith(create(fs,
                                          "c:\\",
                                          false)));
        assertTrue(path.startsWith(create(fs,
                                          "c:\\path",
                                          false)));
        assertTrue(path.startsWith(create(fs,
                                          "c:\\path\\to\\",
                                          false)));
        assertTrue(path.startsWith(create(fs,
                                          "c:\\path\\to\\file.txt",
                                          false)));
        assertFalse(path.startsWith(create(fs,
                                           "c:\\to\\file.txt",
                                           false)));
        assertFalse(path.startsWith(create(fs,
                                           "d:\\",
                                           false)));
        assertFalse(path.startsWith(create(fs,
                                           "/d:/",
                                           false)));
        assertFalse(path.startsWith(create(fs,
                                           "d:\\path\\to\\file.txt",
                                           false)));
        assertTrue(path.startsWith(create(fs,
                                          "/c:/",
                                          false)));
        assertTrue(path.startsWith(create(fs,
                                          "/c:/path/",
                                          false)));
        assertTrue(unixFormatPath.startsWith(create(fs,
                                                    "c:\\",
                                                    false)));
        assertTrue(unixFormatPath.startsWith(path));
        assertTrue(path.startsWith(unixFormatPath));
    }

    @Test
    public void endsWith() {
        when(fs.getSeparator()).thenReturn("/");

        final Path path = create(fs,
                                 "/path/to/file.txt",
                                 false);

        assertTrue(path.endsWith(create(fs,
                                        "file.txt",
                                        false)));
        assertTrue(path.endsWith(create(fs,
                                        "to/file.txt",
                                        false)));
        assertTrue(path.endsWith(create(fs,
                                        "/path/to/file.txt",
                                        false)));
        assertFalse(path.endsWith(create(fs,
                                         "filename.txt",
                                         false)));
        assertFalse(path.endsWith(create(fs,
                                         "/some/other/path/to/file.txt",
                                         false)));
        assertFalse(path.endsWith(create(fs,
                                         "txt",
                                         false)));
    }

    @Test
    public void endsWithWindows() {
        when(fs.getSeparator()).thenReturn("\\");

        final Path path = create(fs,
                                 "c:\\path\\to\\file.txt",
                                 false);
        final Path unixFormatPath = create(fs,
                                           "/c:/path/to/file.txt",
                                           false);

        assertTrue(path.endsWith(create(fs,
                                        "file.txt",
                                        false)));
        assertFalse(path.endsWith(create(fs,
                                         "anotherfile.txt",
                                         false)));
        assertTrue(path.endsWith(create(fs,
                                        "to\\file.txt",
                                        false)));
        assertTrue(path.endsWith(create(fs,
                                        "to/file.txt",
                                        false)));
        assertFalse(path.endsWith(create(fs,
                                         "c:\\different\\path\\to\\file.txt",
                                         false)));
        assertFalse(path.endsWith(create(fs,
                                         "d:\\path\\to\\another\\file.txt",
                                         false)));
        assertTrue(unixFormatPath.endsWith(create(fs,
                                                  "to\\file.txt",
                                                  false)));
        assertTrue(path.endsWith(unixFormatPath));
        assertTrue(unixFormatPath.endsWith(path));
    }

    @Test
    public void testSimpleRelativeUnix() {
        when(fs.getSeparator()).thenReturn("/");

        final Path path = create(fs,
                                 "path/to/file.txt",
                                 false);

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

        assertThat(path.subpath(0,
                                1)).isNotNull();
        assertThat(path.subpath(0,
                                1).toString()).isEqualTo("path");

        assertThat(path.subpath(1,
                                2)).isNotNull();
        assertThat(path.subpath(1,
                                2).toString()).isEqualTo("to");

        assertThat(path.subpath(0,
                                2)).isNotNull();
        assertThat(path.subpath(0,
                                2).toString()).isEqualTo("path/to");

        assertThat(path.subpath(1,
                                3)).isNotNull();
        assertThat(path.subpath(1,
                                3).toString()).isEqualTo("to/file.txt");

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

        final Path path = create(fs,
                                 "c:\\path\\to\\file.txt",
                                 false);

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

        assertThat(path.subpath(0,
                                1)).isNotNull();
        assertThat(path.subpath(0,
                                1).toString()).isEqualTo("path");

        assertThat(path.subpath(1,
                                2)).isNotNull();
        assertThat(path.subpath(1,
                                2).toString()).isEqualTo("to");

        assertThat(path.subpath(0,
                                2)).isNotNull();
        assertThat(path.subpath(0,
                                2).toString()).isEqualTo("path\\to");

        assertThat(path.subpath(1,
                                3)).isNotNull();
        assertThat(path.subpath(1,
                                3).toString()).isEqualTo("to\\file.txt");

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

        final Path path = create(fs,
                                 "c:\\",
                                 false);

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

        final Path path = create(fs,
                                 "path\\to\\file.txt",
                                 false);

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

        assertThat(path.subpath(0,
                                1)).isNotNull();
        assertThat(path.subpath(0,
                                1).toString()).isEqualTo("path");

        assertThat(path.subpath(1,
                                2)).isNotNull();
        assertThat(path.subpath(1,
                                2).toString()).isEqualTo("to");

        assertThat(path.subpath(0,
                                2)).isNotNull();
        assertThat(path.subpath(0,
                                2).toString()).isEqualTo("path\\to");

        assertThat(path.subpath(1,
                                3)).isNotNull();
        assertThat(path.subpath(1,
                                3).toString()).isEqualTo("to\\file.txt");

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
        assertWindowsPath(path.toAbsolutePath().toString(),
                          DEFAULT_PATH.replaceAll("/",
                                                  "\\\\") + "path\\to\\file.txt");

        assertThat(path.getRoot()).isNull();
    }

    @Test
    public void testToFile() throws Exception {
        when(fs.getSeparator()).thenReturn("/");

        final File file = File.createTempFile("foo",
                                              "bar");
        final Path path = GeneralPathImpl.newFromFile(fs,
                                                      file);

        assertThat(path).isNotNull();
        assertThat(path.toString()).isEqualTo(file.getAbsolutePath());

        assertThat(path.toFile()).isEqualTo(file);
    }

    @Test
    public void testAbsloluteSimpleToURIUnix() {
        final FileSystemProvider fsprovider = mock(FileSystemProvider.class);
        when(fsprovider.isDefault()).thenReturn(false);
        when(fsprovider.getScheme()).thenReturn("file");
        when(fs.provider()).thenReturn(fsprovider);

        when(fs.getSeparator()).thenReturn("/");

        final Path path = create(fs,
                                 "/path/to/file.txt",
                                 false);
        final URI uri = path.toUri();

        assertThat(path).isNotNull();
        assertThat(uri).isNotNull();
        assertThat(uri.toString()).isEqualTo("file:///path/to/file.txt");
    }

    @Test
    public void testAbsoluteToURIUnix() {
        final FileSystemProvider fsprovider = mock(FileSystemProvider.class);
        when(fsprovider.isDefault()).thenReturn(true);
        when(fsprovider.getScheme()).thenReturn("file");
        when(fs.provider()).thenReturn(fsprovider);

        when(fs.getSeparator()).thenReturn("/");

        final Path path = create(fs,
                                 "/path/to/file.txt",
                                 false);
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
    public void testRelativeToURIUnix() {
        final FileSystemProvider fsprovider = mock(FileSystemProvider.class);
        when(fsprovider.isDefault()).thenReturn(true);
        when(fsprovider.getScheme()).thenReturn("file");
        when(fs.provider()).thenReturn(fsprovider);

        when(fs.getSeparator()).thenReturn("/");

        final Path path = create(fs,
                                 "path/to/file.txt",
                                 false);
        final URI uri = path.toUri();

        assertThat(path).isNotNull();
        assertThat(uri).isNotNull();

        assertThat(uri.toString()).isEqualTo("default://" + DEFAULT_PATH + "path/to/file.txt");

        assertThat(path.toRealPath().toUri().toString()).isEqualTo("file://" + DEFAULT_PATH + "path/to/file.txt");
    }

    @Test
    public void testAbsoluteToURIWindows() {
        final FileSystemProvider fsprovider = mock(FileSystemProvider.class);
        when(fsprovider.isDefault()).thenReturn(true);
        when(fsprovider.getScheme()).thenReturn("file");
        when(fs.provider()).thenReturn(fsprovider);

        when(fs.getSeparator()).thenReturn("\\");

        final Path path = create(fs,
                                 "c:\\path\\to\\file.txt",
                                 false);
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
    public void testRelativeToURIWindows() {
        final FileSystemProvider fsprovider = mock(FileSystemProvider.class);
        when(fsprovider.isDefault()).thenReturn(true);
        when(fsprovider.getScheme()).thenReturn("file");
        when(fs.provider()).thenReturn(fsprovider);

        when(fs.getSeparator()).thenReturn("\\");

        final Path path = create(fs,
                                 "path\\to\\file.txt",
                                 false);
        final URI uri = path.toUri();

        assertThat(uri).isNotNull();
        assertWindowsUri(uri.toString(),
                         "default:///",
                         DEFAULT_PATH + "path/to/file.txt");

        assertThat(path).isNotNull();
        assertWindowsUri(path.toRealPath().toUri().toString(),
                         "file:///",
                         DEFAULT_PATH + "path/to/file.txt");
    }

    @Test
    public void testResolve() {
        final FileSystemProvider fsprovider = mock(FileSystemProvider.class);
        when(fsprovider.isDefault()).thenReturn(true);
        when(fsprovider.getScheme()).thenReturn("file");
        when(fs.provider()).thenReturn(fsprovider);

        when(fs.getSeparator()).thenReturn("/");

        final Path path = create(fs,
                                 "/path/to/",
                                 false);

        final Path resolvedPath = path.resolve("some/file.text");

        assertThat(resolvedPath).isNotNull();
        assertThat(resolvedPath.toString()).isEqualTo("/path/to/some/file.text");

        final Path resolvedPath2 = path.resolve("/some/file.text");

        assertThat(resolvedPath2).isNotNull();
        assertThat(resolvedPath2.toString()).isEqualTo("/some/file.text");

        final Path path2 = create(fs,
                                  "/path/to",
                                  false);

        final Path resolvedPath3 = path2.resolve("/some/file.text");
        assertThat(resolvedPath3).isNotNull();
        assertThat(resolvedPath3.toString()).isEqualTo("/some/file.text");

        final Path resolvedPath4 = path2.resolve("some/file.text");
        assertThat(resolvedPath4).isNotNull();
        assertThat(resolvedPath4.toString()).isEqualTo("/path/to/some/file.text");

        final Path resolvedPath5 = path2.resolve("");
        assertThat(resolvedPath5).isNotNull();
        assertThat(resolvedPath5.toString()).isEqualTo(path2.toString());
        assertThat(resolvedPath5).isEqualTo(path2);
    }

    @Test
    public void checkResolveNull() {
        final FileSystemProvider fsprovider = mock(FileSystemProvider.class);
        when(fsprovider.isDefault()).thenReturn(true);
        when(fsprovider.getScheme()).thenReturn("file");
        when(fs.provider()).thenReturn(fsprovider);

        when(fs.getSeparator()).thenReturn("/");

        final Path path = create(fs,
                                 "/path/to/",
                                 false);

        assertThatThrownBy(() -> path.resolve((String) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'other' should be not null!");
    }

    @Test
    public void checkResolveNull2() {
        final FileSystemProvider fsprovider = mock(FileSystemProvider.class);
        when(fsprovider.isDefault()).thenReturn(true);
        when(fsprovider.getScheme()).thenReturn("file");
        when(fs.provider()).thenReturn(fsprovider);

        when(fs.getSeparator()).thenReturn("/");

        final Path path = create(fs,
                                 "/path/to/",
                                 false);

        assertThatThrownBy(() -> path.resolve((Path) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'other' should be not null!");
    }

    @Test
    public void testNormalize() {
        final FileSystemProvider fsprovider = mock(FileSystemProvider.class);
        when(fsprovider.isDefault()).thenReturn(true);
        when(fsprovider.getScheme()).thenReturn("file");
        when(fs.provider()).thenReturn(fsprovider);

        when(fs.getSeparator()).thenReturn("/");

        final Path path = create(fs,
                                 "/path/to/",
                                 false);

        assertThat(path.normalize()).isNotNull().isEqualTo(path);
        assertThat(path.normalize().normalize()).isNotNull().isEqualTo(path.normalize());

        final Path path2 = create(fs,
                                  "/some/path/../../to",
                                  false);

        assertThat(path2.normalize()).isNotNull();
        assertThat(path2.normalize().toString()).isEqualTo("/to");
        assertThat(path2.normalize().normalize()).isNotNull().isEqualTo(path2.normalize());
    }

    @Test
    public void testNormalizeWindows() {
        final FileSystemProvider fsprovider = mock(FileSystemProvider.class);
        when(fsprovider.isDefault()).thenReturn(true);
        when(fsprovider.getScheme()).thenReturn("file");
        when(fs.provider()).thenReturn(fsprovider);

        when(fs.getSeparator()).thenReturn("\\");

        final Path path = create(fs,
                                 "c:\\path\\to\\",
                                 false);

        assertThat(path.normalize()).isNotNull().isEqualTo(path);
        assertThat(path.normalize().normalize()).isNotNull().isEqualTo(path.normalize());

        final Path path2 = create(fs,
                                  "c:\\some\\path\\..\\..\\to",
                                  false);

        assertThat(path2.normalize()).isNotNull();
        assertThat(path2.normalize().toString()).isEqualTo("c:\\to");
        assertThat(path2.normalize().normalize()).isNotNull().isEqualTo(path2.normalize());
    }

    @Test
    public void testResolveSibling() {
        final FileSystemProvider fsprovider = mock(FileSystemProvider.class);
        when(fsprovider.isDefault()).thenReturn(true);
        when(fsprovider.getScheme()).thenReturn("file");
        when(fs.provider()).thenReturn(fsprovider);

        when(fs.getSeparator()).thenReturn("/");

        final Path path = create(fs,
                                 "/path/to/",
                                 false);

        final Path resolvedPath = path.resolveSibling("some/file.text");

        assertThat(resolvedPath).isNotNull();
        assertThat(resolvedPath.toString()).isEqualTo("/path/some/file.text");

        final Path resolvedPath2 = path.resolveSibling("/some/file.text");

        assertThat(resolvedPath2).isNotNull();
        assertThat(resolvedPath2.toString()).isEqualTo("/some/file.text");

        final Path path2 = create(fs,
                                  "/path/to",
                                  false);

        final Path resolvedPath3 = path2.resolveSibling("/some/file.text");
        assertThat(resolvedPath3).isNotNull();
        assertThat(resolvedPath3.toString()).isEqualTo("/some/file.text");

        final Path resolvedPath4 = path2.resolveSibling("some/file.text");
        assertThat(resolvedPath4).isNotNull();
        assertThat(resolvedPath4.toString()).isEqualTo("/path/some/file.text");

        final Path resolvedPath5 = path2.resolveSibling("");
        assertThat(resolvedPath5).isNotNull();
        assertThat(resolvedPath5.toString()).isEqualTo(path2.getParent().toString());
        assertThat(resolvedPath5).isEqualTo(path2.getParent());
    }

    @Test
    public void testRelativize() {
        final FileSystemProvider fsprovider = mock(FileSystemProvider.class);
        when(fsprovider.isDefault()).thenReturn(true);
        when(fsprovider.getScheme()).thenReturn("file");
        when(fs.provider()).thenReturn(fsprovider);

        when(fs.getSeparator()).thenReturn("/");

        final Path path = create(fs,
                                 "/path/to",
                                 false);
        final Path other = create(fs,
                                  "/path/to/some/place",
                                  false);

        final Path relative = path.relativize(other);
        assertThat(relative).isNotNull();
        assertThat(relative.toString()).isEqualTo("some/place");

        final Path path2 = create(fs,
                                  "/path/to/some/place",
                                  false);
        final Path other2 = create(fs,
                                   "/path/to",
                                   false);

        final Path relative2 = path2.relativize(other2);
        assertThat(relative2).isNotNull();
        assertThat(relative2.toString()).isEqualTo("../..");

        final Path path3 = create(fs,
                                  "/path/to",
                                  false);
        final Path other3 = create(fs,
                                   "/path/to",
                                   false);

        final Path relative3 = path3.relativize(other3);
        assertThat(relative3).isNotNull();
        assertThat(relative3.toString()).isEqualTo("");

        final Path path4 = create(fs,
                                  "path/to",
                                  false);
        final Path other4 = create(fs,
                                   "path/to/some/place",
                                   false);

        final Path relative4 = path4.relativize(other4);
        assertThat(relative4).isNotNull();
        assertThat(relative4.toString()).isEqualTo("some/place");

        final Path path5 = create(fs,
                                  "path/to",
                                  false);
        final Path other5 = create(fs,
                                   "some/place",
                                   false);

        final Path relative5 = path5.relativize(other5);
        assertThat(relative5).isNotNull();
        assertThat(relative5.toString()).isEqualTo("../../some/place");

        final Path path6 = create(fs,
                                  "some/place",
                                  false);
        final Path other6 = create(fs,
                                   "path/to",
                                   false);

        final Path relative6 = path6.relativize(other6);
        assertThat(relative6).isNotNull();
        assertThat(relative6.toString()).isEqualTo("../../path/to");

        final Path path7 = create(fs,
                                  "path/to/some/thing/here",
                                  false);
        final Path other7 = create(fs,
                                   "some/place",
                                   false);

        final Path relative7 = path7.relativize(other7);
        assertThat(relative7).isNotNull();
        assertThat(relative7.toString()).isEqualTo("../../../../../some/place");

        final Path path8 = create(fs,
                                  "some/place",
                                  false);
        final Path other8 = create(fs,
                                   "path/to/some/thing/here",
                                   false);

        final Path relative8 = path8.relativize(other8);
        assertThat(relative8).isNotNull();
        assertThat(relative8.toString()).isEqualTo("../../path/to/some/thing/here");

        final Path path9 = create(fs,
                                  "/path/to",
                                  false);
        final Path other9 = create(fs,
                                   "/path/to",
                                   false);

        final Path relative9 = path9.relativize(other9);
        assertThat(relative9).isNotNull();
        assertThat(relative9.toString()).isEqualTo("");

        final Path path10 = create(fs,
                                   "path/to",
                                   false);
        final Path other10 = create(fs,
                                    "path/to",
                                    false);

        final Path relative10 = path10.relativize(other10);
        assertThat(relative10).isNotNull();
        assertThat(relative10.toString()).isEqualTo("");

        final Path path11 = create(fs,
                                   "",
                                   false);
        final Path other11 = create(fs,
                                    "path/to",
                                    false);

        final Path relative11 = path11.relativize(other11);
        assertThat(relative11).isNotNull().isEqualTo(other11);
    }

    @Test
    public void testRelativizeIlegal1() {
        final FileSystemProvider fsprovider = mock(FileSystemProvider.class);
        when(fsprovider.isDefault()).thenReturn(true);
        when(fsprovider.getScheme()).thenReturn("file");
        when(fs.provider()).thenReturn(fsprovider);

        when(fs.getSeparator()).thenReturn("/");

        final Path path = create(fs,
                                 "/path/to",
                                 false);
        final Path other = create(fs,
                                  "some/place",
                                  false);

        assertThatThrownBy(() -> path.relativize(other))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Could not relativize path 'otherx', 'isAbsolute()' for 'this' and 'otherx' should be equal.");
    }

    @Test
    public void testRelativizeIlegal2() {
        final FileSystemProvider fsprovider = mock(FileSystemProvider.class);
        when(fsprovider.isDefault()).thenReturn(true);
        when(fsprovider.getScheme()).thenReturn("file");
        when(fs.provider()).thenReturn(fsprovider);

        when(fs.getSeparator()).thenReturn("/");

        final Path path = create(fs,
                                 "some/place",
                                 false);
        final Path other = create(fs,
                                  "/path/to",
                                  false);

        assertThatThrownBy(() -> path.relativize(other))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Could not relativize path 'otherx', 'isAbsolute()' for 'this' and 'otherx' should be equal.");
    }

    @Test
    public void testRelativizeIlegal3() {
        final FileSystemProvider fsprovider = mock(FileSystemProvider.class);
        when(fsprovider.isDefault()).thenReturn(true);
        when(fsprovider.getScheme()).thenReturn("file");
        when(fs.provider()).thenReturn(fsprovider);

        when(fs.getSeparator()).thenReturn("/");

        final Path path = create(fs,
                                 "",
                                 false);
        final Path other = create(fs,
                                  "/path/to",
                                  false);

        assertThatThrownBy(() -> path.relativize(other))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Could not relativize path 'otherx', 'isAbsolute()' for 'this' and 'otherx' should be equal.");
    }

    @Test
    public void testRelativizeWindows() {
        final FileSystemProvider fsprovider = mock(FileSystemProvider.class);
        when(fsprovider.isDefault()).thenReturn(true);
        when(fsprovider.getScheme()).thenReturn("file");
        when(fs.provider()).thenReturn(fsprovider);

        when(fs.getSeparator()).thenReturn("\\");

        final Path path = create(fs,
                                 "c:\\path\\to",
                                 false);
        final Path other = create(fs,
                                  "c:\\path\\to\\some\\place",
                                  false);

        final Path relative = path.relativize(other);
        assertThat(relative).isNotNull();
        assertThat(relative.toString()).isEqualTo("some\\place");

        final Path path2 = create(fs,
                                  "c:\\path\\to\\some\\place",
                                  false);
        final Path other2 = create(fs,
                                   "c:\\path\\to",
                                   false);

        final Path relative2 = path2.relativize(other2);
        assertThat(relative2).isNotNull();
        assertThat(relative2.toString()).isEqualTo("..\\..");

        final Path path3 = create(fs,
                                  "c:\\path/to",
                                  false);
        final Path other3 = create(fs,
                                   "c:\\path/to",
                                   false);

        final Path relative3 = path3.relativize(other3);
        assertThat(relative3).isNotNull();
        assertThat(relative3.toString()).isEqualTo("");

        final Path path4 = create(fs,
                                  "path\\to",
                                  false);
        final Path other4 = create(fs,
                                   "path\\to\\some\\place",
                                   false);

        final Path relative4 = path4.relativize(other4);
        assertThat(relative4).isNotNull();
        assertThat(relative4.toString()).isEqualTo("some\\place");

        final Path path5 = create(fs,
                                  "path\\to",
                                  false);
        final Path other5 = create(fs,
                                   "some\\place",
                                   false);

        final Path relative5 = path5.relativize(other5);
        assertThat(relative5).isNotNull();
        assertThat(relative5.toString()).isEqualTo("..\\..\\some\\place");

        final Path path6 = create(fs,
                                  "some\\place",
                                  false);
        final Path other6 = create(fs,
                                   "path\\to",
                                   false);

        final Path relative6 = path6.relativize(other6);
        assertThat(relative6).isNotNull();
        assertThat(relative6.toString()).isEqualTo("..\\..\\path\\to");

        final Path path7 = create(fs,
                                  "path\\to\\some\\thing\\here",
                                  false);
        final Path other7 = create(fs,
                                   "some\\place",
                                   false);

        final Path relative7 = path7.relativize(other7);
        assertThat(relative7).isNotNull();
        assertThat(relative7.toString()).isEqualTo("..\\..\\..\\..\\..\\some\\place");

        final Path path8 = create(fs,
                                  "some\\place",
                                  false);
        final Path other8 = create(fs,
                                   "path\\to\\some\\thing\\here",
                                   false);

        final Path relative8 = path8.relativize(other8);
        assertThat(relative8).isNotNull();
        assertThat(relative8.toString()).isEqualTo("..\\..\\path\\to\\some\\thing\\here");

        final Path path9 = create(fs,
                                  "c:\\path\\to",
                                  false);
        final Path other9 = create(fs,
                                   "c:\\path\\to",
                                   false);

        final Path relative9 = path9.relativize(other9);
        assertThat(relative9).isNotNull();
        assertThat(relative9.toString()).isEqualTo("");

        final Path path10 = create(fs,
                                   "path\\to",
                                   false);
        final Path other10 = create(fs,
                                    "path\\to",
                                    false);

        final Path relative10 = path10.relativize(other10);
        assertThat(relative10).isNotNull();
        assertThat(relative10.toString()).isEqualTo("");

        final Path path11 = create(fs,
                                   "",
                                   false);
        final Path other11 = create(fs,
                                    "path\\to",
                                    false);

        final Path relative11 = path11.relativize(other11);
        assertThat(relative11).isNotNull().isEqualTo(other11);

        final Path path12 = create(fs,
                                   "/c:/path/to",
                                   false);
        final Path other12 = create(fs,
                                    "c:\\path\\to\\some\\place",
                                    false);

        final Path relative12 = path12.relativize(other12);
        assertThat(relative12).isNotNull();
        assertThat(relative12.toString()).isEqualTo("some\\place");

        final Path path13 = create(fs,
                                   "c:\\path\\to\\some\\place",
                                   false);
        final Path other13 = create(fs,
                                    "/c:/path/to",
                                    false);

        final Path relative13 = path13.relativize(other13);
        assertThat(relative13).isNotNull();
        assertThat(relative13.toString()).isEqualTo("..\\..");

        final Path path14 = create(fs,
                                   "/c:/path/to/some/place",
                                   false);
        final Path other14 = create(fs,
                                    "c:\\path\\to",
                                    false);

        final Path relative14 = path14.relativize(other14);
        assertThat(relative14).isNotNull();
        assertThat(relative14.toString()).isEqualTo("../..");

        final Path path15 = create(fs,
                                   "/c:/path/to/some/place",
                                   false);
        final Path other15 = create(fs,
                                    "c:\\path\\to\\some\\other\\place",
                                    false);

        final Path relative15 = path15.relativize(other15);
        assertThat(relative15).isNotNull();
        assertThat(relative15.toString()).isEqualTo("../other/place");

        final Path path16 = create(fs,
                                   "c:\\path\\to\\some\\place",
                                   false);
        final Path other16 = create(fs,
                                    "/c:/path/to/some/other/place",
                                    false);

        final Path relative16 = path16.relativize(other16);
        assertThat(relative16).isNotNull();
        assertThat(relative16.toString()).isEqualTo("..\\other\\place");

        final Path path17 = create(fs,
                                   "c:\\path\\to\\some\\place",
                                   false);
        final Path other17 = create(fs,
                                    "/c:/path/to/some/place",
                                    false);

        final Path relative17 = path17.relativize(other17);
        assertThat(relative17).isNotNull();
        assertThat(relative17.toString()).isEmpty();
        assertThat(other17.relativize(path17).toString().isEmpty());
    }

    @Test
    public void testRelativizeWindowsIllegal1() {
        final FileSystemProvider fsprovider = mock(FileSystemProvider.class);
        when(fsprovider.isDefault()).thenReturn(true);
        when(fsprovider.getScheme()).thenReturn("file");
        when(fs.provider()).thenReturn(fsprovider);

        when(fs.getSeparator()).thenReturn("\\");

        final Path path = create(fs,
                                 "c:\\path\\to",
                                 false);
        final Path other = create(fs,
                                  "some\\place",
                                  false);

        assertThatThrownBy(() -> path.relativize(other))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Could not relativize path 'otherx', 'isAbsolute()' for 'this' and 'otherx' should be equal.");
    }

    @Test
    public void testRelativizeWindowsIllegal2() {
        final FileSystemProvider fsprovider = mock(FileSystemProvider.class);
        when(fsprovider.isDefault()).thenReturn(true);
        when(fsprovider.getScheme()).thenReturn("file");
        when(fs.provider()).thenReturn(fsprovider);

        when(fs.getSeparator()).thenReturn("\\");

        final Path path = create(fs,
                                 "some\\place",
                                 false);
        final Path other = create(fs,
                                  "c:\\path\\to",
                                  false);

        assertThatThrownBy(() -> path.relativize(other))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Could not relativize path 'otherx', 'isAbsolute()' for 'this' and 'otherx' should be equal.");
    }

    @Test
    public void testRelativizeWindowsIllegal3() {
        final FileSystemProvider fsprovider = mock(FileSystemProvider.class);
        when(fsprovider.isDefault()).thenReturn(true);
        when(fsprovider.getScheme()).thenReturn("file");
        when(fs.provider()).thenReturn(fsprovider);

        when(fs.getSeparator()).thenReturn("\\");

        final Path path = create(fs,
                                 "",
                                 false);
        final Path other = create(fs,
                                  "c:\\path\\to",
                                  false);

        assertThatThrownBy(() -> path.relativize(other))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Could not relativize path 'otherx', 'isAbsolute()' for 'this' and 'otherx' should be equal.");
    }

    @Test
    public void testRelativizeWindowsIllegal4() {
        final FileSystemProvider fsprovider = mock(FileSystemProvider.class);
        when(fsprovider.isDefault()).thenReturn(true);
        when(fsprovider.getScheme()).thenReturn("file");
        when(fs.provider()).thenReturn(fsprovider);

        when(fs.getSeparator()).thenReturn("\\");

        final Path path = create(fs,
                                 "d:\\path\\to",
                                 false);
        final Path other = create(fs,
                                  "c:\\path\\to",
                                  false);

        assertThatThrownBy(() -> path.relativize(other))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Could not relativize path 'otherx', 'getRoot()' for 'this' and 'otherx' should be equal.");
    }

    @Test
    public void testRelativizeWindowsIllegal5() {
        final FileSystemProvider fsprovider = mock(FileSystemProvider.class);
        when(fsprovider.isDefault()).thenReturn(true);
        when(fsprovider.getScheme()).thenReturn("file");
        when(fs.provider()).thenReturn(fsprovider);

        when(fs.getSeparator()).thenReturn("\\");

        final Path path = create(fs,
                                 "/d:/path/to",
                                 false);
        final Path other = create(fs,
                                  "c:\\path\\to",
                                  false);

        assertThatThrownBy(() -> path.relativize(other))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Could not relativize path 'otherx', 'getRoot()' for 'this' and 'otherx' should be equal.");
    }

    private void assertWindowsUri(String actualUri,
                                  String expectedUriScheme,
                                  String expectedUriPath) {
        String expectedPathPrefix = determineWindowsPathPrefix();
        assertThat(actualUri).isEqualTo(expectedUriScheme + expectedPathPrefix + expectedUriPath);
    }

    private void assertWindowsPath(String actualPath,
                                   String expectedPath) {
        String expectedPathPrefix = determineWindowsPathPrefix();
        assertThat(actualPath).isEqualTo(expectedPathPrefix + expectedPath);
    }

    private String determineWindowsPathPrefix() {
        // in case the test runs on unix-like systems, UF adds "C:" prefix to the path
        // on Windows, that of course does not happen as the drive letter is directly in the path
        if (SystemUtils.IS_OS_WINDOWS) {
            return "";
        } else {
            return "C:";
        }
    }
}