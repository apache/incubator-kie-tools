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
import React, { useCallback, useMemo, useState } from "react";
import { Dropdown, DropdownItem, KebabToggle } from "@patternfly/react-core/deprecated";
import { checkProcessInstanceState } from "../utils/ProcessListUtils";
import { ProcessInstance, ProcessInstanceState } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { OUIAProps, componentOuiaProps } from "@kie-tools/runtime-tools-components/dist/ouiaTools";

interface ProcessListActionsKebabProps {
  processInstance: ProcessInstance;
  onSkipClick: (processInstance: ProcessInstance) => Promise<void>;
  onRetryClick: (processInstance: ProcessInstance) => Promise<void>;
  onAbortClick: (processInstance: ProcessInstance) => Promise<void>;
  onOpenTriggerCloudEvent?: (processInstance: ProcessInstance) => void;
}

const ProcessListActionsKebab: React.FC<ProcessListActionsKebabProps & OUIAProps> = ({
  processInstance,
  onSkipClick,
  onRetryClick,
  onAbortClick,
  onOpenTriggerCloudEvent,
  ouiaId,
  ouiaSafe,
}) => {
  const [isKebabOpen, setIsKebabOpen] = useState<boolean>(false);

  const onSelect = useCallback((): void => {
    setIsKebabOpen(!isKebabOpen);
  }, [isKebabOpen]);

  const onToggle = useCallback((isOpen: boolean): void => {
    setIsKebabOpen(isOpen);
  }, []);

  const dropDownList = useMemo(() => {
    const result: JSX.Element[] = [];

    if (processInstance.state === ProcessInstanceState.Error) {
      result.push(
        <DropdownItem key={"Retry"} onClick={() => onRetryClick(processInstance)}>
          Retry
        </DropdownItem>
      );
      result.push(
        <DropdownItem key={"Skip"} onClick={() => onSkipClick(processInstance)}>
          Skip
        </DropdownItem>
      );
    }

    if (onOpenTriggerCloudEvent) {
      result.push(
        <DropdownItem key={"CloudEvent"} onClick={() => onOpenTriggerCloudEvent(processInstance)}>
          Send Cloud Event
        </DropdownItem>
      );
    }
    result.push(
      <DropdownItem key={"Abort"} onClick={() => onAbortClick(processInstance)}>
        Abort
      </DropdownItem>
    );

    return result;
  }, [processInstance, onSkipClick, onRetryClick, onAbortClick, onOpenTriggerCloudEvent]);

  return (
    <Dropdown
      onSelect={onSelect}
      toggle={
        <KebabToggle
          isDisabled={checkProcessInstanceState(processInstance)}
          onToggle={(_event, isOpen: boolean) => onToggle(isOpen)}
          data-testid="kebab-toggle"
          id="kebab-toggle"
        />
      }
      isOpen={isKebabOpen}
      isPlain
      position="right"
      aria-label="process instance actions dropdown"
      aria-labelledby="process instance actions dropdown"
      dropdownItems={dropDownList}
      {...componentOuiaProps(ouiaId, "process-list-actions-kebab", ouiaSafe)}
    />
  );
};

export default ProcessListActionsKebab;
