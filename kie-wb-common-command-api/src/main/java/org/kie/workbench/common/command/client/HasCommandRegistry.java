/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.command.client;

import org.kie.workbench.common.command.client.registry.command.CommandRegistry;

/**
 * A command manager type that provides command registry integration.
 * @param <T> The command's context type.
 * @param <V> The command violation type.
 */
public interface HasCommandRegistry<T, V> {

    /**
     * Returns the command registry instance.
     */
    CommandRegistry<Command<T, V>> getRegistry();

    /**
     * Undo latest command present in the registry.
     */
    CommandResult<V> undo(final T context);
}
