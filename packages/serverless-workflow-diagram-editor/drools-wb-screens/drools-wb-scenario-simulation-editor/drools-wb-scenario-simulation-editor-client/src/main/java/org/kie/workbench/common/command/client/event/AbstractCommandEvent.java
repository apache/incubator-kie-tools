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

package org.kie.workbench.common.command.client.event;

import org.kie.workbench.common.command.client.Command;
import org.kie.workbench.common.command.client.CommandResult;

public abstract class AbstractCommandEvent<C, V> {

    private final Command<C, V> command;
    private final CommandResult<V> result;

    public AbstractCommandEvent(final Command<C, V> command,
                                final CommandResult<V> result) {
        this.command = command;
        this.result = result;
    }

    public Command<C, V> getCommand() {
        return command;
    }

    public CommandResult<V> getResult() {
        return result;
    }

    public boolean hasError() {
        return result != null && CommandResult.Type.ERROR.equals(result.getType());
    }
}
