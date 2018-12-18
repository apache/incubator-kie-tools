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
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.structure.backend.backcompat.BackwardCompatibleUtil;
import org.guvnor.structure.backend.repositories.RepositoryServiceImpl;
import org.guvnor.structure.repositories.GitMetadataStore;
import org.guvnor.structure.repositories.NewRepositoryEvent;
import org.guvnor.structure.repositories.RepositoryRemovedEvent;
import org.guvnor.structure.server.repositories.RepositoryFactory;
import org.jboss.errai.bus.server.annotations.Service;
import org.jboss.errai.security.shared.api.identity.User;
import org.uberfire.io.IOService;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.spaces.SpacesAPI;

@Alternative
@Service
@ApplicationScoped
public class MigrationRepositoryServiceImpl extends RepositoryServiceImpl {

    public MigrationRepositoryServiceImpl() {
        super();
    }

    @Inject
    public MigrationRepositoryServiceImpl(@Named("ioStrategy") final IOService ioService,
                                          final GitMetadataStore metadataStore,
                                          final MigrationConfigurationServiceImpl configurationService,
                                          final MigrationOrganizationalUnitServiceImpl organizationalUnitService,
                                          final MigrationConfigurationFactoryImpl configurationFactory,
                                          final RepositoryFactory repositoryFactory,
                                          final Event<NewRepositoryEvent> event,
                                          final Event<RepositoryRemovedEvent> repositoryRemovedEvent,
                                          final BackwardCompatibleUtil backward,
                                          final MigrationConfiguredRepositories configuredRepositories,
                                          final AuthorizationManager authorizationManager,
                                          final User user,
                                          final SpacesAPI spacesAPI) {
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
              spacesAPI);
    }
}
