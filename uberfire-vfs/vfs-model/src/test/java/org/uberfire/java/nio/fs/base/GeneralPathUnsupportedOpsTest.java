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

import org.junit.Before;
import org.junit.Test;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.WatchEvent;
import org.uberfire.java.nio.file.WatchService;

import static org.mockito.Mockito.*;

public class GeneralPathUnsupportedOpsTest {

    final FileSystem fs = mock(FileSystem.class);
    final WatchService ws = mock(WatchService.class);
    final WatchEvent.Kind kd = mock(WatchEvent.Kind.class);
    final WatchEvent.Modifier mf = mock(WatchEvent.Modifier.class);

    Path param;

    @Before
    public void setup() {
        when(fs.getSeparator()).thenReturn("/");
        param = GeneralPathImpl.create(fs, "path", false);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void startsWith() {
        final Path path = GeneralPathImpl.create(fs, "/path/to/file.txt", false);
        path.startsWith("");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void startsWithPath() {
        final Path path = GeneralPathImpl.create(fs, "/path/to/file.txt", false);
        path.startsWith(param);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void endsWith() {
        final Path path = GeneralPathImpl.create(fs, "/path/to/file.txt", false);
        path.endsWith("");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void endsWithPath() {
        final Path path = GeneralPathImpl.create(fs, "/path/to/file.txt", false);
        path.endsWith(param);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void compareTo() {
        final Path path = GeneralPathImpl.create(fs, "/path/to/file.txt", false);
        path.compareTo(param);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void register() {
        final Path path = GeneralPathImpl.create(fs, "/path/to/file.txt", false);
        path.register(ws);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void register2() {
        final Path path = GeneralPathImpl.create(fs, "/path/to/file.txt", false);
        path.register(ws, new WatchEvent.Kind<?>[]{kd}, mf);
    }

}
