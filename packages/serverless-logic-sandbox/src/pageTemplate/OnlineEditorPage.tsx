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
import { Masthead, MastheadBrand, MastheadMain } from "@patternfly/react-core/dist/js/components/Masthead";
import { Page, PageHeaderToolsItem } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { useHistory } from "react-router";
import { KieSandboxExtendedServicesIcon } from "../kieSandboxExtendedServices/KieSandboxExtendedServicesIcon";
import { useRoutes } from "../navigation/Hooks";
import { OpenshiftDeploymentsDropdown } from "../openshift/OpenshiftDeploymentsDropdown";
import { SettingsButton } from "../settings/SettingsButton";
import { APP_NAME } from "../AppConstants";

export function OnlineEditorPage(props: { children?: React.ReactNode }) {
  const history = useHistory();
  const routes = useRoutes();

  return (
    <Page
      header={
        <Masthead aria-label={"Page header"} display={{ default: "stack" }}>
          <MastheadMain style={{ justifyContent: "space-between" }}>
            <PageHeaderToolsItem className={"pf-l-flex"}>
              <MastheadBrand
                onClick={() => history.push({ pathname: routes.home.path({}) })}
                style={{ textDecoration: "none" }}
              >
                <TextContent>
                  <Text component={TextVariants.h1}>{APP_NAME}</Text>
                </TextContent>
              </MastheadBrand>
            </PageHeaderToolsItem>
            <Flex justifyContent={{ default: "justifyContentFlexEnd" }}>
              <FlexItem>
                <PageHeaderToolsItem>
                  <OpenshiftDeploymentsDropdown />
                </PageHeaderToolsItem>
              </FlexItem>
              <FlexItem>
                <PageHeaderToolsItem>
                  <SettingsButton />
                </PageHeaderToolsItem>
              </FlexItem>
              <FlexItem>
                <PageHeaderToolsItem>
                  <KieSandboxExtendedServicesIcon />
                </PageHeaderToolsItem>
              </FlexItem>
            </Flex>
          </MastheadMain>
        </Masthead>
      }
    >
      {props.children}
    </Page>
  );
}
