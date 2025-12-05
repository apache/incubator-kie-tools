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

const getMockGetRequest = (url: string) =>
  ({
    header: (header: string) => {
      if (header === "origin") {
        return "http://example.com";
      }
    },
    headers: {
      origin: "http://example.com",
    },
    url,
    method: "GET",
  }) as any as Request;

const getMockPostRequest = (targetUrl: string) =>
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
  const allowedRequests = [
    ["http://", "localhost:8081", "localhost,*.github.com"],
    ["https://", "localhost:4000", "localhost,*.github.com"],
    ["http://", "gist.github.com", "localhost,*.github.com"],
    ["http://", "www.github.com", "localhost,*.github.com"],
    ["https://", "www.github.com", "localhost,*.github.com"],
    ["http://", "test.target.example.com", "*.target.example.com,*.github.com"],
    ["http://", "test.target.example.com:9000", "*.target.example.com,*.github.com"],
    [
      "http://",
      "test.target.example.com:9000/path/to/something?search=test&sortBy=name",
      "*.target.example.com,*.github.com",
    ],
    ["https://", "test.target.example.com", "*.target.example.com,*.github.com"],
    ["https://", "test.target.example.com/graphql", "*.target.example.com,*.github.com"],
    ["https://", "test.target.example.com/openapi", "*.target.example.com,*.github.com"],
    ["http://", "gist.github.com", "*.target.example.com,*.github.com"],
    ["http://", "www.github.com", "*.target.example.com,*.github.com"],
    ["https://", "www.github.com", "*.target.example.com,*.github.com"],
    [
      "https://",
      "api.aaa.bbb.p1.openshiftapps.com:6443/version",
      "*.target.example.com,*.github.com,*.openshiftapps.com",
    ],
  ];
  const deniedRequests = [
    ["http://", "www.example.com"],
    ["http://", "www.example.com/graphql"],
    ["http://", "api.notvalid.com"],
    ["http://", "api.notvalid.com/openapi.json"],
    ["http://", "notvalid.com"],
    ["http://", "api.notvalid.com/www.github.com"],
  ];

  beforeEach(() => {
    jest.clearAllMocks();
    mockFetch.mockImplementation(getMockFetchResponse);
  });

  describe("Test GET requests", () => {
    it.each(allowedRequests)(
      `should allow GET requests to %s%s with allowed hosts: %s`,
      async (protocol, targetUrl, allowedHosts) => {
        const proxy = getProxy(allowedHosts.split(","));

        await proxy.handle(getMockGetRequest(`/${targetUrl}`), getMockResponse(), mockNext);

        expect(mockWarn).not.toHaveBeenCalled();
        expect(mockFetch).toHaveBeenCalledWith(
          new URL(protocol + targetUrl),
          expect.objectContaining({ method: "GET" })
        );
        expect(mockNext).not.toHaveBeenCalled();
      }
    );

    it.each(deniedRequests)(
      "should not allow requests to %s%s with allowed hosts: *.target.example.com,*.github.com",
      async (protocol, targetUrl) => {
        const proxy = getProxy(["*.target.example.com", "*.github.com"]);

        await proxy.handle(getMockGetRequest(`/${targetUrl}`), getMockResponse(), mockNext);

        expect(mockWarn).toHaveBeenCalledWith(expect.any(String), expect.stringContaining("not allowed"));
        expect(mockFetch).not.toHaveBeenCalled();
        expect(mockNext).toHaveBeenCalled();
      }
    );
  });

  describe("Test POST requests", () => {
    it.each(allowedRequests)(
      `should allow POST requests to %s%s with allowed hosts: %s`,
      async (protocol, targetUrl, allowedHosts) => {
        const proxy = getProxy(allowedHosts.split(","));

        await proxy.handle(getMockPostRequest(protocol + targetUrl), getMockResponse(), mockNext);

        expect(mockWarn).not.toHaveBeenCalled();
        expect(mockFetch).toHaveBeenCalledWith(
          new URL(protocol + targetUrl),
          expect.objectContaining({ method: "POST" })
        );
        expect(mockNext).not.toHaveBeenCalled();
      }
    );

    it.each(deniedRequests)(
      "should not allow requests to %s%s with allowed hosts: *.target.example.com,*.github.com",
      async (protocol, targetUrl) => {
        const proxy = getProxy(["*.target.example.com", "*.github.com"]);

        await proxy.handle(getMockPostRequest(protocol + targetUrl), getMockResponse(), mockNext);

        expect(mockWarn).toHaveBeenCalledWith(expect.any(String), expect.stringContaining("not allowed"));
        expect(mockFetch).not.toHaveBeenCalled();
        expect(mockNext).toHaveBeenCalled();
      }
    );
  });
});
