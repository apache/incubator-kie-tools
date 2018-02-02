/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.common.services.project.backend.server;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.WorkspaceProjectSearchService;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class WorkspaceWorkspaceProjectSearchServiceTest {

    @Mock
    WorkspaceProjectService workspaceProjectService;

    @Mock
    WorkspaceProject itemA;

    @Mock
    WorkspaceProject itemB;

    private WorkspaceProjectSearchService searchService;

    @Before
    public void setUp() throws Exception {
        when(itemA.getName()).thenReturn("Item A");
        when(itemB.getName()).thenReturn("Item B");
        final Repository repository = mock(Repository.class);
        final Path repositoryRoot = mock(Path.class);

        when(repository.getDefaultBranch()).thenReturn(Optional.of(new Branch("master",
                                                                              repositoryRoot)));
        when(workspaceProjectService.getAllWorkspaceProjects()).thenReturn(new HashSet() {{
            add(itemA);
            add(itemB);
        }});
        searchService = new WorkspaceProjectSearchServiceImpl(workspaceProjectService);
    }

    @Test
    public void testSearchByAlias() throws Exception {
        Collection<WorkspaceProject> result = searchService.searchByName("Item",
                                                                         10,
                                                                         true);
        assertEquals(result.size(),
                     2);
    }

    @Test
    public void testSearchCaseSensitiveEmpty() throws Exception {
        Collection<WorkspaceProject> result = searchService.searchByName("item",
                                                                         10,
                                                                         true);
        assertEquals(result.size(),
                     0);
    }

    @Test
    public void testSearchCaseUnsensitive() throws Exception {
        Collection<WorkspaceProject> result = searchService.searchByName("item",
                                                                         10,
                                                                         false);
        assertEquals(result.size(),
                     2);
    }

    @Test
    public void testSearchMaxItems() throws Exception {
        Collection<WorkspaceProject> result = searchService.searchByName("item",
                                                                         1,
                                                                         false);
        assertEquals(result.size(),
                     1);
    }
}