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

package org.guvnor.structure.backend.backcompat;

import java.util.ArrayList;
import java.util.List;

import org.guvnor.structure.backend.config.ConfigurationFactoryImpl;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.junit.Test;

import static org.junit.Assert.*;

public class BackwardCompatibleUtilTest {

    @Test
    public void backwardCompatibilityNullTest() {
        final ConfigurationFactory factory = new ConfigurationFactoryImpl();
        final BackwardCompatibleUtil backwardUtil = new BackwardCompatibleUtil(factory);

        assertNull(backwardUtil.compat(null));
    }

    @Test
    public void backwardCompatibilityNullSecurityRolesTest() {
        final ConfigurationFactory factory = new ConfigurationFactoryImpl();
        final BackwardCompatibleUtil backwardUtil = new BackwardCompatibleUtil(factory);

        final ConfigGroup group = factory.newConfigGroup(ConfigType.PROJECT,
                                                         "cool",
                                                         "test");
        assertNotNull(backwardUtil.compat(group));
        assertNotNull(backwardUtil.compat(group).getConfigItem("security:groups"));
    }

    @Test
    public void backwardCompatibilityExistingSecurityRolesTest() {
        final ConfigurationFactory factory = new ConfigurationFactoryImpl();
        final BackwardCompatibleUtil backwardUtil = new BackwardCompatibleUtil(factory);

        final ConfigGroup group = factory.newConfigGroup(ConfigType.PROJECT,
                                                         "cool2",
                                                         "test2");

        group.addConfigItem(factory.newConfigItem("security:roles",
                                                  new ArrayList() {{
                                                      add("group1");
                                                  }}));
        assertNotNull(backwardUtil.compat(group).getConfigItem("security:groups"));
        assertEquals(1,
                     ((List<String>) (backwardUtil.compat(group).getConfigItem("security:groups")).getValue()).size());
        assertNull(backwardUtil.compat(group).getConfigItem("security:roles"));
    }

    @Test
    public void backwardCompatibilityEmptySecurityRolesTest() {
        final ConfigurationFactory factory = new ConfigurationFactoryImpl();
        final BackwardCompatibleUtil backwardUtil = new BackwardCompatibleUtil(factory);

        final ConfigGroup group = factory.newConfigGroup(ConfigType.PROJECT,
                                                         "cool3",
                                                         "test3");

        group.addConfigItem(factory.newConfigItem("security:roles",
                                                  new ArrayList()));
        assertNotNull(backwardUtil.compat(group).getConfigItem("security:groups"));
        assertEquals(0,
                     ((List<String>) (backwardUtil.compat(group).getConfigItem("security:groups")).getValue()).size());
        assertNull(backwardUtil.compat(group).getConfigItem("security:roles"));
    }
}
