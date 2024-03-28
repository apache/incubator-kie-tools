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

import java.util.HashMap;

import jakarta.enterprise.event.Event;
import jakarta.inject.Singleton;
import org.kie.j2cl.tools.di.core.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.controls.DeleteNodeConfirmation;
import org.kie.workbench.common.stunner.core.client.canvas.event.selection.CanvasClearSelectionEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasCommandFactory;
import org.kie.workbench.common.stunner.core.client.command.SessionCommandManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.util.DefinitionUtils;

/**
 * The purpose of this class is to have only one copy of Copy, Delete per Client Session.
 * An alternate easier aproach was tried with @Produces on those classes, but the issue is that @Produces Factory from erray does not generate
 * Decorators for @Observes extension and the result was that commands created using @Produces did not listen for Events
 */
@Singleton
public class SessionSingletonCommandsFactory {

    private static HashMap<ClientSession, CopySelectionSessionCommand> copySessionInstances = new HashMap<>();

    private static HashMap<ClientSession, DeleteSelectionSessionCommand> deleteSessionInstances = new HashMap<>();

    public static void createOrPut(AbstractSelectionAwareSessionCommand<EditorSession> command, SessionManager sessionManager) {

        if (sessionManager == null) {
            throw new IllegalStateException("Session Manager is Null");
        }

        if (command instanceof CopySelectionSessionCommand) {
            if (copySessionInstances.containsKey(sessionManager.getCurrentSession())) { // there is one already one
                throw new IllegalStateException("Only one instance of CopySelectionSessionCommand per Client Session can exist");
            }

            copySessionInstances.put(sessionManager.getCurrentSession(), (CopySelectionSessionCommand) command);
        } else if (command instanceof DeleteSelectionSessionCommand) {
            if (deleteSessionInstances.containsKey(sessionManager.getCurrentSession())) { // there is one already one
                throw new IllegalStateException("Only one instance of DeleteSelectionSessionCommand per Client Session can exist");
            }
            deleteSessionInstances.put(sessionManager.getCurrentSession(), (DeleteSelectionSessionCommand) command);
        } else {
            throw new UnsupportedOperationException("Session Command Not Compatible Yet : " + command.getClass());
        }
    }

    public static CopySelectionSessionCommand getInstanceCopy(
            final Event<?> commandExecutedEvent,
            SessionManager sessionManager) {

        final ClientSession currentSession = sessionManager.getCurrentSession();

        if (!copySessionInstances.containsKey(currentSession)) {
            final CopySelectionSessionCommand copySelectionSessionCommand = new CopySelectionSessionCommand((Event<CopySelectionSessionCommandExecutedEvent>) commandExecutedEvent, sessionManager);

            return copySelectionSessionCommand;
        }

        final CopySelectionSessionCommand copySelectionSessionCommand = copySessionInstances.get(currentSession);

        return copySelectionSessionCommand;
    }

    public static DeleteSelectionSessionCommand getInstanceDelete(
            final SessionCommandManager<AbstractCanvasHandler> sessionCommandManager,
            final ManagedInstance<CanvasCommandFactory<AbstractCanvasHandler>> canvasCommandFactoryInstance,
            final Event<CanvasClearSelectionEvent> clearSelectionEvent,
            final DefinitionUtils definitionUtils,
            final SessionManager sessionManager,
            final DeleteNodeConfirmation deleteNodeConfirmation) {
        final ClientSession currentSession = sessionManager.getCurrentSession();

        if (!deleteSessionInstances.containsKey(currentSession)) {
            return new DeleteSelectionSessionCommand(sessionCommandManager, canvasCommandFactoryInstance, clearSelectionEvent, definitionUtils, sessionManager, deleteNodeConfirmation);
        }

        return deleteSessionInstances.get(currentSession);
    }
}
