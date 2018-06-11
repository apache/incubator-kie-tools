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

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

/**
 * Update an existing {@link ControlPoint} position on a given {@link Edge}.
 */
public class UpdateControlPointPositionCommand extends AbstractCanvasGraphCommand {

    private final Edge candidate;
    private final ControlPoint controlPoint;
    private final Point2D position;

    public UpdateControlPointPositionCommand(final Edge candidate,
                                             final ControlPoint controlPoint,
                                             final Point2D position) {
        this.candidate = candidate;
        this.controlPoint = controlPoint;
        this.position = position;
    }

    @Override
    protected Command<GraphCommandExecutionContext, RuleViolation> newGraphCommand(final AbstractCanvasHandler context) {
        return new org.kie.workbench.common.stunner.core.graph.command.impl.UpdateControlPointPositionCommand(candidate, controlPoint, position);
    }

    @Override
    protected Command<AbstractCanvasHandler, CanvasViolation> newCanvasCommand(final AbstractCanvasHandler context) {
        return new UpdateCanvasControlPointPositionCommand(candidate, controlPoint, position);
    }

    public Edge getCandidate() {
        return candidate;
    }

    public ControlPoint getControlPoint() {
        return controlPoint;
    }

    public Point2D getPosition() {
        return position;
    }
}