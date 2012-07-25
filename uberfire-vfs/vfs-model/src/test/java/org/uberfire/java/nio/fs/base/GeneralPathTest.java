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

import java.net.URI;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.FileStore;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.InvalidPathException;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.PathMatcher;
import org.uberfire.java.nio.file.PatternSyntaxException;
import org.uberfire.java.nio.file.WatchService;
import org.uberfire.java.nio.file.attribute.UserPrincipalLookupService;
import org.uberfire.java.nio.file.spi.FileSystemProvider;

import static org.fest.assertions.api.Assertions.*;

public class GeneralPathTest {

    FileSystem unixLikeFS;

    @Test
    public void testSimpleRootedUnixURI() {
        final Path path = GeneralPathImpl.create(unixLikeFS, uri("file:///path/to/file.txt").getPath(), false);

        assertThat(path).isNotNull();
        assertThat(path.isAbsolute()).isTrue();
        assertThat(path.toString()).isEqualTo("/path/to/file.txt");
        assertThat(path.getFileSystem()).isNotNull().isEqualTo(unixLikeFS);

        assertThat(path.getRoot()).isNotNull();
        assertThat(path.getRoot().toString()).isNotNull().isEqualTo("/");
    }

    @Test
    public void testSimpleRootedUnix() {
        final Path path = GeneralPathImpl.create(unixLikeFS, "/path/to/file.txt", false);

        assertThat(path).isNotNull();
        assertThat(path.isAbsolute()).isTrue();
        assertThat(path.toString()).isEqualTo("/path/to/file.txt");
        assertThat(path.getFileSystem()).isNotNull().isEqualTo(unixLikeFS);

        assertThat(path.getRoot()).isNotNull();
        assertThat(path.getRoot().toString()).isNotNull().isEqualTo("/");
    }

    @Test
    public void testSimpleRootUnixURI() {
        final Path path = GeneralPathImpl.create(unixLikeFS, uri("file:///").getPath(), false);

        assertThat(path).isNotNull();
        assertThat(path.isAbsolute()).isTrue();
        assertThat(path.toString()).isEqualTo("/");
        assertThat(path.getFileSystem()).isNotNull().isEqualTo(unixLikeFS);

        assertThat(path.getRoot()).isNotNull().isEqualTo(path);
    }

    @Test
    public void testSimpleRootUnix() {
        final Path path = GeneralPathImpl.create(unixLikeFS, "/", false);

        assertThat(path).isNotNull();
        assertThat(path.isAbsolute()).isTrue();
        assertThat(path.toString()).isEqualTo("/");
//        assertThat(path.getFileName()).isNull();

        assertThat(path.getRoot()).isNotNull().isEqualTo(path);

//        try {
//            path.getName(0);
//            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
//        } catch (Exception e) {
//            assertThat(e).isInstanceOf(IllegalArgumentException.class);
//        }
    }

    public static URI uri(final String uri) {
        return URI.create(uri);
    }

    @Before
    public void setup() {
        unixLikeFS = new FileSystem() {
            @Override public FileSystemProvider provider() {
                return null;
            }

            @Override public boolean isOpen() {
                return true;
            }

            @Override public boolean isReadOnly() {
                return false;
            }

            @Override public String getSeparator() {
                return "/";
            }

            @Override public Iterable<Path> getRootDirectories() {
                return null;
            }

            @Override public Iterable<FileStore> getFileStores() {
                return null;
            }

            @Override public Set<String> supportedFileAttributeViews() {
                return null;
            }

            @Override public Path getPath(String first, String... more) throws InvalidPathException {
                return null;
            }

            @Override public PathMatcher getPathMatcher(String syntaxAndPattern) throws IllegalArgumentException, PatternSyntaxException, UnsupportedOperationException {
                return null;
            }

            @Override public UserPrincipalLookupService getUserPrincipalLookupService() throws UnsupportedOperationException {
                return null;
            }

            @Override public WatchService newWatchService() throws UnsupportedOperationException, IOException {
                return null;
            }

            @Override public void close() throws IOException {
            }
        };
    }
}
