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
import { UndoIcon } from "@patternfly/react-icons/dist/js/icons/undo-icon";
import { ClockIcon } from "@patternfly/react-icons/dist/js/icons/clock-icon";
import { ErrorCircleOIcon } from "@patternfly/react-icons/dist/js/icons/error-circle-o-icon";
import { BanIcon } from "@patternfly/react-icons/dist/js/icons/ban-icon";
import { CheckCircleIcon } from "@patternfly/react-icons/dist/js/icons/check-circle-icon";
import { JobStatus } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";

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
