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
import { NotificationsPanelContext, Something } from "./NotificationsPanelContext";
import { NotificationPanelTab } from "./NotificationsPanelTab";
import { NotificationsApi } from "@kogito-tooling/notifications/dist/api";

interface Props {
  children: React.ReactNode;
}

export function NotificationsPanelContextProvider(props: Props) {
  const [isOpen, setIsOpen] = useState(false);
  const tabs: Map<string, Something> = useMemo(() => new Map(), []);

  // create tabs
  const createTabs = useCallback(
    (names: string[]) => {
      names.forEach(name => {
        const tabRef = React.createRef<NotificationsApi>();
        tabs.set(name, { tabComponent: <NotificationPanelTab name={name} ref={tabRef} />, tabRef });
      });
    },
    [tabs]
  );

  // return a tab a ref
  const getTabRef = useCallback(
    (name: string) => {
      return tabs.get(name)?.tabRef?.current;
    },
    [tabs]
  );

  // return a tab content
  const getTabComponent = useCallback(
    (name: string) => {
      return tabs.get(name)?.tabComponent;
    },
    [tabs]
  );

  return (
    <NotificationsPanelContext.Provider
      value={{
        isOpen,
        setIsOpen,
        createTabs,
        getTabRef,
        getTabComponent
      }}
    >
      {props.children}
    </NotificationsPanelContext.Provider>
  );
}
