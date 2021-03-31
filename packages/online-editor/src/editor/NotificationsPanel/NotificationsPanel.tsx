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
import { useCallback, useMemo, useState } from "react";
import { Badge, Tab, Tabs, TabTitleText } from "@patternfly/react-core";
import { ExclamationCircleIcon } from "@patternfly/react-icons";
import { useNotificationsPanel } from "./NotificationsPanelContext";
import { DmnRunnerNotificationsTab } from "../DmnRunner/DmnRunnerContextProvider";

// add resizable feature;
// add qtt of messages on the tabs title

export function NotificationsPanel(props: {}) {
  const notificationsPanel = useNotificationsPanel();
  const [activeTab, setActiveTab] = useState(DmnRunnerNotificationsTab.EXECUTION);

  const onNotificationsPanelButtonClick = useCallback(() => {
    notificationsPanel.setIsOpen(!notificationsPanel.isOpen);
  }, [notificationsPanel.isOpen, notificationsPanel.setIsOpen]);

  const onSelectTab = useCallback((event, tabIndex) => {
    setActiveTab(tabIndex);
  }, []);

  const tabs = useMemo(() => {
    return (
      <Tab
        eventKey={DmnRunnerNotificationsTab.EXECUTION}
        title={
          <TabTitleText>
            {DmnRunnerNotificationsTab.EXECUTION} <Badge>0</Badge>
          </TabTitleText>
        }
      >
        {notificationsPanel.getTabComponent(DmnRunnerNotificationsTab.EXECUTION)}
      </Tab>
    );
  }, [notificationsPanel.getTabComponent]);

  return (
    <>
      <div
        className={"kogito--editor__dmn-runner-notifications-panel-button"}
        onClick={onNotificationsPanelButtonClick}
      >
        <ExclamationCircleIcon />
      </div>
      {notificationsPanel.isOpen && (
        <div
          style={{
            height: "200px",
            width: "100%",
            borderTop: "solid 1px #ddd"
          }}
        >
          {/* auto generate the tabs based on something... */}
          <Tabs activeKey={activeTab} onSelect={onSelectTab}>
            {tabs}
          </Tabs>
        </div>
      )}
    </>
  );
}
