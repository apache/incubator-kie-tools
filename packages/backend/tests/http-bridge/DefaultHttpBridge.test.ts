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

import axios, { AxiosError } from "axios";
import { DefaultHttpBridge } from "@kie-tooling-core/backend/dist/http-bridge";

jest.mock("axios");
const mockAxios = axios as jest.Mocked<typeof axios>;

const bridge = new DefaultHttpBridge();
const testEndpoint = "some.endpoint/test";

describe("requests throught DefaultHttpBridge", () => {
  test("should execute GET when the request does not have a body", async () => {
    mockAxios.get.mockResolvedValueOnce({});
    await bridge.request({ endpoint: testEndpoint });
    expect(mockAxios.get).toBeCalledWith(testEndpoint);
  });

  test("should execute POST when the request has a body", async () => {
    const requestBody = { foo: "bar" };
    mockAxios.post.mockResolvedValueOnce({});
    await bridge.request({ endpoint: testEndpoint, body: requestBody });
    expect(mockAxios.post).toBeCalledWith(testEndpoint, requestBody);
  });

  test("should return the response data on success of a GET", async () => {
    const responseData = { some: "data" };
    mockAxios.get.mockResolvedValueOnce({ data: responseData });
    const response = await bridge.request({ endpoint: testEndpoint });
    expect(response.body).toBe(responseData);
  });

  test("should return the response data on success of a POST", async () => {
    const responseData = { some: "data" };
    mockAxios.post.mockResolvedValueOnce({ data: responseData });
    const response = await bridge.request({ endpoint: testEndpoint, body: { foo: "bar" } });
    expect(response.body).toBe(responseData);
  });

  test("should reject the promise when a generic error ocurrs", async () => {
    const errorMsg = "Some error";
    mockAxios.get.mockRejectedValueOnce(new Error(errorMsg));
    try {
      await bridge.request({ endpoint: testEndpoint });
      fail("should not have reached here");
    } catch (e) {
      expect(e).toBe(errorMsg);
    }
  });

  test("should reject the promise when an error ocurrs on the endpoint", async () => {
    const errorMsg = "Some error";
    mockAxios.get.mockRejectedValueOnce({ message: errorMsg, config: { url: testEndpoint } } as AxiosError);
    try {
      await bridge.request({ endpoint: testEndpoint });
      fail("should not have reached here");
    } catch (e) {
      expect(e).toBe(`${errorMsg} ${testEndpoint}`);
    }
  });
});
