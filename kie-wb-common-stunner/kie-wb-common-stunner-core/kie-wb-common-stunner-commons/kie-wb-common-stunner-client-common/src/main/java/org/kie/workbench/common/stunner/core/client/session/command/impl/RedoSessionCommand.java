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

package org.kie.workbench.common.stunner.core.client.session.command.impl;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandUndoneEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.ClientRedoCommandHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;
import static org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeysMatcher.doKeysMatch;

@Dependent
@Default
public class RedoSessionCommand extends AbstractClientSessionCommand<EditorSession> {

    private final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private final ClientRedoCommandHandler<Command<AbstractCanvasHandler, CanvasViolation>> redoCommandHandler;

    protected RedoSessionCommand() {
        this(null,
             null);
    }

    @Inject
    public RedoSessionCommand(final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                              final ClientRedoCommandHandler<Command<AbstractCanvasHandler, CanvasViolation>> redoCommandHandler) {
        super(false);
        this.redoCommandHandler = redoCommandHandler;
        this.sessionCommandManager = sessionCommandManager;
    }

    @Override
    public void bind(final EditorSession session) {
        super.bind(session);
        session.getKeyboardControl().addKeyShortcutCallback(this::onKeyDownEvent);
        redoCommandHandler.setSession(getSession());
    }

    @Override
    public boolean accepts(final ClientSession session) {
        return session instanceof EditorSession;
    }

    void onKeyDownEvent(final KeyboardEvent.Key... keys) {
        if (isEnabled()) {
            handleCtrlShiftZ(keys);
        }
    }

    private void handleCtrlShiftZ(final KeyboardEvent.Key[] keys) {
        if (doKeysMatch(keys,
                        KeyboardEvent.Key.CONTROL,
                        KeyboardEvent.Key.SHIFT,
                        KeyboardEvent.Key.Z)) {
            this.execute();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> void execute(final Callback<V> callback) {
        checkNotNull("callback",
                     callback);
        if (!redoCommandHandler.isEnabled()) {
            callback.onSuccess();
        }
        final CommandResult<?> result = redoCommandHandler.execute(getSession().getCanvasHandler(),
                                                                   sessionCommandManager);
        checkState();
        if (CommandUtils.isError(result)) {
            callback.onError((V) result);
        } else {
            callback.onSuccess();
        }
        getSession().getSelectionControl().clearSelection();
    }

    @SuppressWarnings("unchecked")
    void onCommandExecuted(final @Observes CanvasCommandExecutedEvent commandExecutedEvent) {
        checkNotNull("commandExecutedEvent",
                     commandExecutedEvent);
        if (getSession().getCanvasHandler().equals(commandExecutedEvent.getCanvasHandler())) {
            if (null != commandExecutedEvent.getCommand()) {
                redoCommandHandler.onCommandExecuted(commandExecutedEvent.getCommand());
            }
            checkState();
        }
    }

    @SuppressWarnings("unchecked")
    void onCommandUndoExecuted(final @Observes CanvasCommandUndoneEvent commandUndoExecutedEvent) {
        checkNotNull("commandUndoExecutedEvent",
                     commandUndoExecutedEvent);
        CanvasHandler canvasHandler = commandUndoExecutedEvent.getCanvasHandler();
        if (getSession().getCanvasHandler().equals(canvasHandler)) {
            if (null != commandUndoExecutedEvent.getCommand()) {
                redoCommandHandler.onUndoCommandExecuted(commandUndoExecutedEvent.getCommand());
            }
            checkState();
        }
    }

    private void checkState() {
        setEnabled(null != getSession() && redoCommandHandler.isEnabled());
        fire();
    }
}
