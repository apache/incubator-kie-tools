/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.services.datamodel.backend.server.builder;

import java.util.List;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.guvnor.common.services.project.builder.service.BuildService;
import org.guvnor.structure.server.config.ConfigGroup;
import org.guvnor.structure.server.config.ConfigType;
import org.guvnor.structure.server.config.ConfigurationFactory;
import org.guvnor.structure.server.config.ConfigurationService;
import org.guvnor.test.WeldJUnitRunner;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.kie.workbench.common.services.backend.builder.core.LRUBuilderCache;
import org.kie.workbench.common.services.datamodel.backend.server.cache.LRUModuleDataModelOracleCache;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.java.nio.fs.file.SimpleFileSystemProvider;

@RunWith(WeldJUnitRunner.class)
public abstract class AbstractWeldBuilderIntegrationTest {

    protected static final String GLOBAL_SETTINGS = "settings";

    protected final SimpleFileSystemProvider fs = new SimpleFileSystemProvider();

    @Inject
    protected BeanManager beanManager;
    @Inject
    protected Paths paths;
    @Inject
    protected ConfigurationService configurationService;
    @Inject
    protected ConfigurationFactory configurationFactory;
    @Inject
    protected BuildService buildService;
    @Inject
    protected KieModuleService moduleService;
    @Inject
    protected LRUBuilderCache builderCache;
    @Inject
    protected LRUModuleDataModelOracleCache moduleDMOCache;

    @Before
    public void setUp() throws Exception {
        //Define mandatory properties
        List<ConfigGroup> globalConfigGroups = configurationService.getConfiguration(ConfigType.GLOBAL);
        boolean globalSettingsDefined = false;
        for (ConfigGroup globalConfigGroup : globalConfigGroups) {
            if (GLOBAL_SETTINGS.equals(globalConfigGroup.getName())) {
                globalSettingsDefined = true;
                break;
            }
        }
        if (!globalSettingsDefined) {
            configurationService.addConfiguration(getGlobalConfiguration());
        }
    }

    private ConfigGroup getGlobalConfiguration() {
        //Global Configurations used by many of Drools Workbench editors
        final ConfigGroup group = configurationFactory.newConfigGroup(ConfigType.GLOBAL,
                                                                      GLOBAL_SETTINGS,
                                                                      "");
        group.addConfigItem(configurationFactory.newConfigItem("build.enable-incremental",
                                                               "true"));
        return group;
    }
}
