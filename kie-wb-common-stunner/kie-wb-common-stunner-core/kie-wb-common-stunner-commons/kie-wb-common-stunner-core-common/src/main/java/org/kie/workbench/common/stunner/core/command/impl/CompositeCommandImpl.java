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

package org.kie.workbench.common.stunner.core.command.impl;

import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.CompositeCommand;

import java.util.LinkedList;
import java.util.List;

/**
 * A generic composite command implementation. Feel free to add commands into it.
 */
@Portable
public class CompositeCommandImpl<T, V> extends AbstractCompositeCommand<T, V> {

    @Override
    protected CommandResult<V> doAllow( final T context,
                                        final Command<T, V> command ) {
        return command.allow( context );
    }

    @Override
    protected CommandResult<V> doExecute( final T context,
                                          final Command<T, V> command ) {
        return command.execute( context );
    }

    @Override
    protected CommandResult<V> doUndo( final T context,
                                       final Command<T, V> command ) {
        return command.undo( context );
    }

    @NonPortable
    public static class CompositeCommandBuilder<T, V> {

        final CompositeCommand<T, V> compositeCommand = new CompositeCommandImpl<T, V>();

        public CompositeCommandBuilder<T, V> addCommand( final Command<T, V> command ) {
            compositeCommand.addCommand( command );
            return this;
        }

        public CompositeCommandBuilder<T, V> addCommands( final List<Command<T, V>> commands ) {
            commands.stream().forEach( compositeCommand::addCommand );
            return this;
        }

        public CompositeCommand<T, V> build() {
            return compositeCommand;
        }

    }

}
