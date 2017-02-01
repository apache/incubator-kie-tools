/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.uberfire.java.nio.base.dotfiles;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.uberfire.java.nio.base.GeneralPathImpl.create;

@RunWith(MockitoJUnitRunner.class)
public class DotFileUtilsTest {

    @Mock
    FileSystem fs;

    @Before
    public void setup() {
        when(fs.getSeparator()).thenReturn("/");
    }

    @Test
    public void undotNonDotFile() {
        final Path path = create(fs,
                                 "/path/to/file.txt",
                                 false);

        final Path converted = DotFileUtils.undot(path);

        assertEquals(path,
                     converted);
    }

    @Test
    public void undotDotFile() {
        final Path path = create(fs,
                                 "/path/to/.file.txt",
                                 false);
        final Path undot = create(fs,
                                  "/path/to/file.txt",
                                  false);

        final Path converted = DotFileUtils.undot(path);

        assertEquals(undot,
                     converted);
    }
}
