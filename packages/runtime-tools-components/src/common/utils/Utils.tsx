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
import { BanIcon } from "@patternfly/react-icons/dist/js/icons/ban-icon";
import { CheckCircleIcon } from "@patternfly/react-icons/dist/js/icons/check-circle-icon";
import { ErrorCircleOIcon } from "@patternfly/react-icons/dist/js/icons/error-circle-o-icon";
import { InfoCircleIcon } from "@patternfly/react-icons/dist/js/icons/info-circle-icon";
import { OnRunningIcon } from "@patternfly/react-icons/dist/js/icons/on-running-icon";
import { Title, TitleSizes } from "@patternfly/react-core/dist/js/components/Title";
import { BulkListItem } from "../components/BulkList/BulkList";
import { Job, WorkflowInstance, WorkflowInstanceState } from "@kie-tools/runtime-tools-swf-gateway-api/dist/types";
import PausedIcon from "@patternfly/react-icons/dist/js/icons/paused-icon";

export const setTitle = (titleStatus: string, titleText: string): JSX.Element => {
  let icon;

  switch (titleStatus) {
    case "success":
      icon = <InfoCircleIcon className="pf-u-mr-sm" color="var(--pf-global--info-color--100)" />;
      break;
    case "failure":
      icon = <InfoCircleIcon className="pf-u-mr-sm" color="var(--pf-global--danger-color--100)" />;
      break;
  }

  return (
    <Title headingLevel="h1" size={TitleSizes["2xl"]}>
      {icon}
      <span>{titleText}</span>
    </Title>
  );
};

// function adds new property to existing object
export const constructObject = (obj: any, path: any, val: any) => {
  const keys = path.split(",");
  const lastKey = keys.pop();
  // tslint:disable-next-line: no-shadowed-variable
  const lastObj = keys.reduce(
    // tslint:disable-next-line: no-shadowed-variable
    (_obj: any, key: any) => (_obj[key] = obj[key] || {}),
    obj
  );
  lastObj[lastKey] = val;
};

export const formatForBulkListJob = (jobsList: (Job & { errorMessage?: string })[]): BulkListItem[] => {
  const formattedItems: BulkListItem[] = [];
  jobsList.forEach((item: Job & { errorMessage?: string }) => {
    const formattedObj: BulkListItem = {
      id: item.id,
      name: item.workflowId,
      description: item.id,
      errorMessage: item.errorMessage ? item.errorMessage : undefined,
    };
    formattedItems.push(formattedObj);
  });
  return formattedItems;
};

export const getWorkflowInstanceDescription = (workflowInstance: WorkflowInstance) => {
  return {
    id: workflowInstance.id,
    name: workflowInstance.processName,
    description: workflowInstance.businessKey,
  };
};

/* tslint:disable:no-floating-promises */
export const WorkflowInstanceIconCreator = (state: WorkflowInstanceState): JSX.Element => {
  switch (state) {
    case WorkflowInstanceState.Active:
      return (
        <>
          <OnRunningIcon className="pf-u-mr-sm" />
          Active
        </>
      );
    case WorkflowInstanceState.Completed:
      return (
        <>
          <CheckCircleIcon className="pf-u-mr-sm" color="var(--pf-global--success-color--100)" />
          Completed
        </>
      );
    case WorkflowInstanceState.Aborted:
      return (
        <>
          <BanIcon className="pf-u-mr-sm" />
          Aborted
        </>
      );
    case WorkflowInstanceState.Suspended:
      return (
        <>
          <PausedIcon className="pf-u-mr-sm" />
          Suspended
        </>
      );
    case WorkflowInstanceState.Error:
      return (
        <>
          <ErrorCircleOIcon className="pf-u-mr-sm" color="var(--pf-global--danger-color--100)" />
          Error
        </>
      );
  }
};
