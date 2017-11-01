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

package org.uberfire.java.nio.file;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

import org.junit.Test;
import org.uberfire.java.nio.fs.file.BaseSimpleFileSystem;

import static org.fest.assertions.api.Assertions.assertThat;

public class FileSystemsTest {

    @Test
    public void testGetDefault() {
        assertThat(FileSystems.getDefault()).isNotNull().isInstanceOf(BaseSimpleFileSystem.class);
    }

    @Test
    public void testGetFileSystemByURI() {
        assertThat(FileSystems.getFileSystem(URI.create("default:///"))).isNotNull().isInstanceOf(BaseSimpleFileSystem.class);
        assertThat(FileSystems.getFileSystem(URI.create("file:///"))).isNotNull().isInstanceOf(BaseSimpleFileSystem.class);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getFileSystemNull() {
        FileSystems.getFileSystem(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void newFileSystemNull1() {
        final Map<String, ?> emptyMap = Collections.emptyMap();
        FileSystems.newFileSystem(null,
                                  emptyMap);
    }

    @Test(expected = IllegalArgumentException.class)
    public void newFileSystemNull2() {
        FileSystems.newFileSystem(URI.create("jgit:///test"),
                                  null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void newFileSystemNull3() {
        FileSystems.newFileSystem((URI) null,
                                  null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void newFileSystemNull4() {
        FileSystems.newFileSystem((Path) null,
                                  null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void newFileSystemNull5() {
        FileSystems.newFileSystem(URI.create("jgit:///test"),
                                  null,
                                  null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void newFileSystemNull6() {
        FileSystems.newFileSystem(URI.create("jgit:///test"),
                                  null,
                                  null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void newFileSystemNull7() {
        FileSystems.newFileSystem(null,
                                  null,
                                  null);
    }
}
