/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.screens.workbench.backend;

import java.util.List;

import org.guvnor.structure.organizationalunit.OrganizationalUnitService;
import org.guvnor.structure.repositories.RepositoryService;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigItem;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.config.ConfigurationService;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.io.IOService;

public abstract class BaseAppSetup {

    protected static final Logger logger = LoggerFactory.getLogger(BaseAppSetup.class);

    protected static final String GLOBAL_SETTINGS = "settings";
    protected static final String GIT_SCHEME = "git";

    protected IOService ioService;

    protected RepositoryService repositoryService;

    protected OrganizationalUnitService organizationalUnitService;

    protected KieModuleService moduleService;

    protected ConfigurationService configurationService;

    protected ConfigurationFactory configurationFactory;

    protected BaseAppSetup() {
    }

    public BaseAppSetup(final IOService ioService,
                        final RepositoryService repositoryService,
                        final OrganizationalUnitService organizationalUnitService,
                        final KieModuleService moduleService,
                        final ConfigurationService configurationService,
                        final ConfigurationFactory configurationFactory) {
        this.ioService = ioService;
        this.repositoryService = repositoryService;
        this.organizationalUnitService = organizationalUnitService;
        this.moduleService = moduleService;
        this.configurationService = configurationService;
        this.configurationFactory = configurationFactory;
    }

    protected void setupConfigurationGroup(ConfigType configType,
                                           String configGroupName,
                                           ConfigGroup configGroup,
                                           ConfigItem... configItemsToSetManually) {
        List<ConfigGroup> existentConfigGroups = configurationService.getConfiguration(configType);
        boolean settingsDefined = false;

        for (ConfigGroup existentConfigGroup : existentConfigGroups) {
            if (configGroupName.equals(existentConfigGroup.getName())) {
                settingsDefined = true;

                if (configItemsToSetManually != null) {
                    for (ConfigItem configItem : configItemsToSetManually) {
                        ConfigItem existentConfigItem = existentConfigGroup.getConfigItem(configItem.getName());
                        if (existentConfigItem == null) {
                            existentConfigGroup.addConfigItem(configItem);
                            configurationService.updateConfiguration(existentConfigGroup);
                        } else if (!existentConfigItem.getValue().equals(configItem.getValue())) {
                            existentConfigItem.setValue(configItem.getValue());
                            configurationService.updateConfiguration(existentConfigGroup);
                        }
                    }
                }

                break;
            }
        }

        if (!settingsDefined) {
            configurationService.addConfiguration(configGroup);
        }
    }
}
