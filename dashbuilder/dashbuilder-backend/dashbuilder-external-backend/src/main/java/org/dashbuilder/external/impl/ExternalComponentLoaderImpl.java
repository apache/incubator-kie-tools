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

package org.dashbuilder.external.impl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

import com.google.gson.Gson;
import org.dashbuilder.external.model.ExternalComponent;
import org.dashbuilder.external.service.ExternalComponentLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class ExternalComponentLoaderImpl implements ExternalComponentLoader {

    Logger logger = LoggerFactory.getLogger(ExternalComponentLoaderImpl.class);

    public static final String EXTERNAL_COMP_DIR_PROP = "dashbuilder.components.dir";
    public static final String EXTERNAL_COMP_ENABLE_PROP = "dashbuilder.components.enable";
    public static final String DESCRIPTOR_FILE = "manifest.json";

    private static final String DEFAULT_COMPONENTS_PATH = "/tmp/dashbuilder/components/";

    private String externalComponentsDir;

    private Gson gson;

    private boolean isExternalComponentEnabled;

    @PostConstruct
    public void init() {
        gson = new Gson();
        isExternalComponentEnabled = Boolean.parseBoolean(System.getProperty(EXTERNAL_COMP_ENABLE_PROP, Boolean.FALSE.toString()));
        externalComponentsDir = System.getProperty(EXTERNAL_COMP_DIR_PROP, DEFAULT_COMPONENTS_PATH);
        if (isExternalComponentEnabled) {
            Path baseDirPath = Paths.get(externalComponentsDir);
            if (!baseDirPath.toFile().exists()) {
                baseDirPath.toFile().mkdirs();
            }
        }
    }

    @Override
    public List<ExternalComponent> load() {
        if (!isExternalComponentEnabled) {
            return Collections.emptyList();
        }

        try (Stream<Path> walker = Files.walk(Paths.get(externalComponentsDir), 1)) {
            return walker.filter(p -> p.toFile().isDirectory())
                         .map(this::getComponentDescriptor)
                         .filter(File::exists)
                         .map(this::readComponent)
                         .filter(Objects::nonNull)
                         .collect(Collectors.toList());

        } catch (IOException e) {
            logger.error("Error loading components from {}. Error: {}", externalComponentsDir, e.getMessage());
            logger.debug("Error loading external components.", e);
        }
        return Collections.emptyList();
    }

    @Override
    public String getExternalComponentsDir() {
        return externalComponentsDir;
    }

    private ExternalComponent readComponent(File componentDescriptor) {
        try {
            String componentId = componentDescriptor.getParentFile().getName();
            ExternalComponent component = gson.fromJson(new FileReader(componentDescriptor), ExternalComponent.class);
            component.setId(componentId);
            return component;
        } catch (Exception e) {
            logger.error("Not able to load component {}. Error: {}", componentDescriptor, e.getMessage());
            logger.debug("Error reading component.", e);
        }
        return null;
    }

    private File getComponentDescriptor(Path p) {
        return Paths.get(p.toString(), DESCRIPTOR_FILE).toFile();
    }

    public boolean isEnabled() {
        return isExternalComponentEnabled;
    }

}