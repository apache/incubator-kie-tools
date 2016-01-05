/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.uberfire.workbench.model;

import java.util.Date;

import org.jboss.errai.common.client.api.Assert;
import org.jboss.errai.common.client.api.annotations.MapsTo;
import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.workbench.events.NotificationEvent;
import org.uberfire.workbench.events.NotificationEvent.NotificationType;

/**
 * Represents a notification that has been displayed in the UberFire workbench.
 * <p>
 * Instances of this class are normally created by the Workbench Notification Manager in response to a
 * {@link NotificationEvent} being fired as a CDI event.
 *
 * @see NotificationEvent
 */
@Portable
public class Notification {

    public enum State {
        /**
         * The notification is newly created and has not yet been acknowledged by the user.
         */
        NEW,

        /**
         * The notification has been acknowledged by the user.
         */
        ACKNOWLEDGED
    }

    private NotificationType type;
    private String message;
    private Date timestamp;
    private State state;

    public Notification( @MapsTo("type") NotificationType type,
                         @MapsTo("message") String message,
                         @MapsTo("timestamp") Date timestamp,
                         @MapsTo("state") State state ) {
        this.type = Assert.notNull( type );
        this.message = Assert.notNull( message );
        this.timestamp = Assert.notNull( timestamp );
        this.state = Assert.notNull( state );
    }

    public NotificationType getType() {
        return type;
    }

    public void setType( NotificationType type ) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage( String message ) {
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp( Date timestamp ) {
        this.timestamp = timestamp;
    }

    public State getState() {
        return state;
    }

    public void setState( State state ) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "Notification [type=" + type + ", message=" + message + ", timestamp=" + timestamp + ", state=" + state + "]";
    }
}
