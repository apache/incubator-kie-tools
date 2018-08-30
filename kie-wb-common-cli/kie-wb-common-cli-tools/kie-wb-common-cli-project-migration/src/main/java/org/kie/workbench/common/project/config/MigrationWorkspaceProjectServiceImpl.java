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
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.guvnor.common.services.project.backend.server.WorkspaceProjectServiceImpl;
import org.guvnor.common.services.project.events.NewProjectEvent;
import org.guvnor.common.services.project.model.Module;
import org.guvnor.common.services.project.service.ModuleRepositoryResolver;
import org.guvnor.common.services.project.service.ModuleService;
import org.uberfire.spaces.SpacesAPI;

@Alternative
public class MigrationWorkspaceProjectServiceImpl extends WorkspaceProjectServiceImpl {

    public MigrationWorkspaceProjectServiceImpl() {
        super();
    }

    @Inject
    public MigrationWorkspaceProjectServiceImpl(final MigrationOrganizationalUnitServiceImpl organizationalUnitService,
                                                final MigrationRepositoryServiceImpl repositoryService,
                                                final SpacesAPI spaces,
                                                final Event<NewProjectEvent> newProjectEvent,
                                                final Instance<ModuleService<? extends Module>> moduleServices,
                                                final ModuleRepositoryResolver repositoryResolver) {
        super(organizationalUnitService,
              repositoryService,
              spaces,
              newProjectEvent,
              moduleServices,
              repositoryResolver);
    }
}
