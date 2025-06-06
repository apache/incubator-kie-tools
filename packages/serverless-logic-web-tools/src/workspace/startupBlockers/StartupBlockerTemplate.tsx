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

import * as React from "react";
import { Page } from "@patternfly/react-core/dist/js/components/Page";
import { PageHeaderToolsItem } from "@patternfly/react-core/deprecated";
import { Masthead, MastheadBrand, MastheadMain } from "@patternfly/react-core/dist/js/components/Masthead";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { APP_NAME } from "../../AppConstants";

export function StartupBlockerTemplate(props: { children?: React.ReactNode }) {
  return (
    <Page
      header={
        <Masthead aria-label={"Page header"} display={{ default: "stack" }}>
          <MastheadMain style={{ justifyContent: "space-between" }}>
            <PageHeaderToolsItem className={"pf-v5-l-flex"}>
              <MastheadBrand component="a" style={{ textDecoration: "none" }}>
                <TextContent>
                  <Text component={TextVariants.h1}>{APP_NAME}</Text>
                </TextContent>
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
