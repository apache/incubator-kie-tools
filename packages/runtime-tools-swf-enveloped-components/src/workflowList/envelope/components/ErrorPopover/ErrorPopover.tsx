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
import { Popover } from "@patternfly/react-core/dist/js/components/Popover";
import { Button } from "@patternfly/react-core/dist/js/components/Button";
import { WorkflowInstanceIconCreator } from "../utils/WorkflowListUtils";
import { WorkflowInstance } from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";
import { componentOuiaProps, OUIAProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";

interface ErrorPopoverProps {
  workflowInstanceData: WorkflowInstance;
  onSkipClick: (workflowInstance: WorkflowInstance) => Promise<void>;
  onRetryClick: (workflowInstance: WorkflowInstance) => Promise<void>;
}
const ErrorPopover: React.FC<ErrorPopoverProps & OUIAProps> = ({
  workflowInstanceData,
  onSkipClick,
  onRetryClick,
  ouiaId,
  ouiaSafe,
}) => {
  return (
    <Popover
      zIndex={300}
      id={workflowInstanceData.id}
      headerContent={<div>Workflow error</div>}
      bodyContent={
        <div>{workflowInstanceData.error ? workflowInstanceData.error.message : "No error message found"}</div>
      }
      footerContent={
        workflowInstanceData.addons?.includes("workflow-management") &&
        workflowInstanceData.serviceUrl && [
          <Button
            key="confirm1"
            id="skip-button"
            variant="secondary"
            onClick={() => onSkipClick(workflowInstanceData)}
            className="pf-u-mr-sm"
          >
            Skip
          </Button>,
          <Button
            key="confirm2"
            variant="secondary"
            id="retry-button"
            onClick={() => onRetryClick(workflowInstanceData)}
            className="pf-u-mr-sm"
          >
            Retry
          </Button>,
        ]
      }
      position="auto"
      {...componentOuiaProps(ouiaId, "error-popover", ouiaSafe)}
    >
      <Button variant="link" isInline>
        {WorkflowInstanceIconCreator(workflowInstanceData.state)}
      </Button>
    </Popover>
  );
};

export default ErrorPopover;
