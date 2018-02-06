/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.project.backend.server;

import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.common.services.project.model.GAV;
import org.guvnor.common.services.project.model.POM;
import org.guvnor.common.services.project.model.WorkspaceProject;
import org.guvnor.common.services.project.service.WorkspaceProjectService;
import org.guvnor.structure.organizationalunit.OrganizationalUnit;
import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.config.ConfigurationService;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.screens.workbench.backend.BaseAppSetup;
import org.uberfire.commons.services.cdi.ApplicationStarted;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.commons.services.cdi.StartupType;
import org.uberfire.io.IOService;

@Startup(StartupType.BOOTSTRAP)
@ApplicationScoped
public class AppSetup extends BaseAppSetup {

    private WorkspaceProjectService projectService;

    private Event<ApplicationStarted> applicationStartedEvent;

    protected AppSetup() {

    }

    @Inject
    public AppSetup(@Named("ioStrategy") IOService ioService,
                    RepositoryService repositoryService,
                    OrganizationalUnitService organizationalUnitService,
                    KieModuleService moduleService,
                    ConfigurationService configurationService,
                    ConfigurationFactory configurationFactory,
                    WorkspaceProjectService projectService,
                    Event<ApplicationStarted> applicationStartedEvent) {
        super(ioService,
              repositoryService,
              organizationalUnitService,
              moduleService,
              configurationService,
              configurationFactory);
        this.projectService = projectService;
        this.applicationStartedEvent = applicationStartedEvent;

        OrganizationalUnit ou = organizationalUnitService.getOrganizationalUnit("Stunner");
        if (ou == null) {
            ou = organizationalUnitService.createOrganizationalUnit("StunnerOU",
                                                                    "stunner",
                                                                    "stunner-showcase");
        }

        Collection<WorkspaceProject> projects = projectService.getAllWorkspaceProjectsByName(ou,
                                                                                             "StunnerShowcase");
        if (projects.isEmpty()) {
            projectService.newProject(ou,
                                      new POM("StunnerShowcase",
                                              "Stunner showcase project",
                                              new GAV("stunner-showcase",
                                                      "stunner-showcase",
                                                      "1.0")));
        }
        applicationStartedEvent.fire(new ApplicationStarted());
    }
}
