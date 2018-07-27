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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.graph.content.HasControlPoints;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.util.Counter;

public abstract class AbstractControlPointCommand extends AbstractGraphCommand {

    protected final Edge edge;
    protected final ControlPoint[] controlPoints;

    public AbstractControlPointCommand(final Edge edge, final ControlPoint... controlPoints) {
        this.controlPoints = PortablePreconditions.checkNotNull("controlPoints", controlPoints);
        this.edge = PortablePreconditions.checkNotNull("edge", edge);
    }

    @Override
    public CommandResult<RuleViolation> undo(final GraphCommandExecutionContext context) {
        return newUndoCommand().execute(context);
    }

    protected CommandResult<RuleViolation> checkArguments() {
        if (areArgumentsValid()) {
            return GraphCommandResultBuilder.SUCCESS;
        }
        return GraphCommandResultBuilder.FAILED;
    }

    protected boolean areArgumentsValid() {
        return getEdgeContent().getControlPoints().containsAll(getControlPointList());
    }

    protected List<ControlPoint> getControlPointList() {
        return Stream.of(controlPoints).collect(Collectors.toList());
    }

    protected HasControlPoints getEdgeContent() {
        return (HasControlPoints) edge.getContent();
    }

    protected List<ControlPoint> updateControlPointsIndex(final List<ControlPoint> controlPointsList) {
        final Counter counter = new Counter(-1);
        return controlPointsList.stream().sequential().map(cp -> {
            cp.setIndex(counter.increment());
            return cp;
        }).collect(Collectors.toList());
    }

    protected abstract Command<GraphCommandExecutionContext, RuleViolation> newUndoCommand();

    public ControlPoint[] getControlPoints() {
        return controlPoints;
    }
}
