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

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
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
 * A Command to set the incoming connection for an edge.
 * Notes:
 * - In case <code>targetNode</code> is <code>null</code>, connector's target node, if any, will be removed.
 * - if connector is not view based, no need to provide magnet index.
 */
@Portable
public class SetConnectionTargetNodeCommand extends AbstractGraphCommand {

    private final String targetNodeUUID;
    private final String edgeUUID;
    private final Connection connection;

    Connection lastConnection;
    private String lastTargetNodeUUID;
    private transient Edge<? extends View, Node> edge;
    private transient Node<? extends View<?>, Edge> sourceNode;
    private transient Node<? extends View<?>, Edge> targetNode;

    @SuppressWarnings("unchecked")
    public SetConnectionTargetNodeCommand(final @MapsTo("targetNodeUUID") String targetNodeUUID,
                                          final @MapsTo("edgeUUID") String edgeUUID,
                                          final @MapsTo("magnet") Connection connection) {
        this.edgeUUID = checkNotNull("edgeUUID", edgeUUID);
        this.targetNodeUUID = targetNodeUUID;
        this.connection = connection;
        this.lastTargetNodeUUID = null;
        this.lastConnection = null;
    }

    @SuppressWarnings("unchecked")
    public SetConnectionTargetNodeCommand(final Node<? extends View<?>, Edge> targetNode,
                                          final Edge<? extends View, Node> edge,
                                          final Connection connection) {
        this(null != targetNode ? targetNode.getUUID() : null,
             edge.getUUID(),
             connection);
        this.edge = checkNotNull("edge", edge);
        this.sourceNode = edge.getSourceNode();
        this.targetNode = targetNode;
    }

    private static <T> T checkNotNull(String objName, T obj) {
        return Objects.requireNonNull(obj, "Parameter named '" + objName + "' should be not null!");
    }

    @SuppressWarnings("unchecked")
    public SetConnectionTargetNodeCommand(final Node<? extends View<?>, Edge> targetNode,
                                          final Edge<? extends View, Node> edge) {
        this(targetNode,
             edge,
             null);
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<RuleViolation> execute(final GraphCommandExecutionContext context) {
        final CommandResult<RuleViolation> results = allow(context);
        if (!results.getType().equals(CommandResult.Type.ERROR)) {
            final Edge<? extends View, Node> edge = getEdge(context);
            final Node<?, Edge> targetNode = getTargetNode(context);
            final Node<? extends View<?>, Edge> lastTargetNode = edge.getTargetNode();
            // New connection being made
            if (null != lastTargetNode) {
                lastTargetNodeUUID = lastTargetNode.getUUID();
                lastTargetNode.getInEdges().remove(edge);
            }
            if (null != targetNode) {
                targetNode.getInEdges().add(edge);
            }
            edge.setTargetNode(targetNode);
            // Magnet being moved on node
            ViewConnector connectionContent = (ViewConnector) edge.getContent();
            lastConnection = (Connection) connectionContent.getTargetConnection().orElse(null);
            connectionContent.setTargetConnection(connection);
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    protected CommandResult<RuleViolation> check(final GraphCommandExecutionContext context) {
        final GraphCommandResultBuilder resultBuilder = new GraphCommandResultBuilder();
        final Node<? extends View<?>, Edge> targetNode = getTargetNode(context);
        final Edge<View<?>, Node> edge = (Edge<View<?>, Node>) getEdge(context);
        final Node<? extends View<?>, Edge> lastTargetNode = edge.getTargetNode();
        // Only check for rules in case the connector's target node is a different one.
        if ((null == lastTargetNode && null != targetNode) ||
                (null != lastTargetNode && (!lastTargetNode.equals(targetNode)))) {
            final Collection<RuleViolation> connectionRuleViolations =
                    evaluate(context,
                             contextBuilder -> contextBuilder.connection(edge,
                                                                         Optional.ofNullable(sourceNode),
                                                                         Optional.ofNullable(targetNode)));
            resultBuilder.addViolations(connectionRuleViolations);
            final Node<? extends View<?>, Edge> currentTarget = edge.getTargetNode();
            if (null != currentTarget) {
                final Collection<RuleViolation> cardinalityRuleViolations =
                        evaluate(context,
                                 contextBuilder -> contextBuilder.edgeCardinality(currentTarget,
                                                                                  edge,
                                                                                  EdgeCardinalityContext.Direction.INCOMING,
                                                                                  Optional.of(CardinalityContext.Operation.DELETE)));
                resultBuilder.addViolations(cardinalityRuleViolations);
            }
            if (null != targetNode) {
                final Collection<RuleViolation> cardinalityRuleViolations =
                        evaluate(context,
                                 contextBuilder -> contextBuilder.edgeCardinality(targetNode,
                                                                                  edge,
                                                                                  EdgeCardinalityContext.Direction.INCOMING,
                                                                                  Optional.of(CardinalityContext.Operation.ADD)));

                resultBuilder.addViolations(cardinalityRuleViolations);
            }
        }

        return resultBuilder.build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext context) {
        final SetConnectionTargetNodeCommand undoCommand = new SetConnectionTargetNodeCommand((Node<? extends View<?>, Edge>) getNode(context,
                                                                                                                                      lastTargetNodeUUID),
                                                                                              getEdge(context),
                                                                                              lastConnection);
        return undoCommand.execute(context);
    }

    protected Edge<? extends View, Node> getEdge(final GraphCommandExecutionContext context) {
        if (null == this.edge) {
            this.edge = getViewEdge(context,
                                    edgeUUID);
        }
        return this.edge;
    }

    @SuppressWarnings("unchecked")
    private Node<? extends View<?>, Edge> getSourceNode(final GraphCommandExecutionContext context) {
        if (null == sourceNode) {
            sourceNode = getEdge(context).getSourceNode();
        }
        return sourceNode;
    }

    @SuppressWarnings("unchecked")
    protected Node<? extends View<?>, Edge> getTargetNode(final GraphCommandExecutionContext context) {
        if (null == targetNode) {
            targetNode = (Node<? extends View<?>, Edge>) getNode(context,
                                                                 targetNodeUUID);
        }
        return targetNode;
    }

    public Edge<? extends View, Node> getEdge() {
        return edge;
    }

    public Connection getConnection() {
        return connection;
    }

    public Node<? extends View<?>, Edge> getTargetNode() {
        return targetNode;
    }

    public Node<? extends View<?>, Edge> getSourceNode() {
        return sourceNode;
    }

    public String getTargetNodeUUID(){
        return targetNodeUUID;
    }

    public Connection getLastConnection() {
        return lastConnection;
    }

    public String getLastTargetNodeUUID() {
        return lastTargetNodeUUID;
    }

    @Override
    public String toString() {
        return "SetConnectionTargetNodeCommand [edge=" + edgeUUID
                + ", candidate=" + (null != targetNodeUUID ? targetNodeUUID : "null")
                + ", magnet=" + connection + "]";
    }
}
