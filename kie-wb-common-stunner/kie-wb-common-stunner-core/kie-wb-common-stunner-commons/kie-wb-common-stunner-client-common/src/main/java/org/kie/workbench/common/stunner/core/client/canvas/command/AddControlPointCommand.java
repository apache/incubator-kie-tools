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

import java.util.Objects;
import java.util.stream.Stream;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.content.view.ControlPoint;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

/**
 * Adds a connector {@link ControlPoint} into the canvas.
 */
public class AddControlPointCommand extends AbstractCanvasGraphCommand {

    private final Edge edge;
    private final ControlPoint[] controlPoints;

    public AddControlPointCommand(final Edge edge,
                                  final ControlPoint[] controlPoints) {
        //check if canvas should be executed before graph (when user add it not the marshaller)
        super(isCanvasCommandFirst(controlPoints));
        this.edge = edge;
        this.controlPoints = controlPoints;
    }

    private static boolean isCanvasCommandFirst(ControlPoint[] controlPoints) {
        return Stream.of(controlPoints).map(ControlPoint::getIndex).anyMatch(Objects::isNull);
    }

    @Override
    protected Command<GraphCommandExecutionContext, RuleViolation> newGraphCommand(final AbstractCanvasHandler context) {
        return new org.kie.workbench.common.stunner.core.graph.command.impl.AddControlPointCommand(edge, controlPoints);
    }

    @Override
    protected AbstractCanvasCommand newCanvasCommand(final AbstractCanvasHandler context) {
        return new AddCanvasControlPointCommand(edge, controlPoints);
    }
}
