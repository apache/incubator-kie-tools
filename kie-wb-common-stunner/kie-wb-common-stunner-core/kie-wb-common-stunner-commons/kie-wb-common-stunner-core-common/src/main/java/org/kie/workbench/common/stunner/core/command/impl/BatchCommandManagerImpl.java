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

package org.kie.workbench.common.stunner.core.command.impl;

import org.kie.workbench.common.stunner.core.command.*;
import org.kie.workbench.common.stunner.core.command.batch.BatchCommandManager;
import org.kie.workbench.common.stunner.core.command.batch.BatchCommandManagerListener;
import org.kie.workbench.common.stunner.core.command.batch.BatchCommandResult;
import org.uberfire.commons.validation.PortablePreconditions;

import java.util.*;

class BatchCommandManagerImpl<T, V> implements BatchCommandManager<T, V>, HasCommandManagerListener<BatchCommandManagerListener<T, V>> {

    private final CommandManager<T, V> commandManager;
    private final List<Command<T, V>> commands;
    private BatchCommandManagerListener<T, V> listener;

    BatchCommandManagerImpl( final CommandManagerFactory commandManagerFactory ) {
        this.commandManager = commandManagerFactory.newCommandManager();
        this.commands = new LinkedList<>();
        this.listener = null;
    }

    @Override
    public CommandResult<V> allow( final T context,
                                   final Command<T, V> command ) {
        final CommandResult<V> result = commandManager.allow( context, command );
        if ( null != listener ) {
            listener.onAllow( context, command, result );

        }
        return result;
    }

    @Override
    public CommandResult<V> execute( final T context, final Command<T, V> command ) {
        if ( !commands.isEmpty() ) {
            throw new IllegalStateException( " Cannot execute a command while " +
                    "there exist batch commands already queued." );
        }
        final CommandResult<V> result = commandManager.execute( context, command );
        if ( null != listener ) {
            listener.onExecute( context, command, result );

        }
        return result;
    }

    @Override
    public BatchCommandManager<T, V> batch( final Command<T, V> command ) {
        PortablePreconditions.checkNotNull( "command", command );
        this.commands.add( command );
        return this;
    }

    @Override
    public BatchCommandResult<V> executeBatch( final T context ) {
        if ( !commands.isEmpty() ) {
            final BatchCommandResultBuilder<V> builder = new BatchCommandResultBuilder<V>();
            final Stack<Command<T, V>> executedCommands = new Stack<>();
            for ( final Command<T, V> command : commands ) {
                // Execute command.
                final CommandResult<V> result = commandManager.execute( context, command );
                builder.add( result );
                // Check results.
                if ( CommandResult.Type.ERROR.equals( result.getType() ) ) {
                    // Undo previous executed commands on inverse order, so using an stack.
                    _undoMultipleCommands( context, executedCommands );
                    clear();
                    final BatchCommandResult<V> r = new BatchCommandResultBuilder<V>().add( result ).build();
                    if ( null != listener ) {
                        listener.onExecuteBatch( context, new ArrayList<Command<T, V>>( commands ), r );
                    }
                    return r;

                } else {
                    executedCommands.push( command );

                }
            }
            final BatchCommandResult<V> result = builder.build();
            if ( null != listener ) {
                listener.onExecuteBatch( context, new ArrayList<Command<T, V>>( commands ), result );
            }
            clear();
            return result;

        }
        return new BatchCommandResultBuilder<V>().build();

    }

    @Override
    public CommandResult<V> undo( final T context,
                                  final Command<T, V> command ) {
        CommandResult<V> result = commandManager.undo( context, command );
        if ( null != listener ) {
            listener.onUndo( context, command, result );

        }
        return result;
    }

    // TODO: Handle errors while undoing.
    @Override
    public BatchCommandResult<V> undoBatch( final T context ) {
        final BatchCommandResult<V> result = _undoMultipleCommands( context, commands );
        if ( null != listener ) {
            listener.onUndoBatch( context, new ArrayList<Command<T, V>>( commands ), result );

        }
        clear();
        return result;

    }

    @Override
    public Collection<Command<T, V>> getBatchCommands() {
        return commands;
    }

    private BatchCommandResult<V> _undoMultipleCommands( final T context,
                                                         final Collection<Command<T, V>> commandStack ) {
        final BatchCommandResultBuilder<V> builder = new BatchCommandResultBuilder<V>();
        for ( final Command<T, V> undoCommand : commandStack ) {
            final CommandResult<V> undoResult = commandManager.undo( context, undoCommand );
            if ( null != undoResult ) {
                builder.add( undoResult );
            }

        }
        return builder.build();
    }

    private void clear() {
        commands.clear();
    }

    @Override
    public void setCommandManagerListener( final BatchCommandManagerListener<T, V> listener ) {
        this.listener = listener;
    }

    @Override
    public String toString() {
        return "[" + super.toString() + "]";
    }

}
