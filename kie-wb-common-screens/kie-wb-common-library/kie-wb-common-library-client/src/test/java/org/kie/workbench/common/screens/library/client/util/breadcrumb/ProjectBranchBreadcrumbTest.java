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

package org.kie.workbench.common.screens.library.client.util.breadcrumb;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.NewBranchEvent;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.spaces.Space;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectBranchBreadcrumbTest {

    @Mock
    private ProjectBranchBreadcrumb.View view;

    @Mock
    private LibraryPlaces libraryPlaces;

    @Mock
    private OrganizationalUnit organizationalUnit;

    @Mock
    private Space space;

    @Mock
    private Repository repository;

    @Mock
    private WorkspaceProject project;

    @Mock
    private SessionInfo sessionInfo;

    @Mock
    private User user;

    private Branch newBranch;

    private ProjectBranchBreadcrumb presenter;

    @Before
    public void setup() {
        when(user.getIdentifier()).thenReturn("user");
        when(sessionInfo.getIdentity()).thenReturn(user);

        presenter = spy(new ProjectBranchBreadcrumb(view,
                                                    libraryPlaces,
                                                    sessionInfo));

        newBranch = makeBranch("new-branch", "repository");

        final List<Branch> branches = Arrays.asList(makeBranch("branch3", "repo"),
                                                    makeBranch("branch2", "repo"),
                                                    makeBranch("branch4", "repo"),
                                                    makeBranch("branch1", "repo"));

        when(organizationalUnit.getSpace()).thenReturn(space);
        when(repository.getAlias()).thenReturn("repository");
        when(repository.getSpace()).thenReturn(space);
        when(repository.getBranches()).thenReturn(branches);
        when(repository.getBranch("new-branch")).thenReturn(Optional.of(newBranch));
        when(project.getRepository()).thenReturn(repository);

        when(libraryPlaces.getActiveSpace()).thenReturn(organizationalUnit);
        when(libraryPlaces.getActiveWorkspace()).thenReturn(project);
    }

    @Test
    public void setupTest() {
        presenter.setup(repository.getBranches());

        verify(view).init(presenter);

        final List<Branch> orderedBranches = presenter.getBranches();
        assertEquals("branch1", (orderedBranches).get(0).getName());
        assertEquals("branch2", (orderedBranches).get(1).getName());
        assertEquals("branch3", (orderedBranches).get(2).getName());
        assertEquals("branch4", (orderedBranches).get(3).getName());
    }

    @Test
    public void onBranchChangedTest() {
        final Branch branch = makeBranch("branch", "repo");

        presenter.onBranchChanged(branch);

        verify(libraryPlaces).goToProject(project, branch);
    }

    @Test
    public void newBranchCreatedByUserShouldBeOpened() {
        presenter.newBranchEvent(new NewBranchEvent(repository, newBranch.getName(), user));

        verify(presenter).setup(any());
        verify(libraryPlaces).goToProject(project, newBranch);
    }

    @Test
    public void newBranchCreatedByOtherUserShouldNotBeOpened() {
        final User otherUser = mock(User.class);
        when(otherUser.getIdentifier()).thenReturn("otherUser");
        presenter.newBranchEvent(new NewBranchEvent(repository, newBranch.getName(),
                                                    otherUser));

        verify(presenter, never()).setup(any());
        verify(libraryPlaces, never()).goToProject(project, newBranch);
    }

    private Branch makeBranch(final String branchName,
                              final String repoName) {
        final Path path = mock(Path.class);
        doReturn("default://" + branchName + "@" + repoName + "/").when(path).toURI();
        return new Branch(branchName, path);
    }
}
