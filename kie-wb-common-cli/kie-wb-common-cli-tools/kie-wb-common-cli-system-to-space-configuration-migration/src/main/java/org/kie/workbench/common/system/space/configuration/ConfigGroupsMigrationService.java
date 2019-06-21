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

package org.kie.workbench.common.system.space.configuration;

import java.util.Collection;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.structure.organizationalunit.config.SpaceConfigStorageRegistry;
import org.guvnor.structure.organizationalunit.config.SpaceInfo;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationService;
import org.kie.workbench.common.project.cli.util.ConfigGroupToSpaceInfoConverter;

@ApplicationScoped
public class ConfigGroupsMigrationService {

    private ConfigurationService configurationService;

    private SpaceConfigStorageRegistry spaceConfigStorageRegistry;

    private ConfigGroupToSpaceInfoConverter configGroupToSpaceInfoConverter;

    ConfigGroupsMigrationService() {
    }

    @Inject
    public ConfigGroupsMigrationService(final ConfigurationService configurationService, final SpaceConfigStorageRegistry spaceConfigStorageRegistry, final ConfigGroupToSpaceInfoConverter configGroupToSpaceInfoConverter) {
        this.configurationService = configurationService;
        this.spaceConfigStorageRegistry = spaceConfigStorageRegistry;
        this.configGroupToSpaceInfoConverter = configGroupToSpaceInfoConverter;
    }

    public void moveDataToSpaceConfigRepo() {
        Collection<ConfigGroup> groups = configurationService.getConfiguration(ConfigType.SPACE);
        if (groups != null) {
            for (ConfigGroup groupConfig : groups) {
                saveSpaceInfo(configGroupToSpaceInfoConverter.toSpaceInfo(groupConfig));
                configurationService.removeConfiguration(groupConfig);
            }
        }
    }

    void saveSpaceInfo(SpaceInfo spaceInfo) {
        spaceConfigStorageRegistry.get(spaceInfo.getName()).saveSpaceInfo(spaceInfo);
    }
}
