/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.core.client.canvas.command;

import java.util.Optional;
import java.util.function.Consumer;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

public class CloneConnectorCommand extends AbstractCanvasGraphCommand {

    private final String sourceUUID;
    private final String targetUUID;
    private final Edge candidate;
    private final String shapeSetId;
    private final Optional<Consumer<Edge>> callback;
    private transient CompositeCommand<AbstractCanvasHandler, CanvasViolation> command;

    public CloneConnectorCommand(final Edge candidate,
                                 final String sourceUUID,
                                 final String targetUUID,
                                 final String shapeSetId,
                                 final Consumer<Edge> callback) {
        this.candidate = candidate;
        this.sourceUUID = sourceUUID;
        this.targetUUID = targetUUID;
        this.shapeSetId = shapeSetId;
        this.callback = Optional.ofNullable(callback);
        this.command = buildCommand();
    }

    private CompositeCommand<AbstractCanvasHandler, CanvasViolation> buildCommand() {
        return new CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation>()
                .reverse()
                .build();
    }

    @Override
    protected Command<GraphCommandExecutionContext, RuleViolation> newGraphCommand(final AbstractCanvasHandler context) {
        return new org.kie.workbench.common.stunner.core.graph.command.impl.CloneConnectorCommand(candidate, sourceUUID, targetUUID, getCloneCallback());
    }

    protected Consumer<Edge> getCloneCallback() {
        return edge -> {
            //check if not a redo operation, in case size == 1 it was set before
            if (!command.isEmpty()) {
                command = buildCommand();
            }
            command.addCommand(new AddCanvasConnectorCommand(edge, shapeSetId));
            command.addCommand(new SetCanvasConnectionCommand(edge));
            callback.ifPresent(c -> c.accept(edge));
        };
    }

    @Override
    protected Command<AbstractCanvasHandler, CanvasViolation> newCanvasCommand(final AbstractCanvasHandler context) {
        return command;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                " [candidate=" + toUUID(candidate) + "," +
                "source=" + sourceUUID + "," +
                "target=" + targetUUID + "," +
                "shapeSet=" + shapeSetId + "]";
    }
}