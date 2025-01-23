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

import { Card, CardBody, CardHeader } from "@patternfly/react-core/dist/js/components/Card";
import { Title } from "@patternfly/react-core/dist/js/components/Title";
import { TextContent } from "@patternfly/react-core/dist/js/components/Text";
import { Label } from "@patternfly/react-core/dist/js/components/Label";
import React from "react";
import ReactJson from "@microlink/react-json-view";
import { InfoCircleIcon } from "@patternfly/react-icons/dist/js/icons/info-circle-icon";
import { WorkflowInstance, WorkflowInstanceState } from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";
import { componentOuiaProps, OUIAProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import "../styles.css";

interface WorkflowVariablesProps {
  displayLabel: boolean;
  displaySuccess: boolean;
  setDisplayLabel: (displayLabel: boolean) => void;
  setUpdateJson: (variableJson: Record<string, unknown>) => void;
  updateJson: Record<string, unknown>;
  workflowInstance: WorkflowInstance;
}

const WorkflowVariables: React.FC<WorkflowVariablesProps & OUIAProps> = ({
  displayLabel,
  displaySuccess,
  ouiaId,
  ouiaSafe,
  setDisplayLabel,
  setUpdateJson,
  updateJson,
  workflowInstance,
}) => {
  const handleVariablesChange = (e: any) => {
    setUpdateJson({ ...updateJson, ...e.updated_src });
    setDisplayLabel(true);
  };
  const checkWorkflowStatus =
    workflowInstance.state === WorkflowInstanceState.Completed ||
    workflowInstance.state === WorkflowInstanceState.Aborted
      ? false
      : handleVariablesChange;

  return (
    <Card {...componentOuiaProps(ouiaId, "workflow-variables", ouiaSafe)}>
      <CardHeader>
        <Title headingLevel="h3" size="xl">
          Variables
        </Title>
        {displayLabel && (
          <Label color="orange" icon={<InfoCircleIcon />}>
            {" "}
            Changes are not saved yet
          </Label>
        )}
        <Label
          color="green"
          icon={<InfoCircleIcon />}
          className={
            displaySuccess
              ? "kogito-workflow-details--variables__label-fadeIn"
              : "kogito-workflow-details--variables__label-fadeOut"
          }
        >
          {" "}
          Changes are saved
        </Label>
      </CardHeader>
      <CardBody>
        <TextContent>
          <div>
            <ReactJson
              src={updateJson}
              name={false}
              onEdit={checkWorkflowStatus}
              onAdd={checkWorkflowStatus}
              onDelete={checkWorkflowStatus}
            />
          </div>
        </TextContent>
      </CardBody>
    </Card>
  );
};

export default React.memo(WorkflowVariables);
