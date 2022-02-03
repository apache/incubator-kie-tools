/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { Association, PingPongChannelApi, PingPongEnvelopeApi, PingPongInitArgs, PingPongApi } from "../api";
import { EnvelopeApiFactoryArgs } from "@kie-tools-core/envelope";
import { PingPongFactory } from "./PingPongFactory";

export class PingPongEnvelopeApiImpl implements PingPongEnvelopeApi {
  constructor(
    private readonly args: EnvelopeApiFactoryArgs<PingPongEnvelopeApi, PingPongChannelApi, void, {}>,
    private readonly pingPongViewFactory: PingPongFactory
  ) {}

  pingPongApi?: () => PingPongApi | null;

  public async pingPongView__init(association: Association, initArgs: PingPongInitArgs) {
    this.args.envelopeClient.associate(association.origin, association.envelopeServerId);
    this.pingPongApi = this.pingPongViewFactory.create(initArgs, this.args.envelopeClient.manager.clientApi);
  }

  public async pingPongView__clearLogs() {
    this.pingPongApi?.()?.clearLogs();
  }

  public async pingPongView__getLastPingTimestamp() {
    const api = this.pingPongApi?.();
    if (!api) return Promise.resolve(0);

    return api.getLastPingTimestamp();
  }
}
