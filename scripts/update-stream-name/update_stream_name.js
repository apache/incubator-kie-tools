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

const newStreamName = process.argv[2];
if (!newStreamName) {
  console.error("Usage 'node update_stream_name.js [new-name]'");
  return 1;
}
const execOpts = { stdio: "inherit" };

try {
  console.info("[update-stream-name] Updating 'packages/root-env/env/index.js'...");
  const rootEnvPath = path.resolve(__dirname, "../../packages/root-env/env/index.js");
  fs.writeFileSync(
    rootEnvPath,
    fs
      .readFileSync(rootEnvPath, "utf-8")
      .replace(
        /KIE_TOOLS_BUILD__streamName:[\s\n]*{[\s\n]*default:[\s\n]*".*"/,
        `KIE_TOOLS_BUILD__streamName: {\n      default: "${newStreamName}"`
      )
  );

  console.info(`[update-stream-name] Bootstrapping with updated Stream name...`);
  execSync(`pnpm bootstrap`, execOpts);

  console.info(`[update-stream-name] Formatting files...`);
  execSync(`pnpm pretty-quick`, execOpts);

  console.info(`[update-stream-name] Updated Stream name to '${newStreamName}'.`);
  console.info(`[update-stream-name] Done.`);
} catch (error) {
  console.error(error);
  console.error("");
  console.error(`[update-stream-name] Error updating Stream name version. There might be undesired unstaged changes.`);
}
