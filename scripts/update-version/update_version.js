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

const execSync = require("child_process").execSync;

const newVersion = process.argv[2];
if (!newVersion) {
  console.error("Usage 'node update_version.js [version] [pnpm-filter...]'");
  return 1;
}

const pnpmFilterString = process.argv.slice(3).join(" ");
if (pnpmFilterString.length === 0) {
  console.info("[update-version] Updating versions of all packages...");
} else {
  console.info(`[update-version] Updating versions of packages filtered by '${pnpmFilterString}'`);
}

const execOpts = { stdio: "inherit" };
const pnpmVersionArgs = `--git-tag-version=false --allow-same-version=true`;

try {
  console.info("[update-version] Updating root package...");
  execSync(`pnpm version ${newVersion} ${pnpmVersionArgs}`, execOpts);

  console.info("[update-version] Updating workspace packages...");
  execSync(`pnpm -r ${pnpmFilterString} exec pnpm version ${newVersion} ${pnpmVersionArgs}`, execOpts);

  console.info(`[update-version] Bootstrapping with updated version...`);
  execSync(`pnpm bootstrap ${pnpmFilterString}`, execOpts);

  console.info(`[update-version] Formatting files...`);
  execSync(`pnpm pretty-quick`, execOpts);

  console.info(`[update-version] Updated to '${newVersion}'.`);
  console.info(`[update-version] Done.`);
} catch (error) {
  console.error(error);
  console.error("");
  console.error(`[update-version] Error updating versions. There might be undesired unstaged changes.`);
}
