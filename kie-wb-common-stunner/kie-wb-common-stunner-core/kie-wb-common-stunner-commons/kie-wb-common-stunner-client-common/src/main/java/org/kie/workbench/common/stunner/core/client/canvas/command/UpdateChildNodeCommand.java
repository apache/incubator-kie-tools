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

package org.kie.workbench.common.stunner.core.client.canvas.command;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.impl.AbstractCompositeCommand;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;

public class UpdateChildNodeCommand extends AbstractCanvasCompositeCommand {

    private final Node parent;
    private final Node candidate;

    public UpdateChildNodeCommand(final Node parent,
                                  final Node candidate) {
        this.parent = parent;
        this.candidate = candidate;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected AbstractCompositeCommand<AbstractCanvasHandler, CanvasViolation> initialize(final AbstractCanvasHandler context) {
        super.initialize(context);
        final List<Edge<?, Node<?, Edge>>> candidateInEdges = candidate.getInEdges();
        final List<Edge<?, Node<?, Edge>>> candidateOutEdges = candidate.getOutEdges();
        final Optional<Edge<?, Node<?, Edge>>> currentParentEdge = candidateInEdges.stream()
                .filter(isDifferentParent())
                .findAny();
        final Optional<Edge<?, Node<?, Edge>>> currentDockEdge = candidateInEdges.stream()
                .filter(e -> e.getContent() instanceof Dock)
                .findAny();
        final Set<Edge> dockedParentEdges =
                candidateOutEdges.stream()
                        .filter(e -> e.getContent() instanceof Dock)
                        .map(Edge::getTargetNode)
                        .flatMap(node -> node.getInEdges().stream())
                        .filter(isDifferentParent())
                        .collect(Collectors.toSet());

        // Remove current parent for candidate, if any.
        currentParentEdge.ifPresent(e -> addCommand(new RemoveChildCommand(e.getSourceNode(), candidate)));

        // If candidate is docked, un-dock it.
        currentDockEdge.ifPresent(e -> addCommand(new UnDockNodeCommand(e.getSourceNode(), candidate)));

        // Set new parent for the candidate, if necessary.
        currentParentEdge.ifPresent(e -> addCommand(new SetChildNodeCommand(parent, candidate)));

        // Remove current parent for nodes docked to the candidate and Undock them
        // Set new parent for the nodes docked on candidate and Dock them
        dockedParentEdges.forEach(e -> {
            addCommand(new RemoveChildCommand(e.getSourceNode(), e.getTargetNode()));
            addCommand(new UnDockNodeCommand(candidate, e.getTargetNode()));
            addCommand(new SetChildNodeCommand(parent, e.getTargetNode()));
            addCommand(new DockNodeCommand(candidate, e.getTargetNode()));
        });
        return this;
    }

    private Predicate<Edge> isDifferentParent() {
        return e -> e.getContent() instanceof Child &&
                !parent.equals(e.getSourceNode());
    }

    public Node getParent() {
        return parent;
    }

    public Node getCandidate() {
        return candidate;
    }

    @Override
    public String toString() {
        return getClass().getName()
                + " [parent=" + getUUID(parent)
                + " [candidate=" + getUUID(candidate)
                + " {" + super.toString() + "}";
    }
}
