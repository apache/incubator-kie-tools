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

import * as React from "react";
import { useCallback, useEffect, useRef, useState } from "react";
import {
  ApiDefinition,
  NotificationConsumer,
  NotificationPropertyNames,
  SharedValueConsumer,
  SubscriptionCallback,
} from "../api";
import { EnvelopeServer } from "../channel";

export function useConnectedEnvelopeServer<Api extends ApiDefinition<Api>>(
  envelopeServer: EnvelopeServer<Api, any>,
  apiImpl: Api
) {
  useEffect(() => {
    const listener = (msg: MessageEvent) => envelopeServer.receive(msg.data, apiImpl);
    window.addEventListener("message", listener, false);
    envelopeServer.startInitPolling(apiImpl);

    return () => {
      envelopeServer.stopInitPolling();
      window.removeEventListener("message", listener);
    };
  }, [envelopeServer, apiImpl]);
}

export function useSubscription<Api extends ApiDefinition<Api>, M extends NotificationPropertyNames<Api>>(
  notificationConsumer: NotificationConsumer<Api[M]>,
  callback: SubscriptionCallback<Api, M>
) {
  useEffect(() => {
    const subscription = notificationConsumer.subscribe(callback);
    return () => {
      notificationConsumer.unsubscribe(subscription);
    };
  }, [notificationConsumer, callback]);
}

export function useSubscriptionOnce<Api extends ApiDefinition<Api>, M extends NotificationPropertyNames<Api>>(
  notificationConsumer: NotificationConsumer<Api[M]>,
  callback: SubscriptionCallback<Api, M>
) {
  useEffect(() => {
    let unsubscribed = false;

    const subscription = notificationConsumer.subscribe((...args) => {
      callback(...args);
      unsubscribed = true;
      notificationConsumer.unsubscribe(subscription);
    });

    return () => {
      if (!unsubscribed) {
        notificationConsumer.unsubscribe(subscription);
      }
    };
  }, [callback, notificationConsumer]);
}

export function useSharedValue<T>(
  sharedValue: SharedValueConsumer<T> | undefined
): [T | undefined, React.Dispatch<React.SetStateAction<T>>] {
  const [value, setValue] = useState<T>();

  useEffect(() => {
    if (!sharedValue) {
      return;
    }

    const subscription = sharedValue.subscribe((newValue) => setValue(newValue));
    return () => sharedValue.unsubscribe(subscription);
  }, [sharedValue]);

  // keep the same reference, like React does
  const sharedValueRef = useRef(sharedValue);
  const ret__setValue = useCallback((t: T) => {
    sharedValueRef.current?.set(t);
  }, []);

  // update the ref value when the sharedValue changes
  useEffect(() => {
    sharedValueRef.current = sharedValue;
  }, [sharedValue]);

  return [value, ret__setValue];
}

export function useStateAsSharedValue<T>(
  value: T,
  setValue: React.Dispatch<React.SetStateAction<T>>,
  sharedValue: SharedValueConsumer<T> | undefined
) {
  useEffect(() => {
    if (!sharedValue) {
      return;
    }

    const subscription = sharedValue.subscribe((newValue) => setValue(newValue));
    return () => sharedValue.unsubscribe(subscription);
  }, [sharedValue, setValue]);

  useEffect(() => {
    sharedValue?.set(value);
  }, [sharedValue, value]);
}
