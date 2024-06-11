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

import { spawnSync } from "child_process";
import { pnpmFilter } from "./pnpm_filter.js";

const pnpmFilterString = process.argv.slice(2).join(" ");

const argsByCommand = new Map([
  ["node", ["-v"]],
  ["npm", ["-v"]],
  ["pnpm", ["-v"]],
  ["java", ["-version"]],
  ["mvn", ["-v"]],
  ["go", ["version"]],
  ["make", ["-v"]],
  ["helm", ["version"]],
  ["python3", ["--version"]],
  ["pip3", ["--version"]],
  ["s2i", ["version"]],
]);

async function main() {
  if (pnpmFilterString.length === 0) {
    console.info("[check-required-preinstalled-cli-commands] Checking required CLI commands of all packages...");
  } else {
    console.info(
      `[check-required-preinstalled-cli-commands] Checking required CLI commands of packages filtered by '${pnpmFilterString}'`
    );
  }

  const filteredPackagesPaths = await pnpmFilter(pnpmFilterString, { alwaysIncludeRoot: true });

  const packagesByRequiredCommands = new Map();
  for (const pkg of Object.values(filteredPackagesPaths)) {
    for (const cmd of pkg.package.manifest.kieTools?.requiredPreinstalledCliCommands ?? []) {
      const prev = packagesByRequiredCommands.get(cmd) ?? [];
      packagesByRequiredCommands.set(cmd, [...prev, pkg.package.manifest.name]);
    }
  }

  const checks = Array.from(packagesByRequiredCommands.keys()).reduce(
    (acc, nextCmd) => ({
      ...acc,
      ...checkCliCommand(nextCmd, argsByCommand.get(nextCmd) ?? []),
    }),
    {}
  );

  for (const c in checks) {
    console.info(c, checks[c]);
  }

  console.info("[check-required-preinstalled-cli-commands] Done.");
  process.exit(0);
}

function checkCliCommand(cmd, args) {
  const shell = process.platform === "win32" ? { shell: "powershell.exe" } : {};
  const checkingCommand = spawnSync(cmd, args, { stdio: "pipe", ...shell });

  let ret = {};
  if (checkingCommand.status !== 0) {
    const check = `[❌] '${cmd}'`;
    ret[check] = "Not found. Problems may occur during the build.";
  } else {
    const check = `[✅] '${cmd}'`;
    ret[check] = `${checkingCommand.stdout} ${checkingCommand.stderr}`.trim();
  }

  return ret;
}

main();
