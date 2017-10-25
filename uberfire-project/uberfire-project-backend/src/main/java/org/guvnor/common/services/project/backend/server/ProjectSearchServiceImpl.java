/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.common.services.project.backend.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.guvnor.common.services.project.model.Project;
import org.guvnor.common.services.project.service.ProjectSearchService;
import org.guvnor.common.services.project.service.ProjectService;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.bus.server.annotations.Service;

/**
 * TODO: Improve using indexes. Avoid to iterate thorough the entire repo>project hierarchy.
 */
@Service
@ApplicationScoped
public class ProjectSearchServiceImpl implements ProjectSearchService {

    private RepositoryService repositoryService;
    private Instance<ProjectService<? extends Project>> projectServices;

    @Inject
    public ProjectSearchServiceImpl(RepositoryService repositoryService,
                                    Instance<ProjectService<? extends Project>> projectServices) {
        this.repositoryService = repositoryService;
        this.projectServices = projectServices;
    }

    public ProjectService getProjectService() {
        return projectServices.get();
    }

    @Override
    public Collection<Project> searchByName(String pattern,
                                            int maxItems,
                                            boolean caseSensitive) {
        List<Project> results = new ArrayList<>();
        for (Repository repository : repositoryService.getAllRepositories()) {
            ProjectService projectService = getProjectService();
            if (projectService != null) {
                Set<Project> repositoryProjects = projectService.getAllProjects(repository,
                                                                                "master");
                for (Project project : repositoryProjects) {
                    String name = project.getProjectName();
                    if (caseSensitive ? name.contains(pattern) : name.toLowerCase().contains(pattern.toLowerCase())) {
                        results.add(project);
                        if (maxItems > 0 && results.size() >= maxItems) {
                            return results;
                        }
                    }
                }
            }
        }
        return results;
    }

    @Override
    public Collection<Project> searchById(Collection<String> ids) {
        List<Project> results = new ArrayList<>();
        for (Repository repository : repositoryService.getAllRepositories()) {
            ProjectService projectService = getProjectService();
            if (projectService != null) {
                Set<Project> repositoryProjects = projectService.getAllProjects(repository,
                                                                                "master");
                for (Project project : repositoryProjects) {
                    if (ids.contains(project.getIdentifier())) {
                        results.add(project);
                    }
                }
            }
        }
        return results;
    }
}
