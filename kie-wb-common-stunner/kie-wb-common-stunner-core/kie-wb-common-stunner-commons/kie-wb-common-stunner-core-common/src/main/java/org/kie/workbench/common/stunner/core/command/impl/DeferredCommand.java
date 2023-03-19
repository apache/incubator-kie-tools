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

package org.kie.workbench.common.stunner.core.command.impl;

import java.util.function.Supplier;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;

/**
 * Just defers the creation of the command instance until some
 * operation is being demanded.
 * @param <T> The content type.
 * @param <V> The violation type.
 */
@Portable
public class DeferredCommand<T, V>
        implements Command<T, V> {

    private Command<T, V> command;
    private transient Supplier<Command<T, V>> commandSupplier;

    public DeferredCommand() {
    }

    public DeferredCommand(final Supplier<Command<T, V>> commandSupplier) {
        this.commandSupplier = commandSupplier;
    }

    @Override
    public CommandResult<V> allow(final T context) {
        return getCommand().allow(context);
    }

    @Override
    public CommandResult<V> execute(final T context) {
        return getCommand().execute(context);
    }

    @Override
    public CommandResult<V> undo(final T context) {
        return getCommand().undo(context);
    }

    public Command<T, V> getCommand() {
        if (null == command) {
            command = commandSupplier.get();
        }
        return command;
    }

    @Override
    public String toString() {
        return "[" + getClass().getSimpleName() + "] " + command;
    }
}
