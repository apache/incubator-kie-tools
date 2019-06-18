/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates. 
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

package org.kie.workbench.common.screens.server.management.backend.utils;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

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


    @Inject
    private Event<ApplicationStarted> applicationStartedEvent;

    public AppSetup() {
    }

    @Inject
    public AppSetup(@Named("ioStrategy") final IOService ioService,
                    final RepositoryService repositoryService,
                    final OrganizationalUnitService organizationalUnitService,
                    final KieModuleService moduleService,
                    final ConfigurationService configurationService,
                    final ConfigurationFactory configurationFactory,
                    final Event<ApplicationStarted> applicationStartedEvent) {
        super(ioService,
              repositoryService,
              organizationalUnitService,
              moduleService,
              configurationService,
              configurationFactory);
        this.applicationStartedEvent = applicationStartedEvent;
    }

    @PostConstruct
    public void init() {
        try {
            applicationStartedEvent.fire(new ApplicationStarted());
        } catch (final Exception e) {
            logger.error("Error during app start", e);
            throw new RuntimeException(e);
        }
    }

}
