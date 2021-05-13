import { Notification } from "@kogito-tooling/notifications/dist/api";

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
