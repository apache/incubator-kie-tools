import { Tab, Tabs, TabTitleText } from "@patternfly/react-core/dist/js/components/Tabs";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { TextContent } from "@patternfly/react-core/dist/js/components/Text";
import * as React from "react";
import { useEffect, useState } from "react";
import { QueryParams, useQueryParams } from "../queryParams/QueryParamsContext";
import { useHistory } from "react-router";
import { GitHubSettingsTab } from "./GitHubSettingsTab";
import { GeneralSettingsTab } from "./GeneralSettingsTab";

export enum SettingsTabs {
  GENERAL = "general",
  GITHUB = "github",
  OPENSHIFT = "openshift",
}

export function SettingsModalBody() {
  const queryParams = useQueryParams();
  const history = useHistory();
  const [activeTab, setActiveTab] = useState(queryParams.get(QueryParams.SETTINGS) ?? SettingsTabs.GENERAL);

  useEffect(() => {
    const url = new URL(window.location.href);
    url.searchParams.set(QueryParams.SETTINGS, activeTab);
    history.push({
      pathname: history.location.pathname,
      search: url.search,
      state: new Date(),
    });
  }, [history, activeTab]);

  useEffect(() => {
    return () => {
      const url = new URL(window.location.href);
      url.searchParams.delete(QueryParams.SETTINGS);
      history.push({
        pathname: history.location.pathname,
        search: url.search,
      });
    };
  }, [history]);

  return (
    <Tabs activeKey={activeTab} onSelect={(e, k) => setActiveTab(k as string)} isVertical={false} isBox={false}>
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
        <Page>
          <PageSection>
            <TextContent>OpenShift</TextContent>
          </PageSection>
        </Page>
      </Tab>
    </Tabs>
  );
}
