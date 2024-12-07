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

import * as React from "react";
import { useCallback, useMemo } from "react";
import { PingPongApi, PingPongChannelApi, PingPongEnvelopeApi } from "../api";
import { EnvelopeServer } from "@kie-tools-core/envelope-bus/dist/channel";
import { EmbeddedEnvelopeProps, RefForwardingEmbeddedEnvelope } from "@kie-tools-core/envelope/dist/embedded";
import { ContainerType } from "@kie-tools-core/envelope/dist/api";

export type EmbeddedIFramePingPongProps = {
  apiImpl: PingPongChannelApi;
  targetOrigin: string;
  name: string;
  envelopePath: string;
};

export const EmbeddedIFramePingPong = React.forwardRef(
  (props: EmbeddedIFramePingPongProps, forwardedRef: React.Ref<PingPongApi>) => {
    const refDelegate = useCallback(
      (envelopeServer: EnvelopeServer<PingPongChannelApi, PingPongEnvelopeApi>): PingPongApi => ({
        clearLogs: () => envelopeServer.envelopeApi.requests.pingPongView__clearLogs(),
        getLastPingTimestamp: () => envelopeServer.envelopeApi.requests.pingPongView__getLastPingTimestamp(),
      }),
      []
    );

    const pollInit = useCallback(
      (envelopeServer: EnvelopeServer<PingPongChannelApi, PingPongEnvelopeApi>, container: () => HTMLIFrameElement) => {
        return envelopeServer.envelopeApi.requests.pingPongView__init(
          { origin: envelopeServer.origin, envelopeServerId: envelopeServer.id },
          { name: props.name }
        );
      },
      [props.name]
    );

    const config = useMemo(
      () => ({
        containerType: ContainerType.IFRAME,
        envelopePath: props.envelopePath,
      }),
      [props.envelopePath]
    );

    return (
      <EmbeddedIframePingPongEnvelope
        ref={forwardedRef}
        apiImpl={props.apiImpl}
        origin={props.targetOrigin}
        refDelegate={refDelegate}
        pollInit={pollInit}
        config={config}
      />
    );
  }
);

const EmbeddedIframePingPongEnvelope = React.forwardRef<
  PingPongApi,
  EmbeddedEnvelopeProps<PingPongChannelApi, PingPongEnvelopeApi, PingPongApi>
>(RefForwardingEmbeddedEnvelope);
