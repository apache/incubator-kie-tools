import { Tab, Tabs, TabTitleText } from "@patternfly/react-core/dist/js/components/Tabs";
import * as React from "react";
import { GitHubSettingsTab } from "./GitHubSettingsTab";
import { GeneralSettingsTab } from "./GeneralSettingsTab";
import { useSettings } from "./SettingsContext";
import { OpenShiftSettingsTab } from "./OpenShiftSettingsTab";
import { KieToolingExtendedServicesSettingsTab } from "./KieToolingExtendedServicesSettingsTab";

export enum SettingsTabs {
  GENERAL = "general",
  GITHUB = "github",
  OPENSHIFT = "openshift",
  KIE_TOOLING_EXTENDED_SERVICES = "kie-tooling-extended-services",
}

export function SettingsModalBody() {
  const settings = useSettings();

  return (
    <Tabs
      activeKey={settings.activeTab}
      onSelect={(e, k) => settings.open(k as SettingsTabs)}
      isVertical={false}
      isBox={false}
    >
      <Tab
        className="kogito-tooling--settings-tab"
        eventKey={SettingsTabs.GENERAL}
        title={<TabTitleText>General</TabTitleText>}
      >
        <GeneralSettingsTab />
      </Tab>
      <Tab
        className="kogito-tooling--settings-tab"
        eventKey={SettingsTabs.GITHUB}
        title={<TabTitleText>GitHub</TabTitleText>}
      >
        <GitHubSettingsTab />
      </Tab>
      <Tab
        className="kogito-tooling--settings-tab"
        eventKey={SettingsTabs.OPENSHIFT}
        title={<TabTitleText>OpenShift</TabTitleText>}
      >
        <OpenShiftSettingsTab />
      </Tab>
      <Tab
        className="kogito-tooling--settings-tab"
        eventKey={SettingsTabs.KIE_TOOLING_EXTENDED_SERVICES}
        title={<TabTitleText>KIE Tooling Extended Services</TabTitleText>}
      >
        <KieToolingExtendedServicesSettingsTab />
      </Tab>
    </Tabs>
  );
}
