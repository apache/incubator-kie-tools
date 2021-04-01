import * as React from "react";
import { Notification, NotificationsApi, NotificationSeverity } from "@kogito-tooling/notifications/dist/api";
import { useCallback, useEffect, useImperativeHandle, useMemo, useState } from "react";
import {
  Dropdown,
  NotificationDrawer,
  NotificationDrawerBody,
  NotificationDrawerHeader,
  NotificationDrawerGroup,
  NotificationDrawerGroupList,
  NotificationDrawerList,
  NotificationDrawerListItem,
  NotificationDrawerListItemBody,
  NotificationDrawerListItemHeader,
  TreeView,
  TreeViewDataItem
} from "@patternfly/react-core";

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

  const setNotifications = useCallback(
    (path: string, notifications: Notification[]) => {
      props.onNotificationsLengthChange(props.name, notifications.length);
      setTabNotifications(notifications);
    },
    [props.onNotificationsLengthChange, props.name]
  );

  const removeNotifications = useCallback((path: string) => {
    setTabNotifications(previousTabNotifications => {
      return previousTabNotifications.filter(tabNotification => tabNotification.path === path);
    });
  }, []);

  const clearNotifications = useCallback(() => {
    setTabNotifications([]);
  }, []);

  useImperativeHandle(forwardingRef, () => {
    return {
      createNotification,
      setNotifications,
      removeNotifications
    };
  });

  const notificationsMap: Map<string, Notification[]> = useMemo(() => {
    return tabNotifications.reduce((acc, notification) => {
      const notificationEntry = acc.get(notification.path);
      if (!notificationEntry) {
        acc.set(notification.path, [notification]);
      } else {
        acc.set(notification.path, [...notificationEntry, notification]);
      }
      return acc;
    }, new Map());
  }, [tabNotifications]);

  return (
    <>
      {tabNotifications.length > 0 && (
        <NotificationDrawer>
          <NotificationDrawerBody>
            <NotificationDrawerGroupList>
              {[...notificationsMap.entries()].map(([path, notifications]) => (
                <NotificationTabDrawerGroup path={path} notifications={notifications} />
              ))}
            </NotificationDrawerGroupList>
          </NotificationDrawerBody>
        </NotificationDrawer>
      )}
    </>
  );
};

export const NotificationPanelTabContent = React.forwardRef(RefForwardingNotificationPanelTabContent);

interface NotificationDrawerGroupProps {
  path: string;
  notifications: Notification[];
}

function NotificationTabDrawerGroup(props: NotificationDrawerGroupProps) {
  const [isExpanded, setIsExpanded] = useState<boolean>(false);
  const onExpand = useCallback(() => {
    setIsExpanded(prevExpanded => !prevExpanded);
  }, []);

  const variant = useCallback((severity: NotificationSeverity) => {
    switch (severity) {
      case "ERROR":
        return "danger";
      case "HINT":
        return "default";
      case "SUCCESS":
        return "success";
      case "WARNING":
        return "warning";
      default:
        return "info";
    }
  }, []);

  return (
    <NotificationDrawerGroup
      title={props.path}
      isExpanded={isExpanded}
      count={props.notifications.length}
      onExpand={onExpand}
    >
      {props.notifications.map(notification => (
        <NotificationDrawerList isHidden={!isExpanded}>
          <NotificationDrawerListItem variant={variant(notification.severity)}>
            <NotificationDrawerListItemHeader title={notification.message} variant={variant(notification.severity)} />
          </NotificationDrawerListItem>
        </NotificationDrawerList>
      ))}
    </NotificationDrawerGroup>
  );
}
