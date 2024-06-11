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
const path = require("path");

let pnpmFilterString = process.argv.slice(2).join(" ");
let pnpmFilterStringForInstalling;
if (pnpmFilterString.length === 0) {
  console.info("[bootstrap] Bootstrapping all packages...");
  pnpmFilterStringForInstalling = "";
} else {
  console.info(`[bootstrap] Bootstrapping packages filtered by '${pnpmFilterString}'`);
  pnpmFilterStringForInstalling = `${pnpmFilterString}`;
}

const execOpts = { stdio: "inherit" };

console.info("\n\n[bootstrap] Installing packages dependencies...");
execSync(
  `pnpm install --strict-peer-dependencies=false -F !kie-tools-root... ${pnpmFilterStringForInstalling}`,
  execOpts
);

console.info("\n\n[bootstrap] Linking packages with self...");
execSync(`node ${require.resolve("./link_packages_with_self.js")}`, execOpts);

console.info("\n\n[bootstrap] Generating packages graph...");
execSync(`node ${require.resolve("./generate_packages_graph.js")} ${path.resolve(__dirname, "../../repo")}`, execOpts);

console.info("\n\n[bootstrap] Generating build-env report...");
execSync(`node ${require.resolve("./generate_build_env_report.mjs")} ${pnpmFilterString}`, execOpts);

console.info("\n\n[bootstrap] Checking required preinstalled CLI commands...");
execSync(`node ${require.resolve("./check_required_preinstalled_cli_commands.mjs")} ${pnpmFilterString}`, execOpts);

console.info("\n\n[bootstrap] Checking packages dependencies...");
execSync(`node ${require.resolve("./check_packages_dependencies.js")}`, execOpts);

console.info("\n\n[bootstrap] Formatting auto-generated files...");
execSync(`pnpm pretty-quick`, execOpts);

console.info("\n\n[bootstrap] Done.");
