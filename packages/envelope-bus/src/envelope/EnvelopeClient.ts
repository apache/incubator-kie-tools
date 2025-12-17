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

export class EnvelopeClient<
  ApiToProvide extends ApiDefinition<ApiToProvide>,
  ApiToConsume extends ApiDefinition<ApiToConsume>,
> {
  public targetOrigin?: string;
  public associatedEnvelopeServerId?: string;
  public eventListener?: any;
  public readonly manager: EnvelopeBusMessageManager<ApiToProvide, ApiToConsume>;

  public get channelApi() {
    return this.manager.clientApi;
  }

  public get shared() {
    return this.manager.shared;
  }

  constructor(
    private readonly bus: EnvelopeBus,
    private readonly envelopeId?: string
  ) {
    this.manager = new EnvelopeBusMessageManager((message) => this.send(message), "KogitoEnvelopeBus");
  }

  public associate(origin: string, envelopeServerId: string) {
    this.targetOrigin = origin;
    this.associatedEnvelopeServerId = envelopeServerId;
  }

  public startListening(apiImpl: ApiToProvide) {
    if (this.eventListener) {
      return;
    }

    this.manager.currentApiImpl = apiImpl;
    this.eventListener = (event: any) => this.receive(event.data, apiImpl);
    window.addEventListener("message", this.eventListener);
  }

  public stopListening() {
    this.manager.currentApiImpl = undefined;
    window.removeEventListener("message", this.eventListener);
  }

  public send<T>(
    message: EnvelopeBusMessage<T, FunctionPropertyNames<ApiToProvide> | FunctionPropertyNames<ApiToConsume>>
  ) {
    if (!this.targetOrigin || !this.associatedEnvelopeServerId) {
      throw new Error("Tried to send message without associated Envelope Server set");
    }
    this.bus.postMessage(
      {
        ...message,
        targetEnvelopeServerId: this.associatedEnvelopeServerId,
        directSender: EnvelopeBusMessageDirectSender.ENVELOPE_CLIENT,
      },
      this.targetOrigin
    );
  }

  public receive(
    message: EnvelopeBusMessage<any, FunctionPropertyNames<ApiToProvide> | FunctionPropertyNames<ApiToConsume>>,
    apiImpl: ApiToProvide
  ) {
    if (message.directSender === EnvelopeBusMessageDirectSender.ENVELOPE_CLIENT) {
      // When a message came from another EnvelopeClient, it should be ignored
      return;
    }

    if (this.envelopeId !== message.targetEnvelopeId) {
      // The message should be ignored if it contains a different targetEnvelopeId.
      // Messages coming from REMOTE EnvelopeServers will have targetEnvelopeId equal to undefined, and
      // EnvelopeClient will have envelopeId equal to undefined when paired with REMOTE EnvelopeServers,
      // thus messages from REMOTE EnvelopeServers won't be ignored.
      return;
    }

    if (!message.targetEnvelopeServerId) {
      // Message was sent directly from the Channel to this Envelope
      this.manager.server.receive(message, apiImpl);
    } else if (message.targetEnvelopeServerId && message.purpose === EnvelopeBusMessagePurpose.NOTIFICATION) {
      // Message was redirected by the Channel from another Envelope
      this.manager.server.receive(message, {} as any);
    }
  }
}
