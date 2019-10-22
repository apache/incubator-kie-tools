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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.errai.bus.server.annotations.Service;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.DiagramImpl;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.service.DiagramService;
import org.kie.workbench.common.stunner.project.diagram.ProjectDiagram;
import org.kie.workbench.common.stunner.project.diagram.ProjectMetadata;
import org.kie.workbench.common.stunner.project.diagram.impl.ProjectDiagramImpl;
import org.kie.workbench.common.stunner.project.service.ProjectDiagramService;
import org.uberfire.backend.vfs.Path;

@ApplicationScoped
@Service
public class DelegateDiagramService implements DiagramService {

    private final ProjectDiagramService projectDiagramService;

    @Inject
    public DelegateDiagramService(final ProjectDiagramService projectDiagramService) {
        this.projectDiagramService = projectDiagramService;
    }

    private Diagram convert(final ProjectDiagram projectDiagram) {
        final DiagramImpl diagram = new DiagramImpl(projectDiagram.getName(), projectDiagram.getMetadata());
        diagram.setGraph(projectDiagram.getGraph());
        return diagram;
    }

    @SuppressWarnings("unchecked")
    private ProjectDiagram convert(final Diagram<Graph, Metadata> diagram) {
        if (!ProjectMetadata.class.isInstance(diagram.getMetadata())) {
            throw new IllegalStateException("The Metadata is supposed to be a ProjectMetadata for diagram " + diagram.getName());
        }
        return new ProjectDiagramImpl(diagram.getName(), diagram.getGraph(), ProjectMetadata.class.cast(diagram.getMetadata()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public Diagram<Graph, Metadata> getDiagramByPath(final Path path) {
        return convert(projectDiagramService.getDiagramByPath(path));
    }

    @Override
    public boolean accepts(final Path path) {
        return projectDiagramService.accepts(path);
    }

    @Override
    public Path create(final Path path,
                       final String name,
                       final String defSetId) {
        return projectDiagramService.create(path, name, defSetId);
    }

    @Override
    public Metadata saveOrUpdate(final Diagram<Graph, Metadata> diagram) {
        return projectDiagramService.saveOrUpdate(convert(diagram));
    }

    @Override
    public boolean delete(final Diagram<Graph, Metadata> diagram) {
        return projectDiagramService.delete(convert(diagram));
    }

    @Override
    public String getRawContent(final Diagram<Graph, Metadata> diagram) {
        return projectDiagramService.getRawContent(convert(diagram));
    }

    @Override
    public Path saveOrUpdateSvg(final Path diagramPath,
                                final String rawDiagramSvg) {
        return projectDiagramService.saveOrUpdateSvg(diagramPath, rawDiagramSvg);
    }
}