package org.guvnor.structure.repositories.impl.git.event;

import org.uberfire.workbench.events.UberFireEvent;

public abstract class FileSystemHookNotificationEvent implements UberFireEvent {

    private NotificationType type;
    private String text;

    public FileSystemHookNotificationEvent(NotificationType type, String text) {
        this.type = type;
        this.text = text;
    }

    public NotificationType getType() {
        return type;
    }

    public String getText() {
        return text;
    }
}
