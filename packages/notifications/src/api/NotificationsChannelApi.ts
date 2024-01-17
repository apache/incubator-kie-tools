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

import { Notification } from "./Notification";

export interface NotificationsChannelApi {
  /**
   * Creates a single notification. This action does not replace an existent notification.
   * @param notification The notification itself
   */
  kogitoNotifications_createNotification(notification: Notification): void;

  /**
   * Creates a list of notification for a given path. This notifications will replace existent notification for that path.
   * @param normalizedPosixPathRelativeToTheWorkspaceRoot The path that references the Notification
   * @param notifications List of Notifications
   */
  kogitoNotifications_setNotifications(
    normalizedPosixPathRelativeToTheWorkspaceRoot: string,
    notifications: Notification[]
  ): void;

  /**
   * Removes all the notification from a Path.
   * @param normalizedPosixPathRelativeToTheWorkspaceRoot The notifications path
   */
  kogitoNotifications_removeNotifications(normalizedPosixPathRelativeToTheWorkspaceRoot: string): void;
}
