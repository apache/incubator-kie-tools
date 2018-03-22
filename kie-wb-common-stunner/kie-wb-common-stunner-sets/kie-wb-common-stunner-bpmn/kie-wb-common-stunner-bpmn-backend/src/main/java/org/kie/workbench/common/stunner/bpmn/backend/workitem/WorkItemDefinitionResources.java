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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.function.Supplier;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.kie.workbench.common.stunner.core.backend.util.ImageDataUriGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.Path;

@ApplicationScoped
public class WorkItemDefinitionResources {

    private static final Logger LOG = LoggerFactory.getLogger(WorkItemDefinitionResources.class.getName());

    public static final String EXTENSION_WID = ".wid";
    public static final String PATH_GLOBAL = "global/";

    private final IOService ioService;
    private final Supplier<String> globalPathName;

    // CDI proxy.
    protected WorkItemDefinitionResources() {
        this(null);
    }

    @Inject
    public WorkItemDefinitionResources(final @Named("ioStrategy") IOService ioService) {
        this(ioService,
             () -> PATH_GLOBAL);
    }

    protected WorkItemDefinitionResources(final @Named("ioStrategy") IOService ioService,
                                          final Supplier<String> globalPathName) {
        this.ioService = ioService;
        this.globalPathName = globalPathName;
    }

    public static boolean isWorkItemDefinition(final org.uberfire.backend.vfs.Path path) {
        return isWorkItemDefinition(path.getFileName());
    }

    public static boolean isWorkItemDefinition(final String name) {
        return name.toLowerCase().endsWith(EXTENSION_WID);
    }

    public Path resolvePath(final org.uberfire.backend.vfs.Path path) {
        return Paths.convert(path);
    }

    public Path resolveSearchPath(final org.uberfire.backend.vfs.Path path) {
        return resolvePath(path);
    }

    public Collection<Path> resolveResourcesPaths(final org.uberfire.backend.vfs.Path path) throws URISyntaxException {
        final String uri = path.toURI();
        final Collection<Path> result = new LinkedHashSet<>();
        if (isWorkItemDefinition(path)) {
            result.add(ioService.get(uri).getParent());
        } else {
            result.add(ioService.get(uri));
        }
        ioService.getFileSystem(new URI(uri))
                .getRootDirectories()
                .forEach(root -> result.add(root.resolve(globalPathName.get())));
        return result;
    }

    public Optional<Path> resolveIconPath(final org.uberfire.backend.vfs.Path workItemDefinitionPath,
                                          final String icon) throws URISyntaxException {
        return resolveResourcesPaths(workItemDefinitionPath)
                .stream()
                .map(root -> root.resolve(icon))
                .filter(ioService::exists)
                .findFirst();
    }

    public String generateIconDataURI(final org.uberfire.backend.vfs.Path workItemDefinitionPath,
                                      final String icon) {
        try {
            return resolveIconPath(workItemDefinitionPath,
                                   icon)
                    .map(this::generateDataURI)
                    .orElse(null);
        } catch (URISyntaxException e) {
            LOG.error("Error parsing URI [" + workItemDefinitionPath + "]", e);
            return null;
        }
    }

    public String generateDataURI(final Path iconPath) {
        try {
            return ImageDataUriGenerator.buildDataURIFromStream(iconPath.toUri().toString(),
                                                                ioService.newInputStream(iconPath));
        } catch (Exception e) {
            LOG.error("Error generating icon data uri for path [" + iconPath + "]", e);
            return null;
        }
    }
}
