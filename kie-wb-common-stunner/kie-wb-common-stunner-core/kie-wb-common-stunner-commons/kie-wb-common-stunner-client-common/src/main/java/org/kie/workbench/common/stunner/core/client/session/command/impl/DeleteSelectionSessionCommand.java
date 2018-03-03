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

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeysMatcher;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementsClearEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.ClientFullSession;
import org.kie.workbench.common.stunner.core.client.session.Session;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Element;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

/**
 * This session command obtains the selected elements on session and executes a delete operation for each one.
 * It also captures the <code>DELETE</code> keyboard event and fires the delete operation as well.
 */
@Dependent
public class DeleteSelectionSessionCommand extends AbstractSelectionAwareSessionCommand<ClientFullSession> {

    private static Logger LOGGER = Logger.getLogger(DeleteSelectionSessionCommand.class.getName());

    private final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;
    private final Event<CanvasClearSelectionEvent> clearSelectionEvent;

    protected DeleteSelectionSessionCommand() {
        this(null,
             null,
             null);
    }

    @Inject
    public DeleteSelectionSessionCommand(final @Session SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                         final CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory,
                                         final Event<CanvasClearSelectionEvent> clearSelectionEvent) {
        super(false);
        this.sessionCommandManager = sessionCommandManager;
        this.canvasCommandFactory = canvasCommandFactory;
        this.clearSelectionEvent = clearSelectionEvent;
    }

    @Override
    public void bind(final ClientFullSession session) {
        super.bind(session);
        session.getKeyboardControl().addKeyShortcutCallback(this::onKeyDownEvent);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> void execute(final Callback<V> callback) {
        checkNotNull("callback",
                     callback);
        if (null != getSession().getSelectionControl()) {
            final AbstractCanvasHandler canvasHandler = (AbstractCanvasHandler) getSession().getCanvasHandler();
            final SelectionControl<AbstractCanvasHandler, Element> selectionControl = getSession().getSelectionControl();
            final Collection<String> selectedItems = selectionControl.getSelectedItems();
            if (selectedItems != null && !selectedItems.isEmpty()) {
                // Execute the commands.
                final CommandResult<CanvasViolation> result =
                        sessionCommandManager.execute(canvasHandler,
                                                      canvasCommandFactory
                                                              .delete(selectedItems.stream()
                                                                              .map(uuid -> canvasHandler.getGraphIndex().get(uuid))
                                                                              .collect(Collectors.toList())));
                // Check the results.
                if (!CommandUtils.isError(result)) {
                    callback.onSuccess();
                } else {
                    callback.onError((V) new ClientRuntimeError("Error deleing elements [message=" +
                                                                        result.toString() + "]"));
                }
            } else {
                callback.onError((V) new ClientRuntimeError("Cannot delete element, no element selected on canvas"));
            }
            selectionControl.clearSelection();
            clearSelectionEvent.fire(new CanvasClearSelectionEvent(getCanvasHandler()));
        }
    }

    void onKeyDownEvent(final KeyboardEvent.Key... keys) {
        if (KeysMatcher.doKeysMatch(keys,
                                    KeyboardEvent.Key.DELETE)) {
            DeleteSelectionSessionCommand.this.execute(new Callback<ClientRuntimeError>() {
                @Override
                public void onSuccess() {
                    // Nothing to do.
                }

                @Override
                public void onError(final ClientRuntimeError error) {
                    LOGGER.log(Level.SEVERE,
                               "Error while trying to delete selected items. Message=[" + error.toString() + "]",
                               error.getThrowable());
                }
            });
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
