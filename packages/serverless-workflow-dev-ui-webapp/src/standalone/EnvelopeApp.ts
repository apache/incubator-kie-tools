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
import { Envelope } from "@kie-tools-core/envelope";
import { RuntimeToolsDevUIEnvelopeApi, RuntimeToolsDevUIChannelApi } from "../api";
import {
  RuntimeToolsDevUIEnvelopeViewApi,
  RuntimeToolsDevUIEnvelopeContextType,
  RuntimeToolsDevUIEnvelope,
  RuntimeToolsDevUIEnvelopeApiImpl,
} from "../envelope";

const initEnvelope = () => {
  const container = document.getElementById("envelope-app")!;

  const bus = {
    postMessage: (message: any, targetOrigin: string, _: any) => window.parent.postMessage(message, targetOrigin, _),
  };

  const apiImplFactory = {
    create: (args: any) => new RuntimeToolsDevUIEnvelopeApiImpl(args),
  };

  const envelope = new Envelope<
    RuntimeToolsDevUIEnvelopeApi,
    RuntimeToolsDevUIChannelApi,
    RuntimeToolsDevUIEnvelopeViewApi,
    RuntimeToolsDevUIEnvelopeContextType
  >(bus);

  const runtimeToolsDevUIEnvelope = new RuntimeToolsDevUIEnvelope(envelope, apiImplFactory);

  runtimeToolsDevUIEnvelope.start(container);
};

// Envelope should be initialized only after page was loaded.
if (document.readyState !== "loading") {
  initEnvelope();
} else {
  document.addEventListener("DOMContentLoaded", () => {
    initEnvelope();
  });
}
