/*
 * Copyright 2016 JBoss, by Red Hat, Inc
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

package org.kie.workbench.common.screens.library.api;

import java.util.List;
import java.util.Set;

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.DeploymentMode;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.config.BranchPermissions;
import org.guvnor.structure.repositories.Branch;
import org.jboss.errai.bus.server.annotations.Remote;
import org.kie.workbench.common.screens.examples.model.ImportProject;

@Remote
public interface LibraryService {

    String LAST_MODIFIED_TIME = "lastModifiedTime";
    String CREATED_TIME = "creationTime";

    OrganizationalUnitRepositoryInfo getDefaultOrganizationalUnitRepositoryInfo();

    OrganizationalUnitRepositoryInfo getOrganizationalUnitRepositoryInfo(final OrganizationalUnit selectedOrganizationalUnit);

    LibraryInfo getLibraryInfo(final OrganizationalUnit organizationalUnit);

    WorkspaceProject createProject(final String projectName,
                                   final OrganizationalUnit selectedOrganizationalUnit,
                                   final String description,
                                   final DeploymentMode deploymentMode);

    WorkspaceProject createProject(final OrganizationalUnit activeOrganizationalUnit,
                                   final POM pom,
                                   final DeploymentMode mode);

    Boolean thereIsAProjectInTheWorkbench();

    AssetQueryResult getProjectAssets(final ProjectAssetsQuery query);

    Boolean hasProjects(final OrganizationalUnit organizationalUnit);

    int getNumberOfAssets(final ProjectAssetsQuery query);

    int getNumberOfAssets(final WorkspaceProject project);

    Boolean hasAssets(final WorkspaceProject project);

    Set<ImportProject> getExampleProjects();

    List<OrganizationalUnit> getOrganizationalUnits();

    OrganizationalUnit getDefaultOrganizationalUnit();

    GAV createGAV(final String projectName,
                  final OrganizationalUnit selectedOrganizationalUnit);

    List<String> getAllUsers();

    void addBranch(final String newBranchName,
                   final String baseBranchName,
                   final WorkspaceProject project);

    void removeBranch(final WorkspaceProject project,
                      final Branch branch);

    BranchPermissions loadBranchPermissions(final String spaceName,
                                            final String projectIdentifier,
                                            final String branchName);

    void saveBranchPermissions(final String spaceName,
                               final String projectIdentifier,
                               final String branchName,
                               final BranchPermissions branchPermissions);

    Boolean isClustered();
}
