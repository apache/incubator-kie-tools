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

package org.kie.workbench.common.dmn.client.commands.factory.graph;

import java.util.HashMap;
import java.util.Objects;

import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.processing.index.MutableIndex;
import org.kie.workbench.common.stunner.core.graph.processing.index.map.MapIndex;

public class DMNRegisterNodeCommand extends RegisterNodeCommand {

    private final Graph graph;

    public DMNRegisterNodeCommand(final Graph graph,
                                  final Node candidate) {
        super(candidate);
        this.graph = graph;
    }

    protected Graph<?, Node> getGraph(final GraphCommandExecutionContext context) {
        return graph;
    }

    protected MutableIndex<Node, Edge> getMutableIndex(final GraphCommandExecutionContext context) {
        if (!Objects.equals(context.getGraphIndex().getGraph(), getGraph(context))) {
            return new MapIndex(getGraph(context),
                                new HashMap<>(),
                                new HashMap<>());
        }

        return (MutableIndex<Node, Edge>) context.getGraphIndex();
    }
}
