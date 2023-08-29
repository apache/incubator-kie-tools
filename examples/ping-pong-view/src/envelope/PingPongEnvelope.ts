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

import { EnvelopeBus } from "@kie-tools-core/envelope-bus/dist/api";
import { Envelope, EnvelopeDivConfig, EnvelopeIFrameConfig } from "@kie-tools-core/envelope";
import { PingPongChannelApi, PingPongEnvelopeApi } from "../api";
import { PingPongFactory } from "./PingPongFactory";
import { PingPongEnvelopeApiImpl } from "./PingPongEnvelopeApiImpl";

export type PingPongViewType = HTMLElement | void;

export function init(args: {
  config: EnvelopeDivConfig | EnvelopeIFrameConfig;
  bus: EnvelopeBus;
  pingPongViewFactory: PingPongFactory;
}) {
  const envelope = new Envelope<PingPongEnvelopeApi, PingPongChannelApi, PingPongViewType, {}>(args.bus, args.config);

  return envelope.start(
    () => Promise.resolve(() => {}),
    {},
    {
      create: (apiFactoryArgs) => new PingPongEnvelopeApiImpl(apiFactoryArgs, args.pingPongViewFactory),
    }
  );
}
