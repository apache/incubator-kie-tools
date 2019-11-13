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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.guvnor.common.services.backend.MockIOService;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.FileSystemNotFoundException;
import org.uberfire.java.nio.file.NoSuchFileException;
import org.uberfire.java.nio.file.OpenOption;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ArchiverTest {

    private Archiver archiver;
    private MockIOService ioService;

    @Before
    public void setUp() throws Exception {
        final SimpleFileSystemProvider simpleFileSystemProvider = new SimpleFileSystemProvider();
        simpleFileSystemProvider.forceAsDefault();

        ioService = spy(new MockIOService() {

            @Override
            public Path get(URI uri) throws IllegalArgumentException, FileSystemNotFoundException, SecurityException {
                return simpleFileSystemProvider.getPath(uri);
            }

            @Override
            public InputStream newInputStream(Path path,
                                              OpenOption... openOptions) throws IllegalArgumentException, NoSuchFileException, UnsupportedOperationException, IOException, SecurityException {
                String resourcePath = path.toString().substring(path.toString().indexOf("test-classes") + "test-classes".length());
                if (resourcePath.startsWith("\\")) {
                    resourcePath = resourcePath.replaceAll("\\\\", "/");
                }
                return getClass().getResourceAsStream(
                        resourcePath);
            }
        });

        archiver = new Archiver(ioService);
    }

    @Test
    public void testZipRepository() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        archiver.archive(outputStream,
                         this.getClass().getResource("testRepository").toURI().toString());

        assertZipContains(outputStream,
                          "testRepository/project1/file1.txt",
                          "testRepository/project2/file2.txt");
    }

    @Test
    public void testZipProject() throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        archiver.archive(outputStream,
                         this.getClass().getResource("testRepository/project1").toURI().toString());

        assertZipContains(outputStream,
                          "project1/file1.txt");
    }

    private void assertZipContains(ByteArrayOutputStream outputStream,
                                   String... fileNames) throws java.io.IOException {
        ZipInputStream zipInputStream = new ZipInputStream(new ByteArrayInputStream(outputStream.toByteArray()));

        ZipEntry nextEntry = zipInputStream.getNextEntry();

        HashSet<String> files = new HashSet<String>();

        while (nextEntry != null) {

            files.add(nextEntry.getName());

            nextEntry = zipInputStream.getNextEntry();
        }

        for (String fileName : fileNames) {
            assertTrue("Zip did not contain " + fileName,
                       files.contains(fileName));
        }
    }
}
