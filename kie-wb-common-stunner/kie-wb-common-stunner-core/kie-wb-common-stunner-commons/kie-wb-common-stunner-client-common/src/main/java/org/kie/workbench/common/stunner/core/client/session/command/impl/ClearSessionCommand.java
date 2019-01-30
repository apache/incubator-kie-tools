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
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.Session;
import org.kie.workbench.common.stunner.core.client.session.command.AbstractClientSessionCommand;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

import static java.util.logging.Level.FINE;
import static org.kie.soup.commons.validation.PortablePreconditions.checkNotNull;

/**
 * This session commands clear the canvas and internal graph structure.
 * As ClearCanvasCommand does not support undo, it clears the current session's command registry
 * after a successful execution.
 */
@Dependent
@Default
public class ClearSessionCommand extends AbstractClientSessionCommand<EditorSession> {

    private static Logger LOGGER = Logger.getLogger(ClearSessionCommand.class.getName());

    private final ManagedInstance<CanvasCommandFactory<AbstractCanvasHandler>> canvasCommandFactoryInstance;
    private final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager;
    private final Event<ClearSessionCommandExecutedEvent> commandExecutedEvent;
    private final DefinitionUtils definitionUtils;
    private CanvasCommandFactory<AbstractCanvasHandler> canvasCommandFactory;

    protected ClearSessionCommand() {
        this(null,
             null,
             null,
             null);
    }

    @Inject
    public ClearSessionCommand(final @Any ManagedInstance<CanvasCommandFactory<AbstractCanvasHandler>> canvasCommandFactoryInstance,
                               final @Session SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
                               final Event<ClearSessionCommandExecutedEvent> commandExecutedEvent,
                               final DefinitionUtils definitionUtils) {
        super(true);
        this.canvasCommandFactoryInstance = canvasCommandFactoryInstance;
        this.sessionCommandManager = sessionCommandManager;
        this.commandExecutedEvent = commandExecutedEvent;
        this.definitionUtils = definitionUtils;
    }

    @Override
    public void bind(final EditorSession session) {
        super.bind(session);
        canvasCommandFactory = this.loadCanvasFactory(canvasCommandFactoryInstance, definitionUtils);
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
        final CommandResult<CanvasViolation> result = getSession()
                .getCommandManager()
                .execute(getSession().getCanvasHandler(),
                         canvasCommandFactory.clearCanvas());
        if (!CommandUtils.isError(result)) {
            cleanSessionRegistry();
            commandExecutedEvent.fire(new ClearSessionCommandExecutedEvent(this,
                                                                           getSession()));
            callback.onSuccess();
        } else {
            callback.onError((V) result);
        }
    }

    private void cleanSessionRegistry() {
        LOGGER.log(FINE,
                   "Clear Session Command executed - Cleaning the session's command registry...");
        sessionCommandManager.getRegistry().clear();
    }
}
