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

import java.util.List;
import java.util.Objects;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolationImpl;
import org.kie.workbench.common.stunner.core.client.util.ShapeUtils;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.rule.violations.RuleViolationImpl;

public class AddCanvasControlPointCommand extends AbstractCanvasCommand {

    private final Edge candidate;
    private final ControlPoint[] controlPoints;
    private Boolean allowed;

    public AddCanvasControlPointCommand(final Edge candidate, final ControlPoint... controlPoints) {
        this.candidate = candidate;
        this.controlPoints = controlPoints;
    }

    @Override
    public CommandResult<CanvasViolation> allow(AbstractCanvasHandler context) {
        ShapeUtils.hideControlPoints(candidate, context);
        List<ControlPoint> addedControlPoints = ShapeUtils.addControlPoints(candidate,
                                                                            context,
                                                                            this.controlPoints);
        if (addedControlPoints.stream().map(ControlPoint::getIndex).anyMatch(Objects::isNull)) {
            return new CanvasCommandResultBuilder()
                    .setType(CommandResult.Type.ERROR)
                    .addViolation(new CanvasViolationImpl.Builder().build(new RuleViolationImpl("Control Point out of connector")))
                    .build();
        }
        ShapeUtils.showControlPoints(candidate, context);
        allowed = Boolean.TRUE;
        return buildResult();
    }

    @Override
    public CommandResult<CanvasViolation> execute(AbstractCanvasHandler context) {
        if (Objects.isNull(allowed)) {
            CommandResult<CanvasViolation> commandResult = allow(context);
            if (CommandUtils.isError(commandResult)) {
                return commandResult;
            }
        }
        allowed = null;
        //in this case, canvas command was executed on #allow method.
        return buildResult();
    }

    @Override
    public CommandResult<CanvasViolation> undo(AbstractCanvasHandler context) {
        return new DeleteCanvasControlPointCommand(candidate, controlPoints).execute(context);
    }

    public ControlPoint[] getControlPoints() {
        return controlPoints;
    }
}