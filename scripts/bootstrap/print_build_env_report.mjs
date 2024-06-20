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

import * as path from "path";
import * as fs from "fs";
import { markdownTable } from "markdown-table";
import { requireEnv } from "@kie-tools-scripts/build-env/dist/lib.js";
import { getOrDefault } from "@kie-tools-scripts/build-env/dist/index.js";
import { pnpmFilter } from "./pnpm_filter.js";

let pnpmFilterString;
let outputFilePath;
let complete = false;
if (process.argv[2] === "--write-to") {
  outputFilePath = process.argv[3];
  pnpmFilterString = process.argv.slice(4).join(" ");
} else if (process.argv[2] === "--complete") {
  complete = true;
  pnpmFilterString = process.argv.slice(3).join(" ");
  outputFilePath = undefined;
} else {
  complete = false;
  pnpmFilterString = process.argv.slice(2).join(" ");
  outputFilePath = undefined;
}

async function main() {
  if (pnpmFilterString.length === 0) {
    console.info("[print-build-env-report] Printing build-env report of all packages...");
  } else {
    console.info(`[print-build-env-report] Printing build-env report of packages filtered by '${pnpmFilterString}'`);
  }

  // NOTE: This is not recursive as build-env
  const pkgDirsWithEnvDir = Object.keys(await pnpmFilter(pnpmFilterString, { alwaysIncludeRoot: false })).filter(
    (pkgPath) => fs.existsSync(path.resolve(pkgPath, "env"))
  );

  const pkgs = await Promise.all(
    pkgDirsWithEnvDir.map(async (pkgDir) => {
      return {
        env: await requireEnv(pkgDir),
        manifest: JSON.parse(fs.readFileSync(path.resolve(pkgDir, "package.json"), "utf-8")),
        dir: pkgDir,
      };
    })
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

  console.info("[print-build-env-report] Done.");
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
  return new Set([...(a ?? []), ...(b ?? [])]);
}

function treatVarToPrint(varWithName) {
  let value = getOrDefault(varWithName);
  if (varWithName.default === undefined && value) {
    value += " <- CHANGED 👀️ ";
  } else if (value === undefined) {
    value = "[unset] Default value may vary ⚠️ ";
  } else if (value !== varWithName.default) {
    value += " <- CHANGED 👀️ ";
  }
  return value;
}

await main();
