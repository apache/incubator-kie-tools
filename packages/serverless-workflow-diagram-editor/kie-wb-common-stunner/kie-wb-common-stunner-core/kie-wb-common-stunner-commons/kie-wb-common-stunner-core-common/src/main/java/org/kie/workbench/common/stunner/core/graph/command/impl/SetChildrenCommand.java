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

import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.util.UUID;

/**
 * Creates/defines a new parent-child relationship (edge + Child content type)  from the given nodes.
 * All nodes must already be crated and present on the graph storage.
 */
public class SetChildrenCommand extends AbstractGraphCommand {

    private final String parentUUID;
    private final String[] candidateUUIDs;
    private transient Node<?, Edge> parent;
    private transient Collection<Node<?, Edge>> candidates;

    public SetChildrenCommand(final String parentUUID,
                              final String[] candidateUUIDs) {
        this.parentUUID = checkNotNull("parentUUID", parentUUID);
        this.candidateUUIDs = checkNotNull("candidateUUIDs", candidateUUIDs);
    }

    private static <T> T checkNotNull(String objName, T obj) {
        return Objects.requireNonNull(obj, "Parameter named '" + objName + "' should be not null!");
    }

    public SetChildrenCommand(final Node<?, Edge> parent,
                              final Node<?, Edge> candidate) {
        this(parent.getUUID(),
             new String[]{candidate.getUUID()});
        this.parent = parent;
        this.candidates = Collections.singleton(candidate);
    }

    public SetChildrenCommand(final Node<?, Edge> parent,
                              final Collection<Node<?, Edge>> candidates) {
        this(parent.getUUID(),
             CommandUtils.toUUIDs(candidates));
        this.parent = parent;
        this.candidates = candidates;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<RuleViolation> execute(final GraphCommandExecutionContext context) {
        final CommandResult<RuleViolation> results = allow(context);
        if (!results.getType().equals(CommandResult.Type.ERROR)) {
            getCandidates(context).forEach(node -> execute(context,
                                                           getParent(context),
                                                           node));
        }
        return results;
    }

    protected void execute(final GraphCommandExecutionContext context,
                           final Node<?, Edge> parent,
                           final Node<?, Edge> candidate) {
        final String uuid = UUID.uuid();
        final Edge<Child, Node> edge = new EdgeImpl<>(uuid);
        edge.setContent(new Child());
        edge.setSourceNode(parent);
        edge.setTargetNode(candidate);
        parent.getOutEdges().add(edge);
        candidate.getInEdges().add(edge);
        getMutableIndex(context).addEdge(edge);
    }

    @SuppressWarnings("unchecked")
    protected CommandResult<RuleViolation> check(final GraphCommandExecutionContext context) {
        final Element<? extends Definition<?>> parent = (Element<? extends Definition<?>>) getParent(context);
        final Collection candidates = getCandidates(context);
        final Collection<RuleViolation> containmentRuleViolations =
                evaluate(context,
                         contextBuilder -> contextBuilder.containment(parent,
                                                                      candidates));
        return new GraphCommandResultBuilder(containmentRuleViolations).build();
    }

    @Override
    public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext context) {
        final Node<?, Edge> parent = getParent(context);
        final Collection<Node<?, Edge>> candidates = getCandidates(context);
        RemoveChildrenCommand undoCommand = new RemoveChildrenCommand(parent,
                                                                      candidates);
        return undoCommand.execute(context);
    }

    public Node<?, Edge> getParent() {
        return parent;
    }

    public Collection<Node<?, Edge>> getCandidates() {
        return candidates;
    }

    @SuppressWarnings("unchecked")
    protected Node<?, Edge> getParent(final GraphCommandExecutionContext context) {
        if (null == parent) {
            parent = getNodeNotNull(context,
                                    parentUUID);
        }
        return parent;
    }

    @SuppressWarnings("unchecked")
    protected Collection<Node<?, Edge>> getCandidates(final GraphCommandExecutionContext context) {
        if (null == candidates) {
            candidates = CommandUtils.getCandidates(context, candidateUUIDs);
        }
        return candidates;
    }

    @Override
    public String toString() {
        return "AddChildEdgeCommand [parent=" + parentUUID + ", candidates=" + candidateUUIDs + "]";
    }
}
