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

package org.kie.workbench.common.stunner.core.graph.command.impl;

import java.util.Objects;

import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.content.HasControlPoints;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

import static org.kie.workbench.common.stunner.core.graph.util.ControlPointValidations.checkAddControlPoint;

/**
 * A Command which adds control points into and an edge.
 */
public class AddControlPointCommand extends AbstractControlPointCommand {

    private final ControlPoint controlPoint;
    private final int index;

    public AddControlPointCommand(final String edgeUUID,
                                  final ControlPoint controlPoint,
                                  final int index) {
        super(edgeUUID);
        this.controlPoint = Objects.requireNonNull(controlPoint, "Parameter named 'controlPoint' should be not null!");
        this.index = index;
    }

    @Override
    protected CommandResult<RuleViolation> check(final GraphCommandExecutionContext context) {
        checkAddControlPoint(getEdgeControlPoints(context).getControlPoints(),
                             controlPoint,
                             index);
        return GraphCommandResultBuilder.SUCCESS;
    }

    @Override
    public CommandResult<RuleViolation> execute(final GraphCommandExecutionContext context) {
        check(context);
        final HasControlPoints edgeControlPoints = getEdgeControlPoints(context);
        final int size = Objects.isNull(edgeControlPoints.getControlPoints()) ? 0 :
                edgeControlPoints.getControlPoints().length;
        final ControlPoint[] cps = new ControlPoint[size + 1];
        for (int i = 0; i < size + 1; i++) {
            if (i < index) {
                cps[i] = edgeControlPoints.getControlPoints()[i];
            } else if (i == index) {
                cps[i] = controlPoint;
            } else {
                cps[i] = edgeControlPoints.getControlPoints()[i - 1];
            }
        }
        edgeControlPoints.setControlPoints(cps);
        return GraphCommandResultBuilder.SUCCESS;
    }

    @Override
    public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext context) {
        return new DeleteControlPointCommand(getEdgeUUID(), index).execute(context);
    }
}
