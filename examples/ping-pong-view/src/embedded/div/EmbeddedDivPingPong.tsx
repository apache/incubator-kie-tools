/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as React from "react";
import { useCallback, useMemo } from "react";
import { PingPongApi, PingPongChannelApi, PingPongEnvelopeApi } from "../../api";
import { EnvelopeServer } from "@kie-tooling-core/envelope-bus/dist/channel";
import { ContainerType } from "@kie-tooling-core/envelope/dist/api";
import * as PingPongEnvelope from "../../envelope";
import { PingPongFactory } from "../../envelope";
import { EmbeddedEnvelopeProps, RefForwardingEmbeddedEnvelope } from "@kie-tooling-core/envelope/dist/embedded";

export type Props = {
  mapping: {
    title: string;
  };
  apiImpl: PingPongChannelApi;
  targetOrigin: string;
  name: string;
  pingPongViewFactory: PingPongFactory;
};

export const EmbeddedDivPingPong = React.forwardRef((props: Props, forwardedRef: React.Ref<PingPongApi>) => {
  const refDelegate = useCallback((): PingPongApi => ({}), []);

  const pollInit = useCallback(
    (
      envelopeServer: EnvelopeServer<PingPongChannelApi, PingPongEnvelopeApi>,
      container: () => HTMLDivElement | HTMLIFrameElement
    ) => {
      PingPongEnvelope.init({
        config: { containerType: ContainerType.DIV, envelopeId: envelopeServer.id },
        container: container(),
        bus: { postMessage: (message, targetOrigin, transfer) => window.postMessage(message, "*", transfer) },
        pingPongViewFactory: props.pingPongViewFactory,
      });

      return envelopeServer.envelopeApi.requests.pingPongView__init(
        { origin: envelopeServer.origin, envelopeServerId: envelopeServer.id },
        { name: props.name }
      );
    },
    [props.name, props.pingPongViewFactory]
  );

  const config: { containerType: ContainerType.DIV } = useMemo(
    () => ({
      containerType: ContainerType.DIV,
    }),
    []
  );

  return (
    <EmbeddedDivPingPongEnvelope
      ref={forwardedRef}
      apiImpl={props.apiImpl}
      origin={props.targetOrigin}
      refDelegate={refDelegate}
      pollInit={pollInit}
      config={config}
    />
  );
});

const EmbeddedDivPingPongEnvelope =
  React.forwardRef<PingPongApi, EmbeddedEnvelopeProps<PingPongChannelApi, PingPongEnvelopeApi, PingPongApi>>(
    RefForwardingEmbeddedEnvelope
  );
