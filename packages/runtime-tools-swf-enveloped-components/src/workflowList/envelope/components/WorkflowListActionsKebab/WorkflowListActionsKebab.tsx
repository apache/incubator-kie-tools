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

import React, { useMemo, useState } from "react";
import { Dropdown, DropdownItem, KebabToggle } from "@patternfly/react-core/dist/js/components/Dropdown";
import { WorkflowInstance, WorkflowInstanceState } from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";
import { componentOuiaProps, OUIAProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";
import { checkWorkflowInstanceState } from "../utils/WorkflowListUtils";

interface WorkflowListActionsKebabProps {
  workflowInstance: WorkflowInstance;
  onSkipClick: (workflowInstance: WorkflowInstance) => Promise<void>;
  onRetryClick: (workflowInstance: WorkflowInstance) => Promise<void>;
  onAbortClick: (workflowInstance: WorkflowInstance) => Promise<void>;
  onOpenTriggerCloudEvent?: (workflowInstance: WorkflowInstance) => void;
}

const WorkflowListActionsKebab: React.FC<WorkflowListActionsKebabProps & OUIAProps> = ({
  workflowInstance,
  onSkipClick,
  onRetryClick,
  onAbortClick,
  onOpenTriggerCloudEvent,
  ouiaId,
  ouiaSafe,
}) => {
  const [isKebabOpen, setIsKebabOpen] = useState<boolean>(false);

  const onSelect = (): void => {
    setIsKebabOpen(!isKebabOpen);
  };

  const onToggle = (isOpen: boolean): void => {
    setIsKebabOpen(isOpen);
  };

  const dropDownList = useMemo(() => {
    const result: JSX.Element[] = [];

    if (workflowInstance.state === WorkflowInstanceState.Error) {
      result.push(
        <DropdownItem key={"Retry"} onClick={() => onRetryClick(workflowInstance)}>
          Retry
        </DropdownItem>
      );
      result.push(
        <DropdownItem key={"Skip"} onClick={() => onSkipClick(workflowInstance)}>
          Skip
        </DropdownItem>
      );
    }

    if (onOpenTriggerCloudEvent) {
      result.push(
        <DropdownItem key={"CloudEvent"} onClick={() => onOpenTriggerCloudEvent(workflowInstance)}>
          Send Cloud Event
        </DropdownItem>
      );
    }
    result.push(
      <DropdownItem key={"Abort"} onClick={() => onAbortClick(workflowInstance)}>
        Abort
      </DropdownItem>
    );

    return result;
  }, [workflowInstance, onSkipClick, onRetryClick, onAbortClick, onOpenTriggerCloudEvent]);

  return (
    <Dropdown
      onSelect={onSelect}
      toggle={
        <KebabToggle isDisabled={checkWorkflowInstanceState(workflowInstance)} onToggle={onToggle} id="kebab-toggle" />
      }
      isOpen={isKebabOpen}
      isPlain
      position="right"
      aria-label="workflow instance actions dropdown"
      aria-labelledby="workflow instance actions dropdown"
      dropdownItems={dropDownList}
      {...componentOuiaProps(ouiaId, "workflow-list-actions-kebab", ouiaSafe)}
    />
  );
};

export default WorkflowListActionsKebab;
