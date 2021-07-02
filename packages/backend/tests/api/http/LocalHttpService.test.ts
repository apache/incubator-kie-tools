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

import { HttpResponse, HttpService, LocalHttpService } from "@kie-tooling-core/backend/dist/api";
import { DummyLocalHttpService } from "../dummyServices";

const testEndpoint = "/some/local/endpoint";
const testResponse: HttpResponse = { body: "some response content" };
const testPort = 8099;

let localHttpService: LocalHttpService;
beforeEach(() => {
  localHttpService = new DummyLocalHttpService();
});

describe("execute local http requests", () => {
  test("should reject promise when port is not registered", async () => {
    try {
      await localHttpService.execute(testEndpoint);
      fail("should not have reached here");
    } catch (e) {
      expect(e).toBe("Local port not registered.");
    }
  });

  test("should return response when port is registered", async () => {
    const requestBody = "some request content";
    jest.spyOn(HttpService.prototype, "execute").mockResolvedValue(testResponse);

    localHttpService.registerPort(testPort);
    await expect(localHttpService.execute(testEndpoint, requestBody)).resolves.toMatchObject(testResponse);
    expect(HttpService.prototype.execute).toBeCalledWith(`http://localhost:${testPort}${testEndpoint}`, requestBody);
  });

  test("should reject the promise when an error occurs in the bridge", async () => {
    const errorMsg = "Some error";
    jest.spyOn(HttpService.prototype, "execute").mockRejectedValue(errorMsg);

    localHttpService.registerPort(testPort);
    try {
      await localHttpService.execute(testEndpoint);
      fail("should not have reached here");
    } catch (e) {
      expect(e).toBe(errorMsg);
    }
  });
});
