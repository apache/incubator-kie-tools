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


package org.kie.workbench.common.stunner.core.client.canvas.command;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.impl.AbstractCompositeCommand;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;
import org.kie.workbench.common.stunner.core.graph.content.view.View;
import org.kie.workbench.common.stunner.core.graph.util.GraphUtils;

public class UpdateDockNodeCommand extends AbstractCanvasCompositeCommand {

    private final Node parent;
    private final Node candidate;
    private boolean adjustPosition;

    public UpdateDockNodeCommand(final Node parent,
                                 final Node candidate) {
        this(parent, candidate, false);
    }

    public UpdateDockNodeCommand(final Node parent,
                                 final Node candidate, boolean adjustPosition) {
        this.parent = parent;
        this.candidate = candidate;
        this.adjustPosition = adjustPosition;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected AbstractCompositeCommand<AbstractCanvasHandler, CanvasViolation> initialize(final AbstractCanvasHandler context) {
        super.initialize(context);
        // Obtain the edge instance for the docking relationship as well.
        final Optional<Edge<?, Node>> dockEdge = getEdge(candidate.getInEdges(),
                                                         e -> e.getContent() instanceof Dock);
        // Obtain the candidate's parent too.
        final Optional<Edge<?, Node>> childEdge = getEdge(candidate.getInEdges(),
                                                          e -> e.getContent() instanceof Child);
        // Obtain the parent for the target node for docking (the 'target' one).
        final Element<?> parentParent = getParentOfParent(context, parent);

        // Let's check if the current candidate has some parent, because
        // the docking operation implies adding the 'candidate' in the same parent node for 'target'.
        final boolean mustUpdateParent = Objects.nonNull(parentParent) &&
                childEdge.filter(e -> !parentParent.equals(e.getSourceNode())).isPresent();

        // Dock the candidate into the parent node, and update candidate's parent, if necessary,
        // to match the parent for 'target'.
        if (mustUpdateParent) {
            addCommand(new UpdateChildrenCommand((Node) parentParent, candidate));
        } else {
            // UnDock any existing source node from the candidate.
            // The UpdateChildNodeCommand already peforms the UnDockNodeCommand
            dockEdge.ifPresent(e -> addCommand(new UnDockNodeCommand(e.getSourceNode(),
                                                                     candidate)));
        }

        // Finally, dock the candidate into the parent.
        addCommand(new DockNodeCommand(parent, candidate, adjustPosition));
        return this;
    }

    private Element<?> getParentOfParent(AbstractCanvasHandler context, Node parent) {
        final Element parentOfParent = GraphUtils.getParent(parent);
        return (GraphUtils.isRootNode((Element<? extends View<?>>) parentOfParent, context.getGraphIndex().getGraph()) ?
                context.getGraphIndex().getNode(context.getDiagram().getMetadata().getCanvasRootUUID()) :
                parentOfParent);
    }

    protected Optional<Edge<?, Node>> getEdge(final List<Edge<?, Node>> edges,
                                              final Predicate<Edge> predicate) {
        if (null != edges) {
            return edges.stream()
                    .filter(predicate)
                    .findAny();
        }
        return Optional.empty();
    }

    public Node getParent() {
        return parent;
    }

    public Node getCandidate() {
        return candidate;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [parent=" + AbstractCanvasCommand.toUUID(parent) + "," +
                "candidate=" + AbstractCanvasCommand.toUUID(candidate) + "]";
    }
}
