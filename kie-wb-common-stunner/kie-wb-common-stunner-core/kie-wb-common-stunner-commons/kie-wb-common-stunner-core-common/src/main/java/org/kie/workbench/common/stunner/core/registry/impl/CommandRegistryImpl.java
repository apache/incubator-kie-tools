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

package org.kie.workbench.common.stunner.core.registry.impl;

import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.registry.command.CommandRegistry;
import org.kie.workbench.common.stunner.core.registry.exception.RegistrySizeExceededException;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;

/**
 * The Stack class behavior when using the iterator is not the expected one, so used
 * ArrayDeque instead of an Stack to provide right iteration order.
 */
public class CommandRegistryImpl<C extends Command> implements CommandRegistry<C> {

    private final Deque<Iterable<C>> commands = new ArrayDeque<>();
    private int maxStackSize = 50;

    @Override
    public void setMaxSize( final int size ) {
        this.maxStackSize = size;
    }

    @Override
    public void register( final Collection<C> commands ) {
        addIntoStack( commands );
    }

    @Override
    public void register( final C command ) {
        addIntoStack( command );
    }

    @Override
    public boolean remove( final C command ) {
        throw new UnsupportedOperationException( "Remove not implemented yet." );
    }

    @Override
    public void clear() {
        commands.clear();
    }

    @Override
    public boolean contains( final C item ) {
        throw new UnsupportedOperationException( "Contains not implemented yet." );
    }

    @Override
    public Iterable<Iterable<C>> getCommandHistory() {
        return commands;
    }

    @Override
    public Iterable<C> peek() {
        return commands.peek();
    }

    @Override
    public Iterable<C> pop() {
        return commands.pop();
    }

    @Override
    public int getCommandHistorySize() {
        return commands.size();
    }

    private void addIntoStack( final C command ) {
        if ( null != command ) {
            if ( ( commands.size() + 1 ) > maxStackSize ) {
                stackSizeExceeded();
            }
            final Deque<C> s = new ArrayDeque<>();
            s.push( command );
            commands.push( s );
        }
    }

    private void addIntoStack( final Collection<C> _commands ) {
        if ( null != _commands && !_commands.isEmpty() ) {
            if ( ( commands.size() + _commands.size() ) > maxStackSize ) {
                stackSizeExceeded();
            }
            final Deque<C> s = new ArrayDeque<>();
            _commands.forEach( s::push );
            commands.push( s );
        }
    }

    private void stackSizeExceeded() {
        throw new RegistrySizeExceededException( maxStackSize );
    }

}
