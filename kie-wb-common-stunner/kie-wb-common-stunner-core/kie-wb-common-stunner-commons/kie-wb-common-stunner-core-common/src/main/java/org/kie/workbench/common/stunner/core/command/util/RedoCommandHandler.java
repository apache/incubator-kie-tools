/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.workbench.common.stunner.core.command.util;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandManager;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.graph.command.GraphCommandResultBuilder;
import org.kie.workbench.common.stunner.core.registry.command.CommandRegistry;
import org.kie.workbench.common.stunner.core.registry.impl.ClientCommandRegistry;

/**
 * This handler is an util class that achieves command "re-do" features.
 * It's behaviour is to keep in a command registry (usually a in-memory registry), the commands that have been
 * "undone" for a given context. It allows further re-do ( re-execution ) of that commands. If at some point
 * the user undo some commands and executes whatever new action that produces a new command, this registry is cleared
 * so you cannot redo older commands.
 * It can be used as:
 * <b>Inputs</b>
 * - Capture undo operations for commands and call the <code>onUndoCommandExecuted</code> method
 * - Capture regular command executions and call the <code>onCommandExecuted</code> method
 * <b>Output</b>
 * - Check <code>isEnabled</code> to figure out if a re-do operation can be done.
 * - Call <code>clear</code> to clear the internal commands registry and reset the re-do status.
 * - If <code>isEnabled</code> is <code>true</code>, you can run the <code>execute</code> method. It runs last undone command on found this handler's registry.
 * @param <C> The command type.
 */
@Dependent
public class RedoCommandHandler<C extends Command> {

    private final CommandRegistry<C> registry;

    protected RedoCommandHandler() {
        this(null);
    }

    @Inject
    public RedoCommandHandler(final ClientCommandRegistry<C> clientCommandRegistry) {
        this.registry = clientCommandRegistry;
    }

    public boolean onUndoCommandExecuted(final C command) {
        registry.register(command);
        return isEnabled();
    }

    public boolean onCommandExecuted(final C command) {
        if (isEnabled()) {
            final C last = registry.peek();
            if (last.equals(command)) {
                // If the recently executed command is the same in this handler' registry, means it has been
                // executed by this handler so it can be removed from the registry.
                registry.pop();
            } else {
                // Any "new" ( e.g: not a previously undone command ) executed commands cleans the registry,
                // no re-do is possible.
                clear();
            }
        }
        return isEnabled();
    }

    @SuppressWarnings("unchecked")
    public CommandResult<?> execute(final Object context,
                                    final CommandManager commandManager) {
        if (registry.isEmpty()) {
            return GraphCommandResultBuilder.SUCCESS;
        }

        final C last = registry.peek();
        return commandManager.execute(context,
                                      last);
    }

    public boolean isEnabled() {
        return !registry.isEmpty();
    }

    public void clear() {
        registry.clear();
    }
}
