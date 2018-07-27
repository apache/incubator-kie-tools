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
import java.util.Objects;

import org.jboss.errai.common.client.api.annotations.MapsTo;
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

    public AddControlPointCommand(final @MapsTo("edge") Edge edge,
                                  final @MapsTo("controlPoints") ControlPoint... controlPoints) {
        super(edge, controlPoints);
    }

    @Override
    protected CommandResult<RuleViolation> check(final GraphCommandExecutionContext context) {
        return validateControlPoints() ?
                GraphCommandResultBuilder.SUCCESS :
                GraphCommandResultBuilder.FAILED;
    }

    private boolean validateControlPoints() {
        return !getControlPointList().stream().map(ControlPoint::getIndex).anyMatch(Objects::isNull);
    }

    @Override
    public CommandResult<RuleViolation> execute(final GraphCommandExecutionContext context) {
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
            if (edgeContent.getControlPoints().size() > cp.getIndex()) {
                edgeContent.getControlPoints().add(cp.getIndex(), cp);
            } else {
                edgeContent.getControlPoints().add(cp);
            }
        });

        //update index control points
        updateControlPointsIndex(edgeContent.getControlPoints());

        return GraphCommandResultBuilder.SUCCESS;
    }

    @Override
    protected DeleteControlPointCommand newUndoCommand() {
        return new DeleteControlPointCommand(edge, controlPoints);
    }
}