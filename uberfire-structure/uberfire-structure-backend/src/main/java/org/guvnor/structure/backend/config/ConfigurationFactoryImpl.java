/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.guvnor.structure.backend.config;

import java.util.List;
import javax.inject.Inject;

import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigItem;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.config.PasswordService;
import org.guvnor.structure.server.config.SecureConfigItem;

import static org.guvnor.structure.repositories.EnvironmentParameters.CRYPT_PREFIX;

public class ConfigurationFactoryImpl implements ConfigurationFactory {

    protected PasswordService secureService;

    public ConfigurationFactoryImpl() {
    }

    @Inject
    public ConfigurationFactoryImpl(final PasswordService secureService) {
        this.secureService = secureService;
    }

    @Override
    public ConfigGroup newConfigGroup(final ConfigType type,
                                      final String name,
                                      final String description) {
        if (type.hasNamespace()) {
            throw new RuntimeException("The ConfigType " + type.toString() + " requires a namespace.");
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
        if (!type.hasNamespace() && namespace != null && !namespace.isEmpty()) {
            throw new RuntimeException("The ConfigType " + type.toString() + " does not support namespaces.");
        }

        final ConfigGroup configGroup = new ConfigGroup();
        configGroup.setDescription(description);
        configGroup.setName(name);
        configGroup.setType(type);
        configGroup.setNamespace(namespace);
        configGroup.setEnabled(true);
        return configGroup;
    }

    @Override
    public ConfigItem<String> newConfigItem(final String name,
                                            final String valueType) {
        final ConfigItem<String> stringConfigItem = new ConfigItem<String>();
        stringConfigItem.setName(name);
        stringConfigItem.setValue(valueType);
        return stringConfigItem;
    }

    @Override
    public ConfigItem<Boolean> newConfigItem(final String name,
                                             final boolean valueType) {
        final ConfigItem<Boolean> booleanConfigItem = new ConfigItem<Boolean>();
        booleanConfigItem.setName(name);
        booleanConfigItem.setValue(valueType);
        return booleanConfigItem;
    }

    @Override
    public SecureConfigItem newSecuredConfigItem(final String name,
                                                 final String valueType) {
        final SecureConfigItem stringConfigItem = new SecureConfigItem();
        if (name.startsWith(CRYPT_PREFIX)) {
            stringConfigItem.setName(name.substring(CRYPT_PREFIX.length()));
        } else {
            stringConfigItem.setName(name);
        }
        stringConfigItem.setValue(secureService.encrypt(valueType));
        return stringConfigItem;
    }

    @Override
    public ConfigItem<List> newConfigItem(String name,
                                          List valueType) {
        final ConfigItem<List> listConfigItem = new ConfigItem<List>();
        listConfigItem.setName(name);
        listConfigItem.setValue(valueType);
        return listConfigItem;
    }

    @Override
    public ConfigItem<Object> newConfigItem(String name,
                                            Object valueType) {
        final ConfigItem<Object> listConfigItem = new ConfigItem<Object>();
        listConfigItem.setName(name);
        listConfigItem.setValue(valueType);
        return listConfigItem;
    }
}
