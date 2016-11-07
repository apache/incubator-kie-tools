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

import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.command.HasCommandManagerListener;
import org.kie.workbench.common.stunner.core.command.batch.BatchCommandManager;
import org.kie.workbench.common.stunner.core.command.batch.BatchCommandManagerListener;
import org.kie.workbench.common.stunner.core.command.batch.BatchCommandResult;
import org.kie.workbench.common.stunner.core.command.stack.StackCommandManager;
import org.kie.workbench.common.stunner.core.registry.RegistryFactory;
import org.kie.workbench.common.stunner.core.registry.command.CommandRegistry;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

class StackCommandManagerImpl<C, V> implements StackCommandManager<C, V> {

    private static Logger LOGGER = Logger.getLogger( StackCommandManagerImpl.class.getName() );

    private final BatchCommandManager<C, V> batchCommandManager;
    private final CommandRegistry<Command<C, V>> registry;
    private final BatchCommandManagerListener<C, V> _listener;

    @SuppressWarnings( "unchecked" )
    StackCommandManagerImpl( final RegistryFactory registryFactory,
                             final BatchCommandManager<C, V> batchCommandManager ) {
        this.registry = registryFactory.newCommandRegistry();
        this.batchCommandManager = batchCommandManager;
        if ( batchCommandManager instanceof HasCommandManagerListener ) {
            ( ( HasCommandManagerListener ) batchCommandManager ).setCommandManagerListener( listener );
            // Listener will be fired by the batchCommandManager instance.
            _listener = null;

        } else {
            // Listener will be fired here, as the batchCommandManager instance does not support it.
            _listener = listener;

        }

    }

    @Override
    public CommandResult<V> allow( final C context,
                                   final Command<C, V> command ) {
        LOGGER.log( Level.FINE, "Evaluating (allow) command [" + command + "]..." );
        return batchCommandManager.allow( context, command );
    }

    @Override
    public CommandResult<V> execute( final C context,
                                     final Command<C, V> command ) {
        LOGGER.log( Level.FINE, "Executing command [" + command + "]..." );
        CommandResult<V> result = batchCommandManager.execute( context, command );
        if ( null != _listener ) {
            _listener.onExecute( context, command, result );

        }
        return result;
    }

    @Override
    public CommandResult<V> undo( C context, Command<C, V> command ) {
        LOGGER.log( Level.FINE, "Undoing command [" + command + "]..." );
        CommandResult<V> result = batchCommandManager.undo( context, command );
        if ( null != _listener ) {
            _listener.onUndo( context, command, result );

        }
        return result;

    }

    @Override
    public BatchCommandManager<C, V> batch( final Command<C, V> command ) {
        LOGGER.log( Level.FINE, "Batch [" + command + "]" );
        return batchCommandManager.batch( command );
    }

    @Override
    public BatchCommandResult<V> executeBatch( final C context ) {
        final List<Command<C, V>> batchCommands = new LinkedList<>( batchCommandManager.getBatchCommands() );
        LOGGER.log( Level.FINE, "Executing batch commands: " ); logCommands( "--", batchCommands );
        final BatchCommandResult<V> result = batchCommandManager.executeBatch( context );
        if ( null != _listener ) {
            _listener.onExecuteBatch( context, batchCommands, result );

        }
        return result;
    }

    private void logCommands( final String preffix, final Collection<Command<C, V>> commands ) {
        if ( null != commands ) {
            commands.stream().forEach( command ->   LOGGER.log( Level.FINE, preffix + " [" + command + "]" ) );
        }
    }

    @Override
    public BatchCommandResult<V> undoBatch( final C context ) {
        final List<Command<C, V>> batchCommands = new LinkedList<>( batchCommandManager.getBatchCommands() );
        LOGGER.log( Level.FINE, "Undoing batch commands: " ); logCommands( "ç--", batchCommands );
        final BatchCommandResult<V> result = batchCommandManager.undoBatch( context );
        if ( null != _listener ) {
            _listener.onUndoBatch( context, batchCommands, result );

        }
        return result;

    }

    @Override
    public Collection<Command<C, V>> getBatchCommands() {
        return batchCommandManager.getBatchCommands();
    }

    @Override
    public CommandResult<V> undo( final C context ) {
        BatchCommandResult<V> result = null;
        final Iterable<Command<C, V>> lastEntry = registry.peek();
        if ( null != lastEntry ) {
            LOGGER.log( Level.FINE, "Undoing commands: " );
            for ( final Command<C, V> c : lastEntry ) {
                LOGGER.log( Level.FINE, "-- [" + c + "]" );
                batchCommandManager.batch( c );
            }
            result = batchCommandManager.undoBatch( context );
            if ( !CommandUtils.isError( result ) ) {
                registry.pop();
            }
        }
        return result;
    }

    @Override
    public CommandRegistry<Command<C, V>> getRegistry() {
        return registry;
    }

    private final BatchCommandManagerListener<C, V> listener = new BatchCommandManagerListener<C, V>() {

        @Override
        public void onAllow( final C context,
                             final Command<C, V> command,
                             final CommandResult<V> result ) {
        }

        @Override
        public void onExecute( final C context,
                               final Command<C, V> command,
                               final CommandResult<V> result ) {
            if ( !CommandUtils.isError( result ) ) {
                // Keep the just executed batched commands on this stack instance.
                registry.register( command );

            }

        }

        @Override
        public void onExecuteBatch( final C context,
                                    final Collection<Command<C, V>> commands,
                                    final BatchCommandResult<V> result ) {
            if ( !CommandUtils.isError( result ) ) {
                // Keep the just executed batched commands on this instance stack.
                registry.register( commands );

            }

        }

        @Override
        public void onUndo( final C context,
                            final Command<C, V> command,
                            final CommandResult<V> result ) {
        }

        @Override
        public void onUndoBatch( final C context,
                                 final Collection<Command<C, V>> commands,
                                 final CommandResult<V> result ) {
        }

    };

}
