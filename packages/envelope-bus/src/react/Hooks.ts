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

import { ApiDefinition, MessageBusClient, NotificationPropertyNames, SubscriptionCallback } from "../api";
import { useEffect } from "react";

export function useSubscription<Api extends ApiDefinition<Api>, M extends NotificationPropertyNames<Api>>(
  bus: MessageBusClient<Api>,
  method: M,
  callback: SubscriptionCallback<Api, M>
) {
  useEffect(() => {
    const subscription = bus.subscribe(method, callback);
    return () => {
      bus.unsubscribe(method, subscription);
    };
  }, [bus, method, callback]);
}

export function useSubscriptionOnce<Api extends ApiDefinition<Api>, M extends NotificationPropertyNames<Api>>(
  bus: MessageBusClient<Api>,
  method: M,
  callback: SubscriptionCallback<Api, M>
) {
  useEffect(() => {
    let unsubscribed = false;

    const subscription = bus.subscribe(method, (...args) => {
      callback(...args);
      unsubscribed = true;
      bus.unsubscribe(method, subscription);
    });

    return () => {
      if (!unsubscribed) {
        bus.unsubscribe(method, subscription);
      }
    };
  }, [bus, method, callback]);
}
