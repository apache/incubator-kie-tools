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

import java.util.List;

import javax.enterprise.event.Event;

import org.guvnor.common.services.shared.message.Level;
import org.guvnor.messageconsole.events.PublishMessagesEvent;
import org.guvnor.messageconsole.events.SystemMessage;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.workbench.common.stunner.client.widgets.notification.CommandNotification;
import org.kie.workbench.common.stunner.client.widgets.notification.Notification;
import org.kie.workbench.common.stunner.client.widgets.notification.NotificationContext;
import org.kie.workbench.common.stunner.client.widgets.notification.NotificationsObserver;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.CanvasHandler;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectMessagesListenerTest {

    ProjectMessagesListener projectMessagesListener;
    @Mock
    private Event<PublishMessagesEvent> publishMessagesEvent;
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
        this.projectMessagesListener = new ProjectMessagesListener(notificationsObserver,
                                                                   publishMessagesEvent,
                                                                   clientSessionManager
        );
        when(clientSessionManager.getCurrentSession()).thenReturn(session);
        when(session.getCanvasHandler()).thenReturn(canvasHandler);
        when(canvasHandler.getDiagram()).thenReturn(diagram);
        when(diagram.getMetadata()).thenReturn(metadata);
        when(metadata.getPath()).thenReturn(path);
    }

    @Test
    public void testFireNotificationError() {
        NotificationContext context = new NotificationContext.Builder().build("test",
                                                                              "test",
                                                                              "test");
        Command<?, CanvasViolation> source = mock(Command.class);
        CommandNotification commandNotification = CommandNotification.Builder.build(
                context,
                source,
                Notification.Type.ERROR,
                "message");

        projectMessagesListener.fireNotification(commandNotification);
        ArgumentCaptor<PublishMessagesEvent> eventCaptor = ArgumentCaptor.forClass(PublishMessagesEvent.class);
        verify(publishMessagesEvent,
               times(1)).fire(eventCaptor.capture());

        final List<SystemMessage> messagesToPublish = eventCaptor.getValue().getMessagesToPublish();

        assertEquals(messagesToPublish.size(),
                     1);
        SystemMessage message = messagesToPublish.get(0);
        assertEquals(message.getText(),
                     "message");
        assertEquals(message.getLevel(),
                     Level.ERROR);
    }

    @Test
    public void testFireNotificationInfo() {
        NotificationContext context = new NotificationContext.Builder().build("test",
                                                                              "test",
                                                                              "test");
        Command<?, CanvasViolation> source = mock(Command.class);
        CommandNotification commandNotification = CommandNotification.Builder.build(
                context,
                source,
                Notification.Type.INFO,
                "message");
        projectMessagesListener.fireNotification(commandNotification);
        ArgumentCaptor<PublishMessagesEvent> eventCaptor = ArgumentCaptor.forClass(PublishMessagesEvent.class);
        verify(publishMessagesEvent,
               times(1)).fire(eventCaptor.capture());

        final List<SystemMessage> messagesToPublish = eventCaptor.getValue().getMessagesToPublish();

        assertEquals(messagesToPublish.size(),
                     1);
        SystemMessage message = messagesToPublish.get(0);
        assertEquals(message.getText(),
                     "message");
        assertEquals(message.getLevel(),
                     Level.INFO);
    }

    @Test
    public void testFireNotificationWarning() {
        NotificationContext context = new NotificationContext.Builder().build("test",
                                                                              "test",
                                                                              "test");
        Command<?, CanvasViolation> source = mock(Command.class);
        CommandNotification commandNotification = CommandNotification.Builder.build(
                context,
                source,
                Notification.Type.WARNING,
                "message");
        projectMessagesListener.fireNotification(commandNotification);
        ArgumentCaptor<PublishMessagesEvent> eventCaptor = ArgumentCaptor.forClass(PublishMessagesEvent.class);
        verify(publishMessagesEvent,
               times(1)).fire(eventCaptor.capture());

        final List<SystemMessage> messagesToPublish = eventCaptor.getValue().getMessagesToPublish();

        assertEquals(messagesToPublish.size(),
                     1);
        SystemMessage message = messagesToPublish.get(0);
        assertEquals(message.getText(),
                     "message");
        assertEquals(message.getLevel(),
                     Level.WARNING);
    }
}
