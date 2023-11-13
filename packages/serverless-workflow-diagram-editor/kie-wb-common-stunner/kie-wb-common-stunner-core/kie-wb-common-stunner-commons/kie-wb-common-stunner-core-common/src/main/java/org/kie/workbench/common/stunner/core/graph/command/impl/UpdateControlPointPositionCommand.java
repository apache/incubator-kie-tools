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

import static org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder.SUCCESS;
import static org.kie.workbench.common.stunner.core.graph.util.ControlPointValidations.checkUpdateControlPoint;

public class UpdateControlPointPositionCommand extends AbstractControlPointCommand {

    private final ControlPoint[] controlPoints;
    private transient ControlPoint[] oldControlPoints;

    public UpdateControlPointPositionCommand(final String edgeUUID,
                                             final ControlPoint[] controlPoints) {
        super(edgeUUID);
        this.controlPoints = Objects.requireNonNull(controlPoints, "Parameter named 'controlPoints' should be not null!");
    }

    @Override
    protected CommandResult<RuleViolation> check(final GraphCommandExecutionContext context) {
        checkUpdateControlPoint(getEdgeControlPoints(context).getControlPoints(),
                                controlPoints);
        return GraphCommandResultBuilder.SUCCESS;
    }

    @Override
    public CommandResult<RuleViolation> execute(final GraphCommandExecutionContext context) {
        final HasControlPoints hasControlPoints = getEdgeControlPoints(context);
        oldControlPoints = hasControlPoints.getControlPoints();
        hasControlPoints.setControlPoints(controlPoints);
        return SUCCESS;
    }

    @Override
    public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext context) {
        return new UpdateControlPointPositionCommand(getEdgeUUID(),
                                                     oldControlPoints)
                .execute(context);
    }
}
