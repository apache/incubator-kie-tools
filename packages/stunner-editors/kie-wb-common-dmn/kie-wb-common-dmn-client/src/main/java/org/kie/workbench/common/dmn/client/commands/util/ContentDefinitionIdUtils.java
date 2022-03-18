/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.dmn.client.commands.util;

import java.util.Objects;
import java.util.Optional;

import org.kie.workbench.common.stunner.core.diagram.GraphsProvider;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.HasContentDefinitionId;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

public class ContentDefinitionIdUtils {

    private ContentDefinitionIdUtils() {
    }

    public static boolean belongsToCurrentGraph(final Node node,
                                                final GraphsProvider graphsProvider) {
        return isTheCurrentDiagram(getDiagramId(node), graphsProvider);
    }

    public static boolean belongsToCurrentGraph(final Edge<? extends View, Node> edge,
                                                final GraphsProvider graphsProvider) {
        return isTheCurrentDiagram(getDiagramId(edge), graphsProvider);
    }

    public static boolean isTheCurrentDiagram(final Optional<String> diagramId,
                                              final GraphsProvider graphsProvider) {
        return diagramId.isPresent() &&
                Objects.equals(diagramId.get(), graphsProvider.getCurrentDiagramId());
    }

    public static Optional<String> getDiagramId(final Edge<? extends View, Node> edge) {
        final Node node;
        if (Objects.isNull(edge.getSourceNode())) {
            node = edge.getTargetNode();
        } else {
            node = edge.getSourceNode();
        }

        return getDiagramId(node);
    }

    public static Optional<String> getDiagramId(final Node node) {

        if (Objects.isNull(node)) {
            return Optional.empty();
        }

        final Object content = node.getContent();
        if (content instanceof Definition) {
            final Object definition = ((Definition) content).getDefinition();
            if (definition instanceof HasContentDefinitionId) {
                return Optional.of(((HasContentDefinitionId) definition).getDiagramId());
            }
        }

        return Optional.empty();
    }
}
