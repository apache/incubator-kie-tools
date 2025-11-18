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
const originalCors = jest.requireActual("cors");
const originalExpress = jest.requireActual("express");

const corsMock = jest.fn((options?: any) => {
  return originalCors(options);
});

jest.mock("node-fetch", () => jest.fn());
jest.mock("cors", () => corsMock);
jest.mock("express", () => () => {
  const app = originalExpress();
  app.listen = jest.fn(() => {});
  return app;
});

import { ServerArgs, startServer } from "../src/proxy/server";

describe("CORS handler logic test", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe("tests with isDevMode=true", () => {
    it("should allow requests with origin http://localhost:8080", (done) => {
      const args: ServerArgs = {
        isDevMode: true,
        port: 8080,
        origin: "",
        verbose: false,
        hostsToUseHttp: [],
        allowHosts: ["localhost"],
      };

      startServer(args);

      const req: any = {
        headers: {
          origin: "http://localhost:9000",
        },
        method: "GET",
      };

      const res: any = {
        statusCode: 200,
        setHeader: jest.fn(),
        getHeader: jest.fn(),
      };

      const next = function () {
        expect(res.setHeader).toHaveBeenCalledWith("Access-Control-Allow-Origin", "http://localhost:9000");
        done();
      };

      const corsMiddleware = corsMock.mock.results[0].value;
      corsMiddleware(req, res, next);
    });

    it("should not allow requests with origin http://notvalid:8080", (done) => {
      const args: ServerArgs = {
        isDevMode: true,
        port: 8080,
        origin: "",
        verbose: false,
        hostsToUseHttp: [],
        allowHosts: ["localhost"],
      };

      startServer(args);

      const req: any = {
        headers: {
          origin: "http://notvalid:8080",
        },
        method: "GET",
      };

      const res: any = {
        statusCode: 200,
        setHeader: jest.fn(),
        getHeader: jest.fn(),
      };

      const next = function () {
        expect(res.setHeader).not.toHaveBeenCalled();
        done();
      };

      const corsMiddleware = corsMock.mock.results[0].value;
      corsMiddleware(req, res, next);
    });
  });

  describe("tests with isDevMode=false", () => {
    it("should allow requests with origin http://example.com", (done) => {
      const args: ServerArgs = {
        isDevMode: false,
        port: 8080,
        origin: "http://example.com",
        verbose: false,
        hostsToUseHttp: [],
        allowHosts: ["localhost"],
      };

      startServer(args);

      const req: any = {
        headers: {
          origin: "http://example.com",
        },
        method: "GET",
      };

      const res: any = {
        statusCode: 200,
        setHeader: jest.fn(),
        getHeader: jest.fn(),
      };

      const next = function () {
        expect(res.setHeader).toHaveBeenCalledWith("Access-Control-Allow-Origin", "http://example.com");
        done();
      };

      const corsMiddleware = corsMock.mock.results[0].value;
      corsMiddleware(req, res, next);
    });

    it("should not allow requests with origin http://notvalid:9000", (done) => {
      const args: ServerArgs = {
        isDevMode: false,
        port: 8080,
        origin: "http://example.com",
        verbose: false,
        hostsToUseHttp: [],
        allowHosts: ["localhost"],
      };

      startServer(args);

      const req: any = {
        headers: {
          origin: "http://notvalid:9000",
        },
        method: "GET",
      };

      const res: any = {
        statusCode: 200,
        setHeader: jest.fn(),
        getHeader: jest.fn(),
      };

      const next = function () {
        expect(res.setHeader).toHaveBeenCalledWith("Access-Control-Allow-Origin", "http://example.com");
        done();
      };

      const corsMiddleware = corsMock.mock.results[0].value;
      corsMiddleware(req, res, next);
    });
  });
});
