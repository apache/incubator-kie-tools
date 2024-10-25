/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import React, { useEffect } from "react";
import TasksContainer from "../../containers/TasksContainer/TasksContainer";
import "../../styles.css";
import {
  OUIAProps,
  componentOuiaProps,
  ouiaPageTypeAndObjectId,
} from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { PageTitle } from "@kie-tools/runtime-tools-components/dist/components/PageTitle";
import { Grid, GridItem } from "@patternfly/react-core/dist/js/layouts/Grid";
import { Card } from "@patternfly/react-core/dist/js/components/Card";

const TasksPage: React.FC<OUIAProps> = (ouiaId, ouiaSafe) => {
  useEffect(() => {
    return ouiaPageTypeAndObjectId("tasks-page");
  });

  return (
    <React.Fragment>
      <PageSection
        variant="light"
        {...componentOuiaProps("header" + (ouiaId ? "-" + ouiaId : ""), "tasks-page", ouiaSafe)}
      >
        <PageTitle title="Tasks" />
      </PageSection>
      <PageSection {...componentOuiaProps("content" + (ouiaId ? "-" + ouiaId : ""), "tasks-page", ouiaSafe)}>
        <Grid hasGutter md={1} className={"kogito-management-console__full-size"}>
          <GridItem span={12} className={"kogito-management-console__full-size"}>
            <Card className={"kogito-management-console__full-size"}>
              <TasksContainer />
            </Card>
          </GridItem>
        </Grid>
      </PageSection>
    </React.Fragment>
  );
};

export default TasksPage;
