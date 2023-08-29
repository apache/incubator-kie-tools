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
import java.util.function.Consumer;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.definition.adapter.DefinitionId;
import org.kie.workbench.common.stunner.core.definition.clone.ClonePolicy;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.view.Connection;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.util.UUID;

/**
 * A Command which adds an candidate into a graph and sets its target sourceNode.
 */
@Portable
public final class CloneConnectorCommand extends AbstractGraphCompositeCommand {

    private final Edge candidate;
    private transient Edge clone;
    private transient Connection sourceConnection;
    private transient Connection targetConnection;
    private transient Node<? extends View<?>, Edge> sourceNode;
    private transient Node<? extends View<?>, Edge> targetNode;
    private final String sourceNodeUUID;
    private final String targetNodeUUID;
    private final Optional<Consumer<Edge>> callback;

    public CloneConnectorCommand() {
        this(null, null, null);
    }

    public CloneConnectorCommand(final @MapsTo("candidate") Edge candidate, final @MapsTo("sourceNodeUUID") String sourceNodeUUID, final @MapsTo("targetNodeUUID") String targetNodeUUID) {
        this(candidate, sourceNodeUUID, targetNodeUUID, null);
    }

    public CloneConnectorCommand(Edge candidate, String sourceNodeUUID, String targetNodeUUID, Consumer<Edge> callback) {
        this.candidate = checkNotNull("candidate", candidate);
        this.sourceNodeUUID = checkNotNull("sourceNodeUUID", sourceNodeUUID);
        this.targetNodeUUID = checkNotNull("targetNodeUUID", targetNodeUUID);
        this.callback = Optional.ofNullable(callback);
    }

    private static <T> T checkNotNull(String objName, T obj) {
        return Objects.requireNonNull(obj, "Parameter named '" + objName + "' should be not null!");
    }

    @Override
    @SuppressWarnings("unchecked")
    protected CloneConnectorCommand initialize(final GraphCommandExecutionContext context) {
        super.initialize(context);

        this.sourceNode = (Node<? extends View<?>, Edge>) getNode(context, sourceNodeUUID);
        this.targetNode = (Node<? extends View<?>, Edge>) getNode(context, targetNodeUUID);

        if (!(candidate.getContent() instanceof ViewConnector)) {
            throw new IllegalArgumentException("Candidate: " + candidate.getTargetNode() + " content should be a ViewConnector");
        }

        //clone candidate
        ViewConnector edgeContent = (ViewConnector) candidate.getContent();
        final Object bean = edgeContent.getDefinition();
        final DefinitionId definitionId = context.getDefinitionManager().adapters().forDefinition().getId(bean);
        clone = context.getFactoryManager().newElement(UUID.uuid(), definitionId.value()).asEdge();

        //Cloning the candidate content with properties
        Object clonedDefinition = context.getDefinitionManager().cloneManager().clone(edgeContent.getDefinition(), ClonePolicy.ALL);
        ViewConnector clonedContent = (ViewConnector) clone.getContent();
        clonedContent.setDefinition(clonedDefinition);

        // Magnet being moved on node
        ViewConnector connectionContent = (ViewConnector) candidate.getContent();
        this.sourceConnection = (Connection) connectionContent.getSourceConnection().orElse(null);
        this.targetConnection = (Connection) connectionContent.getTargetConnection().orElse(null);

        commands.add(new AddConnectorCommand(sourceNode, clone, sourceConnection));
        commands.add(new SetConnectionTargetNodeCommand(targetNode, clone, targetConnection));

        // Add the candidate into index, so child commands can find it.
        getMutableIndex(context).addEdge(clone);
        return this;
    }

    @Override
    public CommandResult<RuleViolation> execute(GraphCommandExecutionContext context) {
        CommandResult<RuleViolation> commandResult = super.execute(context);
        if (!CommandUtils.isError(commandResult)) {
            callback.ifPresent(c -> c.accept(clone));
        }
        return commandResult;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext context) {
        return new DeleteConnectorCommand(clone).execute(context);
    }

    @Override
    protected boolean delegateRulesContextToChildren() {
        return true;
    }

    protected Edge getCandidate() {
        return candidate;
    }
}