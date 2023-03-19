/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.command;

/**
 * Manager to handle execution of commands in a given context.
 * @param <T> The execution context
 * @param <V> The resulting violations of the command execution in the given context.
 */
public interface CommandManager<T, V> {

    /**
     * Check whether the given command can be executed.
     */
    CommandResult<V> allow(T context,
                           Command<T, V> command);

    /**
     * Execute the given command.
     */
    CommandResult<V> execute(T context,
                             Command<T, V> command);

    /**
     * Undo an executed command.
     */
    CommandResult<V> undo(T context,
                          Command<T, V> command);
}
