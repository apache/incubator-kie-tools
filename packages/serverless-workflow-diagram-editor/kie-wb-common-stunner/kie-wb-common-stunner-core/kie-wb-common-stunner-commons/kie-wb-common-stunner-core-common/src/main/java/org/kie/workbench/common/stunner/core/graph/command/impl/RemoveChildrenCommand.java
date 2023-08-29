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
import java.util.List;
import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Parent;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

/**
 * Removes the parent-child relationship  ( Child ) between a parent and several nodes.
 */
@Portable
public class RemoveChildrenCommand extends AbstractGraphCommand {

    private final String parentUUID;
    private final String[] candidateUUIDs;
    private transient Node<?, Edge> parent;
    private transient Collection<Node<?, Edge>> candidates;

    public RemoveChildrenCommand(final @MapsTo("parentUUID") String parentUUID,
                                 final @MapsTo("candidateUUIDs") String[] candidateUUIDs) {
        this.parentUUID = checkNotNull("parentUUID", parentUUID);
        this.candidateUUIDs = checkNotNull("candidateUUIDs", candidateUUIDs);
    }

    public RemoveChildrenCommand(final Node<?, Edge> parent,
                                 final Node<?, Edge> candidate) {
        this(parent.getUUID(),
             new String[]{candidate.getUUID()});
        this.parent = parent;
        this.candidates = Collections.singleton(candidate);
    }

    public RemoveChildrenCommand(final Node<?, Edge> parent,
                                 final Collection<Node<?, Edge>> candidates) {
        this(parent.getUUID(),
             CommandUtils.toUUIDs(candidates));
        this.parent = parent;
        this.candidates = candidates;
    }

    @Override
    public CommandResult<RuleViolation> execute(final GraphCommandExecutionContext context) {
        final CommandResult<RuleViolation> results = allow(context);
        if (!results.getType().equals(CommandResult.Type.ERROR)) {
            final Node<?, Edge> parent = getParent(context);
            getCandidates(context).forEach(candidate -> removeChild(context, parent, candidate));
        }
        return results;
    }

    private void removeChild(final GraphCommandExecutionContext context,
                             final Node<?, Edge> parent,
                             final Node<?, Edge> candidate) {
        final Edge<Parent, Node> edge = getEdgeForTarget(parent,
                                                         candidate);
        if (null != edge) {
            edge.setSourceNode(null);
            edge.setTargetNode(null);
            parent.getOutEdges().remove(edge);
            candidate.getInEdges().remove(edge);
            getMutableIndex(context).removeEdge(edge);
        }
    }

    @SuppressWarnings("unchecked")
    private Edge<Parent, Node> getEdgeForTarget(final Node<?, Edge> parent,
                                                final Node<?, Edge> candidate) {
        final List<Edge> outEdges = parent.getOutEdges();
        if (null != outEdges && !outEdges.isEmpty()) {
            for (Edge<?, Node> outEdge : outEdges) {
                if (outEdge.getContent() instanceof Child) {
                    final Node targetNode = outEdge.getTargetNode();
                    if (null != targetNode && targetNode.equals(candidate)) {
                        return (Edge<Parent, Node>) outEdge;
                    }
                }
            }
        }
        return null;
    }

    protected CommandResult<RuleViolation> check(final GraphCommandExecutionContext context) {
        getParent(context);
        return GraphCommandResultBuilder.SUCCESS;
    }

    @Override
    public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext context) {
        final SetChildrenCommand undoCommand = new SetChildrenCommand(getParent(context),
                                                                      getCandidates(context));
        return undoCommand.execute(context);
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

    public Node<?, Edge> getParent() {
        return parent;
    }

    public Collection<Node<?, Edge>> getCandidates() {
        return candidates;
    }

    private static <T> T checkNotNull(String objName, T obj) {
        return Objects.requireNonNull(obj, "Parameter named '" + objName + "' should be not null!");
    }

    @Override
    public String toString() {
        return "RemoveChildCommand [parent=" + parentUUID + ", candidates=" + candidateUUIDs + "]";
    }
}
