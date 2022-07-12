/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { I18nHtml } from "@kie-tools-core/i18n/dist/react-components";
import { Masthead, MastheadBrand, MastheadMain } from "@patternfly/react-core/dist/js/components/Masthead";
import { Page, PageHeaderToolsItem } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import HelpIcon from "@patternfly/react-icons/dist/js/icons/help-icon";
import * as React from "react";
import { useHistory } from "react-router";
import { useAppI18n } from "../i18n";
import { routes } from "../routes";

export function BasePage(props: { children?: React.ReactNode }) {
  const history = useHistory();
  const { i18n } = useAppI18n();

  return (
    <Page
      header={
        <Masthead aria-label={"Page header"} display={{ default: "stack" }} className="app--masthead">
          <MastheadMain style={{ justifyContent: "space-between" }}>
            <PageHeaderToolsItem className={"pf-l-flex"}>
              <MastheadBrand
                onClick={() => history.push({ pathname: routes.root.path({}) })}
                style={{ textDecoration: "none" }}
              >
                <TextContent>
                  <Text component={TextVariants.h1}>Serverless Deployment</Text>
                </TextContent>
              </MastheadBrand>
            </PageHeaderToolsItem>
            <Flex justifyContent={{ default: "justifyContentCenter" }}>
              <FlexItem>
                <PageHeaderToolsItem>
                  <Tooltip
                    className="app--masterhead__disclaimer"
                    position="bottom-end"
                    key="disclaimer-tooltip"
                    content={<I18nHtml>{i18n.masthead.disclaimer.description}</I18nHtml>}
                  >
                    <TextContent>
                      <Text component={TextVariants.h5}>
                        {i18n.masthead.disclaimer.title}
                        <HelpIcon className="app--masterhead__disclaimer-icon" />
                      </Text>
                    </TextContent>
                  </Tooltip>
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
