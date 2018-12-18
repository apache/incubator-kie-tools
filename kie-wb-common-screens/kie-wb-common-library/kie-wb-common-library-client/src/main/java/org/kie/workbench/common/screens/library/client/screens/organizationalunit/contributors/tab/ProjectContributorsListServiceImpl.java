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

package org.kie.workbench.common.screens.library.client.screens.organizationalunit.contributors.tab;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.inject.Inject;

import org.guvnor.common.services.project.client.security.ProjectController;
import org.guvnor.structure.contributors.Contributor;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryService;
import org.jboss.errai.bus.client.api.messaging.Message;
import org.jboss.errai.common.client.api.Caller;
import org.jboss.errai.common.client.api.ErrorCallback;
import org.kie.workbench.common.screens.library.client.util.LibraryPlaces;

public class ProjectContributorsListServiceImpl implements ContributorsListService {

    private LibraryPlaces libraryPlaces;

    private Caller<RepositoryService> repositoryService;

    private ProjectController projectController;

    private SpaceContributorsListServiceImpl spaceContributorsListService;

    @Inject
    public ProjectContributorsListServiceImpl(final LibraryPlaces libraryPlaces,
                                              final Caller<RepositoryService> repositoryService,
                                              final ProjectController projectController,
                                              final SpaceContributorsListServiceImpl spaceContributorsListService) {
        this.libraryPlaces = libraryPlaces;
        this.repositoryService = repositoryService;
        this.projectController = projectController;
        this.spaceContributorsListService = spaceContributorsListService;
    }

    @Override
    public void getContributors(Consumer<List<Contributor>> contributorsConsumer) {
        repositoryService.call((Repository repository) -> {
            contributorsConsumer.accept(new ArrayList<>(repository.getContributors()));
        }).getRepositoryFromSpace(libraryPlaces.getActiveSpace().getSpace(), libraryPlaces.getActiveWorkspace().getRepository().getAlias());
    }

    @Override
    public void saveContributors(final List<Contributor> contributors,
                                 final Runnable successCallback,
                                 final ErrorCallback<Message> errorCallback) {
        repositoryService.call((Repository repository) -> {
            repositoryService.call((Void) -> successCallback.run(), errorCallback).updateContributors(repository, contributors);
        }).getRepositoryFromSpace(libraryPlaces.getActiveSpace().getSpace(), libraryPlaces.getActiveWorkspace().getRepository().getAlias());
    }

    @Override
    public boolean canEditContributors() {
        return projectController.canUpdateProject(libraryPlaces.getActiveWorkspace());
    }

    @Override
    public void getValidUsernames(Consumer<List<String>> validUsernamesConsumer) {
        spaceContributorsListService.getContributors(contributors -> {
            validUsernamesConsumer.accept(contributors.stream().map(c -> c.getUsername()).collect(Collectors.toList()));
        });
    }
}
