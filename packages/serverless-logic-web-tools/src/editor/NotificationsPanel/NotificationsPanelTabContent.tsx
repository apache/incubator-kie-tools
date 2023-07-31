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

import * as React from "react";
import { useCallback, useEffect, useImperativeHandle, useMemo, useState } from "react";
import { Notification, NotificationsChannelApi, NotificationSeverity } from "@kie-tools-core/notifications/dist/api";
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
  expandAll: boolean | undefined;
  name: string;
  onNotificationClick?: (notification: Notification) => void;
  onNotificationsLengthChange: (name: string, newQtt: number) => void;
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

export const NotificationPanelTabContent = React.forwardRef<NotificationsChannelApi, Props>((props, forwardedRef) => {
  const { onNotificationsLengthChange, name } = props;
  const [tabNotifications, setTabNotifications] = useState<Notification[]>([]);

  const createNotification = useCallback(
    (notification: Notification) => {
      setTabNotifications([...tabNotifications, notification]);
    },
    [tabNotifications]
  );

  const setNotifications = useCallback(
    (path: string, notifications: Notification[]) => {
      onNotificationsLengthChange(name, notifications.length);
      setTabNotifications(notifications);
    },
    [onNotificationsLengthChange, name]
  );

  const removeNotifications = useCallback((path: string) => {
    setTabNotifications((previousTabNotifications) => {
      return previousTabNotifications.filter((tabNotification) => tabNotification.path === path);
    });
  }, []);

  useImperativeHandle(forwardedRef, () => ({
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
                            onClick={() => props.onNotificationClick?.(notification)}
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
});

interface NotificationDrawerGroupProps {
  path: string;
  notifications: Notification[];
  allExpanded: boolean | undefined;
  setAllExpanded: React.Dispatch<boolean | undefined>;
}

export function NotificationTabDrawerGroup(props: NotificationDrawerGroupProps) {
  const { setAllExpanded } = props;
  const [isExpanded, setIsExpanded] = useState(false);
  const onExpand = useCallback(() => {
    setIsExpanded((prevExpanded) => !prevExpanded);
    setAllExpanded(undefined);
  }, [setAllExpanded]);

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
