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
import { ProcessListApi, ProcessListChannelApi, ProcessListEnvelopeApi, ProcessListDriver } from "../api";
import { ProcessListChannelApiImpl } from "./ProcessListChannelApiImpl";
import { ContainerType } from "@kie-tools-core/envelope/dist/api";
import { init } from "../envelope";
import { ProcessListState } from "@kie-tools/runtime-tools-process-gateway-api/dist/types";

export interface Props {
  targetOrigin: string;
  driver: ProcessListDriver;
  initialState: ProcessListState;
  singularProcessLabel: string;
  pluralProcessLabel: string;
}

export const EmbeddedProcessList = React.forwardRef((props: Props, forwardedRef: React.Ref<ProcessListApi>) => {
  const refDelegate = useCallback(
    (envelopeServer: EnvelopeServer<ProcessListChannelApi, ProcessListEnvelopeApi>): ProcessListApi => ({}),
    []
  );
  const pollInit = useCallback(
    async (
      envelopeServer: EnvelopeServer<ProcessListChannelApi, ProcessListEnvelopeApi>,
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
      return envelopeServer.envelopeApi.requests.processList__init(
        {
          origin: envelopeServer.origin,
          envelopeServerId: envelopeServer.id,
        },
        {
          initialState: { ...props.initialState },
          singularProcessLabel: props.singularProcessLabel,
          pluralProcessLabel: props.pluralProcessLabel,
        }
      );
    },
    [props.initialState, props.pluralProcessLabel, props.singularProcessLabel]
  );

  const apiImpl = useMemo(() => {
    return new ProcessListChannelApiImpl(props.driver);
  }, [props.driver]);

  return (
    <EmbeddedProcessListEnvelope
      ref={forwardedRef}
      apiImpl={apiImpl}
      origin={props.targetOrigin}
      refDelegate={refDelegate}
      pollInit={pollInit}
      config={{ containerType: ContainerType.DIV }}
    />
  );
});

const EmbeddedProcessListEnvelope = React.forwardRef<
  ProcessListApi,
  EmbeddedEnvelopeProps<ProcessListChannelApi, ProcessListEnvelopeApi, ProcessListApi>
>(RefForwardingEmbeddedEnvelope);
