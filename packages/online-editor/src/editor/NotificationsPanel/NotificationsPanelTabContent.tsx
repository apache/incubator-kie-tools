import * as React from "react";
import { Notification, NotificationsApi } from "@kogito-tooling/notifications/dist/api";
import { useCallback, useImperativeHandle, useState } from "react";

interface Props {
  name: string;
  onNotificationsLengthChange: (name: string, newQtt: number) => void;
}

export const RefForwardingNotificationPanelTabContent: React.RefForwardingComponent<NotificationsApi, Props> = (
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
    props.onNotificationsLengthChange(props.name, notifications.length);
    setTabNotifications(notifications);
  }, [props.onNotificationsLengthChange, props.name]);

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
      {tabNotifications.map(notification => (
        <p>{notification.message}</p>
      ))}
    </div>
  );
};

export const NotificationPanelTabContent = React.forwardRef(RefForwardingNotificationPanelTabContent);
