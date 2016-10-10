/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.command;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContext;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandExecutionContextImpl;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

public abstract class AbstractCanvasGraphCommand extends AbstractCanvasCommand implements HasGraphCommand<AbstractCanvasHandler> {

    protected Command<GraphCommandExecutionContext, RuleViolation> graphCommand;

    public AbstractCanvasGraphCommand() {
    }

    protected abstract Command<GraphCommandExecutionContext, RuleViolation> buildGraphCommand( final AbstractCanvasHandler context );

    protected abstract CommandResult<CanvasViolation> doExecute( final AbstractCanvasHandler context );

    protected abstract CommandResult<CanvasViolation> doUndo( final AbstractCanvasHandler context );

    @Override
    public Command<GraphCommandExecutionContext, RuleViolation> getGraphCommand( final AbstractCanvasHandler context ) {
        if ( null == graphCommand ) {
            graphCommand = buildGraphCommand( context );
        }
        return graphCommand;
    }

    @Override
    public CommandResult<CanvasViolation> allow( final AbstractCanvasHandler context ) {
        CommandResult<CanvasViolation> canvasResult = performOperation( context, 1 );
        if ( null == canvasResult ) {
            canvasResult = super.allow( context );

        }
        return canvasResult;
    }

    @Override
    public CommandResult<CanvasViolation> execute( final AbstractCanvasHandler context ) {
        CommandResult<CanvasViolation> canvasResult = performOperation( context, 2 );
        if ( null == canvasResult ) {
            canvasResult = doExecute( context );

        }
        return canvasResult;
    }

    @Override
    public CommandResult<CanvasViolation> undo( AbstractCanvasHandler context ) {
        CommandResult<CanvasViolation> canvasResult = performOperation( context, 3 );
        if ( null == canvasResult ) {
            canvasResult = doUndo( context );

        }
        return canvasResult;
    }

    /**
     * Op argument allowed values:
     * 1 - Allow
     * 2 - Execute
     * 3 - Undo
     */
    private CommandResult<CanvasViolation> performOperation( final AbstractCanvasHandler context,
                                                             final int op ) {
        CommandResult<CanvasViolation> canvasResult = null;
        final GraphCommandExecutionContext graphContext = getGraphCommandExecutionContext( context );
        final Command<GraphCommandExecutionContext, RuleViolation> graphCommand = this.getGraphCommand( context );
        CommandResult<RuleViolation> graphResult = null;
        if ( 1 == op ) {
            graphResult = graphCommand.allow( graphContext );

        } else if ( 2 == op ) {
            graphResult = graphCommand.execute( graphContext );

        } else {
            graphResult = graphCommand.undo( graphContext );

        }
        if ( CommandUtils.isError( graphResult ) ) {
            canvasResult = new CanvasCommandResultBuilder( graphResult ).build();

        }
        return canvasResult;

    }

    private GraphCommandExecutionContext getGraphCommandExecutionContext( final AbstractCanvasHandler context ) {
        return new GraphCommandExecutionContextImpl( context.getClientDefinitionManager(),
                context.getClientFactoryServices().getClientFactoryManager(), context.getRuleManager(), context.getGraphUtils() );
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder( super.toString() );
        if ( null != graphCommand ) {
            result.append( " [graphCommand=" )
                    .append( graphCommand.toString() )
                    .append( "]" );
        }
        return result.toString();
    }

}
