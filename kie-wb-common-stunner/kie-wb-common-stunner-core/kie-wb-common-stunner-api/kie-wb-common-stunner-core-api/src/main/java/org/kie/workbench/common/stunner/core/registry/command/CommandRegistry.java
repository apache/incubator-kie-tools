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

package org.kie.workbench.common.stunner.core.registry.command;

import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.registry.DynamicRegistry;
import org.kie.workbench.common.stunner.core.registry.SizeConstrainedRegistry;

import java.util.Collection;

/**
 * Base registry type for Commands.
 * @param <C> The type of the Command.
 */
public interface CommandRegistry<C extends Command> extends DynamicRegistry<C>, SizeConstrainedRegistry {

    /**
     * Registers a command.
     */
    void register( Collection<C> command );

    /**
     * Returns the registered commands, can be composed collections if they're batched.
     */
    Iterable<Iterable<C>> getCommandHistory();

    /**
     * Peek the command from the registry.
     */
    Iterable<C> peek();

    /**
     * Peek and remove the command from the registry.
     */
    Iterable<C> pop();

    /**
     * Returns the size for this regsitry.
     */
    int getCommandHistorySize();

    /**
     * Clears the registry.
     */
    void clear();

}
