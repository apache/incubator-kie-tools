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

package org.kie.workbench.common.dmn.client.commands.factory.canvas;

import org.kie.workbench.common.dmn.api.definition.model.DecisionService;
import org.kie.workbench.common.dmn.client.commands.factory.graph.DMNDeleteConnectorCommand;
import org.kie.workbench.common.dmn.client.commands.factory.graph.DMNDeregisterNodeCommand;
import org.kie.workbench.common.dmn.client.docks.navigator.drds.DMNGraphsProvider;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.GraphsProvider;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.impl.DeleteConnectorCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.DeregisterNodeCommand;
import org.kie.workbench.common.stunner.core.graph.command.impl.SafeDeleteNodeCommand;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.NodeDefinitionHelper;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

public class DMNSafeDeleteNodeCommand extends SafeDeleteNodeCommand {

    private final DMNGraphsProvider graphsProvider;
    private final Node<?, Edge> node;

    public DMNSafeDeleteNodeCommand(final Node<?, Edge> node,
                                    final SafeDeleteNodeCommandCallback safeDeleteCallback,
                                    final Options options,
                                    final DMNGraphsProvider graphsProvider) {
        super(node, safeDeleteCallback, options);
        this.graphsProvider = graphsProvider;
        this.node = node;
    }

    @Override
    public boolean shouldKeepChildren(final Node<Definition<?>, Edge> candidate) {
        return DefinitionUtils.getElementDefinition(candidate) instanceof DecisionService;
    }

    @Override
    public GraphsProvider getGraphsProvider() {
        return graphsProvider;
    }

    @Override
    protected Graph<?, Node> getGraph(final GraphCommandExecutionContext context) {
        return getGraph(node);
    }

    Graph getGraph(final Node node) {
        final String diagramId = NodeDefinitionHelper.getDiagramId(node);
        final Diagram diagram = graphsProvider.getDiagram(diagramId);
        return diagram.getGraph();
    }

    @Override
    protected Node<?, Edge> getNode(final GraphCommandExecutionContext context,
                                    final String uuid) {
        return node;
    }

    @Override
    protected DeregisterNodeCommand createDeregisterNodeCommand(final Node node) {
        return new DMNDeregisterNodeCommand(getGraph(node), node.getUUID());
    }

    @Override
    protected DeleteConnectorCommand getDeleteConnectorCommand(final Edge<? extends View<?>, Node> edge) {
        return new DMNDeleteConnectorCommand(edge, graphsProvider);
    }
}
