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
import React from "react";
import { FileTypes } from "@kie-tools-core/workspaces-git-fs/dist/constants/ExtensionHelper";
import { QuickStartContext, QuickStartContextValues } from "@patternfly/quickstarts";
import { Card, CardBody, CardHeader, CardTitle } from "@patternfly/react-core/dist/js/components/Card";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Gallery } from "@patternfly/react-core/dist/js/layouts/Gallery";
import { Grid, GridItem } from "@patternfly/react-core/dist/js/layouts/Grid";
import { useContext, useMemo } from "react";
import { ImportFromUrlCard } from "./ImportFromUrlCard";
import { NewModelCard } from "./NewModelCard";
import { UploadCard } from "./UploadCard";

export function CreateOrImportModelGrid(props: { isNavOpen: boolean }) {
  const qsContext = useContext<QuickStartContextValues>(QuickStartContext);

  const force12Cols = useMemo(
    () => props.isNavOpen && !!qsContext.activeQuickStartID,
    [props.isNavOpen, qsContext.activeQuickStartID]
  );

  return (
    <Grid hasGutter>
      <GridItem xl={12} xl2={force12Cols ? 12 : 6}>
        <Card className="Dev-ui__card-size" style={{ height: "100%" }}>
          <CardHeader>
            <CardTitle>
              <Title headingLevel="h2">Create</Title>
            </CardTitle>
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
            <CardTitle>
              <Title headingLevel="h2">Import</Title>
            </CardTitle>
          </CardHeader>
          <CardBody>
            <Gallery
              hasGutter={true}
              // 16px is the "Gutter" width.
              minWidths={{ sm: "calc(50% - 16px)", default: "100%" }}
              style={{ height: "calc(100% - 32px)" }}
            >
              <ImportFromUrlCard />
              <UploadCard />
            </Gallery>
          </CardBody>
        </Card>
      </GridItem>
    </Grid>
  );
}
