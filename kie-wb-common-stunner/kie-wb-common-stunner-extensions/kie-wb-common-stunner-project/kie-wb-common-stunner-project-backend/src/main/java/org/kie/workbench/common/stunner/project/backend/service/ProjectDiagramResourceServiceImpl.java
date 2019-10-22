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

package org.kie.workbench.common.stunner.project.backend.service;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.common.services.shared.metadata.model.Metadata;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.stunner.kogito.api.editor.DiagramType;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.diagram.editor.ProjectDiagramResource;
import org.kie.workbench.common.stunner.project.service.ProjectDiagramResourceService;
import org.kie.workbench.common.stunner.project.service.ProjectDiagramService;
import org.uberfire.backend.vfs.Path;
import org.uberfire.ext.editor.commons.backend.service.SaveAndRenameServiceImpl;
import org.uberfire.ext.editor.commons.service.RenameService;

@Service
@ApplicationScoped
public class ProjectDiagramResourceServiceImpl implements ProjectDiagramResourceService {

    private final ProjectDiagramService projectDiagramService;

    private final RenameService renameService;

    private final SaveAndRenameServiceImpl<ProjectDiagramResource, Metadata> saveAndRenameService;

    public ProjectDiagramResourceServiceImpl() {
        this(null, null, null);
    }

    @Inject
    public ProjectDiagramResourceServiceImpl(final ProjectDiagramService projectDiagramService,
                                             final RenameService renameService,
                                             final SaveAndRenameServiceImpl<ProjectDiagramResource, Metadata> saveAndRenameService) {
        this.projectDiagramService = projectDiagramService;
        this.renameService = renameService;
        this.saveAndRenameService = saveAndRenameService;
    }

    @PostConstruct
    public void init() {
        saveAndRenameService.init(this);
    }

    @Override
    public Path save(final Path path,
                     final ProjectDiagramResource resource,
                     final Metadata metadata,
                     final String comment) {

        final DiagramType type = resource.getType();
        final Map<DiagramType, Function<ProjectDiagramResource, Path>> saveOperations = new HashMap<>();

        saveOperations.put(DiagramType.PROJECT_DIAGRAM, (r) -> projectDiagramService.save(path, getProjectDiagram(r), metadata, comment));
        saveOperations.put(DiagramType.XML_DIAGRAM, (r) -> projectDiagramService.saveAsXml(path, getXmlDiagram(r), metadata, comment));

        return saveOperations.get(type).apply(resource);
    }

    private ProjectDiagram getProjectDiagram(final ProjectDiagramResource resource) {
        final String message = "A ProjectDiagramResource with type = " + resource.getType() + " must have a valid projectDiagram.";
        return resource.projectDiagram().orElseThrow(() -> new IllegalStateException(message));
    }

    private String getXmlDiagram(final ProjectDiagramResource resource) {
        final String message = "A ProjectDiagramResource with type = " + resource.getType() + " must have a valid xmlDiagram.";
        return resource.xmlDiagram().orElseThrow(() -> new IllegalStateException(message));
    }

    @Override
    public Path rename(final Path path,
                       final String newName,
                       final String comment) {
        return renameService.rename(path, newName, comment);
    }

    @Override
    public Path saveAndRename(final Path path,
                              final String newFileName,
                              final Metadata metadata,
                              final ProjectDiagramResource resource,
                              final String comment) {
        return saveAndRenameService.saveAndRename(path, newFileName, metadata, resource, comment);
    }
}
