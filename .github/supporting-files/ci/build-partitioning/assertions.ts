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

import { PartitionDefinition } from "./types";

export async function assertLeafPackagesInPartitionsExist({
  packageNames,
  allLeafPackages,
}: {
  packageNames: string[];
  allLeafPackages: Set<string>;
}) {
  const nonLeafPackagesInPartitions = new Set(packageNames.filter((l) => !allLeafPackages.has(l)));
  console.log(
    `[build-partitioning] Partition definitions only use leaf packages (${
      nonLeafPackagesInPartitions.size > 0 ? "❌" : "✅"
    }):`
  );
  if (nonLeafPackagesInPartitions.size > 0) {
    console.error(`[build-partitioning] Non-leaf packages found in partition definitions. Aborting.`);
    console.error(nonLeafPackagesInPartitions);
    process.exit(1);
  }
}

export async function assertLeafPackagesInPartitionDefinitionsDontOverlap({
  allLeafPackages,
  partitionDefinitions,
}: {
  allLeafPackages: Set<string>;
  partitionDefinitions: PartitionDefinition[];
}) {
  const leafPackagesOverlap = partitionDefinitions.flatMap((s) => [...s.leafPackageNames]);
  const hasPartitionOverlap = allLeafPackages.size !== leafPackagesOverlap.length;

  console.log(`[build-partitioning] Partition definitions don't overlap (${hasPartitionOverlap ? "❌" : "✅"}):`);
  if (hasPartitionOverlap) {
    console.error(`[build-partitioning] Partitions definitions declare overlapping leaf packages. Aborting.`);
    console.error(leafPackagesOverlap);
    process.exit(1);
  }
}

export async function assertCompleteness({
  packageDirsByName,
  partitions,
  allPackageDirs,
}: {
  packageDirsByName: Map<string, string>;
  partitions: PartitionDefinition[];
  allPackageDirs: Set<string>;
}) {
  const partitionsByPkgDir = new Map<string, string[]>();
  for (const p of partitions) {
    for (const pkgDir of p.dirs) {
      partitionsByPkgDir.set(pkgDir, [...(partitionsByPkgDir.get(pkgDir) ?? []), p.name]);
    }
  }

  const redundancyOnPartitionCombinations = new Map<string, string[]>();
  partitionsByPkgDir.forEach((partitions, pkgDir) => {
    if (partitions.length > 1) {
      // We're only interested in packages that are going to be built as part of more than one partition.
      const key = partitions.sort().join(" & ");
      redundancyOnPartitionCombinations.set(key, [...(redundancyOnPartitionCombinations.get(key) ?? []), pkgDir]);
    }
  });

  console.log(`[build-partitioning] Packages that will be built by more than one partition:`);
  console.log(redundancyOnPartitionCombinations);

  const completenessCheck = [...allPackageDirs].filter((pkgDir) => !partitionsByPkgDir.has(pkgDir)).length <= 0;
  console.log(`[build-partitioning] Partition definitions completeness (${!completenessCheck ? "❌" : "✅"}):`);
  if (!completenessCheck) {
    console.error(`[build-partitioning] All packages count: ${allPackageDirs.size}`);
    for (const p of partitions) {
      console.error(`[build-partitioning] ${p.name} packages count: ${p.dirs.size}`);
    }
    process.exit(1);
  }
  return allPackageDirs;
}

export async function assertOptimalPartialBuild(args: {
  partition: PartitionDefinition;
  upstreamPackageNamesInPartition: Set<string>;
  affectedPackageNamesInPartition: Set<string>;
  relevantPackageNamesInPartition: Set<string>;
}) {
  const isOptimalPartialPartitionBuild =
    args.upstreamPackageNamesInPartition.size + args.affectedPackageNamesInPartition.size ===
    args.relevantPackageNamesInPartition.size;

  console.log(
    `[build-partitioning] 'Partial' build of '${args.partition.name}': Optimal build check ((${
      !isOptimalPartialPartitionBuild ? "❌" : "✅"
    }))`
  );
  if (!isOptimalPartialPartitionBuild) {
    console.error(`[build-partitioning] Non-optimal 'Partial' build. Aborting.`);
    process.exit(1);
  }
}
