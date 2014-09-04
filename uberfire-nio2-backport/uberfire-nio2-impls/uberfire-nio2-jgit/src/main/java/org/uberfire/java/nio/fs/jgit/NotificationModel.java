package org.uberfire.java.nio.fs.jgit;

import org.eclipse.jgit.lib.ObjectId;

public class NotificationModel {

    private final ObjectId originalHead;
    private final String sessionId;
    private final String userName;
    private final String message;

    public NotificationModel( final ObjectId originalHead,
                              final String sessionId,
                              final String userName,
                              final String message ) {
        this.originalHead = originalHead;
        this.sessionId = sessionId;
        this.userName = userName;
        this.message = message;
    }

    public ObjectId getOriginalHead() {
        return originalHead;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getUserName() {
        return userName;
    }

    public String getMessage() {
        return message;
    }
}
