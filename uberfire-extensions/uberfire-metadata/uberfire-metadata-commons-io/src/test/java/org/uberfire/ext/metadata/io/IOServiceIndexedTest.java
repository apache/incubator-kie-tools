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
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.ext.metadata.engine.MetaIndexEngine;
import org.uberfire.ext.metadata.io.IndexerDispatcher.IndexerDispatcherFactory;
import org.uberfire.ext.metadata.io.common.util.TestFileSystemProvider;
import org.uberfire.ext.metadata.io.common.util.TestFileSystemProvider.MockFileSystem;
import org.uberfire.ext.metadata.model.KCluster;
import org.uberfire.java.nio.base.AttrsStorage;
import org.uberfire.java.nio.base.FSPath;
import org.uberfire.java.nio.base.WatchContext;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.WatchEvent;
import org.uberfire.java.nio.file.api.FileSystemProviders;
import org.uberfire.java.nio.file.spi.FileSystemProvider;
import org.uberfire.java.nio.fs.jgit.JGitPathImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.refEq;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.uberfire.java.nio.file.StandardWatchEventKind.ENTRY_MODIFY;

@RunWith(MockitoJUnitRunner.class)
public class IOServiceIndexedTest {

    IOServiceIndexedImpl ioService;

    @Mock
    MetaIndexEngine indexEngine;
    @Mock
    ExecutorService executorService;
    @Mock
    IndexersFactory indexersFactory;
    @Mock
    IndexerDispatcherFactory dispatcherFactory;

    FileSystemProvider mockProvider;

    @Before
    public void setup() throws URISyntaxException {
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
        final FileSystem fileSystem = getFileSystem();

        FSPath fsPath = mock(FSPath.class);
        when(fsPath.getFileSystem()).thenReturn(fileSystem);

        ioService.delete(fsPath);

        InOrder inOrder = Mockito.inOrder(ioService, indexEngine, fileSystem);
        inOrder.verify(indexEngine).delete(any(KCluster.class));
        inOrder.verify(ioService).deleteRepositoryFiles(eq(fsPath), any());
    }

    @Test
    public void deleteBranchTest() throws Exception {
        final FileSystem fileSystem = getFileSystem();

        final Path branchPath = fileSystem.getRootDirectories().iterator().next();

        ioService.delete(branchPath);

        InOrder inOrder = Mockito.inOrder(ioService, indexEngine, fileSystem);
        inOrder.verify(indexEngine).delete(any(KCluster.class));
        inOrder.verify(ioService).deleteRepositoryFiles(eq(branchPath), any());
    }

    @Test
    public void deleteFileTest() throws Exception {
        final FileSystem fileSystem = getFileSystem();

        final Path file = mock(Path.class);
        when(file.getFileSystem()).thenReturn(fileSystem);

        ioService.delete(file);

        verify(indexEngine, never()).delete(any(KCluster.class));
        verify(ioService).deleteRepositoryFiles(eq(file), any());
    }

    @Test
    public void dotFileShouldBeIgnored() {
        Path path = mock(Path.class);
        Path fileName = mock(Path.class);

        when(path.getFileName()).thenReturn(fileName);
        when(path.getFileName().toString()).thenReturn(".sample.drl");

        assertTrue(ioService.isIgnored(path));
    }

    @Test
    public void queueRenameShouldDispatchIndexEventTest() throws Exception {
        WatchContext context = mock(WatchContext.class);
        IndexerDispatcher dispatcher = mock(IndexerDispatcher.class);

        Path oldPath = mock(Path.class);
        Path newPath = mock(Path.class);
        Path fileName = mock(Path.class);

        when(newPath.getFileName()).thenReturn(fileName);
        when(newPath.getFileName().toString()).thenReturn("sample.drl");

        when(context.getOldPath()).thenReturn(oldPath);
        when(context.getPath()).thenReturn(newPath);

        ioService.queueRenameEvent(context, dispatcher);

        verify(dispatcher, times(1)).offer(
                refEq(new IndexableIOEvent.RenamedFileEvent(oldPath, newPath)));
    }

