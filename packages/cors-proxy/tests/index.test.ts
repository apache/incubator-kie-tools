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

import { startServer } from "../src/proxy";
import { run } from "../src";

const ORIGINAL_ENV = process.env;

jest.mock("../src/proxy", () => ({
  startServer: jest.fn(),
}));

function setEnv(env: Record<string, string>) {
  process.env = {
    ...env,
    ...process.env,
  };
}

describe("index.ts test", () => {
  beforeEach(() => {
    process.env = { ...ORIGINAL_ENV };
    jest.resetAllMocks();
  });

  it("Default values", () => {
    run();

    expect(startServer).toHaveBeenCalledWith(
      expect.objectContaining({
        port: 8080,
        origin: "*",
        verbose: false,
      })
    );
  });

  it("Custom port", () => {
    setEnv({
      CORS_PROXY_HTTP_PORT: "90",
    });

    run();
    expect(startServer).toHaveBeenCalledWith(
      expect.objectContaining({
        port: 90,
        origin: "*",
        verbose: false,
      })
    );
  });

  it("Custom origin", () => {
    setEnv({
      CORS_PROXY_ORIGIN: "http://localhost",
    });
    run();
    expect(startServer).toHaveBeenCalledWith(
      expect.objectContaining({
        port: 8080,
        origin: "http://localhost",
        verbose: false,
      })
    );
  });

  it("Verbose", () => {
    setEnv({
      CORS_PROXY_VERBOSE: "true",
    });
    run();
    expect(startServer).toHaveBeenCalledWith(
      expect.objectContaining({
        port: 8080,
        origin: "*",
        verbose: true,
      })
    );
  });
});
