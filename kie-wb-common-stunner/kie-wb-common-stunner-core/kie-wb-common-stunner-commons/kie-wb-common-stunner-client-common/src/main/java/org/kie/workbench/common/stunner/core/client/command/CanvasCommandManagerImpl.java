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

package org.kie.workbench.common.stunner.core.client.command;

import java.util.function.Supplier;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.api.ClientFactoryManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandAllowedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandUndoneEvent;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandListener;
import org.kie.workbench.common.stunner.core.command.CommandManager;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.HasCommandListener;
import org.kie.workbench.common.stunner.core.command.impl.CommandManagerImpl;
import org.kie.workbench.common.stunner.core.graph.command.ContextualGraphCommandExecutionContext;

/**
 * The default canvas command manager implementation.
 * It operates with instances of type <code>CanvasCommand</code> and throw different context events.
 */
@Dependent
public class CanvasCommandManagerImpl<H extends AbstractCanvasHandler>
        implements
        CanvasCommandManager<H>,
        HasCommandListener<CommandListener<H, CanvasViolation>> {

    private final ClientFactoryManager clientFactoryManager;
    private final Event<CanvasCommandAllowedEvent> isCanvasCommandAllowedEvent;
    private final Event<CanvasCommandExecutedEvent> canvasCommandExecutedEvent;
    private final Event<CanvasCommandUndoneEvent> canvasUndoCommandExecutedEvent;

    private final CommandManager<H, CanvasViolation> commandManager;
    private CommandListener<H, CanvasViolation> listener;

    protected CanvasCommandManagerImpl() {
        this(null,
             null,
             null,
             null);
    }

    @Inject
    public CanvasCommandManagerImpl(final ClientFactoryManager clientFactoryManager,
                                    final Event<CanvasCommandAllowedEvent> isCanvasCommandAllowedEvent,
                                    final Event<CanvasCommandExecutedEvent> canvasCommandExecutedEvent,
                                    final Event<CanvasCommandUndoneEvent> canvasUndoCommandExecutedEvent) {
        this.clientFactoryManager = clientFactoryManager;
        this.isCanvasCommandAllowedEvent = isCanvasCommandAllowedEvent;
        this.canvasCommandExecutedEvent = canvasCommandExecutedEvent;
        this.canvasUndoCommandExecutedEvent = canvasUndoCommandExecutedEvent;
        this.commandManager = new CommandManagerImpl<>();
        this.listener = null;
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
        return runInContext(context,
                            () -> postExecute(context, command, commandManager.execute(context, command)));
    }

    @Override
    public CommandResult<CanvasViolation> undo(final H context,
                                               final Command<H, CanvasViolation> command) {
        return runInContext(context,
                            () -> postUndo(context, command, commandManager.undo(context, command)));
    }

    @Override
    public void setCommandListener(final CommandListener<H, CanvasViolation> listener) {
        this.listener = listener;
    }

    private CommandResult<CanvasViolation> runInContext(final AbstractCanvasHandler context,
                                                        final Supplier<CommandResult<CanvasViolation>> function) {
        final ContextualGraphCommandExecutionContext graphExecutionContext = newGraphExecutionContext(context);
        context.setGraphExecutionContext(() -> newGraphExecutionContext(context));
        final CommandResult<CanvasViolation> result = function.get();
        graphExecutionContext.clear();
        context.setGraphExecutionContext(() -> null);
        return result;
    }

    private ContextualGraphCommandExecutionContext newGraphExecutionContext(final AbstractCanvasHandler context) {
        return new ContextualGraphCommandExecutionContext(context.getDefinitionManager(),
                                                          clientFactoryManager,
                                                          context.getRuleManager(),
                                                          context.getGraphIndex(),
                                                          context.getRuleSet());
    }

    @SuppressWarnings("unchecked")
    private CommandResult<CanvasViolation> postAllow(final H context,
                                                     final Command<H, CanvasViolation> command,
                                                     final CommandResult<CanvasViolation> result) {
        if (null != this.listener) {
            listener.onAllow(context,
                             command,
                             result);
        }
        if (null != result && null != isCanvasCommandAllowedEvent) {
            isCanvasCommandAllowedEvent.fire(new CanvasCommandAllowedEvent(context,
                                                                           command,
                                                                           result));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private CommandResult<CanvasViolation> postExecute(final H context,
                                                       final Command<H, CanvasViolation> command,
                                                       final CommandResult<CanvasViolation> result) {
        if (null != this.listener) {
            listener.onExecute(context,
                               command,
                               result);
        }
        if (null != result && null != canvasCommandExecutedEvent) {
            canvasCommandExecutedEvent.fire(new CanvasCommandExecutedEvent(context,
                                                                           command,
                                                                           result));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private CommandResult<CanvasViolation> postUndo(final H context,
                                                    final Command<H, CanvasViolation> command,
                                                    final CommandResult<CanvasViolation> result) {
        if (null != this.listener) {
            listener.onUndo(context,
                            command,
                            result);
        }
        if (null != canvasUndoCommandExecutedEvent) {
            canvasUndoCommandExecutedEvent.fire(new CanvasCommandUndoneEvent(context,
                                                                             command,
                                                                             result));
        }
        return result;
    }
}


