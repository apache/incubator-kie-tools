/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.services.backend.project;

import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.builder.ObservablePOMFile;
import org.guvnor.common.services.project.model.MavenRepositoryMetadata;
import org.guvnor.common.services.project.model.ModuleRepositories;
import org.guvnor.common.services.project.service.ModuleRepositoriesService;
import org.guvnor.common.services.project.service.ModuleRepositoryResolver;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.services.shared.project.KieModule;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;
import org.uberfire.workbench.events.ResourceBatchChangesEvent;
import org.uberfire.workbench.events.ResourceChange;
import org.uberfire.workbench.events.ResourceUpdated;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

/**
 * A bridge between changes made to an underlying VFS and Module abstractions. When a Module's pom.xml
 * is updated this bridge ensures the Module's Repository definitions is updated accordingly.
 */
@ApplicationScoped
public class ModuleRepositoriesSynchronizer {

    private IOService ioService;
    private ModuleRepositoryResolver repositoryResolver;
    private ModuleRepositoriesService moduleRepositoriesService;
    private ObservablePOMFile observablePOMFile;
    private KieModuleFactory moduleFactory;

    public ModuleRepositoriesSynchronizer() {
        //Zero-arg constructor for CDI proxying
    }

    @Inject
    public ModuleRepositoriesSynchronizer(final @Named("ioStrategy") IOService ioService,
                                          final ModuleRepositoryResolver repositoryResolver,
                                          final ModuleRepositoriesService moduleRepositoriesService,
                                          final ObservablePOMFile observablePOMFile,
                                          final KieModuleFactory moduleFactory) {
        this.ioService = PortablePreconditions.checkNotNull("ioService",
                                                            ioService);
        this.repositoryResolver = PortablePreconditions.checkNotNull("repositoryResolver",
                                                                     repositoryResolver);
        this.moduleRepositoriesService = PortablePreconditions.checkNotNull("moduleRepositoriesService",
                                                                            moduleRepositoriesService);
        this.observablePOMFile = PortablePreconditions.checkNotNull("observablePOMFile",
                                                                    observablePOMFile);
        this.moduleFactory = PortablePreconditions.checkNotNull("moduleFactory",
                                                                moduleFactory);
    }

    public void onResourceUpdated(@Observes final ResourceUpdatedEvent event) {
        if (observablePOMFile.accept(event.getPath())) {
            syncModuleRepositories(event.getPath());
        }
    }

    public void onBatchResourceChanges(@Observes final ResourceBatchChangesEvent resourceBatchChangesEvent) {
        for (final Map.Entry<org.uberfire.backend.vfs.Path, Collection<ResourceChange>> entry : resourceBatchChangesEvent.getBatch().entrySet()) {
            if (observablePOMFile.accept(entry.getKey()) && isUpdate(entry.getValue())) {
                syncModuleRepositories(entry.getKey());
                break;
            }
        }
    }

    private boolean isUpdate(final Collection<ResourceChange> value) {
        for (final ResourceChange resourceChange : value) {
            if (resourceChange instanceof ResourceUpdated) {
                return true;
            }
        }
        return false;
    }

    private void syncModuleRepositories(final org.uberfire.backend.vfs.Path _path) {
        //Load existing Repository definitions for Module
        final Path path = ioService.get(URI.create(_path.toURI()));
        final KieModule module = moduleFactory.simpleModuleInstance(path.getParent());
        final ModuleRepositories moduleRepositories = moduleRepositoriesService.load(module.getRepositoriesPath());

        //Load all Repository definitions resolved for the Module
        final Set<MavenRepositoryMetadata> mavenRepositories = repositoryResolver.getRemoteRepositoriesMetaData(module);

        //Identify Module Repositories to be removed (they're not in the Repositories resolved for the Module)
        final Set<MavenRepositoryMetadata> existingMavenRepositories = new HashSet<MavenRepositoryMetadata>();
        final Set<ModuleRepositories.ModuleRepository> repositoriesToRemove = new HashSet<ModuleRepositories.ModuleRepository>();
        for (ModuleRepositories.ModuleRepository moduleRepository : moduleRepositories.getRepositories()) {
            final MavenRepositoryMetadata existingMavenRepository = moduleRepository.getMetadata();
            if (mavenRepositories.contains(existingMavenRepository)) {
                existingMavenRepositories.add(existingMavenRepository);
            } else {
                repositoriesToRemove.add(moduleRepository);
            }
        }

        //Identify Maven Repositories to be added (they're not in the Module Repositories)
        final Set<MavenRepositoryMetadata> repositoriesToAdd = new HashSet<MavenRepositoryMetadata>();
        for (MavenRepositoryMetadata mavenRepository : mavenRepositories) {
            if (!existingMavenRepositories.contains(mavenRepository)) {
                repositoriesToAdd.add(mavenRepository);
            }
        }

        //Delete identified Maven Repositories
        for (ModuleRepositories.ModuleRepository repository : repositoriesToRemove) {
            moduleRepositories.getRepositories().remove(repository);
        }

        //Add identified Maven Repositories
        for (MavenRepositoryMetadata repository : repositoriesToAdd) {
            moduleRepositories.getRepositories().add(new ModuleRepositories.ModuleRepository(true,
                                                                                             repository));
        }

        // TODO: project.repositories file does what?
        //Update project.repositories file
        moduleRepositoriesService.save(module.getRepositoriesPath(),
                                       moduleRepositories,
                                       "Automatic synchronization");
    }
}
