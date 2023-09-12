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


package org.kie.workbench.common.stunner.core.client.command;

import java.util.Optional;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;

import org.appformer.client.stateControl.registry.Registry;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.client.session.impl.ViewerSession;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;

import static org.kie.workbench.common.stunner.core.command.util.CommandUtils.isError;

/**
 * - Binds to a single ClientSession.
 * - Delegates command operations to the session's CanvasCommandManager instance.
 * - Listens for request lifecycle, if any
 * - Updates the session's registry as:
 * - If commands have been successfully executed - Add a CompositeCommand by using all commands executed during the current request
 * - If some command execution fails
 * - It rolls-back all successfully executed commands during the current request to retun back to initial state.
 * - No registry updates are being done
 */
@Dependent
@Typed(RegistryAwareCommandManager.class)
public class RegistryAwareCommandManager
        implements SessionCommandManager<AbstractCanvasHandler> {

    private final RequestCommands commands;

    private final SessionManager sessionManager;

    @Inject
    public RegistryAwareCommandManager(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.commands =
                new RequestCommands.Builder()
                        .onComplete(command -> getCommandRegistry().ifPresent(r -> r.register(command)))
                        .onRollback(command -> undo(getCanvasHandler(), command))
                        .build();
    }

    public CommandResult<CanvasViolation> allow(final Command<AbstractCanvasHandler, CanvasViolation> command) {
        return allow(getCanvasHandler(), command);
    }

    public CommandResult<CanvasViolation> execute(final Command<AbstractCanvasHandler, CanvasViolation> command) {
        return execute(getCanvasHandler(), command);
    }

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
        final Command<AbstractCanvasHandler, CanvasViolation> lastEntry =
                getCommandRegistry()
                        .map(Registry::pop)
                        .orElse(null);
        if (null != lastEntry) {
            return undo(context, lastEntry);
        }
        return CanvasCommandResultBuilder.failed();
    }

    public CommandResult<CanvasViolation> undo() {
        return undo(getCanvasHandler());
    }

    @Override
    public CommandResult<CanvasViolation> allow(final AbstractCanvasHandler context,
                                                final Command<AbstractCanvasHandler, CanvasViolation> command) {
        return getCommandManager().allow(context, command);
    }

    @Override
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context,
                                                  final Command<AbstractCanvasHandler, CanvasViolation> command) {
        final CommandResult<CanvasViolation> result = getCommandManager().execute(context, command);
        if (isError(result)) {
            rollback();
        } else if (commands.isStarted()) {
            commands.push(command);
        } else {
            getCommandRegistry().ifPresent(r -> r.register(command));
        }
        return result;
    }

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context,
                                               final Command<AbstractCanvasHandler, CanvasViolation> command) {
        return getCommandManager().undo(context, command);
    }

    @Override
    public void start() {
        if (!commands.isStarted()) {
            commands.start();
        }
    }

    @Override
    public void rollback() {
        commands.rollback();
    }

    @Override
    public void complete() {
        commands.complete();
    }

    @PreDestroy
    public void destroy() {
        commands.clear();
    }

    private Optional<EditorSession> ifEditorSession() {
        if (sessionManager.getCurrentSession() instanceof EditorSession) {
            return Optional.of(sessionManager.getCurrentSession());
        }
        return Optional.empty();
    }

    private Optional<Registry<Command<AbstractCanvasHandler, CanvasViolation>>> getCommandRegistry() {
        return ifEditorSession().map(EditorSession::getCommandRegistry);
    }

    private CanvasCommandManager<AbstractCanvasHandler> getCommandManager() {
        final ClientSession session = sessionManager.getCurrentSession();
        if (session instanceof EditorSession) {
            return ((EditorSession) session).getCommandManager();
        } else {
            return ((ViewerSession) session).getCommandManager();
        }
    }

    private AbstractCanvasHandler getCanvasHandler() {
        return (AbstractCanvasHandler) sessionManager.getCurrentSession().getCanvasHandler();
    }
}
