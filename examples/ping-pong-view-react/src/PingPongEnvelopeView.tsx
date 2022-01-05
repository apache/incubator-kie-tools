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
import * as ReactDOM from "react-dom";
import { useEffect, useState } from "react";
import * as PingPongViewEnvelope from "@kogito-tooling-examples/ping-pong-view/dist/envelope";
import { ContainerType } from "@kie-tooling-core/envelope/dist/api";
import { PingPongReactImplFactory } from ".";
import "./styles.css";
import { EnvelopeDivConfig, EnvelopeIFrameConfig } from "@kie-tooling-core/envelope";

export const pingPongEnvelopViewRender = (
  container: HTMLElement,
  containerType: ContainerType,
  envelopeId?: string
) => {
  return new Promise<void>((res) => {
    let config: EnvelopeDivConfig | EnvelopeIFrameConfig;
    if (containerType === ContainerType.IFRAME) {
      config = { containerType: ContainerType.IFRAME };
    } else {
      if (!envelopeId) {
        throw new Error("Need to specify envelopeId for container type DIV");
      }
      config = { containerType: ContainerType.DIV, envelopeId };
    }
    ReactDOM.render(<PingPongEnvelopeView envelopeConfig={config} />, container, () => res());
  });
};

export const PingPongEnvelopeView = (props: { envelopeConfig: EnvelopeDivConfig | EnvelopeIFrameConfig }) => {
  const [view, setView] = useState<React.ReactElement>();

  useEffect(() => {
    PingPongViewEnvelope.init({
      config: props.envelopeConfig,
      bus: { postMessage: (message, _targetOrigin, transfer) => window.parent.postMessage(message, "*", transfer) },
      pingPongViewFactory: new PingPongReactImplFactory(setView),
      viewReady: () => Promise.resolve(() => {}),
    });
  }, [props.envelopeConfig]);

  return (
    <div className={"ping-pong-view--main"}>
      <h2>This is an implementation of Ping-Pong View</h2>

      <p className={"ping-pong-view--p-iframe"}>
        {" "}
        The envelope boundary border is green. It can be an iframe or a div.
      </p>
      <p className={"ping-pong-view--p-ping-pong"}> The Ping-Pong View implementation border is red </p>

      <div id={"ping-pong-view-container"} className={"ping-pong-view--container"}>
        {view}
      </div>
    </div>
  );
};
