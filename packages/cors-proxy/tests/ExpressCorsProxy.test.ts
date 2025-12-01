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

const mockFetch = jest.fn();
jest.mock("node-fetch", () => ({
  __esModule: true,
  default: mockFetch,
}));

import { ExpressCorsProxy } from "../src/proxy/ExpressCorsProxy";
import { Request, Response } from "express";

const getProxy = (allowedHosts: string[]) =>
  new ExpressCorsProxy({
    allowedOrigins: ["http://example.com"],
    verbose: false,
    hostsToUseHttp: [],
    allowedHosts,
  });

const getMockFetchResponse = () =>
  Promise.resolve({
    status: 200,
    headers: new Map([["content-type", "application/json"]]),
  });

const getMockResponse = (): Response =>
  ({
    header: () => {},
    setHeader: () => {},
    getHeaders: () => {},
    status: () => {},
    end: () => {},
    write: () => {},
    on: () => {},
    once: () => {},
    emit: () => {},
  }) as any as Response;

const getMockRequest = (targetUrl: string) =>
  ({
    header: (header: string) => {
      if (header === "origin") {
        return "http://example.com";
      }
    },
    headers: {
      origin: "http://example.com",
      "target-url": targetUrl,
    },
    method: "POST",
  }) as any as Request;

describe("ExpressCorsProxy allowed hosts functionality", () => {
  const mockWarn = (console.warn = jest.fn());
  const mockNext = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
    mockFetch.mockImplementation(getMockFetchResponse);
  });

  it.each([
    "http://localhost:8080",
    "https://localhost:4000",
    "http://gist.github.com",
    "http://www.github.com",
    "https://www.github.com",
  ])("should allow requests to %s with allowed hosts set to default value", async (targetUrl) => {
    const proxy = getProxy(["localhost", "*.github.com"]);

    await proxy.handle(getMockRequest(targetUrl), getMockResponse(), mockNext);

    expect(mockWarn).not.toHaveBeenCalled();
    expect(mockFetch).toHaveBeenCalled();
    expect(mockNext).not.toHaveBeenCalled();
  });

  it.each([
    "http://test.target.example.com",
    "http://test.target.example.com:9000",
    "https://test.target.example.com",
    "https://test.target.example.com/graphql",
    "https://test.target.example.com/openapi",
    "http://gist.github.com",
    "http://www.github.com",
    "https://www.github.com",
    "https://api.aaa.bbb.p1.openshiftapps.com:6443/version",
  ])("should allow requests to %s with allowed hosts: *.target.example.com and *.github.com", async (targetUrl) => {
    const proxy = getProxy(["*.target.example.com", "*.github.com", "*.openshiftapps.com"]);

    await proxy.handle(getMockRequest(targetUrl), getMockResponse(), mockNext);

    expect(mockWarn).not.toHaveBeenCalled();
    expect(mockFetch).toHaveBeenCalled();
    expect(mockNext).not.toHaveBeenCalled();
  });

  it.each([
    "http://www.example.com",
    "http://www.example.com/graphql",
    "http://api.notvalid.com",
    "http://api.notvalid.com/openapi.json",
    "http://notvalid.com",
    "http://api.notvalid.com/www.github.com",
  ])("should not allow requests to %s with allowed hosts: *.target.example.com and *.github.com", async (targetUrl) => {
    const proxy = getProxy(["*.target.example.com", "*.github.com"]);

    await proxy.handle(getMockRequest(targetUrl), getMockResponse(), mockNext);

    expect(mockWarn).toHaveBeenCalledWith(expect.any(String), expect.stringContaining("not allowed"));
    expect(mockFetch).not.toHaveBeenCalled();
    expect(mockNext).toHaveBeenCalled();
  });
});
