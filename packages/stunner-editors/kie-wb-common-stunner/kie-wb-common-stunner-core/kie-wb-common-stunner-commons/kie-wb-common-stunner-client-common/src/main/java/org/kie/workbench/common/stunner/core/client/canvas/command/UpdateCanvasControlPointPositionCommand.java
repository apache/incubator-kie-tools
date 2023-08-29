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

import java.util.Arrays;
import java.util.stream.Stream;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.util.ControlPointValidations;

import static org.kie.workbench.common.stunner.core.client.canvas.command.AddCanvasControlPointCommand.consumeControlPoints;
import static org.kie.workbench.common.stunner.core.client.canvas.command.AddCanvasControlPointCommand.getControlPoints;

/**
 * Update a given {@link ControlPoint} position on Canvas.
 */
public class UpdateCanvasControlPointPositionCommand extends AbstractCanvasCommand {

    private final Edge edge;
    private final ControlPoint[] controlPoints;
    private ControlPoint[] oldControlPoints;

    public UpdateCanvasControlPointPositionCommand(final Edge edge,
                                                   final ControlPoint[] controlPoints) {
        this.edge = edge;
        this.controlPoints = controlPoints;
        // Actual control points must be cloned at this point, before execution.
        oldControlPoints = Stream.of(getControlPoints(edge))
                .map(ControlPoint::copy)
                .toArray(ControlPoint[]::new);
    }

    @Override
    public CommandResult<CanvasViolation> allow(final AbstractCanvasHandler context) {
        ControlPointValidations.checkUpdateControlPoint(getControlPoints(edge),
                                                        controlPoints);
        return CanvasCommandResultBuilder.SUCCESS;
    }

    @Override
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
        allow(context);
        consumeControlPoints(context,
                             edge,
                             view -> view.updateControlPoints(controlPoints));
        return buildResult();
    }

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
        return new UpdateCanvasControlPointPositionCommand(edge, oldControlPoints).execute(context);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [edge=" + toUUID(edge) + "," +
                "controlPoints=" + Arrays.toString(controlPoints) + "]";
    }
}