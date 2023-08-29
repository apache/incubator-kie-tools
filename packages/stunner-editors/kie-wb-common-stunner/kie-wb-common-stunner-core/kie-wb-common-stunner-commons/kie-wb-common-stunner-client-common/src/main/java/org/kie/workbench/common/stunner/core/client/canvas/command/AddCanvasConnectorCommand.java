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

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.shape.MutationContext;
import org.kie.workbench.common.stunner.core.client.shape.view.HasManageableControlPoints;
import org.kie.workbench.common.stunner.core.client.util.ShapeUtils;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.ViewConnector;

import static org.kie.workbench.common.stunner.core.client.util.ShapeUtils.applyConnections;

/**
 * Adds a new connector shape into the canvas and updates its connections.
 */
public class AddCanvasConnectorCommand extends AbstractCanvasCommand {

    private final Edge candidate;
    private final String shapeSetId;

    public AddCanvasConnectorCommand(final Edge candidate,
                                     final String shapeSetId) {
        this.candidate = candidate;
        this.shapeSetId = shapeSetId;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
        context.register(shapeSetId, candidate);
        if (candidate.getContent() instanceof ViewConnector) {
            final HasManageableControlPoints<?> view =
                    (HasManageableControlPoints<?>) ShapeUtils.getConnectorShape(candidate, context).getShapeView();
            final ControlPoint[] controlPoints = ((ViewConnector) candidate.getContent()).getControlPoints();
            if (null != controlPoints) {
                for (int i = 0; i < controlPoints.length; i++) {
                    view.addControlPoint(controlPoints[i], i);
                }
            }
        }
        applyConnections(candidate, context, MutationContext.STATIC);
        context.applyElementMutation(candidate, MutationContext.STATIC);
        final Node source = candidate.getSourceNode();
        if (null != source) {
            context.notifyCanvasElementUpdated(source);
        }
        return buildResult();
    }

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
        return new DeleteCanvasConnectorCommand(candidate).execute(context);
    }

    public Edge getCandidate() {
        return candidate;
    }

    public String getShapeSetId() {
        return shapeSetId;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [candidate=" + toUUID(candidate) + "," +
                "shapeSet=" + getShapeSetId() + "]";
    }
}
