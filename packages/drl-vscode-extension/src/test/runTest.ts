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

import * as fs from "fs";
import * as os from "os";
import * as path from "path";
import { runTests } from "@vscode/test-electron";

async function main() {
  // Use a short user-data-dir to keep VS Code's IPC socket path under the
  // 103-char Unix socket limit (macOS), which the default path inside the
  // package's .vscode-test directory can exceed on deep checkouts
  const userDataDir = fs.mkdtempSync(path.join(os.tmpdir(), "vsct-"));

  try {
    // The folder containing the Extension Manifest package.json
    // Passed to `--extensionDevelopmentPath`
    const extensionDevelopmentPath = path.resolve(__dirname, "../../");

    // The path to the compiled test runner
    // Passed to --extensionTestsPath
    const extensionTestsPath = path.resolve(__dirname, "./suite/index");

    // Use win64 instead of win32 for testing Windows
    const platform = process.platform === "win32" ? "win32-x64-archive" : undefined;

    // The workspace folder for the test — a Maven project with domain classes
    const testWorkspace = path.resolve(__dirname, "../../src/testFixture");

    // Download VS Code, unzip it and run the integration test
    await runTests({
      extensionDevelopmentPath,
      extensionTestsPath,
      platform,
      launchArgs: [testWorkspace, `--user-data-dir=${userDataDir}`],
    });

    // Clean up the temporary VS Code profile on success only; on failure it is
    // preserved so its logs can be inspected
    fs.rmSync(userDataDir, { recursive: true, force: true });
  } catch (err) {
    console.error("Failed to run tests");
    console.error(`VS Code user-data-dir preserved for diagnostics: ${userDataDir}`);
    process.exitCode = 1;
  }
}

main();
