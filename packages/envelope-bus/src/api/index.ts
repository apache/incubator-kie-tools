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

export interface MessageBusClient<Api extends ApiDefinition<Api>> {
  request<M extends RequestPropertyNames<Api>>(method: M, ...args: ArgsType<Api[M]>): ReturnType<Api[M]>;
  notify<M extends NotificationPropertyNames<Api>>(method: M, ...args: ArgsType<Api[M]>): void;
  unsubscribe<M extends NotificationPropertyNames<Api>>(method: M, callback: SubscriptionCallback<Api, M>): void;
  subscribe<M extends NotificationPropertyNames<Api>>(
    method: M,
    callback: SubscriptionCallback<Api, M>
  ): SubscriptionCallback<Api, M>;
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

export interface EnvelopeBusMessage<D, T> {
  data: D;
  type: T;
  envelopeServerId?: string; // Used for messages going from the envelope to the channel
  requestId?: string; // Used when purpose is REQUEST or RESPONSE
  purpose: EnvelopeBusMessagePurpose;
  error?: any; //Used on RESPONSES when an exception happens when processing a request
}

export enum EnvelopeBusMessagePurpose {
  REQUEST = "request",
  RESPONSE = "response",
  SUBSCRIPTION = "subscription",
  UNSUBSCRIPTION = "unsubscription",
  NOTIFICATION = "notification"
}

export interface EnvelopeBus {
  postMessage<D, T>(message: EnvelopeBusMessage<D, T>, targetOrigin?: string, _?: any): void;
}
