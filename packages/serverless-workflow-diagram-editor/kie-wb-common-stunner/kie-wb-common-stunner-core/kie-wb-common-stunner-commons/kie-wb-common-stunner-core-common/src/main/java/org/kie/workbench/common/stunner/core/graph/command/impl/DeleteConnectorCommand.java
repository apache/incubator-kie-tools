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

import java.util.Objects;
import java.util.Optional;

import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

/**
 * A Command to delete an edge from a graph
 */
public class DeleteConnectorCommand extends AbstractGraphCompositeCommand {

    private final String edgeUUID;
    private transient Edge<? extends View, Node> edge;

    public DeleteConnectorCommand(final String edgeUUID) {
        this.edgeUUID = Objects.requireNonNull(edgeUUID, "Parameter named 'edgeUUID' should be not null!");
    }

    public DeleteConnectorCommand(final Edge<? extends View, Node> edge) {
        this(edge.getUUID());
        this.edge = edge;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected DeleteConnectorCommand initialize(final GraphCommandExecutionContext context) {
        super.initialize(context);
        final Edge<? extends ViewConnector, Node> edge = getCandidateEdge(context);
        final Node<View<?>, Edge> targetNode = edge.getTargetNode();
        final Node<View<?>, Edge> sourceNode = edge.getSourceNode();
        if (null != sourceNode) {
            commands.add(buildSetConnectionSourceCommand(edge));
        }
        if (null != targetNode) {
            commands.add(buildSetConnectionTargetCommand(edge));
        }
        return this;
    }

    @Override
    public CommandResult<RuleViolation> execute(final GraphCommandExecutionContext context) {
        final CommandResult<RuleViolation> result = super.execute(context);
        if (!CommandUtils.isError(result)) {
            final Edge<? extends ViewConnector, Node> edge = getCandidateEdge(context);
            getMutableIndex(context).removeEdge(edge);
        }
        return result;
    }

    @Override
    public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext context) {
        getMutableIndex(context).addEdge(edge);
        final CommandResult<RuleViolation> result = super.undo(context);
        if (CommandUtils.isError(result)) {
            getMutableIndex(context).removeEdge(edge);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private Edge<? extends ViewConnector, Node> getCandidateEdge(final GraphCommandExecutionContext context) {
        if (null == edge) {
            edge = getViewEdge(context,
                               edgeUUID);
        }
        return (Edge<? extends ViewConnector, Node>) edge;
    }

    public Edge<? extends View, Node> getEdge() {
        return edge;
    }

    public String getLastSourceNodeUUID() {
        return getSetConnectionSourceNodeCommand().map(c -> ((SetConnectionSourceNodeCommand) c).getLastSourceNodeUUID()).orElse(null);
    }

    public String getLastTargetNodeUUID() {
        return getSetConnectionTargetNodeCommand().map(c -> ((SetConnectionTargetNodeCommand) c).getLastTargetNodeUUID()).orElse(null);
    }

    @Override
    public String toString() {
        return getClass().getName() + "[edge=" + edgeUUID + "]";
    }

    @Override
    protected boolean delegateRulesContextToChildren() {
        return true;
    }

    protected SetConnectionTargetNodeCommand buildSetConnectionTargetCommand(final Edge<? extends ViewConnector, Node> edge) {
        return new SetConnectionTargetNodeCommand(null,
                                                  edge);
    }

    protected SetConnectionSourceNodeCommand buildSetConnectionSourceCommand(final Edge<? extends ViewConnector, Node> edge) {
        return new SetConnectionSourceNodeCommand(null,
                                                  edge);
    }

    private Optional<? super SetConnectionTargetNodeCommand> getSetConnectionTargetNodeCommand() {
        return commands.stream().filter(c -> c instanceof SetConnectionTargetNodeCommand).findAny();
    }

    private Optional<? super SetConnectionSourceNodeCommand> getSetConnectionSourceNodeCommand() {
        return commands.stream().filter(c -> c instanceof SetConnectionSourceNodeCommand).findAny();
    }
}
