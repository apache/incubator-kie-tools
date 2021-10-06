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
import { useCallback, useImperativeHandle, useMemo, useState } from "react";
import { NotificationsPanelContext, NotificationsPanelContextType } from "./NotificationsPanelContext";
import { NotificationsApi } from "@kie-tooling-core/notifications/dist/api";

interface Props {
  children: React.ReactNode;
}

const NotificationsPanelContextProviderRefForwarding: React.RefForwardingComponent<
  NotificationsPanelContextType,
  Props
> = (props, forwardingRef) => {
  const [isOpen, setIsOpen] = useState(false);
  const [activeTab, setActiveTab] = useState<string>("");
  const tabs: Map<string, React.RefObject<NotificationsApi>> = useMemo(() => new Map(), []);

  // create tabs
  const setTabsMap = useCallback(
    (tabsMap: Array<[string, React.RefObject<NotificationsApi>]>) => {
      tabsMap.forEach(([tabName, tabRef]) => tabs.set(tabName, tabRef));
    },
    [tabs]
  );

  // return a tab a ref
  const getTabRef = useCallback(
    (name: string) => {
      return tabs.get(name)?.current;
    },
    [tabs]
  );

  // return a tab content
  const getTabContent = useCallback(
    (name: string) => {
      return tabs.get(name);
    },
    [tabs]
  );

  const getTabNames = useCallback(() => {
    return [...tabs.keys()].map((tab) => tab);
  }, [tabs]);

  useImperativeHandle(forwardingRef, () => ({
    isOpen,
    setIsOpen,
    setTabsMap,
    getTabRef,
    getTabContent,
    getTabNames,
    activeTab,
    setActiveTab,
  }));

  return (
    <NotificationsPanelContext.Provider
      value={{
        isOpen,
        setIsOpen,
        setTabsMap,
        getTabRef,
        getTabContent,
        getTabNames,
        activeTab,
        setActiveTab,
      }}
    >
      {props.children}
    </NotificationsPanelContext.Provider>
  );
};

export const NotificationsPanelContextProvider = React.forwardRef(NotificationsPanelContextProviderRefForwarding);
