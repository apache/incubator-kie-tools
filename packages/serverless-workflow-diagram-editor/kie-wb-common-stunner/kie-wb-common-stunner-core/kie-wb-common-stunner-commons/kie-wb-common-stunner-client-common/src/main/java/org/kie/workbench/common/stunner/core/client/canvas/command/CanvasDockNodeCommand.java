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
import java.util.Optional;
import java.util.function.Consumer;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolationImpl;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.relationship.Child;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.rule.violations.DockingRuleViolation;

/**
 * Docks a node shape into thee parent's one in the canvas context.
 */
public class CanvasDockNodeCommand extends AbstractCanvasCommand {

    private final Node parent;
    private final Node candidate;
    private final Optional<Consumer<Point2D>> dockedPositionCallback;

    public CanvasDockNodeCommand(final Node parent,
                                 final Node candidate) {
        this(parent, candidate, null);
    }

    public CanvasDockNodeCommand(final Node parent,
                                 final Node candidate,
                                 final Consumer<Point2D> dockedPositionCallback) {
        this.parent = parent;
        this.candidate = candidate;
        this.dockedPositionCallback = Optional.ofNullable(dockedPositionCallback);
    }

    @Override
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
        // For the canvas side, docking the candidate shape into the parent one implies
        // that it has to be set as a child as well.
        // So first remove the current parent for the candidate shape, if any.
        getCandidate().getInEdges().stream()
                .filter(e -> e.getContent() instanceof Child)
                .findAny()
                .ifPresent(e -> context.removeChild(e.getSourceNode(),
                                                    candidate));

        ShapeView shapeView = context.getCanvas().getShape(candidate.getUUID()).getShapeView();
        Point2D currentPosition = new Point2D(shapeView.getShapeX(), shapeView.getShapeY());

        // Update both shape view's attributes.
        context.applyElementMutation(parent,
                                     MutationContext.STATIC);
        context.applyElementMutation(candidate,
                                     MutationContext.STATIC);

        // Dock the candidate shape into the parent one.
        if (!context.dock(parent, candidate)) {
            return new CanvasCommandResultBuilder()
                    .addViolation(CanvasViolationImpl.Builder
                                          .build(new DockingRuleViolation(parent.getUUID(), candidate.getUUID())))
                    .build();
        }

        Point2D dockLocation = new Point2D(shapeView.getShapeX(), shapeView.getShapeY());
        if (!Objects.equals(currentPosition, dockLocation)) {
            dockedPositionCallback.ifPresent(callback -> callback.accept(dockLocation));
        }

        return buildResult();
    }

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
        return new CanvasUndockNodeCommand(parent, candidate).execute(context);
    }

    public Node getParent() {
        return parent;
    }

    public Node<?, Edge> getCandidate() {
        return candidate;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [parent=" + getParent() + "," +
                "candidate=" + getCandidate() + "]";
    }
}