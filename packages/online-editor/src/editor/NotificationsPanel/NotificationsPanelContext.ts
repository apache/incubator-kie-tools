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
import { useContext } from "react";
import { NotificationsApi } from "@kie-tooling-core/notifications/dist/api";

export interface NotificationsPanelContextType {
  isOpen: boolean;
  setIsOpen: React.Dispatch<boolean>;
  setTabsMap: (tabsMap: Array<[string, React.RefObject<NotificationsApi>]>) => void;
  getTabRef: (name: string) => NotificationsApi | null | undefined;
  getTabContent: (name: string) => React.ReactNode | undefined;
  getTabNames: () => string[];
  activeTab: string;
  setActiveTab: React.Dispatch<string>;
}

export const NotificationsPanelContext = React.createContext<NotificationsPanelContextType>({} as any);

export function useNotificationsPanel() {
  return useContext(NotificationsPanelContext);
}
