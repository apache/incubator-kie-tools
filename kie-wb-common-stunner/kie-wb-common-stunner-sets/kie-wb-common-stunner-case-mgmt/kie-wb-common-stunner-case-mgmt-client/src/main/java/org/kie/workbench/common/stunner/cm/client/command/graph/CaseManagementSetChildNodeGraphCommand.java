/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.cm.client.command.graph;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.command.impl.AbstractGraphCommand;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.impl.EdgeImpl;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.util.UUID;

public class CaseManagementSetChildNodeGraphCommand extends AbstractGraphCommand {

    protected final OptionalInt index;
    protected final Optional<Node> originalParent;
    protected final OptionalInt originalIndex;
    private final Node parent;
    private final Node child;

    public CaseManagementSetChildNodeGraphCommand(final Node parent,
                                                  final Node child,
                                                  final OptionalInt index,
                                                  final Optional<Node> originalParent,
                                                  final OptionalInt originalIndex) {
        this.parent = parent;
        this.child = child;
        this.index = index;
        this.originalParent = originalParent;
        this.originalIndex = originalIndex;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<RuleViolation> execute(final GraphCommandExecutionContext context) {
        final CommandResult<RuleViolation> results = allow(context);
        if (results.getType().equals(CommandResult.Type.ERROR)) {
            return results;
        }

        //Remove existing relationship
        getEdgeForTarget(originalParent,
                         child).ifPresent((e) -> removeRelationship(e,
                                                                    originalParent.get(),
                                                                    child,
                                                                    context));

        //Add new relationship
        addRelationship(parent,
                        child,
                        index,
                        context);

        return results;
    }

    @SuppressWarnings("unchecked")
    private Optional<Edge> getEdgeForTarget(final Optional<Node> parent,
                                            final Node child) {
        return parent.flatMap((p) -> {
            Edge edge = null;
            final List<Edge> outEdges = parent.get().getOutEdges();
            if (!(outEdges == null || outEdges.isEmpty())) {
                for (Edge outEdge : outEdges) {
                    if (outEdge.getContent() instanceof Child) {
                        final Node targetNode = outEdge.getTargetNode();
                        if (child.equals(targetNode)) {
                            edge = outEdge;
                            break;
                        }
                    }
                }
            }
            return Optional.ofNullable(edge);
        });
    }

    @SuppressWarnings("unchecked")
    private void removeRelationship(final Edge e,
                                    final Node parent,
                                    final Node child,
                                    final GraphCommandExecutionContext context) {
        e.setSourceNode(null);
        e.setTargetNode(null);
        parent.getOutEdges().remove(e);
        child.getInEdges().remove(e);
        getMutableIndex(context).removeEdge(e);
    }

    @SuppressWarnings("unchecked")
    private void addRelationship(final Node parent,
                                 final Node child,
                                 final OptionalInt index,
                                 final GraphCommandExecutionContext context) {
        final String uuid = UUID.uuid();
        final Edge<Child, Node> edge = new EdgeImpl<>(uuid);
        edge.setContent(new Child());
        edge.setSourceNode(parent);
        edge.setTargetNode(child);
        parent.getOutEdges().add(index.orElseGet(() -> parent.getOutEdges().size()), edge);
        child.getInEdges().add(edge);
        getMutableIndex(context).addEdge(edge);
    }

    @SuppressWarnings("unchecked")
    protected CommandResult<RuleViolation> check(final GraphCommandExecutionContext context) {
        final Collection<RuleViolation> violations = evaluate(context,
                                                              contextBuilder -> contextBuilder.containment(parent,
                                                                                                           child));
        return new GraphCommandResultBuilder(violations).build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext context) {
        //Remove existing relationship
        getEdgeForTarget(Optional.of(parent),
                         child).ifPresent((e) -> removeRelationship(e,
                                                                    parent,
                                                                    child,
                                                                    context));

        //Add new relationship
        originalParent.ifPresent((p) -> addRelationship(originalParent.get(),
                                                        child,
                                                        originalIndex,
                                                        context));

        return GraphCommandResultBuilder.SUCCESS;
    }
}
