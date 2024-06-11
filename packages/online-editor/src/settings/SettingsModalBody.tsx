/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import * as React from "react";
import { Tab, Tabs, TabTitleText } from "@patternfly/react-core/dist/js/components/Tabs";
import { useSettings, useSettingsDispatch } from "./SettingsContext";
import { ExtendedServicesSettingsTab } from "./ExtendedServicesSettingsTab";
import { EditorsSettingsTab } from "./EditorsSettingsTab";
import { CorsProxySettingsTab } from "./CorsProxySettingsTab";
import { useEditorEnvelopeLocator } from "../envelopeLocator/hooks/EditorEnvelopeLocatorContext";

export enum SettingsTabs {
  KIE_SANDBOX_EXTENDED_SERVICES = "extendedServices",
  CORS_PROXY = "corsProxy",
  EDITORS = "editors",
}

export function SettingsModalBody() {
  const editorEnvelopeLocator = useEditorEnvelopeLocator();
  const settings = useSettings();
  const settingsDispatch = useSettingsDispatch();

  return (
    <Tabs
      activeKey={settings.tab}
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
      <Tab
        className="kie-tools--settings-tab"
        eventKey={SettingsTabs.CORS_PROXY}
        title={<TabTitleText>CORS Proxy</TabTitleText>}
      >
        <CorsProxySettingsTab />
      </Tab>
      {editorEnvelopeLocator.hasMappingFor("*.dmn") && (
        <Tab
          className="kie-tools--settings-tab"
          eventKey={SettingsTabs.EDITORS}
          title={<TabTitleText>Editors</TabTitleText>}
        >
          <EditorsSettingsTab />
        </Tab>
      )}
    </Tabs>
  );
}
