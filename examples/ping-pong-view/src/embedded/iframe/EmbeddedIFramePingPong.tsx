/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import { EmbeddedEnvelopeFactory } from "@kie-tooling-core/envelope/dist/embedded";
import { ContainerType } from "@kie-tooling-core/envelope/dist/api";

export type Props = PingPongChannelApi & {
  mapping: {
    title: string;
    envelopePath: string;
  };
  targetOrigin: string;
  name: string;
};

export const EmbeddedIFramePingPong = React.forwardRef((props: Props, forwardedRef: React.Ref<PingPongApi>) => {
  const refDelegate = useCallback((envelopeServer): PingPongApi => ({}), []);

  const pollInit = useCallback(
    (
      envelopeServer: EnvelopeServer<PingPongChannelApi, PingPongEnvelopeApi>,
      container: () => HTMLDivElement | HTMLIFrameElement
    ) => {
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
      config: { containerType: ContainerType.IFRAME, envelopePath: props.mapping.envelopePath },
    });
  }, []);

  return <EmbeddedEnvelope ref={forwardedRef} />;
});
