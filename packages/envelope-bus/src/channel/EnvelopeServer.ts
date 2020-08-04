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

import {
  ApiDefinition,
  EnvelopeBus,
  EnvelopeBusMessage,
  EnvelopeBusMessagePurpose,
  FunctionPropertyNames
} from "../api";
import { EnvelopeBusMessageManager } from "../common";

export class EnvelopeServer<
  ApiToProvide extends ApiDefinition<ApiToProvide>,
  ApiToConsume extends ApiDefinition<ApiToConsume>
> {
  public static INIT_POLLING_TIMEOUT_IN_MS = 10000;
  public static INIT_POLLING_INTERVAL_IN_MS = 100;

  public initPolling?: ReturnType<typeof setInterval>;
  public initPollingTimeout?: ReturnType<typeof setTimeout>;

  public readonly id: string;

  public get client() {
    return this.manager.client;
  }

  constructor(
    bus: EnvelopeBus,
    public readonly origin: string,
    public readonly pollInit: (self: EnvelopeServer<ApiToProvide, ApiToConsume>) => Promise<any>,
    private readonly manager = new EnvelopeBusMessageManager<ApiToProvide, ApiToConsume>(
      message => bus.postMessage(message),
      "EnvelopeServer"
    )
  ) {
    this.id = this.generateRandomId();
  }

  public startInitPolling() {
    this.initPolling = setInterval(() => {
      this.pollInit(this).then(() => this.stopInitPolling());
    }, EnvelopeServer.INIT_POLLING_INTERVAL_IN_MS);

    this.initPollingTimeout = setTimeout(() => {
      this.stopInitPolling();
      console.info("Init polling timed out. Looks like the Envelope is not responding accordingly.");
    }, EnvelopeServer.INIT_POLLING_TIMEOUT_IN_MS);
  }

  public stopInitPolling() {
    clearInterval(this.initPolling!);
    this.initPolling = undefined;
    clearTimeout(this.initPollingTimeout!);
    this.initPollingTimeout = undefined;
  }

  public receive(
    message: EnvelopeBusMessage<unknown, FunctionPropertyNames<ApiToProvide> | FunctionPropertyNames<ApiToConsume>>,
    api: ApiToProvide
  ) {
    if (message.envelopeServerId === this.id) {
      this.manager.server.receive(message, api);
    } else if (message.purpose === EnvelopeBusMessagePurpose.NOTIFICATION) {
      this.manager.server.receive(message, {} as any);
    }
  }

  public generateRandomId() {
    const randomPart = Math.random()
      .toString(36)
      .substr(2, 9);

    const milliseconds = new Date().getMilliseconds();

    return `_${randomPart}_${milliseconds}`;
  }
}
