/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.backend.editors.types;

import java.util.Optional;
import java.util.stream.StreamSupport;

import javax.inject.Inject;

import org.guvnor.common.services.project.model.Package;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNDiagram;
import org.kie.workbench.common.dmn.api.editors.types.DMNIncludeModel;
import org.kie.workbench.common.dmn.backend.editors.types.exceptions.DMNIncludeModelCouldNotBeCreatedException;
import org.kie.workbench.common.services.shared.project.KieModuleService;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.service.DiagramService;
import org.uberfire.backend.vfs.Path;

public class DMNIncludeModelFactory {

    private static final String DEFAULT_PACKAGE_NAME = "";

    private final DiagramService diagramService;

    private final KieModuleService moduleService;

    @Inject
    public DMNIncludeModelFactory(final DiagramService diagramService,
                                  final KieModuleService moduleService) {
        this.diagramService = diagramService;
        this.moduleService = moduleService;
    }

    public DMNIncludeModel create(final Path path) throws DMNIncludeModelCouldNotBeCreatedException {
        try {

            final String fileName = path.getFileName();
            final String modelPackage = getPackage(path);
            final String pathURI = path.toURI();
            final String namespace = getNamespace(path);

            return new DMNIncludeModel(fileName, modelPackage, pathURI, namespace);
        } catch (final Exception e) {
            throw new DMNIncludeModelCouldNotBeCreatedException();
        }
    }

    private String getPackage(final Path path) {
        return Optional
                .ofNullable(moduleService.resolvePackage(path))
                .map(Package::getPackageName)
                .orElse(DEFAULT_PACKAGE_NAME);
    }

    String getNamespace(final Path path) {
        final Diagram<Graph, Metadata> diagram = getDiagramByPath(path);
        return getNamespace(diagram);
    }

    @SuppressWarnings("unchecked")
    String getNamespace(final Diagram diagram) {

        final Graph<?, Node> graph = diagram.getGraph();

        return StreamSupport
                .stream(graph.nodes().spliterator(), false)
                .map(Node::getContent)
                .filter(c -> c instanceof Definition)
                .map(c -> (Definition) c)
                .map(Definition::getDefinition)
                .filter(d -> d instanceof DMNDiagram)
                .map(d -> (DMNDiagram) d)
                .findFirst()
                .map(DMNDiagram::getDefinitions)
                .map(definitions -> definitions.getNamespace().getValue())
                .orElse("");
    }

    private Diagram<Graph, Metadata> getDiagramByPath(final Path path) {
        return diagramService.getDiagramByPath(path);
    }
}
