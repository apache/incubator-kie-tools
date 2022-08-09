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

const path = require("path");
const execSync = require("child_process").execSync;

const pnpmFilter = process.argv.slice(2).join(" ");
if (pnpmFilter.length === 0) {
  console.info("[generate-build-env-report] Generating build-env report of all packages...");
} else {
  console.info(`[generate-build-env-report] Generating build-env report of packages filtered by '${pnpmFilter}'...`);
}

async function main() {
  const pkgs = findPathsOfPackagesWithEnvDir().map((pkgPath) => ({
    env: require(path.join(pkgPath, "env", "index.js")),
    manifest: require(path.join(pkgPath, "package.json")),
    dir: pkgPath,
  }));

  for (const pkg of pkgs) {
    console.info(`${pkg.manifest.name} (at ${pkg.dir})`);
    console.info(JSON.stringify(pkg.env.self, undefined, 2));
  }
}

// TODO: Detect duplicate vars
// TODO: Detect duplicate env root properties

function findPathsOfPackagesWithEnvDir() {
  try {
    const pnpmExecOutput = execSync(
      `pnpm -r ${pnpmFilter} --workspace-concurrency=1 exec 'bash' '-c' 'test -d env && pwd || true'`,
      {
        stdio: "pipe",
      }
    );
    return pnpmExecOutput.toString().trim().split("\n");
  } catch (e) {
    console.info(e.stdout.toString());
    process.exit(1);
  }
}

main();
