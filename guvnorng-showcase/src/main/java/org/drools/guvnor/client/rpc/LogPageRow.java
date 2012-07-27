/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.drools.guvnor.client.rpc;

import java.util.Date;

/**
 * A single row of the event log
 */
public class LogPageRow extends AbstractPageRow {

    private int    severity;  // TODO should be an enum
    private String message;
    private Date   timestamp;

    // ************************************************************************
    // Getters and setters
    // ************************************************************************

    public String getMessage() {
        return message;
    }

    public int getSeverity() {
        return severity;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

}
