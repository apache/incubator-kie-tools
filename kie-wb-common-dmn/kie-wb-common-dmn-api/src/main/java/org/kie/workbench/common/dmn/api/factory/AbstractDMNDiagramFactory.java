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
package org.kie.workbench.common.dmn.api.factory;

import java.util.function.Function;
import java.util.stream.Stream;

import org.kie.workbench.common.dmn.api.definition.v1_1.DMNDiagram;
import org.kie.workbench.common.dmn.api.definition.v1_1.DMNModelInstrumentedBase;
import org.kie.workbench.common.dmn.api.definition.v1_1.Definitions;
import org.kie.workbench.common.dmn.api.property.dmn.Name;
import org.kie.workbench.common.dmn.api.property.dmn.Text;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.factory.impl.BindableDiagramFactory;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;
import org.kie.workbench.common.stunner.core.util.StringUtils;
import org.kie.workbench.common.stunner.core.util.UUID;

public abstract class AbstractDMNDiagramFactory<M extends Metadata, D extends Diagram<Graph, M>>
        extends BindableDiagramFactory<M, D> {

    private static final Class<DMNDiagram> DIAGRAM_TYPE = DMNDiagram.class;

    @SuppressWarnings("unchecked")
    private Function<Graph, Node<Definition<DMNDiagram>, ?>> diagramProvider = graph -> GraphUtils.getFirstNode(graph,
                                                                                                                DIAGRAM_TYPE);

    protected abstract D doBuild(final String name,
                                 final M metadata,
                                 final Graph<DefinitionSet, ?> graph);

    @Override
    public D build(final String name,
                   final M metadata,
                   final Graph<DefinitionSet, ?> graph) {
        final D diagram = doBuild(name,
                                  metadata,
                                  graph);
        final Node<Definition<DMNDiagram>, ?> diagramNode = diagramProvider.apply(graph);
        if (null == diagramNode) {
            throw new IllegalStateException("A DMNDiagram is expected to be present on DMN Diagram graphs.");
        }
        updateProperties(diagramNode, metadata);
        updateDefaultNameSpaces(diagramNode);
        updateName(diagramNode, name);
        return diagram;
    }

    private void updateProperties(final Node<Definition<DMNDiagram>, ?> diagramNode,
                                  final M metadata) {
        // Set the diagram node as the canvas root.
        metadata.setCanvasRootUUID(diagramNode.getUUID());
    }

    private void updateDefaultNameSpaces(final Node<Definition<DMNDiagram>, ?> diagramNode) {
        final DMNDiagram dmnDiagram = diagramNode.getContent().getDefinition();
        final Definitions dmnDefinitions = dmnDiagram.getDefinitions();

        Stream.of(DMNModelInstrumentedBase.Namespace.values())
                .filter(namespace -> !dmnDefinitions.getNsContext().containsValue(namespace.getUri()))
                .forEach(namespace -> dmnDefinitions.getNsContext().put(namespace.getPrefix(), namespace.getUri()));

        String defaultNamespace = !StringUtils.isEmpty(dmnDefinitions.getNamespace().getValue())
                ? dmnDefinitions.getNamespace().getValue()
                : DMNModelInstrumentedBase.Namespace.DEFAULT.getUri() + UUID.uuid();

        dmnDefinitions.setNamespace(new Text(defaultNamespace));
        dmnDefinitions.getNsContext().put(DMNModelInstrumentedBase.Namespace.DEFAULT.getPrefix(),
                                          defaultNamespace);
    }

    private void updateName(final Node<Definition<DMNDiagram>, ?> diagramNode,
                            final String name) {
        final DMNDiagram dmnDiagram = diagramNode.getContent().getDefinition();
        final Definitions dmnDefinitions = dmnDiagram.getDefinitions();
        final Name dmnName = dmnDefinitions.getName();
        if (StringUtils.isEmpty(dmnName.getValue())) {
            dmnName.setValue(name);
        }
    }
}
