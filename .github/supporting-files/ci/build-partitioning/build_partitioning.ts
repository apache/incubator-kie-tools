#!/usr/bin/env bun

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

import { Partial, None, Full, PartitionDefinition } from "./types";
import { __ROOT_PKG_NAME, __NON_SOURCE_FILES_PATTERNS, __PACKAGES_ROOT_PATHS, stdoutArray } from "./globals";
import {
  assertLeafPackagesInPartitionDefinitionsDontOverlap,
  assertLeafPackagesInPartitionsExist,
  assertCompleteness,
  assertOptimalPartialBuild,
} from "./assertions";

import * as path from "path";
import * as fs from "fs";
import { execSync } from "child_process";
import { parseArgs } from "util";

const {
  values: {
    outputPath: __ARG_outputPath,
    forceFull: forceFull,
    baseSha: __ARG_baseSha,
    headSha: __ARG_headSha,
    graphJsonPath: __ARG_graphJsonPath,
    partition: __ARG_partitionFilePaths,
    tmpPartitionFilterPath: __ARG_tmpPartitionFilterPath,
  },
} = parseArgs({
  args: Bun.argv,
  options: {
    graphJsonPath: { type: "string" },
    baseSha: { type: "string" },
    headSha: { type: "string" },
    forceFull: { type: "string" },
    outputPath: { type: "string" },
    partition: { type: "string", multiple: true },
    tmpPartitionFilterPath: { type: "string", default: "/tmp/partition-filter.txt" },
  },
  strict: true,
  allowPositionals: true,
});

const absolutePosixRepoRootPath = execSync("bash -c pwd").toString().trim();

const __ARG_forceFull = forceFull === "true";

const partitions = await getPartitions();
const partitionsJson = JSON.stringify(partitions, null, 2);
console.log(``);
console.log(`[build-partitioning] --- PARTITIONS JSON ---`);
console.log(partitionsJson);

const resolvedOutputPath = path.resolve(".", __ARG_outputPath!);
fs.writeFileSync(resolvedOutputPath, partitionsJson);
console.log(`[build-partitioning] --> Written to '${resolvedOutputPath}'`);
console.log(`[build-partitioning] Done.`);

process.exit(0);

//

