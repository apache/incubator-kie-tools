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

import * as cp from "child_process";
import * as events from "events";
import * as fs from "fs";
import * as portfinder from "portfinder";
import * as sinon from "sinon";
import * as stream from "stream";
import { QuarkusLocalServer } from "@kie-tooling-core/backend/dist/node";
import * as utils from "@kie-tooling-core/backend/dist/node/utils";

jest.mock("fs");

const testPort = 8099;
const testJarFile = "jar/file/path.jar";

beforeAll(() => {
  jest.resetAllMocks();
});

describe("satisfy requirements of the Quarkus local server", () => {
  const service = new QuarkusLocalServer(testJarFile);
  const mockGetPortPromise = jest.spyOn(portfinder, "getPortPromise");
  const mockIsJavaAvailableFn = jest.spyOn(utils, "isJavaAvailable");
  const mockFs = fs as jest.Mocked<typeof fs>;

  test("should check if path provided in the constructor exists", async () => {
    await service.satisfyRequirements();
    expect(mockFs.existsSync).toBeCalledWith(testJarFile);
  });

  test("should return FALSE when the runner jar file is missing", async () => {
    mockFs.existsSync.mockReturnValueOnce(false);
    await expect(service.satisfyRequirements()).resolves.toBeFalsy();
    expect(mockIsJavaAvailableFn).not.toBeCalled();
  });

  test("should return FALSE when java is missing", async () => {
    mockFs.existsSync.mockReturnValueOnce(true);
    mockIsJavaAvailableFn.mockResolvedValueOnce(false);
    await expect(service.satisfyRequirements()).resolves.toBeFalsy();
  });

  test("should return FALSE when an error occurs while trying to find a port", async () => {
    mockFs.existsSync.mockReturnValue(true);
    mockIsJavaAvailableFn.mockResolvedValueOnce(true);
    mockGetPortPromise.mockRejectedValueOnce(new Error());
    await expect(service.satisfyRequirements()).resolves.toBeFalsy();
  });

  test("should return TRUE when all requirements are satisfied", async () => {
    mockFs.existsSync.mockReturnValueOnce(true);
    mockIsJavaAvailableFn.mockResolvedValueOnce(true);
    mockGetPortPromise.mockResolvedValueOnce(testPort);
    await expect(service.satisfyRequirements()).resolves.toBeTruthy();
    expect(service.getPort()).toBe(testPort);
  });
});

describe("start the Quarkus local server", () => {
  const sandbox = sinon.createSandbox();

  let quarkusServer: QuarkusLocalServer;
  beforeEach(() => {
    quarkusServer = new QuarkusLocalServer(testJarFile);
  });

  afterEach(() => {
    sandbox.restore();
  });

  test("should reject the promise when cannot identify that Quarkus is up (timeout reached)", async () => {
    const process = new events.EventEmitter() as cp.ChildProcess;
    process.stdout = new events.EventEmitter() as stream.Readable;
    sandbox.stub(cp, "spawn").returns(process);

    try {
      await quarkusServer.start();
      fail("should not have reached here");
    } catch (e) {
      expect(e.message).toBe("Could not start the Quarkus local server.");
    }
  });

  test("should reject the promise when there is no stdout", async () => {
    sandbox.stub(cp, "spawn").returns(new events.EventEmitter() as cp.ChildProcess);

    try {
      await quarkusServer.start();
      fail("should not have reached here");
    } catch (e) {
      expect(e.message).toBe("Could not start the Quarkus local server.");
    }
  });

  test("should reject the promise when the expected data is not emitted on the stdout (timeout reached)", async () => {
    const process = new events.EventEmitter() as cp.ChildProcess;
    process.stdout = new events.EventEmitter() as stream.Readable;
    sandbox.stub(cp, "spawn").returns(process);

    setTimeout(() => {
      process.stdout!.emit("data", "Some other data");
    }, 1000);

    try {
      await quarkusServer.start();
      fail("should not have reached here");
    } catch (e) {
      expect(e.message).toBe("Could not start the Quarkus local server.");
    }
  });

  test("should resolve the promise when Quarkus is up", async () => {
    const process = new events.EventEmitter() as cp.ChildProcess;
    process.stdout = new events.EventEmitter() as stream.Readable;
    sandbox.stub(cp, "spawn").returns(process);

    setTimeout(() => {
      process.stdout!.emit("data", "Listening on");
    }, 500);

    await expect(quarkusServer.start()).resolves.toBeUndefined();
  });
});

describe("stop the Quarkus local server", () => {
  const sandbox = sinon.createSandbox();
  const mockKillProcessFn = jest.spyOn(utils, "killProcess");

  let quarkusServer: QuarkusLocalServer;
  beforeEach(() => {
    quarkusServer = new QuarkusLocalServer(testJarFile);
  });

  afterEach(() => {
    sandbox.restore();
  });

  test("should do nothing since there is no active process", async () => {
    quarkusServer.stop();
    expect(mockKillProcessFn).not.toBeCalled();
  });

  test("should kill the process", async () => {
    await startQuarkusServerTest(quarkusServer);
    quarkusServer.stop();
    expect(mockKillProcessFn).toBeCalled();
  });

  async function startQuarkusServerTest(server: QuarkusLocalServer): Promise<void> {
    const process = { kill: jest.fn(), pid: 9999 } as unknown as cp.ChildProcess;
    process.stdout = new events.EventEmitter() as stream.Readable;
    sandbox.stub(cp, "spawn").returns(process);

    setTimeout(() => {
      process.stdout!.emit("data", "Listening on");
    }, 500);

    await server.start();
    sandbox.restore();
  }
});
