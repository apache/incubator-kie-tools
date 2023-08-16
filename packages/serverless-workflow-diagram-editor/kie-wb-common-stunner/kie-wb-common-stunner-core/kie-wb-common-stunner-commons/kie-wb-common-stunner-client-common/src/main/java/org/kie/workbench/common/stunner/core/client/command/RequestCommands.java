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


package org.kie.workbench.common.stunner.core.client.command;

import java.util.ArrayList;
import java.util.Stack;
import java.util.function.Consumer;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;

public class RequestCommands implements CommandRequestLifecycle {

    private final Consumer<Command<AbstractCanvasHandler, CanvasViolation>> completedCommand;
    private final Consumer<Command<AbstractCanvasHandler, CanvasViolation>> rollbackCommand;
    private Stack<Command<AbstractCanvasHandler, CanvasViolation>> commands;
    private boolean rollback;

    public static class Builder {

        private Consumer<Command<AbstractCanvasHandler, CanvasViolation>> completedCommand;
        private Consumer<Command<AbstractCanvasHandler, CanvasViolation>> rollbackCommand;

        public Builder onComplete(Consumer<Command<AbstractCanvasHandler, CanvasViolation>> consumer) {
            this.completedCommand = consumer;
            return this;
        }

        public Builder onRollback(Consumer<Command<AbstractCanvasHandler, CanvasViolation>> consumer) {
            this.rollbackCommand = consumer;
            return this;
        }

        public RequestCommands build() {
            return new RequestCommands(completedCommand, rollbackCommand);
        }
    }

    RequestCommands(final Consumer<Command<AbstractCanvasHandler, CanvasViolation>> completedCommand,
                    final Consumer<Command<AbstractCanvasHandler, CanvasViolation>> rollbackCommand) {
        this.completedCommand = completedCommand;
        this.rollbackCommand = rollbackCommand;
    }

    @Override
    public void start() {
        commands = new Stack<>();
        rollback = false;
    }

    public void push(Command<AbstractCanvasHandler, CanvasViolation> command) {
        commands.push(command);
    }

    boolean isStarted() {
        return null != commands;
    }

    @Override
    public void rollback() {
        rollback = true;
    }

    @Override
    public void complete() {
        if (null != commands && !commands.isEmpty()) {
            final CompositeCommand<AbstractCanvasHandler, CanvasViolation> composite =
                    new CompositeCommand.Builder<AbstractCanvasHandler, CanvasViolation>()
                            .reverse()
                            .addCommands(new ArrayList<>(commands))
                            .build();

            if (rollback) {
                rollbackCommand.accept(composite);
            } else {
                completedCommand.accept(composite);
            }
        }
        clear();
    }

    void clear() {
        if (null != commands) {
            commands.clear();
            commands = null;
        }
        rollback = false;
    }
}
