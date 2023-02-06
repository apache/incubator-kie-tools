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
import { FileTypes } from "../../extension";
import { Grid, GridItem } from "@patternfly/react-core/dist/js/layouts/Grid";
import { Gallery } from "@patternfly/react-core/dist/js/layouts/Gallery";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { NewServerlessModelCard } from "./NewServerlessModelCard";
import { Card, CardBody } from "@patternfly/react-core";
import { ImportFromUrlCard } from "./ImportFromUrlCard";
import { UploadCard } from "./UploadCard";
import { useRoutes } from "../../navigation/Hooks";
import { useHistory } from "react-router";
import { useQueryParam, useQueryParams } from "../../queryParams/QueryParamsContext";
import { QueryParams } from "../../navigation/Routes";
import { useCallback } from "react";
import { NewModelCard } from "./NewModelCard";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Stack } from "@patternfly/react-core/dist/js/layouts/Stack";
import { CardHeader, CardHeaderMain, CardTitle } from "@patternfly/react-core/dist/js/components/Card";

export function Overview() {
  const routes = useRoutes();
  const history = useHistory();
  const expandedWorkspaceId = useQueryParam(QueryParams.EXPAND);
  const queryParams = useQueryParams();

  const closeExpandedWorkspace = useCallback(() => {
    history.replace({
      pathname: routes.home.path({}),
      search: queryParams.without(QueryParams.EXPAND).toString(),
    });
  }, [history, routes, queryParams]);

  const expandWorkspace = useCallback(
    (workspaceId: string) => {
      const expand = workspaceId !== expandedWorkspaceId ? workspaceId : undefined;
      if (!expand) {
        closeExpandedWorkspace();
        return;
      }

      history.replace({
        pathname: routes.home.path({}),
        search: routes.home.queryString({ expand }),
      });
    },
    [closeExpandedWorkspace, history, routes, expandedWorkspaceId]
  );
  return (
    <>
      <PageSection
        className="appsrv-marketing--banner pf-u-background-color-100"
        style={
          {
            "--appsrv-marketing--banner--before--BackgroundImage": "url(images/overview-banner-bg.png)",
            "--appsrv-marketing--banner--before--BackgroundPositionY": "-99px",
            "--appsrv-marketing--banner--before--BackgroundRepeat": "no-repeat",
            "--appsrv-marketing--banner--before--BackgroundSize": "478px",
          } as React.CSSProperties
        }
        variant={"light"}
      >
        <Stack hasGutter>
          <Title headingLevel="h1" size="2xl">
            Welcome to Serverless Logic Web Tools
          </Title>
          <Text className="appsrv-marketing--banner__tagline pf-u-color-200">
            Add-on service to create and synchronize your Serverless Workflow, Decision files, and Dashbuilder files
          </Text>
          <Text component={TextVariants.p}>
            The Serverless Logic Web Tools is a web application that enables you to create and synchronize your
            Serverless Workflow, Decision files, and Dashbuilder files in a single interface. Also, the Serverless Logic
            Web Tools application provides the integrations that are needed to deploy and test the Serverless Workflow
            models in development mode.
          </Text>
        </Stack>
      </PageSection>
      <PageSection className="appsrv-marketing--page-section--marketing" isWidthLimited>
        <Grid hasGutter>
          <GridItem xl={12} xl2={6}>
            <Card className="Dev-ui__card-size" style={{ height: "100%" }}>
              <CardHeader>
                <CardHeaderMain>
                  <CardTitle>
                    <Title headingLevel="h2">Create</Title>
                  </CardTitle>
                </CardHeaderMain>
              </CardHeader>
              <CardBody>
                <Grid>
                  <NewServerlessModelCard
                    title={"Workflow"}
                    jsonExtension={FileTypes.SW_JSON}
                    yamlExtension={FileTypes.SW_YAML}
                    description={"Serverless Workflow files are used to define orchestration logic for services."}
                  />
                  <NewServerlessModelCard
                    title={"Decision"}
                    jsonExtension={FileTypes.YARD_JSON}
                    yamlExtension={FileTypes.YARD_YAML}
                    description={"Serverless Decision files are used to define decision logic for services."}
                  />
                  <NewModelCard
                    title={"Dashboard"}
                    extension={FileTypes.DASH_YAML}
                    description={
                      "Dashboard files are used to define data visualization from data extracted from applications."
                    }
                  />
                </Grid>
              </CardBody>
            </Card>
          </GridItem>
          <GridItem xl={12} xl2={6}>
            <Card className="Dev-ui__card-size" style={{ height: "100%" }}>
              <CardHeader>
                <CardHeaderMain>
                  <CardTitle>
                    <Title headingLevel="h2">Import</Title>
                  </CardTitle>
                </CardHeaderMain>
              </CardHeader>
              <CardBody>
                <Gallery
                  hasGutter={true}
                  // 16px is the "Gutter" width.
                  minWidths={{ sm: "calc(50% - 16px)", default: "100%" }}
                  style={{ height: "calc(100% - 32px)" }}
                >
                  <ImportFromUrlCard />
                  <UploadCard expandWorkspace={expandWorkspace} />
                </Gallery>
              </CardBody>
            </Card>
          </GridItem>
        </Grid>
      </PageSection>
    </>
  );
}
