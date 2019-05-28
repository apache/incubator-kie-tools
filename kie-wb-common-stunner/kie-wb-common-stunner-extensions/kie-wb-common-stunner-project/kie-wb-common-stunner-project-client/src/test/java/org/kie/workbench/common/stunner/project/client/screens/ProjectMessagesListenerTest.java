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

package org.kie.workbench.common.stunner.project.client.screens;

import java.util.Collections;
import java.util.List;

import javax.enterprise.event.Event;

import org.guvnor.common.services.shared.message.Level;
import org.guvnor.messageconsole.events.PublishMessagesEvent;
import org.guvnor.messageconsole.events.SystemMessage;
import org.guvnor.messageconsole.events.UnpublishMessagesEvent;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.notification.AbstractNotification;
import org.kie.workbench.common.stunner.client.widgets.notification.CommandNotification;
import org.kie.workbench.common.stunner.client.widgets.notification.Notification;
import org.kie.workbench.common.stunner.client.widgets.notification.NotificationContext;
import org.kie.workbench.common.stunner.client.widgets.notification.NotificationsObserver;
import org.kie.workbench.common.stunner.client.widgets.notification.ValidationFailedNotification;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.i18n.ClientTranslationService;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.diagram.Diagram;
import org.kie.workbench.common.stunner.core.diagram.Metadata;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.uberfire.backend.vfs.Path;
import org.uberfire.mvp.ParameterizedCommand;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectMessagesListenerTest {

    public static final String PATH = "path";
    ProjectMessagesListener projectMessagesListener;
    @Mock
    private Event<PublishMessagesEvent> publishMessagesEvent;
    @Mock
    private Event<UnpublishMessagesEvent> unpublishMessagesEvent;
    @Mock
    private NotificationsObserver notificationsObserver;
    @Mock
    private ParameterizedCommand parameterizedCommand;
    @Mock
    private SessionManager clientSessionManager;
    @Mock
    private Path path;
    @Mock
    private ClientSession session;
    @Mock
    private CanvasHandler canvasHandler;
    @Mock
    private Diagram diagram;
    @Mock
    private Metadata metadata;

    @Before
    @SuppressWarnings("unchecked")
    public void setup() throws Exception {
        this.projectMessagesListener = spy(new ProjectMessagesListener(notificationsObserver,
                                                                       publishMessagesEvent,
                                                                       unpublishMessagesEvent,
                                                                       clientSessionManager));
        when(clientSessionManager.getCurrentSession()).thenReturn(session);
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getPath()).thenReturn(path);
        when(path.toURI()).thenReturn(PATH);
    }

    @Test
    public void testFireNotificationError() {
        NotificationContext context = buildNotificationContext();
        Command<?, CanvasViolation> source = mock(Command.class);
        CommandNotification commandNotification = CommandNotification.Builder.build(
                context,
                source,
                Notification.Type.ERROR,
                "message");

        projectMessagesListener.fireNotification(commandNotification);
        ArgumentCaptor<PublishMessagesEvent> eventCaptor = ArgumentCaptor.forClass(PublishMessagesEvent.class);
        verify(publishMessagesEvent, times(1)).fire(eventCaptor.capture());
        testMessageToPublish(eventCaptor.getValue(), Level.ERROR);
    }

    private void testMessageToPublish(PublishMessagesEvent messageToPublish, Level level) {
        assertFalse(messageToPublish.isShowSystemConsole());
        final List<SystemMessage> messagesToPublish = messageToPublish.getMessagesToPublish();

        assertEquals(messagesToPublish.size(),
                     1);
        SystemMessage message = messagesToPublish.get(0);
        assertEquals(message.getText(),
                     "message");
        assertEquals(message.getLevel(),
                     level);
        assertEquals(message.getText(), "message");
        assertEquals(message.getLevel(), level);
        assertEquals(message.getMessageType(), ProjectMessagesListener.MESSAGE_TYPE + PATH);
    }

    @Test
    public void testFireNotificationInfo() {
        NotificationContext context = buildNotificationContext();
        Command<?, CanvasViolation> source = mock(Command.class);
        CommandNotification commandNotification = CommandNotification.Builder.build(
                context,
                source,
                Notification.Type.INFO,
                "message");
        projectMessagesListener.fireNotification(commandNotification);
        ArgumentCaptor<PublishMessagesEvent> eventCaptor = ArgumentCaptor.forClass(PublishMessagesEvent.class);
        verify(publishMessagesEvent, times(1)).fire(eventCaptor.capture());
        testMessageToPublish(eventCaptor.getValue(), Level.INFO);
    }

    @Test
    public void testFireNotificationWarning() {
        NotificationContext context = buildNotificationContext();
        Command<?, CanvasViolation> source = mock(Command.class);
        CommandNotification commandNotification = CommandNotification.Builder.build(
                context,
                source,
                Notification.Type.WARNING,
                "message");
        projectMessagesListener.fireNotification(commandNotification);
        ArgumentCaptor<PublishMessagesEvent> eventCaptor = ArgumentCaptor.forClass(PublishMessagesEvent.class);
        verify(publishMessagesEvent, times(1)).fire(eventCaptor.capture());
        testMessageToPublish(eventCaptor.getValue(), Level.WARNING);
    }

    private NotificationContext buildNotificationContext() {
        return new NotificationContext.Builder().build("test",
                                                       "test",
                                                       "test");
    }

    @Test
    public void testClearMessages() {
        final ArgumentCaptor<UnpublishMessagesEvent> eventCaptor = ArgumentCaptor.forClass(UnpublishMessagesEvent.class);
        projectMessagesListener.clearMessages(mock(AbstractNotification.class));
        verify(unpublishMessagesEvent).fire(eventCaptor.capture());
        assertEquals(eventCaptor.getValue().getMessageType(), ProjectMessagesListener.MESSAGE_TYPE + PATH);
    }

    @Test
    public void testEnable() {
        projectMessagesListener.enable();
        final ArgumentCaptor<ParameterizedCommand> callbackCaptor =
                ArgumentCaptor.forClass(ParameterizedCommand.class);

        //onCommandExecutionFailed
        verify(notificationsObserver).onCommandExecutionFailed(callbackCaptor.capture());
        callbackCaptor.getAllValues().get(0).execute(CommandNotification.Builder.build(buildNotificationContext(),
                                                                                       mock(Command.class),
                                                                                       Notification.Type.INFO,
                                                                                       "message"));
        verify(projectMessagesListener, times(1)).fireNotification(any());

        //onValidationFailed
        verify(notificationsObserver).onValidationFailed(callbackCaptor.capture());
        callbackCaptor.getAllValues().get(1).execute(ValidationFailedNotification.Builder.build(mock(ClientTranslationService.class), buildNotificationContext(), Collections.emptyList()));
        verify(projectMessagesListener, times(2)).fireNotification(any());

        //onValidationExecuted
        verify(notificationsObserver).onValidationExecuted(callbackCaptor.capture());
        callbackCaptor.getAllValues().get(2).execute(null);
        verify(projectMessagesListener, times(1)).clearMessages(any());
    }
}
