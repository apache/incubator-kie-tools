/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { Notification } from "@kie-tooling-core/notifications/dist/api";

export interface NotificationsApi {
  /**
   * Creates a single notification. This action does not replace an existent notification.
   * @param notification The notification itself
   */
  createNotification(notification: Notification): void;

  /**
   * Creates a list of notification for a given path. This notifications will replace existent notification for that path.
   * @param path The path that references the Notification
   * @param notifications List of Notifications
   */
  setNotifications(path: string, notifications: Notification[]): void;

  /**
   * Removes all the notification from a Path.
   * @param path The notifications path
   */
  removeNotifications(path: string): void;
}
