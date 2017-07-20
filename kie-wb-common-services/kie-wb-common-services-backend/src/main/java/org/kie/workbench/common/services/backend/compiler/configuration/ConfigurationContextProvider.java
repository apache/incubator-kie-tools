/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.kie.workbench.common.services.backend.compiler.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * THis implementation first try to load configuration keys from environment variables then load properties with a files called IncrementalCompiler.properties then an hard coded configuration like the following example
 *
 * MAVEN_PLUGINS =org.apache.maven.plugins
 * MAVEN_COMPILER_PLUGIN =maven-compiler-plugin
 * MAVEN_COMPILER_PLUGIN_VERSION=3.6.1
 *
 * ALTERNATIVE_COMPILER_PLUGINS =io.takari.maven.plugins
 * ALTERNATIVE_COMPILER_PLUGIN =takari-lifecycle-plugin
 * ALTERNATIVE_COMPILER_PLUGIN_VERSION =1.12.6
 *
 * KIE_MAVEN_PLUGINS=org.kie
 * KIE_MAVEN_PLUGIN=kie-takari-plugin
 *
 */
public class ConfigurationContextProvider implements ConfigurationProvider {

    private Map<ConfigurationKey, String> conf;

    public ConfigurationContextProvider() {
        initializeWorkingConfig();
    }

    private void initializeWorkingConfig() {
        List<ConfigurationStrategy> confs = new ArrayList<ConfigurationStrategy>(Arrays.asList(new ConfigurationEnvironmentStrategy(),
                                                                                               new ConfigurationPropertiesStrategy(),
                                                                                               new ConfigurationStaticStrategy()));
        Collections.sort(confs,
                         (ConfigurationStrategy one, ConfigurationStrategy two) -> one.getOrder().compareTo(two.getOrder()));
        for (ConfigurationStrategy item : confs) {
            if (item.isValid()) {
                conf = item.loadConfiguration();
                break;
            }
        }
    }

    @Override
    public Map<ConfigurationKey, String> loadConfiguration() {
        return conf;
    }
}
