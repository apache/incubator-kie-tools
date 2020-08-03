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

import { ApiDefinition, EnvelopeBusMessage } from "@kogito-tooling/envelope-bus/dist/api";
import { ChannelEnvelopeServer } from "@kogito-tooling/envelope-bus/dist/channel";
import * as React from "react";
import { useImperativeHandle, useMemo, useRef } from "react";
import { useConnectedEnvelopeServer } from "@kogito-tooling/envelope-bus/dist/hooks";

export interface Props<
  ApiToProvide extends ApiDefinition<ApiToProvide>,
  ApiToConsume extends ApiDefinition<ApiToConsume>,
  T
> {
  refDelegate: (envelopeServer: ChannelEnvelopeServer<ApiToProvide, ApiToConsume>) => T;
  forwardedRef: React.Ref<T>;
  api: ApiToProvide;
  envelopePath: string;
  origin: string;
  pollInit: (envelopeServer: ChannelEnvelopeServer<ApiToProvide, ApiToConsume>) => Promise<any>;
}

export function EmbeddedEnvelope<
  ApiToProvide extends ApiDefinition<ApiToProvide>,
  ApiToConsume extends ApiDefinition<ApiToConsume>,
  Ref
>(props: Props<ApiToProvide, ApiToConsume, Ref>, forwardedRef: React.Ref<Ref>) {
  const iframeRef = useRef<HTMLIFrameElement>(null);

  const bus = useMemo(
    () => ({
      postMessage<D, T>(message: EnvelopeBusMessage<D, T>) {
        iframeRef.current?.contentWindow?.postMessage(message, "*");
      }
    }),
    []
  );

  const envelopeServer = useMemo(
    () => new ChannelEnvelopeServer<ApiToProvide, ApiToConsume>(bus, props.origin, self => props.pollInit(self)),
    [bus, props.origin, props.pollInit]
  );

  useImperativeHandle(
    forwardedRef,
    () => {
      return props.refDelegate(envelopeServer);
    },
    [props.refDelegate]
  );

  useConnectedEnvelopeServer<ApiToProvide>(envelopeServer, props.api);

  return <iframe ref={iframeRef} src={props.envelopePath} title="X" />;
}
