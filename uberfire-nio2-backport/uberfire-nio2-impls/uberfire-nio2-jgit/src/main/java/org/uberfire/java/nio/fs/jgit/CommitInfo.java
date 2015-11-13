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

import java.util.Date;
import java.util.TimeZone;

public class CommitInfo {

    private final String sessionId;
    private final String name;
    private final String email;
    private final String message;
    private final TimeZone timeZone;
    private final Date when;

    public CommitInfo( final String sessionId,
                       final String name,
                       final String email,
                       final String message,
                       final TimeZone timeZone,
                       final Date when ) {
        this.sessionId = sessionId;
        this.name = name;
        this.email = email;
        this.message = message;
        this.timeZone = timeZone;
        this.when = when;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMessage() {
        return message;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public Date getWhen() {
        return when;
    }
}
