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
import { Badge } from "@patternfly/react-core/dist/js/components/Badge";
import { Tab, Tabs, TabTitleText } from "@patternfly/react-core/dist/js/components/Tabs";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { AngleUpIcon } from "@patternfly/react-icons/dist/js/icons/angle-up-icon";
import { AngleDownIcon } from "@patternfly/react-icons/dist/js/icons/angle-down-icon";
import { NotificationPanelTabContent } from "./NotificationsPanelTabContent";
import { Notification, NotificationsChannelApi } from "@kie-tools-core/notifications/dist/api";
import { useAppI18n } from "../../i18n";
import { Button, ButtonVariant } from "@patternfly/react-core/dist/js/components/Button";

interface Props {
  onNotificationClick?: (notification: Notification) => void;
  tabNames: string[];
}

export interface NotificationsPanelRef {
  getTab: (name: string) => NotificationsChannelApi | undefined;
  setActiveTab: React.Dispatch<React.SetStateAction<string>>;
}

export const NotificationsPanel = React.forwardRef<NotificationsPanelRef, Props>((props, forwardRef) => {
  const { i18n } = useAppI18n();
  const [activeTab, setActiveTab] = useState<string | undefined>();
  const [tabsNotificationsCount, setTabsNotificationsCount] = useState<Map<string, number>>(new Map());
  const tabs: Map<string, React.RefObject<NotificationsChannelApi>> = useMemo(() => new Map(), []);

  useImperativeHandle(
    forwardRef,
    () => ({
      getTab: (name: string) => tabs.get(name)?.current ?? undefined,
      setActiveTab: (name: string) => setActiveTab(name),
    }),
    [tabs]
  );

  const tabsMap: Map<string, React.RefObject<NotificationsChannelApi>> = useMemo(
    () => new Map(props.tabNames.map((tabName) => [tabName, React.createRef<NotificationsChannelApi>()])),
    [props.tabNames]
  );

  useEffect(() => {
    setTabsNotificationsCount((prev) => {
      const newMap = new Map(prev);

      // Add new
      props.tabNames.forEach((name) => {
        newMap.set(name, newMap.get(name) ?? 0);
      });

      // Remove deleted
      Array.from(newMap.keys()).forEach((k) => {
        if (!props.tabNames.includes(k)) {
          newMap.delete(k);
        }
      });
      return newMap;
    });
  }, [props.tabNames]);

  useEffect(() => {
    [...tabsMap.entries()].forEach(([tabName, tabRef]) => tabs.set(tabName, tabRef));
  }, [tabs, tabsMap]);

  const hasChanged = useCallback((newMap: Map<string, number>, prevMap: Map<string, number>) => {
    const newEntries = [...newMap.entries()];
    const prevEntries = [...prevMap.entries()];
    const checkAgainst = (entries: [string, number][], map: Map<string, number>) => {
      return entries.reduce((hasChanged, [key, value]) => {
        if (map.get(key) !== value) {
          hasChanged = true;
        }
        return hasChanged;
      }, false);
    };

    const newCheck = checkAgainst(newEntries, prevMap);
    const prevCheck = checkAgainst(prevEntries, newMap);
    return newCheck && prevCheck;
  }, []);

  const onNotificationsLengthChange = useCallback(
    (name: string, newQtt: number) => {
      setTabsNotificationsCount((prev) => {
        const newMap = new Map(prev);
        newMap.set(name, newQtt);
        if (hasChanged(newMap, prev)) {
          return newMap;
        }
        return prev;
      });
    },
    [hasChanged]
  );

  const onSelectTab = useCallback((event, tabName) => {
    setActiveTab(tabName);
  }, []);

  useEffect(() => {
    setActiveTab(props.tabNames[0]);
  }, [props.tabNames]);

  const [expandAll, setExpandAll] = useState<boolean>();
  const onExpandAll = useCallback(() => {
    setExpandAll(true);
  }, []);

  const onRetractAll = useCallback(() => {
    setExpandAll(false);
  }, []);

  return (
    <>
      <div className={"kogito--editor__notifications-panel-icon-position"}>
        <Tooltip content={i18n.notificationsPanel.tooltip.retractAll}>
          <Button variant={ButtonVariant.plain} onClick={onRetractAll} className={"kie-tools--masthead-hoverable"}>
            <AngleUpIcon />
          </Button>
        </Tooltip>
        <Tooltip content={i18n.notificationsPanel.tooltip.expandAll}>
          <Button variant={ButtonVariant.plain} onClick={onExpandAll} className={"kie-tools--masthead-hoverable"}>
            <AngleDownIcon />
          </Button>
        </Tooltip>
      </div>
      <Tabs activeKey={activeTab} onSelect={onSelectTab} style={{ overflow: "initial" }}>
        {[...tabsMap.entries()].map(([tabName, tabRef], index) => (
          <Tab
            className={"kie-tools--problems-tab-content"}
            key={`tab-${tabName}`}
            eventKey={tabName}
            title={
              <TabTitleText>
                {tabName} <Badge isRead={true}>{tabsNotificationsCount.get(tabName)}</Badge>
              </TabTitleText>
            }
          >
            <div>
              <NotificationPanelTabContent
                name={tabName}
                ref={tabRef}
                onNotificationsLengthChange={onNotificationsLengthChange}
                expandAll={expandAll}
                setExpandAll={setExpandAll}
                onNotificationClick={props.onNotificationClick}
              />
            </div>
          </Tab>
        ))}
      </Tabs>
    </>
  );
});
