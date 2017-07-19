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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.service.ClientDiagramService;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.service.ServiceCallback;
import org.kie.workbench.common.stunner.core.client.session.ClientFullSession;
import org.kie.workbench.common.stunner.core.client.session.Session;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.graph.Graph;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.ParameterizedCommand;

import static org.uberfire.commons.validation.PortablePreconditions.checkNotNull;

@Dependent
public class RefreshSessionCommand extends AbstractClientSessionCommand<ClientFullSession> {

    private static Logger LOGGER = Logger.getLogger(RefreshSessionCommand.class.getName());

    private final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private final ClientDiagramService clientDiagramService;

    protected RefreshSessionCommand() {
        this(null,
             null);
    }

    @Inject
    public RefreshSessionCommand(final @Session SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                                 final ClientDiagramService clientDiagramService) {
        super(false);
        this.sessionCommandManager = sessionCommandManager;
        this.clientDiagramService = clientDiagramService;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V> void execute(final Callback<V> callback) {
        checkNotNull("callback",
                     callback);
        final Path path = getDiagramPath();
        LOGGER.log(Level.FINE,
                   "Refreshing diagram for path [" + path + "]...");
        // Clear the canvas handler.
        getSession().getCanvasHandler().clear();
        // Clear the session's registry.
        sessionCommandManager.getRegistry().clear();
        // Load again the diagram.
        clientDiagramService.getByPath(path,
                                       new ServiceCallback<Diagram<Graph, Metadata>>() {
                                           @Override
                                           public void onSuccess(final Diagram diagram) {
                                               LOGGER.log(Level.FINE,
                                                          "Refreshing diagram for path [" + path + "]...");
                                               getSession().getCanvasHandler().draw(diagram,
                                                                                    new ParameterizedCommand<CommandResult<?>>() {
                                                                                        @Override
                                                                                        public void execute(CommandResult<?> result) {
                                                                                            if (!CommandUtils.isError(result)) {
                                                                                                // TODO: Apply session commands again to restore latest client snapshot?
                                                                                                callback.onSuccess();
                                                                                            } else {
                                                                                                LOGGER.log(Level.SEVERE,
                                                                                                           "Error when drawing diagram for path [" + path + "] - " +
                                                                                                                   "[result=" + result + "]");
                                                                                                callback.onError((V) new ClientRuntimeError(result.toString()));
                                                                                            }
                                                                                        }
                                                                                    });
                                           }

                                           @Override
                                           public void onError(final ClientRuntimeError error) {
                                               LOGGER.log(Level.SEVERE,
                                                          "Error when loading diagram for path [" + path + "]",
                                                          error.getThrowable());
                                               callback.onError((V) error);
                                           }
                                       });
        checkState();
    }

    private Path getDiagramPath() {
        return null != getSession() ? getSession().getCanvasHandler().getDiagram().getMetadata().getPath() : null;
    }

    private void checkState() {
        setEnabled(hasSessionCommands());
        fire();
    }

    private boolean hasSessionCommands() {
        return null != getSession() && !sessionCommandManager.getRegistry().isEmpty();
    }
}
