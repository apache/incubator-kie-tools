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
package org.kie.workbench.common.services.backend.compiler.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MavenBannedVars {

    private static final Logger logger = LoggerFactory.getLogger(MavenBannedVars.class);
    private static final String BANNED_PROPERTIES_FILE = "BannedEnvVars.properties";

    public static Properties getBannedProperties(){
        return loadProperties(BANNED_PROPERTIES_FILE);
    }

    private static Properties loadProperties(String propName) {
        Properties prop = new Properties();
        try (InputStream in = MavenBannedVars.class.getClassLoader().getResourceAsStream(propName)) {
            if (in == null) {
                logger.info("{} not available with the classloader no Banned EnvVars Found . \n", propName);
            } else {
                prop.load(in);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return prop;
    }

}
