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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import javax.enterprise.inject.Instance;

import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProjectSearchServiceTest {

    @Mock
    RepositoryService repositoryService;

    @Mock
    Instance<ProjectService<? extends Project>> projectServices;

    @Mock
    ProjectService projectService;

    @Mock
    Project itemA;

    @Mock
    Project itemB;

    private ProjectSearchServiceImpl searchService;

    @Before
    public void setUp() throws Exception {
        when(itemA.getIdentifier()).thenReturn("itemA");
        when(itemB.getIdentifier()).thenReturn("itemB");
        when(itemA.getProjectName()).thenReturn("Item A");
        when(itemB.getProjectName()).thenReturn("Item B");
        when(repositoryService.getAllRepositories()).thenReturn(Arrays.asList(mock(Repository.class)));
        when(projectServices.get()).thenReturn(projectService);
        when(projectService.getAllProjects(any(),
                                           anyString())).thenReturn(new HashSet() {{
            add(itemA);
            add(itemB);
        }});
        searchService = new ProjectSearchServiceImpl(repositoryService,
                                                     projectServices);
    }

    @Test
    public void testSearchById() throws Exception {
        Collection<Project> result = searchService.searchById(Arrays.asList("itemA"));
        assertEquals(result.size(),
                     1);
        assertEquals(result.iterator().next().getProjectName(),
                     "Item A");
    }

    @Test
    public void testSearchByAlias() throws Exception {
        Collection<Project> result = searchService.searchByName("Item",
                                                                10,
                                                                true);
        assertEquals(result.size(),
                     2);
    }

    @Test
    public void testSearchCaseSensitiveEmpty() throws Exception {
        Collection<Project> result = searchService.searchByName("item",
                                                                10,
                                                                true);
        assertEquals(result.size(),
                     0);
    }

    @Test
    public void testSearchCaseUnsensitive() throws Exception {
        Collection<Project> result = searchService.searchByName("item",
                                                                10,
                                                                false);
        assertEquals(result.size(),
                     2);
    }

    @Test
    public void testSearchMaxItems() throws Exception {
        Collection<Project> result = searchService.searchByName("item",
                                                                1,
                                                                false);
        assertEquals(result.size(),
                     1);
    }
}