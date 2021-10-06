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

import {
  BackendManagerService,
  BackendProxy,
  CapabilityResponse,
  CapabilityResponseStatus,
} from "@kie-tooling-core/backend/dist/api";
import { createMockedService } from "../dummyServices";

const backendManager = new BackendManagerService({});

let backendProxy: BackendProxy;
beforeEach(() => {
  backendProxy = new BackendProxy();
});

describe("stop services", () => {
  test("should call the registered backend manager stop function", () => {
    const stopFn = jest.spyOn(backendManager, "stop");
    backendProxy.registerBackendManager(backendManager);
    backendProxy.stopServices();
    expect(stopFn).toBeCalled();
  });
});

describe("access a capability", () => {
  const testServiceId = "Service A";

  test("should return a MISSING_INFRA response when no backend manager is registered", async () => {
    const response = await backendProxy.withCapability(testServiceId, async () => CapabilityResponse.ok());
    expect(response.status).toBe(CapabilityResponseStatus.MISSING_INFRA);
  });

  test("should return a NOT_AVAILABLE response when the required service is not found", async () => {
    jest.spyOn(backendManager, "getService").mockResolvedValueOnce(undefined);
    backendProxy.registerBackendManager(backendManager);

    const response = await backendProxy.withCapability(testServiceId, async () => CapabilityResponse.ok());
    expect(response.status).toBe(CapabilityResponseStatus.NOT_AVAILABLE);
    expect(response.message).toBe(`Service ${testServiceId} not available.`);
  });

  test("should execute the callback when the service is found", async () => {
    const responseContent = { foo: "bar" };
    jest.spyOn(backendManager, "getService").mockResolvedValueOnce(createMockedService("Service A"));
    const testCallback = jest.fn().mockImplementation(async () => CapabilityResponse.ok(responseContent));
    backendProxy.registerBackendManager(backendManager);

    const response = await backendProxy.withCapability(testServiceId, testCallback);
    expect(testCallback).toBeCalled();
    expect(response.status).toBe(CapabilityResponseStatus.OK);
    expect(response.body).toBe(responseContent);
  });
});
