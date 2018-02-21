/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.util.Objects;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.util.ShapeUtils;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.AbstractCompositeCommand;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPointImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;

/**
 * Update a given {@link ControlPoint} position on Canvas.
 */
public class UpdateCanvasControlPointPositionCommand extends AbstractCanvasCompositeCommand {

    private final Edge edge;
    private final ControlPoint controlPoint;
    private final Point2D position;
    private final ControlPoint positionedControlPoint;

    public UpdateCanvasControlPointPositionCommand(final Edge edge,
                                                   final ControlPoint controlPoint,
                                                   final Point2D position) {
        this.edge = edge;
        this.controlPoint = controlPoint;
        this.position = position;
        this.positionedControlPoint = getUpdatedControlPoint(position);
    }

    @Override
    protected AbstractCompositeCommand<AbstractCanvasHandler, CanvasViolation> initialize(AbstractCanvasHandler context) {
        if (ShapeUtils.getControlPoints(edge, context).stream().anyMatch(cp -> Objects.equals(cp.getLocation(), position))) {
            //skip canvas commands in case the control point is already on the position
            return this;
        }
        addCommand(new DeleteCanvasControlPointCommand(edge, controlPoint));
        addCommand(new AddCanvasControlPointCommand(edge, positionedControlPoint));
        return this;
    }

    @Override
    protected void ensureInitialized(AbstractCanvasHandler context) {
        initialize(context);
    }

    @Override
    public CommandResult<CanvasViolation> undo(AbstractCanvasHandler context) {
        return newUndoCommand().execute(context);
    }

    protected CompositeCommand<AbstractCanvasHandler, CanvasViolation> newUndoCommand() {
        return new CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation>()
                .addCommand(new DeleteCanvasControlPointCommand(edge, positionedControlPoint))
                .addCommand(new AddCanvasControlPointCommand(edge, controlPoint))
                .forward()
                .build();
    }

    private ControlPointImpl getUpdatedControlPoint(Point2D position) {
        return new ControlPointImpl(position, controlPoint.getIndex());
    }
}