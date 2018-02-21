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

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.soup.commons.validation.PortablePreconditions;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.AbstractCompositeCommand;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPointImpl;
import org.kie.workbench.common.stunner.core.graph.content.view.Point2D;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

/**
 * A Command to update a given {@link ControlPoint} position on graph.
 */
@Portable
public class UpdateControlPointPositionCommand extends AbstractGraphCompositeCommand {

    private final Edge candidate;
    private final ControlPoint controlPoint;
    private final Point2D position;
    private final ControlPoint positionedControlPoint;

    public UpdateControlPointPositionCommand() {
        this(null, null, null);
    }

    public UpdateControlPointPositionCommand(final Edge candidate,
                                             final ControlPoint controlPoint,
                                             final Point2D position) {

        this.candidate = PortablePreconditions.checkNotNull("candidate", candidate);
        this.controlPoint = PortablePreconditions.checkNotNull("controlPoint", controlPoint);
        this.position = PortablePreconditions.checkNotNull("position", position);
        this.positionedControlPoint = getUpdatedControlPoint(position);
    }

    @Override
    protected AbstractCompositeCommand<GraphCommandExecutionContext, RuleViolation> initialize(GraphCommandExecutionContext context) {
        addCommand(new DeleteControlPointCommand(candidate, controlPoint));
        addCommand(new AddControlPointCommand(candidate, positionedControlPoint));
        return this;
    }

    private ControlPointImpl getUpdatedControlPoint(Point2D position) {
        return new ControlPointImpl(position, controlPoint.getIndex());
    }

    @Override
    public CommandResult<RuleViolation> undo(GraphCommandExecutionContext context) {
        return newUndoCommand().execute(context);
    }

    protected CompositeCommand<GraphCommandExecutionContext, RuleViolation> newUndoCommand() {
        return new CompositeCommand.Builder<GraphCommandExecutionContext, RuleViolation>()
                .addCommand(new DeleteControlPointCommand(candidate, positionedControlPoint))
                .addCommand(new AddControlPointCommand(candidate, controlPoint))
                .build();
    }

    @Override
    protected boolean delegateRulesContextToChildren() {
        return false;
    }
}