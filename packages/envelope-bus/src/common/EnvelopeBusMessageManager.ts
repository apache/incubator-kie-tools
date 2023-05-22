/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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
  ApiSharedValueConsumers,
  ArgsType,
  EnvelopeBusMessage,
  EnvelopeBusMessagePurpose,
  FunctionPropertyNames,
  MessageBusClientApi,
  MessageBusServer,
  NotificationCallback,
  NotificationConsumer,
  NotificationPropertyNames,
  RequestConsumer,
  RequestPropertyNames,
  SharedValueConsumer,
  SharedValueProviderPropertyNames,
} from "../api";

type Func = (...args: any[]) => any;
interface StoredPromise {
  resolve: (arg: unknown) => void;
  reject: (arg: unknown) => void;
}

export class EnvelopeBusMessageManager<
  ApiToProvide extends ApiDefinition<ApiToProvide>,
  ApiToConsume extends ApiDefinition<ApiToConsume>
> {
  private readonly requestHandlers = new Map<string, StoredPromise>();

  private readonly localNotificationsSubscriptions = new Map<NotificationPropertyNames<ApiToConsume>, Func[]>();
  private readonly remoteNotificationsSubscriptions: Array<NotificationPropertyNames<ApiToProvide>> = [];

  private readonly localSharedValueSubscriptions = new Map<
    SharedValueProviderPropertyNames<ApiToProvide> | SharedValueProviderPropertyNames<ApiToConsume>,
    Func[]
  >();
  private readonly localSharedValuesStore = new Map<
    SharedValueProviderPropertyNames<ApiToProvide> | SharedValueProviderPropertyNames<ApiToConsume>,
    ApiToProvide[keyof ApiToProvide] | ApiToConsume[keyof ApiToConsume]
  >();

  private requestIdCounter: number;
  public currentApiImpl?: ApiToProvide;

  public clientApi: MessageBusClientApi<ApiToConsume> = {
    requests: cachedProxy(
      new Map<RequestPropertyNames<ApiToConsume>, RequestConsumer<ApiToConsume[keyof ApiToConsume]>>(),
      {
        get: (target, name) => {
          return (...args) => this.request(name, ...args);
        },
      }
    ),
    notifications: cachedProxy(
      new Map<NotificationPropertyNames<ApiToConsume>, NotificationConsumer<ApiToConsume[keyof ApiToConsume]>>(),
      {
        get: (target, name) => ({
          subscribe: (callback) => this.subscribeToNotification(name, callback),
          unsubscribe: (callback) => this.unsubscribeFromNotification(name, callback),
          send: (...args) => this.notify(name, ...args),
        }),
      }
    ),
    shared: cachedProxy(
      new Map<SharedValueProviderPropertyNames<ApiToConsume>, SharedValueConsumer<ApiToConsume[keyof ApiToConsume]>>(),
      {
        get: (target, name) => ({
          set: (value) => this.setSharedValue(name, value),
          subscribe: (callback) => this.subscribeToSharedValue(name, callback, { owned: false }),
          unsubscribe: (callback) => this.unsubscribeFromSharedValue(name, callback),
        }),
      }
    ),
  };

  public shared: ApiSharedValueConsumers<ApiToProvide> = cachedProxy(
    new Map<SharedValueProviderPropertyNames<ApiToProvide>, SharedValueConsumer<ApiToProvide[keyof ApiToProvide]>>(),
    {
      get: (target, name) => ({
        set: (value) => this.setSharedValue(name, value),
        subscribe: (callback) => this.subscribeToSharedValue(name, callback, { owned: true }),
        unsubscribe: (callback) => this.unsubscribeFromSharedValue(name, callback),
      }),
    }
  );

  public get server(): MessageBusServer<ApiToProvide, ApiToConsume> {
    return {
      receive: (m, apiImpl) => {
        console.debug(m);
        this.receive(m, apiImpl);
      },
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

  private setSharedValue<
    M extends SharedValueProviderPropertyNames<ApiToProvide> | SharedValueProviderPropertyNames<ApiToConsume>
  >(method: M, value: any) {
    this.localSharedValuesStore.set(method, value);
    this.localSharedValueSubscriptions.get(method)?.forEach((callback) => callback(value));
    this.send({
      type: method,
      purpose: EnvelopeBusMessagePurpose.SHARED_VALUE_UPDATE,
      data: value,
    });
  }

  private subscribeToSharedValue<
    M extends SharedValueProviderPropertyNames<ApiToProvide> | SharedValueProviderPropertyNames<ApiToConsume>
  >(method: M, callback: Func, config: { owned: boolean }) {
    const activeSubscriptions = this.localSharedValueSubscriptions.get(method) ?? [];
    this.localSharedValueSubscriptions.set(method, [...activeSubscriptions, callback]);
    if (config.owned || this.localSharedValuesStore.get(method)) {
      callback(this.getCurrentStoredSharedValueOrDefault(method, this.currentApiImpl));
    } else {
      this.send({
        type: method,
        purpose: EnvelopeBusMessagePurpose.SHARED_VALUE_GET_DEFAULT,
        data: [],
      });
    }
    return callback;
  }

  private unsubscribeFromSharedValue<
    M extends SharedValueProviderPropertyNames<ApiToProvide> | SharedValueProviderPropertyNames<ApiToConsume>
  >(name: M, callback: any) {
    const activeSubscriptions = this.localSharedValueSubscriptions.get(name);
    if (!activeSubscriptions) {
      return;
    }

    const index = activeSubscriptions.indexOf(callback);
    if (index < 0) {
      return;
    }

    activeSubscriptions.splice(index, 1);
  }

  private getCurrentStoredSharedValueOrDefault<
    M extends SharedValueProviderPropertyNames<ApiToProvide> | SharedValueProviderPropertyNames<ApiToConsume>
  >(method: M, apiImpl?: ApiToProvide) {
    const m = method as SharedValueProviderPropertyNames<ApiToProvide>;
    return (
      this.localSharedValuesStore.get(m) ??
      this.localSharedValuesStore.set(m, apiImpl?.[m]?.apply(apiImpl).defaultValue).get(method)
    );
  }

  private subscribeToNotification<M extends NotificationPropertyNames<ApiToConsume>>(
    method: M,
    callback: (...args: ArgsType<ApiToConsume[M]>) => void
  ) {
    const activeSubscriptions = this.localNotificationsSubscriptions.get(method) ?? [];
    this.localNotificationsSubscriptions.set(method, [...activeSubscriptions, callback]);
    this.send({
      type: method,
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION_SUBSCRIPTION,
      data: [],
    });
    return callback;
  }

  private unsubscribeFromNotification<M extends NotificationPropertyNames<ApiToConsume>>(
    method: M,
    callback: NotificationCallback<ApiToConsume, M>
  ) {
    const activeSubscriptions = this.localNotificationsSubscriptions.get(method);
    if (!activeSubscriptions) {
      return;
    }

    const index = activeSubscriptions.indexOf(callback);
    if (index < 0) {
      return;
    }

    activeSubscriptions.splice(index, 1);
    this.send({
      type: method,
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION_UNSUBSCRIPTION,
      data: [],
    });
  }

  private request<M extends RequestPropertyNames<ApiToConsume>>(method: M, ...args: ArgsType<ApiToConsume[M]>) {
    const requestId = this.getNextRequestId();

    this.send({
      requestId: requestId,
      type: method,
      data: args,
      purpose: EnvelopeBusMessagePurpose.REQUEST,
    });

    return new Promise<any>((resolve, reject) => {
      this.requestHandlers.set(requestId, { resolve, reject });
    }) as ReturnType<ApiToConsume[M]>;

    //TODO: Setup timeout to avoid memory leaks
  }

  private notify<M extends NotificationPropertyNames<ApiToConsume>>(method: M, ...args: ArgsType<ApiToConsume[M]>) {
    this.send({
      type: method,
      data: args,
      purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
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
      error: error instanceof Error ? error.message : JSON.stringify(error),
    });
  }

  private callback(response: EnvelopeBusMessage<unknown, FunctionPropertyNames<ApiToConsume>>) {
    if (response.purpose !== EnvelopeBusMessagePurpose.RESPONSE) {
      throw new Error("Cannot invoke callback with a message that is not a response");
    }
    if (!response.requestId) {
      throw new Error("Cannot acknowledge a response without a requestId");
    }

    const callback = this.requestHandlers.get(response.requestId);
    if (!callback) {
      throw new Error("Callback not found for " + response);
    }

    this.requestHandlers.delete(response.requestId);

    if (!response.error) {
      callback.resolve(response.data);
    } else {
      callback.reject(new Error(response.error));
    }
  }

  private receive(
    // We can receive messages from both the APIs we provide and consume.
    message: EnvelopeBusMessage<unknown, FunctionPropertyNames<ApiToConsume> | FunctionPropertyNames<ApiToProvide>>,
    apiImpl: ApiToProvide
  ) {
    this.currentApiImpl = apiImpl;

    if (message.purpose === EnvelopeBusMessagePurpose.RESPONSE) {
      // We can only receive responses for the API we consume.
      this.callback(message as EnvelopeBusMessage<unknown, RequestPropertyNames<ApiToConsume>>);
      return;
    }

    if (message.purpose === EnvelopeBusMessagePurpose.REQUEST) {
      // We can only receive requests for the API we provide.
      const request = message as EnvelopeBusMessage<unknown, RequestPropertyNames<ApiToProvide>>;

      let response;
      try {
        response = apiImpl[request.type].apply(apiImpl, request.data);
      } catch (err) {
        console.error(err);
        this.respond(request, undefined, err);
        return;
      }

      if (!(response instanceof Promise)) {
        throw new Error(`Cannot make a request to '${String(request.type)}' because it does not return a Promise`);
      }

      response
        .then((data) => {
          this.respond(request, data);
        })
        .catch((err) => {
          console.error(err);
          this.respond(request, undefined, err);
        });

      return;
    }

    if (message.purpose === EnvelopeBusMessagePurpose.NOTIFICATION) {
      // We can only receive notifications for methods of the API we provide.
      const method = message.type as NotificationPropertyNames<ApiToProvide>;
      apiImpl[method]?.apply(apiImpl, message.data);

      if (this.remoteNotificationsSubscriptions.indexOf(method) >= 0) {
        this.send({
          type: method,
          purpose: EnvelopeBusMessagePurpose.NOTIFICATION,
          data: message.data,
        });
      }

      // We can only receive notifications from subscriptions of the API we consume.
      const localSubscriptionMethod = message.type as NotificationPropertyNames<ApiToConsume>;
      this.localNotificationsSubscriptions.get(localSubscriptionMethod)?.forEach((callback) => {
        callback(...(message.data as any[]));
      });

      return;
    }

    if (message.purpose === EnvelopeBusMessagePurpose.NOTIFICATION_SUBSCRIPTION) {
      // We can only receive subscriptions for methods of the API we provide.
      const method = message.type as NotificationPropertyNames<ApiToProvide>;
      if (this.remoteNotificationsSubscriptions.indexOf(method) < 0) {
        this.remoteNotificationsSubscriptions.push(method);
      }
      return;
    }

    if (message.purpose === EnvelopeBusMessagePurpose.NOTIFICATION_UNSUBSCRIPTION) {
      // We can only receive unsubscriptions for methods of the API we provide.
      const method = message.type as NotificationPropertyNames<ApiToProvide>;
      const index = this.remoteNotificationsSubscriptions.indexOf(method);
      if (index >= 0) {
        this.remoteNotificationsSubscriptions.splice(index, 1);
      }
      return;
    }

    if (message.purpose === EnvelopeBusMessagePurpose.SHARED_VALUE_GET_DEFAULT) {
      const method = message.type as SharedValueProviderPropertyNames<ApiToProvide>;
      this.send({
        type: method,
        purpose: EnvelopeBusMessagePurpose.SHARED_VALUE_UPDATE,
        data: this.getCurrentStoredSharedValueOrDefault(method, apiImpl),
      });
      return;
    }

    if (message.purpose === EnvelopeBusMessagePurpose.SHARED_VALUE_UPDATE) {
      const method = message.type as SharedValueProviderPropertyNames<ApiToProvide>;
      const subscriptions = this.localSharedValueSubscriptions.get(method);
      this.localSharedValuesStore.set(method, message.data as any);
      subscriptions?.forEach((callback) => callback(message.data));
      return;
    }
  }

  public getNextRequestId() {
    return `${this.name}_${this.requestIdCounter++}`;
  }
}

function cachedProxy<T extends object, K extends keyof T, V>(cache: Map<K, V>, p: { get(target: T, p: keyof T): V }) {
  return new Proxy<T>({} as T, {
    set: (target, name, value) => {
      cache.set(name as K, value);
      return true;
    },
    get: (target, name) => {
      return cache.get(name as K) ?? cache.set(name as K, p.get?.(target, name as K)).get(name as K);
    },
  });
}
