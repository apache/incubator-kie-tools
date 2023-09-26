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

import React, { useEffect, useRef } from "react";
import { Page, PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Text, TextContent, TextVariants } from "@patternfly/react-core/dist/js/components/Text";
import { useHistory } from "react-router";
import { Card, CardBody } from "@patternfly/react-core/dist/js/components/Card";
import WorkflowFormContainer from "./WorkflowFormContainer/WorkflowFormContainer";
import { useWorkflowFormGatewayApi, WorkflowFormGatewayApi } from "./WorkflowForm";
import { WorkflowDefinition } from "@kie-tools/runtime-tools-common";
import { ouiaPageTypeAndObjectId } from "@kie-tools/runtime-tools-common/dist/ouiaTools";
import { InlineEdit, InlineEditApi } from "@kie-tools/runtime-tools-common/dist/components/InlineEdit";

const PAGE_TITLE = "Start new workflow";

export function RuntimeToolsWorkflowForm() {
  const history = useHistory();
  const gatewayApi: WorkflowFormGatewayApi = useWorkflowFormGatewayApi();

  const inlineEditRef = useRef<InlineEditApi>(null);

  const workflowDefinition: WorkflowDefinition = (history.location.state as any)["workflowDefinition"];

  const onResetForm = () => {
    gatewayApi.setBusinessKey("");
    inlineEditRef.current!.reset();
  };

  const getBusinessKey = () => {
    return gatewayApi.getBusinessKey();
  };

  useEffect(() => {
    onResetForm();
    return ouiaPageTypeAndObjectId("workflow-form");
  }, []);

  return (
    <>
      <Page>
        <PageSection variant={"light"}>
          <TextContent>
            <Text component={TextVariants.h1}>{PAGE_TITLE}</Text>
            <Text component={TextVariants.p}>
              <InlineEdit
                ref={inlineEditRef}
                setBusinessKey={(bk: string) => gatewayApi.setBusinessKey(bk)}
                getBusinessKey={getBusinessKey}
              />
            </Text>
          </TextContent>
        </PageSection>

        <PageSection isFilled aria-label="workflow-definitions-section">
          <Card>
            <CardBody>
              <WorkflowFormContainer workflowDefinitionData={workflowDefinition} onResetForm={onResetForm} />
            </CardBody>
          </Card>
        </PageSection>
      </Page>
    </>
  );
}
