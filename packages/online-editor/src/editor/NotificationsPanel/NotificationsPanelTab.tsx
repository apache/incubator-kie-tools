import * as React from "react";
import { Notification, NotificationsApi } from "@kogito-tooling/notifications/dist/api";
import { useCallback, useImperativeHandle, useState } from "react";

interface Props {
  name: string;
}

export const RefForwardingNotificationPanelTab: React.RefForwardingComponent<NotificationsApi, Props> = (
  props,
  forwardingRef
) => {
  const [tabNotifications, setTabNotifications] = useState<Notification[]>([]);

  const createNotification = useCallback(
    (notification: Notification) => {
      setTabNotifications([...tabNotifications, notification]);
    },
    [tabNotifications]
  );

  const setNotifications = useCallback((path: string, notifications: Notification[]) => {
    setTabNotifications(notifications);
  }, []);

  const removeNotifications = useCallback((path: string) => {
    return;
  }, []);

  useImperativeHandle(forwardingRef, () => {
    return {
      createNotification,
      setNotifications,
      removeNotifications
    };
  });

  return (
    <div>
      <p>Something</p>
    </div>
  );
};

export const NotificationPanelTab = React.forwardRef(RefForwardingNotificationPanelTab);
