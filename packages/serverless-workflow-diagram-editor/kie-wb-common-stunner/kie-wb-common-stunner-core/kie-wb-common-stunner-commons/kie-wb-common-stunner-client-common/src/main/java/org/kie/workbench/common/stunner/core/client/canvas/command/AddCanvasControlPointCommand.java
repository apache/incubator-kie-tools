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

import java.util.function.Consumer;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.shape.impl.ConnectorShape;
import org.kie.workbench.common.stunner.core.client.shape.view.HasControlPoints;
import org.kie.workbench.common.stunner.core.client.shape.view.HasManageableControlPoints;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;

import static org.kie.workbench.common.stunner.core.client.util.ShapeUtils.getConnectorShape;
import static org.kie.workbench.common.stunner.core.graph.util.ControlPointValidations.checkAddControlPoint;

public class AddCanvasControlPointCommand extends AbstractCanvasCommand {

    private final Edge candidate;
    private final ControlPoint controlPoint;
    private final int index;

    public AddCanvasControlPointCommand(final Edge candidate,
                                        final ControlPoint controlPoint,
                                        final int index) {
        this.candidate = candidate;
        this.controlPoint = controlPoint;
        this.index = index;
    }

    @Override
    public CommandResult<CanvasViolation> allow(final AbstractCanvasHandler context) {
        checkAddControlPoint(getViewControlPoints(context, candidate),
                             controlPoint,
                             index);
        return CanvasCommandResultBuilder.SUCCESS;
    }

    @Override
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
        allow(context);
        consumeControlPoints(context,
                             candidate,
                             view -> view.addControlPoint(controlPoint, index));
        return buildResult();
    }

    @Override
    public CommandResult<CanvasViolation> undo(AbstractCanvasHandler context) {
        return new DeleteCanvasControlPointCommand(candidate, index).execute(context);
    }

    public ControlPoint getControlPoint() {
        return controlPoint;
    }

    public int getIndex() {
        return index;
    }

    public static ControlPoint[] getControlPoints(final Edge edge) {
        ViewConnector<?> connector = (ViewConnector<?>) edge.getContent();
        return connector.getControlPoints();
    }

    public static void consumeControlPoints(final AbstractCanvasHandler context,
                                            final Edge edge,
                                            final Consumer<HasManageableControlPoints> consumer) {
        final HasManageableControlPoints<?> view = getManageableControlPoints(context, edge);
        final boolean visible = view.areControlsVisible();
        if (visible) {
            view.hideControlPoints();
        }
        consumer.accept(view);
        if (visible) {
            view.showControlPoints(HasControlPoints.ControlPointType.POINTS);
        }
    }

    public static HasManageableControlPoints<?> getManageableControlPoints(final AbstractCanvasHandler context,
                                                                           final Edge candidate) {
        final ConnectorShape shape = getConnectorShape(candidate, context);
        return (HasManageableControlPoints<?>) shape.getShapeView();
    }

    public static ControlPoint[] getViewControlPoints(final AbstractCanvasHandler context,
                                                      final Edge candidate) {
        return getManageableControlPoints(context, candidate).getManageableControlPoints();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [controlPoint=" + controlPoint + "," +
                "index=" + index + "]";
    }
}