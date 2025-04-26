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
import React, { useCallback, useMemo } from "react";
import { EnvelopeServer } from "@kie-tools-core/envelope-bus/dist/channel";
import { EmbeddedEnvelopeProps, RefForwardingEmbeddedEnvelope } from "@kie-tools-core/envelope/dist/embedded";
import {
  ProcessDefinitionsListApi,
  ProcessDefinitionsListChannelApi,
  ProcessDefinitionsListEnvelopeApi,
  ProcessDefinitionsListState,
} from "../api";
import { ContainerType } from "@kie-tools-core/envelope/dist/api";
import { init } from "../envelope";

export interface Props {
  targetOrigin: string;
  channelApi: ProcessDefinitionsListChannelApi;
  initialState: ProcessDefinitionsListState;
}

export const EmbeddedProcessDefinitionsList = React.forwardRef(
  (props: Props, forwardedRef: React.Ref<ProcessDefinitionsListApi>) => {
    const refDelegate = useCallback(
      (
        envelopeServer: EnvelopeServer<ProcessDefinitionsListChannelApi, ProcessDefinitionsListEnvelopeApi>
      ): ProcessDefinitionsListApi => ({}),
      []
    );
    const pollInit = useCallback(
      async (
        envelopeServer: EnvelopeServer<ProcessDefinitionsListChannelApi, ProcessDefinitionsListEnvelopeApi>,
        container: () => HTMLDivElement
      ) => {
        await init({
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
        return envelopeServer.envelopeApi.requests.processDefinitionsList__init(
          {
            origin: envelopeServer.origin,
            envelopeServerId: envelopeServer.id,
          },
          {
            initialState: { ...props.initialState },
          }
        );
      },
      [props.initialState]
    );

    return (
      <EmbeddedProcessDefinitionsListEnvelope
        ref={forwardedRef}
        apiImpl={props.channelApi}
        origin={props.targetOrigin}
        refDelegate={refDelegate}
        pollInit={pollInit}
        config={{ containerType: ContainerType.DIV }}
      />
    );
  }
);

const EmbeddedProcessDefinitionsListEnvelope = React.forwardRef<
  ProcessDefinitionsListApi,
  EmbeddedEnvelopeProps<ProcessDefinitionsListChannelApi, ProcessDefinitionsListEnvelopeApi, ProcessDefinitionsListApi>
>(RefForwardingEmbeddedEnvelope);
