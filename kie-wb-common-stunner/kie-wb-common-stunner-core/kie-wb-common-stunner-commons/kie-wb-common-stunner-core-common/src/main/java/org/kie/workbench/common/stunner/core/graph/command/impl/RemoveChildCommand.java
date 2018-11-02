/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.graph.command.impl;

import java.util.List;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Parent;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

/**
 * Removes the parent-child relationship  ( Child ) between two nodes.
 */
@Portable
public class RemoveChildCommand extends AbstractGraphCommand {

    private final String parentUUID;
    private final String candidateUUID;
    private transient Node<?, Edge> parent;
    private transient Node<?, Edge> candidate;

    public RemoveChildCommand(final @MapsTo("parentUUID") String parentUUID,
                              final @MapsTo("candidateUUID") String candidateUUID) {
        this.parentUUID = PortablePreconditions.checkNotNull("parentUUID",
                                                             parentUUID);
        this.candidateUUID = PortablePreconditions.checkNotNull("candidateUUID",
                                                                candidateUUID);
    }

    public RemoveChildCommand(final Node<?, Edge> parent,
                              final Node<?, Edge> candidate) {
        this(parent.getUUID(),
             candidate.getUUID());
        this.parent = parent;
        this.candidate = candidate;
    }

    @Override
    public CommandResult<RuleViolation> execute(final GraphCommandExecutionContext context) {
        final CommandResult<RuleViolation> results = allow(context);
        if (!results.getType().equals(CommandResult.Type.ERROR)) {
            final Node<?, Edge> parent = getParent(context);
            final Node<?, Edge> candidate = getCandidate(context);
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
        return results;
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
        getCandidate(context);
        return GraphCommandResultBuilder.SUCCESS;
    }

    @Override
    public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext context) {
        final SetChildNodeCommand undoCommand = new SetChildNodeCommand(getParent(context),
                                                                        getCandidate(context));
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
    protected Node<?, Edge> getCandidate(final GraphCommandExecutionContext context) {
        if (null == candidate) {
            candidate = getNodeNotNull(context,
                                       candidateUUID);
        }
        return candidate;
    }

    public Node<?, Edge> getParent() {
        return parent;
    }

    public Node<?, Edge> getCandidate() {
        return candidate;
    }

    @Override
    public String toString() {
        return "RemoveChildCommand [parent=" + parentUUID + ", candidate=" + candidateUUID + "]";
    }
}
