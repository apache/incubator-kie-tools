/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { useRoutes } from "../../navigation/Hooks";
import { Page, PageHeaderToolsItem } from "@patternfly/react-core/dist/js/components/Page";
import { Masthead, MastheadBrand, MastheadMain } from "@patternfly/react-core/dist/js/components/Masthead";
import { Flex, FlexItem } from "@patternfly/react-core/dist/js/layouts/Flex";
import { Brand } from "@patternfly/react-core/dist/js/components/Brand";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";

export function StartupBlockerTemplate(props: { children?: React.ReactNode }) {
  const routes = useRoutes();
  return (
    <Page
      header={
        <Masthead aria-label={"Page header"} display={{ default: "stack" }}>
          <MastheadMain style={{ justifyContent: "space-between" }}>
            <PageHeaderToolsItem className={"kie-sandbox--logo"}>
              <MastheadBrand style={{ textDecoration: "none" }}>
                <Flex alignItems={{ default: "alignItemsCenter" }}>
                  <FlexItem style={{ display: "flex", alignItems: "center" }}>
                    <Brand
                      src={routes.static.images.appLogoReverse.path({})}
                      alt={"Logo"}
                      heights={{ default: "38px" }}
                    >
                      <source srcSet={routes.static.images.appLogoReverse.path({})} />
                    </Brand>
                  </FlexItem>
                </Flex>
              </MastheadBrand>
            </PageHeaderToolsItem>
          </MastheadMain>
        </Masthead>
      }
    >
      {props.children}
    </Page>
  );
}
