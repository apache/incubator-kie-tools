/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { HttpService, LocalHttpServer, LocalHttpService, Service } from "@kie-tooling-core/backend/dist/api";

export class DummyLocalHttpServer extends LocalHttpServer {
  public identify(): string {
    return "Dummy Local HTTP Server";
  }
  public async start(): Promise<void> {
    // Intentionally empty
  }
  public stop(): void {
    // Intentionally empty
  }
  public async satisfyRequirements(): Promise<boolean> {
    return true;
  }
}

export class DummyHttpService extends HttpService {
  public identify(): string {
    return "Dummy HTTP Service";
  }
}

export class DummyLocalHttpService extends LocalHttpService {
  public identify(): string {
    return "Dummy Local HTTP Service";
  }
}

export function createMockedService(id: string, reqSatisfied: boolean = true): jest.Mocked<Service> {
  return {
    identify: jest.fn(() => id),
    satisfyRequirements: jest.fn(async () => reqSatisfied),
    start: jest.fn(),
    stop: jest.fn(),
  };
}
