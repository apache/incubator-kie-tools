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

package org.guvnor.common.services.project.client.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import elemental2.promise.Promise;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.security.ProjectPermissionsService;
import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.contributors.ContributorType;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.config.RolePermissions;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.security.RepositoryAction;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.client.promise.Promises;
import org.uberfire.security.ResourceRef;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.workbench.model.ActivityResourceType;

@ApplicationScoped
public class ProjectController {

    private AuthorizationManager authorizationManager;
    private User user;
    private Caller<ProjectPermissionsService> projectPermissionsService;
    private Promises promises;

    @Inject
    public ProjectController(final AuthorizationManager authorizationManager,
                             final User user,
                             final Caller<ProjectPermissionsService> projectPermissionsService,
                             final Promises promises) {
        this.authorizationManager = authorizationManager;
        this.user = user;
        this.projectPermissionsService = projectPermissionsService;
        this.promises = promises;
    }

    public boolean canCreateProjects(final OrganizationalUnit organizationalUnit) {
        final boolean securityPermission = authorizationManager.authorize(Repository.RESOURCE_TYPE,
                                                                          RepositoryAction.CREATE,
                                                                          user);

        return securityPermission
                || userIsAtLeast(ContributorType.CONTRIBUTOR,
                                 organizationalUnit.getContributors());
    }

    public boolean canReadProject(final WorkspaceProject workspaceProject) {
        final boolean securityPermission = authorizationManager.authorize(workspaceProject.getRepository(),
                                                                          RepositoryAction.READ,
                                                                          user);

        return securityPermission
                || userIsAtLeast(ContributorType.OWNER,
                                 workspaceProject.getOrganizationalUnit().getContributors())
                || userIsAtLeast(ContributorType.CONTRIBUTOR,
                                 workspaceProject.getRepository().getContributors());
    }

    public Promise<Boolean> canUpdateProject(final WorkspaceProject workspaceProject) {
        return canUpdateBranch(workspaceProject,
                               workspaceProject.getBranch());
    }

    public Promise<Boolean> canUpdateBranch(final WorkspaceProject workspaceProject,
                                            final Branch branch) {
        if (workspaceProject.getMainModule() == null) {
            return promises.resolve(false);
        }

        if (authorizationManager.authorize(workspaceProject.getRepository(),
                                           RepositoryAction.UPDATE,
                                           user)) {
            return promises.resolve(true);
        }

        return checkBranchPermission(workspaceProject,
                                     branch.getName(),
                                     RolePermissions::canWrite);
    }

    public boolean canDeleteProject(final WorkspaceProject workspaceProject) {
        final boolean securityPermission = authorizationManager.authorize(workspaceProject.getRepository(),
                                                                          RepositoryAction.DELETE,
                                                                          user);

        return securityPermission
                || userIsAtLeast(ContributorType.OWNER,
                                 workspaceProject.getRepository().getContributors())
                || userIsAtLeast(ContributorType.OWNER,
                                 workspaceProject.getOrganizationalUnit().getContributors());
    }

    public Promise<Boolean> canBuildProject(final WorkspaceProject workspaceProject) {
        if (workspaceProject.getMainModule() == null) {
            return promises.resolve(false);
        }

        if (authorizationManager.authorize(workspaceProject.getRepository(),
                                           RepositoryAction.BUILD,
                                           user)) {
            return promises.resolve(true);
        }

        return checkBranchPermission(workspaceProject,
                                     workspaceProject.getBranch().getName(),
                                     RolePermissions::canWrite);
    }

    public Promise<Boolean> canDeployProject(final WorkspaceProject workspaceProject) {
        if (workspaceProject.getMainModule() == null) {
            return promises.resolve(false);
        }

        if (authorizationManager.authorize(workspaceProject.getRepository(),
                                           RepositoryAction.BUILD,
                                           user)) {
            return promises.resolve(true);
        }

        return checkBranchPermission(workspaceProject,
                                     workspaceProject.getBranch().getName(),
                                     RolePermissions::canDeploy);
    }

    public Promise<Boolean> canReadBranch(final WorkspaceProject project) {
        return canReadBranch(project,
                             project.getBranch().getName());
    }

    public Promise<Boolean> canReadBranch(final WorkspaceProject project,
                                          final String branch) {
        if (authorizationManager.authorize(project.getRepository(),
                                           RepositoryAction.READ,
                                           user)) {
            return promises.resolve(true);
        }

        return checkBranchPermission(project,
                                     branch,
                                     RolePermissions::canRead);
    }

    public Promise<Boolean> canDeleteBranch(final WorkspaceProject project) {
        return canDeleteBranch(project,
                               project.getBranch().getName());
    }

    public Promise<Boolean> canSubmitChangeRequest(final WorkspaceProject project) {
        return canSubmitChangeRequest(project,
                                      project.getBranch().getName());
    }

