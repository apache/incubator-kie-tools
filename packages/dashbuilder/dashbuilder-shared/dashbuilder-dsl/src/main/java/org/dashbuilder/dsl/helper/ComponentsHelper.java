/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.dashbuilder.dsl.helper;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.dashbuilder.dsl.model.Dashboard;
import org.dashbuilder.dsl.model.Page;
import org.dashbuilder.external.model.ExternalComponent;
import org.dashbuilder.external.service.ComponentLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.ext.layout.editor.api.editor.LayoutComponent;
import org.uberfire.ext.layout.editor.api.editor.LayoutRow;
import org.uberfire.ext.layout.editor.api.editor.LayoutTemplate;

public class ComponentsHelper {

    private static final Logger logger = LoggerFactory.getLogger(ComponentsHelper.class);

    private static final Gson gson = new GsonBuilder().create();

    private ComponentsHelper() {
        // empty
    }

    public static List<String> listComponentsIds(Dashboard dashboard) {
        Optional<Path> componentsPath = dashboard.getComponentsPath();
        if (!componentsPath.isPresent()) {
            return Collections.emptyList();
        }
        return ComponentsHelper.listComponents(componentsPath.get())
                               .stream()
                               .map(ExternalComponent::getId)
                               .collect(Collectors.toList());
    }

    public static List<ExternalComponent> listComponents(Path componentsPath) {
        try (Stream<Path> walker = Files.walk(componentsPath, 1)) {
            return walker.filter(p -> p.toFile().isDirectory())
                         .map(p -> Paths.get(p.toString(), ComponentLoader.DESCRIPTOR_FILE))
                         .filter(f -> f.toFile().exists())
                         .map(ComponentsHelper::readComponent)
                         .filter(Objects::nonNull)
                         .collect(Collectors.toList());

        } catch (IOException e) {
            logger.debug("Error loading external components.", e);
            throw new RuntimeException("Error loading components from " + componentsPath + ". Error: " + e.getMessage(), e);
        }
    }

    public static List<String> listPagesComponents(List<Page> pages) {
        return pages.stream()
                    .map(Page::getLayoutTemplate)
                    .map(LayoutTemplate::getRows)
                    .flatMap(ComponentsHelper::allLayoutComponentsStream)
                    .map(lt -> lt.getProperties().get(ExternalComponent.COMPONENT_ID_KEY))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
    }

    public static Stream<String> collectingPropertyValue(Page page, String propertyId) {
        return allLayoutComponentsStream(page.getLayoutTemplate().getRows()).map(lc -> lc.getProperties().get(propertyId))
                                                                            .filter(Objects::nonNull);
    }

    private static Stream<LayoutComponent> allLayoutComponentsStream(List<LayoutRow> row) {
        return row.stream()
                  .flatMap(r -> r.getLayoutColumns().stream())
                  .flatMap(cl -> Stream.concat(cl.getLayoutComponents().stream(),
                                               allLayoutComponentsStream(cl.getRows())));
    }

    private static ExternalComponent readComponent(Path file) {
        String id = file.getParent().toFile().getName();
        try {
            ExternalComponent component = gson.fromJson(new FileReader(file.toFile()),
                                                        ExternalComponent.class);
            component.setId(id);
            return component;
        } catch (FileNotFoundException e) {
            logger.error("Not able to read component manifest file {}. Error: {}", file, e.getMessage());
            logger.debug("Error reading component file.", e);
            return null;
        }
    }

}