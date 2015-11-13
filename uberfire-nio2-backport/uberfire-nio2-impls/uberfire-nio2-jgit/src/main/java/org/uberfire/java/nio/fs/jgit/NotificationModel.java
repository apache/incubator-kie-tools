/*
 * Copyright 2015 JBoss, by Red Hat, Inc
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
