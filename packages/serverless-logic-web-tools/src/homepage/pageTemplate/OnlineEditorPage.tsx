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

import { QuickStartContainer, QuickStartContainerProps } from "@patternfly/quickstarts";
import {
  Brand,
  Button,
  Masthead,
  MastheadBrand,
  MastheadContent,
  MastheadMain,
  MastheadToggle,
  Page,
  PageSidebar,
  SkipToContent,
  Toolbar,
  ToolbarContent,
  ToolbarGroup,
  ToolbarItem,
  Tooltip,
} from "@patternfly/react-core/dist/js/components";
import { BarsIcon, ExclamationIcon } from "@patternfly/react-icons/dist/js/icons";
import * as React from "react";
import { useMemo, useState } from "react";
import { useHistory, useRouteMatch } from "react-router";
import { useLocation } from "react-router-dom";
import { KieSandboxExtendedServicesIcon } from "../../kieSandboxExtendedServices/KieSandboxExtendedServicesIcon";
import { useRoutes } from "../../navigation/Hooks";
import { OpenshiftDeploymentsDropdown } from "../../openshift/dropdown/OpenshiftDeploymentsDropdown";
import {
  ApplicationServicesIntegrationQuickStart,
  GitHubTokenQuickStart,
  OpenShiftIntegrationQuickStart,
} from "../../quickstarts-data";
import { SettingsButton } from "../../settings/SettingsButton";
import { SettingsPageNav } from "../../settings/uiNav/SettingsPageNav";
import { HomePageNav } from "../uiNav/HomePageNav";

export type OnlineEditorPageProps = {
  children?: React.ReactNode;
  pageContainerRef: React.RefObject<HTMLDivElement>;
  isNavOpen: boolean;
  setIsNavOpen: (value: boolean) => void;
};

export function OnlineEditorPage(props: OnlineEditorPageProps) {
  const history = useHistory();
  const routes = useRoutes();
  const isRouteInSettingsSection = useRouteMatch(routes.settings.home.path({}));
  const navToggle = () => {
    props.setIsNavOpen(!props.isNavOpen);
  };
  const [activeQuickStartID, setActiveQuickStartID] = useState("");
  const [allQuickStartStates, setAllQuickStartStates] = useState({});

  const drawerProps: QuickStartContainerProps = {
    quickStarts: [GitHubTokenQuickStart, OpenShiftIntegrationQuickStart, ApplicationServicesIntegrationQuickStart],
    activeQuickStartID,
    allQuickStartStates,
    setActiveQuickStartID,
    setAllQuickStartStates,
    useQueryParams: false,
  };

  const isChromiumBased = useMemo(() => {
    const agent = window.navigator.userAgent.toLowerCase();
    return agent.indexOf("edg") > -1 || agent.indexOf("chrome") > -1;
  }, []);

  const headerToolbar = (
    <Toolbar id="toolbar" isFullHeight isStatic>
      <ToolbarContent>
        <ToolbarGroup
          variant="icon-button-group"
          alignment={{ default: "alignRight" }}
          spacer={{ default: "spacerNone", md: "spacerMd" }}
        >
          <ToolbarItem>
            <OpenshiftDeploymentsDropdown />
          </ToolbarItem>
          <ToolbarItem>
            <SettingsButton />
          </ToolbarItem>
          <ToolbarItem>
            <KieSandboxExtendedServicesIcon />
          </ToolbarItem>
          {!isChromiumBased && (
            <ToolbarItem>
              <Tooltip
                className="kogito--editor__light-tooltip"
                key={"not-chromium"}
                content={"To get the best experience, please prefer using Chromium based browsers."}
                flipBehavior={["left"]}
                distance={20}
              >
                <ExclamationIcon
                  data-testid="not-chromium-icon"
                  className="kogito--editor__kie-sandbox-extended-services-dropdown-icon-outdated static-opacity"
                  id="kie-sandbox-extended-services-not-chromium-icon"
                  style={{ cursor: "pointer" }}
                />
              </Tooltip>
            </ToolbarItem>
          )}
        </ToolbarGroup>
      </ToolbarContent>
    </Toolbar>
  );

  const masthead = (
    <Masthead>
      <MastheadToggle>
        <Button
          id="nav-toggle"
          variant="plain"
          aria-label="Global NAV"
          onClick={navToggle}
          aria-expanded={props.isNavOpen}
          aria-controls=""
        >
          <BarsIcon />
        </Button>
      </MastheadToggle>
      <MastheadMain>
        <MastheadBrand
          onClick={() => history.push({ pathname: routes.home.path({}) })}
          style={{ textDecoration: "none" }}
        >
          <Brand
            className="kogito-tools-common--brand"
            src="images/kogito_log_workbranch.svg"
            alt="kogito_logo_white.png"
          ></Brand>
        </MastheadBrand>
      </MastheadMain>
      <MastheadContent>{headerToolbar}</MastheadContent>
    </Masthead>
  );
  const location = useLocation();

  const pageNav = useMemo(
    () =>
      !isRouteInSettingsSection ? (
        <HomePageNav pathname={location.pathname}></HomePageNav>
      ) : (
        <SettingsPageNav pathname={location.pathname}></SettingsPageNav>
      ),
    [location, isRouteInSettingsSection]
  );

  const sidebar = <PageSidebar nav={pageNav} isNavOpen={props.isNavOpen} theme="dark" />;
  const mainContainerId = "main-content-page-layout-tertiary-nav";

  const pageSkipToContent = <SkipToContent href={`#${mainContainerId}`}>Skip to content</SkipToContent>;

  return (
    <QuickStartContainer {...drawerProps}>
      <div id="page-container" ref={props.pageContainerRef}>
        <Page header={masthead} sidebar={sidebar} skipToContent={pageSkipToContent} mainContainerId={mainContainerId}>
          {props.children}
        </Page>
      </div>
    </QuickStartContainer>
  );
}
