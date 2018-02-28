/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.backend.server.util;

import org.junit.Test;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PathsTest {

    @Test
    public void getFilePathFromProjectRoot() throws Exception {

        final Path repositoryRoot = getPath("file:///spaceName/repositoryName",
                                            "/");
        final Path pathToFile = getPath("file:///spaceName/repositoryName/org/test/file.txt",
                                        "file.txt");

        assertEquals("org/test/file.txt",
                     Paths.removePrefix(pathToFile,
                                        repositoryRoot));
    }

    @Test
    public void getFilePath() throws Exception {
        final Path repositoryRoot = getPath("file:///spaceName/repositoryName/org/test",
                                            "test");
        final Path pathToFile = getPath("file:///spaceName/repositoryName/org/test/file.txt",
                                        "file.txt");

        assertEquals("file.txt",
                     Paths.removePrefix(pathToFile,
                                        repositoryRoot));
    }

    @Test
    public void pathsAreEqual() throws Exception {
        final Path repositoryRoot = getPath("file:///spaceName/repositoryName/org/test",
                                            "test");

        assertEquals("",
                     Paths.removePrefix(repositoryRoot,
                                        repositoryRoot));
    }

    @Test(expected = IllegalArgumentException.class)
    public void pathIsShorterThanPrefix() throws Exception {
        final Path repositoryRoot = getPath("file:///spaceName/repositoryName/org/test",
                                            "test");
        final Path pathToFile = getPath("file:///spaceName/repositoryName/org/test/file.txt",
                                        "file.txt");

        Paths.removePrefix(repositoryRoot,
                           pathToFile);
    }

    @Test(expected = IllegalArgumentException.class)
    public void prefixDoesNotMatch() throws Exception {
        final Path repositoryRoot = getPath("file:///spaceName/repositoryName/org/test",
                                            "test");
        final Path pathToFile = getPath("file:///otherSpace/repositoryName/org/test/file.txt",
                                        "file.txt");

        Paths.removePrefix(pathToFile,
                           repositoryRoot);
    }

    private Path getPath(final String uri,
                         final String fileName) {
        final Path path = mock(Path.class);

        when(path.toURI()).thenReturn(uri);
        when(path.getFileName()).thenReturn(fileName);
        return path;
    }
}