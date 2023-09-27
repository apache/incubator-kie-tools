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
import { JobStatus, Job, WorkflowInstance, NodeInstance } from "@kie-tools/runtime-tools-gateway-api/dist/types";
import { setTitle } from "@kie-tools/runtime-tools-components/dist/utils/Utils";
import { ClockIcon } from "@patternfly/react-icons/dist/js/icons/clock-icon";
import { BanIcon } from "@patternfly/react-icons/dist/js/icons/ban-icon";
import { UndoIcon } from "@patternfly/react-icons/dist/js/icons/undo-icon";
import { ErrorCircleOIcon } from "@patternfly/react-icons/dist/js/icons/error-circle-o-icon";
import { CheckCircleIcon } from "@patternfly/react-icons/dist/js/icons/check-circle-icon";
import { WorkflowDetailsDriver } from "../api";

export const JobsIconCreator = (state: JobStatus): JSX.Element => {
  switch (state) {
    case JobStatus.Error:
      return (
        <>
          <ErrorCircleOIcon className="pf-u-mr-sm" color="var(--pf-global--danger-color--100)" />
          Error
        </>
      );
    case JobStatus.Canceled:
      return (
        <>
          <BanIcon className="pf-u-mr-sm" />
          Canceled
        </>
      );
    case JobStatus.Executed:
      return (
        <>
          <CheckCircleIcon className="pf-u-mr-sm" color="var(--pf-global--success-color--100)" />
          Executed
        </>
      );
    case JobStatus.Retry:
      return (
        <>
          <UndoIcon className="pf-u-mr-sm" />
          Retry
        </>
      );
    case JobStatus.Scheduled:
      return (
        <>
          <ClockIcon className="pf-u-mr-sm" />
          Scheduled
        </>
      );
  }
};

export const handleRetry = async (
  workflowInstance: WorkflowInstance,
  drive: WorkflowDetailsDriver,
  onRetrySuccess: () => void,
  onRetryFailure: (errorMessage: string) => void
) => {
  try {
    await drive.handleWorkflowRetry(workflowInstance);
    onRetrySuccess();
  } catch (error) {
    onRetryFailure(JSON.stringify(error.message));
  }
};

export const handleSkip = async (
  workflowInstance: WorkflowInstance,
  drive: WorkflowDetailsDriver,
  onSkipSuccess: () => void,
  onSkipFailure: (errorMessage: string) => void
) => {
  try {
    await drive.handleWorkflowSkip(workflowInstance);
    onSkipSuccess();
  } catch (error) {
    onSkipFailure(JSON.stringify(error.message));
  }
};

export const handleNodeInstanceRetrigger = (
  workflowInstance: WorkflowInstance,
  driver: WorkflowDetailsDriver,
  node: NodeInstance,
  onRetriggerSuccess: () => void,
  onRetriggerFailure: (errorMessage: string) => void
) => {
  driver
    .handleNodeInstanceRetrigger(workflowInstance, node)
    .then(() => {
      onRetriggerSuccess();
    })
    .catch((error: any) => {
      onRetriggerFailure(JSON.stringify(error.message));
    });
};

export const handleNodeInstanceCancel = (
  workflowInstance: WorkflowInstance,
  driver: WorkflowDetailsDriver,
  node: NodeInstance,
  onCancelSuccess: () => void,
  onCancelFailure: (errorMessage: string) => void
) => {
  driver
    .handleNodeInstanceCancel(workflowInstance, node)
    .then(() => {
      onCancelSuccess();
    })
    .catch((error: any) => {
      onCancelFailure(JSON.stringify(error.message));
    });
};

export const jobCancel = async (
  drive: WorkflowDetailsDriver,
  job: Pick<Job, "id" | "endpoint">,
  setModalTitle: (title: JSX.Element) => void,
  setModalContent: (content: string) => void
) => {
  const response = await drive.cancelJob(job);
  setModalTitle(setTitle(response.modalTitle, "Job cancel"));
  setModalContent(response.modalContent);
};

export const handleJobRescheduleUtil = async (
  repeatInterval: number | string,
  repeatLimit: number | string,
  scheduleDate: Date,
  selectedJob: Job,
  handleRescheduleAction: () => void,
  driver: WorkflowDetailsDriver,
  setRescheduleError: (modalContent: string) => void
): Promise<void> => {
  const response = await driver.rescheduleJob(selectedJob, repeatInterval, repeatLimit, scheduleDate);
  if (response && response.modalTitle === "success") {
    handleRescheduleAction();
  } else if (response && response.modalTitle === "failure") {
    handleRescheduleAction();
    setRescheduleError(response.modalContent);
  }
};
