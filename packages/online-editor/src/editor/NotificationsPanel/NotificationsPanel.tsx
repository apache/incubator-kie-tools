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
import { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { Badge, Tab, Tabs, TabTitleText, Tooltip } from "@patternfly/react-core";
import { AngleRightIcon, ExclamationCircleIcon } from "@patternfly/react-icons";
import { useNotificationsPanel } from "./NotificationsPanelContext";
import { NotificationPanelTabContent } from "./NotificationsPanelTabContent";
import { NotificationsApi } from "@kogito-tooling/notifications/dist/api";

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
    setTabsNotifications(previousTabsNotifications => {
      const newTabsNotifications = new Map(previousTabsNotifications);
      if (previousTabsNotifications.get(name) !== newQtt) {
        const updatedResult = document.getElementById(`dmn-runner-errors`);
        updatedResult?.classList.add("kogito--editor__notifications-panel-error-count-updated");
      }
      newTabsNotifications.set(name, newQtt);
      return newTabsNotifications;
    });
  }, []);

  const onAnimationEnd = useCallback((e: React.AnimationEvent<HTMLElement>) => {
    e.preventDefault();
    e.stopPropagation();

    const updatedResult = document.getElementById(`dmn-runner-errors`);
    updatedResult?.classList.remove("kogito--editor__notifications-panel-error-count-updated");
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

  const notificationsPanelDivRef = useRef<HTMLDivElement>(null);
  const [notificationsPanelIconPlace, setNotificationsPanelIconPlace] = useState<number>();
  const notificationsPanelIconRef = useRef<HTMLDivElement>(null);

  const onMouseMove = useCallback((e: MouseEvent) => {
    const iframe = document.getElementById("kogito-iframe");
    if (iframe) {
      iframe.style.pointerEvents = "none";
    }

    const notificationsPanelDiv = notificationsPanelDivRef.current?.getBoundingClientRect();
    const newNotificationsPanelSize = notificationsPanelDiv!.bottom - e.clientY;
    notificationsPanelDivRef.current?.style?.setProperty("height", `${newNotificationsPanelSize}px`);
    setNotificationsPanelIconPlace(newNotificationsPanelSize + 12);
  }, []);

  const onMouseUp = useCallback((e: MouseEvent) => {
    const iframe = document.getElementById("kogito-iframe");
    if (iframe) {
      iframe.style.pointerEvents = "visible";
    }

    document.removeEventListener("mousemove", onMouseMove);
    document.removeEventListener("mouseup", onMouseUp);
  }, []);

  const onMouseDown = useCallback(() => {
    document.addEventListener("mousemove", onMouseMove);
    document.addEventListener("mouseup", onMouseUp);
  }, []);
  const [expandAll, setExpandAll] = useState(false);

  const onExpandAll = useCallback(() => {
    setExpandAll(true);
  }, []);

  const onRetractAll = useCallback(() => {
    setExpandAll(false);
  }, []);

  useEffect(() => {
    if (notificationsPanel.isOpen) {
      notificationsPanelIconRef.current?.style?.setProperty("bottom", `${notificationsPanelIconPlace ?? 360}px`);
    } else {
      notificationsPanelIconRef.current?.style?.setProperty("bottom", `5px`);
    }
  }, [notificationsPanel.isOpen, notificationsPanelIconPlace]);

  return (
    <>
      <div
        ref={notificationsPanelIconRef}
        className={
          notificationsPanel.isOpen
            ? "kogito--editor__notifications-panel-button open"
            : "kogito--editor__notifications-panel-button"
        }
        onClick={onNotificationsPanelButtonClick}
      >
        {totalNotifications === 0 ? (
          <Tooltip
            key={"without-notifications"}
            content={"Notifications Panel"}
            flipBehavior={["left"]}
            distance={20}
            children={<ExclamationCircleIcon />}
          />
        ) : (
          <Tooltip
            key={"with-notifications"}
            content={"Notifications Panel"}
            flipBehavior={["left"]}
            distance={20}
            children={
              <div style={{ display: "flex", flexDirection: "column", justifyContent: "center", alignItems: "center" }}>
                <span id={"dmn-runner-errors"} onAnimationEnd={onAnimationEnd}>
                  {totalNotifications}
                </span>
                <ExclamationCircleIcon style={{ marginTop: "1px" }} />
              </div>
            }
          />
        )}
      </div>
      <div
        style={{
          display: notificationsPanel.isOpen ? "flex" : "none",
          flexDirection: "column",
          borderTop: "solid 1px #ddd"
        }}
      >
        <div
          onMouseDown={onMouseDown}
          style={{ height: "10px", borderBottom: "solid 1px #ddd", cursor: "row-resize" }}
        />
        <div
          ref={notificationsPanelDivRef}
          style={{
            height: "350px",
            width: "100%",
            position: "relative"
          }}
        >
          <div
            style={{
              right: 0,
              top: 0,
              position: "absolute",
              display: "flex",
              width: "50px",
              height: "40px",
              justifyContent: "space-around",
              alignItems: "center",
              padding: "7px",
              zIndex: 999,
              userSelect: "none"
            }}
            onClick={() => (expandAll ? onRetractAll() : onExpandAll())}
          >
            <span style={{ transition: ".2s ease-in 0s", transform: expandAll ? "rotate(90deg)" : undefined }}>
              <AngleRightIcon />
            </span>
          </div>
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
                <div>
                  <NotificationPanelTabContent
                    name={tabName}
                    ref={tabRef}
                    onNotificationsLengthChange={onNotificationsLengthChange}
                    expandAll={expandAll}
                  />
                </div>
              </Tab>
            ))}
          </Tabs>
        </div>
      </div>
    </>
  );
}
