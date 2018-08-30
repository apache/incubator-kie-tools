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

package org.uberfire.ext.metadata.io;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.metadata.engine.MetaIndexEngine;
import org.uberfire.ext.metadata.io.IOServiceIndexedImpl;
import org.uberfire.ext.metadata.io.IndexerDispatcher.IndexerDispatcherFactory;
import org.uberfire.ext.metadata.io.IndexersFactory;
import org.uberfire.ext.metadata.io.common.util.TestFileSystemProvider;
import org.uberfire.ext.metadata.io.common.util.TestFileSystemProvider.MockFileSystem;
import org.uberfire.ext.metadata.model.KCluster;
import org.uberfire.java.nio.base.FSPath;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.api.FileSystemProviders;
import org.uberfire.java.nio.file.spi.FileSystemProvider;
import org.uberfire.java.nio.fs.jgit.JGitFSPath;
import org.uberfire.java.nio.fs.jgit.JGitFileSystem;
import org.uberfire.java.nio.fs.jgit.JGitFileSystemImpl;
import org.uberfire.java.nio.fs.jgit.JGitPathImpl;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IOServiceIndexedTest {

    IOServiceIndexedImpl ioService;

    @Mock MetaIndexEngine indexEngine;
    @Mock ExecutorService executorService;
    @Mock IndexersFactory indexersFactory;
    @Mock IndexerDispatcherFactory dispatcherFactory;

    FileSystemProvider mockProvider;

    @Before
    public void setup() {
        TestFileSystemProvider testProvider = FileSystemProviders.installedProviders()
                                                                 .stream()
                                                                 .filter(provider -> provider instanceof TestFileSystemProvider)
                                                                 .map(provider -> (TestFileSystemProvider) provider)
                                                                 .findAny()
                                                                 .orElseThrow(() -> new RuntimeException("Failed to setup mock provider for test."));
        testProvider.resetMock();
        mockProvider = testProvider.getMock();

        when(mockProvider.newFileSystem(any(URI.class), any())).thenReturn(mock(MockFileSystem.class, RETURNS_DEEP_STUBS));
        when(mockProvider.newFileSystem(any(Path.class), any())).thenReturn(mock(MockFileSystem.class, RETURNS_DEEP_STUBS));
        when(mockProvider.getFileSystem(any(URI.class))).thenReturn(mock(MockFileSystem.class, RETURNS_DEEP_STUBS));

        ioService = spy(new IOServiceIndexedImpl(indexEngine, executorService, indexersFactory, dispatcherFactory));
    }

    @Test
    public void recreatingFileSystemMakesNewWatchService() throws Exception {
        URI uri = new URI("test:///some/path");
        Map<String, ?> env = Collections.emptyMap();

        FileSystem fs1 = ioService.newFileSystem(uri, env);
        verify(fs1).newWatchService();

        FSPath fsPath = mock(FSPath.class);
        when(fsPath.getFileSystem()).thenReturn(fs1);

        ioService.delete(fsPath);
        verify(fs1.newWatchService()).close();

        FileSystem fs2 = ioService.newFileSystem(uri, env);
        // Stubs are reused so this will have been invoked one above in IOService, and once in this test in a verify method.
        verify(fs2, times(3)).newWatchService();
    }

    @Test
    public void deleteFSTest() throws Exception {
        URI uri = new URI("test:///some/path");
        Map<String, ?> env = Collections.emptyMap();

        final Path fsRootDirectory1 = mock(JGitPathImpl.class);
        final Path fsRootDirectory2 = mock(JGitPathImpl.class);
        final Collection<Path> fsRootDirectories = Arrays.asList(fsRootDirectory1, fsRootDirectory2);

        FileSystem fs1 = ioService.newFileSystem(uri, env);
        doReturn(fsRootDirectories).when(fs1).getRootDirectories();
        when(fsRootDirectory1.getFileSystem()).thenReturn(fs1);
        when(fsRootDirectory2.getFileSystem()).thenReturn(fs1);

        FSPath fsPath = mock(FSPath.class);
        when(fsPath.getFileSystem()).thenReturn(fs1);

        ioService.delete(fsPath);

        InOrder inOrder = Mockito.inOrder(ioService, indexEngine, fs1);
        inOrder.verify(indexEngine).delete(any(KCluster.class));
        inOrder.verify(ioService).deleteRepositoryFiles(eq(fsPath), any());
    }
}
