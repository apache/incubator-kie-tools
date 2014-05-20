package org.kie.workbench.common.screens.messageconsole.client.console;

import org.kie.workbench.common.screens.messageconsole.events.SystemMessage;

import org.uberfire.backend.vfs.Path;

public class MessageConsoleServiceRow {

    String sessionId;

    String userId;

    SystemMessage message;

    public MessageConsoleServiceRow( String sessionId, String userId, SystemMessage message ) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.message = message;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId( String sessionId ) {
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId( String userId ) {
        this.userId = userId;
    }

    public SystemMessage getMessage() {
        return message;
    }

    public void setMessage( SystemMessage message ) {
        this.message = message;
    }


    public String getMessageType() {
        return getMessage() != null ? getMessage().getMessageType() : null;
    }

    public String getMessageUserId() {
        return getMessage() != null ? getMessage().getUserId() : null;
    }

    public long getMessageId() {
        return getMessage() != null ? getMessage().getId() : -1;
    }

    public SystemMessage.Level getMessageLevel() {
        return getMessage() != null ? getMessage().getLevel() : null;
    }

    public Path getMessagePath() {
        return getMessage() != null ? getMessage().getPath() : null;
    }

    public int getMessageLine() {
        return getMessage() != null ? getMessage().getLine() : 0;
    }

    public int getMessageColumn() {
        return getMessage() != null ? getMessage().getColumn() : 0;
    }

    public String getMessageText() {
        return getMessage() != null ? getMessage().getText() : null;
    }

}
