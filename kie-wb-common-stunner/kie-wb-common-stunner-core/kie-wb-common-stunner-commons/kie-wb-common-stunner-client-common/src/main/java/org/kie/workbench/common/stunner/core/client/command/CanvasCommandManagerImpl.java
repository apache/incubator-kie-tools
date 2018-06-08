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

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandAllowedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasUndoCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandListener;
import org.kie.workbench.common.stunner.core.command.CommandManager;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.DelegateCommandManager;
import org.kie.workbench.common.stunner.core.command.HasCommandListener;
import org.kie.workbench.common.stunner.core.command.impl.CommandManagerImpl;

/**
 * The default canvas command manager implementation.
 * It operates with instances of type <code>CanvasCommand</code> and throw different context events.
 */
@Dependent
public class CanvasCommandManagerImpl<H extends AbstractCanvasHandler>
        extends DelegateCommandManager<H, CanvasViolation>
        implements
        CanvasCommandManager<H>,
        HasCommandListener<CommandListener<H, CanvasViolation>> {

    private final Event<CanvasCommandAllowedEvent> isCanvasCommandAllowedEvent;
    private final Event<CanvasCommandExecutedEvent> canvasCommandExecutedEvent;
    private final Event<CanvasUndoCommandExecutedEvent> canvasUndoCommandExecutedEvent;

    private final CommandManager<H, CanvasViolation> commandManager;
    private CommandListener<H, CanvasViolation> listener;

    protected CanvasCommandManagerImpl() {
        this(null,
             null,
             null);
    }

    @Inject
    public CanvasCommandManagerImpl(final Event<CanvasCommandAllowedEvent> isCanvasCommandAllowedEvent,
                                    final Event<CanvasCommandExecutedEvent> canvasCommandExecutedEvent,
                                    final Event<CanvasUndoCommandExecutedEvent> canvasUndoCommandExecutedEvent) {
        this.isCanvasCommandAllowedEvent = isCanvasCommandAllowedEvent;
        this.canvasCommandExecutedEvent = canvasCommandExecutedEvent;
        this.canvasUndoCommandExecutedEvent = canvasUndoCommandExecutedEvent;
        this.commandManager = new CommandManagerImpl<>();
        this.listener = null;
    }

    @Override
    protected CommandManager<H, CanvasViolation> getDelegate() {
        return commandManager;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void postAllow(final H context,
                             final Command<H, CanvasViolation> command,
                             final CommandResult<CanvasViolation> result) {
        super.postAllow(context,
                        command,
                        result);
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
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void postExecute(final H context,
                               final Command<H, CanvasViolation> command,
                               final CommandResult<CanvasViolation> result) {
        super.postExecute(context,
                          command,
                          result);
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
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void postUndo(final H context,
                            final Command<H, CanvasViolation> command,
                            final CommandResult<CanvasViolation> result) {
        super.postUndo(context,
                       command,
                       result);
        if (null != this.listener) {
            listener.onUndo(context,
                            command,
                            result);
        }
        if (null != canvasUndoCommandExecutedEvent) {
            canvasUndoCommandExecutedEvent.fire(new CanvasUndoCommandExecutedEvent(context,
                                                                                   command,
                                                                                   result));
        }
    }

    @Override
    public void setCommandListener(final CommandListener<H, CanvasViolation> listener) {
        this.listener = listener;
    }
}


