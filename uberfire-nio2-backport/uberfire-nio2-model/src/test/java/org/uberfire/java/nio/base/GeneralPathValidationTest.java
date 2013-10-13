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

package org.uberfire.java.nio.base;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;

import static org.mockito.Mockito.*;

public class GeneralPathValidationTest {

    final FileSystem fs = mock(FileSystem.class);

    @Before
    public void setup() {
        when(fs.getSeparator()).thenReturn("/");
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNull1() {
        GeneralPathImpl.create(null, "/path/to/file.txt", false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void createNull2() {
        GeneralPathImpl.create(fs, null, false);
    }

    @Test(expected = IllegalArgumentException.class)
    public void newFromFileNull1() {
        GeneralPathImpl.newFromFile(null, new File(""));
    }

    @Test(expected = IllegalArgumentException.class)
    public void newFromFileNull2() {
        GeneralPathImpl.newFromFile(fs, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getNameNegative() {
        final Path path = GeneralPathImpl.create(fs, "/path/to/file.txt", false);
        path.getName(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void subpathInvaligRange1() {
        final Path path = GeneralPathImpl.create(fs, "/path/to/file.txt", false);
        path.subpath(-1, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void subpathInvaligRange2() {
        final Path path = GeneralPathImpl.create(fs, "/path/to/file.txt", false);
        path.subpath(5, 7);
    }

    @Test(expected = IllegalArgumentException.class)
    public void subpathInvaligRange3() {
        final Path path = GeneralPathImpl.create(fs, "/path/to/file.txt", false);
        path.subpath(0, 7);
    }

    @Test(expected = IllegalArgumentException.class)
    public void subpathInvaligRange4() {
        final Path path = GeneralPathImpl.create(fs, "/path/to/file.txt", false);
        path.subpath(2, 1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void startsWith() {
        final Path path = GeneralPathImpl.create(fs, "/path/to/file.txt", false);
        path.startsWith((String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void startsWithPath() {
        final Path path = GeneralPathImpl.create(fs, "/path/to/file.txt", false);
        path.startsWith((Path) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void endsWith() {
        final Path path = GeneralPathImpl.create(fs, "/path/to/file.txt", false);
        path.endsWith((String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void endsWithPath() {
        final Path path = GeneralPathImpl.create(fs, "/path/to/file.txt", false);
        path.endsWith((Path) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void resolve() {
        final Path path = GeneralPathImpl.create(fs, "/path/to/file.txt", false);
        path.resolve((String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void resolvePath() {
        final Path path = GeneralPathImpl.create(fs, "/path/to/file.txt", false);
        path.resolve((Path) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void resolveSibling() {
        final Path path = GeneralPathImpl.create(fs, "/path/to/file.txt", false);
        path.resolveSibling((String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void resolveSiblingPath() {
        final Path path = GeneralPathImpl.create(fs, "/path/to/file.txt", false);
        path.resolveSibling((Path) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void relativize() {
        final Path path = GeneralPathImpl.create(fs, "/path/to/file.txt", false);
        path.relativize(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void compareTo() {
        final Path path = GeneralPathImpl.create(fs, "/path/to/file.txt", false);
        path.compareTo(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void checkEquals() {
        final Path path = GeneralPathImpl.create(fs, "/path/to/file.txt", false);
        path.equals(null);
    }

}
