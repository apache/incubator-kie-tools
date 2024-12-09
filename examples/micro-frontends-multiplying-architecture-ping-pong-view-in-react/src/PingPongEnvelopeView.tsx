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
import { useEffect, useState } from "react";
import * as PingPongViewEnvelope from "@kie-tools-examples/micro-frontends-multiplying-architecture-ping-pong-view/dist/envelope";
import { PingPongReactImplFactory } from ".";
import "./styles.css";
import { EnvelopeDivConfig, EnvelopeIFrameConfig } from "@kie-tools-core/envelope";

export const PingPongEnvelopeView = (props: { envelopeConfig: EnvelopeDivConfig | EnvelopeIFrameConfig }) => {
  const [view, setView] = useState<React.ReactElement>();

  useEffect(() => {
    PingPongViewEnvelope.init({
      config: props.envelopeConfig,
      bus: { postMessage: (message, _targetOrigin, transfer) => window.parent.postMessage(message, "*", transfer) },
      pingPongViewFactory: new PingPongReactImplFactory(setView),
    });
  }, [props.envelopeConfig]);

  return (
    <div className={"ping-pong-view--main"}>
      <h2>This is an implementation of Ping-Pong View in React</h2>

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
