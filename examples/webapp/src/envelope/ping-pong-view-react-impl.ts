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

import * as PingPongViewEnvelope from "@kogito-tooling-examples/ping-pong-view/dist/envelope";
import { PingPongReactImplFactory } from "@kogito-tooling-examples/ping-pong-view-react";
import { ContainerType } from "@kie-tooling-core/envelope/dist/api";

PingPongViewEnvelope.init({
  container: document.getElementById("envelope-app")!,
  config: { containerType: ContainerType.IFRAME },
  bus: { postMessage: (message, targetOrigin, transfer) => window.parent.postMessage(message, "*", transfer) },
  pingPongViewFactory: new PingPongReactImplFactory(),
});
