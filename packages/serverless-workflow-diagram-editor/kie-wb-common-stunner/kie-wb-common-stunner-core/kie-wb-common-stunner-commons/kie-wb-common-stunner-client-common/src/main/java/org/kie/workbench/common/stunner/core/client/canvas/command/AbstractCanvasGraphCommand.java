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

import java.util.Objects;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandResultBuilder;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.HasGraphCommand;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Edge;
import org.kie.workbench.common.stunner.core.graph.Node;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

/**
 * Base type for commands which update both graph status/structure and canvas.
 */
public abstract class AbstractCanvasGraphCommand
        extends AbstractCanvasCommand
        implements HasGraphCommand<AbstractCanvasHandler> {

    /**
     * The private instance of the graph command.
     * It's a private stateful command instance - will be used for undoing the operation on the graph.
     */
    protected Command<GraphCommandExecutionContext, RuleViolation> graphCommand;

    /**
     * The private instance of the canvas command.
     * It's a private stateful command instance - will be used for undoing the operation on the graph.
     */
    protected Command<AbstractCanvasHandler, CanvasViolation> canvasCommand;

    /**
     * Creates a new command instance for the graph context.
     */
    protected abstract Command<GraphCommandExecutionContext, RuleViolation> newGraphCommand(final AbstractCanvasHandler context);

    /**
     * Creates a new command instance for the canvas context.
     */
    protected abstract Command<AbstractCanvasHandler, CanvasViolation> newCanvasCommand(final AbstractCanvasHandler context);

    @Override
    public Command<GraphCommandExecutionContext, RuleViolation> getGraphCommand(final AbstractCanvasHandler context) {
        if (null == graphCommand) {
            graphCommand = newGraphCommand(context);
        }
        return graphCommand;
    }

    public Command<AbstractCanvasHandler, CanvasViolation> getCanvasCommand(final AbstractCanvasHandler context) {
        if (null == canvasCommand) {
            canvasCommand = newCanvasCommand(context);
        }
        return canvasCommand;
    }

    @Override
    public CommandResult<CanvasViolation> allow(final AbstractCanvasHandler context) {
        final CommandResult<CanvasViolation> result = performOperationOnGraph(context, CommandOperation.ALLOW);
        if (canDoNexOperation(result)) {
            return performOperationOnCanvas(context, CommandOperation.ALLOW);
        }
        return result;
    }

    @Override
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context) {
        CommandResult<CanvasViolation> result = performOperationOnGraph(context, CommandOperation.EXECUTE);
        if (canDoNexOperation(result)) {
            final CommandResult<CanvasViolation> canvasResult =
                    performOperationOnCanvas(context, CommandOperation.EXECUTE);
            if (!canDoNexOperation(canvasResult)) {
                performOperationOnGraph(context, CommandOperation.UNDO);
                return canvasResult;
            }
        }
        return result;
    }

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
        final CommandResult<CanvasViolation> result = performOperationOnGraph(context, CommandOperation.UNDO);
        if (canDoNexOperation(result)) {
            return performOperationOnCanvas(context, CommandOperation.UNDO);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    protected Node<?, Edge> getNode(final AbstractCanvasHandler context,
                                    final String uuid) {
        return context.getGraphIndex().getNode(uuid);
    }

    private enum CommandOperation {
        ALLOW,
        EXECUTE,
        UNDO
    }

    private CommandResult<CanvasViolation> performOperationOnGraph(final AbstractCanvasHandler context,
                                                                   final CommandOperation op) {
        // Ensure the canvas command is initialized before updating the element on the graph side.
        getCanvasCommand(context);
        // Obtain the graph execution context and execute the graph command updates.
        final GraphCommandExecutionContext graphContext = context.getGraphExecutionContext();

        if (Objects.isNull(graphContext)) {
            //skipping command in case there is no graph execution context
            return CanvasCommandResultBuilder.SUCCESS;
        }

        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = getGraphCommand(context);
        CommandResult<RuleViolation> graphResult = null;
        switch (op) {
            case ALLOW:
                graphResult = graphCommand.allow(graphContext);
                break;
            case EXECUTE:
                graphResult = graphCommand.execute(graphContext);
                break;
            case UNDO:
                graphResult = graphCommand.undo(graphContext);
                break;
        }
        return new CanvasCommandResultBuilder(graphResult).build();
    }

    private CommandResult<CanvasViolation> performOperationOnCanvas(final AbstractCanvasHandler context,
                                                                    final CommandOperation op) {
        // Ensure the graph command is initialized
        getGraphCommand(context);

        final Command<AbstractCanvasHandler, CanvasViolation> command = getCanvasCommand(context);
        CommandResult<CanvasViolation> result = null;
        switch (op) {
            case ALLOW:
                result = command.allow(context);
                break;
            case EXECUTE:
                result = command.execute(context);
                break;
            case UNDO:
                result = command.undo(context);
                break;
        }
        return result;
    }

    private boolean canDoNexOperation(CommandResult<CanvasViolation> result) {
        return null == result || !CommandUtils.isError(result);
    }
}
