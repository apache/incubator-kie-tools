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

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.kie.workbench.common.services.backend.compiler.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationPropertiesStrategy implements ConfigurationStrategy,
                                                        Order {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationPropertiesStrategy.class);

    protected Map<ConfigurationKey, String> conf;

    private String propertiesFile = "IncrementalCompiler.properties";

    private Boolean valid = Boolean.FALSE;

    public ConfigurationPropertiesStrategy() {
        Properties props = loadProperties();
        if (isValid()) {
            conf = new HashMap<>();

            conf.put(ConfigurationKey.MAVEN_PLUGINS,
                     props.getProperty(ConfigurationKey.MAVEN_PLUGINS.name()));
            conf.put(ConfigurationKey.MAVEN_COMPILER_PLUGIN,
                     props.getProperty(ConfigurationKey.MAVEN_COMPILER_PLUGIN.name()));
            conf.put(ConfigurationKey.MAVEN_COMPILER_PLUGIN_VERSION,
                     props.getProperty(ConfigurationKey.MAVEN_COMPILER_PLUGIN_VERSION.name()));

            conf.put(ConfigurationKey.ALTERNATIVE_COMPILER_PLUGINS,
                     props.getProperty(ConfigurationKey.ALTERNATIVE_COMPILER_PLUGINS.name()));
            conf.put(ConfigurationKey.ALTERNATIVE_COMPILER_PLUGIN,
                     props.getProperty(ConfigurationKey.ALTERNATIVE_COMPILER_PLUGIN.name()));
            conf.put(ConfigurationKey.ALTERNATIVE_COMPILER_PLUGIN_VERSION,
                     props.getProperty(ConfigurationKey.ALTERNATIVE_COMPILER_PLUGIN_VERSION.name()));

            conf.put(ConfigurationKey.KIE_MAVEN_PLUGINS,
                     props.getProperty(ConfigurationKey.KIE_MAVEN_PLUGINS.name()));
            conf.put(ConfigurationKey.KIE_MAVEN_PLUGIN,
                     props.getProperty(ConfigurationKey.KIE_MAVEN_PLUGIN.name()));
        }
    }

    private Properties loadProperties() {
        Properties prop = new Properties();
        InputStream in = getClass().getClassLoader().getResourceAsStream(propertiesFile);
        if (in == null) {
            logger.info("{} not available with the classloader, skip to the next ConfigurationStrategy. \n",
                        propertiesFile);
            valid = Boolean.FALSE;
        } else {
            try {
                prop.load(in);
                in.close();
                valid = Boolean.TRUE;
            } catch (IOException e) {
                logger.error(e.getMessage());
                valid = Boolean.FALSE;
            }
        }
        return prop;
    }

    @Override
    public Map<ConfigurationKey, String> loadConfiguration() {
        return Collections.unmodifiableMap(conf);
    }

    @Override
    public Boolean isValid() {
        return valid;
    }

    @Override
    public Integer getOrder() {
        return Integer.valueOf(100);
    }
}
