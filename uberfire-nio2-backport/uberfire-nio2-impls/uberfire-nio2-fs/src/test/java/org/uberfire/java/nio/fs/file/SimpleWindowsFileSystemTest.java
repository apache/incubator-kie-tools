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

package org.uberfire.java.nio.fs.file;

import java.io.File;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.uberfire.java.nio.base.GeneralPathImpl;
import org.uberfire.java.nio.file.FileStore;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.spi.FileSystemProvider;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

public class SimpleWindowsFileSystemTest {

    final FileSystemProvider fsProvider = mock(FileSystemProvider.class);
    final File[] roots = new File[]{new File("c:\\"), new File("a:\\")};
    final File[] singleRoot = new File[]{new File("c:\\")};

    @Test
    public void simpleTests() {

        final FileSystem fileSystem = new SimpleWindowsFileSystem(roots,
                                                                  fsProvider,
                                                                  "c:\\");

        assertThat(fileSystem.isOpen()).isTrue();
        assertThat(fileSystem.isReadOnly()).isFalse();
        assertThat(fileSystem.getSeparator()).isEqualTo("\\");
        assertThat(fileSystem.provider()).isEqualTo(fsProvider);
        Assertions.assertThat(fileSystem.supportedFileAttributeViews()).hasSize(1).contains("basic");

        assertThat(fileSystem.getPath("c:\\path\\to\\file.txt")).isNotNull().isEqualTo(GeneralPathImpl.create(fileSystem,
                                                                                                              "c:\\path\\to\\file.txt",
                                                                                                              false));
        assertThat(fileSystem.getPath("/c:/path/to/file.txt")).isNotNull().isEqualTo(GeneralPathImpl.create(fileSystem,
                                                                                                            "/c:/path/to/file.txt",
                                                                                                            false));
        assertThat(fileSystem.getPath("c:\\path\\to\\")).isNotNull().isEqualTo(GeneralPathImpl.create(fileSystem,
                                                                                                      "c:\\path\\to",
                                                                                                      false));
        assertThat(fileSystem.getPath("/c:/path/to/")).isNotNull().isEqualTo(GeneralPathImpl.create(fileSystem,
                                                                                                    "/c:/path/to",
                                                                                                    false));
        assertThat(fileSystem.getPath("c:\\path\\to\\file.txt",
                                      null)).isNotNull().isEqualTo(GeneralPathImpl.create(fileSystem,
                                                                                          "c:\\path\\to\\file.txt",
                                                                                          false));
        assertThat(fileSystem.getPath("c:\\path",
                                      "to",
                                      "file.txt")).isNotNull().isEqualTo(GeneralPathImpl.create(fileSystem,
                                                                                                "c:\\path\\to\\file.txt",
                                                                                                false));
        assertThat(fileSystem.getPath("c:\\path\\",
                                      "to",
                                      "file.txt")).isNotNull().isEqualTo(GeneralPathImpl.create(fileSystem,
                                                                                                "c:\\path\\to\\file.txt",
                                                                                                false));
        assertThat(fileSystem.getPath("/c:/path",
                                      "to",
                                      "file.txt")).isNotNull().isEqualTo(GeneralPathImpl.create(fileSystem,
                                                                                                "/c:/path/to/file.txt",
                                                                                                false));
        assertThat(fileSystem.getPath("/c:/path/",
                                      "to",
                                      "file.txt")).isNotNull().isEqualTo(GeneralPathImpl.create(fileSystem,
                                                                                                "/c:/path/to/file.txt",
                                                                                                false));
        assertThat(fileSystem.getPath("/",
                                      "c:",
                                      "path")).isNotNull().isEqualTo(GeneralPathImpl.create(fileSystem,
                                                                                            "/c:/path",
                                                                                            false));

        assertThatThrownBy(() -> fileSystem.close())
                .isInstanceOf(UnsupportedOperationException.class);

        Assertions.assertThat(fileSystem.getFileStores()).hasSize(2);
        assertThat(fileSystem.getFileStores().iterator().next().name()).isEqualTo("c:\\");

        Assertions.assertThat(fileSystem.getRootDirectories()).hasSize(2);
        assertThat(fileSystem.getRootDirectories().iterator().next().toString()).isEqualTo("c:\\");
        assertThat(fileSystem.getRootDirectories().iterator().next().isAbsolute()).isTrue();
    }

    @Test(expected = IllegalArgumentException.class)
    public void invalidCOnstructorPath() {
        new SimpleWindowsFileSystem(fsProvider,
                                    "home");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getPathMatcherUnsupportedOp() {
        new SimpleWindowsFileSystem(fsProvider,
                                    "c:\\").getPathMatcher("*.*");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void getUserPrincipalLookupServiceUnsupportedOp() {
        new SimpleWindowsFileSystem(fsProvider,
                                    "c:\\").getUserPrincipalLookupService();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void newWatchServiceUnsupportedOp() {
        new SimpleWindowsFileSystem(fsProvider,
                                    "c:\\").newWatchService();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void removeElementFromRootIteratorUnsupportedOp() {
        new SimpleWindowsFileSystem(fsProvider,
                                    "c:\\").getRootDirectories().iterator().remove();
    }

    @Test(expected = NoSuchElementException.class)
    public void invalidElementFromRootIterator() {
        final Iterator<Path> iterator = new SimpleWindowsFileSystem(singleRoot,
                                                                    fsProvider,
                                                                    "c:\\").getRootDirectories().iterator();
        try {
            iterator.next();
        } catch (Exception e) {
            fail("first is valid");
        }
        iterator.next();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void removeElementFromFStoreIteratorUnsupportedOp() {
        new SimpleWindowsFileSystem(fsProvider,
                                    "c:\\").getFileStores().iterator().remove();
    }

    @Test(expected = NoSuchElementException.class)
    public void invalidElementFromFStoreIterator() {
        final Iterator<FileStore> iterator = new SimpleWindowsFileSystem(singleRoot,
                                                                         fsProvider,
                                                                         "c:\\").getFileStores().iterator();
        try {
            iterator.next();
        } catch (Exception e) {
            fail("first is valid");
        }
        iterator.next();
    }
}
