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

import { Tab, Tabs, TabTitleText } from "@patternfly/react-core/dist/js/components/Tabs";
import * as React from "react";
import { useSettings, useSettingsDispatch } from "./SettingsContext";
import { ExtendedServicesSettingsTab } from "./ExtendedServicesSettingsTab";

export enum SettingsTabs {
  OPENSHIFT = "openshift",
  KIE_SANDBOX_EXTENDED_SERVICES = "extendedServices",
}

export function SettingsModalBody() {
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();

  return (
    <Tabs
      activeKey={settings.activeTab}
      onSelect={(e, k) => settingsDispatch.open(k as SettingsTabs)}
      isVertical={false}
      isBox={false}
    >
      <Tab
        className="kie-tools--settings-tab"
        eventKey={SettingsTabs.KIE_SANDBOX_EXTENDED_SERVICES}
        title={<TabTitleText>Extended Services</TabTitleText>}
      >
        <ExtendedServicesSettingsTab />
      </Tab>
    </Tabs>
  );
}
