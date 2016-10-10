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
import org.kie.workbench.common.stunner.core.command.CommandUtils;
import org.kie.workbench.common.stunner.core.command.CompositeCommand;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractCompositeCommand<T, V> implements CompositeCommand<T, V> {

    protected final List<Command<T, V>> commands = new LinkedList<>();
    private boolean initialized = false;

    public AbstractCompositeCommand<T, V> addCommand( final Command<T, V> command ) {
        commands.add( command );
        return this;
    }

    protected abstract void initialize( T context );

    protected abstract CommandResult<V> buildResult( final List<CommandResult<V>> violations );

    protected abstract CommandResult<V> doAllow( T context, Command<T, V> command );

    protected abstract CommandResult<V> doExecute( T context, Command<T, V> command );

    protected abstract CommandResult<V> doUndo( T context, Command<T, V> command );

    @Override
    public CommandResult<V> allow( T context ) {
        checkInitialized( context );
        final List<CommandResult<V>> results = new LinkedList<>();
        for ( final Command<T, V> command : commands ) {
            final CommandResult<V> violations = doAllow( context, command );
            results.add( violations );
        }
        return buildResult( results );
    }

    @Override
    public CommandResult<V> execute( final T context ) {
        CommandResult<V> allowResult = this.allow( context );
        if ( !CommandUtils.isError( allowResult ) ) {
            final List<CommandResult<V>> results = new LinkedList<>();
            for ( final Command<T, V> command : commands ) {
                final CommandResult<V> violations = doExecute( context, command );
                results.add( violations );
            }
            return buildResult( results );
        }
        return allowResult;
    }

    @Override
    public CommandResult<V> undo( final T context ) {
        final List<CommandResult<V>> results = new LinkedList<>();
        for ( final Command<T, V> command : commands ) {
            final CommandResult<V> violations = doUndo( context, command );
            results.add( violations );
        }
        return buildResult( results );
    }

    private void checkInitialized( T context ) {
        if ( !initialized ) {
            initialize( context );
            this.initialized = true;
        }
    }

}
