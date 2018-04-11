/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.bpmn.backend.workitem;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.kie.workbench.common.stunner.core.backend.util.URLUtils;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Files;

import static org.uberfire.backend.server.util.Paths.convert;

@ApplicationScoped
public class WorkItemDefinitionResources {

    private static final Logger LOG = LoggerFactory.getLogger(WorkItemDefinitionResources.class.getName());

    public static final String EXTENSION_WID = ".wid";
    public static final String PATH_GLOBAL = "global/";
    public static final String PATH_RESOURCES = "src/main/resources";

    private final IOService ioService;

    // CDI proxy.
    protected WorkItemDefinitionResources() {
        this(null);
    }

    @Inject
    public WorkItemDefinitionResources(final @Named("ioStrategy") IOService ioService) {
        this.ioService = ioService;
    }

    public Collection<Path> resolveResources(final Metadata metadata) {
        return resolveResources(resolveResourcePath(metadata),
                                resolveGlobalPath(metadata),
                                resolveResourcesPath(metadata),
                                p -> p);
    }

    public Optional<Path> resolveResource(final Metadata metadata,
                                          final Path resource,
                                          final String fileName) {
        Collection<Path> paths = resolveResources(ioService.get(resource.toURI()),
                                                  resolveGlobalPath(metadata),
                                                  resolveResourcesPath(metadata),
                                                  p -> p.resolve(fileName));
        return paths.isEmpty() ?
                Optional.empty() :
                Optional.of(paths.iterator().next());
    }

    public Collection<Path> resolveResources(final org.uberfire.java.nio.file.Path resource,
                                             final org.uberfire.java.nio.file.Path global,
                                             final org.uberfire.java.nio.file.Path resources,
                                             final Function<org.uberfire.java.nio.file.Path, org.uberfire.java.nio.file.Path> mapper) {
        final org.uberfire.java.nio.file.Path resourcePath = Files.isDirectory(resource) ? resource : resource.getParent();
        return Stream.of(resourcePath,
                         global,
                         resources)
                .map(mapper)
                .filter(ioService::exists)
                .map(Paths::convert)
                .collect(Collectors.toList());
    }

    public String generateIconDataURI(final Metadata metadata,
                                      final Path wid,
                                      final String icon) {
        return resolveResource(metadata,
                               wid,
                               icon)
                .map(this::generateDataURI)
                .orElse(null);
    }

    private String generateDataURI(final Path iconPath) {
        try {
            return URLUtils.buildDataURIFromStream(iconPath.getFileName(),
                                                   ioService.newInputStream(convert(iconPath)));
        } catch (Exception e) {
            LOG.error("Error generating icon data uri for path [" + iconPath + "]", e);
            return null;
        }
    }

    public org.uberfire.java.nio.file.Path resolveResourcePath(final Metadata metadata) {
        return ioService.get(metadata.getPath().toURI());
    }

    protected org.uberfire.java.nio.file.Path resolveGlobalPathByRoot(final Path path) {
        return ioService.get(path.toURI()).resolve(PATH_GLOBAL);
    }

    public org.uberfire.java.nio.file.Path resolveGlobalPath(final Metadata metadata) {
        return resolveGlobalPathByRoot(metadata.getRoot());
    }

    public org.uberfire.java.nio.file.Path resolveResourcesPath(final Metadata metadata) {
        return ioService.get(metadata.getRoot().toURI()).resolve(PATH_RESOURCES);
    }

    public static boolean isWorkItemDefinition(final Path path) {
        return isWorkItemDefinition(path.getFileName());
    }

    public static boolean isWorkItemDefinition(final String fileName) {
        return fileName.toLowerCase().endsWith(EXTENSION_WID);
    }

    public static boolean isHidden(final Path path) {
        return isHidden(path.getFileName());
    }

    public static boolean isHidden(final String fileName) {
        return fileName.startsWith(".");
    }

    protected IOService getIoService() {
        return ioService;
    }
}
