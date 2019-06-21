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

import javax.inject.Inject;

import org.guvnor.structure.backend.config.ConfigurationFactoryImpl;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.config.PasswordService;

@Migration
public class MigrationConfigurationFactoryImpl extends ConfigurationFactoryImpl implements ConfigurationFactory {

    public MigrationConfigurationFactoryImpl() {
    }

    @Inject
    public MigrationConfigurationFactoryImpl(final PasswordService secureService) {
        super(secureService);
    }

    @Override
    public ConfigGroup newConfigGroup(ConfigType type,
                                      final String name,
                                      final String description) {
        if (ConfigType.SPACE.equals(type)) {
            type = ConfigType.ORGANIZATIONAL_UNIT;
        }

        final ConfigGroup configGroup = new ConfigGroup();
        configGroup.setDescription(description);
        configGroup.setName(name);
        configGroup.setType(type);
        configGroup.setEnabled(true);
        return configGroup;
    }

    @Override
    public ConfigGroup newConfigGroup(final ConfigType type,
                                      final String namespace,
                                      final String name,
                                      final String description) {
        return newConfigGroup(type,
                              name,
                              description);
    }
}
