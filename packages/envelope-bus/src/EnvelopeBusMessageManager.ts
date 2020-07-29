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

/* tslint:disable:ban-types */

type NotificationCallback<
  ApiToConsume extends ApiDefinition<ApiToConsume>,
  M extends NotificationPropertyNames<ApiToConsume>
> = (...args: ArgsType<ApiToConsume[M]>) => void;

export type NotificationPropertyNames<T extends ApiDefinition<T>> = {
  [K in keyof T]: ReturnType<T[K]> extends void ? K : never;
}[keyof T];

export type RequestPropertyNames<T extends ApiDefinition<T>> = {
  [K in keyof T]: ReturnType<T[K]> extends Promise<any> ? K : never;
}[keyof T];

export type FunctionPropertyNames<T extends ApiDefinition<T>> = NotificationPropertyNames<T> | RequestPropertyNames<T>;

export type ApiDefinition<T> = { [P in keyof T]: (...a: any) => Promise<any> | void };
export type ArgsType<T> = T extends (...args: infer A) => any ? A : never;

export interface MessageBusClient<Api extends ApiDefinition<Api>> {
  request<M extends RequestPropertyNames<Api>>(method: M, ...args: ArgsType<Api[M]>): ReturnType<Api[M]>;
  notify<M extends NotificationPropertyNames<Api>>(method: M, ...args: ArgsType<Api[M]>): void;
  subscribe<M extends NotificationPropertyNames<Api>>(method: M, callback: (...args: ArgsType<Api[M]>) => void): void;
  unsubscribe<M extends NotificationPropertyNames<Api>>(method: M, callback: (...args: ArgsType<Api[M]>) => void): void;
}

export interface MessageBusServer<
  ApiToProvide extends ApiDefinition<ApiToProvide>,
  ApiToConsume extends ApiDefinition<ApiToConsume>
> {
  receive(
    message: EnvelopeBusMessage<unknown, FunctionPropertyNames<ApiToProvide> | FunctionPropertyNames<ApiToConsume>>,
    api: ApiToProvide
  ): void;
}

export class EnvelopeBusMessageManager<
  ApiToProvide extends ApiDefinition<ApiToProvide>,
  ApiToConsume extends ApiDefinition<ApiToConsume>
