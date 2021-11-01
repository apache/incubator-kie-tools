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
import { useCallback, useEffect, useImperativeHandle, useMemo, useRef, useState } from "react";
import { Badge } from "@patternfly/react-core/dist/js/components/Badge";
import { Tab, Tabs, TabTitleText } from "@patternfly/react-core/dist/js/components/Tabs";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { ExclamationCircleIcon } from "@patternfly/react-icons/dist/js/icons/exclamation-circle-icon";
import { AngleUpIcon } from "@patternfly/react-icons/dist/js/icons/angle-up-icon";
import { AngleDownIcon } from "@patternfly/react-icons/dist/js/icons/angle-down-icon";
import { NotificationPanelTabContent } from "./NotificationsPanelTabContent";
import { NotificationsApi } from "@kie-tooling-core/notifications/dist/api";
import { useOnlineI18n } from "../../common/i18n";

interface Props {
  tabNames: string[];
}

export interface NotificationsPanelController {
  getTab: (name: string) => NotificationsApi | undefined;
  setActiveTab: React.Dispatch<React.SetStateAction<string>>;
  getTotalNotificationsCount: () => number;
}

export const NotificationsPanel = React.forwardRef<NotificationsPanelController, Props>((props, forwardRef) => {
  const { i18n } = useOnlineI18n();
  const [activeTab, setActiveTab] = useState<string | undefined>();
  const [tabsNotificationsCount, setTabsNotificationsCount] = useState<Map<string, number>>(new Map());
  const tabs: Map<string, React.RefObject<NotificationsApi>> = useMemo(() => new Map(), []);

  useImperativeHandle(
    forwardRef,
    () => ({
      getTab: (name: string) => tabs.get(name)?.current ?? undefined,
      setActiveTab,
      getTotalNotificationsCount,
    }),
    [tabs]
  );

  const getTotalNotificationsCount = useCallback(() => {
    return [...tabsNotificationsCount.values()].reduce((acc, value) => acc + value, 0);
  }, [tabsNotificationsCount]);

  // create tabs
  const setTabsMap = useCallback(
    (tabsMap: Array<[string, React.RefObject<NotificationsApi>]>) => {
      tabsMap.forEach(([tabName, tabRef]) => tabs.set(tabName, tabRef));
    },
    [tabs]
  );

  const tabsMap: Map<string, React.RefObject<NotificationsApi>> = useMemo(
    () => new Map(props.tabNames.map((tabName) => [tabName, React.createRef<NotificationsApi>()])),
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
    setTabsMap([...tabsMap.entries()]);
  }, [tabsMap, setTabsMap]);

  const onNotificationsLengthChange = useCallback((name: string, newQtt: number) => {
    setTabsNotificationsCount((prev) => {
      const newMap = new Map(prev);
      if (prev.get(name) !== newQtt) {
        // totalNotificationsSpanRef.current?.classList.add("kogito--editor__notifications-panel-error-count-updated");
      }
      newMap.set(name, newQtt);
      return newMap;
    });
  }, []);

  const onSelectTab = useCallback((event, tabName) => {
    setActiveTab(tabName);
  }, []);

  useEffect(() => {
    setActiveTab(props.tabNames[0]);
  }, []);

  // const totalNotificationsSpanRef = useRef<HTMLSpanElement>(null);
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
        <div onClick={() => onRetractAll()}>
          <Tooltip content={i18n.notificationsPanel.tooltip.retractAll}>
            <AngleUpIcon />
          </Tooltip>
        </div>
        <div onClick={() => onExpandAll()}>
          <Tooltip content={i18n.notificationsPanel.tooltip.expandAll}>
            <AngleDownIcon />
          </Tooltip>
        </div>
      </div>
      <Tabs activeKey={activeTab} onSelect={onSelectTab}>
        {[...tabsMap.entries()].map(([tabName, tabRef], index) => (
          <Tab
            key={`tab-${index}`}
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
              />
            </div>
          </Tab>
        ))}
      </Tabs>
    </>
  );
});
