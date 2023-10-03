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
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import WorkflowListContainer from "./WorkflowListContainer/WorkflowListContainer";
import { useHistory } from "react-router";
import { WorkflowListState } from "./WorkflowList/WorkflowListGatewayApi";

const PAGE_TITLE = "Workflow Instances";

export function RuntimeToolsWorkflowInstances() {
  const history = useHistory();

  const initialState: WorkflowListState = history.location && (history.location.state as WorkflowListState);

  return (
    <>
      <Page>
        <PageSection variant={"light"}>
          <TextContent>
            <Text component={TextVariants.h1}>{PAGE_TITLE}</Text>
            <Text component={TextVariants.p}>
              List and view workflows from the Data Index linked in your Runtime Tools settings.
            </Text>
          </TextContent>
        </PageSection>

        <PageSection isFilled aria-label="workflow-instances-section">
          <WorkflowListContainer initialState={initialState} />
        </PageSection>
      </Page>
    </>
  );
}
