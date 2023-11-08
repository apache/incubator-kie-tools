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
import java.util.Collections;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.exception.BadCommandArgumentsException;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.rule.context.CardinalityContext;

/**
 * A Command to deregister a node from the graph storage.
 */
public class DeregisterNodeCommand extends AbstractGraphCommand {

    private static final Logger LOGGER = Logger.getLogger(DeregisterNodeCommand.class.getName());

    protected final String uuid;
    transient Node<?, Edge> node;
    transient Node<?, Edge> removed;

    public DeregisterNodeCommand(final String uuid) {
        this.uuid = Objects.requireNonNull(uuid, "Parameter named 'uuid' should be not null!");
        this.removed = null;
    }

    public DeregisterNodeCommand(final Node<?, Edge> node) {
        this(node.getUUID());
        this.node = node;
    }

    @Override
    public CommandResult<RuleViolation> execute(final GraphCommandExecutionContext context) {
        CommandResult<RuleViolation> results = allow(context);
        if (!results.getType().equals(CommandResult.Type.ERROR)) {
            LOGGER.log(Level.FINE,
                       "Executing...");
            final org.kie.workbench.common.stunner.core.graph.Graph graph = getGraph(context);
            final Node<?, Edge> candidate = getCandidate(context);
            this.removed = candidate;
            graph.removeNode(candidate.getUUID());
            getMutableIndex(context).removeNode(candidate);
            LOGGER.log(Level.FINE,
                       "Node [" + uuid + " removed from strcture and index.");
        }
        return results;
    }

    @Override
    public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext context) {
        final RegisterNodeCommand undoCommand = new RegisterNodeCommand(removed);
        return undoCommand.execute(context);
    }

    @SuppressWarnings("unchecked")
    protected CommandResult<RuleViolation> check(final GraphCommandExecutionContext context) {
        // And check it really exist on the graph storage as well.
        final Node<View<?>, Edge> candidate = (Node<View<?>, Edge>) checkCandidateNotNull(context);
        final GraphCommandResultBuilder builder = new GraphCommandResultBuilder();
        final Collection<RuleViolation> cardinalityRuleViolations =
                evaluate(context,
                         contextBuilder -> contextBuilder.cardinality(Collections.singleton(candidate),
                                                                      CardinalityContext.Operation.DELETE));
        builder.addViolations(cardinalityRuleViolations);
        return builder.build();
    }

    public Node<?, Edge> getNode() {
        return node;
    }

    public Node<?, Edge> getRemoved() {
        return removed;
    }

    @SuppressWarnings("unchecked")
    protected Node<?, Edge> getCandidate(final GraphCommandExecutionContext context) {
        if (null == node) {
            node = getNode(context,
                           uuid);
        }
        return node;
    }

    protected Node<?, Edge> checkCandidateNotNull(final GraphCommandExecutionContext context) {
        final Node<?, Edge> e = getCandidate(context);
        if (null == e) {
            throw new BadCommandArgumentsException(this,
                                                   uuid,
                                                   "Node not found for [" + uuid + "].");
        }
        return e;
    }

    @Override
    public String toString() {
        return "DeregisterNodeCommand [candidate=" + uuid + "]";
    }
}
