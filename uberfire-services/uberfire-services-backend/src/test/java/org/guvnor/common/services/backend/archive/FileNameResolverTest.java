/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.common.services.backend.archive;

import org.junit.Test;

import static org.junit.Assert.*;

public class FileNameResolverTest {

    @Test
    public void testNormal() throws Exception {
        assertEquals("hello/file.txt",
                     Archiver.FileNameResolver.resolve("hello/file.txt",
                                                       "hello"));
    }

    @Test
    public void testFewFolders() throws Exception {
        assertEquals("hello/file.txt",
                     Archiver.FileNameResolver.resolve("project/hello/file.txt",
                                                       "project/hello"));
    }

    @Test
    public void testRoot() throws Exception {
        assertEquals("file.txt",
                     Archiver.FileNameResolver.resolve("/file.txt",
                                                       "/"));
    }

    @Test
    public void testRootFolder() throws Exception {
        assertEquals("project/file.txt",
                     Archiver.FileNameResolver.resolve("/repositoryName/file.txt",
                                                       "/repositoryName/"));
    }
}
