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

import { PingPong } from "./PingPong";
import * as React from "react";
import { useImperativeHandle, useState } from "react";
import "./styles.scss";

export interface PingPongEnvelopeViewApi {
  setView(page: PingPong): Promise<void>;
}

export const PingPongEnvelopeView = React.forwardRef((props, forwardedRef) => {
  const [view, setView] = useState<PingPong>();

  useImperativeHandle(forwardedRef, () => ({ setView: setView }), []);

  return (
    <div className={"ping-pong-view--main"}>
      {view && (
        <>
          <h2>This is an implementation of Ping-Pong View</h2>

          <p className={"ping-pong-view--p-iframe"}> The {"<iframe>"} border is green </p>
          <p className={"ping-pong-view--p-ping-pong"}> The Ping-Pong View implementation border is red </p>

          <div id={"ping-pong-view-container"} className={"ping-pong-view-container"}>
            {view?.reactComponent?.()}
          </div>
        </>
      )}
    </div>
  );
});
