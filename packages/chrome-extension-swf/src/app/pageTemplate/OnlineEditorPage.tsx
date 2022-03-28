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
import * as React from "react";
import { useRoutes } from "../navigation/Hooks";
import { useHistory } from "react-router";
import { Masthead, MastheadBrand, MastheadMain } from "@patternfly/react-core/dist/js/components/Masthead";
import { SettingsButton } from "../settings/SettingsButton";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Alerts, AlertsController } from "../alerts/Alerts";
import { useAlertsController } from "../alerts/AlertsProvider";

export function OnlineEditorPage(props: { children?: React.ReactNode }) {
  const history = useHistory();
  const routes = useRoutes();
  const [alerts, alertsRef] = useAlertsController();

  return (
    <Page
      header={
        <Masthead aria-label={"Page header"} display={{ default: "stack" }} backgroundColor="light">
          <MastheadMain style={{ justifyContent: "space-between" }}>
            <Alerts ref={alertsRef} width={"500px"} />
            <PageHeaderToolsItem className={"pf-l-flex"}>
              <MastheadBrand
                onClick={() => history.replace({ pathname: routes.home.path({}) })}
                style={{ textDecoration: "none" }}
              >
                <Flex alignItems={{ default: "alignItemsCenter" }}>
                  <FlexItem style={{ display: "flex", alignItems: "center" }}>
                    <TextContent>
                      <Text component={TextVariants.h1}>Serverless Workflow</Text>
                    </TextContent>
                  </FlexItem>
                </Flex>
              </MastheadBrand>
            </PageHeaderToolsItem>
            <Flex justifyContent={{ default: "justifyContentFlexEnd" }}>
              <FlexItem>
                <PageHeaderToolsItem>
                  <SettingsButton />
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
