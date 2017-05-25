/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.core.client.session.command.impl;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasUndoCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.event.keyboard.ClientKeyShortcutsHandler;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.event.keyboard.SessionKeyShortcutsHandler;
import org.kie.workbench.common.stunner.core.client.session.ClientFullSession;
import org.kie.workbench.common.stunner.core.client.session.Session;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
public class UndoSessionCommand extends AbstractClientSessionCommand<ClientFullSession> {

    private final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private final SessionKeyShortcutsHandler keyboardListener;

    protected UndoSessionCommand() {
        this(null,
             null);
    }

    @Inject
    public UndoSessionCommand(final @Session SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                              final SessionKeyShortcutsHandler keyboardListener) {
        super(false);
        this.sessionCommandManager = sessionCommandManager;
        this.keyboardListener = keyboardListener;
    }

    @PostConstruct
    public void init() {
        this.keyboardListener.setKeyShortcutCallback(keys -> {
            if (isUndoShortcut(keys)) {
                UndoSessionCommand.this.execute();
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> void execute(final Callback<V> callback) {
        checkNotNull("callback",
                     callback);
        final SessionCommandManager<AbstractCanvasHandler> scm = getSessionCommandManager();
        if (null != scm) {
            final CommandResult<CanvasViolation> result = getSessionCommandManager().undo((AbstractCanvasHandler) getSession().getCanvasHandler());
            checkState();
            if (CommandUtils.isError(result)) {
                callback.onError((V) result);
            } else {
                callback.onSuccess();
            }
        }
    }

    @Override
    public AbstractClientSessionCommand<ClientFullSession> bind(final ClientFullSession session) {
        super.bind(session);
        keyboardListener.bind(session);
        return this;
    }

    @Override
    public void unbind() {
        super.unbind();
        keyboardListener.unbind();
    }

    void onCommandExecuted(final @Observes CanvasCommandExecutedEvent commandExecutedEvent) {
        checkNotNull("commandExecutedEvent",
                     commandExecutedEvent);
        checkState();
    }

    void onCommandUndoExecuted(final @Observes CanvasUndoCommandExecutedEvent commandUndoExecutedEvent) {
        checkNotNull("commandUndoExecutedEvent",
                     commandUndoExecutedEvent);
        checkState();
    }

    private void checkState() {
        if (null != getSession()) {
            final SessionCommandManager<AbstractCanvasHandler> cm = getSessionCommandManager();
            final boolean isHistoryEmpty = cm == null || cm.getRegistry().getCommandHistory().isEmpty();
            setEnabled(!isHistoryEmpty);
        } else {
            setEnabled(false);
        }
        fire();
    }

    private SessionCommandManager<AbstractCanvasHandler> getSessionCommandManager() {
        return sessionCommandManager;
    }

    private boolean isUndoShortcut(final KeyboardEvent.Key... keys) {
        return ClientKeyShortcutsHandler.isSameShortcut(keys,
                                                        KeyboardEvent.Key.CONTROL,
                                                        KeyboardEvent.Key.Z);
    }
}
