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
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.RegisterChangedEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.Session;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;
import static org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeysMatcher.doKeysMatch;

@Dependent
@Default
public class UndoSessionCommand extends AbstractClientSessionCommand<EditorSession> {

    private final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;

    protected UndoSessionCommand() {
        this(null);
    }

    @Inject
    public UndoSessionCommand(final @Session SessionCommandManager<AbstractCanvasHandler> sessionCommandManager) {
        super(false);
        this.sessionCommandManager = sessionCommandManager;
    }

    @Override
    public void bind(final EditorSession session) {
        super.bind(session);
        session.getKeyboardControl().addKeyShortcutCallback(this::onKeyDownEvent);
    }

    @Override
    public boolean accepts(final ClientSession session) {
        return session instanceof EditorSession;
    }

    void onKeyDownEvent(final KeyboardEvent.Key... keys) {
        if (isEnabled()) {
            handleCtrlZ(keys);
        }
    }

    private void handleCtrlZ(final KeyboardEvent.Key[] keys) {
        if (doKeysMatch(keys,
                        KeyboardEvent.Key.CONTROL,
                        KeyboardEvent.Key.Z)) {
            this.execute();
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> void execute(final Callback<V> callback) {
        checkNotNull("callback",
                     callback);
        final SessionCommandManager<AbstractCanvasHandler> scm = getSessionCommandManager();
        if (null != scm) {
            final CommandResult<CanvasViolation> result = getSessionCommandManager().undo(getSession().getCanvasHandler());
            checkState();
            if (CommandUtils.isError(result)) {
                callback.onError((V) result);
            } else {
                callback.onSuccess();
            }
            getSession().getSelectionControl().clearSelection();
        }
    }

    void onCommandAdded(final @Observes RegisterChangedEvent registerChangedEvent) {
        checkNotNull("registerChangedEvent",
                     registerChangedEvent);
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
}
