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
import { Tab, Tabs, TabTitleText } from "@patternfly/react-core/dist/js/components/Tabs";
import { GitHubSettingsTab } from "./github/GitHubSettingsTab";
import { useSettings, useSettingsDispatch } from "./SettingsContext";
import { OpenShiftSettingsTab } from "./openshift/OpenShiftSettingsTab";
import { ApacheKafkaSettingsTab } from "./kafka/ApacheKafkaSettingsTab";
import { ServiceAccountSettingsTab } from "./serviceAccount/ServiceAccountSettingsTab";
import { ServiceRegistrySettingsTab } from "./serviceRegistry/ServiceRegistrySettingsTab";
import { KieSandboxExtendedServicesSettingsTab } from "./extendedServices/KieSandboxExtendedServicesSettingsTab";
import { FeaturePreviewSettingsTab } from "./featurePreview/FeaturePreviewSettingsTab";

export enum SettingsTabs {
  GITHUB = "github",
  OPENSHIFT = "openshift",
  KIE_SANDBOX_EXTENDED_SERVICES = "kie-sandbox-extended-services",
  SERVICE_ACCOUNT = "serviceAccount",
  SERVICE_REGISTRY = "serviceRegistry",
  KAFKA = "kafka",
  FEATURE_PREVIEW = "featurePreview",
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
        eventKey={SettingsTabs.GITHUB}
        title={<TabTitleText>GitHub</TabTitleText>}
      >
        <GitHubSettingsTab />
      </Tab>
      <Tab
        className="kie-tools--settings-tab"
        eventKey={SettingsTabs.KIE_SANDBOX_EXTENDED_SERVICES}
        title={<TabTitleText>KIE Sandbox Extended Services</TabTitleText>}
      >
        <KieSandboxExtendedServicesSettingsTab />
      </Tab>
      <Tab
        className="kie-tools--settings-tab"
        eventKey={SettingsTabs.OPENSHIFT}
        title={<TabTitleText>OpenShift</TabTitleText>}
      >
        <OpenShiftSettingsTab />
      </Tab>
      <Tab
        className="kie-tools--settings-tab"
        eventKey={SettingsTabs.SERVICE_ACCOUNT}
        title={<TabTitleText>Service Account</TabTitleText>}
      >
        <ServiceAccountSettingsTab />
      </Tab>
      <Tab
        className="kie-tools--settings-tab"
        eventKey={SettingsTabs.SERVICE_REGISTRY}
        title={<TabTitleText>Service Registry</TabTitleText>}
      >
        <ServiceRegistrySettingsTab />
      </Tab>
      <Tab
        className="kie-tools--settings-tab"
        eventKey={SettingsTabs.KAFKA}
        title={<TabTitleText>Streams for Apache Kafka</TabTitleText>}
      >
        <ApacheKafkaSettingsTab />
      </Tab>
      <Tab
        className="kie-tools--settings-tab"
        eventKey={SettingsTabs.FEATURE_PREVIEW}
        title={<TabTitleText>Feature Preview</TabTitleText>}
      >
        <FeaturePreviewSettingsTab />
      </Tab>
    </Tabs>
  );
}
