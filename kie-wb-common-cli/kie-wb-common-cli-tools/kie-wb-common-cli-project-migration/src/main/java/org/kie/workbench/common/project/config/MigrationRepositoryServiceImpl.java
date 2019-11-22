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

package org.kie.workbench.common.project.config;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.project.events.RepositoryContributorsUpdatedEvent;
import org.guvnor.structure.backend.backcompat.BackwardCompatibleUtil;
import org.guvnor.structure.backend.repositories.ConfiguredRepositories;
import org.guvnor.structure.backend.repositories.RepositoryServiceImpl;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorage;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorageRegistry;
import org.guvnor.structure.organizationalunit.config.SpaceInfo;
import org.guvnor.structure.repositories.Branch;
import org.guvnor.structure.repositories.GitMetadataStore;
import org.guvnor.structure.repositories.NewRepositoryEvent;
import org.guvnor.structure.repositories.Repository;
import org.guvnor.structure.repositories.RepositoryRemovedEvent;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.config.ConfigurationService;
import org.guvnor.structure.server.config.PasswordService;
import org.guvnor.structure.server.repositories.RepositoryFactory;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.spaces.SpacesAPI;

import static org.uberfire.backend.server.util.Paths.convert;

@Migration
@Service
@ApplicationScoped
public class MigrationRepositoryServiceImpl extends RepositoryServiceImpl {

    private SpaceConfigStorageRegistry spaceConfigStorage;
    private IOService ioService;

    public MigrationRepositoryServiceImpl() {
        super();
    }

    @Inject
    public MigrationRepositoryServiceImpl(@Named("ioStrategy") final IOService ioService,
                                          final GitMetadataStore metadataStore,
                                          final @Migration ConfigurationService configurationService,
                                          final @Migration OrganizationalUnitService organizationalUnitService,
                                          final @Migration ConfigurationFactory configurationFactory,
                                          final RepositoryFactory repositoryFactory,
                                          final Event<NewRepositoryEvent> event,
                                          final Event<RepositoryRemovedEvent> repositoryRemovedEvent,
                                          final BackwardCompatibleUtil backward,
                                          final @Migration ConfiguredRepositories configuredRepositories,
                                          final AuthorizationManager authorizationManager,
                                          final User user,
                                          final SpacesAPI spacesAPI,
                                          final SpaceConfigStorageRegistry spaceConfigStorage,
                                          final Event<RepositoryContributorsUpdatedEvent> projectContributorsUpdatedEvent,
                                          final PasswordService secureService) {
        super(ioService,
              metadataStore,
              configurationService,
              organizationalUnitService,
              configurationFactory,
              repositoryFactory,
              event,
              repositoryRemovedEvent,
              backward,
              configuredRepositories,
              authorizationManager,
              user,
              spacesAPI,
              spaceConfigStorage,
              projectContributorsUpdatedEvent,
              secureService);
        this.ioService = ioService;
        this.spaceConfigStorage = spaceConfigStorage;
    }

    public void deleteRepository(Repository repository) {
        Path path = getPath(repository);
        ioService.delete(path);
        this.removeRepositoryFromSpaceInfo(repository);
    }

    private org.uberfire.java.nio.file.Path getPath(Repository repo) {
        Branch defaultBranch = repo.getDefaultBranch().orElseThrow(() -> new IllegalStateException("Repository should have at least one branch."));
        return convert(defaultBranch.getPath()).getFileSystem().getPath(null);
    }

    private void removeRepositoryFromSpaceInfo(Repository repo) {
        SpaceConfigStorage spaceConfigStorage = this.spaceConfigStorage.get(repo.getSpace().getName());
        SpaceInfo spaceInfo = this.spaceConfigStorage.get(repo.getSpace().getName()).loadSpaceInfo();
        spaceInfo.removeRepository(repo.getAlias());
        spaceConfigStorage.saveSpaceInfo(spaceInfo);
    }
}
