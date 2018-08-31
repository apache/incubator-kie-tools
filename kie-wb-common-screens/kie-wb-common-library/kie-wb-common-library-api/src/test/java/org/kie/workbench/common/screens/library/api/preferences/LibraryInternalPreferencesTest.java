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

package org.kie.workbench.common.screens.library.api.preferences;

import java.util.ArrayList;
import java.util.List;

import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.junit.Before;
import org.junit.Test;
import org.uberfire.spaces.Space;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class LibraryInternalPreferencesTest {

    private LibraryInternalPreferences libraryInternalPreferences;

    @Before
    public void setup() {
        libraryInternalPreferences = new LibraryInternalPreferences();
    }

    @Test
    public void lastBranchOpenedDoNotExist() {
        assertFalse(libraryInternalPreferences.getLastBranchOpened(makeProject("space", "repo", "master")).isPresent());
    }

    @Test
    public void lastBranchOpenedExists() {
        final WorkspaceProject project = makeProject("space", "repo", "master");
        final Branch masterBranch = makeBranch("master");

        libraryInternalPreferences.setLastBranchOpened(project,
                                                       masterBranch);

        assertEquals(masterBranch.getName(), libraryInternalPreferences.getLastBranchOpened(project).get().getName());
    }

    @Test
    public void lastBranchOpenedOfAnotherProject() {
        final WorkspaceProject project1 = makeProject("space", "repo1", "master");
        final WorkspaceProject project2 = makeProject("space", "repo2", "master");
        final Branch masterBranch = makeBranch("master");

        libraryInternalPreferences.setLastBranchOpened(project1,
                                                       masterBranch);

        assertFalse(libraryInternalPreferences.getLastBranchOpened(makeProject("space", "repo2", "master")).isPresent());
    }

    private WorkspaceProject makeProject(final String spaceName,
                                         final String repoAlias,
                                         final String... branchNames) {
        final Space space = mock(Space.class);
        final Repository repository = mock(Repository.class);
        final WorkspaceProject project = mock(WorkspaceProject.class);

        doReturn(space).when(project).getSpace();
        doReturn(repository).when(project).getRepository();

        doReturn(spaceName).when(space).getName();
        doReturn(repoAlias).when(repository).getAlias();

        final List<Branch> branches = new ArrayList<>();
        for (String branchName : branchNames) {
            branches.add(makeBranch(branchName));
        }

        doReturn(branches).when(repository).getBranches();
        doAnswer(invocationOnMock -> branches.stream().filter(b -> b.getName().equals(invocationOnMock.getArgumentAt(0, String.class))).findFirst()).when(repository).getBranch(anyString());

        return project;
    }

    private Branch makeBranch(String branchName) {
        final Branch branch = mock(Branch.class);
        doReturn(branchName).when(branch).getName();

        return branch;
    }
}
