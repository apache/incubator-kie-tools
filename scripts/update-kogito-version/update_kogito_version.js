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

const fs = require("fs");
const path = require("path");
const execSync = require("child_process").execSync;

const newMavenVersion = process.argv[3];
if (!newMavenVersion) {
  console.error("Usage 'node update_kogito_version.js --maven [version]");
  return 1;
}

if (process.argv[2] !== "--maven") {
  console.error("Arguments need to be passed in the correct order.");
  console.error(`Argv: ${process.argv.join(", ")}`);
  console.error("Usage 'node update_kogito_version.js --maven [version]");
  process.exit(1);
}

const execOpts = { stdio: "inherit" };

try {
  console.info("[update-kogito-version] Updating 'packages/root-env/env/index.js'...");
  const rootEnvPath = path.resolve(__dirname, "../../packages/root-env/env/index.js");
  fs.writeFileSync(
    rootEnvPath,
    fs
      .readFileSync(rootEnvPath, "utf-8")
      .replace(
        /KOGITO_RUNTIME_version:[\s\n]*{[\s\n]*default:[\s\n]*".*"/,
        `KOGITO_RUNTIME_version: {\n      default: "${newMavenVersion}"`
      )
  );

  console.info(`[update-kogito-version] Bootstrapping with updated Kogito version...`);
  execSync(`pnpm bootstrap`, execOpts);

  console.info(`[update-kogito-version] Formatting files...`);
  execSync(`pnpm pretty-quick`, execOpts);

  console.info(`[update-kogito-version] Updated Kogito to '${newMavenVersion}' (Maven)`);
  console.info(`[update-kogito-version] Done.`);
} catch (error) {
  console.error(error);
  console.error("");
  console.error(`[update-kogito-version] Error updating Kogito version. There might be undesired unstaged changes.`);
}
