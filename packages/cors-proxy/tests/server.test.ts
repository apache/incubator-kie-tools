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

  describe("Single origin in allowedOrigins", () => {
    it("should allow requests from origin http://localhost:9000", (done) => {
      const args: ServerArgs = {
        allowedOrigins: ["http://localhost:9000"],
        port: 8080,
        verbose: false,
        hostsToUseHttp: [],
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

    it("should not allow requests from non-allowed origin", (done) => {
      const args: ServerArgs = {
        allowedOrigins: ["http://localhost:9000"],
        port: 8080,
        verbose: false,
        hostsToUseHttp: [],
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

  describe("Multiple origins in allowedOrigins", () => {
    it.each(["http://example.com", "http://staging.example.com"])(
      'should allow requests from %s allowed origin with allowedOrigins: ["http://example.com", "http://staging.example.com"]',
      (origin, done) => {
        const args: ServerArgs = {
          allowedOrigins: ["http://example.com", "http://staging.example.com"],
          port: 8080,
          verbose: false,
          hostsToUseHttp: [],
        };

        startServer(args);

        const req: any = {
          headers: {
            origin,
          },
          method: "GET",
        };

        const res: any = {
          statusCode: 200,
          setHeader: jest.fn(),
          getHeader: jest.fn(),
        };

        const next = function () {
          expect(res.setHeader).toHaveBeenCalledWith("Access-Control-Allow-Origin", origin);
          done();
        };

        const corsMiddleware = corsMock.mock.results[0].value;
        corsMiddleware(req, res, next);
      }
    );

    it("should not allow requests from non-allowed origin", (done) => {
      const args: ServerArgs = {
        allowedOrigins: ["http://example.com", "http://staging.example.com"],
        port: 8080,
        verbose: false,
        hostsToUseHttp: [],
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
        expect(res.setHeader).not.toHaveBeenCalled();
        done();
      };

      const corsMiddleware = corsMock.mock.results[0].value;
      corsMiddleware(req, res, next);
    });
  });
});
