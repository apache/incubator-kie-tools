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
process.env.CORS_PROXY_MODE = "development";

import { startServer } from "../src/proxy";
import { run } from "../src";

const ORIGINAL_ENV = process.env;

jest.mock("../src/proxy", () => ({
  startServer: jest.fn(),
}));

function setEnv(env: Record<string, string>) {
  process.env = {
    ...process.env,
    ...env,
  };
}

describe("index.ts test", () => {
  beforeEach(() => {
    process.env = { ...ORIGINAL_ENV };
    jest.resetAllMocks();
  });

  describe("Development mode", () => {
    it("Default values", () => {
      delete process.env.CORS_PROXY_ORIGIN;

      setEnv({
        CORS_PROXY_MODE: "development",
      });

      run();
      expect(startServer).toHaveBeenCalledWith(
        expect.objectContaining({
          port: 8080,
          origin: "",
          verbose: false,
          allowHosts: ["localhost", "*.github.com"],
        })
      );
    });
  });

  describe("Production mode", () => {
    it("Custom port", () => {
      setEnv({
        CORS_PROXY_MODE: "production",
        CORS_PROXY_HTTP_PORT: "90",
        CORS_PROXY_ORIGIN: "http://example.com",
      });

      run();
      expect(startServer).toHaveBeenCalledWith(
        expect.objectContaining({
          port: 90,
          origin: "http://example.com",
          verbose: false,
        })
      );
    });
    it("Custom origin", () => {
      setEnv({
        CORS_PROXY_MODE: "production",
        CORS_PROXY_ORIGIN: "http://example.com",
      });
      run();
      expect(startServer).toHaveBeenCalledWith(
        expect.objectContaining({
          port: 8080,
          origin: "http://example.com",
          verbose: false,
        })
      );
    });
    it("Custom allow hosts", () => {
      setEnv({
        CORS_PROXY_MODE: "production",
        CORS_PROXY_ORIGIN: "http://example.com",
        CORS_PROXY_ALLOW_HOSTS: "*.target.example.com,*.github.com",
      });
      run();
      expect(startServer).toHaveBeenCalledWith(
        expect.objectContaining({
          port: 8080,
          origin: "http://example.com",
          verbose: false,
          allowHosts: ["*.target.example.com", "*.github.com"],
        })
      );
    });

    it("Verbose", () => {
      setEnv({
        CORS_PROXY_MODE: "production",
        CORS_PROXY_ORIGIN: "http://example.com",
        CORS_PROXY_VERBOSE: "true",
      });
      run();
      expect(startServer).toHaveBeenCalledWith(
        expect.objectContaining({
          port: 8080,
          origin: "http://example.com",
          verbose: true,
        })
      );
    });

    it("Should throw an error when the server is started without setting the origin", () => {
      delete process.env.CORS_PROXY_ORIGIN;

      setEnv({
        CORS_PROXY_MODE: "production",
      });

      expect(() => {
        run();
      }).toThrow(new Error("Invalid origin: please set an origin"));

      setEnv({
        CORS_PROXY_MODE: "production",
        CORS_PROXY_ORIGIN: "",
      });

      expect(() => {
        run();
      }).toThrow(new Error("Invalid origin: please set an origin"));
    });

    it("Should throw an error when the server is started origin '*'", () => {
      delete process.env.CORS_PROXY_ORIGIN;

      setEnv({
        CORS_PROXY_MODE: "production",
        CORS_PROXY_ORIGIN: "*",
      });

      expect(() => {
        run();
      }).toThrow(new Error('Invalid origin: wildcard "*" is not allowed.'));
    });
  });
});
