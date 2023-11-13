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
import java.util.List;
import java.util.function.Supplier;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import org.kie.workbench.common.stunner.core.client.api.ClientFactoryManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandAllowedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandUndoneEvent;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandManager;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.impl.CommandManagerImpl;
import org.kie.workbench.common.stunner.core.graph.Element;

/**
 * The default canvas command manager implementation.
 * It operates with instances of type <code>CanvasCommand</code> and throw different context events.
 */
@Dependent
public class CanvasCommandManagerImpl<H extends AbstractCanvasHandler>
        implements
        CanvasCommandManager<H> {

    private final ClientFactoryManager clientFactoryManager;
    private final Event<CanvasCommandAllowedEvent> isCanvasCommandAllowedEvent;
    private final Event<CanvasCommandUndoneEvent> canvasUndoCommandExecutedEvent;

    private final CommandManager<H, CanvasViolation> commandManager;

    protected CanvasCommandManagerImpl() {
        this(null,
             null,
             null);
    }

    @Inject
    public CanvasCommandManagerImpl(final ClientFactoryManager clientFactoryManager,
                                    final Event<CanvasCommandAllowedEvent> isCanvasCommandAllowedEvent,
                                    final Event<CanvasCommandUndoneEvent> canvasUndoCommandExecutedEvent) {
        this.clientFactoryManager = clientFactoryManager;
        this.isCanvasCommandAllowedEvent = isCanvasCommandAllowedEvent;
        this.canvasUndoCommandExecutedEvent = canvasUndoCommandExecutedEvent;
        this.commandManager = new CommandManagerImpl<>();
    }

    @Override
    public CommandResult<CanvasViolation> allow(final H context,
                                                final Command<H, CanvasViolation> command) {
        return runInContext(context,
                            () -> postAllow(context, command, commandManager.allow(context, command)));
    }

    @Override
    public CommandResult<CanvasViolation> execute(final H context,
                                                  final Command<H, CanvasViolation> command) {
        return runInContext(context, () -> commandManager.execute(context, command));
    }

    @Override
    public CommandResult<CanvasViolation> undo(final H context,
                                               final Command<H, CanvasViolation> command) {
        return runInContext(context,
                            () -> postUndo(context, command, commandManager.undo(context, command)));
    }

    private CommandResult<CanvasViolation> runInContext(final AbstractCanvasHandler context,
                                                        final Supplier<CommandResult<CanvasViolation>> function) {

        final List<Element> queue = new ArrayList<>();
        final List<QueueGraphExecutionContext> contextsCreated = new ArrayList<>();

        context.setGraphExecutionContext(() -> {
            final QueueGraphExecutionContext queueGraphExecutionContext2 = newQueueGraphExecutionContext(context);
            contextsCreated.add(queueGraphExecutionContext2);
            return queueGraphExecutionContext2;
        });

        final CommandResult<CanvasViolation> result = function.get();

        boolean multipleSubqueues = false;
        for (QueueGraphExecutionContext contexts : contextsCreated) {
            if (!queue.isEmpty()) {
                multipleSubqueues = true;
                break;
            }
            queue.addAll(contexts.getUpdatedElements());
        }

        if (!queue.isEmpty() && !multipleSubqueues) { // Only send updates to first queue
            context.doBatchUpdate(queue);
        }

        for (QueueGraphExecutionContext contexts : contextsCreated) {
            contexts.resetUpdatedElements();
            contexts.clear();
        }

        context.setGraphExecutionContext(() -> null);
        return result;
    }

    public QueueGraphExecutionContext newQueueGraphExecutionContext(final AbstractCanvasHandler context) {
        return new QueueGraphExecutionContext(context.getDefinitionManager(),
                                              clientFactoryManager,
                                              context.getRuleManager(),
                                              context.getGraphIndex(),
                                              context.getRuleSet());
    }

    @SuppressWarnings("unchecked")
    private CommandResult<CanvasViolation> postAllow(final H context,
                                                     final Command<H, CanvasViolation> command,
                                                     final CommandResult<CanvasViolation> result) {
        if (null != result && null != isCanvasCommandAllowedEvent) {
            isCanvasCommandAllowedEvent.fire(new CanvasCommandAllowedEvent(context,
                                                                           command,
                                                                           result));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private CommandResult<CanvasViolation> postUndo(final H context,
                                                    final Command<H, CanvasViolation> command,
                                                    final CommandResult<CanvasViolation> result) {
        if (null != canvasUndoCommandExecutedEvent) {
            canvasUndoCommandExecutedEvent.fire(new CanvasCommandUndoneEvent(context,
                                                                             command,
                                                                             result));
        }
        return result;
    }
}
