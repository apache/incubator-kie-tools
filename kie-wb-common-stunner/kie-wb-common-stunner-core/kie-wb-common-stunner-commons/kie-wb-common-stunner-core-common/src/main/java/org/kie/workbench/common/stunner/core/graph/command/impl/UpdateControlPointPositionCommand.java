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
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

/**
 * A Command to update a given {@link ControlPoint} position on graph.
 */
@Portable
public class UpdateControlPointPositionCommand extends AbstractControlPointCommand {

    private final Point2D position;
    private transient Point2D oldPosition;

    public UpdateControlPointPositionCommand(final @MapsTo("candidate") Edge candidate,
                                             final @MapsTo("controlPoint") ControlPoint controlPoint,
                                             final @MapsTo("position") Point2D position) {
        super(candidate, controlPoint);
        this.position = PortablePreconditions.checkNotNull("position", position);
    }

    @Override
    protected CommandResult<RuleViolation> check(final GraphCommandExecutionContext context) {
        return checkArguments();
    }

    @Override
    public CommandResult<RuleViolation> execute(final GraphCommandExecutionContext context) {
        if (areArgumentsValid()) {
            final int index = getControlPoint().getIndex();
            oldPosition = Point2D.clone(getControlPoint().getLocation());
            getEdgeContent().getControlPoints().get(index).setLocation(position);
            return GraphCommandResultBuilder.SUCCESS;
        }
        return GraphCommandResultBuilder.FAILED;
    }

    private ControlPoint getControlPoint() {
        return controlPoints[0];
    }

    public Point2D getPosition() {
        return position;
    }

    Point2D getOldPosition() {
        return oldPosition;
    }

    @Override
    protected Command<GraphCommandExecutionContext, RuleViolation> newUndoCommand() {
        return new UpdateControlPointPositionCommand(edge,
                                                     getControlPoint(),
                                                     oldPosition);
    }
}