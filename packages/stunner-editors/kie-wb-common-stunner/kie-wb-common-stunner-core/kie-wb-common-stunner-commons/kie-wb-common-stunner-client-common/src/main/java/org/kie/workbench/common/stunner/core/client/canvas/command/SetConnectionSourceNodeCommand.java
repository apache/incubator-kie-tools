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

package org.kie.workbench.common.stunner.core.client.canvas.command;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

public class SetConnectionSourceNodeCommand extends AbstractCanvasGraphCommand {

    private final Node<? extends View<?>, Edge> node;
    private final Edge<? extends ViewConnector<?>, Node> edge;
    private final Connection connection;

    public SetConnectionSourceNodeCommand(final Node<? extends View<?>, Edge> node,
                                          final Edge<? extends ViewConnector<?>, Node> edge,
                                          final Connection connection) {
        this.node = node;
        this.edge = edge;
        this.connection = connection;
    }

    @Override
    protected Command<GraphCommandExecutionContext, RuleViolation> newGraphCommand(final AbstractCanvasHandler context) {
        return new org.kie.workbench.common.stunner.core.graph.command.impl.SetConnectionSourceNodeCommand(node,
                                                                                                           edge,
                                                                                                           connection);
    }

    @Override
    protected AbstractCanvasCommand newCanvasCommand(final AbstractCanvasHandler context) {
        return new SetCanvasConnectionCommand(edge);
    }

    public Node<? extends View<?>, Edge> getNode() {
        return node;
    }

    public Edge<? extends View<?>, Node> getEdge() {
        return edge;
    }

    public Connection getConnection() {
        return connection;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [node=" + toUUID(getNode()) + "," +
                "edge=" + toUUID(getEdge()) + "," +
                "connection=" + connection + "]";
    }
}
