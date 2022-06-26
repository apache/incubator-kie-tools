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
package org.kie.workbench.common.stunner.core.graph.command.impl;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.content.HasControlPoints;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

import static org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder.SUCCESS;
import static org.kie.workbench.common.stunner.core.graph.util.ControlPointValidations.checkUpdateControlPoint;

@Portable
public class UpdateControlPointPositionCommand extends AbstractControlPointCommand {

    private final ControlPoint[] controlPoints;
    private transient ControlPoint[] oldControlPoints;

    public UpdateControlPointPositionCommand(final @MapsTo("edgeUUID") String edgeUUID,
                                             final @MapsTo("controlPoints") ControlPoint[] controlPoints) {
        super(edgeUUID);
        this.controlPoints = PortablePreconditions.checkNotNull("controlPoints", controlPoints);
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