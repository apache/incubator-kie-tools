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
import React, { useMemo } from "react";
import { useImperativeHandle, useState } from "react";
import { MessageBusClientApi } from "@kie-tools-core/envelope-bus/dist/api";
import { JobsManagementChannelApi, JobsManagementInitArgs } from "../api";
import JobsManagement from "./components/JobsManagement/JobsManagement";
import JobsManagementEnvelopeViewDriver from "./JobsManagementEnvelopeViewDriver";
import "@patternfly/patternfly/patternfly.css";
import { JobsManagementState, JobStatus } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";
import { OrderBy } from "@kie-tools/runtime-tools-shared-gateway-api/dist/types";

export interface JobsManagementEnvelopeViewApi {
  initialize: (initArgs?: JobsManagementInitArgs) => void;
}
interface Props {
  channelApi: MessageBusClientApi<JobsManagementChannelApi>;
}

const defaultStatus = [JobStatus.Scheduled];

const defaultOrderBy = {
  lastUpdate: OrderBy.DESC,
};

export const JobsManagementEnvelopeView = React.forwardRef<JobsManagementEnvelopeViewApi, Props>(
  (props, forwardedRef) => {
    const [jobsManagementInitArgs, setJobsManagementInitArgs] = useState<JobsManagementInitArgs>({
      initialState: {
        filters: defaultStatus,
        orderBy: defaultOrderBy,
      } as JobsManagementState,
    });
    const [isEnvelopeConnectedToChannel, setEnvelopeConnectedToChannel] = useState<boolean>(false);
    const driver = useMemo(() => new JobsManagementEnvelopeViewDriver(props.channelApi), [props.channelApi]);
    useImperativeHandle(
      forwardedRef,
      () => ({
        initialize: (initArgs) => {
          setEnvelopeConnectedToChannel(false);
          setJobsManagementInitArgs(initArgs!);
          setEnvelopeConnectedToChannel(true);
        },
      }),
      []
    );

    return (
      <>
        <JobsManagement
          isEnvelopeConnectedToChannel={isEnvelopeConnectedToChannel}
          initialState={jobsManagementInitArgs.initialState}
          driver={driver}
        />
      </>
    );
  }
);

export default JobsManagementEnvelopeView;
