/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.common.services.project.client.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.security.ProjectPermissionsService;
import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.contributors.ContributorType;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.config.BranchPermissions;
import org.guvnor.structure.organizationalunit.config.RolePermissions;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.security.RepositoryAction;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.security.shared.api.identity.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.client.promise.Promises;
import org.uberfire.mocks.CallerMock;
import org.uberfire.promise.SyncPromises;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.spaces.Space;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectControllerTest {

    @Mock
    private AuthorizationManager authorizationManager;

    @Mock
    private User user;

    @Mock
    private ProjectPermissionsService projectPermissionsService;
    private Caller<ProjectPermissionsService> projectPermissionsServiceCaller;

    private Promises promises;

    private ProjectController projectController;

    @Before
    public void setup() {
        promises = new SyncPromises();
        projectPermissionsServiceCaller = new CallerMock<>(projectPermissionsService);
        projectController = spy(new ProjectController(authorizationManager,
                                                      user,
                                                      projectPermissionsServiceCaller,
                                                      promises));
    }

    @Test
    public void userCanCreateProjectTest() {
        final WorkspaceProject project = getProject();
        when(authorizationManager.authorize(Repository.RESOURCE_TYPE,
                                            RepositoryAction.CREATE,
                                            user)).thenReturn(true);
        assertTrue(projectController.canCreateProjects(project.getOrganizationalUnit()));
    }

    @Test
    public void userCanNotCreateProjectTest() {
        final WorkspaceProject project = getProject();
        when(authorizationManager.authorize(Repository.RESOURCE_TYPE,
                                            RepositoryAction.CREATE,
                                            user)).thenReturn(false);
        doReturn(false).when(projectController).userIsAtLeast(eq(ContributorType.CONTRIBUTOR),
                                                              any());
        assertFalse(projectController.canCreateProjects(project.getOrganizationalUnit()));
    }

    @Test
    public void spaceContributorCanCreateProjectTest() {
        final WorkspaceProject project = getProject();
        final Collection<Contributor> spaceContributors = project.getOrganizationalUnit().getContributors();
        doReturn(true).when(projectController).userIsAtLeast(eq(ContributorType.CONTRIBUTOR),
                                                             same(spaceContributors));
        assertTrue(projectController.canCreateProjects(project.getOrganizationalUnit()));
    }

    @Test
    public void spaceContributorCanNotCreateProjectTest() {
        final WorkspaceProject project = getProject();
        doReturn(false).when(projectController).userIsAtLeast(eq(ContributorType.CONTRIBUTOR),
                                                              any());
        assertFalse(projectController.canCreateProjects(project.getOrganizationalUnit()));
    }

    @Test
    public void userCanReadProjectTest() {
        final WorkspaceProject project = getProject();
        when(authorizationManager.authorize(project.getRepository(),
                                            RepositoryAction.READ,
                                            user)).thenReturn(true);
        assertTrue(projectController.canReadProject(project));
    }

    @Test
    public void userCanNotReadProjectTest() {
        final WorkspaceProject project = getProject();
        when(authorizationManager.authorize(project.getRepository(),
                                            RepositoryAction.READ,
                                            user)).thenReturn(false);
        doReturn(false).when(projectController).userIsAtLeast(any(),
                                                              any());
        assertFalse(projectController.canCreateProjects(project.getOrganizationalUnit()));
    }

    @Test
    public void projectContributorCanReadProjectTest() {
        final WorkspaceProject project = getProject();
        final Collection<Contributor> projectContributors = project.getRepository().getContributors();
        doReturn(true).when(projectController).userIsAtLeast(eq(ContributorType.CONTRIBUTOR),
                                                             same(projectContributors));
        assertTrue(projectController.canReadProject(project));
    }

    @Test
    public void projectContributorCanNotReadProjectTest() {
        final WorkspaceProject project = getProject();
        final Collection<Contributor> projectContributors = project.getRepository().getContributors();
        doReturn(false).when(projectController).userIsAtLeast(eq(ContributorType.CONTRIBUTOR),
                                                              same(projectContributors));
        assertFalse(projectController.canReadProject(project));
    }

    @Test
    public void spaceOwnerCanReadProjectTest() {
        final WorkspaceProject project = getProject();
        final Collection<Contributor> spaceContributors = project.getOrganizationalUnit().getContributors();
        doReturn(true).when(projectController).userIsAtLeast(eq(ContributorType.OWNER),
                                                             same(spaceContributors));
        assertTrue(projectController.canReadProject(project));
    }

    @Test
    public void spaceOwnerCanNotReadProjectTest() {
        final WorkspaceProject project = getProject();
        final Collection<Contributor> projectContributors = project.getOrganizationalUnit().getContributors();
        doReturn(false).when(projectController).userIsAtLeast(eq(ContributorType.OWNER),
                                                              same(projectContributors));
        assertFalse(projectController.canReadProject(project));
    }

    @Test
    public void userCanUpdateProjectTest() {
        final WorkspaceProject project = getProject();
        when(authorizationManager.authorize(project.getRepository(),
                                            RepositoryAction.UPDATE,
                                            user)).thenReturn(true);
        projectController.canUpdateProject(project).then(userCanUpdateProject -> {
            assertTrue(userCanUpdateProject);
            return promises.resolve();
        }).catch_(error -> {
            fail();
            return promises.resolve();
        });
    }

    @Test
    public void userCanNotUpdateProjectTest() {
        final WorkspaceProject project = getProject();
        when(authorizationManager.authorize(project.getRepository(),
                                            RepositoryAction.UPDATE,
                                            user)).thenReturn(false);
        doReturn(promises.resolve(Optional.of(new RolePermissions("CONTRIBUTOR", true, false, true, true))))
                .when(projectController).getBranchPermissionsForUser(project, "branch");
        projectController.canUpdateProject(project).then(userCanUpdateProject -> {
            assertFalse(userCanUpdateProject);
            return promises.resolve();
        }).catch_(error -> {
            fail();
            return promises.resolve();
        });
    }

    @Test
    public void projectContributorCanUpdateProjectTest() {
        final WorkspaceProject project = getProject();
        doReturn(promises.resolve(Optional.of(new RolePermissions("CONTRIBUTOR", true, true, true, true))))
                .when(projectController).getBranchPermissionsForUser(project, "branch");
        projectController.canUpdateProject(project).then(userCanUpdateProject -> {
            assertTrue(userCanUpdateProject);
            return promises.resolve();
        }).catch_(error -> {
            fail();
            return promises.resolve();
        });
    }

    @Test
    public void projectContributorCanNotUpdateProjectTest() {
        final WorkspaceProject project = getProject();
        doReturn(promises.resolve(Optional.of(new RolePermissions("CONTRIBUTOR", true, false, true, true))))
                .when(projectController).getBranchPermissionsForUser(project, "branch");
        projectController.canUpdateProject(project).then(userCanUpdateProject -> {
            assertFalse(userCanUpdateProject);
            return promises.resolve();
        }).catch_(error -> {
            fail();
            return promises.resolve();
        });
    }

    @Test
    public void userCanUpdateBranchTest() {
        final WorkspaceProject project = getProject();
        when(authorizationManager.authorize(project.getRepository(),
                                            RepositoryAction.UPDATE,
                                            user)).thenReturn(true);
        projectController.canUpdateBranch(project, project.getRepository().getBranch("branch2").get()).then(userCanUpdateBranch -> {
            assertTrue(userCanUpdateBranch);
            return promises.resolve();
        }).catch_(error -> {
            fail();
            return promises.resolve();
        });
    }

    @Test
    public void userCanNotUpdateBranchTest() {
        final WorkspaceProject project = getProject();
        when(authorizationManager.authorize(project.getRepository(),
                                            RepositoryAction.UPDATE,
                                            user)).thenReturn(false);
        doReturn(promises.resolve(Optional.of(new RolePermissions("CONTRIBUTOR", true, false, true, true))))
                .when(projectController).getBranchPermissionsForUser(project, "branch2");
        projectController.canUpdateBranch(project, project.getRepository().getBranch("branch2").get()).then(userCanUpdateBranch -> {
            assertFalse(userCanUpdateBranch);
            return promises.resolve();
        }).catch_(error -> {
            fail();
            return promises.resolve();
        });
    }

    @Test
    public void projectContributorCanUpdateBranchTest() {
        final WorkspaceProject project = getProject();
        doReturn(promises.resolve(Optional.of(new RolePermissions("CONTRIBUTOR", true, true, true, true))))
                .when(projectController).getBranchPermissionsForUser(project, "branch2");
        projectController.canUpdateBranch(project, project.getRepository().getBranch("branch2").get()).then(userCanUpdateBranch -> {
            assertTrue(userCanUpdateBranch);
            return promises.resolve();
        }).catch_(error -> {
            fail();
            return promises.resolve();
        });
    }

    @Test
    public void projectContributorCanNotUpdateBranchTest() {
        final WorkspaceProject project = getProject();
        doReturn(promises.resolve(Optional.of(new RolePermissions("CONTRIBUTOR", true, false, true, true))))
                .when(projectController).getBranchPermissionsForUser(project, "branch2");
        projectController.canUpdateBranch(project, project.getRepository().getBranch("branch2").get()).then(userCanUpdateBranch -> {
            assertFalse(userCanUpdateBranch);
            return promises.resolve();
        }).catch_(error -> {
            fail();
            return promises.resolve();
        });
    }

    @Test
    public void userCanDeleteProjectTest() {
        final WorkspaceProject project = getProject();
        when(authorizationManager.authorize(project.getRepository(),
                                            RepositoryAction.DELETE,
                                            user)).thenReturn(true);
        assertTrue(projectController.canDeleteProject(project));
    }

    @Test
    public void userCanNotDeleteProjectTest() {
        final WorkspaceProject project = getProject();
        when(authorizationManager.authorize(project.getRepository(),
                                            RepositoryAction.DELETE,
                                            user)).thenReturn(false);
        assertFalse(projectController.canDeleteProject(project));
    }

    @Test
    public void projectContributorCanDeleteProjectTest() {
        final WorkspaceProject project = getProject();
        final Collection<Contributor> projectContributors = project.getRepository().getContributors();
        doReturn(true).when(projectController).userIsAtLeast(eq(ContributorType.OWNER),
                                                             same(projectContributors));
        assertTrue(projectController.canDeleteProject(project));
    }

    @Test
    public void projectContributorCanNotDeleteProjectTest() {
        final WorkspaceProject project = getProject();
        doReturn(false).when(projectController).userIsAtLeast(eq(ContributorType.OWNER),
                                                              any());
        assertFalse(projectController.canDeleteProject(project));
    }

    @Test
    public void spaceContributorCanDeleteProjectTest() {
        final WorkspaceProject project = getProject();
        final Collection<Contributor> spaceContributors = project.getOrganizationalUnit().getContributors();
        doReturn(true).when(projectController).userIsAtLeast(eq(ContributorType.OWNER),
                                                             same(spaceContributors));
        assertTrue(projectController.canDeleteProject(project));
    }

    @Test
    public void spaceContributorCanNotDeleteProjectTest() {
        final WorkspaceProject project = getProject();
        doReturn(false).when(projectController).userIsAtLeast(eq(ContributorType.OWNER),
                                                              any());
        assertFalse(projectController.canDeleteProject(project));
    }

    @Test
    public void userCanBuildProjectTest() {
        final WorkspaceProject project = getProject();
        when(authorizationManager.authorize(project.getRepository(),
                                            RepositoryAction.BUILD,
                                            user)).thenReturn(true);
        projectController.canBuildProject(project).then(userCanBuildProject -> {
            assertTrue(userCanBuildProject);
            return promises.resolve();
        }).catch_(error -> {
            fail();
            return promises.resolve();
        });
    }

    @Test
    public void userCanNotBuildProjectTest() {
        final WorkspaceProject project = getProject();
        when(authorizationManager.authorize(project.getRepository(),
                                            RepositoryAction.BUILD,
                                            user)).thenReturn(false);
        doReturn(promises.resolve(Optional.of(new RolePermissions("CONTRIBUTOR", true, false, true, true))))
                .when(projectController).getBranchPermissionsForUser(project, "branch");
        projectController.canBuildProject(project).then(userCanBuildProject -> {
            assertFalse(userCanBuildProject);
            return promises.resolve();
        }).catch_(error -> {
            fail();
            return promises.resolve();
        });
    }

    @Test
    public void projectContributorCanBuildProjectTest() {
        final WorkspaceProject project = getProject();
        doReturn(promises.resolve(Optional.of(new RolePermissions("CONTRIBUTOR", true, true, true, true))))
                .when(projectController).getBranchPermissionsForUser(project, "branch");
        projectController.canBuildProject(project).then(userCanBuildProject -> {
            assertTrue(userCanBuildProject);
            return promises.resolve();
        }).catch_(error -> {
            fail();
            return promises.resolve();
        });
    }

    @Test
    public void projectContributorCanNotBuildProjectTest() {
        final WorkspaceProject project = getProject();
        doReturn(promises.resolve(Optional.of(new RolePermissions("CONTRIBUTOR", true, false, true, true))))
                .when(projectController).getBranchPermissionsForUser(project, "branch");
        projectController.canBuildProject(project).then(userCanBuildProject -> {
            assertFalse(userCanBuildProject);
            return promises.resolve();
        }).catch_(error -> {
            fail();
            return promises.resolve();
        });
    }

    @Test
    public void userCanDeployProjectTest() {
        final WorkspaceProject project = getProject();
        when(authorizationManager.authorize(project.getRepository(),
                                            RepositoryAction.BUILD,
                                            user)).thenReturn(true);
        projectController.canDeployProject(project).then(userCanDeployProject -> {
            assertTrue(userCanDeployProject);
            return promises.resolve();
        }).catch_(error -> {
            fail();
            return promises.resolve();
        });
    }

    @Test
    public void userCanNotDeployProjectTest() {
        final WorkspaceProject project = getProject();
        when(authorizationManager.authorize(project.getRepository(),
                                            RepositoryAction.BUILD,
                                            user)).thenReturn(false);
        doReturn(promises.resolve(Optional.of(new RolePermissions("CONTRIBUTOR", true, true, true, false))))
                .when(projectController).getBranchPermissionsForUser(project, "branch");
        projectController.canDeployProject(project).then(userCanDeployProject -> {
            assertFalse(userCanDeployProject);
            return promises.resolve();
        }).catch_(error -> {
            fail();
            return promises.resolve();
        });
    }

    @Test
    public void projectContributorCanDeployProjectTest() {
        final WorkspaceProject project = getProject();
        doReturn(promises.resolve(Optional.of(new RolePermissions("CONTRIBUTOR", true, true, true, true))))
                .when(projectController).getBranchPermissionsForUser(project, "branch");
        projectController.canDeployProject(project).then(userCanDeployProject -> {
            assertTrue(userCanDeployProject);
            return promises.resolve();
        }).catch_(error -> {
            fail();
            return promises.resolve();
        });
    }

    @Test
    public void projectContributorCanNotDeployProjectTest() {
        final WorkspaceProject project = getProject();
        doReturn(promises.resolve(Optional.of(new RolePermissions("CONTRIBUTOR", true, true, true, false))))
                .when(projectController).getBranchPermissionsForUser(project, "branch");
        projectController.canDeployProject(project).then(userCanDeployProject -> {
            assertFalse(userCanDeployProject);
            return promises.resolve();
        }).catch_(error -> {
            fail();
            return promises.resolve();
        });
    }

    @Test
    public void userCanReadBranchTest() {
        final WorkspaceProject project = getProject();
        when(authorizationManager.authorize(project.getRepository(),
                                            RepositoryAction.READ,
                                            user)).thenReturn(true);
        projectController.canReadBranch(project).then(userCanReadBranch -> {
            assertTrue(userCanReadBranch);
            return promises.resolve();
        }).catch_(error -> {
            fail();
            return promises.resolve();
        });
    }

    @Test
    public void userCanNotReadBranchTest() {
        final WorkspaceProject project = getProject();
        when(authorizationManager.authorize(project.getRepository(),
                                            RepositoryAction.READ,
                                            user)).thenReturn(false);
        doReturn(promises.resolve(false)).when(projectController).checkBranchPermission(eq(project),
                                                                                        eq("branch"),
                                                                                        any());
        projectController.canReadBranch(project).then(userCanReadBranch -> {
            assertFalse(userCanReadBranch);
            return promises.resolve();
        }).catch_(error -> {
            fail();
            return promises.resolve();
        });
    }

    @Test
    public void projectContributorCanReadBranchTest() {
        final WorkspaceProject project = getProject();
        doReturn(promises.resolve(Optional.of(new RolePermissions("CONTRIBUTOR", true, true, true, true))))
                .when(projectController).getBranchPermissionsForUser(project, "branch");
        projectController.canDeleteBranch(project).then(userCanDeleteBranch -> {
            assertTrue(userCanDeleteBranch);
            return promises.resolve();
        }).catch_(error -> {
            fail();
            return promises.resolve();
        });
    }

    @Test
    public void projectContributorCanNotReadBranchTest() {
        final WorkspaceProject project = getProject();
        doReturn(promises.resolve(Optional.of(new RolePermissions("CONTRIBUTOR", false, true, true, true))))
                .when(projectController).getBranchPermissionsForUser(project, "branch");
        projectController.canReadBranch(project).then(userCanReadBranch -> {
            assertFalse(userCanReadBranch);
            return promises.resolve();
        }).catch_(error -> {
            fail();
            return promises.resolve();
        });
    }

    @Test
    public void userCanDeleteBranchTest() {
        final WorkspaceProject project = getProject();
        when(authorizationManager.authorize(project.getRepository(),
                                            RepositoryAction.DELETE,
                                            user)).thenReturn(true);
        projectController.canDeleteBranch(project).then(userCanDeleteBranch -> {
            assertTrue(userCanDeleteBranch);
            return promises.resolve();
        }).catch_(error -> {
            fail();
            return promises.resolve();
        });
    }

    @Test
    public void userCanNotDeleteBranchTest() {
        final WorkspaceProject project = getProject();
        when(authorizationManager.authorize(project.getRepository(),
                                            RepositoryAction.DELETE,
                                            user)).thenReturn(false);
        doReturn(promises.resolve(false)).when(projectController).checkBranchPermission(eq(project),
                                                                                        eq("branch"),
                                                                                        any());
        projectController.canDeleteBranch(project).then(userCanDeleteBranch -> {
            assertFalse(userCanDeleteBranch);
            return promises.resolve();
        }).catch_(error -> {
            fail();
            return promises.resolve();
        });
    }

    @Test
    public void userCanSubmitChangeRequestTest() {
        final WorkspaceProject project = getProject();

        when(authorizationManager.authorize(project.getRepository(),
                                            RepositoryAction.UPDATE,
                                            user)).thenReturn(true);

        projectController.canSubmitChangeRequest(project).then(userCanSubmitChangeRequest -> {
            assertTrue(userCanSubmitChangeRequest);
            return promises.resolve();
        }).catch_(error -> {
            fail();
            return promises.resolve();
        });
    }

    @Test
    public void userCannotSubmitChangeRequestTest() {
        final WorkspaceProject project = getProject();
        when(authorizationManager.authorize(project.getRepository(),
                                            RepositoryAction.UPDATE,
                                            user)).thenReturn(false);
        doReturn(promises.resolve(false)).when(projectController).checkBranchPermission(eq(project),
                                                                                        eq("branch"),
                                                                                        any());
        projectController.canSubmitChangeRequest(project).then(userCanSubmitChangeRequest -> {
            assertFalse(userCanSubmitChangeRequest);
            return promises.resolve();
        }).catch_(error -> {
            fail();
            return promises.resolve();
        });
    }

    @Test
    public void userCannotSubmitChangeRequestWhenInMasterBranchTest() {
        WorkspaceProject project = mock(WorkspaceProject.class);
        Branch master = mock(Branch.class);
        doReturn("master").when(master).getName();
        doReturn(master).when(project).getBranch();

        projectController.canSubmitChangeRequest(project).then(userCanSubmitChangeRequest -> {
            assertFalse(userCanSubmitChangeRequest);
            return promises.resolve();
        }).catch_(error -> {
            fail();
            return promises.resolve();
        });
    }

    @Test
    public void projectContributorCanDeleteBranchTest() {
        final WorkspaceProject project = getProject();
        doReturn(promises.resolve(Optional.of(new RolePermissions("CONTRIBUTOR", true, true, true, true))))
                .when(projectController).getBranchPermissionsForUser(project, "branch");
        projectController.canDeleteBranch(project).then(userCanDeleteBranch -> {
            assertTrue(userCanDeleteBranch);
            return promises.resolve();
        }).catch_(error -> {
            fail();
            return promises.resolve();
        });
    }

    @Test
    public void projectContributorCanNotDeleteBranchTest() {
        final WorkspaceProject project = getProject();
        doReturn(promises.resolve(Optional.of(new RolePermissions("CONTRIBUTOR", true, true, false, true))))
                .when(projectController).getBranchPermissionsForUser(project, "branch");
        projectController.canDeleteBranch(project).then(userCanDeleteBranch -> {
            assertFalse(userCanDeleteBranch);
            return promises.resolve();
        }).catch_(error -> {
            fail();
            return promises.resolve();
        });
    }

    @Test
    public void userCanUpdateAllBranchesTest() {
        final WorkspaceProject project = getProject();
        when(authorizationManager.authorize(project.getRepository(),
                                            RepositoryAction.UPDATE,
                                            user)).thenReturn(true);
        projectController.getUpdatableBranches(project).then(branches -> {
            assertEquals(2, branches.size());
            return promises.resolve();
        }).catch_(error -> {
            fail();
            return promises.resolve();
        });
    }

    @Test
    public void userCanReadAllBranchesTest() {
        final WorkspaceProject project = getProject();
        when(authorizationManager.authorize(project.getRepository(),
                                            RepositoryAction.READ,
                                            user)).thenReturn(true);
        projectController.getReadableBranches(project).then(branches -> {
            assertEquals(2, branches.size());
            return promises.resolve();
        }).catch_(error -> {
            fail();
            return promises.resolve();
        });
    }

    @Test
    public void getReadableBranchesWhenInvalidModuleTest() {
        WorkspaceProject project = mock(WorkspaceProject.class);

        projectController.getReadableBranches(project).then(branches -> {
            assertEquals(0, branches.size());
            return promises.resolve();
        }).catch_(error -> {
            fail();
            return promises.resolve();
        });
    }

    @Test
    public void userCanNotUpdateAllBranchesTest() {
        final WorkspaceProject project = getProject();
        when(authorizationManager.authorize(project.getRepository(),
                                            RepositoryAction.UPDATE,
                                            user)).thenReturn(false);

        final Map<String, RolePermissions> branch1PermissionsByRole = new HashMap<>();
        branch1PermissionsByRole.put("CONTRIBUTOR", new RolePermissions("CONTRIBUTOR", true, false, true, true));

        final Map<String, RolePermissions> branch2PermissionsByRole = new HashMap<>();
        branch2PermissionsByRole.put("CONTRIBUTOR", new RolePermissions("CONTRIBUTOR", true, false, true, true));

        final Map<String, BranchPermissions> branchPermissions = new HashMap<>();
        branchPermissions.put("branch", new BranchPermissions("branch", branch1PermissionsByRole));
        branchPermissions.put("branch2", new BranchPermissions("branch2", branch2PermissionsByRole));

        doReturn(Optional.of(new Contributor("contributor", ContributorType.CONTRIBUTOR))).when(projectController).getUserContributor(any());
        doReturn(branchPermissions).when(projectPermissionsService).loadBranchPermissions(anyString(), anyString(), anyList());

        projectController.getUpdatableBranches(project).then(branches -> {
            assertEquals(0, branches.size());
            return promises.resolve();
        }).catch_(error -> {
            fail();
            return promises.resolve();
        });
    }

    @Test
    public void projectContributorCanUpdateSomeBranchesTest() {
        final WorkspaceProject project = getProject();

        final Map<String, RolePermissions> branch1PermissionsByRole = new HashMap<>();
        branch1PermissionsByRole.put("CONTRIBUTOR", new RolePermissions("CONTRIBUTOR", true, false, true, true));

        final Map<String, RolePermissions> branch2PermissionsByRole = new HashMap<>();
        branch2PermissionsByRole.put("CONTRIBUTOR", new RolePermissions("CONTRIBUTOR", true, true, true, true));

        final Map<String, BranchPermissions> branchPermissions = new HashMap<>();
        branchPermissions.put("branch", new BranchPermissions("branch", branch1PermissionsByRole));
        branchPermissions.put("branch2", new BranchPermissions("branch2", branch2PermissionsByRole));

        doReturn(Optional.of(new Contributor("contributor", ContributorType.CONTRIBUTOR))).when(projectController).getUserContributor(any());
        doReturn(branchPermissions).when(projectPermissionsService).loadBranchPermissions(anyString(), anyString(), anyList());

        projectController.getUpdatableBranches(project).then(branches -> {
            assertEquals(1, branches.size());
            assertEquals("branch2", branches.get(0).getName());
            return promises.resolve();
        }).catch_(error -> {
            fail();
            return promises.resolve();
        });
    }

    @Test
    public void projectContributorCanReadAllBranchesTest() {
        final WorkspaceProject project = getProject();

        final Map<String, RolePermissions> branch1PermissionsByRole = new HashMap<>();
        branch1PermissionsByRole.put("CONTRIBUTOR", new RolePermissions("CONTRIBUTOR", true, false, false, false));

        final Map<String, RolePermissions> branch2PermissionsByRole = new HashMap<>();
        branch2PermissionsByRole.put("CONTRIBUTOR", new RolePermissions("CONTRIBUTOR", true, false, false, false));

        final Map<String, BranchPermissions> branchPermissions = new HashMap<>();
        branchPermissions.put("branch", new BranchPermissions("branch", branch1PermissionsByRole));
        branchPermissions.put("branch2", new BranchPermissions("branch2", branch2PermissionsByRole));

        doReturn(Optional.of(new Contributor("contributor", ContributorType.CONTRIBUTOR))).when(projectController).getUserContributor(any());
        doReturn(branchPermissions).when(projectPermissionsService).loadBranchPermissions(anyString(), anyString(), anyList());

        projectController.getReadableBranches(project).then(branches -> {
            assertEquals(2, branches.size());
            assertEquals("branch", branches.get(0).getName());
            assertEquals("branch2", branches.get(1).getName());
            return promises.resolve();
        }).catch_(error -> {
            fail();
            return promises.resolve();
        });
    }

    @Test
    public void projectContributorCanReadSomeBranchesTest() {
        final WorkspaceProject project = getProject();

        final Map<String, RolePermissions> branch1PermissionsByRole = new HashMap<>();
        branch1PermissionsByRole.put("CONTRIBUTOR", new RolePermissions("CONTRIBUTOR", false, false, false, false));

        final Map<String, RolePermissions> branch2PermissionsByRole = new HashMap<>();
        branch2PermissionsByRole.put("CONTRIBUTOR", new RolePermissions("CONTRIBUTOR", true, false, false, false));

        final Map<String, BranchPermissions> branchPermissions = new HashMap<>();
        branchPermissions.put("branch", new BranchPermissions("branch", branch1PermissionsByRole));
        branchPermissions.put("branch2", new BranchPermissions("branch2", branch2PermissionsByRole));

        doReturn(Optional.of(new Contributor("contributor", ContributorType.CONTRIBUTOR))).when(projectController).getUserContributor(any());
        doReturn(branchPermissions).when(projectPermissionsService).loadBranchPermissions(anyString(), anyString(), anyList());

        projectController.getReadableBranches(project).then(branches -> {
            assertEquals(1, branches.size());
            assertEquals("branch2", branches.get(0).getName());
            return promises.resolve();
        }).catch_(error -> {
            fail();
            return promises.resolve();
        });
    }

    @Test
    public void projectContributorCannotReadBranchesTest() {
        final WorkspaceProject project = getProject();

        final Map<String, RolePermissions> branch1PermissionsByRole = new HashMap<>();
        branch1PermissionsByRole.put("CONTRIBUTOR", new RolePermissions("CONTRIBUTOR", false, false, false, false));

        final Map<String, RolePermissions> branch2PermissionsByRole = new HashMap<>();
        branch2PermissionsByRole.put("CONTRIBUTOR", new RolePermissions("CONTRIBUTOR", false, false, false, false));

        final Map<String, BranchPermissions> branchPermissions = new HashMap<>();
        branchPermissions.put("branch", new BranchPermissions("branch", branch1PermissionsByRole));
        branchPermissions.put("branch2", new BranchPermissions("branch2", branch2PermissionsByRole));

        doReturn(Optional.of(new Contributor("contributor", ContributorType.CONTRIBUTOR))).when(projectController).getUserContributor(any());
        doReturn(branchPermissions).when(projectPermissionsService).loadBranchPermissions(anyString(), anyString(), anyList());

        projectController.getReadableBranches(project).then(branches -> {
            assertEquals(0, branches.size());
            return promises.resolve();
        }).catch_(error -> {
            fail();
            return promises.resolve();
        });
    }

    private WorkspaceProject getProject() {
        final Repository repository = mock(Repository.class);
        final List<Contributor> projectContributors = new ArrayList<>();
        doReturn(projectContributors).when(repository).getContributors();

        final OrganizationalUnit organizationalUnit = mock(OrganizationalUnit.class);
        final List<Contributor> organizationalUnitContributors = new ArrayList<>();
        doReturn(organizationalUnitContributors).when(organizationalUnit).getContributors();

        final Space space = mock(Space.class);

        final Branch branch = mock(Branch.class);
        doReturn("branch").when(branch).getName();
        final Branch branch2 = mock(Branch.class);
        doReturn("branch2").when(branch2).getName();

        final WorkspaceProject project = mock(WorkspaceProject.class);
        doReturn(repository).when(project).getRepository();
        doReturn(organizationalUnit).when(project).getOrganizationalUnit();
        doReturn(space).when(project).getSpace();
        doReturn(branch).when(project).getBranch();
        doReturn(Optional.of(branch)).when(repository).getBranch("branch");
        doReturn(Optional.of(branch2)).when(repository).getBranch("branch2");
        doReturn(Arrays.asList(branch, branch2)).when(repository).getBranches();
        doReturn(mock(Module.class)).when(project).getMainModule();

        return project;
    }
}
