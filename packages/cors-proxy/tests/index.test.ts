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

process.env.CORS_PROXY_ALLOWED_ORIGINS = "http://localhost:9001";

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

  it("Default values", () => {
    setEnv({
      CORS_PROXY_MODE: "development",
    });

    run();
    expect(startServer).toHaveBeenCalledWith(
      expect.objectContaining({
        port: 8080,
        verbose: false,
        allowHosts: ["localhost", "*.github.com"],
        allowedOrigins: ["http://localhost:9001"],
      })
    );
  });

  it("Custom port", () => {
    setEnv({
      CORS_PROXY_HTTP_PORT: "90",
      CORS_PROXY_ALLOWED_ORIGINS: "http://example.com",
    });

    run();
    expect(startServer).toHaveBeenCalledWith(
      expect.objectContaining({
        port: 90,
        allowedOrigins: ["http://example.com"],
        verbose: false,
        allowHosts: ["localhost", "*.github.com"],
      })
    );
  });

  it("Verbose mode", () => {
    setEnv({
      CORS_PROXY_ALLOWED_ORIGINS: "http://example.com",
      CORS_PROXY_VERBOSE: "true",
    });
    run();
    expect(startServer).toHaveBeenCalledWith(
      expect.objectContaining({
        port: 8080,
        allowedOrigins: ["http://example.com"],
        verbose: true,
        allowHosts: ["localhost", "*.github.com"],
      })
    );
  });

  describe("Allowed origins configuration", () => {
    it("Single allowed origin", () => {
      setEnv({
        CORS_PROXY_ALLOWED_ORIGINS: "http://example.com",
      });

      run();
      expect(startServer).toHaveBeenCalledWith(
        expect.objectContaining({
          port: 8080,
          allowedOrigins: ["http://example.com"],
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
          verbose: false,
          allowHosts: ["*.target.example.com", "*.github.com"],
        })
      );
    });

    it("Multiple allowed origins", () => {
      setEnv({
        CORS_PROXY_ALLOWED_ORIGINS: "http://example.com,https://other.example.com,http://localhost:9001",
      });

      run();
      expect(startServer).toHaveBeenCalledWith(
        expect.objectContaining({
          port: 8080,
          allowedOrigins: ["http://example.com", "https://other.example.com", "http://localhost:9001"],
          verbose: false,
        })
      );
    });

    it("Should throw an error when allowed origins is not set", () => {
      delete process.env.CORS_PROXY_ALLOWED_ORIGINS;

      expect(() => {
        run();
      }).toThrow(new Error("Invalid origin: empty origins are not allowed in CORS_PROXY_ALLOWED_ORIGINS."));
    });

    it("Should throw an error when allowed origins is empty", () => {
      setEnv({
        CORS_PROXY_ALLOWED_ORIGINS: "",
      });

      expect(() => {
        run();
      }).toThrow(new Error("Invalid origin: empty origins are not allowed in CORS_PROXY_ALLOWED_ORIGINS."));
    });

    it("Should throw an error when wildcard '*' is in the list", () => {
      setEnv({
        CORS_PROXY_ALLOWED_ORIGINS: "http://example.com,*",
      });

      expect(() => {
        run();
      }).toThrow(new Error('Invalid origin: wildcard "*" is not allowed in CORS_PROXY_ALLOWED_ORIGINS.'));
    });

    it("Should throw an error when there are empty origins in the list", () => {
      setEnv({
        CORS_PROXY_ALLOWED_ORIGINS: "http://example.com,,https://other.example.com",
      });

      expect(() => {
        run();
      }).toThrow(new Error("Invalid origin: empty origins are not allowed in CORS_PROXY_ALLOWED_ORIGINS."));
    });
  });
});
