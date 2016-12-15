/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.command.impl;

import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.command.CompositeCommand;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractCompositeCommand<T, V> implements CompositeCommand<T, V> {

    private static Logger LOGGER = Logger.getLogger( AbstractCompositeCommand.class.getName() );

    protected final List<Command<T, V>> commands = new LinkedList<>();
    private boolean initialized = false;

    public AbstractCompositeCommand<T, V> addCommand( final Command<T, V> command ) {
        commands.add( command );
        return this;
    }

    protected abstract CommandResult<V> doAllow( T context, Command<T, V> command );

    protected abstract CommandResult<V> doExecute( T context, Command<T, V> command );

    protected abstract CommandResult<V> doUndo( T context, Command<T, V> command );

    @Override
    public CommandResult<V> allow( T context ) {
        ensureInitialized( context );
        final List<CommandResult<V>> results = new LinkedList<>();
        for ( final Command<T, V> command : commands ) {
            final CommandResult<V> result = doAllow( context, command );
            results.add( result );
            if ( CommandUtils.isError( result ) ) {
                break;
            }
        }
        return buildResult( results );
    }

    @Override
    public CommandResult<V> execute( final T context ) {
        final CommandResult<V> allowResult = this.allow( context );
        if ( !CommandUtils.isError( allowResult ) ) {
            final Stack<Command<T, V>> executedCommands = new Stack<>();
            final List<CommandResult<V>> results = new LinkedList<>();
            for ( final Command<T, V> command : commands ) {
                LOGGER.log( Level.FINE, "Checking execution for command [" + command + "]" );
                final CommandResult<V> violations = doExecute( context, command );
                LOGGER.log( Level.FINE, "Execution of command [" + command + "] finished - "
                        + "Violations [" + violations + "]");
                results.add( violations );
                if ( CommandResult.Type.ERROR.equals( violations.getType() ) ) {
                    undoMultipleExecutedCommands( context, executedCommands );
                    break;
                }
            }
            return buildResult( results );
        }
        return allowResult;
    }

    @Override
    public CommandResult<V> undo( final T context ) {
        final List<CommandResult<V>> results = new LinkedList<>();
        final int cs = commands.size();
        for ( int x = 0; x < cs; x++ ) {
            final Command<T, V> command = commands.get( cs - ( x + 1 ) );
            LOGGER.log( Level.FINE, "Undoing command [" + command + "]" );
            final CommandResult<V> violations = doUndo( context, command );
            LOGGER.log( Level.FINE, "Undo of command [" + command + "] finished - "
                    + "Violations [" + violations + "]");
            results.add( violations );
        }
        return buildResult( results );
    }

    protected void initialize( T context ) {
        // Nothing to do by default. Implementation can add commands here.
    }

    protected void ensureInitialized( T context ) {
        if ( !initialized ) {
            initialize( context );
            this.initialized = true;
        }
    }

    public List<Command<T, V>> getCommands() {
        return commands;
    }

    private CommandResult<V> buildResult( final List<CommandResult<V>> results ) {
        final CommandResult.Type[] type = { CommandResult.Type.INFO };
        String message = "Found" + results.size() + " violations.";
        final List<V> violations = new LinkedList<V>();
        results.stream().forEach( rr -> {
            if ( hasMoreSeverity( rr.getType(), type[0] ) ) {
                type[0] = rr.getType();
            }
            final Iterable<V> rrIter = rr.getViolations();
            if ( null != rrIter ) {
                rrIter.forEach( violations::add );
            }
        } );
        return new CommandResultImpl<V>( type[0], message, violations );
    }

    private boolean hasMoreSeverity( final CommandResult.Type type, final CommandResult.Type reference ) {
        return type.getSeverity() > reference.getSeverity();

    }

    private CommandResult<V> undoMultipleExecutedCommands( final T context,
                                                            final List<Command<T, V>> commandStack ) {
        final List<CommandResult<V>> results = new LinkedList<>();
        commandStack.stream().forEach( command -> results.add( doUndo( context, command ) ) );
        return buildResult( results );
    }

}
