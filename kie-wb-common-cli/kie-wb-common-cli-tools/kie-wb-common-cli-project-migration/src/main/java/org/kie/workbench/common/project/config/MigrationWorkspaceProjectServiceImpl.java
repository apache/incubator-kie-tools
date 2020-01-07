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

import javax.enterprise.event.Event;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.project.backend.server.utils.PathUtil;
import org.guvnor.common.services.project.backend.server.WorkspaceProjectServiceImpl;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.service.ModuleRepositoryResolver;
import org.guvnor.common.services.project.service.ModuleService;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.organizationalunit.config.SpaceConfigStorageRegistry;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.repositories.RepositoryUpdatedEvent;
import org.guvnor.structure.repositories.NewBranchEvent;
import org.guvnor.structure.repositories.changerequest.ChangeRequestService;
import org.uberfire.spaces.SpacesAPI;
import org.uberfire.io.IOService;

@Migration
public class MigrationWorkspaceProjectServiceImpl extends WorkspaceProjectServiceImpl {

    public MigrationWorkspaceProjectServiceImpl() {
        super();
    }

    @Inject
    public MigrationWorkspaceProjectServiceImpl(final @Migration OrganizationalUnitService organizationalUnitService,
                                                final @Migration RepositoryService repositoryService,
                                                final SpacesAPI spaces,
                                                final Event<NewProjectEvent> newProjectEvent,
                                                final Event<RepositoryUpdatedEvent> repositoryUpdatedEvent,
                                                final Event<NewBranchEvent> newBranchEvent,
                                                final Instance<ModuleService<? extends Module>> moduleServices,
                                                final ModuleRepositoryResolver repositoryResolver,
                                                @Named("ioStrategy") final IOService ioService,
                                                final PathUtil pathUtil,
                                                final ChangeRequestService changeRequestService,
                                                final SpaceConfigStorageRegistry spaceConfigStorageRegistry) {
        super(organizationalUnitService,
              repositoryService,
              spaces,
              newProjectEvent,
              repositoryUpdatedEvent,
              newBranchEvent,
              moduleServices,
              repositoryResolver,
              ioService,
              pathUtil,
              changeRequestService,
              spaceConfigStorageRegistry);
    }
}
