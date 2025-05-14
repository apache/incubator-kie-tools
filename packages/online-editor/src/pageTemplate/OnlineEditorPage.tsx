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

import { Page } from "@patternfly/react-core/dist/js/components/Page";
import { PageHeaderToolsItem } from "@patternfly/react-core/deprecated";

import * as React from "react";
import { useRoutes } from "../navigation/Hooks";
import { useNavigate } from "react-router-dom";
import { Masthead, MastheadBrand, MastheadMain } from "@patternfly/react-core/dist/js/components/Masthead";
import { SettingsButton } from "../settings/SettingsButton";
import { Flex } from "@patternfly/react-core/dist/js/layouts/Flex";
import { ExtendedServicesIcon } from "../extendedServices/ExtendedServicesIcon";
import { DevDeploymentsDropdown } from "../devDeployments/DevDeploymentsDropdown";

import { AccountsIcon } from "../accounts/AccountsIcon";
import { AboutButton } from "../aboutModal/AboutButton";
import { MastheadContent } from "@patternfly/react-core/dist/js/components/Masthead";
import { Toolbar, ToolbarContent } from "@patternfly/react-core/dist/js/components/Toolbar";
import { ToolbarGroup, ToolbarItem } from "@patternfly/react-core/dist/js/components/Toolbar";

export function OnlineEditorPage(props: { children?: React.ReactNode; onKeyDown?: (ke: React.KeyboardEvent) => void }) {
  const navigate = useNavigate();
  const routes = useRoutes();

  return (
    <Page
      onKeyDown={props.onKeyDown}
      header={
        <Masthead aria-label={"Page header"} display={{ default: "inline" }}>
          <MastheadMain style={{ justifyContent: "space-between" }}>
            <PageHeaderToolsItem className={"kie-sandbox--logo"}>
              <Flex
                justifyContent={{ default: "justifyContentFlexEnd" }}
                flexWrap={{ default: "nowrap" }}
                gap={{ default: "gapMd" }}
              >
                <MastheadBrand
                  component="a"
                  onClick={() => navigate({ pathname: routes.home.path({}) })}
                  style={{ textDecoration: "none" }}
                >
                  <img alt={"Logo"} src={routes.static.images.appLogoReverse.path({})} style={{ height: "38px" }} />
                </MastheadBrand>
                <AboutButton />
              </Flex>
            </PageHeaderToolsItem>
          </MastheadMain>
          <MastheadContent>
            <Toolbar isStatic>
              <ToolbarContent>
                <ToolbarGroup align={{ default: "alignRight" }} spacer={{ default: "spacerNone", md: "spacerMd" }}>
                  <ToolbarItem>
                    <DevDeploymentsDropdown />
                  </ToolbarItem>
                  <ToolbarItem variant="separator" />
                  <ToolbarItem>
                    <ExtendedServicesIcon />
                  </ToolbarItem>
                  <ToolbarItem>
                    <SettingsButton />
                  </ToolbarItem>
                  <ToolbarItem>
                    <AccountsIcon />
                  </ToolbarItem>
                </ToolbarGroup>
              </ToolbarContent>
            </Toolbar>
          </MastheadContent>
        </Masthead>
      }
    >
      {props.children}
    </Page>
  );
}
