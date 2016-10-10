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

package org.kie.workbench.common.stunner.core.command.delegate;

import org.kie.workbench.common.stunner.core.command.*;

public abstract class DelegateCommandManager<C, V> implements CommandManager<C, V> {

    private final CommandManagerListener<C, V> listener = new CommandManagerListener<C, V>() {
        @Override
        public void onAllow( final C context,
                             final Command<C, V> command,
                             final CommandResult<V> result ) {
            postAllow( context, command, result );

        }

        @Override
        public void onExecute( final C context,
                               final Command<C, V> command,
                               final CommandResult<V> result ) {
            postExecute( context, command, result );

        }

        @Override
        public void onUndo( final C context,
                            final Command<C, V> command,
                            final CommandResult<V> result ) {
            postUndo( context, command, result );

        }
    };

    protected abstract CommandManager<C, V> getDelegate();

    @Override
    public CommandResult<V> allow( final C context,
                                   final Command<C, V> command ) {
        if ( null != getDelegate() ) {
            return getDelegateWithListener().allow( context, command );
        }
        return null;
    }

    protected void postAllow( final C context,
                              final Command<C, V> command,
                              final CommandResult<V> result ) {
    }

    @Override
    public CommandResult<V> execute( final C context,
                                     final Command<C, V> command ) {
        if ( null != getDelegate() ) {
            return getDelegateWithListener().execute( context, command );
        }
        return null;
    }

    protected void postExecute( final C context,
                                final Command<C, V> command,
                                final CommandResult<V> result ) {
    }

    @Override
    public CommandResult<V> undo( final C context,
                                  final Command<C, V> command ) {
        if ( null != getDelegate() ) {
            return getDelegateWithListener().undo( context, command );
        }
        return null;
    }

    protected void postUndo( final C context,
                             final Command<C, V> command,
                             final CommandResult<V> result ) {
    }

    protected CommandManagerListener<C, V> getListener() {
        return listener;
    }

    @SuppressWarnings( "unchecked" )
    private CommandManager<C, V> getDelegateWithListener() {
        final CommandManager<C, V> delegate = getDelegate();
        if ( null != delegate
                && delegate instanceof HasCommandManagerListener ) {
            ( ( HasCommandManagerListener ) delegate ).setCommandManagerListener( getListener() );
        }
        return delegate;
    }

}
