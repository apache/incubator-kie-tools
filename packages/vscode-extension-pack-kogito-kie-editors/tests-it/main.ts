/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as __path from "path";
import * as vscodeTest from "vscode-test";

async function main() {
  try {
    const extensionDevelopmentPath = __path.resolve(__dirname, "../../");
    const extensionTestsPath = __path.resolve(__dirname, "./suite");
    const workspace = __path.resolve(__dirname, "../../tests-it/test-workspace");

    await vscodeTest.runTests({
      extensionDevelopmentPath,
      extensionTestsPath,
      launchArgs: [workspace, "--disableExtensions", "--enable-proposed-api", "kiegroup.vscode-extension-pack-kogito-kie-editors"]
    });
  } catch (e) {
    console.error("Failed to run integration tests");
    process.exit(1);
  }
}

main();
