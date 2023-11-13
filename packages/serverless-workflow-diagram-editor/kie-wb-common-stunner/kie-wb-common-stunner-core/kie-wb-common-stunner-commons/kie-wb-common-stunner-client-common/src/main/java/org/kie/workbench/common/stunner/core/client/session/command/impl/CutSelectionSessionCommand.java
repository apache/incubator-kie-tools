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


package org.kie.workbench.common.stunner.core.client.session.command.impl;

import java.util.logging.Logger;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.ClipboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl.KogitoKeyPress;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementsClearEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent.Key;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.command.Command;

import static org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeysMatcher.doKeysMatch;
import static org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent.Key.CONTROL;
import static org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent.Key.X;

/**
 * This session command copy to the clipboard using {@link CopySelectionSessionCommand} selected elements and delete them using the {@link DeleteSelectionSessionCommand}. *
 */
@Dependent
@Default
public class CutSelectionSessionCommand extends AbstractSelectionAwareSessionCommand<EditorSession> {

    private CopySelectionSessionCommand copySelectionSessionCommand;
    private final DeleteSelectionSessionCommand deleteSelectionSessionCommand;
    private final Event<CutSelectionSessionCommandExecutedEvent> commandExecutedEvent;
    private static Logger LOGGER = Logger.getLogger(CopySelectionSessionCommand.class.getName());
    private ClipboardControl clipboardControl;

    protected CutSelectionSessionCommand() {
        this(null,
             null);
    }

    @Inject
    public CutSelectionSessionCommand(final Event<CutSelectionSessionCommandExecutedEvent> commandExecutedEvent,
                                      final SessionManager sessionManager) {
        super(true);
        this.copySelectionSessionCommand = CopySelectionSessionCommand.getInstance(sessionManager);
        this.deleteSelectionSessionCommand = DeleteSelectionSessionCommand.getInstance(sessionManager);
        this.commandExecutedEvent = commandExecutedEvent;
    }

    @Override
    public void bind(final EditorSession session) {
        session.getKeyboardControl().addKeyShortcutCallback(new KogitoKeyPress(new Key[]{CONTROL, X}, "Edit | Cut selection", () -> {
            if (isEnabled()) {
                execute();
            }
        }));
        session.getKeyboardControl().addKeyShortcutCallback(this::onKeyDownEvent);

        super.bind(session);
        this.clipboardControl = session.getClipboardControl();
    }

    public void setCopySelectionSessionCommand(CopySelectionSessionCommand copySelectionSessionCommand) {
        this.copySelectionSessionCommand = copySelectionSessionCommand;
    }

    @Override
    public boolean accepts(final ClientSession session) {
        return session instanceof EditorSession;
    }

    protected void onKeyDownEvent(final Key... keys) {
        if (isEnabled()) {
            handleCtrlX(keys);
        }
    }

    private void handleCtrlX(final Key[] keys) {
        if (doKeysMatch(keys,
                        CONTROL,
                        Key.X)) {
            this.execute();
        }
    }

    @Override
    public <V> void execute(Callback<V> callback) {
        copySelectionSessionCommand.execute(new Callback<V>() {
            @Override
            public void onSuccess() {
                deleteSelectionSessionCommand.execute(callback);
                Command<AbstractCanvasHandler, CanvasViolation> command = getSession().getCommandRegistry().peek();
                clipboardControl.setRollbackCommand(command);
                commandExecutedEvent.fire(new CutSelectionSessionCommandExecutedEvent(CutSelectionSessionCommand.this,
                                                                                      CutSelectionSessionCommand.this.getSession()));
            }

            @Override
            public void onError(V error) {
                LOGGER.severe("Error on cut selection." + String.valueOf(error));
                callback.onError(error);
            }
        });
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
