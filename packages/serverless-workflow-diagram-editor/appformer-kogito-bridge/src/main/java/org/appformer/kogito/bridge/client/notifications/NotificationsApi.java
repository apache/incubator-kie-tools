/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License. 
 */


package org.appformer.kogito.bridge.client.notifications;

import java.util.List;

import jsinterop.annotations.JsType;
import org.uberfire.workbench.model.bridge.Notification;
import org.uberfire.workbench.model.bridge.NotificationSeverity;
import org.uberfire.workbench.model.bridge.NotificationType;

/**
 * This is the API that will let communicate with Notifications channel implementation. There are two types of Notifications:
 * Alerts ({@link NotificationType#ALERT}) and Problems ({@link NotificationType#ALERT}). The main difference between
 * Alerts and Problems is the lifespan. Alerts are ephemeral (like a "Compilation Success" notifications) and Problems
 * are information we need to keep, so we can take actions based on them.
 * Problems supports severities {@link NotificationSeverity#INFO} {@link NotificationSeverity#ERROR}
 * {@link NotificationSeverity#HINT} {@link NotificationSeverity#WARNING}. Any other severity will be treated as INFO.
 * Alerts supports severities {@link NotificationSeverity#INFO} and {@link NotificationSeverity#ERROR}. Any other
 * severity will be treated as INFO.
 */
@JsType(isNative = true)
public interface NotificationsApi {

    /**
     * Send a single notification to the channel. This will append a notification on the channel so the older ones are
     * not going to be removed. For that consumer must manually delete the notifications on a path.
     *
     * @param notification The notification (Problem | Alert)
     */
    void createNotification(Notification notification);

    /**
     * Send a list of notifications to the channel. The diferrence with {@link NotificationsApi#createNotification(Notification)} is that all the
     * notifications for that path are overwritten. So if there is a notification that you want to keep you will need
     * to send it again in that list.
     *
     * @param path         All the notifications are going to by grouped by path
     * @param notification The notification (Problem | Alert)
     */
    void setNotifications(String path, List<Notification> notification);

    /**
     * Removes all the notifications for given path.
     *
     * @param path The path of the notifications to be deleted
     */
    void removeNotifications(String path);
}
