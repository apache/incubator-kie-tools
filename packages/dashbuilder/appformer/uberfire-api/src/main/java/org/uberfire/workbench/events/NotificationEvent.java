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
package org.uberfire.workbench.events;

import org.jboss.errai.common.client.api.annotations.Portable;
import org.uberfire.mvp.PlaceRequest;

/**
 * An event to show a notification pop-up in the Workbench
 */
@Portable
public class NotificationEvent implements UberFireEvent {

    private String notification;
    private NotificationType type;
    private boolean isSingleton;
    private PlaceRequest placeRequest;
    private Integer initialTopOffset;
    private boolean autoHide = true;
    private String navigationText;
    private PlaceRequest navigationPlace;

    public NotificationEvent() {
    }

    public NotificationEvent(final String notification) {
        this(notification,
             NotificationType.DEFAULT,
             false,
             null);
    }

    public NotificationEvent(final String notification,
                             final NotificationType type) {
        this(notification,
             type,
             false,
             null);
    }

    public NotificationEvent(final String notification,
                             final boolean isSingleton) {
        this(notification,
             NotificationType.DEFAULT,
             isSingleton,
             null);
    }

    public NotificationEvent(final String notification,
                             final NotificationType type,
                             final boolean isSingleton) {
        this(notification,
             NotificationType.DEFAULT,
             isSingleton,
             null);
    }

    public NotificationEvent(final String notification,
                             final NotificationType type,
                             final boolean isSingleton,
                             final PlaceRequest placeRequest) {

        this(notification,
             type,
             isSingleton,
             placeRequest,
             null);
    }

    public NotificationEvent(final String notification,
                             final NotificationType type,
                             final boolean isSingleton,
                             final PlaceRequest placeRequest,
                             final Integer initialTopOffset) {

        this.notification = notification;
        this.type = type;
        this.isSingleton = isSingleton;
        this.placeRequest = placeRequest;
        this.initialTopOffset = initialTopOffset;
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

    public PlaceRequest getPlaceRequest() {
        return placeRequest;
    }

    public Integer getInitialTopOffset() {
        return initialTopOffset;
    }

    public NotificationEvent setAutoHide(final boolean autoHide) {
        this.autoHide = autoHide;
        return this;
    }

    public boolean autoHide() {
        return autoHide;
    }

    public NotificationEvent setNavigation(final String text,
                                       final PlaceRequest place) {
        this.navigationText = text;
        this.navigationPlace = place;
        return this;
    }

    public boolean hasNavigation() {
        return navigationText != null
            && navigationPlace != null;
    }

    public String getNavigationText() {
        return navigationText;
    }

    public PlaceRequest getNavigationPlace() {
        return navigationPlace;
    }

    @Override
    public String toString() {
        return "NotificationEvent [notification=" + notification + ", type=" + type + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (isSingleton ? 1231 : 1237);
        result = prime * result + ((notification == null) ? 0 : notification.hashCode());
        result = prime * result + ((placeRequest == null) ? 0 : placeRequest.hashCode());
        result = prime * result + ((initialTopOffset == null) ? 0 : initialTopOffset.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        NotificationEvent other = (NotificationEvent) obj;
        if (isSingleton != other.isSingleton) {
            return false;
        }
        if (notification == null) {
            if (other.notification != null) {
                return false;
            }
        } else if (!notification.equals(other.notification)) {
            return false;
        }
        if (placeRequest == null) {
            if (other.placeRequest != null) {
                return false;
            }
        } else if (!placeRequest.equals(other.placeRequest)) {
            return false;
        }
        if (initialTopOffset == null) {
            if (other.initialTopOffset != null) {
                return false;
            }
        } else if (!initialTopOffset.equals(other.initialTopOffset)) {
            return false;
        }
        if (type != other.type) {
            return false;
        }
        return true;
    }

    public static enum NotificationType {
        DEFAULT,
        ERROR,
        SUCCESS,
        INFO,
        WARNING
    }
}