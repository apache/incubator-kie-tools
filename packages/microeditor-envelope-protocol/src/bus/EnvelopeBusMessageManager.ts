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

export class EnvelopeBusMessageManager<MessageTypeToSend, MessageTypeToReceive, Api> {
  private readonly callbacks = new Map<string, { resolve: (arg: unknown) => void; reject: (arg: unknown) => void }>();

  constructor(
    private readonly send: (message: EnvelopeBusMessage<unknown, MessageTypeToSend | MessageTypeToReceive>) => void,
    private readonly api: Api,
    private readonly apiMapping: Map<MessageTypeToReceive, keyof Api>
  ) {}

  public request<T>(type: MessageTypeToSend, args: unknown): Promise<T> {
    const message = {
      requestId: this.generateRandomId(),
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

  public respond<T>(request: EnvelopeBusMessage<unknown, MessageTypeToReceive>, data: T): void {
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

  public notify<T>(type: MessageTypeToSend, data: T): void {
    const notification = { purpose: EnvelopeBusMessagePurpose.NOTIFICATION, type, data };

    console.debug("Notifying...");
    console.debug(notification);

    this.send(notification);
  }

  public callback(response: EnvelopeBusMessage<unknown, MessageTypeToReceive>) {
    console.debug("Executing response for " + response.type);
    if (response.purpose !== EnvelopeBusMessagePurpose.RESPONSE) {
      throw new Error("Cannot invoke callback with a message that is not a response");
    }

    const callback = this.callbacks.get(response.requestId!);
    if (!callback) {
      throw new Error("Callback not found for " + response);
    }

    this.callbacks.delete(response.requestId!);
    callback.resolve(response.data);
  }
  public receive(message: EnvelopeBusMessage<unknown, MessageTypeToReceive | MessageTypeToSend>) {
    if (message.purpose === EnvelopeBusMessagePurpose.RESPONSE) {
      this.callback(message as EnvelopeBusMessage<unknown, MessageTypeToReceive>);
      return;
    }

    if (message.purpose === EnvelopeBusMessagePurpose.REQUEST) {
      const handle = this.apiMapping.get(message.type as MessageTypeToReceive)!;
      const response = (this.api[handle] as any).apply(this.api, message.data ? [message.data] : []) as Promise<
        unknown
      >;
      response.then(r => this.respond(message as EnvelopeBusMessage<unknown, MessageTypeToReceive>, r));
      return;
    }

    if (message.purpose === EnvelopeBusMessagePurpose.NOTIFICATION) {
      const handle = this.apiMapping.get(message.type as MessageTypeToReceive)!;
      (this.api[handle] as any).apply(this.api, message.data ? [message.data] : []);
      return;
    }
  }

  public generateRandomId() {
    return (
      "_" +
      Math.random()
        .toString(36)
        .substr(2, 9)
    );
  }
}
