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

const findWorkspacePackages = require("@pnpm/find-workspace-packages").default;

async function main() {
  const packages = await findWorkspacePackages(".");
  const packagesByName = new Map(
    packages.map((p) => [
      p.manifest.name,
      {
        private: p.manifest.private,
        dependencies: Object.keys(p.manifest.dependencies ?? {}).sort(),
      },
    ])
  );

  const invalidDependencies = Array.from(packagesByName.entries())
    .filter(([pkgName, pkg]) => !pkg.private)
    .flatMap(([pkgName, pkg]) =>
      pkg.dependencies.filter((dep) => packagesByName.get(dep)?.private).map((dep) => [pkgName, dep])
    );

  if (invalidDependencies.length > 0) {
    console.error(`[check-packages-dependencies] There are public packages depending on private packages:`);
    invalidDependencies.forEach(([pkgName, dep]) => {
      console.error(`[check-packages-dependencies] ${pkgName} -> ${dep}`);
    });
    process.exit(1);
  }

  console.info(`[check-packages-dependencies] Done.`);
  process.exit(0);
}

main();
