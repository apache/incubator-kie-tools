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
  console.info(`[generate-build-env-report] Generating build-env report of packages filtered by '${pnpmFilter}'`);
}

async function main() {
  const pkgs = findPathsOfPackagesWithEnvDir().map((pkgPath) => ({
    env: require(path.join(pkgPath, "env", "index.js")),
    manifest: require(path.join(pkgPath, "package.json")),
    dir: pkgPath,
  }));

  const varsReport = buildVarsReport(pkgs);
  const varsWithMultipleOwners = Array.from(Object.values(varsReport)).filter((varLine) => varLine.owner.size > 1);
  if (varsWithMultipleOwners.length > 0) {
    console.info("[generate-build-env-report] ERROR: Found env vars with multiple owners.");
    console.info(varsWithMultipleOwners);
    console.info("[generate-build-env-report] Done.");
    process.exit(1);
  }

  const envsReport = buildEnvsReport(pkgs);
  const envsWithConflictingRootObjectNames = [];
  if (envsWithConflictingRootObjectNames.length > 0) {
    console.info("[generate-build-env-report] ERROR: Found envs with conflicting root object names.");
    console.info(envsWithConflictingRootObjectNames);
    console.info("[generate-build-env-report] Done.");
    process.exit(1);
  }

  console.info(varsReport);
  console.info("[generate-build-env-report] Done.");
}

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

function buildEnvsReport(pkgs) {
  // TODO: tiago implement
}

function buildVarsReport(pkgs) {
  return pkgs
    .flatMap((pkg) => [
      ...Array.from(Object.keys(pkg.env.self.vars)).map((name) => ({
        ...pkg.env.self.vars[name],
        owner: [pkg.manifest.name],
        accessibleBy: [pkg.manifest.name],
      })),
      ...Array.from(Object.keys(pkg.env.vars)).map((name) => ({
        ...pkg.env.vars[name],
        owner: [],
        accessibleBy: [pkg.manifest.name],
      })),
    ])
    .reduce(
      (acc, next) => ({
        ...acc,
        [next.name]: {
          name: next.name,
          description: next.description,
          owner: new Set([...(acc[next.name]?.owner ?? []), ...(next.owner ?? [])]),
          accessibleBy: new Set([...(acc[next.name]?.accessibleBy ?? []), ...(next.accessibleBy ?? [])]),
        },
      }),
      {} // <-- start value for reduce
    );
}

main();
