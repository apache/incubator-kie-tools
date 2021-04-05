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
import { useCallback, useEffect, useMemo, useState } from "react";
import { Badge, Tab, Tabs, TabTitleText } from "@patternfly/react-core";
import { ExclamationCircleIcon } from "@patternfly/react-icons";
import { useNotificationsPanel } from "./NotificationsPanelContext";
import { NotificationPanelTabContent } from "./NotificationsPanelTabContent";
import { NotificationsApi } from "@kogito-tooling/notifications/dist/api";

// TODO: add resizable feature;

interface Props {
  tabNames: string[];
}

export function NotificationsPanel(props: Props) {
  const notificationsPanel = useNotificationsPanel();
  const [tabsNotifications, setTabsNotifications] = useState<Map<string, number>>(new Map());

  const tabsMap: Map<string, React.RefObject<NotificationsApi>> = useMemo(
    () => new Map(props.tabNames.map(tabName => [tabName, React.createRef<NotificationsApi>()])),
    [props.tabNames]
  );

  useEffect(() => {
    notificationsPanel.setTabsMap([...tabsMap.entries()]);
  }, [tabsMap]);

  const onNotificationsPanelButtonClick = useCallback(() => {
    notificationsPanel.setIsOpen(!notificationsPanel.isOpen);
  }, [notificationsPanel.isOpen, notificationsPanel.setIsOpen]);

  const onNotificationsLengthChange = useCallback((name: string, newQtt: number) => {
    setTabsNotifications(previousTabsProps => {
      const newTabsProps = new Map(previousTabsProps);
      newTabsProps.set(name, newQtt);
      return newTabsProps;
    });
  }, []);

  const onSelectTab = useCallback((event, tabName) => {
    notificationsPanel.setActiveTab(tabName);
  }, []);

  useEffect(() => {
    notificationsPanel.setActiveTab(props.tabNames[0]);
  }, []);

  const totalNotifications = useMemo(() => [...tabsNotifications.values()].reduce((acc, value) => acc + value, 0), [
    tabsNotifications
  ]);

  return (
    <>
      <div
        className={
          notificationsPanel.isOpen
            ? "kogito--editor__notifications-panel-button open"
            : "kogito--editor__notifications-panel-button"
        }
        onClick={onNotificationsPanelButtonClick}
      >
        {totalNotifications === 0 ? (
          <ExclamationCircleIcon />
        ) : (
          <>
            <span>{totalNotifications}</span>
            <ExclamationCircleIcon style={{ marginTop: "1px" }} />
          </>
        )}
      </div>
      <div
        style={{
          height: "350px",
          width: "100%",
          borderTop: "solid 1px #ddd",
          display: notificationsPanel.isOpen ? "block" : "none"
        }}
      >
        <Tabs activeKey={notificationsPanel.activeTab} onSelect={onSelectTab}>
          {[...tabsMap.entries()].map(([tabName, tabRef], index) => (
            <Tab
              key={`tab-${index}`}
              eventKey={tabName}
              title={
                <TabTitleText>
                  {tabName} <Badge isRead={true}>{tabsNotifications.get(tabName)}</Badge>
                </TabTitleText>
              }
            >
              <div style={{ height: "309px" }}>
                <NotificationPanelTabContent
                  name={tabName}
                  ref={tabRef}
                  onNotificationsLengthChange={onNotificationsLengthChange}
                />
              </div>
            </Tab>
          ))}
        </Tabs>
      </div>
    </>
  );
}
