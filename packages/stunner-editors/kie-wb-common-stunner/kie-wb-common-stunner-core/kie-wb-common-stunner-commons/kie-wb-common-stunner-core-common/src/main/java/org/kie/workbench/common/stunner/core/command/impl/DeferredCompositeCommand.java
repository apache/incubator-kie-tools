/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.kie.workbench.common.stunner.core.command.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;

import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.NonPortable;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;

/**
 * This type composites several commands but defers the creation of the command instances
 * until execution, it means that this command cannot be evaluated for allow.
 * <p>
 * This way dependencies between the composites commands are possible but consider not previous
 * allow evaluations can be done, so consider doing the right checks when using this type.
 * @param <T> The content type.
 * @param <V> The violation type.
 */
@Portable
public class DeferredCompositeCommand<T, V> extends AbstractCompositeCommand<T, V> {

    static final String ILLEGAL_ALLOW_MESSAGE = "Deferred commands cannot be evaluated previous to the execution";

    private final boolean reverse;

    public DeferredCompositeCommand(final @MapsTo("reverse") boolean reverse) {
        this.reverse = reverse;
    }

    @Override
    public CommandResult<V> allow(final T context) {
        throw new IllegalStateException(ILLEGAL_ALLOW_MESSAGE);
    }

    @Override
    public CommandResult<V> execute(final T context) {
        return executeCommands(context);
    }

    @Override
    protected CommandResult<V> doExecute(final T context,
                                         final Command<T, V> command) {
        CommandResult<V> violations = doAllow(context, command);
        if (!CommandUtils.isError(violations)) {
            violations = command.execute(context);
        }
        return violations;
    }

    @Override
    protected CommandResult<V> doUndo(final T context,
                                      final Command<T, V> command) {
        return command.undo(context);
    }

    @Override
    public boolean isUndoReverse() {
        return reverse;
    }

    @NonPortable
    public static class Builder<T, V> {

        private final List<Command<T, V>> commands = new LinkedList<>();
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

        public Builder<T, V> deferCommand(final Supplier<Command<T, V>> commandSupplier) {
            commands.add(new DeferredCommand<>(commandSupplier));
            return this;
        }

        public int size() {
            return commands.size();
        }

        public DeferredCompositeCommand<T, V> build() {
            final DeferredCompositeCommand<T, V> compositeCommand = new DeferredCompositeCommand<T, V>(reverse);
            commands.forEach(compositeCommand::addCommand);
            return compositeCommand;
        }
    }
}
