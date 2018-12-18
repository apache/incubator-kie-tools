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
import org.guvnor.structure.backend.organizationalunit.OrganizationalUnitServiceImpl;
import org.guvnor.structure.backend.repositories.ConfiguredRepositories;
import org.guvnor.structure.organizationalunit.NewOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.RemoveOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.RepoAddedToOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.RepoRemovedFromOrganizationalUnitEvent;
import org.guvnor.structure.organizationalunit.UpdatedOrganizationalUnitEvent;
import org.jboss.errai.bus.server.annotations.Service;
import org.uberfire.io.IOService;
import org.uberfire.rpc.SessionInfo;
import org.uberfire.security.authz.AuthorizationManager;
import org.uberfire.spaces.SpacesAPI;

@Alternative
@Service
@ApplicationScoped
public class MigrationOrganizationalUnitServiceImpl extends OrganizationalUnitServiceImpl {

    public MigrationOrganizationalUnitServiceImpl() {
        super();
    }

    @Inject
    public MigrationOrganizationalUnitServiceImpl(final MigrationConfigurationServiceImpl configurationService,
                                                  final MigrationConfigurationFactoryImpl configurationFactory,
                                                  final MigrationOrganizationalUnitFactoryImpl organizationalUnitFactory,
                                                  final MigrationRepositoryServiceImpl repositoryService,
                                                  final BackwardCompatibleUtil backward,
                                                  final Event<NewOrganizationalUnitEvent> newOrganizationalUnitEvent,
                                                  final Event<RemoveOrganizationalUnitEvent> removeOrganizationalUnitEvent,
                                                  final Event<RepoAddedToOrganizationalUnitEvent> repoAddedToOrgUnitEvent,
                                                  final Event<RepoRemovedFromOrganizationalUnitEvent> repoRemovedFromOrgUnitEvent,
                                                  final Event<UpdatedOrganizationalUnitEvent> updatedOrganizationalUnitEvent,
                                                  final AuthorizationManager authorizationManager,
                                                  final SpacesAPI spaces,
                                                  final SessionInfo sessionInfo,
                                                  @Named("ioStrategy") final IOService ioService,
                                                  final ConfiguredRepositories configuredRepositories) {
        super(configurationService,
              configurationFactory,
              organizationalUnitFactory,
              repositoryService,
              backward,
              newOrganizationalUnitEvent,
              removeOrganizationalUnitEvent,
              repoAddedToOrgUnitEvent,
              repoRemovedFromOrgUnitEvent,
              updatedOrganizationalUnitEvent,
              authorizationManager,
              spaces,
              sessionInfo,
              ioService,
              configuredRepositories);
    }
}
