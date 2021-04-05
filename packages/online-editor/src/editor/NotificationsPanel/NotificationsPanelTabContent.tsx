import * as React from "react";
import { useCallback, useImperativeHandle, useMemo, useState } from "react";
import { Notification, NotificationsApi, NotificationSeverity } from "@kogito-tooling/notifications/dist/api";
import {
  NotificationDrawer,
  NotificationDrawerBody,
  NotificationDrawerGroup,
  NotificationDrawerGroupList,
  NotificationDrawerList,
  NotificationDrawerListItem,
  NotificationDrawerListItemHeader
} from "@patternfly/react-core";

interface Props {
  name: string;
  onNotificationsLengthChange: (name: string, newQtt: number) => void;
}

function variant(severity: NotificationSeverity) {
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

  useImperativeHandle(forwardingRef, () => ({
    createNotification,
    setNotifications,
    removeNotifications
  }));

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
              {[...notificationsMap.entries()]
                .sort(([a], [b]) => (a < b ? -1 : 1))
                .map(([path, notifications], groupIndex) => (
                  <>
                    {path === "" ? (
                      <NotificationDrawerList isHidden={false}>
                        {notifications.map((notification, notificationIndex) => (
                          <NotificationDrawerListItem
                            key={`validation-notification-${notificationIndex}`}
                            isRead={true}
                            variant={variant(notification.severity)}
                          >
                            <NotificationDrawerListItemHeader
                              title={notification.message}
                              variant={variant(notification.severity)}
                            />
                          </NotificationDrawerListItem>
                        ))}
                      </NotificationDrawerList>
                    ) : (
                      <NotificationTabDrawerGroup
                        key={`execution-notification-group-${groupIndex}`}
                        path={path}
                        notifications={notifications}
                      />
                    )}
                  </>
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
  const [isExpanded, setIsExpanded] = useState(false);
  const onExpand = useCallback(() => {
    setIsExpanded(prevExpanded => !prevExpanded);
  }, []);

  return (
    <NotificationDrawerGroup
      isRead={true}
      title={props.path}
      isExpanded={isExpanded}
      count={props.notifications.length}
      onExpand={onExpand}
    >
      {props.notifications.map((notification, index) => (
        <NotificationDrawerList key={`execution-notification-item-${props.path}-${index}`} isHidden={!isExpanded}>
          <NotificationDrawerListItem isRead={true} variant={variant(notification.severity)}>
            <NotificationDrawerListItemHeader title={notification.message} variant={variant(notification.severity)} />
          </NotificationDrawerListItem>
        </NotificationDrawerList>
      ))}
    </NotificationDrawerGroup>
  );
}
