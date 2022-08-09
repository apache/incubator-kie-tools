/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

const { spawnSync } = require("child_process");

async function main() {
  console.info(`[check-cli-tools] Checking CLI tools...`);
  const checks = {
    ...checkCliTool("node", ["-v"]),
    ...checkCliTool("npm", ["-v"]),
    ...checkCliTool("pnpm", ["-v"]),
    ...checkCliTool("go", ["version"]),
    ...checkCliTool("mvn", ["-v"]),
  };

  for (const c in checks) {
    console.info(c, checks[c]);
  }

  console.info("[check-cli-tools] Done.");
  process.exit(0);
}

function checkCliTool(bin, args) {
  const shell = process.platform === "win32" ? { shell: "powershell.exe" } : {};
  const checkingCommand = spawnSync(bin, args, { stdio: "pipe", ...shell });

  let ret = {};
  if (checkingCommand.status !== 0) {
    const check = `[❌] ${bin}`;
    ret[check] = "Not found. Problems may occur during the build.";
  } else {
    const check = `[✅] ${bin}`;
    ret[check] = `${checkingCommand.stdout}`.trim();
  }

  return ret;
}

main();
