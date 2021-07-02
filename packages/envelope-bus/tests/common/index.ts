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

import { ApiDefinition, MessageBusClientApi } from "@kie-tooling-core/envelope-bus/dist/api";

export function messageBusClientApiMock<T extends ApiDefinition<T>>(): MessageBusClientApi<T> {
  const mocks = new Map<any, any>();

  const proxyMock = new Proxy({} as any, {
    get: (target, name) => {
      return mocks.get(name) ?? mocks.set(name, jest.fn()).get(name);
    },
  });

  return {
    notifications: proxyMock,
    requests: proxyMock,
    subscribe: jest.fn(),
    unsubscribe: jest.fn(),
  };
}
