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

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Dock;

/**
 * Deletes a node shape from the canvas.
 */
public class DeleteCanvasNodeCommand extends AbstractCanvasCommand {

    private final Node candidate;
    private final Node parent;

    public DeleteCanvasNodeCommand(final Node candidate) {
        this.candidate = candidate;
        this.parent = getParent(candidate);
    }

    public DeleteCanvasNodeCommand(final Node candidate,
                                   final Node parent) {
        this.candidate = candidate;
        this.parent = parent;
    }

    @Override
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
        if (null != parent) {
            context.removeChild(parent,
                                candidate);
        }
        context.deregister(candidate);
        if (null != parent) {
            context.applyElementMutation(parent,
                                         MutationContext.STATIC);
        }
        return buildResult();
    }

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
        final String ssid = context.getDiagram().getMetadata().getShapeSetId();
        final AbstractCanvasCommand command = createUndoCommand(parent, candidate, ssid);
        return command.execute(context);
    }

    @SuppressWarnings("unchecked")
    public static Node getParent(final Node node) {
        List<Edge> inEdges = null != node ? node.getInEdges() : null;
        if (null != inEdges && !inEdges.isEmpty()) {
            for (final Edge edge : inEdges) {
                if (isChildEdge(edge) || isDockEdge(edge)) {
                    return edge.getSourceNode();
                }
            }
        }
        return null;
    }

    public static boolean isChildEdge(final Edge edge) {
        return edge.getContent() instanceof Child;
    }

    public static boolean isDockEdge(final Edge edge) {
        return edge.getContent() instanceof Dock;
    }

    public Node getCandidate() {
        return candidate;
    }

    protected AbstractCanvasCommand createUndoCommand(final Node parent,
                                                      final Node candidate,
                                                      String ssid) {
        return new AddCanvasChildNodeCommand(parent, candidate, ssid);
    }

    @Override
    public String toString() {
        return getClass().getName() +
                " [parent=" + getUUID(parent) + "," +
                " candidate=" + getUUID(candidate) + "]";
    }
}
