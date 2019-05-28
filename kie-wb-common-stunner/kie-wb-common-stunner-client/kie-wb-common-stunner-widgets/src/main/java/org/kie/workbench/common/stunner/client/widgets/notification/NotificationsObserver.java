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

package org.kie.workbench.common.stunner.client.widgets.notification;

import java.util.Collection;
import java.util.Optional;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.AbstractCanvasCommandEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandUndoneEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.validation.canvas.CanvasValidationEvent;
import org.kie.workbench.common.stunner.core.client.validation.canvas.CanvasValidationFailEvent;
import org.kie.workbench.common.stunner.core.client.validation.canvas.CanvasValidationSuccessEvent;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.validation.DiagramElementViolation;
import org.uberfire.mvp.ParameterizedCommand;

@Dependent
public class NotificationsObserver {

    private final ClientTranslationService translationService;

    private Optional<ParameterizedCommand<Notification>> onNotification;
    private Optional<ParameterizedCommand<CommandNotification>> commandSuccess;
    private Optional<ParameterizedCommand<CommandNotification>> commandFailed;
    private Optional<ParameterizedCommand<ValidationSuccessNotification>> validationSuccess;
    private Optional<ParameterizedCommand<ValidationFailedNotification>> validationFailed;
    private Optional<ParameterizedCommand<ValidationExecutedNotification>> validationExecuted;
    NotificationBuilder notificationBuilder;

    @Inject
    public NotificationsObserver(final ClientTranslationService translationService) {
        this.translationService = translationService;
        this.notificationBuilder = new NotificationBuilderImpl();
        this.onNotification = Optional.empty();
        this.commandSuccess = Optional.empty();
        this.commandFailed = Optional.empty();
        this.validationSuccess = Optional.empty();
        this.validationFailed = Optional.empty();
        this.validationExecuted = Optional.empty();
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

    public NotificationsObserver onValidationSuccess(final ParameterizedCommand<ValidationSuccessNotification> callback) {
        this.validationSuccess = Optional.ofNullable(callback);
        return this;
    }

    public NotificationsObserver onValidationFailed(final ParameterizedCommand<ValidationFailedNotification> callback) {
        this.validationFailed = Optional.ofNullable(callback);
        return this;
    }

    public NotificationsObserver onValidationExecuted(final ParameterizedCommand<ValidationExecutedNotification> callback) {
        this.validationExecuted = Optional.ofNullable(callback);
        return this;
    }

    @PreDestroy
    public void destroy() {
        onNotification = null;
        commandFailed = null;
        commandSuccess = null;
        validationSuccess = null;
        validationFailed = null;
        notificationBuilder = null;
        validationExecuted = null;
    }

    @SuppressWarnings("unchecked")
    void onGraphCommandExecuted(final @Observes CanvasCommandExecutedEvent<? extends CanvasHandler> commandExecutedEvent) {
        final Notification notification = translateCommand(commandExecutedEvent);
        fireCommandNotification(notification);
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
    void onCanvasValidationSuccessEvent(final @Observes CanvasValidationSuccessEvent validationSuccessEvent) {
        final NotificationContext context = buildContext(validationSuccessEvent);

        //first call the executed notification
        handleValidationExecuted(context);

        handleValidationSuccess(context);
    }

    private void handleValidationExecuted(NotificationContext context) {
        validationExecuted.ifPresent(v -> v.execute(new ValidationExecutedNotification(context)));
    }

    private void handleValidationSuccess(NotificationContext context) {
        final Notification notification = notificationBuilder.createValidationSuccessNotification(context);
        fireNotification(notification);
        validationSuccess.ifPresent(n -> n.execute((ValidationSuccessNotification) notification));
    }

    @SuppressWarnings("unchecked")
    void onCanvasValidationFailEvent(final @Observes CanvasValidationFailEvent validationFailEvent) {
        final NotificationContext context = buildContext(validationFailEvent);

        //first call the executed notification
        handleValidationExecuted(context);

        handleValidationFailed(validationFailEvent, context);
    }

    private NotificationContext buildContext(CanvasValidationEvent event) {
        return NotificationContext.Builder.build(event.getCanvasHandlerUUID(),
                                                 event.getDiagramName(),
                                                 event.getDiagramTitle());
    }

    private void handleValidationFailed(CanvasValidationFailEvent validationFailEvent, NotificationContext context) {
        final Notification notification =
                notificationBuilder.createValidationFailedNotification(context,
                                                                       validationFailEvent.getViolations());
        fireNotification(notification);
        validationFailed.ifPresent(n -> n.execute((ValidationFailedNotification) notification));
    }

    private void fireNotification(final Notification notification) {
        onNotification.ifPresent(n -> n.execute(notification));
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

        Notification createValidationSuccessNotification(final NotificationContext context);

        Notification createValidationFailedNotification(final NotificationContext context,
                                                        final Collection<DiagramElementViolation<RuleViolation>> errors);
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

        @Override
        public Notification createValidationSuccessNotification(final NotificationContext context) {
            return ValidationSuccessNotification.Builder.build(translationService,
                                                               context);
        }

        @Override
        public Notification createValidationFailedNotification(final NotificationContext context,
                                                               final Collection<DiagramElementViolation<RuleViolation>> errors) {
            return ValidationFailedNotification.Builder.build(translationService,
                                                              context,
                                                              errors);
        }
    }
}