    public Promise<Boolean> canDeleteBranch(final WorkspaceProject project,
                                            final String branch) {
        if (project.getBranch().getName().equals("master")) {
            return promises.resolve(false);
        }

        if (authorizationManager.authorize(project.getRepository(),
                                           RepositoryAction.DELETE,
                                           user)) {
            return promises.resolve(true);
        }

        return checkBranchPermission(project,
                                     branch,
                                     RolePermissions::canDelete);
    }

    public Promise<Boolean> canViewDeploymentDetails(String id) {
        ResourceRef resourceRef = new ResourceRef(id, ActivityResourceType.PERSPECTIVE);
        Boolean authorized = authorizationManager.authorize(resourceRef, user);
        return promises.resolve(authorized);
    }

    public Promise<Boolean> canSubmitChangeRequest(final WorkspaceProject project,
                                                   final String branch) {
        if (project.getBranch().getName().equals("master")) {
            return promises.resolve(false);
        }

        if (authorizationManager.authorize(project.getRepository(),
                                           RepositoryAction.UPDATE,
                                           user)) {
            return promises.resolve(true);
        }

        return checkBranchPermission(project,
                                     branch,
                                     RolePermissions::canRead);
    }

    boolean userIsAtLeast(final ContributorType type,
                          final Collection<Contributor> contributors) {
        return contributors.stream().anyMatch(c -> c.getUsername().equals(user.getIdentifier())
                && ContributorType.PRIORITY_ORDER.indexOf(c.getType()) <= ContributorType.PRIORITY_ORDER.indexOf(type));
    }

    Optional<Contributor> getUserContributor(final Collection<Contributor> contributors) {
        return contributors.stream().filter(c -> c.getUsername().equals(user.getIdentifier())).findFirst();
    }

    Promise<Boolean> checkBranchPermission(final WorkspaceProject project,
                                           final String branch,
                                           final Function<RolePermissions, Boolean> rolePermissionsCheck) {
        return getBranchPermissionsForUser(project, branch).then(rolePermissions -> {
            if (rolePermissions.isPresent()) {
                return promises.resolve(rolePermissionsCheck.apply(rolePermissions.get()));
            }

            return promises.resolve(false);
        });
    }

    public Promise<Optional<RolePermissions>> getBranchPermissionsForUser(final WorkspaceProject project,
                                                                          final String branch) {
        return promises.promisify(projectPermissionsService,
                                  s -> {
                                      return s.loadBranchPermissions(project.getSpace().getName(),
                                                                     project.getRepository().getIdentifier(),
                                                                     branch);
                                  }).then(branchPermissions -> promises.resolve(getBranchPermissionsForUser(project, branchPermissions.getPermissionsByRole())));
    }

    public Optional<RolePermissions> getBranchPermissionsForUser(final WorkspaceProject project,
                                                                 final Map<String, RolePermissions> permissionsByRole) {
        final Optional<Contributor> userContributor = getUserContributor(project.getRepository().getContributors());
        if (userContributor.isPresent()) {
            final RolePermissions rolePermissions = permissionsByRole.get(userContributor.get().getType().name());
            if (rolePermissions != null) {
                return Optional.of(rolePermissions);
            }
        }

        return Optional.empty();
    }

    public Promise<List<Branch>> getReadableBranches(final WorkspaceProject project) {
        if (project.getMainModule() == null) {
            return promises.resolve(Collections.emptyList());
        }

        if (authorizationManager.authorize(project.getRepository(),
                                           RepositoryAction.READ,
                                           user)) {
            return promises.resolve(new ArrayList<>(project.getRepository().getBranches()));
        }

        return getBranchesWithPermission(project, RolePermissions::canRead);
    }

    public Promise<List<Branch>> getUpdatableBranches(final WorkspaceProject project) {
        if (project.getMainModule() == null) {
            return promises.resolve(Collections.emptyList());
        }

        if (authorizationManager.authorize(project.getRepository(),
                                           RepositoryAction.UPDATE,
                                           user)) {
            return promises.resolve(new ArrayList<>(project.getRepository().getBranches()));
        }

        return getBranchesWithPermission(project, RolePermissions::canWrite);
    }

    private Promise<List<Branch>> getBranchesWithPermission(final WorkspaceProject project,
                                                            final Function<RolePermissions, Boolean> rolePermissionsCheck) {

        return promises.promisify(projectPermissionsService, service -> {
            return service.loadBranchPermissions(project.getSpace().getName(),
                                                 project.getRepository().getIdentifier(),
                                                 project.getRepository().getBranches().stream().map(Branch::getName).collect(Collectors.toList()));
        }).then(branchPermissions -> promises.resolve(project.getRepository().getBranches().stream().filter(branch -> {
            final Optional<RolePermissions> branchPermissionsForUser = getBranchPermissionsForUser(project,
                                                                                                   branchPermissions.get(branch.getName()).getPermissionsByRole());
            return branchPermissionsForUser.map(rolePermissionsCheck).orElse(false);
        }).collect(Collectors.toList())));
    }
}
