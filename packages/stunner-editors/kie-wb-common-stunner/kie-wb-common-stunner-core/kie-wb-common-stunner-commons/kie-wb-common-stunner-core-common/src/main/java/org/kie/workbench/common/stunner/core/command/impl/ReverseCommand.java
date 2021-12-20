/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.command.impl;

import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;

/**
 * Reverse the execute and undo operations of the given command
 */
public class ReverseCommand<T, V> implements Command<T, V> {

    private final Command<T, V> command;

    public ReverseCommand(Command<T, V> command) {
        this.command = command;
    }

    @Override
    public CommandResult<V> allow(T context) {
        return command.allow(context);
    }

    @Override
    public CommandResult<V> execute(T context) {
        return command.undo(context);
    }

    @Override
    public CommandResult<V> undo(T context) {
        return command.execute(context);
    }
}
