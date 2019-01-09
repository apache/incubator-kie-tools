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

package org.kie.workbench.common.screens.library.client.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.client.security.OrganizationalUnitController;
import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.contributors.ContributorType;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LibraryPermissionsTest {

    @Mock
    private User user;

    @Mock
    private OrganizationalUnitController organizationalUnitController;

    @Mock
    private ProjectController projectController;

    private LibraryPermissions libraryPermissions;

    @Before
    public void setup() {
        libraryPermissions = spy(new LibraryPermissions(user,
                                                        organizationalUnitController,
                                                        projectController));
    }

    @Test
    public void userCanReadOrganizationalUnitsTest() {
        doReturn(true).when(organizationalUnitController).canReadOrgUnits();
        assertTrue(libraryPermissions.userCanReadOrganizationalUnits());
    }

    @Test
    public void userCanNotReadOrganizationalUnitsTest() {
        doReturn(false).when(organizationalUnitController).canReadOrgUnits();
        assertFalse(libraryPermissions.userCanReadOrganizationalUnits());
    }

    @Test
    public void userCanReadOrganizationalUnitTest() {
        doReturn(true).when(organizationalUnitController).canReadOrgUnit(any());
        assertTrue(libraryPermissions.userCanReadOrganizationalUnit(mock(OrganizationalUnit.class)));
    }

    @Test
    public void userCanNotReadOrganizationalUnitTest() {
        doReturn(false).when(organizationalUnitController).canReadOrgUnit(any());
        assertFalse(libraryPermissions.userCanReadOrganizationalUnit(mock(OrganizationalUnit.class)));
    }

    @Test
    public void contributorCanReadOrganizationalUnitTest() {
        doReturn(true).when(libraryPermissions).userIsAtLeast(eq(ContributorType.CONTRIBUTOR), any());
        assertTrue(libraryPermissions.userCanReadOrganizationalUnit(mock(OrganizationalUnit.class)));
    }

    @Test
    public void contributorCanNotReadOrganizationalUnitTest() {
        doReturn(false).when(libraryPermissions).userIsAtLeast(eq(ContributorType.CONTRIBUTOR), any());
        assertFalse(libraryPermissions.userCanReadOrganizationalUnit(mock(OrganizationalUnit.class)));
    }

    @Test
    public void userCanDeleteOrganizationalUnitTest() {
        doReturn(true).when(organizationalUnitController).canDeleteOrgUnit(any());
        assertTrue(libraryPermissions.userCanDeleteOrganizationalUnit(mock(OrganizationalUnit.class)));
    }

    @Test
    public void userCanNotDeleteOrganizationalUnitTest() {
        doReturn(false).when(organizationalUnitController).canDeleteOrgUnit(any());
        assertFalse(libraryPermissions.userCanDeleteOrganizationalUnit(mock(OrganizationalUnit.class)));
    }

    @Test
    public void contributorCanDeleteOrganizationalUnitTest() {
        doReturn(true).when(libraryPermissions).userIsAtLeast(eq(ContributorType.OWNER), any());
        assertTrue(libraryPermissions.userCanDeleteOrganizationalUnit(mock(OrganizationalUnit.class)));
    }

    @Test
    public void contributorCanNotDeleteOrganizationalUnitTest() {
        doReturn(false).when(libraryPermissions).userIsAtLeast(eq(ContributorType.OWNER), any());
        assertFalse(libraryPermissions.userCanDeleteOrganizationalUnit(mock(OrganizationalUnit.class)));
    }

    @Test
    public void userCanUpdateOrganizationalUnitTest() {
        doReturn(true).when(organizationalUnitController).canUpdateOrgUnit(any());
        assertTrue(libraryPermissions.userCanUpdateOrganizationalUnit(mock(OrganizationalUnit.class)));
    }

    @Test
    public void userCanNotUpdateOrganizationalUnitTest() {
        doReturn(false).when(organizationalUnitController).canUpdateOrgUnit(any());
        assertFalse(libraryPermissions.userCanUpdateOrganizationalUnit(mock(OrganizationalUnit.class)));
    }

    @Test
    public void contributorCanUpdateOrganizationalUnitTest() {
        doReturn(true).when(libraryPermissions).userIsAtLeast(eq(ContributorType.ADMIN), any());
        assertTrue(libraryPermissions.userCanUpdateOrganizationalUnit(mock(OrganizationalUnit.class)));
    }

    @Test
    public void contributorCanNotUpdateOrganizationalUnitTest() {
        doReturn(false).when(libraryPermissions).userIsAtLeast(eq(ContributorType.ADMIN), any());
        assertFalse(libraryPermissions.userCanUpdateOrganizationalUnit(mock(OrganizationalUnit.class)));
    }

    @Test
    public void userCanCreateOrganizationalUnitTest() {
        doReturn(true).when(organizationalUnitController).canCreateOrgUnits();
        assertTrue(libraryPermissions.userCanCreateOrganizationalUnit());
    }

    @Test
    public void userCanNotCreateOrganizationalUnitsTest() {
        doReturn(false).when(organizationalUnitController).canCreateOrgUnits();
        assertFalse(libraryPermissions.userCanCreateOrganizationalUnit());
    }

    @Test
    public void userCanDeleteProjectTest() {
        final WorkspaceProject project = getProject();
        doReturn(true).when(projectController).canDeleteProject(any());
        assertTrue(libraryPermissions.userCanDeleteProject(project));
    }

    @Test
    public void userCanNotDeleteProjectTest() {
        final WorkspaceProject project = getProject();
        doReturn(false).when(projectController).canDeleteProject(any());
        assertFalse(libraryPermissions.userCanDeleteProject(project));
    }

    @Test
    public void projectContributorCanDeleteProjectTest() {
        final WorkspaceProject project = getProject();
        final Collection<Contributor> projectContributors = project.getRepository().getContributors();
        doReturn(true).when(libraryPermissions).userIsAtLeast(eq(ContributorType.OWNER), same(projectContributors));
        assertTrue(libraryPermissions.userCanDeleteProject(project));
    }

    @Test
    public void projectContributorCanNotDeleteProjectTest() {
        final WorkspaceProject project = getProject();
        doReturn(false).when(libraryPermissions).userIsAtLeast(eq(ContributorType.OWNER), any());
        assertFalse(libraryPermissions.userCanDeleteProject(project));
    }

    @Test
    public void spaceContributorCanDeleteProjectTest() {
        final WorkspaceProject project = getProject();
        final Collection<Contributor> spaceContributors = project.getOrganizationalUnit().getContributors();
        doReturn(true).when(libraryPermissions).userIsAtLeast(eq(ContributorType.ADMIN), same(spaceContributors));
        assertTrue(libraryPermissions.userCanDeleteProject(project));
    }

    @Test
    public void spaceContributorCanNotDeleteProjectTest() {
        final WorkspaceProject project = getProject();
        doReturn(false).when(libraryPermissions).userIsAtLeast(eq(ContributorType.ADMIN), any());
        assertFalse(libraryPermissions.userCanDeleteProject(project));
    }

    @Test
    public void userCanDeleteBranchTest() {
        final WorkspaceProject project = getProject();
        doReturn(true).when(libraryPermissions).userCanDeleteProject(any());
        assertTrue(libraryPermissions.userCanDeleteBranch(project));
    }

    @Test
    public void userCanNotDeleteBranchTest() {
        final WorkspaceProject project = getProject();
        doReturn(false).when(libraryPermissions).userCanDeleteProject(any());
        assertFalse(libraryPermissions.userCanDeleteBranch(project));
    }

    @Test
    public void projectContributorCanDeleteBranchTest() {
        final WorkspaceProject project = getProject();
        final Collection<Contributor> projectContributors = project.getRepository().getContributors();
        doReturn(true).when(libraryPermissions).userIsAtLeast(eq(ContributorType.ADMIN), same(projectContributors));
        assertTrue(libraryPermissions.userCanDeleteBranch(project));
    }

    @Test
    public void projectContributorCanNotDeleteBranchTest() {
        final WorkspaceProject project = getProject();
        doReturn(false).when(libraryPermissions).userIsAtLeast(eq(ContributorType.ADMIN), any());
        assertFalse(libraryPermissions.userCanDeleteBranch(project));
    }

    @Test
    public void spaceContributorCanDeleteBranchTest() {
        final WorkspaceProject project = getProject();
        final Collection<Contributor> spaceContributors = project.getOrganizationalUnit().getContributors();
        doReturn(true).when(libraryPermissions).userIsAtLeast(eq(ContributorType.ADMIN), same(spaceContributors));
        assertTrue(libraryPermissions.userCanDeleteBranch(project));
    }

    @Test
    public void spaceContributorCanNotDeleteBranchTest() {
        final WorkspaceProject project = getProject();
        doReturn(false).when(libraryPermissions).userIsAtLeast(eq(ContributorType.ADMIN), any());
        assertFalse(libraryPermissions.userCanDeleteBranch(project));
    }

    @Test
    public void userCanBuildProjectTest() {
        final WorkspaceProject project = getProject();
        doReturn(true).when(projectController).canBuildProject(any());
        assertTrue(libraryPermissions.userCanBuildProject(project));
    }

    @Test
    public void userCanNotBuildProjectTest() {
        final WorkspaceProject project = getProject();
        doReturn(false).when(projectController).canBuildProject(any());
        assertFalse(libraryPermissions.userCanBuildProject(project));
    }

    @Test
    public void projectContributorCanBuildProjectTest() {
        final WorkspaceProject project = getProject();
        final Collection<Contributor> projectContributors = project.getRepository().getContributors();
        doReturn(true).when(libraryPermissions).userIsAtLeast(eq(ContributorType.ADMIN), same(projectContributors));
        assertTrue(libraryPermissions.userCanBuildProject(project));
    }

    @Test
    public void projectContributorCanNotBuildProjectTest() {
        final WorkspaceProject project = getProject();
        doReturn(false).when(libraryPermissions).userIsAtLeast(eq(ContributorType.ADMIN), any());
        assertFalse(libraryPermissions.userCanBuildProject(project));
    }

    @Test
    public void spaceContributorCanBuildProjectTest() {
        final WorkspaceProject project = getProject();
        final Collection<Contributor> spaceContributors = project.getOrganizationalUnit().getContributors();
        doReturn(true).when(libraryPermissions).userIsAtLeast(eq(ContributorType.CONTRIBUTOR), same(spaceContributors));
        assertTrue(libraryPermissions.userCanBuildProject(project));
    }

    @Test
    public void spaceContributorCanNotBuildProjectTest() {
        final WorkspaceProject project = getProject();
        doReturn(false).when(libraryPermissions).userIsAtLeast(eq(ContributorType.CONTRIBUTOR), any());
        assertFalse(libraryPermissions.userCanBuildProject(project));
    }

    @Test
    public void userCanDeployProjectTest() {
        final WorkspaceProject project = getProject();
        doReturn(true).when(projectController).canBuildProject(any());
        assertTrue(libraryPermissions.userCanDeployProject(project));
    }

    @Test
    public void userCanNotDeployProjectTest() {
        final WorkspaceProject project = getProject();
        doReturn(false).when(projectController).canBuildProject(any());
        assertFalse(libraryPermissions.userCanDeployProject(project));
    }

    @Test
    public void projectContributorCanDeployProjectTest() {
        final WorkspaceProject project = getProject();
        final Collection<Contributor> projectContributors = project.getRepository().getContributors();
        doReturn(true).when(libraryPermissions).userIsAtLeast(eq(ContributorType.ADMIN), same(projectContributors));
        assertTrue(libraryPermissions.userCanDeployProject(project));
    }

    @Test
    public void projectContributorCanNotDeployProjectTest() {
        final WorkspaceProject project = getProject();
        doReturn(false).when(libraryPermissions).userIsAtLeast(eq(ContributorType.ADMIN), any());
        assertFalse(libraryPermissions.userCanDeployProject(project));
    }

    @Test
    public void spaceContributorCanDeployProjectTest() {
        final WorkspaceProject project = getProject();
        final Collection<Contributor> spaceContributors = project.getOrganizationalUnit().getContributors();
        doReturn(true).when(libraryPermissions).userIsAtLeast(eq(ContributorType.ADMIN), same(spaceContributors));
        assertTrue(libraryPermissions.userCanDeployProject(project));
    }

    @Test
    public void spaceContributorCanNotDeployProjectTest() {
        final WorkspaceProject project = getProject();
        doReturn(false).when(libraryPermissions).userIsAtLeast(eq(ContributorType.ADMIN), any());
        assertFalse(libraryPermissions.userCanDeployProject(project));
    }

    @Test
    public void userCanUpdateProjectTest() {
        final WorkspaceProject project = getProject();
        doReturn(true).when(projectController).canUpdateProject(any());
        assertTrue(libraryPermissions.userCanUpdateProject(project));
    }

    @Test
    public void userCanNotUpdateProjectTest() {
        final WorkspaceProject project = getProject();
        doReturn(false).when(projectController).canUpdateProject(any());
        assertFalse(libraryPermissions.userCanUpdateProject(project));
    }

    @Test
    public void projectContributorCanUpdateProjectTest() {
        final WorkspaceProject project = getProject();
        final Collection<Contributor> projectContributors = project.getRepository().getContributors();
        doReturn(true).when(libraryPermissions).userIsAtLeast(eq(ContributorType.ADMIN), same(projectContributors));
        assertTrue(libraryPermissions.userCanUpdateProject(project));
    }

    @Test
    public void projectContributorCanNotUpdateProjectTest() {
        final WorkspaceProject project = getProject();
        doReturn(false).when(libraryPermissions).userIsAtLeast(eq(ContributorType.ADMIN), any());
        assertFalse(libraryPermissions.userCanUpdateProject(project));
    }

    @Test
    public void spaceContributorCanUpdateProjectTest() {
        final WorkspaceProject project = getProject();
        final Collection<Contributor> spaceContributors = project.getOrganizationalUnit().getContributors();
        doReturn(true).when(libraryPermissions).userIsAtLeast(eq(ContributorType.CONTRIBUTOR), same(spaceContributors));
        assertTrue(libraryPermissions.userCanUpdateProject(project));
    }

    @Test
    public void spaceContributorCanNotUpdateProjectTest() {
        final WorkspaceProject project = getProject();
        doReturn(false).when(libraryPermissions).userIsAtLeast(eq(ContributorType.CONTRIBUTOR), any());
        assertFalse(libraryPermissions.userCanUpdateProject(project));
    }

    @Test
    public void userCanCreateProjectTest() {
        final WorkspaceProject project = getProject();
        doReturn(true).when(projectController).canCreateProjects();
        assertTrue(libraryPermissions.userCanCreateProject(project.getOrganizationalUnit()));
    }

    @Test
    public void userCanNotCreateProjectTest() {
        final WorkspaceProject project = getProject();
        doReturn(false).when(projectController).canCreateProjects();
        assertFalse(libraryPermissions.userCanCreateProject(project.getOrganizationalUnit()));
    }

    @Test
    public void spaceContributorCanCreateProjectTest() {
        final WorkspaceProject project = getProject();
        final Collection<Contributor> spaceContributors = project.getOrganizationalUnit().getContributors();
        doReturn(true).when(libraryPermissions).userIsAtLeast(eq(ContributorType.CONTRIBUTOR), same(spaceContributors));
        assertTrue(libraryPermissions.userCanCreateProject(project.getOrganizationalUnit()));
    }

    @Test
    public void spaceContributorCanNotCreateProjectTest() {
        final WorkspaceProject project = getProject();
        doReturn(false).when(libraryPermissions).userIsAtLeast(eq(ContributorType.CONTRIBUTOR), any());
        assertFalse(libraryPermissions.userCanCreateProject(project.getOrganizationalUnit()));
    }

    private WorkspaceProject getProject() {
        final Repository repository = mock(Repository.class);
        final List<Contributor> projectContributors = new ArrayList<>();
        doReturn(projectContributors).when(repository).getContributors();

        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        final List<Contributor> organizationalUnitContributors = new ArrayList<>();
        doReturn(organizationalUnitContributors).when(organizationalUnit).getContributors();

        final Branch branch = mock(Branch.class);
        doReturn("branch").when(branch).getName();

        final WorkspaceProject project = mock(WorkspaceProject.class);
        doReturn(repository).when(project).getRepository();
        doReturn(organizationalUnit).when(project).getOrganizationalUnit();
        doReturn(branch).when(project).getBranch();
        doReturn(mock(Module.class)).when(project).getMainModule();

        return project;
    }
}
