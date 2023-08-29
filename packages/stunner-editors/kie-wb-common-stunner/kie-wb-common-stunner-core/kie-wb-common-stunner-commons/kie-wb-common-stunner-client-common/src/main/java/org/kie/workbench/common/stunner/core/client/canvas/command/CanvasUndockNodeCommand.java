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

import java.util.Objects;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;

/**
 * Removes the parent-dock relationship between two nodes in the canvas context.
 */
public class CanvasUndockNodeCommand extends AbstractCanvasCommand {

    private final Node parent;
    private final Node child;

    public CanvasUndockNodeCommand(final Node parent,
                                   final Node child) {
        this.parent = parent;
        this.child = child;
    }

    @Override
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
        if (Objects.isNull(parent) || Objects.isNull(child)) {
            throw new IllegalArgumentException("Parent and child should not be null");
        }
        context.undock(parent, child);
        // on canvas side dock removes the parent that was in which it was docked
        // so, it is necessary to add the current parent
        getChild().getInEdges().stream()
                .filter(e -> e.getContent() instanceof Child)
                .findAny()
                .ifPresent(e -> context.addChild(e.getSourceNode(), child));

        // Check parent node is not being removed from the graph.
        if (existNode(parent, context)) {
            context.applyElementMutation(parent, MutationContext.STATIC);
        }
        // Check child node is not being removed from the graph.
        if (existNode(child, context)) {
            context.applyElementMutation(child, MutationContext.STATIC);
        }

        return buildResult();
    }

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
        return new CanvasDockNodeCommand(parent,
                                         child).execute(context);
    }

    public Node getParent() {
        return parent;
    }

    public Node<?, Edge> getChild() {
        return child;
    }

    private boolean existNode(final Node node,
                              final AbstractCanvasHandler context) {
        return null != context.getGraphIndex().getNode(node.getUUID());
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [parent=" + toUUID(parent) + "," +
                "candidate=" + toUUID(child) + "]";
    }
}
