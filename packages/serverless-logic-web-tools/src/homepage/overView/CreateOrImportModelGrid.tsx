/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
import React from "react";
import { FileTypes } from "@kie-tools-core/workspaces-git-fs/dist/constants/ExtensionHelper";
import { QuickStartContext, QuickStartContextValues } from "@patternfly/quickstarts";
import { Card, CardBody } from "@patternfly/react-core/dist/js/components/Card";
import { CardHeader, CardHeaderMain, CardTitle } from "@patternfly/react-core/dist/js/components/Card";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Gallery } from "@patternfly/react-core/dist/js/layouts/Gallery";
import { Grid, GridItem } from "@patternfly/react-core/dist/js/layouts/Grid";
import { useCallback, useContext, useMemo } from "react";
import { useHistory } from "react-router";
import { useRoutes } from "../../navigation/Hooks";
import { QueryParams } from "../../navigation/Routes";
import { useQueryParam, useQueryParams } from "../../queryParams/QueryParamsContext";
import { ImportFromUrlCard } from "./ImportFromUrlCard";
import { NewModelCard } from "./NewModelCard";
import { UploadCard } from "./UploadCard";

export function CreateOrImportModelGrid(props: { isNavOpen: boolean }) {
  const routes = useRoutes();
  const history = useHistory();
  const expandedWorkspaceId = useQueryParam(QueryParams.EXPAND);
  const queryParams = useQueryParams();
  const qsContext = useContext<QuickStartContextValues>(QuickStartContext);

  const force12Cols = useMemo(
    () => props.isNavOpen && !!qsContext.activeQuickStartID,
    [props.isNavOpen, qsContext.activeQuickStartID]
  );

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
    <Grid hasGutter>
      <GridItem xl={12} xl2={force12Cols ? 12 : 6}>
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
              <NewModelCard
                title={"Workflow"}
                jsonExtension={FileTypes.SW_JSON}
                yamlExtension={FileTypes.SW_YAML}
                description={"Define orchestration logic for services."}
              />
              <NewModelCard
                title={"Decision"}
                jsonExtension={FileTypes.YARD_JSON}
                yamlExtension={FileTypes.YARD_YAML}
                description={"Define decision logic for services."}
              />
              <NewModelCard
                title={"Dashboard"}
                yamlExtension={FileTypes.DASH_YAML}
                description={"Define data visualization from data extracted from applications."}
              />
            </Grid>
          </CardBody>
        </Card>
      </GridItem>
      <GridItem xl={12} xl2={force12Cols ? 12 : 6}>
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
  );
}
