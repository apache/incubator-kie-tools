/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.backend.server;

import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.uberfire.backend.vfs.DirectoryStream;
import org.uberfire.backend.vfs.Path;
import org.uberfire.backend.vfs.PathFactory;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.IOException;
import org.uberfire.java.nio.file.FileSystem;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VFSServicesServerImplTest {

    private static final Path ROOT_PATH = PathFactory.newPath("root", "file://root");
    private static final String ROOT_URI = ROOT_PATH.toURI();
    private static Set<String> FS_ATTRIBUTES = new HashSet<String>() {{
        add("version");
    }};

    @Mock
    private IOService ioService;

    @Mock
    private FileSystem fileSystem;

    @Mock
    private org.uberfire.java.nio.file.Path rootPath;

    private VFSServicesServerImpl tested;
    private URI rootUri;

    @Before
    public void setup() throws Exception {
        rootUri = new URI(ROOT_URI);
        when(fileSystem.supportedFileAttributeViews()).thenReturn(FS_ATTRIBUTES);
        when(rootPath.toUri()).thenReturn(rootUri);
        when(rootPath.getFileSystem()).thenReturn(fileSystem);
        doAnswer((Answer<org.uberfire.java.nio.file.Path>) invocationOnMock -> {
            final URI arg = (URI) invocationOnMock.getArguments()[0];
            return arg.toURL().toString().equals(ROOT_URI) ? rootPath : null;
        }).when(ioService).get(any(URI.class));
        tested = new VFSServicesServerImpl(ioService);
    }

    @Test
    public void testGet() {
        final Path path = tested.get(ROOT_URI);
        assertEquals(ROOT_URI, path.toURI());
    }

    @Test
    public void testNewDirectoryStream() {
        doAnswer((Answer<org.uberfire.java.nio.file.DirectoryStream<org.uberfire.java.nio.file.Path>>) invocationOnMock -> rootDirectoryStream)
                .when(ioService)
                .newDirectoryStream(any(org.uberfire.java.nio.file.Path.class));
        final DirectoryStream<Path> paths = tested.newDirectoryStream(ROOT_PATH);
        final Path path = paths.iterator().next();
        assertEquals(ROOT_PATH, path);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNewDirectoryStreamFiltered() {
        final DirectoryStream.Filter<Path> filter = mock(DirectoryStream.Filter.class);
        doAnswer((Answer<org.uberfire.java.nio.file.DirectoryStream<org.uberfire.java.nio.file.Path>>) invocationOnMock -> {
            org.uberfire.java.nio.file.DirectoryStream.Filter<org.uberfire.java.nio.file.Path> argFilter =
                    (org.uberfire.java.nio.file.DirectoryStream.Filter<org.uberfire.java.nio.file.Path>) invocationOnMock.getArguments()[1];
            argFilter.accept(rootPath);
            return rootDirectoryStream;
        })
                .when(ioService)
                .newDirectoryStream(any(org.uberfire.java.nio.file.Path.class),
                                    any(org.uberfire.java.nio.file.DirectoryStream.Filter.class));
        final DirectoryStream<Path> paths = tested.newDirectoryStream(ROOT_PATH,
                                                                      filter);
        verify(filter, times(1)).accept(eq(ROOT_PATH));
        final Path path = paths.iterator().next();
        assertEquals(ROOT_PATH, path);
    }

    @Test
    public void testCreateDirectory() {
        tested.createDirectory(ROOT_PATH);
        verify(ioService, times(1)).createDirectory(any(org.uberfire.java.nio.file.Path.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateDirectoryWithAttributes() {
        final Map attributes = mock(Map.class);
        tested.createDirectory(ROOT_PATH,
                               attributes);
        verify(ioService, times(1))
                .createDirectory(any(org.uberfire.java.nio.file.Path.class),
                                 eq(attributes));
    }

    @Test
    public void testCreateDirectories() {
        tested.createDirectories(ROOT_PATH);
        verify(ioService, times(1)).createDirectories(any(org.uberfire.java.nio.file.Path.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateDirectoriesWithAttributes() {
        final Map attributes = mock(Map.class);
        tested.createDirectories(ROOT_PATH,
                                 attributes);
        verify(ioService, times(1))
                .createDirectories(any(org.uberfire.java.nio.file.Path.class),
                                   eq(attributes));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSetAttributes() {
        final Map attributes = mock(Map.class);
        tested.setAttributes(ROOT_PATH,
                             attributes);
        verify(ioService, times(1))
                .setAttributes(any(org.uberfire.java.nio.file.Path.class),
                               eq(attributes));
    }

    @Test
    public void testDelete() {
        tested.delete(ROOT_PATH);
        verify(ioService, times(1)).delete(any(org.uberfire.java.nio.file.Path.class));
    }

    @Test
    public void testDeleteIfExists() {
        tested.deleteIfExists(ROOT_PATH);
        verify(ioService, times(1)).deleteIfExists(any(org.uberfire.java.nio.file.Path.class));
    }

    @Test
    public void testCopy() {
        tested.copy(ROOT_PATH,
                    ROOT_PATH);
        verify(ioService, times(1))
                .copy(any(org.uberfire.java.nio.file.Path.class),
                      any(org.uberfire.java.nio.file.Path.class));
    }

    @Test
    public void testMove() {
        tested.move(ROOT_PATH,
                    ROOT_PATH);
        verify(ioService, times(1))
                .move(any(org.uberfire.java.nio.file.Path.class),
                      any(org.uberfire.java.nio.file.Path.class));
    }

    @Test
    public void testReadAllString() {
        tested.readAllString(ROOT_PATH);
        verify(ioService, times(1)).readAllString(any(org.uberfire.java.nio.file.Path.class));
    }

    @Test
    public void testWrite() {
        final String content = "some-content-goes-here";
        tested.write(ROOT_PATH,
                     content);
        verify(ioService, times(1))
                .write(any(org.uberfire.java.nio.file.Path.class),
                       eq(content));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testWriteWithAttributes() {
        final Map attributes = mock(Map.class);
        final String content = "some-content-goes-here";
        tested.write(ROOT_PATH,
                     content,
                     attributes);
        verify(ioService, times(1))
                .write(any(org.uberfire.java.nio.file.Path.class),
                       eq(content),
                       eq(attributes));
    }

    private final org.uberfire.java.nio.file.DirectoryStream<org.uberfire.java.nio.file.Path> rootDirectoryStream = new org.uberfire.java.nio.file.DirectoryStream<org.uberfire.java.nio.file.Path>() {
        @Override
        public void close() throws IOException {
        }

        @Override
        public Iterator<org.uberfire.java.nio.file.Path> iterator() {
            return Collections.singletonList(rootPath).iterator();
        }
    };
}
