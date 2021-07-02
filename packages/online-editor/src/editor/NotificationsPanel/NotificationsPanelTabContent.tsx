import * as React from "react";
import { useCallback, useEffect, useImperativeHandle, useMemo, useState } from "react";
import { Notification, NotificationsApi, NotificationSeverity } from "@kie-tooling-core/notifications/dist/api";
import {
  NotificationDrawer,
  NotificationDrawerBody,
  NotificationDrawerGroup,
  NotificationDrawerGroupList,
  NotificationDrawerList,
  NotificationDrawerListItem,
  NotificationDrawerListItemHeader,
} from "@patternfly/react-core/dist/js/components/NotificationDrawer";

interface Props {
  name: string;
  onNotificationsLengthChange: (name: string, newQtt: number) => void;
  expandAll: boolean | undefined;
  setExpandAll: React.Dispatch<boolean | undefined>;
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
    setTabNotifications((previousTabNotifications) => {
      return previousTabNotifications.filter((tabNotification) => tabNotification.path === path);
    });
  }, []);

  useImperativeHandle(forwardingRef, () => ({
    kogitoNotifications_createNotification: createNotification,
    kogitoNotifications_setNotifications: setNotifications,
    kogitoNotifications_removeNotifications: removeNotifications,
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
                  <React.Fragment key={path}>
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
                        allExpanded={props.expandAll}
                        setAllExpanded={props.setExpandAll}
                      />
                    )}
                  </React.Fragment>
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
  allExpanded: boolean | undefined;
  setAllExpanded: React.Dispatch<boolean | undefined>;
}

function NotificationTabDrawerGroup(props: NotificationDrawerGroupProps) {
  const [isExpanded, setIsExpanded] = useState(false);
  const onExpand = useCallback(() => {
    setIsExpanded((prevExpanded) => !prevExpanded);
    props.setAllExpanded(undefined);
  }, []);

  useEffect(() => {
    if (props.allExpanded !== undefined) {
      setIsExpanded(props.allExpanded);
    }
  }, [props.allExpanded]);

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
