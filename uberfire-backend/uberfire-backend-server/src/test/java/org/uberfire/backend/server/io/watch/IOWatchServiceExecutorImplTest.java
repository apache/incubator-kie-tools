/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.backend.server.io.watch;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.server.util.Filter;
import org.uberfire.java.nio.base.WatchContext;
import org.uberfire.java.nio.file.FileSystem;
import org.uberfire.java.nio.file.Path;
import org.uberfire.java.nio.file.StandardWatchEventKind;
import org.uberfire.java.nio.file.WatchEvent;
import org.uberfire.java.nio.file.WatchKey;
import org.uberfire.mocks.EventSourceMock;
import org.uberfire.workbench.events.ResourceAddedEvent;
import org.uberfire.workbench.events.ResourceBatchChangesEvent;
import org.uberfire.workbench.events.ResourceChange;
import org.uberfire.workbench.events.ResourceChangeType;
import org.uberfire.workbench.events.ResourceDeletedEvent;
import org.uberfire.workbench.events.ResourceRenamedEvent;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class IOWatchServiceExecutorImplTest {

    private static final String COMMIT_MESSAGE = "COMMIT_MESSAGE";
    private static final String SESSION_ID = "SESSION_ID";
    private static final String USER = "USER";

    private static final String ORIGINAL_FILE1_URI = "file:///originalPath/OriginalFile1.java";
    private static final String NEW_FILE1_URI = "file:///newFilePath/NewFile1.java";

    private static final String ORIGINAL_FILE2_URI = "file:///originalPath/OriginalFile2.java";
    private static final String NEW_FILE2_URI = "file:///newFilePath/NewFile2.java";

    private static final String ORIGINAL_FILE3_URI = "file:///originalPath/OriginalFile3.java";
    private static final String NEW_FILE3_URI = "file:///newFilePath/NewFile3.java";

    private static final String ORIGINAL_FILE4_URI = "file:///originalPath/OriginalFile4.java";
    private static final String NEW_FILE4_URI = "file:///newFilePath/NewFile4.java";

    @Mock
    private EventSourceMock<ResourceBatchChangesEvent> resourceBatchChanges;

    @Mock
    private EventSourceMock<ResourceUpdatedEvent> resourceUpdatedEvent;

    @Mock
    private EventSourceMock<ResourceRenamedEvent> resourceRenamedEvent;

    @Mock
    private EventSourceMock<ResourceDeletedEvent> resourceDeletedEvent;

    @Mock
    private EventSourceMock<ResourceAddedEvent> resourceAddedEvent;

    @Captor
    private ArgumentCaptor<ResourceUpdatedEvent> resourceUpdatedEventCaptor;

    @Captor
    private ArgumentCaptor<ResourceDeletedEvent> resourceDeletedEventCaptor;

    @Captor
    private ArgumentCaptor<ResourceAddedEvent> resourceAddedEventCaptor;

    @Captor
    private ArgumentCaptor<ResourceRenamedEvent> resourceRenamedEventCaptor;

    @Captor
    private ArgumentCaptor<ResourceBatchChangesEvent> resourceBatchChangesEventCaptor;

    private IOWatchServiceExecutorImpl watchServiceExecutor;

    @Before
    public void setUp() {
        watchServiceExecutor = new IOWatchServiceExecutorImpl();
        watchServiceExecutor.setEvents(resourceBatchChanges,
                                       resourceUpdatedEvent,
                                       resourceRenamedEvent,
                                       resourceDeletedEvent,
                                       resourceAddedEvent);
    }

    @Test
    public void testSingleEventModify() throws Exception {
        testSingleEvent(StandardWatchEventKind.ENTRY_MODIFY,
                        ORIGINAL_FILE1_URI,
                        NEW_FILE1_URI,
                        SESSION_ID,
                        USER,
                        COMMIT_MESSAGE);
    }

    @Test
    public void testSingleEventDelete() throws Exception {
        testSingleEvent(StandardWatchEventKind.ENTRY_DELETE,
                        ORIGINAL_FILE1_URI,
                        NEW_FILE1_URI,
                        SESSION_ID,
                        USER,
                        COMMIT_MESSAGE);
    }

    @Test
    public void testSingleEventCreate() throws Exception {
        testSingleEvent(StandardWatchEventKind.ENTRY_CREATE,
                        ORIGINAL_FILE1_URI,
                        NEW_FILE1_URI,
                        SESSION_ID,
                        USER,
                        COMMIT_MESSAGE);
    }

    @Test
    public void testSingleEventRename() throws Exception {
        testSingleEvent(StandardWatchEventKind.ENTRY_RENAME,
                        ORIGINAL_FILE1_URI,
                        NEW_FILE1_URI,
                        SESSION_ID,
                        USER,
                        COMMIT_MESSAGE);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testMultipleEvents() throws Exception {
        List<WatchEvent<?>> events = new ArrayList<>();

        //file1 modified
        events.add(mockWatchEvent(StandardWatchEventKind.ENTRY_MODIFY,
                                  ORIGINAL_FILE1_URI,
                                  NEW_FILE1_URI,
                                  SESSION_ID,
                                  USER,
                                  COMMIT_MESSAGE));
        //file1 renamed
        events.add(mockWatchEvent(StandardWatchEventKind.ENTRY_RENAME,
                                  ORIGINAL_FILE1_URI,
                                  NEW_FILE1_URI,
                                  SESSION_ID,
                                  USER,
                                  COMMIT_MESSAGE));
        //file 2 added
        events.add(mockWatchEvent(StandardWatchEventKind.ENTRY_CREATE,
                                  ORIGINAL_FILE2_URI,
                                  NEW_FILE2_URI,
                                  SESSION_ID,
                                  USER,
                                  COMMIT_MESSAGE));
        //file 3  removed
        events.add(mockWatchEvent(StandardWatchEventKind.ENTRY_DELETE,
                                  ORIGINAL_FILE3_URI,
                                  NEW_FILE3_URI,
                                  SESSION_ID,
                                  USER,
                                  COMMIT_MESSAGE));
        //file 4  added
        events.add(mockWatchEvent(StandardWatchEventKind.ENTRY_CREATE,
                                  ORIGINAL_FILE4_URI,
                                  NEW_FILE4_URI,
                                  SESSION_ID,
                                  USER,
                                  COMMIT_MESSAGE));
        //file 4  modified
        events.add(mockWatchEvent(StandardWatchEventKind.ENTRY_MODIFY,
                                  ORIGINAL_FILE4_URI,
                                  NEW_FILE4_URI,
                                  SESSION_ID,
                                  USER,
                                  COMMIT_MESSAGE));
        //file 4  deleted
        events.add(mockWatchEvent(StandardWatchEventKind.ENTRY_DELETE,
                                  ORIGINAL_FILE4_URI,
                                  NEW_FILE4_URI,
                                  SESSION_ID,
                                  USER,
                                  COMMIT_MESSAGE));

        WatchKey watchKey = mock(WatchKey.class);
        Filter<WatchEvent<?>> filter = mock(Filter.class);
        when(watchKey.pollEvents()).thenReturn(events);
        when(filter.doFilter(any(WatchEvent.class))).thenReturn(false);

        watchServiceExecutor.execute(watchKey,
                                     filter);

        verify(resourceBatchChanges).fire(resourceBatchChangesEventCaptor.capture());

        //verify file1 was modified
        verifyResourceChange(resourceBatchChangesEventCaptor.getValue(),
                             ORIGINAL_FILE1_URI,
                             COMMIT_MESSAGE,
                             ResourceChangeType.UPDATE);
        //verify file1 was renamed
        verifyResourceChange(resourceBatchChangesEventCaptor.getValue(),
                             ORIGINAL_FILE1_URI,
                             COMMIT_MESSAGE,
                             ResourceChangeType.RENAME);
        //verify file2 was added
        verifyResourceChange(resourceBatchChangesEventCaptor.getValue(),
                             NEW_FILE2_URI,
                             COMMIT_MESSAGE,
                             ResourceChangeType.ADD);
        //verify file3 was removed
        verifyResourceChange(resourceBatchChangesEventCaptor.getValue(),
                             ORIGINAL_FILE3_URI,
                             COMMIT_MESSAGE,
                             ResourceChangeType.DELETE);
        //verify file4 was added
        verifyResourceChange(resourceBatchChangesEventCaptor.getValue(),
                             NEW_FILE4_URI,
                             COMMIT_MESSAGE,
                             ResourceChangeType.ADD);
        //verify file4 was modified
        verifyResourceChange(resourceBatchChangesEventCaptor.getValue(),
                             ORIGINAL_FILE4_URI,
                             COMMIT_MESSAGE,
                             ResourceChangeType.UPDATE);
        //verify file4 was deleted
        verifyResourceChange(resourceBatchChangesEventCaptor.getValue(),
                             ORIGINAL_FILE4_URI,
                             COMMIT_MESSAGE,
                             ResourceChangeType.DELETE);
    }

    private void verifyResourceChange(ResourceBatchChangesEvent resourceBatchChanges,
                                      String originalFile1Uri,
                                      String commitMessage,
                                      ResourceChangeType changeType) {
        Optional<Collection<ResourceChange>> expectedResourceChanges = resourceBatchChanges.getBatch().entrySet()
                .stream()
                .filter(entry -> originalFile1Uri.equals(entry.getKey().toURI()))
                .map(Map.Entry::getValue)
                .findFirst()
                .filter(resourceChanges -> resourceChanges.stream()
                        .filter(resourceChange -> resourceChange.getType().equals(changeType) && resourceChange.getMessage().equals(commitMessage))
                        .findFirst().isPresent());
        assertTrue("Change " + changeType.name() + " was not found for resource: " + originalFile1Uri,
                   expectedResourceChanges.isPresent());
    }

    @SuppressWarnings("unchecked")
    public void testSingleEvent(WatchEvent.Kind kind,
                                String originalPathURI,
                                String newPathURI,
                                String sessionId,
                                String userId,
                                String commitMessage) throws Exception {
        WatchKey watchKey = mock(WatchKey.class);
        Filter<WatchEvent<?>> filter = mock(Filter.class);

        List<WatchEvent<?>> events = new ArrayList<>();
        events.add(mockWatchEvent(kind,
                                  originalPathURI,
                                  newPathURI,
                                  sessionId,
                                  userId,
                                  commitMessage));
        when(watchKey.pollEvents()).thenReturn(events);
        when(filter.doFilter(any(WatchEvent.class))).thenReturn(false);

        watchServiceExecutor.execute(watchKey,
                                     filter);

        if (kind == StandardWatchEventKind.ENTRY_MODIFY) {
            verifyResourceUpdatedEvent(originalPathURI,
                                       sessionId,
                                       userId,
                                       commitMessage);
        } else if (kind == StandardWatchEventKind.ENTRY_DELETE) {
            verifyResourceDeletedEvent(originalPathURI,
                                       sessionId,
                                       userId,
                                       commitMessage);
        } else if (kind == StandardWatchEventKind.ENTRY_CREATE) {
            verifyResourceAddedEvent(newPathURI,
                                     sessionId,
                                     userId,
                                     commitMessage);
        } else {
            verifyResourceRenamedEvent(originalPathURI,
                                       newPathURI,
                                       sessionId,
                                       userId,
                                       commitMessage);
        }
    }

    @SuppressWarnings("unchecked")
    private WatchEvent<?> mockWatchEvent(WatchEvent.Kind kind,
                                         String originalPathURI,
                                         String newPathURI,
                                         String sessionId,
                                         String userId,
                                         String commitMessage) throws URISyntaxException {
        WatchEvent<?> event = mock(WatchEvent.class);
        when(event.kind()).thenReturn(kind);

        FileSystem fileSystem = mock(FileSystem.class);
        Set<String> supportedViews = new HashSet<>();
        when(fileSystem.supportedFileAttributeViews()).thenReturn(supportedViews);

        WatchContext context = mock(WatchContext.class);
        when(context.getSessionId()).thenReturn(sessionId);
        when(context.getUser()).thenReturn(userId);
        when(context.getMessage()).thenReturn(commitMessage);

        Path oldPath = mock(Path.class);
        URI oldPathUri = new URI(originalPathURI);
        when(oldPath.toUri()).thenReturn(oldPathUri);
        when(oldPath.getFileSystem()).thenReturn(fileSystem);

        Path path = mock(Path.class);
        URI pathUri = new URI(newPathURI);
        when(path.toUri()).thenReturn(pathUri);
        when(path.getFileSystem()).thenReturn(fileSystem);

        when(context.getOldPath()).thenReturn(oldPath);
        when(context.getPath()).thenReturn(path);
        when(event.context()).thenReturn(context);
        return event;
    }

    private void verifyResourceUpdatedEvent(String file,
                                            String sessionId,
                                            String userId,
                                            String commitMessage) {
        verify(resourceUpdatedEvent,
               times(1)).fire(resourceUpdatedEventCaptor.capture());

        assertEquals(file,
                     resourceUpdatedEventCaptor.getValue().getPath().toURI());
        assertEquals(sessionId,
                     resourceUpdatedEventCaptor.getValue().getSessionInfo().getId());
        assertEquals(userId,
                     resourceUpdatedEventCaptor.getValue().getSessionInfo().getIdentity().getIdentifier());
        assertEquals(commitMessage,
                     resourceUpdatedEventCaptor.getValue().getMessage());
    }

    private void verifyResourceDeletedEvent(String file,
                                            String sessionId,
                                            String userId,
                                            String commitMessage) {
        verify(resourceDeletedEvent,
               times(1)).fire(resourceDeletedEventCaptor.capture());

        assertEquals(file,
                     resourceDeletedEventCaptor.getValue().getPath().toURI());
        assertEquals(sessionId,
                     resourceDeletedEventCaptor.getValue().getSessionInfo().getId());
        assertEquals(userId,
                     resourceDeletedEventCaptor.getValue().getSessionInfo().getIdentity().getIdentifier());
        assertEquals(commitMessage,
                     resourceDeletedEventCaptor.getValue().getMessage());
    }

    private void verifyResourceAddedEvent(String file,
                                          String sessionId,
                                          String userId,
                                          String commitMessage) {
        verify(resourceAddedEvent,
               times(1)).fire(resourceAddedEventCaptor.capture());

        assertEquals(file,
                     resourceAddedEventCaptor.getValue().getPath().toURI());
        assertEquals(sessionId,
                     resourceAddedEventCaptor.getValue().getSessionInfo().getId());
        assertEquals(userId,
                     resourceAddedEventCaptor.getValue().getSessionInfo().getIdentity().getIdentifier());
        assertEquals(commitMessage,
                     resourceAddedEventCaptor.getValue().getMessage());
    }

    private void verifyResourceRenamedEvent(String file,
                                            String destinationFile,
                                            String sessionId,
                                            String userId,
                                            String commitMessage) {
        verify(resourceRenamedEvent,
               times(1)).fire(resourceRenamedEventCaptor.capture());

        assertEquals(file,
                     resourceRenamedEventCaptor.getValue().getPath().toURI());
        assertEquals(destinationFile,
                     resourceRenamedEventCaptor.getValue().getDestinationPath().toURI());
        assertEquals(sessionId,
                     resourceRenamedEventCaptor.getValue().getSessionInfo().getId());
        assertEquals(userId,
                     resourceRenamedEventCaptor.getValue().getSessionInfo().getIdentity().getIdentifier());
        assertEquals(commitMessage,
                     resourceRenamedEventCaptor.getValue().getMessage());
    }
}
