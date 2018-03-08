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

import org.junit.Test;
import org.uberfire.java.nio.fs.file.BaseSimpleFileSystem;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class FileSystemsTest {

    @Test
    public void testGetDefault() {
        assertThat(FileSystems.getDefault())
                .isInstanceOf(BaseSimpleFileSystem.class);
    }

    @Test
    public void testGetFileSystemByURI() {
        assertThat(FileSystems.getFileSystem(URI.create("default:///"))).isInstanceOf(BaseSimpleFileSystem.class);
        assertThat(FileSystems.getFileSystem(URI.create("file:///"))).isInstanceOf(BaseSimpleFileSystem.class);
    }

    @Test
    public void getFileSystemNull() {
        assertThatThrownBy(() -> FileSystems.getFileSystem(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'uri' should be not null!");
    }

    @Test
    public void newFileSystemNull1() {
        assertThatThrownBy(() -> FileSystems.newFileSystem(null, Collections.emptyMap()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'uri' should be not null!");
    }

    @Test
    public void newFileSystemNull2() {
        assertThatThrownBy(() -> FileSystems.newFileSystem(URI.create("jgit:///test"), null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'env' should be not null!");
    }

    @Test
    public void newFileSystemNull3() {
        assertThatThrownBy(() -> FileSystems.newFileSystem((URI) null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'uri' should be not null!");
    }

    @Test
    public void newFileSystemNull4() {
        assertThatThrownBy(() -> FileSystems.newFileSystem((Path) null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void newFileSystemNull5() {
        assertThatThrownBy(() -> FileSystems.newFileSystem(URI.create("jgit:///test"), null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'env' should be not null!");
    }

    @Test
    public void newFileSystemNull6() {
        assertThatThrownBy(() -> FileSystems.newFileSystem(null, null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'uri' should be not null!");
    }
}
