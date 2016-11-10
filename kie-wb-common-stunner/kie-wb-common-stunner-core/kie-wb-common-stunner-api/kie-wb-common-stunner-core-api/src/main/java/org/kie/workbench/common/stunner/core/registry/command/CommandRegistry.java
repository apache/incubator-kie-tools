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
import java.util.List;

/**
 * Base registry type for Commands.
 * @param <C> The type of the Command.
 */
public interface CommandRegistry<C extends Command> extends DynamicRegistry<C>, SizeConstrainedRegistry {

    /**
     * Registers a single or more than one command/s.
     */
    void register( C command );

    /**
     * Peek the command from the registry.
     */
    C peek();

    /**
     * Peek and remove the command from the registry.
     */
    C pop();

    /**
     * Returns the registered commands, can be composite commands as well.
     */
    List<C> getCommandHistory();

    /**
     * Clears the registry.
     */
    void clear();

}
