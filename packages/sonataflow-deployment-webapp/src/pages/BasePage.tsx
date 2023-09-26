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

import { PromiseStateStatus } from "@kie-tools-core/react-hooks/dist/PromiseState";
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
import { Toolbar, ToolbarContent, ToolbarItem } from "@patternfly/react-core/dist/js/components/Toolbar";
import { Tooltip } from "@patternfly/react-core/dist/js/components/Tooltip";
import { BarsIcon } from "@patternfly/react-icons/dist/js/icons";
import HelpIcon from "@patternfly/react-icons/dist/js/icons/help-icon";
import * as React from "react";
import { useMemo } from "react";
import { useHistory } from "react-router";
import { useApp } from "../context/AppContext";
import { routes } from "../routes";
import { BasePageNav } from "./basePage/BasePageNav";
import { Truncate } from "@patternfly/react-core/dist/js/components/Truncate";

export function BasePage(props: { children?: React.ReactNode }) {
  const history = useHistory();
  const app = useApp();

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
            onClick={() => history.push({ pathname: routes.home.path({}) })}
            style={{ textDecoration: "none" }}
          >
            <Brand className="sonataflow-deployment-common--brand" src="favicon.svg" alt="Kie logo"></Brand>
            <TextContent className="brand-name">
              <Text component={TextVariants.h1}>
                <Truncate
                  content={app.appDataPromise.status === PromiseStateStatus.PENDING ? "" : app.data.appName}
                  style={{ maxWidth: "70vw" }}
                />
              </Text>
            </TextContent>
          </MastheadBrand>
        </MastheadMain>
        <MastheadContent>
          <Toolbar id="toolbar" isFullHeight isStatic>
            <ToolbarContent>
              {app.data.showDisclaimer && (
                <ToolbarItem alignment={{ default: "alignRight" }}>
                  <Tooltip
                    className="app--masterhead__disclaimer"
                    position="bottom-end"
                    key="disclaimer-tooltip"
                    content={
                      <>
                        This deployment is intended to be used during <b>development</b>, so users should not use the
                        deployed services in production or for any type of business-critical workloads.
                      </>
                    }
                  >
                    <TextContent>
                      <Text component={TextVariants.h5}>
                        Development only
                        <HelpIcon className="app--masterhead__disclaimer-icon" />
                      </Text>
                    </TextContent>
                  </Tooltip>
                </ToolbarItem>
              )}
            </ToolbarContent>
          </Toolbar>
        </MastheadContent>
      </Masthead>
    ),
    [app.data.appName, history, app.appDataPromise.status, app.data.showDisclaimer]
  );

  return (
    <Page sidebar={<PageSidebar nav={<BasePageNav />} theme="dark" />} header={masthead} isManagedSidebar>
      {props.children}
    </Page>
  );
}
