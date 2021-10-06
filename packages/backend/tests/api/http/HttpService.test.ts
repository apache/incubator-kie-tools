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

import { HttpBridge, HttpResponse, HttpService } from "@kie-tooling-core/backend/dist/api";
import { DummyHttpService } from "../dummyServices";

let httpService: HttpService;
beforeEach(() => {
  httpService = new DummyHttpService();
});

describe("execute http requests", () => {
  const testEndpoint = "some.endpoint/test";
  test("should reject promise when bridge is not registered", async () => {
    try {
      await httpService.execute(testEndpoint);
      fail("should not have reached here");
    } catch (e) {
      expect(e).toBe("Service bridge is not registered.");
    }
  });

  test("should return response when bridge is registered", async () => {
    const response: HttpResponse = { body: "some content" };
    const bridge: jest.Mocked<HttpBridge> = { request: jest.fn().mockResolvedValue(response) };
    httpService.registerHttpBridge(bridge);
    await expect(httpService.execute(testEndpoint)).resolves.toBe(response);
  });

  test("should reject the promise when an error occurs in the bridge", async () => {
    const errorMsg = "Some error";
    const bridge: jest.Mocked<HttpBridge> = { request: jest.fn().mockRejectedValue(errorMsg) };
    httpService.registerHttpBridge(bridge);
    try {
      await httpService.execute(testEndpoint);
      fail("should not have reached here");
    } catch (e) {
      expect(e).toBe(errorMsg);
    }
  });
});

describe("satisfy requirements of the http service", () => {
  test("should always return TRUE since there are no requirements to satisfy", async () => {
    await expect(httpService.satisfyRequirements()).resolves.toBeTruthy();
  });
});
