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

import React, { ReactText, useCallback, useEffect, useState } from "react";
import { Toolbar, ToolbarContent, ToolbarGroup, ToolbarItem } from "@patternfly/react-core/dist/js/components/Toolbar";
import { Select, SelectOption, SelectVariant } from "@patternfly/react-core/dist/js/components/Select";
import { Tab, Tabs, TabTitleText } from "@patternfly/react-core/dist/js/components/Tabs";
import { Card } from "@patternfly/react-core/dist/js/components/Card";
import { PageSection } from "@patternfly/react-core/dist/js/components/Page";
import {
  OUIAProps,
  ouiaPageTypeAndObjectId,
  componentOuiaProps,
} from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import { PageSectionHeader } from "@kie-tools/runtime-tools-components/dist/components/PageSectionHeader";
import MonitoringContainer from "../../containers/MonitoringContainer/MonitoringContainer";
import {
  KogitoEmptyState,
  KogitoEmptyStateType,
} from "@kie-tools/runtime-tools-components/dist/components/KogitoEmptyState";
import "../../styles.css";
import { WorkflowInstance, WorkflowInstanceState } from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";
import { Dashboard } from "@kie-tools/runtime-tools-shared-enveloped-components/dist/monitoring";
import {
  WorkflowListGatewayApi,
  useWorkflowListGatewayApi,
} from "@kie-tools/runtime-tools-swf-webapp-components/dist/WorkflowList";
import { OrderBy } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";

interface Props {
  dataIndexUrl?: string;
}
const MonitoringPage: React.FC<OUIAProps & Props> = ({ ouiaId, ouiaSafe, dataIndexUrl }) => {
  const gatewayApi: WorkflowListGatewayApi = useWorkflowListGatewayApi();
  const [hasWorkflow, setHasWorkflow] = useState(false);
  const [loading, setLoading] = useState(true);
  const [openWorkflowSelect, setOpenWorkflowSelect] = useState(false);
  const [dashboard, setDashboard] = useState(Dashboard.MONITORING);
  const [workflowList, setWorkflowList] = useState<WorkflowInstance[]>([]);
  const [selectedWorkflow, setSelectedWorkflow] = useState<WorkflowInstance>();
  const [activeTabKey, setActiveTabKey] = useState<ReactText>(0);

  const initialLoad = () =>
    gatewayApi.initialLoad(
      {
        status: [
          WorkflowInstanceState.Aborted,
          WorkflowInstanceState.Active,
          WorkflowInstanceState.Completed,
          WorkflowInstanceState.Error,
          WorkflowInstanceState.Suspended,
        ],
        businessKey: [],
      },
      { start: OrderBy.DESC }
    );

  const loadWorkflowList = useCallback(() => {
    gatewayApi.query(0, 1000).then((list) => {
      setSelectedWorkflow(list[0]);
      setWorkflowList(list);
    });
  }, [workflowList, selectedWorkflow]);

  useEffect(() => {
    const intervaId = setInterval(() => {
      if (!hasWorkflow) {
        initialLoad();
        gatewayApi.query(0, 1).then((list) => {
          if (list.length > 0) {
            setHasWorkflow(true);
            loadWorkflowList();
          }
          setLoading(false);
        });
      }
    }, 500);
    return () => clearInterval(intervaId);
  }, [hasWorkflow, loading]);

  useEffect(() => {
    if (dashboard === Dashboard.DETAILS) {
      loadWorkflowList();
    }
  }, [dashboard]);

  useEffect(() => {
    return ouiaPageTypeAndObjectId("monitoring");
  });

  return (
    <React.Fragment>
      <PageSectionHeader titleText="Monitoring" ouiaId={ouiaId} />
      {hasWorkflow ? (
        <>
          <Tabs
            activeKey={activeTabKey}
            onSelect={(event, tabIndex) => {
              setActiveTabKey(tabIndex);
              const dashboard = tabIndex === 0 ? Dashboard.MONITORING : Dashboard.DETAILS;
              setDashboard(dashboard);
              loadWorkflowList();
            }}
            isBox
            variant="light300"
            style={{
              background: "white",
            }}
          >
            <Tab id="monitoring-report-tab" eventKey={0} title={<TabTitleText>Summary</TabTitleText>}></Tab>
            <Tab id="monitoring-workflow-tab" eventKey={1} title={<TabTitleText>Workflows</TabTitleText>}></Tab>
          </Tabs>
          <PageSection {...componentOuiaProps(ouiaId, "monitoring-page-section", ouiaSafe)}>
            <Card className="Dev-ui__card-size">
              {dashboard === Dashboard.DETAILS && (
                <Toolbar>
                  <ToolbarContent>
                    <ToolbarGroup>
                      <ToolbarItem>
                        <Select
                          aria-labelledby={"workfflow-id-select"}
                          variant={SelectVariant.single}
                          onSelect={(event, v) => {
                            setSelectedWorkflow(workflowList.find((p) => p.id === v));
                            setOpenWorkflowSelect(false);
                          }}
                          onToggle={() => setOpenWorkflowSelect(!openWorkflowSelect)}
                          isOpen={openWorkflowSelect}
                          placeholderText="Select Workflow"
                          hasInlineFilter
                          maxHeight={"300px"}
                        >
                          {workflowList.map((p, i) => (
                            <SelectOption key={i} value={p.id} description={p.processId} />
                          ))}
                        </Select>
                      </ToolbarItem>
                    </ToolbarGroup>
                  </ToolbarContent>
                </Toolbar>
              )}
              <MonitoringContainer
                dataIndexUrl={dataIndexUrl}
                workflow={selectedWorkflow ? selectedWorkflow.id : undefined}
                dashboard={dashboard}
              />
            </Card>
          </PageSection>
        </>
      ) : (
        <KogitoEmptyState
          title={loading ? "Loading" : "No Data"}
          body={loading ? "Loading Data" : "No workflows were started"}
          type={KogitoEmptyStateType.Info}
        />
      )}
    </React.Fragment>
  );
};

export default MonitoringPage;
