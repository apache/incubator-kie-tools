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

import {
  ApiDefinition,
  EnvelopeBus,
  EnvelopeBusMessage,
  EnvelopeBusMessageDirectSender,
  EnvelopeBusMessagePurpose,
  FunctionPropertyNames,
} from "../api";
import { EnvelopeBusMessageManager } from "../common";

export enum EnvelopeServerType {
  LOCAL = "local",
  REMOTE = "remote",
}

export class EnvelopeServer<
  ApiToProvide extends ApiDefinition<ApiToProvide>,
  ApiToConsume extends ApiDefinition<ApiToConsume>
> {
  public static INIT_POLLING_TIMEOUT_IN_MS = 60000;
  public static INIT_POLLING_INTERVAL_IN_MS = 100;

  public initPolling?: ReturnType<typeof setInterval>;
  public initPollingTimeout?: ReturnType<typeof setTimeout>;
  public initialPollingSetting?: ReturnType<typeof setTimeout>;

  public readonly id: string;

  public get envelopeApi() {
    return this.manager.clientApi;
  }

  public get shared() {
    return this.manager.shared;
  }

  constructor(
    bus: EnvelopeBus,
    public readonly origin: string,
    public readonly pollInit: (self: EnvelopeServer<ApiToProvide, ApiToConsume>) => Promise<any>,
    public readonly type: EnvelopeServerType = EnvelopeServerType.REMOTE,
    public readonly manager = new EnvelopeBusMessageManager<ApiToProvide, ApiToConsume>(
      (message) =>
        bus.postMessage({
          ...message,
          targetEnvelopeId: type === EnvelopeServerType.LOCAL ? this.id : undefined,
          directSender: EnvelopeBusMessageDirectSender.ENVELOPE_SERVER,
        }),
      "EnvelopeServer"
    )
  ) {
    this.id = this.generateRandomId();
  }

  public startInitPolling(apiImpl: ApiToProvide) {
    // We can't wait for the setInterval to run, because messages can be sent during the current event-loop pass,
    // making the Envelope reply a message to an old EnvelopeServer instance.
    this.pollInit(this).then(() => {
      this.stopInitPolling();
    });

    this.manager.currentApiImpl = apiImpl;

    // Set intervals and timeout only after first poll.
    this.initialPollingSetting = setTimeout(() => {
      this.initPolling = setInterval(() => {
        this.pollInit(this).then(() => this.stopInitPolling());
      }, EnvelopeServer.INIT_POLLING_INTERVAL_IN_MS);

      this.initPollingTimeout = setTimeout(() => {
        this.stopInitPolling();
        console.info("Init polling timed out. Looks like the Envelope is not responding accordingly.");
      }, EnvelopeServer.INIT_POLLING_TIMEOUT_IN_MS);
    }, EnvelopeServer.INIT_POLLING_INTERVAL_IN_MS);
  }

  public stopInitPolling() {
    clearTimeout(this.initialPollingSetting!);
    this.initialPollingSetting = undefined;
    this.manager.currentApiImpl = undefined;
    clearInterval(this.initPolling!);
    this.initPolling = undefined;
    clearTimeout(this.initPollingTimeout!);
    this.initPollingTimeout = undefined;
  }

  public receive(
    message: EnvelopeBusMessage<unknown, FunctionPropertyNames<ApiToProvide> | FunctionPropertyNames<ApiToConsume>>,
    apiImpl: ApiToProvide
  ) {
    if (message.directSender === EnvelopeBusMessageDirectSender.ENVELOPE_SERVER) {
      // When a message came from another EnvelopeServer, it should be ignored
      return;
    }

    if (message.targetEnvelopeId) {
      // When the message has a targetEnvelopeId, it was directed to a specific envelope,
      // thus the channel should ignore it.
      return;
    }

    if (message.targetEnvelopeServerId === this.id) {
      // Message was sent directly from the Envelope to this EnvelopeServer
      this.manager.server.receive(message, apiImpl);
    } else if (message.purpose === EnvelopeBusMessagePurpose.NOTIFICATION) {
      // Message was sent from any Envelope to some EnvelopeServer, so it should be forwarded to this Envelope
      this.manager.server.receive(message, {} as any);
    }
  }

  public generateRandomId() {
    const randomPart = Math.random().toString(36).substr(2, 9);

    const milliseconds = new Date().getMilliseconds();

    return `_${randomPart}_${milliseconds}`;
  }
}
