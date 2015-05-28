/*
 * Copyright 2012 JBoss Inc
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
package org.uberfire.workbench.events;

/**
 * An event to show a notification pop-up in the Workbench
 */
public class NotificationEvent implements UberFireEvent {

    public static enum NotificationType {
        DEFAULT, ERROR, SUCCESS, INFO, WARNING
    }

    private final String notification;
    private final NotificationType type;
    private final boolean isSingleton;

    public NotificationEvent( final String notification ) {
        this( notification,
              NotificationType.DEFAULT,
              false );
    }

    public NotificationEvent( final String notification,
                              final NotificationType type ) {
        this( notification,
              type,
              false );
    }

    public NotificationEvent( final String notification,
                              final boolean isSingleton ) {
        this( notification,
              NotificationType.DEFAULT,
              isSingleton );
    }

    public NotificationEvent( final String notification,
                              final NotificationType type,
                              final boolean isSingleton ) {
        this.notification = notification;
        this.type = type;
        this.isSingleton = isSingleton;
    }

    public String getNotification() {
        return this.notification;
    }

    public NotificationType getType() {
        return type;
    }

    public boolean isSingleton() {
        return isSingleton;
    }

    @Override
    public String toString() {
        return "NotificationEvent [notification=" + notification + ", type=" + type + "]";
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof NotificationEvent ) ) {
            return false;
        }

        NotificationEvent that = (NotificationEvent) o;

        if ( isSingleton != that.isSingleton ) {
            return false;
        }
        if ( notification != null ? !notification.equals( that.notification ) : that.notification != null ) {
            return false;
        }
        return type == that.type;

    }

    @Override
    public int hashCode() {
        int result = notification != null ? notification.hashCode() : 0;
        result = ~~result;
        result = 31 * result + type.hashCode();
        result = ~~result;
        result = 31 * result + ( isSingleton ? 1 : 0 );
        result = ~~result;
        return result;
    }
}
