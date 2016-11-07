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

package org.kie.workbench.common.stunner.core.command.util;

import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.batch.BatchCommandManager;
import org.kie.workbench.common.stunner.core.registry.RegistryFactory;
import org.kie.workbench.common.stunner.core.registry.command.CommandRegistry;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;
import java.util.logging.Logger;

/**
 * This handler is an util class that achieves command "re-do" features.
 * It can be used as:
 * <b>Inputs</b>
 * - Capture undo operations for commands and call the <code>onUndoCommandExecuted</code> method
 * - Capture regular command executions and call the <code>onCommandExecuted</code> method
 * <b>Ouput</b>
 * - Check <code>isEnabled</code> to figure out if a re-do operation can be done.
 * - Call <code>clear</code> to clear the internal commands registry and reset the re-do status.
 * - If <code>isEnabled</code> is <code>true</code>, you can run the <code>execute</code> method. It runs last undone command on found this handler's registry.
 *
 * @param <C> The command type.
 */
@Dependent
public class RedoCommandHandler<C extends Command> {

    private static Logger LOGGER = Logger.getLogger( RedoCommandHandler.class.getName() );

    private final CommandRegistry<C> registry;

    protected RedoCommandHandler() {
        this( null );
    }

    @Inject
    public RedoCommandHandler( final RegistryFactory registryFactory ) {
        this.registry = registryFactory.newCommandRegistry();
    }

    public boolean onUndoCommandExecuted( final C command ) {
        registry.register( command );
        return isEnabled();
    }

    public boolean onUndoCommandExecuted( final Collection<C> commands ) {
        registry.register( commands );
        return isEnabled();
    }

    @SuppressWarnings( "unchecked" )
    public boolean onCommandExecuted( final C command ) {
        return _onCommandExecuted( new ArrayList<C>( 1 ) {{
            add( command );
        }} );
    }

    public boolean onCommandExecuted( final Collection<C> commands ) {
        return _onCommandExecuted( commands );
    }

    @SuppressWarnings( "unchecked" )
    public CommandResult<?> execute( final Object context,
                                     final BatchCommandManager commandManager ) {
        if ( !registry.isEmpty() ) {
            final Collection<C> last = ( Collection<C> ) registry.peek();
            final int s = last.size();
            CommandResult<?> result = null;
            if ( s == 1 ) {
                result = commandManager.execute( context, last.iterator().next() );
            } else {
                final Stack<C> t = new Stack<>();
                last.stream().forEach( t::push );
                t.stream().forEach( commandManager::batch );
                result = commandManager.executeBatch( context );
            }
            return result;
        }
        return null;
    }

    public boolean isEnabled() {
        return !registry.isEmpty();
    }

    public void clear() {
        registry.clear();
    }

    private boolean _onCommandExecuted( final Collection<C> commands ) {
        if ( !registry.isEmpty() ) {
            final Collection<C> last = getLastRegistryItem();
            if ( null != last && last.equals( commands ) ) {
                // If the recently executed command is the same in this handler' registry, means it has been
                // executed by this handler so it can be removed from the registry.
                registry.pop();
            } else {
                // Any "new" ( e.g: not a previously undone command ) executed commands cleans the registry,
                // no re-do is possible.
                registry.clear();
            }
        }
        return isEnabled();
    }

    private Collection<C> getLastRegistryItem() {
        try {
            return ( Collection<C> ) registry.peek();
        } catch ( ClassCastException e ) {
            throw new UnsupportedOperationException( "Registry type not supported [" + registry.getClass().getName() + "]", e );
        }
    }

}
