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

package org.kie.workbench.common.screens.library.client.screens.organizationalunit.contributors.tab;

import java.util.ArrayList;
import java.util.List;

import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.contributors.ContributorType;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.common.client.api.Caller;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.mocks.CallerMock;
import org.uberfire.spaces.Space;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ProjectContributorsListServiceImplTest {

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock
    private RepositoryService repositoryService;
    private Caller<RepositoryService> repositoryServiceCaller;

    @Mock
    private ProjectController projectController;

    @Mock
    private SpaceContributorsListServiceImpl spaceContributorsListService;

    private ProjectContributorsListServiceImpl service;

    @Before
    public void setup() {
        repositoryServiceCaller = new CallerMock<>(repositoryService);

        final OrganizationalUnit activeSpace = mock(OrganizationalUnit.class);
        doReturn(mock(Space.class)).when(activeSpace).getSpace();
        doReturn(activeSpace).when(libraryPlaces).getActiveSpace();

        final WorkspaceProject activeProject = mock(WorkspaceProject.class);
        final Repository activeRepository = mock(Repository.class);
        doReturn("alias").when(activeRepository).getAlias();
        doReturn(activeRepository).when(activeProject).getRepository();
        doReturn(activeProject).when(libraryPlaces).getActiveWorkspace();

        service = new ProjectContributorsListServiceImpl(libraryPlaces,
                                                         repositoryServiceCaller,
                                                         projectController,
                                                         spaceContributorsListService);
    }

    @Test
    public void getContributorsTest() {
        final List<Contributor> contributors = new ArrayList<>();
        contributors.add(new Contributor("admin", ContributorType.OWNER));
        contributors.add(new Contributor("user", ContributorType.CONTRIBUTOR));
        contributors.add(new Contributor("Director", ContributorType.ADMIN));

        final Repository repository = mock(Repository.class);
        doReturn(contributors).when(repository).getContributors();

        doReturn(repository).when(repositoryService).getRepositoryFromSpace(any(), any());

        service.getContributors(repositoryContributors -> {
            assertEquals(3, repositoryContributors.size());
        });
    }

    @Test
    public void saveContributorsTest() {
        final Repository repository = mock(Repository.class);
        doReturn(repository).when(repositoryService).getRepositoryFromSpace(any(), any());

        final List<Contributor> contributors = new ArrayList<>();
        contributors.add(new Contributor("admin", ContributorType.OWNER));
        contributors.add(new Contributor("user", ContributorType.CONTRIBUTOR));
        contributors.add(new Contributor("Director", ContributorType.ADMIN));

        service.saveContributors(contributors, () -> {}, null);

        verify(repositoryService).updateContributors(repository, contributors);
    }
}
