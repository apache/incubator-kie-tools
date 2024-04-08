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

import { PageSectionHeader } from "@kie-tools/runtime-tools-components/dist/components/PageSectionHeader";
import {
  componentOuiaProps,
  ouiaPageTypeAndObjectId,
  OUIAProps,
} from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import { WorkflowDefinition, WorkflowListState } from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";
import { WorkflowDefinitionListContainer } from "@kie-tools/runtime-tools-swf-webapp-components/dist/WorkflowDefinitionListContainer";
import { WorkflowListContainer } from "@kie-tools/runtime-tools-swf-webapp-components/dist/WorkflowListContainer";
import { Card } from "@patternfly/react-core/dist/js/components/Card";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import { Tab, Tabs, TabTitleText } from "@patternfly/react-core/dist/js/components/Tabs";
import * as H from "history";
import React, { ReactText, useCallback, useEffect, useState } from "react";
import { StaticContext, useHistory } from "react-router";
import { RouteComponentProps } from "react-router-dom";
import { useDevUIAppContext } from "../../contexts/DevUIAppContext";
import "../../styles.css";
import {
  useWorkflowListGatewayApi,
  WorkflowListGatewayApi,
} from "@kie-tools/runtime-tools-swf-webapp-components/dist/WorkflowList";

interface MatchProps {
  instanceID: string;
}

const WorkflowsPage: React.FC<RouteComponentProps<MatchProps, StaticContext, H.LocationState> & OUIAProps> = ({
  ouiaId,
  ouiaSafe,
  ...props
}) => {
  const apiContext = useDevUIAppContext();
  const history = useHistory();
  const gatewayApi: WorkflowListGatewayApi = useWorkflowListGatewayApi();

  const [activeTabKey, setActiveTabKey] = useState<ReactText>(0);

  useEffect(() => {
    return ouiaPageTypeAndObjectId("workflow-instances");
  });

  const initialState: WorkflowListState = props.location && (props.location.state as WorkflowListState);

  const handleTabClick = (_event: React.MouseEvent<HTMLElement, MouseEvent>, tabIndex: number) => {
    setActiveTabKey(tabIndex);
  };

  const onOpenWorkflowDetails = useCallback(
    (args: { workflowId: string; state: WorkflowListState }) => {
      history.push({
        pathname: `/Workflow/${args.workflowId}`,
        state: gatewayApi.workflowListState,
      });
    },
    [history]
  );

  const onOpenWorkflowForm = useCallback(
    (workflowDefinition: WorkflowDefinition) => {
      history.push({
        pathname: `/WorkflowDefinition/Form/${workflowDefinition.workflowName}`,
        state: {
          workflowDefinition: {
            workflowName: workflowDefinition.workflowName,
            endpoint: workflowDefinition.endpoint,
          },
        },
      });
    },
    [history]
  );

  return (
    <React.Fragment>
      {activeTabKey === 0 && <PageSectionHeader titleText={`Workflow Instances`} ouiaId={ouiaId} />}
      {activeTabKey === 1 && <PageSectionHeader titleText={`Workflow Definitions`} ouiaId={ouiaId} />}
      <div>
        <Tabs
          activeKey={activeTabKey}
          onSelect={handleTabClick}
          isBox
          variant="light300"
          style={{
            background: "white",
          }}
        >
          <Tab id="workflow-list-tab" eventKey={0} title={<TabTitleText>Workflow Instances</TabTitleText>}>
            <PageSection {...componentOuiaProps(ouiaId, "workflow-list-page-section", ouiaSafe)}>
              <Card className="Dev-ui__card-size">
                <WorkflowListContainer
                  initialState={initialState}
                  onOpenWorkflowDetails={onOpenWorkflowDetails}
                  targetOrigin={apiContext.getDevUIUrl()}
                />
              </Card>
            </PageSection>
          </Tab>
          <Tab id="workflow-definitions-tab" eventKey={1} title={<TabTitleText>Workflow Definitions</TabTitleText>}>
            <PageSection {...componentOuiaProps(ouiaId, "workflow-definition-list-page-section", ouiaSafe)}>
              <Card className="Dev-ui__card-size">
                <WorkflowDefinitionListContainer
                  onOpenWorkflowForm={onOpenWorkflowForm}
                  targetOrigin={apiContext.getDevUIUrl()}
                />
              </Card>
            </PageSection>
          </Tab>
        </Tabs>
      </div>
    </React.Fragment>
  );
};

export default WorkflowsPage;
