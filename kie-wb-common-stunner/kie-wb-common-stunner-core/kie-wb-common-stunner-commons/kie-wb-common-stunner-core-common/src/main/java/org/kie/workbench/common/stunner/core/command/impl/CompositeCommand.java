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

import java.util.LinkedList;
import java.util.List;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;

/**
 * A generic composite command implementation.
 */
@Portable
public class CompositeCommand<T, V> extends AbstractCompositeCommand<T, V> {

    private final boolean reverse;

    public CompositeCommand(final @MapsTo("reverse") boolean reverse) {
        this.reverse = reverse;
    }

    @Override
    protected CommandResult<V> doAllow(final T context,
                                       final Command<T, V> command) {
        return command.allow(context);
    }

    @Override
    protected CommandResult<V> doExecute(final T context,
                                         final Command<T, V> command) {
        return command.execute(context);
    }

    @Override
    protected CommandResult<V> doUndo(final T context,
                                      final Command<T, V> command) {
        return command.undo(context);
    }

    @Override
    protected boolean isUndoReverse() {
        return reverse;
    }

    @NonPortable
    public static class Builder<T, V> {

        private final LinkedList<Command<T, V>> commands = new LinkedList<>();
        private boolean reverse = true;

        /**
         * The undo for this composite command will be done by using a reverse order.
         */
        public Builder<T, V> reverse() {
            this.reverse = true;
            return this;
        }

        /**
         * The undo for this composite command will be done by using the same insertion order.
         */
        public Builder<T, V> forward() {
            this.reverse = false;
            return this;
        }

        public Builder<T, V> addFirstCommand(final Command<T, V> command) {
            commands.addFirst(command);
            return this;
        }

        public Builder<T, V> addCommand(final Command<T, V> command) {
            commands.add(command);
            return this;
        }

        public Builder<T, V> addCommands(final List<Command<T, V>> _commands) {
            commands.addAll(_commands);
            return this;
        }

        public int size() {
            return commands.size();
        }

        public Command<T, V> get(final int index) {
            return commands.get(index);
        }

        public CompositeCommand<T, V> build() {
            final CompositeCommand<T, V> compositeCommand = new CompositeCommand<T, V>(reverse);
            commands.forEach(compositeCommand::addCommand);
            return compositeCommand;
        }
    }
}
