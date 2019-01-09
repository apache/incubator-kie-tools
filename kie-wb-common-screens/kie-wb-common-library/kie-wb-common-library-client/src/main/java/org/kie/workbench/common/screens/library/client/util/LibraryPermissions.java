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

import java.util.Collection;
import javax.inject.Inject;

import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.structure.client.security.OrganizationalUnitController;
import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.contributors.ContributorType;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.jboss.errai.security.shared.api.identity.User;

public class LibraryPermissions {

    private User user;

    private OrganizationalUnitController organizationalUnitController;

    private ProjectController projectController;

    @Inject
    public LibraryPermissions(final User user,
                              final OrganizationalUnitController organizationalUnitController,
                              final ProjectController projectController) {
        this.user = user;
        this.organizationalUnitController = organizationalUnitController;
        this.projectController = projectController;
    }

    public boolean userCanReadOrganizationalUnits() {
        return organizationalUnitController.canReadOrgUnits();
    }

    public boolean userCanReadOrganizationalUnit(final OrganizationalUnit organizationalUnit) {
        return userIsAtLeast(ContributorType.CONTRIBUTOR, organizationalUnit.getContributors())
                || organizationalUnitController.canReadOrgUnit(organizationalUnit);
    }

    public boolean userCanDeleteOrganizationalUnit(final OrganizationalUnit organizationalUnit) {
        return userIsAtLeast(ContributorType.OWNER, organizationalUnit.getContributors())
                || organizationalUnitController.canDeleteOrgUnit(organizationalUnit);
    }

    public boolean userCanUpdateOrganizationalUnit(final OrganizationalUnit organizationalUnit) {
        return userIsAtLeast(ContributorType.ADMIN, organizationalUnit.getContributors())
                || organizationalUnitController.canUpdateOrgUnit(organizationalUnit);
    }

    public boolean userCanCreateOrganizationalUnit() {
        return organizationalUnitController.canCreateOrgUnits();
    }

    public boolean userCanDeleteProject(final WorkspaceProject project) {
        return userIsAtLeast(ContributorType.OWNER, project.getRepository().getContributors())
                || userIsAtLeast(ContributorType.ADMIN, project.getOrganizationalUnit().getContributors())
                || projectController.canDeleteProject(project);
    }

    public boolean userCanDeleteBranch(final WorkspaceProject project) {
        return (userIsAtLeast(ContributorType.ADMIN, project.getRepository().getContributors())
                || userIsAtLeast(ContributorType.ADMIN, project.getOrganizationalUnit().getContributors())
                || userCanDeleteProject(project))
                && !project.getBranch().getName().equals("master");
    }

    public boolean userCanBuildProject(final WorkspaceProject project) {
        return (userIsAtLeast(ContributorType.ADMIN, project.getRepository().getContributors())
                || userIsAtLeast(ContributorType.CONTRIBUTOR, project.getOrganizationalUnit().getContributors())
                || projectController.canBuildProject(project))
                && project.getMainModule() != null;
    }

    public boolean userCanDeployProject(final WorkspaceProject project) {
        return (userIsAtLeast(ContributorType.ADMIN, project.getRepository().getContributors())
                || userIsAtLeast(ContributorType.ADMIN, project.getOrganizationalUnit().getContributors())
                || projectController.canBuildProject(project))
                && project.getMainModule() != null;
    }

    public boolean userCanUpdateProject(final WorkspaceProject project) {
        return (userIsAtLeast(ContributorType.ADMIN, project.getRepository().getContributors())
                || userIsAtLeast(ContributorType.CONTRIBUTOR, project.getOrganizationalUnit().getContributors())
                || projectController.canUpdateProject(project))
                && project.getMainModule() != null;
    }

    public boolean userCanCreateProject(final OrganizationalUnit organizationalUnit) {
        return userIsAtLeast(ContributorType.CONTRIBUTOR, organizationalUnit.getContributors())
                || projectController.canCreateProjects();
    }

    boolean userIsAtLeast(final ContributorType type,
                          final Collection<Contributor> contributors) {
        return contributors.stream().anyMatch(c -> c.getUsername().equals(user.getIdentifier())
                && ContributorType.PRIORITY_ORDER.indexOf(c.getType()) <= ContributorType.PRIORITY_ORDER.indexOf(type));
    }
}
