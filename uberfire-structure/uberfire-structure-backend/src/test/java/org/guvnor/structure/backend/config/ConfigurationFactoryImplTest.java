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

package org.guvnor.structure.backend.config;

import java.util.ArrayList;
import java.util.List;

import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigItem;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.config.PasswordService;
import org.guvnor.structure.server.config.SecureConfigItem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ConfigurationFactoryImplTest {

    private PasswordService passwordService;

    private ConfigurationFactory configurationFactory;

    @Before
    public void setup() {
        passwordService = new DefaultPasswordServiceImpl();
        configurationFactory = new ConfigurationFactoryImpl(passwordService);
    }

    @Test
    public void newConfigGroupWithoutNamespaceTest() {
        final ConfigGroup configGroup = configurationFactory.newConfigGroup(ConfigType.GLOBAL,
                                                                            "my-config",
                                                                            "my-description");

        assertEquals(ConfigType.GLOBAL,
                     configGroup.getType());
        assertEquals("my-config",
                     configGroup.getName());
        assertEquals("my-description",
                     configGroup.getDescription());
        assertTrue(configGroup.isEnabled());
    }

    @Test(expected = RuntimeException.class)
    public void newConfigGroupWithoutNamespaceButTypeRequiresNamespaceTest() {
        configurationFactory.newConfigGroup(ConfigType.REPOSITORY,
                                            "my-config",
                                            "my-description");
    }

    @Test
    public void newConfigGroupWithNamespaceTest() {
        final ConfigGroup configGroup = configurationFactory.newConfigGroup(ConfigType.REPOSITORY,
                                                                            "my-namespace",
                                                                            "my-config",
                                                                            "my-description");

        assertEquals(ConfigType.REPOSITORY,
                     configGroup.getType());
        assertEquals("my-namespace",
                     configGroup.getNamespace());
        assertEquals("my-config",
                     configGroup.getName());
        assertEquals("my-description",
                     configGroup.getDescription());
        assertTrue(configGroup.isEnabled());
    }

    @Test(expected = RuntimeException.class)
    public void newConfigGroupWithNamespaceButTypeDoesNotSupportNamespacesTest() {
        configurationFactory.newConfigGroup(ConfigType.GLOBAL,
                                            "my-namespace",
                                            "my-config",
                                            "my-description");
    }

    @Test
    public void newStringConfigItemTest() {
        final ConfigItem<String> stringConfigItem = configurationFactory.newConfigItem("my-item",
                                                                                       "my-value");

        assertEquals("my-item",
                     stringConfigItem.getName());
        assertEquals("my-value",
                     stringConfigItem.getValue());
    }

    @Test
    public void newBooleanConfigItemTest() {
        final ConfigItem<Boolean> booleanConfigItem = configurationFactory.newConfigItem("my-item",
                                                                                         true);

        assertEquals("my-item",
                     booleanConfigItem.getName());
        assertTrue(booleanConfigItem.getValue());
    }

    @Test
    public void newSecuredConfigItemTest() {
        final SecureConfigItem securedConfigItem = configurationFactory.newSecuredConfigItem("my-item",
                                                                                             "my-password");

        assertEquals("my-item",
                     securedConfigItem.getName());
        assertEquals("my-password",
                     passwordService.decrypt(securedConfigItem.getValue()));
    }

    @Test
    public void newListConfigItemTest() {
        final List<String> values = new ArrayList<>();
        values.add("value1");
        values.add("value2");

        final ConfigItem<List> listConfigItem = configurationFactory.newConfigItem("my-item",
                                                                                   values);

        assertEquals("my-item",
                     listConfigItem.getName());
        assertEquals(2,
                     listConfigItem.getValue().size());
        assertEquals("value1",
                     listConfigItem.getValue().get(0));
        assertEquals("value2",
                     listConfigItem.getValue().get(1));
    }

    @Test
    public void newLongConfigItemTest() {
        final ConfigItem<Object> longConfigItem = configurationFactory.newConfigItem("my-item",
                                                                                     2L);

        assertEquals("my-item",
                     longConfigItem.getName());
        assertEquals(2L,
                     longConfigItem.getValue());
    }
}
