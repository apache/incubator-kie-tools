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
import {
  Masthead,
  MastheadBrand,
  MastheadMain,
  MastheadToggle,
  Button,
  Brand,
  MastheadContent,
  ToolbarContent,
  Toolbar,
  ToolbarGroup,
  ToolbarItem,
  PageSidebar,
  SkipToContent,
} from "@patternfly/react-core";
import { Page } from "@patternfly/react-core/dist/js/components/Page";
import { useHistory } from "react-router";
import { KieSandboxExtendedServicesIcon } from "../../kieSandboxExtendedServices/KieSandboxExtendedServicesIcon";
import { useRoutes } from "../../navigation/Hooks";
import { OpenshiftDeploymentsDropdown } from "../../openshift/dropdown/OpenshiftDeploymentsDropdown";
import { SettingsButton } from "../../settings/SettingsButton";
import { BarsIcon } from "@patternfly/react-icons";
import { HomePageNav } from "../uiNav/HomePageNav";
import { useLocation } from "react-router-dom";
import { useState } from "react";

export function OnlineEditorPage(props: { children?: React.ReactNode }) {
  const history = useHistory();
  const routes = useRoutes();
  const [isNavOpen, setIsNavOpen] = useState(true);
  const navToggle = () => {
    setIsNavOpen(!isNavOpen);
  };

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
          aria-expanded={isNavOpen}
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

  const sidebar = (
    <PageSidebar nav={<HomePageNav pathname={location.pathname}></HomePageNav>} isNavOpen={isNavOpen} theme="dark" />
  );
  const mainContainerId = "main-content-page-layout-tertiary-nav";

  const pageSkipToContent = <SkipToContent href={`#${mainContainerId}`}>Skip to content</SkipToContent>;

  return (
    <Page header={masthead} sidebar={sidebar} skipToContent={pageSkipToContent} mainContainerId={mainContainerId}>
      {props.children}
    </Page>
  );
}