    @Test
    public void queueRenameShouldNotDispatchIndexEventTest() throws Exception {
        WatchContext context = mock(WatchContext.class);
        IndexerDispatcher dispatcher = mock(IndexerDispatcher.class);

        Path oldPath = mock(Path.class);
        Path newPath = mock(Path.class);
        Path fileName = mock(Path.class);

        when(newPath.getFileName()).thenReturn(fileName);
        when(newPath.getFileName().toString()).thenReturn(".sample.drl");

        when(context.getOldPath()).thenReturn(oldPath);
        when(context.getPath()).thenReturn(newPath);

        ioService.queueRenameEvent(context, dispatcher);

        verify(dispatcher, times(0)).offer(
                refEq(new IndexableIOEvent.RenamedFileEvent(oldPath, newPath)));
    }

    @Test
    public void queueDeleteShouldDispatchIndexEventTest() throws Exception {
        WatchContext context = mock(WatchContext.class);
        IndexerDispatcher dispatcher = mock(IndexerDispatcher.class);

        Path oldPath = mock(Path.class);
        Path fileName = mock(Path.class);

        when(oldPath.getFileName()).thenReturn(fileName);
        when(oldPath.getFileName().toString()).thenReturn("sample.drl");

        when(context.getOldPath()).thenReturn(oldPath);

        ioService.queueDeleteEvent(context, dispatcher);

        verify(dispatcher, times(1)).offer(
                refEq(new IndexableIOEvent.DeletedFileEvent(oldPath)));
    }

    @Test
    public void queueDeleteShouldNotDispatchIndexEventTest() throws Exception {
        WatchContext context = mock(WatchContext.class);
        IndexerDispatcher dispatcher = mock(IndexerDispatcher.class);

        Path oldPath = mock(Path.class);
        Path fileName = mock(Path.class);

        when(oldPath.getFileName()).thenReturn(fileName);
        when(oldPath.getFileName().toString()).thenReturn(".sample.drl");

        when(context.getOldPath()).thenReturn(oldPath);

        ioService.queueDeleteEvent(context, dispatcher);

        verify(dispatcher, times(0)).offer(
                refEq(new IndexableIOEvent.DeletedFileEvent(oldPath)));
    }

    @Test
    public void queueCreationAndModificationEventShouldUndotAndDispatchTest() throws Exception {
        WatchContext context = mock(WatchContext.class);
        IndexerDispatcher dispatcher = mock(IndexerDispatcher.class);

        Path path = mock(Path.class);
        Path fileName = mock(Path.class);

        when(path.getFileName()).thenReturn(fileName);
        when(path.getFileName().toString()).thenReturn(".sample.drl");

        when(context.getPath()).thenReturn(path);

        Path realPath = mock(Path.class);
        Path realFileName = mock(Path.class);

        when(realPath.getFileName()).thenReturn(realFileName);
        when(realPath.getFileName().toString()).thenReturn("sample.drl");

        Set<Path> eventRealPaths = new HashSet();

        when(path.resolveSibling("sample.drl")).thenReturn(realPath);

        ioService.queueCreationAndModificationEvent(eventRealPaths, context, dispatcher);

        verify(dispatcher, times(1)).offer(
                refEq(new IndexableIOEvent.NewFileEvent(realPath)));
    }

    @Test
    public void shouldNotAddBlankFilesToEventRealPathsTest() throws Exception {
        WatchContext context = mock(WatchContext.class);
        IndexerDispatcher dispatcher = mock(IndexerDispatcher.class);

        when(context.getPath()).thenReturn(null);

        WatchEvent watchEvent = mock(WatchEvent.class);
        when(watchEvent.context()).thenReturn(context);
        when(watchEvent.kind()).thenReturn(ENTRY_MODIFY);

        List<WatchEvent<?>> watchEvents = Arrays.asList(watchEvent);

        Set<Path> eventRealPaths = ioService.getRealCreatedPaths(watchEvents);
        assertEquals(0, eventRealPaths.size());
    }

