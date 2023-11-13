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

import { ApiDefinition, EnvelopeBusMessage } from "@kie-tools-core/envelope-bus/dist/api";
import { EnvelopeServer, EnvelopeServerType } from "@kie-tools-core/envelope-bus/dist/channel";
import * as React from "react";
import { useImperativeHandle, useMemo, useRef } from "react";
import { useConnectedEnvelopeServer } from "@kie-tools-core/envelope-bus/dist/hooks";
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

export interface EmbeddedEnvelopeProps<
  ApiToProvide extends ApiDefinition<ApiToProvide>,
  ApiToConsume extends ApiDefinition<ApiToConsume>,
  Ref
> {
  refDelegate: (envelopeServer: EnvelopeServer<ApiToProvide, ApiToConsume>) => Ref;
  apiImpl: ApiToProvide;
  origin: string;
  config: EnvelopeDivConfig | EnvelopeIFrameConfig;
  pollInit: (
    envelopeServer: EnvelopeServer<ApiToProvide, ApiToConsume>,
    container: () => HTMLDivElement | HTMLIFrameElement
  ) => Promise<any>;
}

export function RefForwardingEmbeddedEnvelope<
  ApiToProvide extends ApiDefinition<ApiToProvide>,
  ApiToConsume extends ApiDefinition<ApiToConsume>,
  Ref
>(props: EmbeddedEnvelopeProps<ApiToProvide, ApiToConsume, Ref>, forwardRef: React.RefObject<Ref>) {
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
    [props.config.containerType]
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
    [bus, props.origin, props.pollInit, props.config.containerType]
  );

  useImperativeHandle(
    forwardRef,
    () => {
      return props.refDelegate(envelopeServer);
    },
    [envelopeServer, props.refDelegate]
  );

  useConnectedEnvelopeServer<ApiToProvide>(envelopeServer, props.apiImpl);

  if (props.config.containerType === ContainerType.DIV) {
    return <div ref={divRef} />;
  }

  return <iframe ref={iframeRef} src={props.config.envelopePath} style={containerStyles} title="X" />;
}

export const EmbeddedEnvelope = React.forwardRef(RefForwardingEmbeddedEnvelope);
