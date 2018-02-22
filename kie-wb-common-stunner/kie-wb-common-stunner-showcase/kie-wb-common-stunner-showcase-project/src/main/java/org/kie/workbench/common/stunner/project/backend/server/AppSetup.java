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

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.inject.Named;

import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigItem;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.config.ConfigurationService;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.screens.workbench.backend.BaseAppSetup;
import org.uberfire.commons.services.cdi.ApplicationStarted;
import org.uberfire.commons.services.cdi.Startup;
import org.uberfire.commons.services.cdi.StartupType;
import org.uberfire.io.IOService;

//This is a temporary solution when running in PROD-MODE as /webapp/.niogit/system.git folder
//is not deployed to the Application Servers /bin folder. This will be remedied when an
//installer is written to create the system.git repository in the correct location.
@Startup(StartupType.BOOTSTRAP)
@ApplicationScoped
public class AppSetup extends BaseAppSetup {

    // default groups
    private static final String DROOLS_WB_ORGANIZATIONAL_UNIT1 = "demo";
    private static final String DROOLS_WB_ORGANIZATIONAL_UNIT1_OWNER = "demo@drools.org";
    // default repository section - end

    @Inject
    private Event<ApplicationStarted> applicationStartedEvent;

    protected AppSetup() {
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
    public void assertPlayground() {
        try {
            configurationService.startBatch();

            // Setup mandatory properties for Drools-Workbench
            final ConfigItem<String> supportRuntimeDeployConfigItem = new ConfigItem<>();
            supportRuntimeDeployConfigItem.setName("support.runtime.deploy");
            supportRuntimeDeployConfigItem.setValue("false");
            setupConfigurationGroup(ConfigType.GLOBAL,
                                    GLOBAL_SETTINGS,
                                    getGlobalConfiguration(),
                                    supportRuntimeDeployConfigItem);

            // notify components that bootstrap is completed to start post setups
            applicationStartedEvent.fire(new ApplicationStarted());
        } catch (final Exception e) {
            logger.error("Error during update config", e);
            throw new RuntimeException(e);
        } finally {
            configurationService.endBatch();
        }
    }

    private ConfigGroup getGlobalConfiguration() {
        //Global Configurations used by many of Drools Workbench editors
        final ConfigGroup group = configurationFactory.newConfigGroup(ConfigType.GLOBAL,
                                                                      GLOBAL_SETTINGS,
                                                                      "");
        group.addConfigItem(configurationFactory.newConfigItem("drools.dateformat",
                                                               "dd-MMM-yyyy"));
        group.addConfigItem(configurationFactory.newConfigItem("drools.datetimeformat",
                                                               "dd-MMM-yyyy hh:mm:ss"));
        group.addConfigItem(configurationFactory.newConfigItem("drools.defaultlanguage",
                                                               "en"));
        group.addConfigItem(configurationFactory.newConfigItem("drools.defaultcountry",
                                                               "US"));
        group.addConfigItem(configurationFactory.newConfigItem("build.enable-incremental",
                                                               "true"));
        group.addConfigItem(configurationFactory.newConfigItem("rule-modeller-onlyShowDSLStatements",
                                                               "false"));
        return group;
    }
}
