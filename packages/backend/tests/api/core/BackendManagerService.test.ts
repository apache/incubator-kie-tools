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

import { BackendManagerService, HttpBridge } from "@kie-tooling-core/backend/dist/api";
import { createMockedService, DummyHttpService, DummyLocalHttpServer, DummyLocalHttpService } from "../dummyServices";

const localHttpServer = new DummyLocalHttpServer();

describe("satisfy requirements of the backend manager service", () => {
  test("should always return TRUE since there are no requirements to satisfy", async () => {
    const manager = new BackendManagerService({});
    await expect(manager.satisfyRequirements()).resolves.toBeTruthy();
  });
});

describe("stop the backend manager service", () => {
  test("should stop all registered services", async () => {
    const serviceA = createMockedService("Service A");
    const serviceB = createMockedService("Service B");
    const serviceC = createMockedService("Service C");
    const manager = new BackendManagerService({
      localHttpServer: localHttpServer,
      bootstrapServices: [serviceA, serviceB],
      lazyServices: [serviceC],
    });
    const localServerStopFn = jest.spyOn(localHttpServer, "stop");
    await manager.start();
    await manager.getService("Service C");
    manager.stop();
    expect(serviceA.stop).toBeCalled();
    expect(serviceB.stop).toBeCalled();
    expect(serviceC.stop).toBeCalled();
    expect(localServerStopFn).toBeCalled();
  });
});

describe("start the backend manager service", () => {
  test("should not register when there is no service to be registered", async () => {
    const manager = new BackendManagerService({});
    const registerServiceFn = jest.spyOn(manager, "registerService");
    await manager.start();
    expect(registerServiceFn).not.toBeCalled();
  });

  test("should only register the local http server", async () => {
    const manager = new BackendManagerService({ localHttpServer: localHttpServer });
    const registerServiceFn = jest.spyOn(manager, "registerService");
    await manager.start();
    expect(registerServiceFn).toBeCalledTimes(1);
    expect(registerServiceFn).toBeCalledWith(localHttpServer);
  });

  test("should only register the bootstrap services", async () => {
    const serviceA = createMockedService("Service A");
    const serviceB = createMockedService("Service B");
    const manager = new BackendManagerService({ bootstrapServices: [serviceA, serviceB] });
    const registerServiceFn = jest.spyOn(manager, "registerService");
    await manager.start();
    expect(registerServiceFn).toBeCalledTimes(2);
    expect(registerServiceFn).toBeCalledWith(serviceA);
    expect(registerServiceFn).toBeCalledWith(serviceB);
  });

  test("should register both the local http server and the bootstrap services", async () => {
    const serviceA = createMockedService("Service A");
    const serviceB = createMockedService("Service B");
    const manager = new BackendManagerService({
      localHttpServer: localHttpServer,
      bootstrapServices: [serviceA, serviceB],
    });
    const registerServiceFn = jest.spyOn(manager, "registerService");
    await manager.start();
    expect(registerServiceFn).toBeCalledTimes(3);
    expect(registerServiceFn).toBeCalledWith(localHttpServer);
    expect(registerServiceFn).toBeCalledWith(serviceA);
    expect(registerServiceFn).toBeCalledWith(serviceB);
  });

  test("should not register the lazy services", async () => {
    const serviceA = createMockedService("Service A");
    const serviceB = createMockedService("Service B");
    const manager = new BackendManagerService({ lazyServices: [serviceA, serviceB] });
    const registerServiceFn = jest.spyOn(manager, "registerService");
    await manager.start();
    expect(registerServiceFn).not.toBeCalled();
  });
});

describe("retrieve a service", () => {
  test("should return undefined when the required service is not found", async () => {
    const manager = new BackendManagerService({});
    await expect(manager.getService("Unknown Service")).resolves.toBeUndefined();
  });

  test("should return undefined when the required service is not found while having other lazy services", async () => {
    const manager = new BackendManagerService({ lazyServices: [createMockedService("Service A")] });
    await expect(manager.getService("Unknown Service")).resolves.toBeUndefined();
  });

  test("should return the required bootstrap service", async () => {
    const serviceId = "Service A";
    const serviceA = createMockedService(serviceId);
    const manager = new BackendManagerService({ bootstrapServices: [serviceA] });
    const registerServiceFn = jest.spyOn(manager, "registerService");
    await manager.start();
    await expect(manager.getService(serviceId)).resolves.toBe(serviceA);
    expect(registerServiceFn).toBeCalledTimes(1);
  });

  test("should register and return the required lazy service", async () => {
    const serviceId = "Service A";
    const serviceA = createMockedService(serviceId);
    const manager = new BackendManagerService({ lazyServices: [serviceA] });
    const registerServiceFn = jest.spyOn(manager, "registerService");
    await expect(manager.getService(serviceId)).resolves.toBe(serviceA);
    expect(registerServiceFn).toBeCalled();
  });
});

describe("register a new service", () => {
  const httpService = new DummyHttpService();
  const localHttpService = new DummyLocalHttpService();
  const httpBridge: jest.Mocked<HttpBridge> = {
    request: jest.fn(),
  };

  test("should return TRUE when the service is already registered", async () => {
    const serviceA = createMockedService("Service A");
    const manager = new BackendManagerService({ bootstrapServices: [serviceA] });
    await manager.start();
    await expect(manager.registerService(serviceA)).resolves.toBeTruthy();
  });

  test("should return FALSE when the requirements are not satisfied", async () => {
    const serviceA = createMockedService("Service A", false);
    const manager = new BackendManagerService({});
    await expect(manager.registerService(serviceA)).resolves.toBeFalsy();
  });

  test("should start the service when the requirements are satisfied", async () => {
    const serviceA = createMockedService("Service A");
    const manager = new BackendManagerService({});
    await expect(manager.registerService(serviceA)).resolves.toBeTruthy();
    expect(serviceA.start).toBeCalled();
  });

  test("should return FALSE when an error occurs while starting the service up", async () => {
    const serviceA = createMockedService("Service A");
    const manager = new BackendManagerService({});
    serviceA.start.mockRejectedValueOnce("Some error");
    await expect(manager.registerService(serviceA)).resolves.toBeFalsy();
  });

  test("should return FALSE when there is no HTTP bridge for an HTTP service", async () => {
    const manager = new BackendManagerService({});
    await expect(manager.registerService(httpService)).resolves.toBeFalsy();
  });

  test("should return TRUE when there is an HTTP bridge for an HTTP service", async () => {
    const manager = new BackendManagerService({ bridge: httpBridge });
    const registerHttpBridgeFn = jest.spyOn(httpService, "registerHttpBridge");
    await expect(manager.registerService(httpService)).resolves.toBeTruthy();
    expect(registerHttpBridgeFn).toBeCalled();
  });

  test("should return FALSE when there is no HTTP bridge for a local HTTP service", async () => {
    const manager = new BackendManagerService({});
    await expect(manager.registerService(localHttpService)).resolves.toBeFalsy();
  });

  test("should return FALSE when there is no local HTTP server for a local HTTP service", async () => {
    const manager = new BackendManagerService({ bridge: httpBridge });
    await expect(manager.registerService(localHttpService)).resolves.toBeFalsy();
  });

  test("should return TRUE when there is a local HTTP server for a local HTTP service", async () => {
    const manager = new BackendManagerService({ bridge: httpBridge, localHttpServer: localHttpServer });
    const registerPortFn = jest.spyOn(localHttpService, "registerPort");
    await manager.start();
    await expect(manager.registerService(localHttpService)).resolves.toBeTruthy();
    expect(registerPortFn).toBeCalled();
  });
});
