/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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

import org.junit.Before;
import org.junit.Test;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;

import static org.fest.assertions.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GeneralPathEqualsTest {

    final FileSystem fs = mock(FileSystem.class);
    final FileSystem nfs = mock(FileSystem.class);

    @Before
    public void setup() {
        when(fs.getSeparator()).thenReturn("/");
        when(nfs.getSeparator()).thenReturn("/");
    }

    @Test
    public void testEquals() {
        final Path path = GeneralPathImpl.create(fs, "/path/to/file.txt", false);
        assertThat(path).isEqualTo(path);

        assertThat(path.equals(new Object())).isFalse();

        assertThat(path).isNotEqualTo(GeneralPathImpl.create(fs, "/path/to/file.txt", true));
        assertThat(path).isNotEqualTo(GeneralPathImpl.create(fs, "path/to/file.txt", false));
        assertThat(path).isNotEqualTo(GeneralPathImpl.create(fs, "/path/to/file.txts", false));
        assertThat(path).isNotEqualTo(GeneralPathImpl.create(nfs, "/path/to/file.txts", false));
        assertThat(path.getRoot()).isNotEqualTo(path);
    }

    @Test
    public void testEqualsWindows() {
        final Path path = GeneralPathImpl.create(fs, "path/to/file.txt", false);
        final Path wpath = GeneralPathImpl.create(fs, "path\\to\\file.txt", false);

        assertThat(path).isNotEqualTo(wpath);
    }

    @Test
    public void testHashCode() {
        final Path path = GeneralPathImpl.create(fs, "/path/to/file.txt", false);
        assertThat(path.hashCode()).isEqualTo(path.hashCode());

        assertThat(path.hashCode()).isNotEqualTo(new Object().hashCode());

        assertThat(path.hashCode()).isNotEqualTo(GeneralPathImpl.create(fs, "/path/to/file.txt", true).hashCode());
        assertThat(path.hashCode()).isNotEqualTo(GeneralPathImpl.create(fs, "path/to/file.txt", false).hashCode());
        assertThat(path.hashCode()).isNotEqualTo(GeneralPathImpl.create(fs, "/path/to/file.txts", false).hashCode());
        assertThat(path.hashCode()).isNotEqualTo(GeneralPathImpl.create(nfs, "/path/to/file.txts", false).hashCode());
        assertThat(path.getRoot().hashCode()).isNotEqualTo(path.hashCode());
    }

    @Test
    public void testHashCodeWindows() {
        final Path path = GeneralPathImpl.create(fs, "path/to/file.txt", false);
        final Path wpath = GeneralPathImpl.create(fs, "path\\to\\file.txt", false);

        assertThat(path.hashCode()).isNotEqualTo(wpath.hashCode());
    }

}
