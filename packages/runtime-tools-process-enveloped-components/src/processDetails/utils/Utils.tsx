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

import React from "react";
import { ClockIcon } from "@patternfly/react-icons/dist/js/icons/clock-icon";
import { BanIcon } from "@patternfly/react-icons/dist/js/icons/ban-icon";
import { UndoIcon } from "@patternfly/react-icons/dist/js/icons/undo-icon";
import { ErrorCircleOIcon } from "@patternfly/react-icons/dist/js/icons/error-circle-o-icon";
import { CheckCircleIcon } from "@patternfly/react-icons/dist/js/icons/check-circle-icon";
import { NodeInstance } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";
import { setTitle } from "@kie-tools/runtime-tools-components/dist/utils/Utils";
import { Job, JobStatus, ProcessInstance } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { ProcessDetailsChannelApi } from "../api";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";

export const JobsIconCreator = (state: JobStatus): JSX.Element => {
  switch (state) {
    case JobStatus.Error:
      return (
        <>
          <ErrorCircleOIcon className="pf-v5-u-mr-sm" color="var(--pf-v5-global--danger-color--100)" />
          Error
        </>
      );
    case JobStatus.Canceled:
      return (
        <>
          <BanIcon className="pf-v5-u-mr-sm" />
          Canceled
        </>
      );
    case JobStatus.Executed:
      return (
        <>
          <CheckCircleIcon className="pf-v5-u-mr-sm" color="var(--pf-v5-global--success-color--100)" />
          Executed
        </>
      );
    case JobStatus.Retry:
      return (
        <>
          <UndoIcon className="pf-v5-u-mr-sm" />
          Retry
        </>
      );
    case JobStatus.Scheduled:
      return (
        <>
          <ClockIcon className="pf-v5-u-mr-sm" />
          Scheduled
        </>
      );
  }
};

export const handleRetry = async (
  processInstance: ProcessInstance,
  channelApi: MessageBusClientApi<ProcessDetailsChannelApi>,
  onRetrySuccess: () => void,
  onRetryFailure: (errorMessage: string) => void
) => {
  try {
    await channelApi.requests.processDetails__handleProcessRetry(processInstance);
    onRetrySuccess();
  } catch (error) {
    onRetryFailure(JSON.stringify(error.message));
  }
};

export const handleSkip = async (
  processInstance: ProcessInstance,
  channelApi: MessageBusClientApi<ProcessDetailsChannelApi>,
  onSkipSuccess: () => void,
  onSkipFailure: (errorMessage: string) => void
) => {
  try {
    await channelApi.requests.processDetails__handleProcessSkip(processInstance);
    onSkipSuccess();
  } catch (error) {
    onSkipFailure(JSON.stringify(error.message));
  }
};

export const handleNodeInstanceRetrigger = (
  processInstance: ProcessInstance,
  channelApi: MessageBusClientApi<ProcessDetailsChannelApi>,
  node: NodeInstance,
  onRetriggerSuccess: () => void,
  onRetriggerFailure: (errorMessage: string) => void
) => {
  channelApi.requests
    .processDetails__handleNodeInstanceRetrigger(processInstance, node)
    .then(() => {
      onRetriggerSuccess();
    })
    .catch((error) => {
      onRetriggerFailure(JSON.stringify(error.message));
    });
};

export const handleNodeInstanceCancel = (
  processInstance: ProcessInstance,
  channelApi: MessageBusClientApi<ProcessDetailsChannelApi>,
  node: NodeInstance,
  onCancelSuccess: () => void,
  onCancelFailure: (errorMessage: string) => void
) => {
  channelApi.requests
    .processDetails__handleNodeInstanceCancel(processInstance, node)
    .then(() => {
      onCancelSuccess();
    })
    .catch((error) => {
      onCancelFailure(JSON.stringify(error.message));
    });
};

export const jobCancel = async (
  channelApi: MessageBusClientApi<ProcessDetailsChannelApi>,
  job: Job,
  setModalTitle: (title: JSX.Element) => void,
  setModalContent: (content: string) => void
) => {
  const response = await channelApi.requests.processDetails__cancelJob(job);
  setModalTitle(setTitle(response.modalTitle, "Job cancel"));
  setModalContent(response.modalContent);
};

export const handleJobRescheduleUtil = async (
  repeatInterval: string | number,
  repeatLimit: string | number,
  scheduleDate: Date,
  selectedJob: Job,
  handleRescheduleAction: () => void,
  channelApi: MessageBusClientApi<ProcessDetailsChannelApi>,
  setRescheduleError: (modalContent: string) => void
): Promise<void> => {
  const response = await channelApi.requests.processDetails__rescheduleJob(
    selectedJob,
    repeatInterval,
    repeatLimit,
    scheduleDate
  );
  if (response && response.modalTitle === "success") {
    handleRescheduleAction();
  } else if (response && response.modalTitle === "failure") {
    setRescheduleError(response.modalContent);
  }
};
