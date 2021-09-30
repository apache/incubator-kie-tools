/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.dashbuilder.components.internal;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Access internal component information and expose during runtime.
 *
 */
public class ProvidedComponentInfo {

    Logger logger = Logger.getLogger(ProvidedComponentInfo.class.getName());

    private static final String DASHBUILDER_COMPONENTS_PROPERTIES = "/dashbuilder-components.properties";
    private static final String DASHBUILDER_COMPONENTS_LIST_PROP = "dashbuilder.internal.components.list";
    private static final String DASHBUILDER_COMPONENTS_ROOT_PROP = "dashbuilder.internal.components.root";

    private static ProvidedComponentInfo instance;

    private List<String> internalComponentsList = Collections.emptyList();

    private String internalComponentsRootPath = null;

    ProvidedComponentInfo() {}

    public static ProvidedComponentInfo get() {
        if (instance == null) {
            instance = new ProvidedComponentInfo();
            instance.loadProperties(DASHBUILDER_COMPONENTS_PROPERTIES);
        }
        return instance;
    }

    void loadProperties(String resourcePath) {
        InputStream is = this.getClass().getResourceAsStream(resourcePath);
        if (is == null) {
            logger.warning("Not able to find internal components properties file.");
            return;
        }
        try {
            Properties properties = new Properties();
            properties.load(is);
            loadInternalComponentsList(properties);
            loadInternalComponentsRootDir(properties);
        } catch (IOException e) {
            logger.warning("Not able to load internal components properties file.");
        }

    }

    private void loadInternalComponentsRootDir(Properties properties) {
        internalComponentsRootPath = properties.getProperty(DASHBUILDER_COMPONENTS_ROOT_PROP);
        if (internalComponentsRootPath == null || internalComponentsRootPath.trim().isEmpty()) {
            logger.warning("Internal components root not configured.");
        }

    }

    private void loadInternalComponentsList(Properties properties) {
        String componentsListStr = properties.getProperty(DASHBUILDER_COMPONENTS_LIST_PROP);
        if (componentsListStr == null || componentsListStr.trim().isEmpty()) {
            logger.warning("Internal components list is empty");
        } else {
            this.internalComponentsList = Arrays.stream(componentsListStr.split("\\,")).collect(Collectors.toList());
            logger.log(Level.INFO, () -> "Registered internal dashbuilder components: " + internalComponentsList);
        }
    }

    public List<String> getInternalComponentsList() {
        return internalComponentsList;
    }

    public String getInternalComponentsRootPath() {
        return internalComponentsRootPath;
    }

}