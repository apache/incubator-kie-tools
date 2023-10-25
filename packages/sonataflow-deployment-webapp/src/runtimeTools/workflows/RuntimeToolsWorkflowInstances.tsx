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
import { EmptyState, EmptyStateBody, EmptyStateIcon } from "@patternfly/react-core/dist/js/components/EmptyState";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import { CubesIcon } from "@patternfly/react-icons/dist/js/icons";
import { useHistory } from "react-router";
import { useApp } from "../../context/AppContext";
import { WorkflowListState } from "./WorkflowList/WorkflowListGatewayApi";
import WorkflowListContainer from "./WorkflowListContainer/WorkflowListContainer";

const PAGE_TITLE = "Workflow Instances";

const DataIndexNotAvailable = () => (
  <PageSection variant="light">
    <Bullseye>
      <EmptyState>
        <EmptyStateIcon icon={CubesIcon} />
        <Title headingLevel="h4" size="lg">
          {`Data Index service not available`}
        </Title>
        <EmptyStateBody>
          <TextContent>
            <Text>
              Start by setting the Data Index in the config file.
              <br />
              Read more on &nbsp;
              <a
                href="https://kiegroup.github.io/kogito-docs/serverlessworkflow/latest/data-index/data-index-core-concepts.html"
                target="_blank"
                rel="noopener noreferrer"
              >
                SonataFlow Guides.
              </a>
            </Text>
          </TextContent>
        </EmptyStateBody>
      </EmptyState>
    </Bullseye>
  </PageSection>
);

export function RuntimeToolsWorkflowInstances() {
  const history = useHistory();
  const app = useApp();
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
          {!app.dataIndexAvailable ? <DataIndexNotAvailable /> : <WorkflowListContainer initialState={initialState} />}
        </PageSection>
      </Page>
    </>
  );
}
