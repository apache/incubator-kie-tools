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

import * as path from "path";
import * as fs from "fs";
import { execSync } from "child_process";
import { markdownTable } from "markdown-table";
import { treatVarToPrint } from "../packages/build-env/dist/index.js";

let pnpmFilter;
let outputFilePath;
let complete = false;
if (process.argv[2] === "--write-to") {
  outputFilePath = process.argv[3];
  pnpmFilter = process.argv.slice(4).join(" ");
} else if (process.argv[2] === "--complete") {
  complete = true;
  pnpmFilter = process.argv.slice(3).join(" ");
  outputFilePath = undefined;
} else {
  complete = false;
  pnpmFilter = process.argv.slice(2).join(" ");
  outputFilePath = undefined;
}

if (pnpmFilter.length === 0) {
  console.info("[generate-build-env-report] Generating build-env report of all packages...");
} else {
  console.info(`[generate-build-env-report] Generating build-env report of packages filtered by '${pnpmFilter}'`);
}

async function main() {
  const pkgs = await Promise.all(
    findPathsOfPackagesWithEnvDir().map(async (pkgPath) => ({
      env: (await import(path.join(pkgPath, "env", "index.js"))).default,
      manifest: JSON.parse(fs.readFileSync(path.resolve(pkgPath, "package.json"), "utf-8")),
      dir: pkgPath,
    }))
  );

  const completeReport = buildVarsReport(pkgs);

  if (outputFilePath) {
    fs.writeFileSync(
      outputFilePath,
      markdownTable([
        ["Name", "Description", "Owner", "Default"],
        ...Object.values(completeReport).map((r) => [
          r.name,
          r.description,
          r.owner,
          r.default === undefined
            ? "_(undefined)_" //
            : r.default === ""
            ? "_(empty)_" //
            : r.default,
        ]),
      ])
    );
  } else if (complete) {
    console.info(completeReport);
  } else {
    const shortReport = Object.values(completeReport)
      .map((varr) => ({ [varr.name]: treatVarToPrint(varr) }))
      .reduce((acc, next) => ({ ...acc, ...next }), {});
    console.info(JSON.stringify(shortReport, undefined, 2));
  }

  console.info("[generate-build-env-report] Done.");
}

function findPathsOfPackagesWithEnvDir() {
  try {
    // NOTE: This is not recursive as build-env
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

function buildVarsReport(pkgs) {
  return pkgs
    .flatMap((pkg) => [
      ...Array.from(Object.values(pkg.env.self.vars)).map((v) => ({
        ...v,
        owner: [pkg.manifest.name],
        accessibleBy: [pkg.manifest.name],
      })),
      ...Array.from(Object.values(pkg.env.vars)).map((v) => ({
        ...v,
        owner: [],
        accessibleBy: [pkg.manifest.name],
      })),
    ])
    .reduce(
      (acc, nextVar) => ({
        ...acc,
        [nextVar.name]: {
          ...nextVar,
          owner: concatArraysWithoutDuplicates(acc[nextVar.name]?.owner, nextVar.owner),
          accessibleBy: concatArraysWithoutDuplicates(acc[nextVar.name]?.accessibleBy, nextVar.accessibleBy),
          currentValue: treatVarToPrint(nextVar),
        },
      }),
      {} // <-- start value for reduce
    );
}

function concatArraysWithoutDuplicates(a, b) {
  return [...new Set([...(a ?? []), ...(b ?? [])])];
}

await main();
