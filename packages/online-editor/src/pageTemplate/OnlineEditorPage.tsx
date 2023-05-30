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

import { Page, PageHeaderToolsItem } from "@patternfly/react-core/dist/js/components/Page";
import { Brand } from "@patternfly/react-core/dist/js/components/Brand";
import * as React from "react";
import { useRoutes } from "../navigation/Hooks";
import { useHistory } from "react-router";
import { Masthead, MastheadBrand, MastheadMain } from "@patternfly/react-core/dist/js/components/Masthead";
import { SettingsButton } from "../settings/SettingsButton";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { ExtendedServicesIcon } from "../extendedServices/ExtendedServicesIcon";
import { DevDeploymentsDropdown } from "../devDeployments/DevDeploymentsDropdown";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { AccountsIcon } from "../accounts/AccountsIcon";
import { AboutButton } from "../aboutModal/AboutButton";
import { MastheadContent } from "@patternfly/react-core/dist/js/components/Masthead";
import { Toolbar, ToolbarContent } from "@patternfly/react-core/dist/js/components/Toolbar";
import { ToolbarGroup, ToolbarItem } from "@patternfly/react-core/dist/js/components/Toolbar";

export function OnlineEditorPage(props: { children?: React.ReactNode; onKeyDown?: (ke: React.KeyboardEvent) => void }) {
  const history = useHistory();
  const routes = useRoutes();

  return (
    <Page
      onKeyDown={props.onKeyDown}
      header={
        <Masthead aria-label={"Page header"} display={{ default: "inline" }}>
          <MastheadMain style={{ justifyContent: "space-between" }}>
            <PageHeaderToolsItem className={"kie-sandbox--logo"}>
              <Flex justifyContent={{ default: "justifyContentFlexEnd" }} flexWrap={{ default: "nowrap" }}>
                <MastheadBrand
                  onClick={() => history.push({ pathname: routes.home.path({}) })}
                  style={{ textDecoration: "none" }}
                >
                  <Brand src={routes.static.images.appLogoReverse.path({})} alt={"Logo"} heights={{ default: "38px" }}>
                    <source srcSet={routes.static.images.appLogoReverse.path({})} />
                  </Brand>
                </MastheadBrand>
                <AboutButton />
              </Flex>
            </PageHeaderToolsItem>
          </MastheadMain>
          <MastheadContent>
            <Toolbar isStatic>
              <ToolbarContent>
                <ToolbarGroup alignment={{ default: "alignRight" }} spacer={{ default: "spacerNone", md: "spacerMd" }}>
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
