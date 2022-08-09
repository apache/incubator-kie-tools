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

const execSync = require("child_process").execSync;

const pnpmFilter = process.argv.slice(2).join(" ");
if (pnpmFilter.length === 0) {
  console.info("[generate-build-env-report] Generating build-env report of all packages...");
} else {
  console.info(`[generate-build-env-report] Generating build-env report of packages filtered by '${pnpmFilter}'...`);
}

const envPaths = execSync(
  `pnpm -r ${pnpmFilter} --workspace-concurrency=1 exec bash -c 'test -d env && echo $(pwd)/$(find env -type f) || true'`,
  { stdio: "pipe" }
);

for (const envPath of envPaths.toString().trim().split("\n")) {
  console.info(envPath);
  console.info(JSON.stringify(require(envPath).self, undefined, 2));
}

// console.info(`[generate-build-env-report] Printing envs...`);
// execSync(`pnpm -r ${pnpmFilter} --workspace-concurrency=1 exec bash -c 'test -d env && pnpm build-env --print-env:self && echo ',' || echo '{},''`, execOpts)
//
// console.info(`[generate-build-env-report] Printing vars...`);
// execSync(`pnpm -r ${pnpmFilter} --workspace-concurrency=1 exec bash -c 'test -d env && pnpm build-env --print-vars:self && echo ',' || echo '{},''`, execOpts)
