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
import java.util.Map;
import java.util.Properties;

import org.guvnor.common.services.project.backend.server.utils.configuration.ConfigurationKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationUtil {


    private static final Logger logger = LoggerFactory.getLogger(ConfigurationUtil.class);

    public final String KIE_VERSION_FILE = "IncrementalCompiler.properties";
    public final static String KIE_VERSION_KEY = "KIE_VERSION";

    public Properties loadKieVersionProperties() {
        Properties prop = new Properties();
        InputStream in = getClass().getClassLoader().getResourceAsStream(KIE_VERSION_FILE);
        if (in == null) {
            logger.info("{} not available with the classloader, unable to initialize the StaticConfigurationStrategy. \n",
                        KIE_VERSION_FILE);
        } else {
            try {
                prop.load(in);
                in.close();
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
        return prop;
    }

}
