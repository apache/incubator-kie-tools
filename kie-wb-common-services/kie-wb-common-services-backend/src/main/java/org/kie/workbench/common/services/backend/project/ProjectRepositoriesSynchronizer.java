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
import org.guvnor.common.services.project.model.ProjectRepositories;
import org.guvnor.common.services.project.service.ProjectRepositoriesService;
import org.guvnor.common.services.project.service.ProjectRepositoryResolver;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.services.shared.project.KieProject;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;
import org.uberfire.workbench.events.ResourceBatchChangesEvent;
import org.uberfire.workbench.events.ResourceChange;
import org.uberfire.workbench.events.ResourceUpdated;
import org.uberfire.workbench.events.ResourceUpdatedEvent;

/**
 * A bridge between changes made to an underlying VFS and Project abstractions. When a Project's pom.xml
 * is updated this bridge ensures the Project's Repository definitions is updated accordingly.
 */
@ApplicationScoped
public class ProjectRepositoriesSynchronizer {

    private IOService ioService;
    private ProjectRepositoryResolver repositoryResolver;
    private ProjectRepositoriesService projectRepositoriesService;
    private ObservablePOMFile observablePOMFile;
    private KieProjectFactory projectFactory;

    public ProjectRepositoriesSynchronizer() {
        //Zero-arg constructor for CDI proxying
    }

    @Inject
    public ProjectRepositoriesSynchronizer(final @Named("ioStrategy") IOService ioService,
                                           final ProjectRepositoryResolver repositoryResolver,
                                           final ProjectRepositoriesService projectRepositoriesService,
                                           final ObservablePOMFile observablePOMFile,
                                           final KieProjectFactory projectFactory) {
        this.ioService = PortablePreconditions.checkNotNull("ioService",
                                                            ioService);
        this.repositoryResolver = PortablePreconditions.checkNotNull("repositoryResolver",
                                                                     repositoryResolver);
        this.projectRepositoriesService = PortablePreconditions.checkNotNull("projectRepositoriesService",
                                                                             projectRepositoriesService);
        this.observablePOMFile = PortablePreconditions.checkNotNull("observablePOMFile",
                                                                    observablePOMFile);
        this.projectFactory = PortablePreconditions.checkNotNull("projectFactory",
                                                                 projectFactory);
    }

    public void onResourceUpdated(@Observes final ResourceUpdatedEvent event) {
        if (observablePOMFile.accept(event.getPath().getFileName())) {
            syncProjectRepositories(event.getPath());
        }
    }

    public void onBatchResourceChanges(@Observes final ResourceBatchChangesEvent resourceBatchChangesEvent) {
        for (final Map.Entry<org.uberfire.backend.vfs.Path, Collection<ResourceChange>> entry : resourceBatchChangesEvent.getBatch().entrySet()) {
            if (observablePOMFile.accept(entry.getKey().getFileName()) && isUpdate(entry.getValue())) {
                syncProjectRepositories(entry.getKey());
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

    private void syncProjectRepositories(final org.uberfire.backend.vfs.Path _path) {
        //Load existing Repository definitions for Project
        final Path path = ioService.get(URI.create(_path.toURI()));
        final KieProject project = projectFactory.simpleProjectInstance(path.getParent());
        final ProjectRepositories projectRepositories = projectRepositoriesService.load(project.getRepositoriesPath());

        //Load all Repository definitions resolved for the Project
        final Set<MavenRepositoryMetadata> mavenRepositories = repositoryResolver.getRemoteRepositoriesMetaData(project);

        //Identify Project Repositories to be removed (they're not in the Repositories resolved for the Project)
        final Set<MavenRepositoryMetadata> existingMavenRepositories = new HashSet<MavenRepositoryMetadata>();
        final Set<ProjectRepositories.ProjectRepository> repositoriesToRemove = new HashSet<ProjectRepositories.ProjectRepository>();
        for (ProjectRepositories.ProjectRepository projectRepository : projectRepositories.getRepositories()) {
            final MavenRepositoryMetadata existingMavenRepository = projectRepository.getMetadata();
            if (mavenRepositories.contains(existingMavenRepository)) {
                existingMavenRepositories.add(existingMavenRepository);
            } else {
                repositoriesToRemove.add(projectRepository);
            }
        }

        //Identify Maven Repositories to be added (they're not in the Project Repositories)
        final Set<MavenRepositoryMetadata> repositoriesToAdd = new HashSet<MavenRepositoryMetadata>();
        for (MavenRepositoryMetadata mavenRepository : mavenRepositories) {
            if (!existingMavenRepositories.contains(mavenRepository)) {
                repositoriesToAdd.add(mavenRepository);
            }
        }

        //Delete identified Maven Repositories
        for (ProjectRepositories.ProjectRepository repository : repositoriesToRemove) {
            projectRepositories.getRepositories().remove(repository);
        }

        //Add identified Maven Repositories
        for (MavenRepositoryMetadata repository : repositoriesToAdd) {
            projectRepositories.getRepositories().add(new ProjectRepositories.ProjectRepository(true,
                                                                                                repository));
        }

        //Update project.repositories file
        projectRepositoriesService.save(project.getRepositoriesPath(),
                                        projectRepositories,
                                        "Automatic synchronization");
    }
}
