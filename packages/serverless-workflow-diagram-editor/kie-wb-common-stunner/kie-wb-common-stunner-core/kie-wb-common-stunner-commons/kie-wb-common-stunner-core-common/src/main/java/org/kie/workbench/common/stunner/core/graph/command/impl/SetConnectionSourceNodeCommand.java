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

package org.kie.workbench.common.stunner.core.graph.command.impl;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.context.CardinalityContext;
import org.kie.workbench.common.stunner.core.rule.context.EdgeCardinalityContext;

/**
 * A Command to set the outgoing connection for an edge.
 * Notes:
 * - In case <code>sourceNode</code> is <code>null</code>, connector's source node, if any, will be removed.
 * - if connector is not view based, no need to provide magnet index.
 */
public class SetConnectionSourceNodeCommand extends AbstractGraphCommand {

    private final String sourceNodeUUID;
    private final String edgeUUID;
    private final Connection connection;

    Connection lastConnection;
    private String lastSourceNodeUUID;
    private transient Edge<? extends View, Node> edge;
    private transient Node<? extends View<?>, Edge> targetNode;
    private transient Node<? extends View<?>, Edge> sourceNode;

    @SuppressWarnings("unchecked")
    public SetConnectionSourceNodeCommand(final String sourceNodeUUID,
                                          final String edgeUUID,
                                          final Connection connection) {
        this.edgeUUID = Objects.requireNonNull(edgeUUID, "Parameter named 'edgeUUID' should be not null!");
        this.sourceNodeUUID = sourceNodeUUID;
        this.connection = connection;
        this.lastSourceNodeUUID = null;
        this.lastConnection = null;
    }

    @SuppressWarnings("unchecked")
    public SetConnectionSourceNodeCommand(final Node<? extends View<?>, Edge> sourceNode,
                                          final Edge<? extends View, Node> edge,
                                          final Connection connection) {
        this(null != sourceNode ? sourceNode.getUUID() : null,
             edge.getUUID(),
             connection);
        this.sourceNode = sourceNode;
        this.edge = edge;
        this.targetNode = edge.getTargetNode();
    }

    @SuppressWarnings("unchecked")
    public SetConnectionSourceNodeCommand(final Node<? extends View<?>, Edge> sourceNode,
                                          final Edge<? extends View, Node> edge) {
        this(sourceNode,
             edge,
             null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<RuleViolation> execute(final GraphCommandExecutionContext context) {
        final CommandResult<RuleViolation> results = allow(context);
        if (!results.getType().equals(CommandResult.Type.ERROR)) {
            final Edge<? extends View, Node> edge = getEdge(context);
            final Node<?, Edge> sourceNode = getSourceNode(context);
            final Node<? extends View<?>, Edge> lastSourceNode = edge.getSourceNode();
            // New connection being made
            if (null != lastSourceNode) {
                this.lastSourceNodeUUID = lastSourceNode.getUUID();
                lastSourceNode.getOutEdges().remove(edge);
            }
            if (null != sourceNode) {
                sourceNode.getOutEdges().add(edge);
            }
            edge.setSourceNode(sourceNode);
            // Magnet being moved on node
            ViewConnector connectionContent = (ViewConnector) edge.getContent();
            lastConnection = (Connection) connectionContent.getSourceConnection().orElse(null);
            connectionContent.setSourceConnection(connection);
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    protected CommandResult<RuleViolation> check(final GraphCommandExecutionContext context) {
        final GraphCommandResultBuilder resultBuilder = new GraphCommandResultBuilder();
        final Node<View<?>, Edge> sourceNode = (Node<View<?>, Edge>) getSourceNode(context);
        final Edge<View<?>, Node> edge = (Edge<View<?>, Node>) getEdge(context);
        final Node<? extends View<?>, Edge> lastSourceNode = edge.getSourceNode();
        // Only check for rules in case the connector's source node is a different one.
        if ((null == lastSourceNode && null != sourceNode) ||
                (null != lastSourceNode && (!lastSourceNode.equals(sourceNode)))) {
            // New connection being made
            final Collection<RuleViolation> connectionRuleViolations =
                    evaluate(context,
                             contextBuilder -> contextBuilder.connection(edge,
                                                                         Optional.ofNullable(sourceNode),
                                                                         Optional.ofNullable(targetNode)));
            resultBuilder.addViolations(connectionRuleViolations);
            final Node<View<?>, Edge> currentSource = edge.getSourceNode();
            // If the edge has an outoutgoing source node, check cardinality for removing it.
            if (null != currentSource) {
                final Collection<RuleViolation> cardinalityRuleViolations =
                        evaluate(context,
                                 contextBuilder -> contextBuilder.edgeCardinality(currentSource,
                                                                                  edge,
                                                                                  EdgeCardinalityContext.Direction.OUTGOING,
                                                                                  Optional.of(CardinalityContext.Operation.DELETE)));
                resultBuilder.addViolations(cardinalityRuleViolations);
            }
            // If the new source node exist, evaluate cardinality rules for this edge.
            if (null != sourceNode) {
                final Collection<RuleViolation> cardinalityRuleViolations =
                        evaluate(context,
                                 contextBuilder -> contextBuilder.edgeCardinality(sourceNode,
                                                                                  edge,
                                                                                  EdgeCardinalityContext.Direction.OUTGOING,
                                                                                  Optional.of(CardinalityContext.Operation.ADD)));
                resultBuilder.addViolations(cardinalityRuleViolations);
            }
        }
        return resultBuilder.build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext context) {
        final SetConnectionSourceNodeCommand undoCommand = new SetConnectionSourceNodeCommand((Node<? extends View<?>, Edge>) getNode(context,
                                                                                                                                      lastSourceNodeUUID),
                                                                                              getEdge(context),
                                                                                              lastConnection);
        return undoCommand.execute(context);
    }

    @SuppressWarnings("unchecked")
    public Node<? extends View<?>, Edge> getTargetNode(final GraphCommandExecutionContext context) {
        if (null == targetNode) {
            targetNode = getEdge(context).getTargetNode();
        }
        return targetNode;
    }

    @SuppressWarnings("unchecked")
    public Node<? extends View<?>, Edge> getSourceNode(final GraphCommandExecutionContext context) {
        if (null == sourceNode) {
            sourceNode = (Node<? extends View<?>, Edge>) getNode(context,
                                                                 sourceNodeUUID);
        }
        return sourceNode;
    }

    public Edge<? extends View, Node> getEdge(final GraphCommandExecutionContext context) {
        if (null == this.edge) {
            this.edge = getViewEdge(context,
                                    edgeUUID);
        }
        return this.edge;
    }

    public Node<? extends View<?>, Edge> getSourceNode() {
        return sourceNode;
    }

    public Edge<? extends View, Node> getEdge() {
        return edge;
    }

    public Node<? extends View<?>, Edge> getTargetNode() {
        return targetNode;
    }

    public Connection getConnection() {
        return connection;
    }

    public String getSourceNodeUUID() {
        return sourceNodeUUID;
    }

    public Connection getLastConnection() {
        return lastConnection;
    }

    public String getLastSourceNodeUUID() {
        return lastSourceNodeUUID;
    }

    @Override
    public String toString() {
        return "SetConnectionSourceNodeCommand [edge=" + edgeUUID
                + ", candidate=" + (null != sourceNodeUUID ? sourceNodeUUID : "null")
                + ", magnet=" + connection + "]";
    }
}
