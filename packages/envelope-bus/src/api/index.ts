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

export type NotificationCallback<
  ApiToConsume extends ApiDefinition<ApiToConsume>,
  M extends NotificationPropertyNames<ApiToConsume>
> = (...args: ArgsType<ApiToConsume[M]>) => void;

export type SharedValueProviderPropertyNames<T extends ApiDefinition<T>> = {
  [K in keyof T]: ReturnType<T[K]> extends SharedValueProvider<any> ? K : never;
}[keyof T];

export type NotificationPropertyNames<T extends ApiDefinition<T>> = {
  [K in keyof T]: ReturnType<T[K]> extends void ? K : never;
}[keyof T];

export type RequestPropertyNames<T extends ApiDefinition<T>> = {
  [K in keyof T]: ReturnType<T[K]> extends Promise<any> ? K : never;
}[keyof T];

export interface SharedValueConsumer<T> {
  subscribe(callback: (newValue: T) => void): (newValue: T) => any;
  unsubscribe(subscription: (newValue: T) => void): void;
  set(t: T): void;
}

export interface SharedValueProvider<T> {
  defaultValue: T;
}

export type FunctionPropertyNames<T extends ApiDefinition<T>> =
  | SharedValueProviderPropertyNames<T>
  | NotificationPropertyNames<T>
  | RequestPropertyNames<T>;

export type ApiDefinition<T> = { [P in keyof T]: (...a: any) => Promise<any> | SharedValueProvider<any> | void };

export type ArgsType<T> = T extends (...args: infer A) => any ? A : never;

export type SubscriptionCallback<Api extends ApiDefinition<Api>, M extends NotificationPropertyNames<Api>> = (
  ...args: ArgsType<Api[M]>
) => void;

export type ApiRequests<T extends ApiDefinition<T>> = Pick<T, RequestPropertyNames<T>>;

export type ApiNotificationConsumers<T extends ApiDefinition<T>> = Pick<
  WithNotificationConsumers<T>,
  NotificationPropertyNames<T>
>;

export type ApiSharedValueConsumers<T extends ApiDefinition<T>> = Pick<
  WithSharedValueConsumers<T>,
  SharedValueProviderPropertyNames<T>
>;

export type WithSharedValueConsumers<T extends ApiDefinition<T>> = {
  [K in keyof T]: ReturnType<T[K]> extends SharedValueProvider<infer U> ? SharedValueConsumer<U> : never;
};

export interface NotificationConsumer<N> {
  subscribe(callback: (...newValue: ArgsType<N>) => void): (...newValue: ArgsType<N>) => any;
  unsubscribe(subscription: (...newValue: ArgsType<N>) => void): void;
  send(...args: ArgsType<N>): void;
}
export type WithNotificationConsumers<T extends ApiDefinition<T>> = {
  [K in keyof T]: ReturnType<T[K]> extends void ? NotificationConsumer<T[K]> : never;
};

export type WithRequestConsumers<T extends ApiDefinition<T>> = {
  [K in keyof T]: ReturnType<T[K]> extends void ? RequestConsumer<T[K]> : never;
};

export type RequestConsumer<T extends () => any> = (...args: ArgsType<T>) => ReturnType<T>;

export interface MessageBusClientApi<Api extends ApiDefinition<Api>> {
  requests: ApiRequests<Api>;
  notifications: ApiNotificationConsumers<Api>;
  shared: ApiSharedValueConsumers<Api>;
}

export interface MessageBusServer<
  ApiToProvide extends ApiDefinition<ApiToProvide>,
  ApiToConsume extends ApiDefinition<ApiToConsume>
> {
  receive(
    message: EnvelopeBusMessage<unknown, FunctionPropertyNames<ApiToProvide> | FunctionPropertyNames<ApiToConsume>>,
    apiImpl: ApiToProvide
  ): void;
}

export interface EnvelopeBusMessage<D, T> {
  data: D;
  type: T;
  targetEnvelopeServerId?: string; // Used for messages going from the Envelope to the EnvelopeServer
  requestId?: string; // Used when purpose is REQUEST or RESPONSE
  purpose: EnvelopeBusMessagePurpose;
  error?: string; // Used on RESPONSES when an exception happens when processing a request
  targetEnvelopeId?: string; // Used for messages going from the EnvelopeServer to the Envelope
  directSender?: EnvelopeBusMessageDirectSender;
}

export enum EnvelopeBusMessagePurpose {
  REQUEST = "request",
  RESPONSE = "response",
  NOTIFICATION_SUBSCRIPTION = "subscription",
  NOTIFICATION_UNSUBSCRIPTION = "unsubscription",
  NOTIFICATION = "notification",
  SHARED_VALUE_GET_DEFAULT = "shared-value-get-default",
  SHARED_VALUE_UPDATE = "shared-value-update",
}

export enum EnvelopeBusMessageDirectSender {
  ENVELOPE_CLIENT = "envelopeClient",
  ENVELOPE_SERVER = "envelopeServer",
}

export interface EnvelopeBus {
  postMessage<D, T>(message: EnvelopeBusMessage<D, T>, targetOrigin?: string, _?: any): void;
}
