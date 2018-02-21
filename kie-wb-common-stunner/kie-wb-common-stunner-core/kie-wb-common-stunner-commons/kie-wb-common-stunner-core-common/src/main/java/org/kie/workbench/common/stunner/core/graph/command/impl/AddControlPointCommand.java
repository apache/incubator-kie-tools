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

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.content.HasControlPoints;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

/**
 * A Command which adds control points into and an edge.
 */
@Portable
public class AddControlPointCommand extends AbstractControlPointCommand {

    public AddControlPointCommand() {
        this(null, null);
    }

    public AddControlPointCommand(final Edge edge,
                                  final ControlPoint... controlPoints) {
        super(edge, controlPoints);
    }

    @Override
    protected CommandResult<RuleViolation> check(GraphCommandExecutionContext context) {
        if (edge.getContent() instanceof HasControlPoints && validateControlPoints()) {
            return GraphCommandResultBuilder.SUCCESS;
        }
        return GraphCommandResultBuilder.FAILED;
    }

    private boolean validateControlPoints() {
        return !getControlPointList().stream().map(ControlPoint::getIndex).anyMatch(Objects::isNull);
    }

    @Override
    public CommandResult<RuleViolation> execute(GraphCommandExecutionContext context) {
        if (checkExistingControlPoints(getEdgeContent().getControlPoints())) {
            //skipping in case adding already existing control points
            return GraphCommandResultBuilder.SUCCESS;
        }

        CommandResult<RuleViolation> allowResult = allow(context);
        if (CommandUtils.isError(allowResult)) {
            return allowResult;
        }

        HasControlPoints edgeContent = getEdgeContent();
        if (Objects.isNull(edgeContent.getControlPoints())) {
            edgeContent.setControlPoints(new LinkedList<>());
        }

        //add on the right index position on the list
        getControlPointList().stream().forEach(cp -> {
            //the effective index should not consider the head point that is a control point on the connector
            int effectiveIndex = cp.getIndex() - 1;
            if (edgeContent.getControlPoints().size() > effectiveIndex) {
                edgeContent.getControlPoints().add(effectiveIndex, cp);
            } else {
                edgeContent.getControlPoints().add(cp);
            }
        });

        //update index control points
        updateControlPointsIndex(edgeContent.getControlPoints());

        return GraphCommandResultBuilder.SUCCESS;
    }

    private boolean checkExistingControlPoints(List<ControlPoint> currentControlPoints) {
        return Objects.nonNull(currentControlPoints) && !currentControlPoints.isEmpty() &&
                getControlPointList().stream().allMatch(cp -> getEdgeContent().getControlPoints().contains(cp));
    }

    @Override
    public CommandResult<RuleViolation> undo(GraphCommandExecutionContext context) {
        return newUndoCommand().execute(context);
    }

    @Override
    protected DeleteControlPointCommand newUndoCommand() {
        return new DeleteControlPointCommand(edge, controlPoints);
    }
}