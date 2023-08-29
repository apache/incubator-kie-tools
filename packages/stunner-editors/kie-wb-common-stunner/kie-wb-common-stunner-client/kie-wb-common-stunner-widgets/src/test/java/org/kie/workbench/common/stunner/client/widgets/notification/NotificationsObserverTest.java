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

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandExecutedEvent;
import org.kie.workbench.common.stunner.core.client.canvas.event.command.CanvasCommandUndoneEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.validation.canvas.CanvasValidationFailEvent;
import org.kie.workbench.common.stunner.core.client.validation.canvas.CanvasValidationSuccessEvent;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.CommandResult;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.kie.workbench.common.stunner.core.rule.RuleViolation;
import org.kie.workbench.common.stunner.core.validation.DiagramElementViolation;
import org.kie.workbench.common.stunner.core.validation.DomainViolation;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.uberfire.mvp.ParameterizedCommand;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class NotificationsObserverTest {

    private static final String UUID = "uuid1";
    private static final String NAME = "name1";
    private static final String TITLE = "title1";

    @Mock
    ClientTranslationService translationService;
    @Mock
    CanvasHandler canvasHandler;
    @Mock
    Diagram diagram;
    @Mock
    Metadata metadata;
    @Mock
    ParameterizedCommand<Notification> onNotification;
    @Mock
    ParameterizedCommand<CommandNotification> commandSuccess;
    @Mock
    ParameterizedCommand<CommandNotification> commandFailed;
    @Mock
    ParameterizedCommand<ValidationSuccessNotification> validationSuccess;
    @Mock
    ParameterizedCommand<ValidationFailedNotification> validationFailed;
    @Mock
    ParameterizedCommand<ValidationExecutedNotification> validationExecuted;

    private NotificationsObserver tested;
    private NotificationContext notificationContext;
    private CommandNotification commandNotification;
    private ValidationSuccessNotification validationSuccessNotification;
    private ValidationFailedNotification validationFailedNotification;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() {
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getName()).thenReturn(NAME);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getTitle()).thenReturn(TITLE);

        this.notificationContext = new NotificationContext(UUID,
                                                           NAME,
                                                           TITLE);

        final NotificationsObserver.NotificationBuilder notificationBuilder =
                new NotificationsObserver.NotificationBuilder() {
                    @Override
                    public Notification createCommandNotification(NotificationContext context,
                                                                  Command<?, CanvasViolation> command,
                                                                  CommandResult<CanvasViolation> result) {
                        return commandNotification;
                    }

                    @Override
                    public Notification createValidationSuccessNotification(NotificationContext context) {
                        return validationSuccessNotification;
                    }

                    @Override
                    public Notification createValidationFailedNotification(NotificationContext context,
                                                                           DiagramElementViolation<RuleViolation> error) {
                        return validationFailedNotification;
                    }
                };

        this.tested = new NotificationsObserver(translationService, notificationBuilder);
        this.tested.onNotification(onNotification);
        this.tested.onCommandExecutionSuccess(commandSuccess);
        this.tested.onCommandExecutionFailed(commandFailed);
        this.tested.onValidationSuccess(validationSuccess);
        this.tested.onValidationFailed(validationFailed);
        this.tested.onValidationExecuted(validationExecuted);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNotifyCommandExecutionSuccess() {
        final Command command = mock(Command.class);
        final CommandResult<CanvasViolation> result = mock(CommandResult.class);
        final CanvasCommandExecutedEvent<? extends CanvasHandler> commandExecutedEvent =
                new CanvasCommandExecutedEvent<>(canvasHandler,
                                                 command,
                                                 result);
        commandNotification =
                new CommandNotification(Notification.Type.INFO,
                                        notificationContext,
                                        command,
                                        "message1");
        tested.onGraphCommandExecuted(commandExecutedEvent);
        verify(onNotification,
               times(1)).execute(eq(commandNotification));
        verify(commandSuccess,
               times(1)).execute(eq(commandNotification));
        verify(commandFailed,
               never()).execute(any(CommandNotification.class));
        verify(validationSuccess,
               never()).execute(any(ValidationSuccessNotification.class));
        verify(validationFailed,
               never()).execute(any(ValidationFailedNotification.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNotifyCommandExecutionFailed() {
        final Command command = mock(Command.class);
        final CommandResult<CanvasViolation> result = mock(CommandResult.class);
        final CanvasCommandExecutedEvent<? extends CanvasHandler> commandExecutedEvent =
                new CanvasCommandExecutedEvent<>(canvasHandler,
                                                 command,
                                                 result);
        commandNotification =
                new CommandNotification(Notification.Type.ERROR,
                                        notificationContext,
                                        command,
                                        "message1");
        tested.onGraphCommandExecuted(commandExecutedEvent);
        verify(onNotification,
               times(1)).execute(eq(commandNotification));
        verify(commandFailed,
               times(1)).execute(eq(commandNotification));
        verify(commandSuccess,
               never()).execute(any(CommandNotification.class));
        verify(validationSuccess,
               never()).execute(any(ValidationSuccessNotification.class));
        verify(validationFailed,
               never()).execute(any(ValidationFailedNotification.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNotifyCommandUndoSuccess() {
        final Command command = mock(Command.class);
        final CommandResult<CanvasViolation> result = mock(CommandResult.class);
        final CanvasCommandUndoneEvent<CanvasHandler> commandExecutedEvent =
                new CanvasCommandUndoneEvent<>(canvasHandler,
                                               command,
                                               result);
        commandNotification =
                new CommandNotification(Notification.Type.INFO,
                                        notificationContext,
                                        command,
                                        "message1");
        tested.onCanvasCommandUndoneEvent(commandExecutedEvent);
        verify(onNotification,
               times(1)).execute(eq(commandNotification));
        verify(commandSuccess,
               times(1)).execute(eq(commandNotification));
        verify(commandFailed,
               never()).execute(any(CommandNotification.class));
        verify(validationSuccess,
               never()).execute(any(ValidationSuccessNotification.class));
        verify(validationFailed,
               never()).execute(any(ValidationFailedNotification.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNotifyCommandUndoFailed() {
        final Command command = mock(Command.class);
        final CommandResult<CanvasViolation> result = mock(CommandResult.class);
        final CanvasCommandUndoneEvent<? extends CanvasHandler> commandExecutedEvent =
                new CanvasCommandUndoneEvent<>(canvasHandler,
                                               command,
                                               result);
        commandNotification =
                new CommandNotification(Notification.Type.ERROR,
                                        notificationContext,
                                        command,
                                        "message1");
        tested.onCanvasCommandUndoneEvent(commandExecutedEvent);
        verify(onNotification,
               times(1)).execute(eq(commandNotification));
        verify(commandFailed,
               times(1)).execute(eq(commandNotification));
        verify(commandSuccess,
               never()).execute(any(CommandNotification.class));
        verify(validationSuccess,
               never()).execute(any(ValidationSuccessNotification.class));
        verify(validationFailed,
               never()).execute(any(ValidationFailedNotification.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNotifyValidationSuccess() {
        validationSuccessNotification = new ValidationSuccessNotification("message",
                                                                          notificationContext);
        final CanvasValidationSuccessEvent validationSuccessEvent =
                new CanvasValidationSuccessEvent(UUID,
                                                 NAME,
                                                 TITLE);
        tested.onCanvasValidationSuccessEvent(validationSuccessEvent);
        final InOrder inOrder = inOrder(validationExecuted, onNotification, validationFailed,
                                        commandFailed, validationSuccess, commandSuccess);
        inOrder.verify(validationExecuted).execute(any(ValidationExecutedNotification.class));
        inOrder.verify(onNotification, times(1)).execute(any(Notification.class));
        inOrder.verify(validationSuccess, times(1)).execute(eq(validationSuccessNotification));
        inOrder.verify(commandFailed, never()).execute(any(CommandNotification.class));
        inOrder.verify(commandSuccess, never()).execute(any(CommandNotification.class));
        inOrder.verify(validationFailed, never()).execute(any(ValidationFailedNotification.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testNotifyValidationFailed() {
        final DomainViolation domainV1 = mock(DomainViolation.class);
        when(domainV1.getMessage()).thenReturn("message");
        when(translationService.getElementName(anyString())).thenReturn(Optional.of("name"));
        when(translationService.getValue(anyString(), anyString(), anyString())).thenReturn("message");

        final DiagramElementViolation violation = mock(DiagramElementViolation.class);
        final DiagramElementViolation violation2 = mock(DiagramElementViolation.class);
        when(violation.getDomainViolations()).thenReturn(Arrays.asList(domainV1));
        when(violation2.getDomainViolations()).thenReturn(Arrays.asList(domainV1));

        final Collection<DiagramElementViolation<RuleViolation>> violations = Arrays.asList(violation, violation2);
        validationFailedNotification = new ValidationFailedNotification(violations,
                                                                        notificationContext,
                                                                        "message1",
                                                                        Notification.Type.ERROR);
        final CanvasValidationFailEvent validationFailEvent =
                new CanvasValidationFailEvent(UUID,
                                              NAME,
                                              TITLE,
                                              violations);
        tested.onCanvasValidationFailEvent(validationFailEvent);
        final InOrder inOrder = inOrder(validationExecuted, onNotification, validationFailed,
                                        commandFailed, validationSuccess, commandSuccess);

        //verify in order calls
        inOrder.verify(validationExecuted).execute(any(ValidationExecutedNotification.class));
        inOrder.verify(onNotification, times(1)).execute(any(Notification.class));
        inOrder.verify(validationFailed, times(1)).execute(eq(validationFailedNotification));
        inOrder.verify(onNotification, times(1)).execute(any(Notification.class));
        inOrder.verify(validationFailed, times(1)).execute(eq(validationFailedNotification));
        inOrder.verify(commandFailed, never()).execute(any(CommandNotification.class));
        inOrder.verify(validationSuccess, never()).execute(any(ValidationSuccessNotification.class));
        inOrder.verify(commandSuccess, never()).execute(any(CommandNotification.class));

        //verify total calls
        verify(validationFailed, times(2)).execute(eq(validationFailedNotification));
        verify(onNotification, times(2)).execute(any(Notification.class));
    }
}
