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
import * as utils from "@kie-tooling-core/backend/dist/node";
import * as sinon from "sinon";
import * as os from "os";

jest.mock("child_process");

describe("utility to check whether maven is available or not", () => {
  test("should return FALSE when an error occurs", async () => {
    mockCpExecCallbackOnce({ name: "error", message: "error" }, "", "");
    await expect(utils.isMavenAvailable({ major: 3, minor: 6, patch: 3 })).resolves.toBeFalsy();
  });

  test("should return FALSE when stdout is empty", async () => {
    mockCpExecCallbackOnce(null, "", "");
    await expect(utils.isMavenAvailable({ major: 3, minor: 6, patch: 3 })).resolves.toBeFalsy();
  });

  test("should return FALSE when regex does not match", async () => {
    mockCpExecCallbackOnce(null, "some text that does not match the regex", "");
    await expect(utils.isMavenAvailable({ major: 3, minor: 6, patch: 3 })).resolves.toBeFalsy();
  });

  test("should return FALSE when minimun required version > actual version", async () => {
    mockCpExecCallbackOnce(null, "Apache Maven 3.6.3", "");
    await expect(utils.isMavenAvailable({ major: 4, minor: 0, patch: 0 })).resolves.toBeFalsy();
  });

  test("should return TRUE when a match is found and version is not specified", async () => {
    mockCpExecCallbackOnce(null, "Apache Maven 3.6.3", "");
    await expect(utils.isMavenAvailable()).resolves.toBeTruthy();
  });

  test("should return TRUE when only the major value is found/given", async () => {
    mockCpExecCallbackOnce(null, "Apache Maven 3", "");
    await expect(utils.isMavenAvailable({ major: 3, minor: 0, patch: 0 })).resolves.toBeTruthy();
  });

  test("should return TRUE when minimun required version == actual version", async () => {
    mockCpExecCallbackOnce(null, "Apache Maven 3.6.3", "");
    await expect(utils.isMavenAvailable({ major: 3, minor: 6, patch: 3 })).resolves.toBeTruthy();
  });

  test("should return TRUE when minimun required version < actual version", async () => {
    mockCpExecCallbackOnce(null, "Apache Maven 3.6.3", "");
    await expect(utils.isMavenAvailable({ major: 3, minor: 5, patch: 2 })).resolves.toBeTruthy();
  });

  test("should return TRUE when minimun required version < actual version (some combinations)", async () => {
    const versions = [
      { major: 2, minor: 15, patch: 0 },
      { major: 2, minor: 0, patch: 15 },
      { major: 2, minor: 15, patch: 15 },
      { major: 2, minor: 0, patch: 0 },
      { major: 3, minor: 6, patch: 0 },
      { major: 3, minor: 0, patch: 10 },
    ];

    for (const v of versions) {
      mockCpExecCallbackOnce(null, "Apache Maven 3.6.3", "");
      await expect(utils.isMavenAvailable(v)).resolves.toBeTruthy();
    }
  });
});

