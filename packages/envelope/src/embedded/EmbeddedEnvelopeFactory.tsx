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

import { ApiDefinition, EnvelopeBusMessage } from "@kie-tooling-core/envelope-bus/dist/api";
import { EnvelopeServer, EnvelopeServerType } from "@kie-tooling-core/envelope-bus/dist/channel";
import * as React from "react";
import { useImperativeHandle, useMemo, useRef } from "react";
import { useConnectedEnvelopeServer } from "@kie-tooling-core/envelope-bus/dist/hooks";
import type * as CSS from "csstype";
import { ContainerType } from "../api";

const containerStyles: CSS.Properties = {
  display: "flex",
  flex: 1,
  flexDirection: "column",
  width: "100%",
  height: "100%",
  border: "none",
  margin: 0,
  padding: 0,
  overflow: "hidden",
};

export interface EnvelopeDivConfig {
  containerType: ContainerType.DIV;
}

export interface EnvelopeIFrameConfig {
  containerType: ContainerType.IFRAME;
  envelopePath: string;
}

export interface Props<
  ApiToProvide extends ApiDefinition<ApiToProvide>,
  ApiToConsume extends ApiDefinition<ApiToConsume>,
  Ref
> {
  refDelegate: (envelopeServer: EnvelopeServer<ApiToProvide, ApiToConsume>) => Ref;
  api: ApiToProvide;
  origin: string;
  pollInit: (
    envelopeServer: EnvelopeServer<ApiToProvide, ApiToConsume>,
    container: () => HTMLDivElement | HTMLIFrameElement
  ) => Promise<any>;
  config: EnvelopeDivConfig | EnvelopeIFrameConfig;
}

export function EmbeddedEnvelopeFactory<
  ApiToProvide extends ApiDefinition<ApiToProvide>,
  ApiToConsume extends ApiDefinition<ApiToConsume>,
  Ref
>(props: Props<ApiToProvide, ApiToConsume, Ref>) {
  return React.forwardRef((_, forwardRef) => {
    const iframeRef = useRef<HTMLIFrameElement>(null);
    const divRef = useRef<HTMLDivElement>(null);

    const bus = useMemo(
      () => ({
        postMessage<D, T>(message: EnvelopeBusMessage<D, T>) {
          if (props.config.containerType === ContainerType.DIV) {
            window.postMessage(message, "*");
          } else {
            iframeRef.current?.contentWindow?.postMessage(message, "*");
          }
        },
      }),
      []
    );

    const envelopeServer = useMemo(
      () =>
        new EnvelopeServer<ApiToProvide, ApiToConsume>(
          bus,
          props.origin,
          (self) =>
            props.pollInit(self, () =>
              props.config.containerType === ContainerType.DIV ? divRef.current! : iframeRef.current!
            ),
          props.config.containerType === ContainerType.DIV ? EnvelopeServerType.LOCAL : EnvelopeServerType.REMOTE
        ),
      [bus, props.origin, props.pollInit]
    );

    useImperativeHandle(
      forwardRef,
      () => {
        return props.refDelegate(envelopeServer);
      },
      [props.refDelegate]
    );

    useConnectedEnvelopeServer<ApiToProvide>(envelopeServer, props.api);

    if (props.config.containerType === ContainerType.DIV) {
      return <div ref={divRef} />;
    }

    return <iframe ref={iframeRef} src={props.config.envelopePath} style={containerStyles} title="X" />;
  });
}
