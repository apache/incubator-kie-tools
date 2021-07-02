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

export type NotificationCallback<
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

export type SubscriptionCallback<Api extends ApiDefinition<Api>, M extends NotificationPropertyNames<Api>> = (
  ...args: ArgsType<Api[M]>
) => void;

export type ApiRequests<T extends ApiDefinition<T>> = Pick<T, RequestPropertyNames<T>>;

export type ApiNotifications<T extends ApiDefinition<T>> = Pick<T, NotificationPropertyNames<T>>;

export interface MessageBusClientApi<Api extends ApiDefinition<Api>> {
  requests: ApiRequests<Api>;
  notifications: ApiNotifications<Api>;

  subscribe<Method extends NotificationPropertyNames<Api>>(
    method: Method,
    callback: SubscriptionCallback<Api, Method>
  ): SubscriptionCallback<Api, Method>;

  unsubscribe<Method extends NotificationPropertyNames<Api>>(
    method: Method,
    callback: SubscriptionCallback<Api, Method>
  ): void;
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
  error?: any; // Used on RESPONSES when an exception happens when processing a request
  targetEnvelopeId?: string; // Used for messages going from the EnvelopeServer to the Envelope
  directSender?: EnvelopeBusMessageDirectSender;
}

export enum EnvelopeBusMessagePurpose {
  REQUEST = "request",
  RESPONSE = "response",
  SUBSCRIPTION = "subscription",
  UNSUBSCRIPTION = "unsubscription",
  NOTIFICATION = "notification",
}

export enum EnvelopeBusMessageDirectSender {
  ENVELOPE_BUS_CONTROLLER = "envelopeBusController",
  ENVELOPE_SERVER = "envelopeServer",
}

export interface EnvelopeBus {
  postMessage<D, T>(message: EnvelopeBusMessage<D, T>, targetOrigin?: string, _?: any): void;
}
