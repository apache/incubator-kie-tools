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

package org.kie.workbench.common.stunner.core.command.event;

import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;

import java.util.ArrayList;
import java.util.Collection;

public abstract class AbstractCommandEvent<C, V> {

    private final Collection<Command<C, V>> commands;
    private final boolean isBatch;
    private final CommandResult<V> result;

    public AbstractCommandEvent( final Collection<Command<C, V>> commands,
                                 final CommandResult<V> result ) {
        this.commands = commands;
        this.isBatch = true;
        this.result = result;
    }

    public AbstractCommandEvent( final Command<C, V> command,
                                 final CommandResult<V> result ) {
        this.commands = new ArrayList<Command<C, V>>( 1 ) {{
            add( command );
        }};
        this.isBatch = false;
        this.result = result;
    }

    public Command<C, V> getCommand() {
        if ( !isBatch ) {
            return commands.iterator().next();
        }
        return null;
    }

    public Collection<Command<C, V>> getCommands() {
        if ( isBatch ) {
            return commands;

        }
        return null;
    }

    public CommandResult<V> getResult() {
        return result;
    }

    public boolean hasError() {
        return result != null && result.getType() != null && CommandResult.Type.ERROR.equals( result.getType() );
    }
}
