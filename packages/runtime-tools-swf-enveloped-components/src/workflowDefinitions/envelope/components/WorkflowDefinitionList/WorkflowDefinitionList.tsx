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

import React, { useCallback, useEffect, useState } from "react";
import { ServerErrors } from "@kie-tools/runtime-tools-components/dist/components/ServerErrors";
import { DataTable, DataTableColumn } from "@kie-tools/runtime-tools-components/dist/components/DataTable";
import { KogitoSpinner } from "@kie-tools/runtime-tools-components/dist/components/KogitoSpinner";
import { OUIAProps, componentOuiaProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import { getActionColumn, getColumn } from "../utils/WorkflowDefinitionListUtils";
import { WorkflowDefinitionListDriver } from "../../../api/WorkflowDefinitionListDriver";
import { Divider } from "@patternfly/react-core/dist/js/components/Divider";
import { Bullseye } from "@patternfly/react-core/dist/js/layouts/Bullseye";
import WorkflowDefinitionListToolbar from "../WorkflowDefinitionListToolbar/WorkflowDefinitionListToolbar";
import { WorkflowDefinition } from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";

export interface WorkflowDefinitionListProps {
  isEnvelopeConnectedToChannel: boolean;
  driver: WorkflowDefinitionListDriver;
}

const WorkflowDefinitionList: React.FC<WorkflowDefinitionListProps & OUIAProps> = ({
  isEnvelopeConnectedToChannel,
  driver,
  ouiaId,
  ouiaSafe,
}) => {
  const [workflowDefinitionList, setWorkflowDefinitionList] = useState<WorkflowDefinition[]>([]);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [filterWorkflowNames, setFilterWorkflowNames] = useState<string[]>([]);
  const [error, setError] = useState<string>();

  const doQuery = async (): Promise<void> => {
    try {
      const response: WorkflowDefinition[] = await driver.getWorkflowDefinitionsQuery();
      setWorkflowDefinitionList(response);
      setIsLoading(false);
    } catch (err) {
      setError(err.errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (!isEnvelopeConnectedToChannel) {
      setIsLoading(true);
      return;
    }
    init();
    return () => {
      setFilterWorkflowNames([]);
    };
  }, [isEnvelopeConnectedToChannel]);

  const init = async (): Promise<void> => {
    doQuery();
  };

  const columns: DataTableColumn[] = [
    getColumn("workflowName", `Workflow Name`),
    getColumn("endpoint", "Endpoint"),
    getActionColumn(
      (workflowDefinition) => {
        driver.openWorkflowForm(workflowDefinition);
      },
      (workflowDefinition) => {
        driver.openTriggerCloudEvent(workflowDefinition);
      }
    ),
  ];

  const applyFilter = async (): Promise<void> => {
    await driver.setWorkflowDefinitionFilter(filterWorkflowNames);
  };

  const doRefresh = async (): Promise<void> => {
    setIsLoading(true);
    doQuery();
  };

  const filterWorkflowDefinition = (): WorkflowDefinition[] => {
    if (filterWorkflowNames.length === 0) {
      return workflowDefinitionList;
    }
    return workflowDefinitionList.filter((workflowDefinition) => {
      return filterWorkflowNames.some((filter) => workflowDefinition.workflowName.includes(filter));
    });
  };

  const workflowDefinitionLoadingComponent: JSX.Element = (
    <Bullseye>
      <KogitoSpinner spinnerText={`Loading workflow definitions...`} ouiaId="forms-list-loading-workflow-definitions" />
    </Bullseye>
  );

  if (error) {
    return <ServerErrors error={error} variant={"large"} />;
  }

  return (
    <div {...componentOuiaProps(ouiaId, "workflow-definition-list", ouiaSafe ? ouiaSafe : !isLoading)}>
      <WorkflowDefinitionListToolbar
        filterWorkflowNames={filterWorkflowNames}
        setFilterWorkflowNames={setFilterWorkflowNames}
        applyFilter={applyFilter}
        doRefresh={doRefresh}
      />
      <Divider />
      <DataTable
        data={filterWorkflowDefinition()}
        isLoading={isLoading}
        columns={columns}
        error={false}
        LoadingComponent={workflowDefinitionLoadingComponent}
      />
    </div>
  );
};

export default WorkflowDefinitionList;