describe("utility to check whether java|openjdk is available or not", () => {
  test("should return FALSE when an error occurs", async () => {
    mockCpExecCallbackOnce({ name: "error", message: "error" }, "", "");
    await expect(utils.isJavaAvailable({ major: 11, minor: 0, patch: 0 })).resolves.toBeFalsy();
  });

  test("should return FALSE when stderr is empty", async () => {
    mockCpExecCallbackOnce(null, "", "");
    await expect(utils.isJavaAvailable({ major: 11, minor: 0, patch: 0 })).resolves.toBeFalsy();
  });

  test("should return FALSE when regex does not match", async () => {
    mockCpExecCallbackOnce(null, "", "some text that does not match the regex");
    await expect(utils.isJavaAvailable({ major: 11, minor: 0, patch: 0 })).resolves.toBeFalsy();
  });

  test("should return FALSE when minimun required version > actual version (up to java 8 format)", async () => {
    mockCpExecCallbackOnce(null, "", 'java version "1.7.0_55"');
    await expect(utils.isJavaAvailable({ major: 1, minor: 8, patch: 0 })).resolves.toBeFalsy();
    mockCpExecCallbackOnce(null, "", 'openjdk version "1.7.0_55"');
    await expect(utils.isJavaAvailable({ major: 1, minor: 8, patch: 0 })).resolves.toBeFalsy();
  });

  test("should return FALSE when minimun required version > actual version (java 9+ format)", async () => {
    mockCpExecCallbackOnce(null, "", 'java version "11.0.0" 2020-04-14 LTS');
    await expect(utils.isJavaAvailable({ major: 12, minor: 0, patch: 0 })).resolves.toBeFalsy();
    mockCpExecCallbackOnce(null, "", 'openjdk version "11.0.0" 2020-04-14 LTS');
    await expect(utils.isJavaAvailable({ major: 12, minor: 0, patch: 0 })).resolves.toBeFalsy();
  });

  test("should return TRUE when a match is found and version is not specified", async () => {
    mockCpExecCallbackOnce(null, "", 'java version "11.0.0" 2020-04-14 LTS');
    await expect(utils.isJavaAvailable()).resolves.toBeTruthy();
    mockCpExecCallbackOnce(null, "", 'openjdk version "11.0.0" 2020-04-14 LTS');
    await expect(utils.isJavaAvailable()).resolves.toBeTruthy();
  });

  test("should return TRUE when only the major value is found/given", async () => {
    mockCpExecCallbackOnce(null, "", 'java version "11" 2020-04-14 LTS');
    await expect(utils.isJavaAvailable({ major: 11, minor: 0, patch: 0 })).resolves.toBeTruthy();
    mockCpExecCallbackOnce(null, "", 'openjdk version "11" 2020-04-14 LTS');
    await expect(utils.isJavaAvailable({ major: 11, minor: 0, patch: 0 })).resolves.toBeTruthy();
  });

  test("should return TRUE when minimun required version == actual version", async () => {
    mockCpExecCallbackOnce(null, "", 'java version "11.0.0" 2020-04-14 LTS');
    await expect(utils.isJavaAvailable({ major: 11, minor: 0, patch: 0 })).resolves.toBeTruthy();
    mockCpExecCallbackOnce(null, "", 'openjdk version "11.0.0" 2020-04-14 LTS');
    await expect(utils.isJavaAvailable({ major: 11, minor: 0, patch: 0 })).resolves.toBeTruthy();
  });

  test("should return TRUE when minimun required version < actual version", async () => {
    mockCpExecCallbackOnce(null, "", 'java version "12.0.0" 2020-04-14 LTS');
    await expect(utils.isJavaAvailable({ major: 11, minor: 0, patch: 0 })).resolves.toBeTruthy();
    mockCpExecCallbackOnce(null, "", 'openjdk version "12.0.0" 2020-04-14 LTS');
    await expect(utils.isJavaAvailable({ major: 11, minor: 0, patch: 0 })).resolves.toBeTruthy();
  });

  test("should return TRUE when minimun required version < actual version (some combinations)", async () => {
    const versions = [
      { major: 9, minor: 15, patch: 0 },
      { major: 9, minor: 0, patch: 15 },
      { major: 9, minor: 15, patch: 15 },
      { major: 9, minor: 0, patch: 0 },
      { major: 10, minor: 10, patch: 0 },
      { major: 10, minor: 0, patch: 15 },
    ];

    for (const v of versions) {
      mockCpExecCallbackOnce(null, "", 'java version "10.10.10" 2020-04-14 LTS');
      await expect(utils.isJavaAvailable(v)).resolves.toBeTruthy();
    }
  });
});

function mockCpExecCallbackOnce(error: cp.ExecException | null, stdout: string, stderr: string) {
  (cp as jest.Mocked<typeof cp>).exec.mockImplementationOnce(((
    command: string,
    callback?: (error: cp.ExecException | null, stdout: string, stderr: string) => void
  ): cp.ChildProcess => {
    if (callback) {
      callback(error, stdout, stderr);
    }

    return {} as cp.ChildProcess;
  }) as typeof cp.exec);
}

describe("Utility to kill a process", () => {
  const process = { kill: jest.fn(), pid: 9999 } as unknown as cp.ChildProcess;
  const sandbox = sinon.createSandbox();

  afterEach(() => {
    sandbox.restore();
  });

  test("should kill the process through taskkill on Windows OS", async () => {
    const cpStub = sandbox.stub(cp, "spawn");
    sandbox.stub(os, "platform").returns("win32");
    utils.killProcess(process);
    expect(cpStub.called).toBeTruthy();
  });

  test("should kill the process through the active process on Linux OS", async () => {
    sandbox.stub(os, "platform").returns("linux");
    utils.killProcess(process);
    expect(process.kill).toBeCalled();
  });

  test("should kill the process through the active process on Mac OS", async () => {
    sandbox.stub(os, "platform").returns("darwin");
    utils.killProcess(process);
    expect(process.kill).toBeCalled();
  });
});