async function getPartitions(): Promise<Array<None | Full | Partial>> {
  console.log(``);
  console.log(`[build-partitioning] --- Summary ---`);
  console.log(`[build-partitioning] graphJsonPath:            ${__ARG_graphJsonPath}`);
  console.log(`[build-partitioning] baseSha:                  ${__ARG_baseSha}`);
  console.log(`[build-partitioning] headSha:                  ${__ARG_headSha}`);
  console.log(`[build-partitioning] forceFull:                ${forceFull}`);
  console.log(`[build-partitioning] outputPath:               ${__ARG_outputPath}`);
  console.log(`[build-partitioning] partition:                ${__ARG_partitionFilePaths}`);
  console.log(`[build-partitioning] tmpPartitionFilterPath:   ${__ARG_tmpPartitionFilterPath}`);
  console.log(`[build-partitioning] ---------------`);
  console.log(``);

  const graphJson = await import(path.resolve(".", __ARG_graphJsonPath!));
  const packageDirsByName = new Map<string, string>(graphJson.serializedPackagesLocationByName);
  const packageNamesByDir = new Map([...packageDirsByName.entries()].map(([k, v]) => [v, k]));

  const allLeafPackages = new Set(packageDirsByName.keys());
  for (const link of graphJson.serializedDatavisGraph.links) {
    allLeafPackages.delete(link.target);
  }
  allLeafPackages.delete(__ROOT_PKG_NAME);

  console.log(`[build-partitioning] All leaf packages:`);
  console.log(allLeafPackages);

  let leafPackagesDefinedByPartitions = new Set<string>();
  const partitionDefinitions: PartitionDefinition[] = await Promise.all(
    (__ARG_partitionFilePaths ?? []).map(async (partitionFile) => {
      const leafPackageNames = new Set(
        fs
          .readFileSync(path.resolve(".", partitionFile), "utf-8")
          .trim()
          .split("\n")
          .map((pkgName) => pkgName.trim())
      );

      leafPackagesDefinedByPartitions = new Set([...leafPackagesDefinedByPartitions, ...leafPackageNames]);

      return {
        name: partitionFile,
        leafPackageNames: leafPackageNames,
        dirs: await getDirsOfDependencies(leafPackageNames),
      };
    })
  );

  // Adds the remaining packages as a last partition
  const remainingLeafPackages = new Set(
    [...allLeafPackages].filter((leaf) => !leafPackagesDefinedByPartitions.has(leaf))
  );
  partitionDefinitions.push({
    name: "Partition N (remaining leaf packages)",
    leafPackageNames: remainingLeafPackages,
    dirs: await getDirsOfDependencies(remainingLeafPackages),
  });

  for (const p of partitionDefinitions) {
    console.log(`[build-partitioning] ${p.name}:`);
    console.log(p.leafPackageNames);
  }

  await assertLeafPackagesInPartitionDefinitionsDontOverlap({ allLeafPackages, partitionDefinitions });
  await assertLeafPackagesInPartitionsExist({
    packageNames: partitionDefinitions.flatMap((p) => [...p.leafPackageNames]),
    allLeafPackages,
  });

  const allPackageDirs = new Set(
    stdoutArray(await execSync(`bash -c "pnpm -F !${__ROOT_PKG_NAME}... exec bash -c pwd"`).toString()).map((pkgDir) =>
      convertToPosixPathRelativeToRepoRoot(pkgDir)
    )
  );

  await assertCompleteness({ packageDirsByName, partitions: partitionDefinitions, allPackageDirs });

  const nonSourceFilesPatternsForGitDiff = __NON_SOURCE_FILES_PATTERNS.map((p) => `':!${p}'`).join(" ");
  const changedSourcePaths = stdoutArray(
    execSync(
      `bash -c "git diff --name-only ${__ARG_baseSha} ${__ARG_headSha} -- ${nonSourceFilesPatternsForGitDiff}"`
    ).toString()
  );
  const changedPackages: Array<{ path: string; name: string }> = JSON.parse(
    execSync(`bash -c "turbo ls --filter='[${__ARG_baseSha}...${__ARG_headSha}]' --output json"`).toString()
  ).packages.items.map((item: { path: string; name: string }) => ({
    path: convertToPosixPathRelativeToRepoRoot(item.path),
    name: item.name,
  }));

  const changedPackagesDirs = changedPackages.map((item) => item.path);

  const changedPackagesNames = changedPackages.map((item) => item.name);

  console.log("[build-partitioning] Changed source paths:");
  console.log(new Set(changedSourcePaths));

  const changedSourcePathsInRoot = changedSourcePaths.filter((path) =>
    __PACKAGES_ROOT_PATHS.every((rootPath) => !path.startsWith(rootPath))
  );

  // On Windows there's a 8191 character limit for commands in PowerShell.
  // See https://learn.microsoft.com/en-us/troubleshoot/windows-client/shell-experience/command-line-string-limitation
  // To circunvent this, we break the package list in chunks, run turbo ls, and then merge them all to a final list.
  // Chunk size is defined as 50. This is an arbitrary value.
  // If each package name has 100 characters, 50 of them is 5000 characters, making the final command less than 8191.
  const changedPackagesNamesChunks: string[][] = [];
  const chunkSize = 50;
  for (let i = 0; i < changedPackagesNames.length; i += chunkSize) {
    changedPackagesNamesChunks.push(
      changedPackagesNames.slice(i, Math.min(i + chunkSize, changedPackagesNames.length))
    );
  }

  // Using a Set because it forces unique values, so no need to deduplicate.
  const affectedPackageDirsInAllPartitionsSet = new Set<string>();
  for (let packagesNamesChunk of changedPackagesNamesChunks) {
    await JSON.parse(
      execSync(
        `bash -c "turbo ls ${packagesNamesChunk.map((packageName) => `-F '...${packageName}'`).join(" ")} --output json"`
      ).toString()
    ).packages.items.forEach((item: { path: string }) => {
      affectedPackageDirsInAllPartitionsSet.add(convertToPosixPathRelativeToRepoRoot(item.path));
    });
  }
  const affectedPackageDirsInAllPartitions = Array.from(affectedPackageDirsInAllPartitionsSet);

  return await Promise.all(
    partitionDefinitions.map(async (partition) => {
      if (__ARG_forceFull || changedSourcePathsInRoot.length > 0) {
        console.log(`[build-partitioning] 'Full' build of '${partition.name}'.`);
        console.log(
          `[build-partitioning] Building ${partition.dirs.size}/${partition.dirs.size}/${allPackageDirs.size} packages.`
        );
        return {
          mode: "full",
          name: partition.name,
          bootstrapPnpmFilterString: [...partition.leafPackageNames].map((l) => `-F '${l}...'`).join(" "),
          fullBuildPnpmFilterString: [...partition.leafPackageNames].map((l) => `-F '${l}...'`).join(" "),
        };
      }

      const changedSourcePathsInPartition = changedPackagesDirs.filter((path) =>
        [...partition.dirs].some((partitionDir) => path === partitionDir)
      );

      if (changedSourcePathsInPartition.length === 0) {
        console.log(`[build-partitioning] 'None' build of '${partition.name}'.`);
        console.log(`[build-partitioning] Building 0/${partition.dirs.size}/${allPackageDirs.size} packages.`);
        console.log(``);
        return {
          mode: "none",
          name: partition.name,
        };
      }

      const affectedPackageNamesInPartition = new Set(
        affectedPackageDirsInAllPartitions
          .filter((pkgDir) => partition.dirs.has(pkgDir))
          .map((packageDir) => packageNamesByDir.get(packageDir)!)
      );

      const relevantPackageNamesInPartition = new Set(
        [...(await getDirsOfDependencies(affectedPackageNamesInPartition))].map(
          (pkgDir) => packageNamesByDir.get(pkgDir)!
        )
      );

      console.log(
        `[build-partitioning] Building ${relevantPackageNamesInPartition.size}/${partition.dirs.size}/${allPackageDirs.size} packages.`
      );
      console.log(relevantPackageNamesInPartition);

      const upstreamPackageNamesInPartition = new Set(
        [...relevantPackageNamesInPartition].filter((pkgName) => !affectedPackageNamesInPartition.has(pkgName))
      );

      await assertOptimalPartialBuild({
        partition,
        relevantPackageNamesInPartition,
        upstreamPackageNamesInPartition,
        affectedPackageNamesInPartition,
      });

      return {
        mode: "partial",
        name: partition.name,
        bootstrapPnpmFilterString: [...relevantPackageNamesInPartition].map((p) => `-F '${p}'`).join(" "),
        upstreamPnpmFilterString: [...upstreamPackageNamesInPartition].map((p) => `-F '${p}'`).join(" "),
        affectedPnpmFilterString: [...affectedPackageNamesInPartition].map((p) => `-F '${p}'`).join(" "),
      };
    })
  );
}

async function getDirsOfDependencies(leafPackageNames: Set<string>) {
  if (leafPackageNames.size === 0) {
    return new Set<string>();
  }
  const packagesFilter = [...leafPackageNames].map((pkgName) => `-F ${pkgName}...`).join(" ");

  // creating a file for the filters is required by windows CI to avoid the error "command line too long"
  fs.writeFileSync(__ARG_tmpPartitionFilterPath, packagesFilter);

  return new Set(
    stdoutArray(execSync(`bash -c "pnpm $(cat ${__ARG_tmpPartitionFilterPath}) exec bash -c pwd"`).toString()) //
      .map((pkgDir) => convertToPosixPathRelativeToRepoRoot(pkgDir))
  );
}

function convertToPosixPathRelativeToRepoRoot(targetPath: string) {
  // If it's not an absolute path we can assume it's relative to the repository root (the current directory).
  const isTargetPathAbsolute = targetPath.startsWith(path.sep) || targetPath.startsWith(path.posix.sep);

  // Replace separators to make sure they are posix style.
  return `${isTargetPathAbsolute ? path.relative(absolutePosixRepoRootPath, targetPath) : targetPath}`
    .split(path.sep)
    .join(path.posix.sep);
}
