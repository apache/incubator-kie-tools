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

import org.guvnor.common.services.project.backend.server.utils.configuration.ConfigurationKey;
import org.guvnor.common.services.project.backend.server.utils.configuration.ConfigurationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.java.nio.file.Files;
import org.uberfire.java.nio.file.Path;

/**
 * Strategy implementation to create the Configuration from properties file
 */
public class ConfigurationPropertiesStrategy implements ConfigurationStrategy {

    private static final Logger logger = LoggerFactory.getLogger(ConfigurationPropertiesStrategy.class);
    private static final String PROPERTIES_FILE = "IncrementalCompiler.properties";
    protected Map<ConfigurationKey, String> conf;
    private Boolean valid = Boolean.FALSE;

    public ConfigurationPropertiesStrategy() {
        loadProperties(PROPERTIES_FILE);
    }

    /**
     * Useful if an alternative properties file is needed
     */
    public ConfigurationPropertiesStrategy(Path propertiesFilePath) {
        loadProperties(propertiesFilePath);
    }

    @Override
    public Map<ConfigurationKey, String> loadConfiguration() {
        return Collections.unmodifiableMap(conf);
    }

    @Override
    public Boolean isValid() {
        return valid && (conf.size() == ConfigurationKey.values().length);
    }

    @Override
    public Integer getOrder() {
        return Integer.valueOf(100);
    }

    private void setUpValues(Properties props) {
        conf = new HashMap<>();

        for (ConfigurationKey key : ConfigurationKey.values()) {
            String value = props.getProperty(key.name());
            if (value == null) {
                logger.info("Key {} is not available with the classloader properties, skip to the next ConfigurationStrategy. \n",
                            key.name());
                valid = Boolean.FALSE;
                break;
            } else {
                conf.put(key, value);
            }
        }
    }

    private Properties loadProperties(String propName) {
        Properties prop = new Properties();
        InputStream in = getClass().getClassLoader().getResourceAsStream(propName);
        if (in == null) {
            logger.info("{} not available with the classloader, skip to the next ConfigurationStrategy. \n", propName);
            valid = Boolean.FALSE;
        } else {
            try {
                prop.load(in);
                valid = Boolean.TRUE;
                setUpValues(prop);
            } catch (IOException e) {
                logger.error(e.getMessage());
                valid = Boolean.FALSE;
            } finally {
                try{
                    in.close();
                }catch (Exception e){
                    logger.error(e.getMessage());
                }
            }
        }
        return prop;
    }

    private Properties loadProperties(Path propertiesPath) {
        Properties prop = new Properties();
        try (InputStream propFileInpStream = Files.newInputStream(propertiesPath)) {
            if (propFileInpStream == null) {
                logger.info("{} not available, skip to the next ConfigurationStrategy. \n", propertiesPath.toString());
                valid = Boolean.FALSE;
            } else {
                prop.load(propFileInpStream);
                propFileInpStream.close();
                valid = Boolean.TRUE;
                setUpValues(prop);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return prop;
    }
}
