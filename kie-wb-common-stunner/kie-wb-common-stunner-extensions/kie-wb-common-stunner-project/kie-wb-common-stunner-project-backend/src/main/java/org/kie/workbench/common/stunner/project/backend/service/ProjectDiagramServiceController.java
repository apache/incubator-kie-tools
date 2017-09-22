/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.project.backend.service;

import java.io.InputStream;
import java.util.Map;

import javax.enterprise.inject.Instance;

import org.kie.workbench.common.stunner.core.api.DefinitionManager;
import org.kie.workbench.common.stunner.core.api.FactoryManager;
import org.kie.workbench.common.stunner.core.backend.service.AbstractVFSDiagramService;
import org.kie.workbench.common.stunner.core.definition.service.DefinitionSetService;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.registry.BackendRegistryFactory;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.kie.workbench.common.stunner.project.diagram.impl.ProjectMetadataImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.uberfire.backend.server.util.Paths;
import org.uberfire.backend.vfs.Path;
import org.uberfire.io.IOService;
import org.uberfire.java.nio.file.OpenOption;
import org.uberfire.java.nio.file.StandardDeleteOption;

class ProjectDiagramServiceController extends AbstractVFSDiagramService<ProjectMetadata, ProjectDiagram> {

    private static final Logger LOG =
            LoggerFactory.getLogger(ProjectDiagramServiceController.class.getName());

    ProjectDiagramServiceController(final DefinitionManager definitionManager,
                                    final FactoryManager factoryManager,
                                    final Instance<DefinitionSetService> definitionSetServiceInstances,
                                    final IOService ioService,
                                    final BackendRegistryFactory registryFactory) {
        super(definitionManager,
              factoryManager,
              definitionSetServiceInstances,
              ioService,
              registryFactory);
    }

    @Override
    protected void initialize() {
        // Initialize caches.
        super.initialize();
    }

    @Override
    protected Class<? extends Metadata> getMetadataType() {
        return ProjectMetadata.class;
    }

    @Override
    public Path create(final Path path,
                       final String name,
                       final String defSetId) {
        return create(path,
                      name,
                      defSetId,
                      null,
                      null);
    }

    public Path create(final Path path,
                       final String name,
                       final String defSetId,
                       final String projName,
                       final String projPkg) {
        ProjectMetadata metadata = buildProjectMetadataInstance(path,
                                                                name,
                                                                defSetId,
                                                                projName,
                                                                projPkg);
        return this.create(path,
                           name,
                           defSetId,
                           metadata);
    }

    // TODO: Jeremy - set project name and package values when loading a diagram.
    @Override
    protected ProjectMetadata buildMetadataInstance(final org.uberfire.backend.vfs.Path path,
                                                    final String defSetId,
                                                    final String title) {
        return buildProjectMetadataInstance(path,
                                            title,
                                            defSetId,
                                            null,
                                            null);
    }

    private ProjectMetadata buildProjectMetadataInstance(final Path path,
                                                         final String name,
                                                         final String defSetId,
                                                         final String projName,
                                                         final String projPkg) {
        return new ProjectMetadataImpl.ProjectMetadataBuilder()
                .forDefinitionSetId(defSetId)
                .forProjectName(projName)
                .forProjectPackage(projPkg)
                .forTitle(name)
                .forPath(path)
                .build();
    }

    @Override
    protected InputStream loadMetadataForPath(final Path path) {
        return null;
    }

    public Path save(final Path path,
                     final ProjectDiagram diagram,
                     final Map<String, ?> attributes,
                     final OpenOption... comment) {
        try {
            String[] raw = serizalize(diagram);
            getIoService().write(Paths.convert(path),
                                 raw[0],
                                 attributes,
                                 comment);
        } catch (Exception e) {
            LOG.error("Error while saving diagram with UUID [" + diagram.getName() + "].",
                      e);
            throw new RuntimeException(e);
        }
        return path;
    }

    @Override
    protected ProjectMetadata doSave(final ProjectDiagram diagram,
                                     final String raw,
                                     final String metadata) {
        try {
            Path _path = diagram.getMetadata().getPath();
            org.uberfire.java.nio.file.Path path = Paths.convert(_path);
            if (!getIoService().exists(path)) {
                final org.uberfire.java.nio.file.Path parent = path.getParent();
                final String fileName = path.getFileName().toString();
                path = parent.resolve(fileName);
            }
            getIoService().write(path,
                                 raw);
        } catch (Exception e) {
            LOG.error("Error while saving diagram with UUID [" + diagram.getName() + "].",
                      e);
            throw new RuntimeException(e);
        }
        return diagram.getMetadata();
    }

    // TODO: Use commit message.
    public boolean delete(final Path _path,
                          final String message) {
        final org.uberfire.java.nio.file.Path path = Paths.convert(_path);
        return getIoService().deleteIfExists(path,
                                             StandardDeleteOption.NON_EMPTY_DIRECTORIES);
    }

    @Override
    protected boolean doDelete(final Path path) {
        return delete(path,
                      "");
    }
}
