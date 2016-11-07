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

package org.kie.workbench.common.stunner.core.graph.command;

import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandManagerFactory;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.batch.BatchCommandManager;
import org.kie.workbench.common.stunner.core.command.batch.BatchCommandResult;
import org.kie.workbench.common.stunner.core.command.event.local.CommandExecutedEvent;
import org.kie.workbench.common.stunner.core.command.event.local.CommandUndoExecutedEvent;
import org.kie.workbench.common.stunner.core.command.event.local.IsCommandAllowedEvent;
import org.kie.workbench.common.stunner.core.command.exception.CommandException;
import org.kie.workbench.common.stunner.core.command.impl.BatchCommandResultBuilder;
import org.kie.workbench.common.stunner.core.command.impl.BatchCommandResultImpl;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

@Dependent
public class GraphCommandManagerImpl
        implements GraphCommandManager {

    private static Logger LOGGER = Logger.getLogger( GraphCommandManagerImpl.class.getName() );

    private final BatchCommandManager<GraphCommandExecutionContext, RuleViolation> batchCommandManager;
    private final Event<IsCommandAllowedEvent> isCommandAllowedEvent;
    private final Event<CommandExecutedEvent> commandExecutedEvent;
    private final Event<CommandUndoExecutedEvent> commandUndoExecutedEvent;

    protected GraphCommandManagerImpl() {
        this( null, null, null, null );
    }

    @Inject
    public GraphCommandManagerImpl( final CommandManagerFactory commandManagerFactory,
                                    final Event<IsCommandAllowedEvent> isCommandAllowedEvent,
                                    final Event<CommandExecutedEvent> commandExecutedEvent,
                                    final Event<CommandUndoExecutedEvent> commandUndoExecutedEvent ) {
        this.batchCommandManager = commandManagerFactory.newBatchCommandManager();
        this.isCommandAllowedEvent = isCommandAllowedEvent;
        this.commandExecutedEvent = commandExecutedEvent;
        this.commandUndoExecutedEvent = commandUndoExecutedEvent;
    }

    @Override
    public CommandResult<RuleViolation> allow( final GraphCommandExecutionContext context,
                                               final Command<GraphCommandExecutionContext, RuleViolation> command ) {
        try {
            final CommandResult<RuleViolation> result = batchCommandManager.allow( context, command );
            if ( null != isCommandAllowedEvent ) {
                isCommandAllowedEvent.fire( new IsCommandAllowedEvent( command, result ) );
            }
            return result;
        } catch ( CommandException e ) {
            LOGGER.log( Level.SEVERE, "Error while executing graph command. Message [" +e.getMessage() + "]." );
        }
        return GraphCommandResultBuilder.FAILED;
    }

    @Override
    public CommandResult<RuleViolation> execute( final GraphCommandExecutionContext context,
                                                 final Command<GraphCommandExecutionContext, RuleViolation> command ) {
        try {
            final CommandResult<RuleViolation> result = batchCommandManager.execute( context, command );
            if ( null != commandExecutedEvent ) {
                commandExecutedEvent.fire( new CommandExecutedEvent( command, result ) );
            }
            return result;
        } catch ( CommandException e ) {
            LOGGER.log( Level.SEVERE, "Error while checking allow for graph command. Message [" +e.getMessage() + "]." );
        }
        return GraphCommandResultBuilder.FAILED;
    }

    @Override
    public BatchCommandManager<GraphCommandExecutionContext, RuleViolation> batch( final Command<GraphCommandExecutionContext, RuleViolation> command ) {
        return batchCommandManager.batch( command );
    }

    @Override
    public BatchCommandResult<RuleViolation> executeBatch( final GraphCommandExecutionContext context ) {
        try {
            final BatchCommandResult<RuleViolation> result = batchCommandManager.executeBatch( context );
            if ( null != commandExecutedEvent ) {
                commandExecutedEvent.fire( new CommandExecutedEvent( batchCommandManager.getBatchCommands(), result ) );
            }
            return result;
        } catch ( CommandException e ) {
            LOGGER.log( Level.SEVERE, "Error while executing batch command. Message [" +e.getMessage() + "]." );
        }
        return new BatchCommandResultBuilder<RuleViolation>()
                .add( GraphCommandResultBuilder.FAILED )
                .build();
    }

    @Override
    public Collection<Command<GraphCommandExecutionContext, RuleViolation>> getBatchCommands() {
        return batchCommandManager.getBatchCommands();
    }

    @Override
    public CommandResult<RuleViolation> undo( final GraphCommandExecutionContext context,
                                              final Command<GraphCommandExecutionContext, RuleViolation> command ) {
        final CommandResult<RuleViolation> result = batchCommandManager.undo( context, command );
        if ( null != commandUndoExecutedEvent ) {
            final CommandUndoExecutedEvent event = new CommandUndoExecutedEvent( command, result );
            commandUndoExecutedEvent.fire( event );
        }
        return result;
    }

    @Override
    public BatchCommandResult<RuleViolation> undoBatch( final GraphCommandExecutionContext context ) {
        final BatchCommandResult<RuleViolation> result = batchCommandManager.undoBatch( context );
        if ( null != commandUndoExecutedEvent ) {
            final CommandUndoExecutedEvent event = new CommandUndoExecutedEvent(
                    new ArrayList<>( batchCommandManager.getBatchCommands() ),
                    result );
            commandUndoExecutedEvent.fire( event );
        }
        return result;

    }
}
