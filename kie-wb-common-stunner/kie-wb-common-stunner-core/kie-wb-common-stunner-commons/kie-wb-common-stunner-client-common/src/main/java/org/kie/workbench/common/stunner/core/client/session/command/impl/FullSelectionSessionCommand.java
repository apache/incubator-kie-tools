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

import java.util.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.ClipboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementsClearEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.graph.Element;

import static org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeysMatcher.doKeysMatch;
import static org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent.Key.A;
import static org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent.Key.CONTROL;

@Dependent
@Default
public class FullSelectionSessionCommand extends AbstractSelectionAwareSessionCommand<EditorSession> {

    private static Logger LOGGER = Logger.getLogger(FullSelectionSessionCommand.class.getName());

    private final Event<FullSelectionSessionCommandExecutedEvent> commandExecutedEvent;

    private ClipboardControl<Element, AbstractCanvas, ClientSession> clipboardControl;

    @Inject
    public FullSelectionSessionCommand(final Event<FullSelectionSessionCommandExecutedEvent> commandExecutedEvent, final SessionManager sessionManager) {
        super(true);
        this.commandExecutedEvent = commandExecutedEvent;
        SessionSingletonCommandsFactory.createOrPut(this, sessionManager);
    }

    public static FullSelectionSessionCommand getInstance(SessionManager sessionManager) {
        return SessionSingletonCommandsFactory.getInstanceFullSelection(null, sessionManager);
    }

    public static FullSelectionSessionCommand getInstance(final Event<FullSelectionSessionCommandExecutedEvent> commandExecutedEvent, SessionManager sessionManager) {
        return SessionSingletonCommandsFactory.getInstanceFullSelection(commandExecutedEvent, sessionManager);
    }

    @Override
    public void bind(final EditorSession session) {
        super.bind(session);
        session.getKeyboardControl().addKeyShortcutCallback(new KeyboardControl.KogitoKeyPress(
                new KeyboardEvent.Key[]{CONTROL, A}, "Full process selection", this::execute));
        session.getKeyboardControl().addKeyShortcutCallback(this::onKeyDownEvent);
        this.clipboardControl = session.getClipboardControl();
    }

    @Override
    public boolean accepts(final ClientSession session) {
        return session instanceof EditorSession;
    }

    protected void onKeyDownEvent(final KeyboardEvent.Key... keys) {
        handleCtrlA(keys);
    }

    private void handleCtrlA(final KeyboardEvent.Key[] keys) {
        if (doKeysMatch(keys, CONTROL, A)) {
            this.execute();
        }
    }

    @Override
    public <V> void execute(final Callback<V> callback) {
        if (getSession() != null && null != getSession().getSelectionControl()) {
            try {
                final SelectionControl<AbstractCanvasHandler, Element> selectionControl = getSession().getSelectionControl();
                for(String item : selectionControl.getItems().keySet()) {
                    getSession().getSelectionControl().select(item);
                }

                commandExecutedEvent.fire(new FullSelectionSessionCommandExecutedEvent(this,
                        getSession()));
                callback.onSuccess();
            } catch (Exception e) {
                LOGGER.severe("Error on Full selection." + e.getMessage());
                return;
            }
        }
    }

    @Override
    protected void doDestroy() {
        super.doDestroy();
        clipboardControl = null;
    }

    @Override
    protected void handleCanvasSelectionEvent(final CanvasSelectionEvent event) {
        if (event.getIdentifiers().isEmpty() || onlyCanvasRootSelected(event)) {
            enable(false);
        } else {
            enable(true);
        }
    }

    @Override
    protected void handleCanvasClearSelectionEvent(final CanvasClearSelectionEvent event) {
        enable(false);
    }

    @Override
    protected void handleCanvasElementsClearEvent(final CanvasElementsClearEvent event) {
        enable(false);
    }
}
