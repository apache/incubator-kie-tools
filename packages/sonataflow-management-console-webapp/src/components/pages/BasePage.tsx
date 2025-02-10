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
import { Brand } from "@patternfly/react-core/dist/js/components/Brand";
import {
  Masthead,
  MastheadBrand,
  MastheadContent,
  MastheadMain,
  MastheadToggle,
} from "@patternfly/react-core/dist/js/components/Masthead";
import { Page, PageToggleButton } from "@patternfly/react-core/dist/js/components/Page";
import { PageSidebar } from "@patternfly/react-core/dist/js/components/Page/PageSidebar";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { BarsIcon } from "@patternfly/react-icons/dist/js/icons";
import PageToolbar from "@kie-tools/runtime-tools-components/dist/components/PageToolbar/PageToolbar";
import { useMemo } from "react";
import { useHistory } from "react-router";
import { routes } from "../../navigation/Routes";
import { ManagementConsoleNav } from "../console";

export function BasePage(props: { children?: React.ReactNode }) {
  const history = useHistory();

  const masthead = useMemo(
    () => (
      <Masthead aria-label={"Page header"} className="app--masthead">
        <MastheadToggle>
          <PageToggleButton variant="plain" aria-label="Global navigation">
            <BarsIcon />
          </PageToggleButton>
        </MastheadToggle>
        <MastheadMain>
          <MastheadBrand
            component="a"
            onClick={() => history.push({ pathname: routes.home.path({}) })}
            style={{ textDecoration: "none" }}
          >
            <Brand className="sonataflow-management-console-common--brand" src="favicon.svg" alt="Kie logo"></Brand>
            <TextContent className="brand-name">
              <Text component={TextVariants.h1}>SonataFlow Management Console</Text>
            </TextContent>
          </MastheadBrand>
        </MastheadMain>
        <MastheadContent>
          <PageToolbar />
        </MastheadContent>
      </Masthead>
    ),
    [history]
  );

  return (
    <Page
      sidebar={
        <PageSidebar theme="dark">
          <ManagementConsoleNav pathname={history.location.pathname} />
        </PageSidebar>
      }
      header={masthead}
      isManagedSidebar
    >
      {props.children}
    </Page>
  );
}
