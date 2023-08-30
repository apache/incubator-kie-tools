/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */

package org.kie.workbench.common.dmn.client.commands.factory.graph;

import java.util.Optional;

import org.kie.workbench.common.dmn.client.commands.util.ContentDefinitionIdUtils;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNGraphsProvider;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.diagram.GraphsProvider;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.impl.SetConnectionSourceNodeCommand;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

import static org.kie.workbench.common.dmn.client.commands.util.ContentDefinitionIdUtils.isTheCurrentDiagram;

public class DMNSetConnectionSourceNodeCommand extends SetConnectionSourceNodeCommand {

    private final DMNGraphsProvider graphsProvider;
    private final Optional<String> diagramId;

    public DMNSetConnectionSourceNodeCommand(final Node<? extends View<?>, Edge> sourceNode,
                                             final Edge<? extends View, Node> edge,
                                             final Connection connection,
                                             final DMNGraphsProvider graphsProvider) {
        super(sourceNode, edge, connection);
        this.graphsProvider = graphsProvider;
        this.diagramId = ContentDefinitionIdUtils.getDiagramId(edge);
    }

    @Override
    public Node<? extends View<?>, Edge> getSourceNode(final GraphCommandExecutionContext context) {
        if (commandBelongsToAnotherGraph()) {
            return getEdgesGraph().getNode(getSourceNodeUUID());
        } else {
            return superGetSourceNode(context);
        }
    }

    Node<? extends View<?>, Edge> superGetSourceNode(final GraphCommandExecutionContext context) {
        return super.getSourceNode(context);
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext context) {
        final DMNSetConnectionSourceNodeCommand undoCommand = new DMNSetConnectionSourceNodeCommand((Node<? extends View<?>, Edge>) getNode(context,
                                                                                                                                            getLastSourceNodeUUID()),
                                                                                                    getEdge(context),
                                                                                                    getLastConnection(),
                                                                                                    graphsProvider);
        return undoCommand.execute(context);
    }

    public Optional<String> getDiagramId() {
        return diagramId;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Node<?, Edge> getNode(final GraphCommandExecutionContext context,
                                    final String uuid) {
        if (commandBelongsToAnotherGraph()) {
            return getEdgesGraph().getNode(uuid);
        } else {
            return superGetNode(context, uuid);
        }
    }

    Node<?, Edge> superGetNode(final GraphCommandExecutionContext context,
                               final String uuid) {
        return super.getNode(context, uuid);
    }

    boolean commandBelongsToAnotherGraph() {
        return getDiagramId().isPresent()
                && !isTheCurrentDiagram(getDiagramId(), getGraphsProvider());
    }

    Graph getEdgesGraph() {
        final Optional<String> id = getDiagramId();
        if (id.isPresent()) {
            return getGraphsProvider().getDiagram(id.get()).getGraph();
        }
        throw new IllegalStateException("Unable to get the edges graph. The diagramId is not set.");
    }

    public GraphsProvider getGraphsProvider() {
        return graphsProvider;
    }
}
