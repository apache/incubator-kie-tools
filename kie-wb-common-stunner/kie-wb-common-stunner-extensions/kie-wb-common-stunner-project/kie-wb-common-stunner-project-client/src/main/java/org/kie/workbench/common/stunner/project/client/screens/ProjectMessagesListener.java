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

import java.util.ArrayList;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.guvnor.common.services.shared.message.Level;
import org.guvnor.messageconsole.events.PublishMessagesEvent;
import org.guvnor.messageconsole.events.SystemMessage;
import org.kie.workbench.common.stunner.client.widgets.notification.AbstractNotification;
import org.kie.workbench.common.stunner.client.widgets.notification.NotificationsObserver;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.uberfire.backend.vfs.Path;

@Dependent
public class ProjectMessagesListener {

    private final Event<PublishMessagesEvent> publishMessagesEvent;
    private final NotificationsObserver notificationsObserver;
    private final SessionManager clientSessionManager;

    protected ProjectMessagesListener() {
        this(null,
             null,
             null);
    }

    @Inject
    public ProjectMessagesListener(final NotificationsObserver notificationsObserver,
                                   final Event<PublishMessagesEvent> publishMessagesEvent,
                                   final SessionManager clientSessionManager) {
        this.publishMessagesEvent = publishMessagesEvent;
        this.notificationsObserver = notificationsObserver;
        this.clientSessionManager = clientSessionManager;
    }

    public void enable() {
        notificationsObserver.onCommandExecutionFailed(parameter -> fireNotification(parameter));
        notificationsObserver.onValidationFailed(parameter -> fireNotification(parameter));
    }

    void fireNotification(final AbstractNotification notification) {
        final ClientSession session = clientSessionManager.getCurrentSession();
        final Path path = session.getCanvasHandler().getDiagram().getMetadata().getPath();
        final SystemMessage systemMessage = new SystemMessage();
        final ArrayList<SystemMessage> messagesList = new ArrayList<>();

        switch (notification.getType()) {
            case ERROR:
                systemMessage.setLevel(Level.ERROR);
                break;
            case WARNING:
                systemMessage.setLevel(Level.WARNING);
                break;
            case INFO:
                systemMessage.setLevel(Level.INFO);
                break;
        }
        systemMessage.setText(notification.getMessage());
        systemMessage.setPath(path);
        messagesList.add(systemMessage);
        PublishMessagesEvent messages = new PublishMessagesEvent();
        messages.setMessagesToPublish(messagesList);
        publishMessagesEvent.fire(messages);
    }
}