> {
  private readonly callbacks = new Map<string, { resolve: (arg: unknown) => void; reject: (arg: unknown) => void }>();

  private readonly remoteSubscriptions: Array<NotificationPropertyNames<ApiToProvide>> = [];

  private readonly localSubscriptions = new Map<NotificationPropertyNames<ApiToConsume>, Function[]>();

  private requestIdCounter: number;

  public get client(): MessageBusClient<ApiToConsume> {
    return {
      request: (m, ...a) => this.request(m, ...a),
      notify: (m, ...a) => this.notify(m, ...a),
      subscribe: (m, a) => this.subscribe(m, a),
      unsubscribe: (m, a) => this.unsubscribe(m, a)
    };
  }
  public get server(): MessageBusServer<ApiToProvide, ApiToConsume> {
    return {
      receive: (m, api) => this.receive(m, api)
    };
  }

  constructor(
    private readonly send: (
      // We can send messages for both the APIs we provide and consume
      message: EnvelopeBusMessage<unknown, FunctionPropertyNames<ApiToConsume> | FunctionPropertyNames<ApiToProvide>>
    ) => void,
    private readonly name: string = `${new Date().getMilliseconds()}`
  ) {
    this.requestIdCounter = 0;
  }

  private subscribe<M extends NotificationPropertyNames<ApiToConsume>>(
    method: M,
    callback: (...args: ArgsType<ApiToConsume[M]>) => void
  ) {
    const activeSubscriptions = this.localSubscriptions.get(method) ?? [];
    this.localSubscriptions.set(method, [...activeSubscriptions, callback]);
    this.send({
      type: method,
      purpose: EnvelopeBusMessagePurpose.SUBSCRIPTION,
      data: []
    });
    return callback;
  }

  private unsubscribe<M extends NotificationPropertyNames<ApiToConsume>>(
    method: M,
    callback: NotificationCallback<ApiToConsume, M>
  ) {
    const values = this.localSubscriptions.get(method);
    if (!values) {
      return;
    }

    const index = values.indexOf(callback);
    if (index < 0) {
      return;
    }

    values.splice(index, 1);
  }

  private request<M extends RequestPropertyNames<ApiToConsume>>(method: M, ...args: ArgsType<ApiToConsume[M]>) {
    const requestId = this.getNextRequestId();

    this.send({
      requestId: requestId,
      type: method,
      data: args,
      purpose: EnvelopeBusMessagePurpose.REQUEST
    });

    return new Promise((resolve, reject) => {
      this.callbacks.set(requestId, { resolve, reject });
    }) as ReturnType<ApiToConsume[M]>;

    //TODO: Setup timeout to avoid memory leaks
  }

  private notify<M extends NotificationPropertyNames<ApiToConsume>>(method: M, ...args: ArgsType<ApiToConsume[M]>) {
    this.send({
      type: method,
      data: args,
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION
    });
  }

  private respond<T>(
    request: EnvelopeBusMessage<unknown, FunctionPropertyNames<ApiToProvide>>,
    data: T,
    error?: any
  ): void {
    if (request.purpose !== EnvelopeBusMessagePurpose.REQUEST) {
      throw new Error("Cannot respond a message that is not a request");
    }

    if (!request.requestId) {
      throw new Error("Cannot respond a request without a requestId");
    }

    this.send({
      requestId: request.requestId,
      purpose: EnvelopeBusMessagePurpose.RESPONSE,
      type: request.type as FunctionPropertyNames<ApiToProvide>,
      data: data,
      error: error
    });
  }

  private callback(response: EnvelopeBusMessage<unknown, FunctionPropertyNames<ApiToConsume>>) {
    if (response.purpose !== EnvelopeBusMessagePurpose.RESPONSE) {
      throw new Error("Cannot invoke callback with a message that is not a response");
    }
    if (!response.requestId) {
      throw new Error("Cannot acknowledge a response without a requestId");
    }

    const callback = this.callbacks.get(response.requestId);
    if (!callback) {
      throw new Error("Callback not found for " + response);
    }

    this.callbacks.delete(response.requestId);

    if (!response.error) {
      callback.resolve(response.data);
    } else {
      console.error(JSON.stringify(response.error));
      callback.reject(response.error);
    }
  }
  private receive(
    // We can receive messages from both the APIs we provide and consume.
    message: EnvelopeBusMessage<unknown, FunctionPropertyNames<ApiToConsume> | FunctionPropertyNames<ApiToProvide>>,
    api: ApiToProvide
  ) {
    if (message.purpose === EnvelopeBusMessagePurpose.RESPONSE) {
      // We can only receive responses for the API we consume.
      this.callback(message as EnvelopeBusMessage<unknown, RequestPropertyNames<ApiToConsume>>);
      return;
    }

    if (message.purpose === EnvelopeBusMessagePurpose.REQUEST) {
      // We can only receive requests for the API we provide.
      const request = message as EnvelopeBusMessage<unknown, RequestPropertyNames<ApiToProvide>>;

      const response = api[request.type].apply(api, request.data);
      if (!(response instanceof Promise)) {
        throw new Error(`Cannot make a request to '${request.type}' because it does not return a Promise`);
      }

      response.then(data => this.respond(request, data)).catch(err => this.respond(request, undefined, err));
      return;
    }

    if (message.purpose === EnvelopeBusMessagePurpose.NOTIFICATION) {
      // We can only receive notifications for methods of the API we provide.
      const method = message.type as NotificationPropertyNames<ApiToProvide>;
      api[method]?.apply(api, message.data);

      if (this.remoteSubscriptions.indexOf(method) >= 0) {
        this.send({
          type: method,
          purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
          data: message.data
        });
      }

      // We can only receive notifications from subscriptions of the API we consume.
      const localSubscriptionMethod = message.type as NotificationPropertyNames<ApiToConsume>;
      (this.localSubscriptions.get(localSubscriptionMethod) ?? []).forEach(callback => {
        callback(message.data);
      });

      return;
    }

    if (message.purpose === EnvelopeBusMessagePurpose.SUBSCRIPTION) {
      // We can only receive subscriptions for methods of the API we provide.
      const method = message.type as NotificationPropertyNames<ApiToProvide>;
      if (this.remoteSubscriptions.indexOf(method) < 0) {
        this.remoteSubscriptions.push(method);
      }
      return;
    }

    if (message.purpose === EnvelopeBusMessagePurpose.UNSUBSCRIPTION) {
      // We can only receive unsubscriptions for methods of the API we provide.
      const method = message.type as NotificationPropertyNames<ApiToProvide>;
      const index = this.remoteSubscriptions.indexOf(method);
      if (index >= 0) {
        this.remoteSubscriptions.splice(index, 1);
      }
      return;
    }
  }

  public getNextRequestId() {
    return `${this.name}_${this.requestIdCounter++}`;
  }
}
