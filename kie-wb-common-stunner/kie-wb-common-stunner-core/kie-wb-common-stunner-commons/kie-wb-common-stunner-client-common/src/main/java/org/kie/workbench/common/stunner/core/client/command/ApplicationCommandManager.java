/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.core.client.command;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Typed;
import javax.inject.Inject;

import org.jboss.errai.ioc.client.api.ManagedInstance;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.service.ClientRuntimeError;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.event.SessionDestroyedEvent;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.command.exception.CommandException;
import org.kie.workbench.common.stunner.core.command.util.CommandUtils;

@ApplicationScoped
@Typed(SessionCommandManager.class)
public class ApplicationCommandManager
        implements SessionCommandManager<AbstractCanvasHandler> {

    private final SessionManager sessionManager;
    private final MouseRequestLifecycle lifecycle;
    private final ManagedInstance<RegistryAwareCommandManager> commandManagerInstances;
    private final Map<String, RegistryAwareCommandManager> commandManagers;

    @Inject
    public ApplicationCommandManager(final SessionManager sessionManager,
                                     final MouseRequestLifecycle lifecycle,
                                     final @Any ManagedInstance<RegistryAwareCommandManager> commandManagerInstances) {
        this.sessionManager = sessionManager;
        this.lifecycle = lifecycle;
        this.commandManagerInstances = commandManagerInstances;
        this.commandManagers = new HashMap<>(3);
    }

    @PostConstruct
    public void init() {
        lifecycle.listen(() -> this);
    }

    @Override
    public void start() {
        getDelegate().start();
    }

    @Override
    public void rollback() {
        getDelegate().rollback();
    }

    @Override
    public void complete() {
        getDelegate().complete();
    }

    public CommandResult<CanvasViolation> allow(final Command<AbstractCanvasHandler, CanvasViolation> command) {
        return runExceptionSafeOperation(() -> getDelegate().allow(command));
    }

    public CommandResult<CanvasViolation> execute(final Command<AbstractCanvasHandler, CanvasViolation> command) {
        return runSafeOperation(() -> getDelegate().execute(command));
    }

    public CommandResult<CanvasViolation> undo() {
        return runSafeOperation(() -> getDelegate().undo());
    }

    @Override
    public CommandResult<CanvasViolation> allow(final AbstractCanvasHandler context,
                                                final Command<AbstractCanvasHandler, CanvasViolation> command) {
        return runExceptionSafeOperation(() -> getDelegate().allow(context, command));
    }

    @Override
    public CommandResult<CanvasViolation> execute(final AbstractCanvasHandler context,
                                                  final Command<AbstractCanvasHandler, CanvasViolation> command) {
        return runSafeOperation(() -> getDelegate().execute(context, command));
    }

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context,
                                               final Command<AbstractCanvasHandler, CanvasViolation> command) {
        return runSafeOperation(() -> getDelegate().undo(context, command));
    }

    @Override
    public CommandResult<CanvasViolation> undo(final AbstractCanvasHandler context) {
        return runSafeOperation(() -> getDelegate().undo(context));
    }

    @PreDestroy
    public void destroy() {
        getCommandManagers().clear();
        commandManagerInstances.destroyAll();
    }

    void onSessionDestroyed(@Observes SessionDestroyedEvent event) {
        final String sessionUUID = event.getSessionUUID();
        final RegistryAwareCommandManager commandManager = getCommandManagers().get(sessionUUID);
        commandManagerInstances.destroy(commandManager);
        getCommandManagers().remove(sessionUUID);
    }

    Map<String, RegistryAwareCommandManager> getCommandManagers() {
        return commandManagers;
    }

    private CommandResult<CanvasViolation> runSafeOperation(final Supplier<CommandResult<CanvasViolation>> operation) {
        final CommandResult<CanvasViolation> result = runExceptionSafeOperation(operation);
        if (CommandUtils.isError(result)) {
            rollback();
        }
        return result;
    }

    private CommandResult<CanvasViolation> runExceptionSafeOperation(final Supplier<CommandResult<CanvasViolation>> operation) {
        return runExceptionSafeOperation(operation, result -> {
        });
    }

    private CommandResult<CanvasViolation> runExceptionSafeOperation(final Supplier<CommandResult<CanvasViolation>> operation,
                                                                     final Consumer<CommandResult<CanvasViolation>> postOperation) {
        CommandResult<CanvasViolation> result = null;
        try {
            result = operation.get();
            if (!CommandUtils.isError(result)) {
                postOperation.accept(result);
            }
        } catch (final CommandException ce) {
            sessionManager.handleCommandError(ce);
            result = CanvasCommandResultBuilder.failed();
        } catch (final RuntimeException e) {
            sessionManager.handleClientError(new ClientRuntimeError(e));
            result = CanvasCommandResultBuilder.failed();
        }
        return result;
    }

    private RegistryAwareCommandManager getDelegate() {
        final ClientSession session = getCurrentSession();
        final String sessionUUID = session.getSessionUUID();
        RegistryAwareCommandManager commandManager = getCommandManagers().get(sessionUUID);
        if (null == commandManager) {
            commandManager = commandManagerInstances.get().init(session);
            getCommandManagers().put(sessionUUID, commandManager);
        }
        return commandManager;
    }

    private ClientSession getCurrentSession() {
        return sessionManager.getCurrentSession();
    }
}
