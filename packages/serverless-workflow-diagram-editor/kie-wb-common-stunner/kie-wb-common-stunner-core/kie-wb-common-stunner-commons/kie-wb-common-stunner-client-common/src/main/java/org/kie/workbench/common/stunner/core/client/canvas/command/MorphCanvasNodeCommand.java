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
import java.util.Optional;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.shape.EdgeShape;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.util.ShapeUtils;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.definition.Definition;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;
import org.kie.workbench.common.stunner.core.graph.content.view.View;

import static org.kie.workbench.common.stunner.core.graph.util.GraphUtils.getChildNodes;
import static org.kie.workbench.common.stunner.core.graph.util.GraphUtils.getDockParent;
import static org.kie.workbench.common.stunner.core.graph.util.GraphUtils.getDockedNodes;

public class MorphCanvasNodeCommand extends AbstractCanvasCommand {

    private Node<? extends Definition<?>, Edge> candidate;
    private String shapeSetId;

    public MorphCanvasNodeCommand(final Node<? extends Definition<?>, Edge> candidate,
                                  final String shapeSetId) {
        this.candidate = candidate;
        this.shapeSetId = shapeSetId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
        final Optional<Node> parentOptional = getParent();
        final Optional<Node> dockParentOptional = getDockParent(candidate);
        final List<Node> dockedNodes = getDockedNodes(candidate);
        final List<Node> childNodes = getChildNodes(candidate);

        // Removing parent from morphed node
        if (dockParentOptional.isPresent()) {
            context.undock(dockParentOptional.get(), candidate);
        } else {
            parentOptional.ifPresent(parent -> context.removeChild(parent, candidate));
        }

        //Remove docked nodes
        dockedNodes.stream().forEach(node -> context.undock(candidate, node));

        //Remove child
        childNodes.stream().forEach(node -> context.removeChild(candidate, node));

        // Deregister the existing shape.
        context.deregister(candidate);

        // Register the shape for the new morphed element.
        context.register(shapeSetId, candidate);

        //Adding the Docked nodes
        dockedNodes.stream().forEach(node -> context.dock(candidate, node));

        //Adding the child
        childNodes.stream().forEach(node -> context.addChild(candidate, node));

        // Adding parent to the new morphed node
        if (dockParentOptional.isPresent()) {
            context.dock(dockParentOptional.get(), candidate);
        } else {
            parentOptional.ifPresent(parent -> context.addChild(parent, candidate));
        }

        //Update connections.
        updateConnectionEdges(context, candidate);

        // Update the new shape.
        context.applyElementMutation(candidate, MutationContext.STATIC);

        return buildResult();
    }

    private void updateConnectionEdges(AbstractCanvasHandler context, Node<? extends Definition<?>, Edge> candidate) {
        // Update incoming edges for the new shape
        Optional.ofNullable(candidate.getInEdges())
                .ifPresent(edges -> edges.stream()
                        .filter(this::isViewEdge)
                        .forEach(edge -> updateConnections(context, edge, edge.getSourceNode(), candidate)));

        // Update outgoing edges for the new shape.
        Optional.ofNullable(candidate.getOutEdges())
                .ifPresent(edges -> edges.stream()
                        .filter(this::isViewEdge)
                        .forEach(edge -> updateConnections(context, edge, candidate, edge.getTargetNode())));
        ShapeUtils.moveViewConnectorsToTop(context, candidate);
    }

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
        return execute(context);
    }

    @SuppressWarnings("unchecked")
    private void updateConnections(final AbstractCanvasHandler context,
                                   final Edge edge,
                                   final Node sourceNode,
                                   final Node targetNode) {
        if (null != edge && null != sourceNode && null != targetNode) {
            final EdgeShape edgeShape = (EdgeShape) context.getCanvas().getShape(edge.getUUID());
            final Shape sourceNodeShape = context.getCanvas().getShape(sourceNode.getUUID());
            final Shape targetNodeShape = context.getCanvas().getShape(targetNode.getUUID());
            edgeShape.applyConnections(edge,
                                       sourceNodeShape.getShapeView(),
                                       targetNodeShape.getShapeView(),
                                       MutationContext.STATIC);
        }
    }

    private Optional<Node> getParent() {
        List<Edge> inEdges = candidate.getInEdges();
        if (null != inEdges && !inEdges.isEmpty()) {
            for (final Edge edge : inEdges) {
                if (isChildEdge(edge) || isDockEdge(edge)) {
                    return Optional.ofNullable(edge.getSourceNode());
                }
            }
        }
        return Optional.empty();
    }

    private boolean isChildEdge(final Edge edge) {
        return edge.getContent() instanceof Child;
    }

    private boolean isDockEdge(final Edge edge) {
        return edge.getContent() instanceof Dock;
    }

    private boolean isViewEdge(final Edge edge) {
        return edge.getContent() instanceof View;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [parent=" + toUUID(getParent().orElse(null)) + "," +
                "candidate=" + toUUID(candidate) + "," +
                "shapeSet=" + shapeSetId + "]";
    }
}
