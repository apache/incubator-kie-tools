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

import org.junit.Before;
import org.junit.Test;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

public class GeneralPathValidationTest {

    final FileSystem fs = mock(FileSystem.class);

    @Before
    public void setup() {
        when(fs.getSeparator()).thenReturn("/");
    }

    @Test
    public void createNull1() {
        assertThatThrownBy(() -> GeneralPathImpl.create(null,
                                                        "/path/to/file.txt",
                                                        false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'fs' should be not null!");
    }

    @Test
    public void createNull2() {
        assertThatThrownBy(() -> GeneralPathImpl.create(fs,
                                                        null,
                                                        false))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'path' should be not null!");
    }

    @Test
    public void newFromFileNull1() {
        assertThatThrownBy(() -> GeneralPathImpl.newFromFile(null,
                                                             new File("")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'fs' should be not null!");
    }

    @Test
    public void newFromFileNull2() {
        assertThatThrownBy(() -> GeneralPathImpl.newFromFile(fs,
                                                             null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'file' should be not null!");
    }

    @Test
    public void getNameNegative() {
        final Path path = GeneralPathImpl.create(fs,
                                                 "/path/to/file.txt",
                                                 false);

        assertThatThrownBy(() -> path.getName(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid index argument: -1");
    }

    @Test
    public void subpathInvaligRange1() {
        final Path path = GeneralPathImpl.create(fs,
                                                 "/path/to/file.txt",
                                                 false);

        assertThatThrownBy(() -> path.subpath(-1, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid beginIndex argument: -1");
    }

    @Test
    public void subpathInvaligRange2() {
        final Path path = GeneralPathImpl.create(fs,
                                                 "/path/to/file.txt",
                                                 false);
        assertThatThrownBy(() -> path.subpath(5, 7))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid beginIndex argument: 5");
    }

    @Test
    public void subpathInvaligRange3() {
        final Path path = GeneralPathImpl.create(fs,
                                                 "/path/to/file.txt",
                                                 false);
        assertThatThrownBy(() -> path.subpath(0, 7))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid endIndex argument: 7");
    }

    @Test
    public void subpathInvaligRange4() {
        final Path path = GeneralPathImpl.create(fs,
                                                 "/path/to/file.txt",
                                                 false);
        assertThatThrownBy(() -> path.subpath(2, 1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid arguments, beginIndex must be < endIndex, but they were: bI 2, eI 1");
    }

    @Test
    public void startsWith() {
        final Path path = GeneralPathImpl.create(fs,
                                                 "/path/to/file.txt",
                                                 false);

        assertThatThrownBy(() -> path.startsWith((String) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'other' should be not null!");
    }

    @Test
    public void startsWithPath() {
        final Path path = GeneralPathImpl.create(fs,
                                                 "/path/to/file.txt",
                                                 false);

        assertThatThrownBy(() -> path.startsWith((Path) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'other' should be not null!");
    }

    @Test
    public void endsWith() {
        final Path path = GeneralPathImpl.create(fs,
                                                 "/path/to/file.txt",
                                                 false);

        assertThatThrownBy(() -> path.endsWith((String) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'other' should be not null!");
    }

    @Test
    public void endsWithPath() {
        final Path path = GeneralPathImpl.create(fs,
                                                 "/path/to/file.txt",
                                                 false);

        assertThatThrownBy(() -> path.endsWith((Path) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'other' should be not null!");
    }

    @Test
    public void resolve() {
        final Path path = GeneralPathImpl.create(fs,
                                                 "/path/to/file.txt",
                                                 false);

        assertThatThrownBy(() -> path.resolve((String) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'other' should be not null!");
    }

    @Test
    public void resolvePath() {
        final Path path = GeneralPathImpl.create(fs,
                                                 "/path/to/file.txt",
                                                 false);

        assertThatThrownBy(() -> path.resolve((Path) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'other' should be not null!");
    }

    @Test
    public void resolveSibling() {
        final Path path = GeneralPathImpl.create(fs,
                                                 "/path/to/file.txt",
                                                 false);

        assertThatThrownBy(() -> path.resolveSibling((String) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'other' should be not null!");
    }

    @Test
    public void resolveSiblingPath() {
        final Path path = GeneralPathImpl.create(fs,
                                                 "/path/to/file.txt",
                                                 false);

        assertThatThrownBy(() -> path.resolveSibling((Path) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'other' should be not null!");
    }

    @Test
    public void relativize() {
        final Path path = GeneralPathImpl.create(fs,
                                                 "/path/to/file.txt",
                                                 false);

        assertThatThrownBy(() -> path.relativize(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'otherx' should be not null!");
    }

    @Test
    public void compareTo() {
        final Path path = GeneralPathImpl.create(fs,
                                                 "/path/to/file.txt",
                                                 false);

        assertThatThrownBy(() -> path.compareTo(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'other' should be not null!");
    }

    @Test
    public void checkEquals() {
        final Path path = GeneralPathImpl.create(fs,
                                                 "/path/to/file.txt",
                                                 false);

        assertThatThrownBy(() -> path.equals(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Parameter named 'o' should be not null!");
    }
}
