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

import { EnvelopeBusMessage, EnvelopeBusMessagePurpose } from "./EnvelopeBusMessage";
import { MessageTypesYouCanSendToTheEnvelope } from "./MessageTypesYouCanSendToTheEnvelope";
import { MessageTypesYouCanSendToTheChannel } from "./MessageTypesYouCanSendToTheChannel";

export class EnvelopeBusMessageManager<MessageType extends MessageTypesYouCanSendToTheEnvelope | MessageTypesYouCanSendToTheChannel> {
  private readonly callbacks = new Map<string, { resolve: (arg: unknown) => void; reject: (arg: unknown) => void }>();

  constructor(private readonly send: (message: EnvelopeBusMessage<unknown>) => void) {}

  public request<T>(type: MessageType, args: unknown): Promise<T> {
    const message = {
      requestId: EnvelopeBusMessageManager.generateRandomId(),
      type: type,
      data: args,
      purpose: EnvelopeBusMessagePurpose.REQUEST
    };

    console.debug("Requesting..");
    console.debug(message);

    this.send(message);

    return new Promise<T>((resolve, reject) => {
      this.callbacks.set(message.requestId, { resolve, reject });
    });

    //TODO: Setup timeout to avoid memory leaks
  }

  public respond<T>(request: EnvelopeBusMessage<unknown>, data: T): void {
    console.debug("Responding..");
    console.debug(request);

    if (request.purpose !== EnvelopeBusMessagePurpose.REQUEST) {
      throw new Error("Cannot respond a message that is not a request");
    }
    if (!request.requestId) {
      throw new Error("Cannot respond a request without a requestId");
    }

    const response = {
      requestId: request.requestId,
      purpose: EnvelopeBusMessagePurpose.RESPONSE,
      type: request.type,
      data
    };

    console.debug("With..");
    console.debug(response);

    this.send(response);
  }

  public notify<T>(type: MessageType, data: T): void {
    const notification = { purpose: EnvelopeBusMessagePurpose.NOTIFICATION, type, data };

    console.debug("Notifying...");
    console.debug(notification);

    this.send(notification);
  }

  public callback(response: EnvelopeBusMessage<unknown>) {
    if (response.purpose !== EnvelopeBusMessagePurpose.RESPONSE) {
      throw new Error("Cannot invoke callback with a message that is not a response");
    }
    this.callbacks.get(response.requestId!)?.resolve(response.data);
  }

  static generateRandomId() {
    return (
      "_" +
      Math.random()
        .toString(36)
        .substr(2, 9)
    );
  }
}
