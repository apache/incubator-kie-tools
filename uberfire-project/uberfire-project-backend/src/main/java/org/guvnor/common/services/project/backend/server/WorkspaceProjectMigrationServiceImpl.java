/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.guvnor.common.services.project.backend.server;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.project.WorkspaceProjectMigrationService;
import org.guvnor.common.services.project.service.ModuleService;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryCopier;
import org.guvnor.structure.repositories.RepositoryEnvironmentConfigurations;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.impl.git.GitRepository;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

public class WorkspaceProjectMigrationServiceImpl
        implements WorkspaceProjectMigrationService {

    private WorkspaceProjectService workspaceProjectService;
    private RepositoryService repositoryService;
    private Event<NewProjectEvent> newProjectEvent;
    private RepositoryCopier repositoryCopier;
    private ModuleService<? extends Module> moduleService;
    private IOService ioService;

    public WorkspaceProjectMigrationServiceImpl() {
    }

    @Inject
    public WorkspaceProjectMigrationServiceImpl(final WorkspaceProjectService workspaceProjectService,
                                                final RepositoryService repositoryService,
                                                final OrganizationalUnitService organizationalUnitService, // TODO remove unused
                                                final Event<NewProjectEvent> newProjectEvent,
                                                final RepositoryCopier repositoryCopier,
                                                final ModuleService<? extends Module> moduleService,
                                                final @Named("ioStrategy") IOService ioService) {
        this.workspaceProjectService = workspaceProjectService;
        this.repositoryService = repositoryService;
        this.newProjectEvent = newProjectEvent;
        this.repositoryCopier = repositoryCopier;
        this.moduleService = moduleService;
        this.ioService = ioService;
    }

    @Override
    public void migrate(final WorkspaceProject legacyWorkspaceProject) {
        new Migrator(legacyWorkspaceProject).migrate();
    }

    private class Migrator {

        private final WorkspaceProject legacyWorkspaceProject;
        private final Map<String, Repository> newRepositories = new HashMap<>();

        public Migrator(final WorkspaceProject legacyWorkspaceProject) {
            this.legacyWorkspaceProject = legacyWorkspaceProject;
        }

        public void migrate() {

            copyModulesToRepositories();
            fireNewProjectEvents();
        }

        private void fireNewProjectEvents() {
            for (final Repository repository : newRepositories.values()) {
                final WorkspaceProject newWorkspaceProject = workspaceProjectService.resolveProject(repository);
                newProjectEvent.fire(new NewProjectEvent(newWorkspaceProject));
            }
        }

        private void copyModulesToRepositories() {

            for (final Branch branch : legacyWorkspaceProject.getRepository().getBranches()) {

                for (final Module module : moduleService.getAllModules(branch)) {

                    if (!newRepositories.containsKey(module.getModuleName())) {
                        createRepository(module);
                    }

                    copyFromLegacyRepositoryToTheNew(branch,
                                                     module);
                }
            }
        }

        private void copyFromLegacyRepositoryToTheNew(final Branch branch,
                                                      final Module module) {
            final Repository targetRepository = newRepositories.get(module.getModuleName());

            final URI uri = URI.create( targetRepository.getScheme().toString() +"://" + branch.getName() + "@" + targetRepository.getSpace() + "/" + targetRepository.getAlias());
            final Path targetBranchRoot = ioService.get(uri);

            repositoryCopier.copy(targetRepository.getSpace(),
                                  module.getRootPath(),
                                  Paths.convert(targetBranchRoot));
        }

        private void createRepository(final Module module) {
            final RepositoryEnvironmentConfigurations configurations = new RepositoryEnvironmentConfigurations();
            configurations.setSpace(legacyWorkspaceProject.getOrganizationalUnit().getSpace().getName());
            final Repository repository = repositoryService.createRepository(legacyWorkspaceProject.getOrganizationalUnit(),
                                                                             GitRepository.SCHEME.toString(),
                                                                             repositoryCopier.makeSafeRepositoryName(module.getModuleName()),
                                                                             configurations);

            newRepositories.put(module.getModuleName(),
                                repository);
        }
    }
}
