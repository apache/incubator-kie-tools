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
import org.guvnor.messageconsole.events.UnpublishMessagesEvent;
import org.kie.workbench.common.stunner.client.widgets.notification.AbstractNotification;
import org.kie.workbench.common.stunner.client.widgets.notification.NotificationsObserver;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.uberfire.backend.vfs.Path;

@Dependent
public class ProjectMessagesListener {

    public static final String MESSAGE_TYPE = "Stunner.";
    private final Event<PublishMessagesEvent> publishMessagesEvent;
    private final Event<UnpublishMessagesEvent> unpublishMessagesEvent;
    private final NotificationsObserver notificationsObserver;
    private final SessionManager clientSessionManager;

    protected ProjectMessagesListener() {
        this(null,
             null,
             null,
             null);
    }

    @Inject
    public ProjectMessagesListener(final NotificationsObserver notificationsObserver,
                                   final Event<PublishMessagesEvent> publishMessagesEvent,
                                   final Event<UnpublishMessagesEvent> unpublishMessagesEvent,
                                   final SessionManager clientSessionManager) {
        this.notificationsObserver = notificationsObserver;
        this.publishMessagesEvent = publishMessagesEvent;
        this.unpublishMessagesEvent = unpublishMessagesEvent;
        this.clientSessionManager = clientSessionManager;
    }

    public void enable() {
        notificationsObserver.onCommandExecutionFailed(parameter -> fireNotification(parameter));
        notificationsObserver.onValidationFailed(parameter -> fireNotification(parameter));
        notificationsObserver.onValidationExecuted(parameter -> clearMessages(parameter));
    }

    private Path getDiagramPath() {
        final ClientSession session = clientSessionManager.getCurrentSession();
        return session.getCanvasHandler().getDiagram().getMetadata().getPath();
    }

    void fireNotification(final AbstractNotification notification) {

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

        final Path path = getDiagramPath();
        systemMessage.setText(notification.getMessage());
        systemMessage.setPath(path);
        systemMessage.setMessageType(getMessageType(path));

        messagesList.add(systemMessage);
        PublishMessagesEvent messages = new PublishMessagesEvent();
        messages.setShowSystemConsole(false);
        messages.setMessagesToPublish(messagesList);
        publishMessagesEvent.fire(messages);
    }

    private String getMessageType(Path path) {
        return MESSAGE_TYPE + path.toURI();
    }

    protected void clearMessages(AbstractNotification notification) {
        final UnpublishMessagesEvent unpublishMessagesEvent = new UnpublishMessagesEvent();
        unpublishMessagesEvent.setMessageType(getMessageType(getDiagramPath()));
        this.unpublishMessagesEvent.fire(unpublishMessagesEvent);
    }
}
