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
package org.kie.workbench.common.command.client.exception;

import org.kie.workbench.common.command.client.Command;

/**
 * A command argument is not valid.
 * The command's executor should check the arguments on the context before calling the command.
 * For example if using a bad runtime execution context where element cannot be found in the index.
 */
public final class BadCommandArgumentsException extends CommandException {

    private final Object argument;

    public BadCommandArgumentsException(final Command<?, ?> command,
                                        final Object argument,
                                        final String message) {
        super("Bad argument: " + message,
              command);
        this.argument = argument;
    }

    public Object getArgument() {
        return argument;
    }
}