    @Test
    public void shouldNotAddDotFilesToEventRealPathsTest() throws Exception {
        WatchContext context = mock(WatchContext.class);
        IndexerDispatcher dispatcher = mock(IndexerDispatcher.class);

        Path path = mock(Path.class);
        Path fileName = mock(Path.class);

        when(path.getFileName()).thenReturn(fileName);
        when(path.getFileName().toString()).thenReturn(".sample.drl");

        when(context.getPath()).thenReturn(path);

        WatchEvent watchEvent = mock(WatchEvent.class);
        when(watchEvent.context()).thenReturn(context);
        when(watchEvent.kind()).thenReturn(ENTRY_MODIFY);

        List<WatchEvent<?>> watchEvents = Arrays.asList(watchEvent);

        Set<Path> eventRealPaths = ioService.getRealCreatedPaths(watchEvents);
        assertEquals(0, eventRealPaths.size());
    }

    @Test
    public void shouldAddRealFilesToEventRealPathsTest() throws Exception {
        WatchContext context = mock(WatchContext.class);
        IndexerDispatcher dispatcher = mock(IndexerDispatcher.class);

        Path path = mock(Path.class);
        Path fileName = mock(Path.class);

        when(path.getFileName()).thenReturn(fileName);
        when(path.getFileName().toString()).thenReturn("sample.drl");

        when(context.getPath()).thenReturn(path);

        WatchEvent watchEvent = mock(WatchEvent.class);
        when(watchEvent.context()).thenReturn(context);
        when(watchEvent.kind()).thenReturn(ENTRY_MODIFY);

        List<WatchEvent<?>> watchEvents = Arrays.asList(watchEvent);

        Set<Path> eventRealPaths = ioService.getRealCreatedPaths(watchEvents);
        assertEquals(1, eventRealPaths.size());
    }

    private FileSystem getFileSystem() throws URISyntaxException {
        URI uri = new URI("test:///some/path");
        Map<String, ?> env = Collections.emptyMap();

        final FileSystem fileSystem = ioService.newFileSystem(uri, env);

        final Path fsRootDirectory1 = mock(JGitPathImpl.class);
        when(fsRootDirectory1.getFileSystem()).thenReturn(fileSystem);
        when(fsRootDirectory1.getRoot()).thenReturn(fsRootDirectory1);
        when(((JGitPathImpl) fsRootDirectory1).getAttrStorage()).thenReturn(mock(AttrsStorage.class));

        final Path fsRootDirectory2 = mock(JGitPathImpl.class);
        when(fsRootDirectory2.getFileSystem()).thenReturn(fileSystem);
        when(fsRootDirectory2.getRoot()).thenReturn(fsRootDirectory2);
        when(((JGitPathImpl) fsRootDirectory2).getAttrStorage()).thenReturn(mock(AttrsStorage.class));

        final Collection<Path> fsRootDirectories = Arrays.asList(fsRootDirectory1, fsRootDirectory2);
        doReturn(fsRootDirectories).when(fileSystem).getRootDirectories();

        return fileSystem;
    }

    @Test
    public void deleteIfExists() throws URISyntaxException {

        final FileSystem fileSystem = getFileSystem();
        String fsName = "fsName";
        FSPath fsPath = mock(FSPath.class);
        when(fsPath.getFileSystem()).thenReturn(fileSystem);
        when(fileSystem.getName()).thenReturn("fsName");
        doReturn(true).when(ioService).delIfExists(any());

        ioService.deleteIfExists(fsPath);

        InOrder inOrder = Mockito.inOrder(ioService, indexEngine, fileSystem);
        inOrder.verify(ioService).cleanupDeletedFS(eq(fsName), any());
        inOrder.verify(indexEngine).delete(any(KCluster.class));
    }
}
