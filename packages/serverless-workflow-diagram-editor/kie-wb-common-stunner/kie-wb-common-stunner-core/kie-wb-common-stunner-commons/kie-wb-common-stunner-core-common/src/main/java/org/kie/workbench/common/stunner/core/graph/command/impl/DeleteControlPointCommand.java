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

import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.content.HasControlPoints;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

import static org.kie.workbench.common.stunner.core.graph.util.ControlPointValidations.checkDeleteControlPoint;

/**
 * A Graph command that deletes {@link ControlPoint} from a given {@link Edge}.
 */
public class DeleteControlPointCommand extends AbstractControlPointCommand {

    private final int index;
    private transient ControlPoint deletedControlPoint;

    public DeleteControlPointCommand(final String edgeUUID,
                                     final int index) {
        super(edgeUUID);
        this.index = index;
    }

    @Override
    protected CommandResult<RuleViolation> check(final GraphCommandExecutionContext context) {
        checkDeleteControlPoint(getEdgeControlPoints(context).getControlPoints(),
                                index);
        return GraphCommandResultBuilder.SUCCESS;
    }

    @Override
    public CommandResult<RuleViolation> execute(final GraphCommandExecutionContext context) {
        check(context);
        final HasControlPoints edgeControlPoints = getEdgeControlPoints(context);
        final int size = edgeControlPoints.getControlPoints().length;
        final ControlPoint[] cps = new ControlPoint[size - 1];
        for (int i = 0, j = 0; i < size; i++, j++) {
            ControlPoint controlPoint = edgeControlPoints.getControlPoints()[i];
            if (i == index) {
                j--;
                deletedControlPoint = controlPoint;
            } else {
                cps[j] = controlPoint;
            }
        }
        edgeControlPoints.setControlPoints(cps);
        return GraphCommandResultBuilder.SUCCESS;
    }

    @Override
    public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext context) {
        return new AddControlPointCommand(getEdgeUUID(), deletedControlPoint, index).execute(context);
    }
}
