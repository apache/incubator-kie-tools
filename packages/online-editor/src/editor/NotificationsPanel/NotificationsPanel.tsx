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
  open: () => void;
  getTab: (name: string) => NotificationsApi | undefined;
  setActiveTab: React.Dispatch<React.SetStateAction<string>>;
}

export const NotificationsPanel = React.forwardRef<NotificationsPanelController, Props>((props, forwardRef) => {
  const [isOpen, setIsOpen] = useState(false);
  const [activeTab, setActiveTab] = useState<string | undefined>();
  const tabs: Map<string, React.RefObject<NotificationsApi>> = useMemo(() => new Map(), []);

  useImperativeHandle(
    forwardRef,
    () => ({
      open: () => setIsOpen(true),
      getTab: (name: string) => tabs.get(name)?.current ?? undefined,
      setActiveTab,
    }),
    [tabs]
  );

  // create tabs
  const setTabsMap = useCallback(
    (tabsMap: Array<[string, React.RefObject<NotificationsApi>]>) => {
      tabsMap.forEach(([tabName, tabRef]) => tabs.set(tabName, tabRef));
    },
    [tabs]
  );

  const [tabsNotificationsCount, setTabsNotificationsCount] = useState<Map<string, number>>(new Map());
  const { i18n } = useOnlineI18n();

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

  const togglePanel = useCallback(() => {
    setIsOpen((prev) => !prev);
  }, []);

  const onNotificationsLengthChange = useCallback((name: string, newQtt: number) => {
    setTabsNotificationsCount((prev) => {
      const newMap = new Map(prev);
      if (prev.get(name) !== newQtt) {
        totalNotificationsSpanRef.current?.classList.add("kogito--editor__notifications-panel-error-count-updated");
      }
      newMap.set(name, newQtt);
      return newMap;
    });
  }, []);

  const onAnimationEnd = useCallback((e: React.AnimationEvent<HTMLElement>) => {
    e.preventDefault();
    e.stopPropagation();

    totalNotificationsSpanRef.current?.classList.remove("kogito--editor__notifications-panel-error-count-updated");
  }, []);

  const onSelectTab = useCallback((event, tabName) => {
    setActiveTab(tabName);
  }, []);

  useEffect(() => {
    setActiveTab(props.tabNames[0]);
  }, []);

  const totalNotifications = useMemo(
    () => [...tabsNotificationsCount.values()].reduce((acc, value) => acc + value, 0),
    [tabsNotificationsCount]
  );

  const notificationsPanelDivRef = useRef<HTMLDivElement>(null);
  const [notificationsPanelIconPlace, setNotificationsPanelIconPlace] = useState<number>();
  const notificationsPanelIconRef = useRef<HTMLDivElement>(null);
  const totalNotificationsSpanRef = useRef<HTMLSpanElement>(null);

  const onMouseMove = useCallback((e: MouseEvent) => {
    const iframe = document.getElementById("kogito-iframe");
    if (iframe) {
      iframe.style.pointerEvents = "none";
    }

    const notificationsPanelDiv = notificationsPanelDivRef.current?.getBoundingClientRect();
    const newNotificationsPanelSize = notificationsPanelDiv!.bottom - e.clientY;
    notificationsPanelDivRef.current?.style?.setProperty("height", `${newNotificationsPanelSize}px`);
    notificationsPanelDivRef.current?.style?.setProperty("user-select", "none");
    setNotificationsPanelIconPlace(newNotificationsPanelSize + 12);
  }, []);

  const onMouseUp = useCallback(
    (e: MouseEvent) => {
      const iframe = document.getElementById("kogito-iframe");
      if (iframe) {
        iframe.style.pointerEvents = "";
      }

      notificationsPanelDivRef.current?.style?.setProperty("user-select", "");
      document.removeEventListener("mousemove", onMouseMove);
      document.removeEventListener("mouseup", onMouseUp);
    },
    [onMouseMove]
  );

  const onMouseDown = useCallback(() => {
    document.addEventListener("mousemove", onMouseMove);
    document.addEventListener("mouseup", onMouseUp);
  }, [onMouseMove, onMouseUp]);

  const [expandAll, setExpandAll] = useState<boolean>();
  const onExpandAll = useCallback(() => {
    setExpandAll(true);
  }, []);

  const onRetractAll = useCallback(() => {
    setExpandAll(false);
  }, []);

  useEffect(() => {
    notificationsPanelIconRef.current?.style?.setProperty(
      "bottom",
      isOpen ? `${notificationsPanelIconPlace ?? 360}px` : `5px`
    );
  }, [isOpen, notificationsPanelIconPlace]);

  return (
    <>
      <div
        ref={notificationsPanelIconRef}
        className={
          isOpen ? "kogito--editor__notifications-panel-button open" : "kogito--editor__notifications-panel-button"
        }
        onClick={togglePanel}
      >
        {totalNotifications === 0 ? (
          <Tooltip
            key={"without-notifications"}
            content={i18n.notificationsPanel.name}
            flipBehavior={["left"]}
            distance={20}
          >
            <ExclamationCircleIcon />
          </Tooltip>
        ) : (
          <Tooltip
            key={"with-notifications"}
            content={i18n.notificationsPanel.name}
            flipBehavior={["left"]}
            distance={20}
          >
            <div className={"kogito--editor__notifications-panel-with-notifications-tooltip "}>
              <span ref={totalNotificationsSpanRef} onAnimationEnd={onAnimationEnd}>
                {totalNotifications}
              </span>
              <ExclamationCircleIcon
                className={"kogito--editor__notifications-panel-with-notifications-tooltip-exclamation-icon"}
              />
            </div>
          </Tooltip>
        )}
      </div>
      <div
        className={isOpen ? "kogito--editor__notifications-panel-open" : "kogito--editor__notifications-panel-close"}
      >
        <div onMouseDown={onMouseDown} className={"kogito--editor__notifications-panel-resizable-div"} />
        <div ref={notificationsPanelDivRef} className={"kogito--editor__notifications-panel-div"}>
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
            {[...tabsMap.entries()].map(([tabName, tabRef]) => (
              <Tab
                key={tabName}
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
        </div>
      </div>
    </>
  );
});
