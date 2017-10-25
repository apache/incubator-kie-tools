/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.guvnor.messageconsole.client.console;

import org.guvnor.common.services.shared.message.Level;
import org.guvnor.messageconsole.events.SystemMessage;
import org.uberfire.backend.vfs.Path;
import org.uberfire.paging.AbstractPageRow;

public class MessageConsoleServiceRow extends AbstractPageRow {

    String sessionId;

    String userId;

    SystemMessage message;

    public MessageConsoleServiceRow(String sessionId,
                                    String userId,
                                    SystemMessage message) {
        this.sessionId = sessionId;
        this.userId = userId;
        this.message = message;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public SystemMessage getMessage() {
        return message;
    }

    public void setMessage(SystemMessage message) {
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

    public Level getMessageLevel() {
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
