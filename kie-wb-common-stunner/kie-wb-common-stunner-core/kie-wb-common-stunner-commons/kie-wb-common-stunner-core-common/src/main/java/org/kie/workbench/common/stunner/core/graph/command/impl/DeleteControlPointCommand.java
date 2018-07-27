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

import java.util.List;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.content.HasControlPoints;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

/**
 * A Graph command that deletes {@link ControlPoint} from a given {@link Edge}.
 */
@Portable
public class DeleteControlPointCommand extends AbstractControlPointCommand {

    public DeleteControlPointCommand(final @MapsTo("edge") Edge edge,
                                     final @MapsTo("controlPoints") ControlPoint... controlPoints) {
        super(edge, controlPoints);
    }

    @Override
    protected CommandResult<RuleViolation> check(GraphCommandExecutionContext context) {
        return checkArguments();
    }

    @Override
    public CommandResult<RuleViolation> execute(GraphCommandExecutionContext context) {
        HasControlPoints edgeContent = getEdgeContent();

        List<ControlPoint> connectorControlPoints = edgeContent.getControlPoints();
        connectorControlPoints.removeAll(getControlPointList());

        //update index
        updateControlPointsIndex(connectorControlPoints);

        return GraphCommandResultBuilder.SUCCESS;
    }

    @Override
    protected AddControlPointCommand newUndoCommand() {
        return new AddControlPointCommand(edge, controlPoints);
    }
}