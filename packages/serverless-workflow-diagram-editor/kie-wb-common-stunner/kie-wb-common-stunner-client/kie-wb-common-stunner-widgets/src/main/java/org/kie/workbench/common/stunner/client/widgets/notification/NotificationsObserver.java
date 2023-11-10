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


package org.kie.workbench.common.stunner.client.widgets.notification;

import java.util.Optional;

import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.AbstractCanvasCommandEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandUndoneEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class NotificationsObserver {

    private final ClientTranslationService translationService;

    private Optional<ParameterizedCommand<Notification>> onNotification;
    private Optional<ParameterizedCommand<CommandNotification>> commandSuccess;
    private Optional<ParameterizedCommand<CommandNotification>> commandFailed;
    private final NotificationBuilder notificationBuilder;

    @Inject
    public NotificationsObserver(final ClientTranslationService translationService) {
        this.translationService = translationService;
        this.notificationBuilder = new NotificationBuilderImpl();
        this.onNotification = Optional.empty();
        this.commandSuccess = Optional.empty();
        this.commandFailed = Optional.empty();
    }

    protected NotificationsObserver(ClientTranslationService translationService, NotificationBuilder notificationBuilder) {
        this.translationService = translationService;
        this.notificationBuilder = notificationBuilder;
    }

    public NotificationsObserver onNotification(final ParameterizedCommand<Notification> callback) {
        this.onNotification = Optional.ofNullable(callback);
        return this;
    }

    public NotificationsObserver onCommandExecutionSuccess(final ParameterizedCommand<CommandNotification> callback) {
        this.commandSuccess = Optional.ofNullable(callback);
        return this;
    }

    public NotificationsObserver onCommandExecutionFailed(final ParameterizedCommand<CommandNotification> callback) {
        this.commandFailed = Optional.ofNullable(callback);
        return this;
    }

    @PreDestroy
    public void destroy() {
        onNotification = null;
        commandFailed = null;
        commandSuccess = null;
    }

    @SuppressWarnings("unchecked")
    void onCanvasCommandUndoneEvent(final @Observes CanvasCommandUndoneEvent<? extends CanvasHandler> commandUndoneEvent) {
        final Notification notification = translateCommand(commandUndoneEvent);
        fireCommandNotification(notification);
    }

    private void fireCommandNotification(final Notification notification) {
        final boolean isError = Notification.Type.ERROR.equals(notification.getType());
        fireNotification(notification);
        if (isError) {
            commandFailed.ifPresent(c -> c.execute((CommandNotification) notification));
        } else {
            commandSuccess.ifPresent(c -> c.execute((CommandNotification) notification));
        }
    }

    @SuppressWarnings("unchecked")

    private Notification fireNotification(final Notification notification) {
        onNotification.ifPresent(n -> n.execute(notification));
        return notification;
    }

    @SuppressWarnings("unchecked")
    private Notification translateCommand(final AbstractCanvasCommandEvent<? extends CanvasHandler> commandExecutedEvent) {
        final CanvasHandler canvasHandler = commandExecutedEvent.getCanvasHandler();
        final NotificationContext context = NotificationContext.Builder.build(canvasHandler.toString(),
                                                                              canvasHandler.getDiagram().getName(),
                                                                              canvasHandler.getDiagram().getMetadata().getTitle());
        return notificationBuilder.createCommandNotification(context,
                                                             commandExecutedEvent.getCommand(),
                                                             commandExecutedEvent.getResult());
    }

    /**
     * Just an internal Notification instance builder type used for testing goals as well.
     */
    interface NotificationBuilder {

        Notification createCommandNotification(final NotificationContext context,
                                               final Command<?, CanvasViolation> command,
                                               final CommandResult<CanvasViolation> result);
    }

    private final class NotificationBuilderImpl implements NotificationBuilder {

        @Override
        public Notification createCommandNotification(final NotificationContext context,
                                                      final Command<?, CanvasViolation> command,
                                                      final CommandResult<CanvasViolation> result) {
            return CommandNotification.Builder.build(translationService,
                                                     context,
                                                     command,
                                                     result);
        }
    }
}
