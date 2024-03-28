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

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;
import org.kie.j2cl.tools.di.core.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvas;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.Canvas;
import org.kie.workbench.common.stunner.core.client.canvas.controls.DeleteNodeConfirmation;
import org.kie.workbench.common.stunner.core.client.canvas.controls.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeyboardControl.KogitoKeyPress;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementsClearEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent.Key;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

import static org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeysMatcher.doKeysMatch;
import static org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent.Key.DELETE;
import static org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent.Key.KEY_BACKSPACE;

/**
 * This session command obtains the selected elements on session and executes a delete operation for each one.
 * It also captures the <code>DELETE</code> keyboard event and fires the delete operation as well.
 */
@Dependent
@Default
public class DeleteSelectionSessionCommand extends AbstractSelectionAwareSessionCommand<EditorSession> {

    private final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private final ManagedInstance<CanvasCommandFactory<AbstractCanvasHandler>> canvasCommandFactoryInstance;
    private final Event<CanvasClearSelectionEvent> clearSelectionEvent;
    private final DefinitionUtils definitionUtils;
    private final DeleteNodeConfirmation deleteNodeConfirmation;

    private CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;

    protected DeleteSelectionSessionCommand() {
        this(null,
             null,
             null,
             null,
             null,
             null
        );
    }

    @Inject
    public DeleteSelectionSessionCommand(final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                         final @Any ManagedInstance<CanvasCommandFactory<AbstractCanvasHandler>> canvasCommandFactoryInstance,
                                         final Event<CanvasClearSelectionEvent> clearSelectionEvent,
                                         final DefinitionUtils definitionUtils,
                                         final SessionManager sessionmanager,
                                         final DeleteNodeConfirmation deleteNodeConfirmation) {
        super(false);
        this.sessionCommandManager = sessionCommandManager;
        this.canvasCommandFactoryInstance = canvasCommandFactoryInstance;
        this.clearSelectionEvent = clearSelectionEvent;
        this.definitionUtils = definitionUtils;
        this.deleteNodeConfirmation = deleteNodeConfirmation;
        SessionSingletonCommandsFactory.createOrPut(this, sessionmanager);
    }

    void setCanvasCommandFactory(final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory) {
        this.canvasCommandFactory = canvasCommandFactory;
    }

    public static DeleteSelectionSessionCommand getInstance(SessionManager sessionManager) {
        return SessionSingletonCommandsFactory.getInstanceDelete(null, null, null, null, sessionManager, null);
    }

    public static DeleteSelectionSessionCommand getInstance(final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                                            final ManagedInstance<CanvasCommandFactory<AbstractCanvasHandler>> canvasCommandFactoryInstance,
                                                            final Event<CanvasClearSelectionEvent> clearSelectionEvent,
                                                            final DefinitionUtils definitionUtils,
                                                            final SessionManager sessionmanager,
                                                            final DeleteNodeConfirmation deleteNodeConfirmation) {

        return SessionSingletonCommandsFactory.getInstanceDelete(sessionCommandManager, canvasCommandFactoryInstance, clearSelectionEvent, definitionUtils, sessionmanager, deleteNodeConfirmation);
    }

    @Override
    public void bind(final EditorSession session) {
        superBind(session);
        session.getKeyboardControl().addKeyShortcutCallback(getShortcutCallback(KEY_BACKSPACE));
        session.getKeyboardControl().addKeyShortcutCallback(getShortcutCallback(DELETE));
        session.getKeyboardControl().addKeyShortcutCallback(getOnKeyDownEvent());
        setCanvasCommandFactory(this.loadCanvasFactory(canvasCommandFactoryInstance, definitionUtils));
    }

    KeyboardControl.KeyShortcutCallback getOnKeyDownEvent() {
        return this::onKeyDownEvent;
    }

    void superBind(final EditorSession session) {
        super.bind(session);
    }

    KogitoKeyPress getShortcutCallback(final Key keyBackspace) {
        return new KogitoKeyPress(new Key[]{keyBackspace}, "Edit | Delete selection", () -> {
            if (isEnabled() && isEventHandlesEnabled()) {
                execute();
            }
        });
    }

    boolean isEventHandlesEnabled() {
        final Canvas canvas = getSession().getCanvasHandler().getCanvas();
        if (canvas instanceof AbstractCanvas) {
            return ((AbstractCanvas) canvas).isEventHandlesEnabled();
        }
        return true;
    }

    @Override
    public boolean accepts(final ClientSession session) {
        return session instanceof EditorSession;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> void execute(final Callback<V> callback) {
        Objects.requireNonNull(callback, "Parameter named 'callback' should be not null!");
        if (null != getSession() && null != getSession().getSelectionControl()) {
            final AbstractCanvasHandler canvasHandler = getSession().getCanvasHandler();
            final SelectionControl<AbstractCanvasHandler, Element> selectionControl = getSession().getSelectionControl();
            final Collection<String> selectedItems = selectionControl.getSelectedItems();

            if (selectedItems != null && !selectedItems.isEmpty()) {
                final List<Element> elements = selectedItems.stream()
                        .map(uuid -> canvasHandler.getGraphIndex().get(uuid))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                if (!Objects.isNull(deleteNodeConfirmation)
                        && deleteNodeConfirmation.requiresDeletionConfirmation(elements)) {
                    deleteNodeConfirmation.confirmDeletion(() -> executeDelete(elements,
                                                                               canvasHandler,
                                                                               selectionControl,
                                                                               callback),
                                                           () -> {
                                                               // do nothing if user cancels
                                                           },
                                                           elements);
                } else {
                    executeDelete(elements,
                                  canvasHandler,
                                  selectionControl,
                                  callback);
                }
            } else {
                callback.onError((V) new ClientRuntimeError("Cannot delete element, no element selected on canvas"));
            }
        }
    }

    private <V> void executeDelete(final List<Element> elements,
                                   final AbstractCanvasHandler canvasHandler,
                                   final SelectionControl<AbstractCanvasHandler, Element> selectionControl,
                                   final Callback<V> callback) {

        clearSelectionEvent.fire(new CanvasClearSelectionEvent(canvasHandler));
        selectionControl.clearSelection();

        // Execute the commands.
        final CommandResult<CanvasViolation> result =
                sessionCommandManager.execute(canvasHandler,
                                              canvasCommandFactory.delete(elements));

        // Check the results.
        if (!CommandUtils.isError(result)) {
            callback.onSuccess();
        } else {
            callback.onError((V) new ClientRuntimeError("Error deleing elements [message=" +
                                                                result.toString() + "]"));
        }
    }

    protected void onKeyDownEvent(final Key... keys) {
        if (isEnabled() && isEventHandlesEnabled()) {
            handleDelete(keys);
        }
    }

    private void handleDelete(final Key... keys) {
        if ((doKeysMatch(keys, Key.DELETE)) || doKeysMatch(keys, Key.KEY_BACKSPACE)) {
            this.execute();
        }
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
