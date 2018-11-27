/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.guvnor.structure.client.repositories.git;

import org.guvnor.structure.repositories.impl.git.event.FileSystemHookNotificationEvent;
import org.guvnor.structure.repositories.impl.git.event.PostCommitNotificationEvent;
import org.uberfire.workbench.events.NotificationEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

@ApplicationScoped
public class FileSystemHookNotifier {

    private Event<NotificationEvent> notificationEvent;

    @Inject
    public FileSystemHookNotifier(Event<NotificationEvent> notificationEvent) {
        this.notificationEvent = notificationEvent;
    }

    public void notify(@Observes PostCommitNotificationEvent event) {
        notificationEvent.fire(new NotificationEvent(event.getText(), getNotificationType(event)));
    }

    private NotificationEvent.NotificationType getNotificationType(FileSystemHookNotificationEvent event) {
        switch (event.getType()) {
            case SUCCESS: return NotificationEvent.NotificationType.SUCCESS;
            case WARNING: return NotificationEvent.NotificationType.WARNING;
            default: return NotificationEvent.NotificationType.ERROR;
        }
    }
}
