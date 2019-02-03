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

package org.kie.workbench.common.stunner.bpmn.factory;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import org.kie.workbench.common.stunner.bpmn.definition.BPMNDiagram;
import org.kie.workbench.common.stunner.bpmn.definition.property.diagram.BaseDiagramSet;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.factory.impl.BindableDiagramFactory;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.definition.DefinitionSet;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;

public abstract class AbstractBPMNDiagramFactory<M extends Metadata, D extends Diagram<Graph, M>>
        extends BindableDiagramFactory<M, D> {

    private Class<? extends BPMNDiagram> diagramType;

    protected abstract D doBuild(final String name,
                                 final M metadata,
                                 final Graph<DefinitionSet, ?> graph);

    public void setDiagramType(final Class<? extends BPMNDiagram> diagramType) {
        this.diagramType = diagramType;
    }

    @Override
    public D build(final String name,
                   final M metadata,
                   final Graph<DefinitionSet, ?> graph) {
        final D diagram = doBuild(name,
                                  metadata,
                                  graph);
        final Node<Definition<BPMNDiagram>, ?> diagramNode = diagramProvider.apply(graph);
        if (null == diagramNode) {
            throw new IllegalStateException("A BPMN Diagram is expected to be present on BPMN Diagram graphs.");
        }
        updateProperties(name,
                         diagramNode,
                         metadata);
        return diagram;
    }

    protected void updateDiagramProperties(final String name,
                                           final Node<Definition<BPMNDiagram>, ?> diagramNode,
                                           final M metadata) {
        // Default initializations.
        final Optional<BaseDiagramSet> diagramSet = Optional.ofNullable(diagramNode)
                .map(Node::<Definition<BPMNDiagram>>getContent)
                .map(Definition::getDefinition)
                .map(BPMNDiagram::getDiagramSet);

        diagramSet
                .map(BaseDiagramSet::getId)
                .filter(id -> Objects.isNull(id.getValue()))
                .ifPresent(id -> id.setValue(metadata.getTitle()));

        diagramSet
                .map(BaseDiagramSet::getName)
                .filter(attr -> Objects.isNull(attr.getValue()))
                .ifPresent(attr -> attr.setValue(name));
    }

    private void updateProperties(final String name,
                                  final Node<Definition<BPMNDiagram>, ?> diagramNode,
                                  final M metadata) {
        // Set the diagram node as the canvas root.
        metadata.setCanvasRootUUID(diagramNode.getUUID());
        // Delegate to subtypes.
        updateDiagramProperties(name,
                                diagramNode,
                                metadata);
    }

    @SuppressWarnings("unchecked")
    Function<Graph, Node<Definition<BPMNDiagram>, ?>> diagramProvider =
            graph -> GraphUtils.<BPMNDiagram>getFirstNode(graph,
                                                          diagramType);

    protected void setDiagramProvider(Function<Graph, Node<Definition<BPMNDiagram>, ?>> diagramProvider) {
        this.diagramProvider = diagramProvider;
    }
}
