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
  Page,
  PageHeaderToolsItem,
  PageSection,
  PageSectionVariants,
} from "@patternfly/react-core/dist/js/components/Page";
import { useRoutes } from "../navigation/Hooks";
import { useHistory } from "react-router";
import { MastheadBrand } from "@patternfly/react-core/dist/js/components/Masthead";
import { SettingsButton } from "../settings/SettingsButton";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Alerts } from "../alerts/Alerts";
import { useAlertsController } from "../alerts/AlertsProvider";
import { OpenshiftStatusButton } from "../openshift/OpenShiftStatusButton";

export function OnlineEditorPage(props: { children?: React.ReactNode }) {
  const history = useHistory();
  const routes = useRoutes();
  const [, alertsRef] = useAlertsController();

  return (
    <Page>
      <PageSection variant={PageSectionVariants.light}>
        <Alerts ref={alertsRef} width={"500px"} />
        <Flex>
          <FlexItem>
            <PageHeaderToolsItem>
              <MastheadBrand
                onClick={() => history.replace({ pathname: routes.home.path({}) })}
                style={{ textDecoration: "none" }}
              >
                <TextContent>
                  <Text component={TextVariants.h1}>Serverless Workflow</Text>
                  <Text component={TextVariants.p}>
                    Manage and deploy your Serverless Workflows to the Openshift cloud.
                  </Text>
                </TextContent>
              </MastheadBrand>
            </PageHeaderToolsItem>
          </FlexItem>
          <FlexItem grow={{ default: "grow" }} alignSelf={{ default: "alignSelfCenter" }}>
            <Flex justifyContent={{ default: "justifyContentFlexEnd" }} spaceItems={{ default: "spaceItemsNone" }}>
              <FlexItem>
                <PageHeaderToolsItem>
                  <OpenshiftStatusButton />
                </PageHeaderToolsItem>
              </FlexItem>
              <FlexItem>
                <PageHeaderToolsItem>
                  <SettingsButton />
                </PageHeaderToolsItem>
              </FlexItem>
            </Flex>
          </FlexItem>
        </Flex>
      </PageSection>
      {props.children}
    </Page>
  );
}
