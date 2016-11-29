/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *     http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.canvas.command;

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
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContextImpl;
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
    private Command<GraphCommandExecutionContext, RuleViolation> graphCommand;

    /**
     * The private instance of the canvas command.
     * It's a private stateful command instance - will be used for undoing the operation on the graph.
     */
    private AbstractCanvasCommand canvasCommand;

    /**
     * Creates a new command instance for the graph context.
     */
    protected abstract Command<GraphCommandExecutionContext, RuleViolation> newGraphCommand( AbstractCanvasHandler context );

    /**
     * Creates a new command instance for the canvas context.
     */
    protected abstract AbstractCanvasCommand newCanvasCommand( AbstractCanvasHandler context );

    @Override
    public Command<GraphCommandExecutionContext, RuleViolation> getGraphCommand( final AbstractCanvasHandler context ) {
        if ( null == graphCommand ) {
            graphCommand = newGraphCommand( context );
        }
        return graphCommand;
    }

    public AbstractCanvasCommand getCanvasCommand( final AbstractCanvasHandler context ) {
        if ( null == canvasCommand ) {
            canvasCommand = newCanvasCommand( context );
        }
        return canvasCommand;
    }

    @Override
    public CommandResult<CanvasViolation> allow( final AbstractCanvasHandler context ) {
        // Ensure the canvas command is initialized before updating the element on the graph side.
        getCanvasCommand( context );
        final CommandResult<CanvasViolation> canvasResult =
                performOperationOnGraph( context, CommandOperation.ALLOW );
        if ( !CommandUtils.isError( canvasResult ) ) {
            return getCanvasCommand( context ).allow( context );
        }
        return canvasResult;
    }

    @Override
    public CommandResult<CanvasViolation> execute( final AbstractCanvasHandler context ) {
        // Ensure the canvas command is initialized before updating the element on the graph side.
        getCanvasCommand( context );
        final CommandResult<CanvasViolation> canvasResult =
                performOperationOnGraph( context, CommandOperation.EXECUTE );
        if ( !CommandUtils.isError( canvasResult ) ) {
            return getCanvasCommand( context ).execute( context );

        }
        return canvasResult;
    }

    @Override
    public CommandResult<CanvasViolation> undo( AbstractCanvasHandler context ) {
        // Ensure the canvas command is initialized before updating the element on the graph side.
        getCanvasCommand( context );
        final CommandResult<CanvasViolation> canvasResult =
                performOperationOnGraph( context, CommandOperation.UNDO );
        if ( !CommandUtils.isError( canvasResult ) ) {
            return getCanvasCommand( context ).undo( context );
        }
        return canvasResult;
    }

    @SuppressWarnings( "unchecked" )
    protected Node<?, Edge> getNode( final AbstractCanvasHandler context, final String uuid ) {
        return context.getGraphIndex().getNode( uuid );
    }

    private enum CommandOperation {
        ALLOW, EXECUTE, UNDO;
    }

    /**
     * Performs any of the following operations on the graph command.
     */
    private CommandResult<CanvasViolation> performOperationOnGraph( final AbstractCanvasHandler context,
                                                                    final CommandOperation op ) {
        final GraphCommandExecutionContext graphContext = getGraphCommandExecutionContext( context );
        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = getGraphCommand( context );
        CommandResult<RuleViolation> graphResult = null;
        switch ( op ) {
            case ALLOW:
                graphResult = graphCommand.allow( graphContext );
                break;
            case EXECUTE:
                graphResult = graphCommand.execute( graphContext );
                break;
            case UNDO:
                graphResult = graphCommand.undo( graphContext );
                break;
        }
        return new CanvasCommandResultBuilder( graphResult ).build();
    }

    private GraphCommandExecutionContext getGraphCommandExecutionContext( final AbstractCanvasHandler context ) {
        return new GraphCommandExecutionContextImpl( context.getClientDefinitionManager(),
                context.getClientFactoryServices().getClientFactoryManager(), context.getGraphRulesManager(),
                context.getGraphIndex(), context.getGraphUtils() );
    }

    @Override
    public String toString() {
        return  "[" +
                this.getClass().getName() +
                "]" +
                " [canvasCommand=" +
                ( null != canvasCommand ? canvasCommand.toString() : "null" ) +
                " [graphCommand=" +
                ( null != graphCommand ? graphCommand.toString() : null ) +
                "]";
    }

}
