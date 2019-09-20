/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.screens.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.event.Event;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.structure.repositories.Repository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.api.RepositoryFileListUpdatedEvent;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.metadata.event.BatchIndexEvent;
import org.uberfire.ext.metadata.event.IndexEvent;
import org.uberfire.ext.metadata.model.KObject;
import org.uberfire.java.nio.fs.jgit.JGitPathImpl;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class LibraryFileUpdateNotifierTest {

    private LibraryFileUpdateNotifier libraryFileUpdateNotifier;

    @Mock
    private WorkspaceProjectService workspaceProjectService;

    @Mock
    private Event<RepositoryFileListUpdatedEvent> repositoryFileListUpdatedEvent;

    @Before
    public void setup() {
        libraryFileUpdateNotifier = spy(new LibraryFileUpdateNotifier(workspaceProjectService,
                                                                      repositoryFileListUpdatedEvent));
    }

    @Test
    public void onBatchIndexEventDoNothingWhenEmptyEventList() {
        BatchIndexEvent event = mock(BatchIndexEvent.class);

        doReturn(Collections.emptyList()).when(event).getIndexEvents();

        libraryFileUpdateNotifier.onBatchIndexEvent(event);

        verify(repositoryFileListUpdatedEvent, never()).fire(any(RepositoryFileListUpdatedEvent.class));
    }

    @Test
    public void onBatchIndexEventNotifyOnceForSameProjectAndBranch() {
        this.prepareNewWorkspaceProjectMock(Collections.singletonMap("branch", "a/file/path"),
                                            "myRepositoryId");

        List<IndexEvent> indexEvents = Collections.nCopies(10,
                                                           buildIndexedEvent("a/file/path"));

        libraryFileUpdateNotifier.onBatchIndexEvent(buildBatchIndexEvent(indexEvents));

        verify(repositoryFileListUpdatedEvent, times(1))
                .fire(any(RepositoryFileListUpdatedEvent.class));
    }

    @Test
    public void onBatchIndexEventNotifyForSameProjectDistinctBranches() {
        Map<String, String> branchPathMap = new HashMap<String, String>() {{
            put("branchA", "a/file/pathA");
            put("branchB", "a/file/pathB");
            put("branchC", "a/file/pathC");
        }};

        this.prepareNewWorkspaceProjectMock(branchPathMap,
                                            "myRepositoryId");

        List<IndexEvent> indexEvents = new ArrayList<IndexEvent>() {{
            add(buildIndexedEvent("a/file/pathA"));
            add(buildIndexedEvent("a/file/pathB"));
            add(buildIndexedEvent("a/file/pathC"));
        }};

        libraryFileUpdateNotifier.onBatchIndexEvent(buildBatchIndexEvent(indexEvents));

        verify(repositoryFileListUpdatedEvent, times(3))
                .fire(any(RepositoryFileListUpdatedEvent.class));
    }

    @Test
    public void onBatchIndexEventNotifyDistinctProjects() {
        this.prepareNewWorkspaceProjectMock(Collections.singletonMap("branchA", "a/file/pathA"),
                                            "myRepositoryIdA");

        this.prepareNewWorkspaceProjectMock(Collections.singletonMap("branchB", "a/file/pathB"),
                                            "myRepositoryIdB");

        List<IndexEvent> indexEvents = new ArrayList<IndexEvent>() {{
            add(buildIndexedEvent("a/file/pathA"));
            add(buildIndexedEvent("a/file/pathB"));
        }};

        libraryFileUpdateNotifier.onBatchIndexEvent(buildBatchIndexEvent(indexEvents));

        verify(repositoryFileListUpdatedEvent, times(2))
                .fire(any(RepositoryFileListUpdatedEvent.class));
    }

    private IndexEvent.NewlyIndexedEvent buildIndexedEvent(final String pathStr) {
        KObject kObject = mock(KObject.class);
        doReturn(pathStr).when(kObject).getKey();

        IndexEvent.NewlyIndexedEvent indexEvent = mock(IndexEvent.NewlyIndexedEvent.class);
        doReturn(IndexEvent.Kind.NewlyIndexed).when(indexEvent).getKind();
        doReturn(kObject).when(indexEvent).getKObject();

        return indexEvent;
    }

    private BatchIndexEvent buildBatchIndexEvent(final List<IndexEvent> indexEvents) {
        BatchIndexEvent event = mock(BatchIndexEvent.class);
        doReturn(indexEvents).when(event).getIndexEvents();

        return event;
    }

    private org.uberfire.backend.vfs.Path buildPathMockForBranch(final String branchName,
                                                                 final String pathStr) {
        JGitPathImpl jGitPath = mock(JGitPathImpl.class);
        doReturn(jGitPath).when(libraryFileUpdateNotifier).getPath(pathStr);
        doReturn(branchName).when(jGitPath).getRefTree();

        org.uberfire.backend.vfs.Path vfsPath = mock(Path.class);
        doReturn(vfsPath).when(libraryFileUpdateNotifier).convertPath(jGitPath);

        return vfsPath;
    }

    private void prepareNewWorkspaceProjectMock(final Map<String, String> branchPathMap,
                                                final String repositoryId) {

        WorkspaceProject workspaceProject = mock(WorkspaceProject.class);

        for (Map.Entry<String, String> entry : branchPathMap.entrySet()) {
            org.uberfire.backend.vfs.Path path = buildPathMockForBranch(entry.getKey(),
                                                                        entry.getValue());

            doReturn(workspaceProject).when(workspaceProjectService).resolveProject(path);
        }

        Repository repository = mock(Repository.class);
        doReturn(repository).when(workspaceProject).getRepository();
        doReturn(repositoryId).when(repository).getIdentifier();
    }
}
