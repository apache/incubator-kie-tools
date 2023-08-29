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

package org.kie.workbench.common.command.client.impl;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.kie.workbench.common.command.client.Command;
import org.kie.workbench.common.command.client.CommandListener;
import org.kie.workbench.common.command.client.CommandManager;
import org.kie.workbench.common.command.client.CommandResult;
import org.kie.workbench.common.command.client.HasCommandListener;
import org.kie.workbench.common.command.client.util.CommandUtils;

public class CommandManagerImpl<C, V> implements CommandManager<C, V>,
                                                 HasCommandListener<CommandListener<C, V>> {

    private static Logger LOGGER = Logger.getLogger(CommandManagerImpl.class.getName());

    private CommandListener<C, V> listener;

    public CommandManagerImpl() {
        this.listener = null;
    }

    @Override
    public CommandResult<V> allow(final C context,
                                  final Command<C, V> command) {
        Objects.requireNonNull(command, "command");
        logCommand("ALLOW",
                   command);
        final CommandResult<V> result = command.allow(context);
        if (null != listener) {
            listener.onAllow(context,
                             command,
                             result);
        }
        logResult(result);
        return result;
    }

    @Override
    public CommandResult<V> execute(final C context,
                                    final Command<C, V> command) {
        Objects.requireNonNull(command, "command");
        logCommand("EXECUTE",
                   command);
        final CommandResult<V> result = command.execute(context);
        if (null != listener) {
            listener.onExecute(context,
                               command,
                               result);
        }
        logResult(result);
        return result;
    }

    @Override
    public CommandResult<V> undo(final C context,
                                 final Command<C, V> command) {
        logCommand("UNDO",
                   command);
        final CommandResult<V> result = command.undo(context);
        if (null != listener) {
            listener.onUndo(context,
                            command,
                            result);
        }
        logResult(result);
        return result;
    }

    @Override
    public void setCommandListener(final CommandListener<C, V> listener) {
        this.listener = listener;
    }

    private static void logCommand(final String type,
                                   final Command<?, ?> command) {
        LOGGER.log(Level.FINE,
                   "Evaluating (" + type + ") command [" + command + "]...");
    }

    private static void logResult(final CommandResult<?> result) {
        LOGGER.log(Level.FINE,
                   "Evaluation " + (CommandUtils.isError(result) ? "FAILED" : "SUCCESS") + " - " +
                           "Result [" + result + "]");
    }
}
