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
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.select.SelectionControl;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.CanvasElementsClearEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.event.keyboard.KeyboardEvent;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.Session;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.graph.Element;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;
import static org.kie.workbench.common.stunner.core.client.canvas.controls.keyboard.KeysMatcher.doKeysMatch;

/**
 * This session command obtains the selected elements on session and executes a delete operation for each one.
 * It also captures the <code>DELETE</code> keyboard event and fires the delete operation as well.
 */
@Dependent
@Default
public class DeleteSelectionSessionCommand extends AbstractSelectionAwareSessionCommand<EditorSession> {

    private static Logger LOGGER = Logger.getLogger(DeleteSelectionSessionCommand.class.getName());

    private final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private final ManagedInstance<CanvasCommandFactory<AbstractCanvasHandler>> canvasCommandFactoryInstance;
    private final Event<CanvasClearSelectionEvent> clearSelectionEvent;
    private final DefinitionUtils definitionUtils;
    private CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;

    protected DeleteSelectionSessionCommand() {
        this(null,
             null,
             null,
             null);
    }

    @Inject
    public DeleteSelectionSessionCommand(final @Session SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                         final @Any ManagedInstance<CanvasCommandFactory<AbstractCanvasHandler>> canvasCommandFactoryInstance,
                                         final Event<CanvasClearSelectionEvent> clearSelectionEvent,
                                         final DefinitionUtils definitionUtils) {
        super(false);
        this.sessionCommandManager = sessionCommandManager;
        this.canvasCommandFactoryInstance = canvasCommandFactoryInstance;
        this.clearSelectionEvent = clearSelectionEvent;
        this.definitionUtils = definitionUtils;
    }

    @Override
    public void bind(final EditorSession session) {
        super.bind(session);
        session.getKeyboardControl().addKeyShortcutCallback(this::onKeyDownEvent);
        this.canvasCommandFactory = this.loadCanvasFactory(canvasCommandFactoryInstance, definitionUtils);
    }

    @Override
    public boolean accepts(final ClientSession session) {
        return session instanceof EditorSession;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> void execute(final Callback<V> callback) {
        checkNotNull("callback",
                     callback);
        if (null != getSession().getSelectionControl()) {
            final AbstractCanvasHandler canvasHandler = getSession().getCanvasHandler();
            final SelectionControl<AbstractCanvasHandler, Element> selectionControl = getSession().getSelectionControl();
            final Collection<String> selectedItems = selectionControl.getSelectedItems();

            clearSelectionEvent.fire(new CanvasClearSelectionEvent(canvasHandler));
            selectionControl.clearSelection();

            if (selectedItems != null && !selectedItems.isEmpty()) {
                // Execute the commands.
                final CommandResult<CanvasViolation> result =
                        sessionCommandManager.execute(canvasHandler,
                                                      canvasCommandFactory.delete(selectedItems.stream()
                                                                                          .map(uuid -> canvasHandler.getGraphIndex().get(uuid))
                                                                                          .filter(Objects::nonNull)
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
        }
    }

    protected void onKeyDownEvent(final KeyboardEvent.Key... keys) {
        if (isEnabled()) {
            handleDelete(keys);
        }
    }

    private void handleDelete(final KeyboardEvent.Key... keys) {
        if (doKeysMatch(keys,
                        KeyboardEvent.Key.DELETE)) {
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
