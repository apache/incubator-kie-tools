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

package org.guvnor.asset.management.backend.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;

import org.guvnor.asset.management.model.RepositoryStructureModel;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.POMService;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.common.services.shared.metadata.MetadataService;
import org.guvnor.structure.repositories.Repository;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;

import static org.guvnor.structure.repositories.EnvironmentParameters.MANAGED;

public class RepositoryStructureModelLoader {

    @Inject
    private POMService pomService;

    @Inject
    private ProjectService<? extends Project> projectService;

    @Inject
    private ManagedStatusUpdater managedStatusUpdater;

    @Inject
    private MetadataService metadataService;

    public RepositoryStructureModel load(final Repository repository,
                                         final String branch,
                                         final boolean includeModules) {
        if (repository == null) {
            return null;
        }

        final Project project = projectService.resolveToParentProject(repository.getBranchRoot(branch));

        if (project != null) {
            return getModel(repository,
                            includeModules,
                            project);
        } else {
            return getModel(repository,
                            branch);
        }
    }

    private RepositoryStructureModel getModel(final Repository repository,
                                              final String branch) {

        final RepositoryStructureModel model = new RepositoryStructureModel();

        //if no parent pom.xml present we must check if there are orphan projects for this repository.
        final Set<Project> repositoryProjects = projectService.getProjects(repository,
                                                                           branch);

        switch (getManagedStatus(repository)) {
            case MANAGED:
                model.setManaged(true);
                break;
            case UNMANAGED:
                model.setManaged(false);
                break;
            case UNKNOWN:
                if (repositoryProjects.isEmpty()) {
                    //there are no projects and the managed attribute is not set, means the repository was never initialized.
                    return null;
                } else if (repositoryProjects.size() > 1) {
                    //update managed status
                    managedStatusUpdater.updateManagedStatus(repository,
                                                             false);
                } else {
                    break;
                }
        }

        model.setOrphanProjects(new ArrayList<>(repositoryProjects));
        for (Project orphanProject : repositoryProjects) {
            final POM pom = pomService.load(orphanProject.getPomXMLPath());
            model.getOrphanProjectsPOM().put(orphanProject.getIdentifier(),
                                             pom);
        }

        return model;
    }

    private RepositoryStructureModel getModel(final Repository repository,
                                              final boolean includeModules,
                                              final Project project) {
        final RepositoryStructureModel model = new RepositoryStructureModel();

        switch (getManagedStatus(repository)) {
            case MANAGED:
                model.setManaged(true);
                break;
            case UNMANAGED:
                model.setManaged(false);
                break;
            case UNKNOWN:
                break;
        }

        if (!model.isManaged()) {
            //uncommon case, the repository is managed. Update managed status.
            managedStatusUpdater.updateManagedStatus(repository,
                                                     true);
            model.setManaged(true);
        }

        model.setPOM(pomService.load(project.getPomXMLPath()));
        model.setPOMMetaData(metadataService.getMetadata(project.getPomXMLPath()));
        model.setPathToPOM(project.getPomXMLPath());
        model.setModules(project.getModules());

        if (includeModules && project.getModules() != null) {
            model.setModulesProject(getModuleProjects(project.getRootPath(),
                                                      project.getModules()));
        }

        return model;
    }

    private Map<String, Project> getModuleProjects(final Path projectRootPath,
                                                   final Collection<String> moduleNames) {
        final Map<String, Project> result = new HashMap<>();

        final org.uberfire.java.nio.file.Path parentPath = Paths.convert(projectRootPath);

        for (final String moduleName : moduleNames) {
            result.put(moduleName,
                       projectService.resolveProject(Paths.convert(parentPath.resolve(moduleName))));
        }

        return result;
    }

    private ManagedStatus getManagedStatus(final Repository repository) {
        if (repository.getEnvironment() != null) {
            final Boolean managed = (Boolean) repository.getEnvironment().get(MANAGED);
            if (Boolean.TRUE.equals(managed)) {
                return ManagedStatus.MANAGED;
            } else {
                return ManagedStatus.UNMANAGED;
            }
        } else {
            return ManagedStatus.UNKNOWN;
        }
    }

    enum ManagedStatus {
        MANAGED,
        UNMANAGED,
        UNKNOWN
    }
}
