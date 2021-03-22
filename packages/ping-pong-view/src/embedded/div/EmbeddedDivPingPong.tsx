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
import { EnvelopeServer } from "@kogito-tooling/envelope-bus/dist/channel";
import { EmbeddedEnvelopeFactory } from "@kogito-tooling/envelope/dist/embedded";
import { ContainerType } from "@kogito-tooling/envelope/dist/api";
import { init } from "../../envelope";
import { EnvelopeBusMessage } from "@kogito-tooling/envelope-bus/dist/api";
import { PingPongReactImplFactory } from "ping-pong-view-react";

export type Props = PingPongChannelApi & {
  mapping: {
    title: string;
  };
  targetOrigin: string;
  name: string;
};

export const EmbeddedDivPingPong = React.forwardRef((props: Props, forwardedRef: React.Ref<PingPongApi>) => {
  const refDelegate = useCallback((envelopeServer): PingPongApi => ({}), []);

  const pollInit = useCallback(
    (
      envelopeServer: EnvelopeServer<PingPongChannelApi, PingPongEnvelopeApi>,
      container: () => HTMLDivElement | HTMLIFrameElement
    ) => {
      init({
        envelopeConfig: { containerType: ContainerType.DIV, envelopeId: envelopeServer.id },
        container: container(),
        bus: {
          postMessage<D, Type>(message: EnvelopeBusMessage<D, Type>, targetOrigin?: string, transfer?: any) {
            window.postMessage(message, "*", transfer);
          },
        },
        pingPongViewFactory: new PingPongReactImplFactory(),
      });

      return envelopeServer.envelopeApi.requests.pingPongView__init(
        { origin: envelopeServer.origin, envelopeServerId: envelopeServer.id },
        { name: props.name }
      );
    },
    []
  );

  const EmbeddedEnvelope = useMemo(() => {
    return EmbeddedEnvelopeFactory({
      api: props,
      origin: props.targetOrigin,
      refDelegate,
      pollInit,
      config: { containerType: ContainerType.DIV },
    });
  }, []);

  return <EmbeddedEnvelope ref={forwardedRef} />;
});
