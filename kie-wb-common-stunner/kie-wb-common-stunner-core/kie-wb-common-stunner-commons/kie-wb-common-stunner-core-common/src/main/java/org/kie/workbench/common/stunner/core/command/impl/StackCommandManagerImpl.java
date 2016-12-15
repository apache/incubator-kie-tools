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

import org.kie.workbench.common.stunner.core.command.*;
import org.kie.workbench.common.stunner.core.command.stack.StackCommandManager;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.registry.RegistryFactory;
import org.kie.workbench.common.stunner.core.registry.command.CommandRegistry;

import java.util.logging.Level;
import java.util.logging.Logger;

class StackCommandManagerImpl<C, V> implements StackCommandManager<C, V> {

    private static Logger LOGGER = Logger.getLogger( StackCommandManagerImpl.class.getName() );

    private final CommandManager<C, V> commandManager;
    private final CommandRegistry<Command<C, V>> registry;
    private final CommandManagerListener<C, V> _listener;

    @SuppressWarnings( "unchecked" )
    StackCommandManagerImpl( final RegistryFactory registryFactory,
                             final CommandManager<C, V> commandManager ) {
        this.registry = registryFactory.newCommandRegistry();
        this.commandManager = commandManager;
        if ( commandManager instanceof HasCommandManagerListener ) {
            ( ( HasCommandManagerListener ) commandManager ).setCommandManagerListener( listener );
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
        return commandManager.allow( context, command );
    }

    @Override
    public CommandResult<V> execute( final C context,
                                     final Command<C, V> command ) {
        LOGGER.log( Level.FINE, "Executing command [" + command + "]..." );
        CommandResult<V> result = commandManager.execute( context, command );
        if ( null != _listener ) {
            listener.onExecute( context, command, result );
        }
        return result;
    }

    @Override
    public CommandResult<V> undo( C context, Command<C, V> command ) {
        LOGGER.log( Level.FINE, "Undoing command [" + command + "]..." );
        CommandResult<V> result = commandManager.undo( context, command );
        if ( null != _listener ) {
            listener.onUndo( context, command, result );
        }
        return result;

    }

    private void logCommands( final String preffix, final Iterable<Command<C, V>> commands ) {
        if ( null != commands ) {
            commands.forEach( command ->   LOGGER.log( Level.FINE, preffix + " [" + command + "]" ) );
        }
    }

    @Override
    public CommandResult<V> undo( final C context ) {
        CommandResult<V> result = null;
        final Command<C, V> lastEntry = registry.peek();
        if ( null != lastEntry ) {
            LOGGER.log( Level.FINE, "Undoing commands: " );
            result = commandManager.undo( context, lastEntry );
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

    private final CommandManagerListener<C, V> listener = new CommandManagerListener<C, V>() {

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
        public void onUndo( final C context,
                            final Command<C, V> command,
                            final CommandResult<V> result ) {
        }

    };

}
