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
import React, { useCallback } from "react";
import { EnvelopeServer } from "@kie-tools-core/envelope-bus/dist/channel";
import { EmbeddedEnvelopeProps, RefForwardingEmbeddedEnvelope } from "@kie-tools-core/envelope/dist/embedded";
import { ContainerType } from "@kie-tools-core/envelope/dist/api";
import { init } from "../envelope";
import { JobsManagementApi, JobsManagementChannelApi, JobsManagementEnvelopeApi, JobsManagementDriver } from "../api";
import { JobsManagementChannelApiImpl } from "./JobsManagementChannelApiImpl";

export interface Props {
  targetOrigin: string;
  driver: JobsManagementDriver;
}

export const EmbeddedJobsManagement = React.forwardRef((props: Props, forwardedRef: React.Ref<JobsManagementApi>) => {
  const refDelegate = useCallback(
    (envelopeServer: EnvelopeServer<JobsManagementChannelApi, JobsManagementEnvelopeApi>): JobsManagementApi => ({}),
    []
  );
  const pollInit = useCallback(
    (
      envelopeServer: EnvelopeServer<JobsManagementChannelApi, JobsManagementEnvelopeApi>,
      container: () => HTMLDivElement
    ) => {
      init({
        config: {
          containerType: ContainerType.DIV,
          envelopeId: envelopeServer.id,
        },
        container: container(),
        bus: {
          postMessage(message, targetOrigin, transfer) {
            window.postMessage(message, targetOrigin!, transfer);
          },
        },
      });

      return envelopeServer.envelopeApi.requests.jobsManagement__init({
        origin: envelopeServer.origin,
        envelopeServerId: envelopeServer.id,
      });
    },
    []
  );

  return (
    <EmbeddedJobsManagementEnvelope
      ref={forwardedRef}
      apiImpl={new JobsManagementChannelApiImpl(props.driver)}
      origin={props.targetOrigin}
      refDelegate={refDelegate}
      pollInit={pollInit}
      config={{ containerType: ContainerType.DIV }}
    />
  );
});

const EmbeddedJobsManagementEnvelope = React.forwardRef<
  JobsManagementApi,
  EmbeddedEnvelopeProps<JobsManagementChannelApi, JobsManagementEnvelopeApi, JobsManagementApi>
>(RefForwardingEmbeddedEnvelope);
