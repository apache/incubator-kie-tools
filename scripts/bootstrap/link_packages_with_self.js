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
const fs = require("fs");
const path = require("path");

async function main() {
  (await findWorkspacePackages(".")).forEach((pkg) => {
    // always create node_modules. this fixes the case where we don't install dependencies for every package.
    fs.mkdirSync(path.join(pkg.dir, "node_modules"), { recursive: true });

    const isScopedPackage = pkg.manifest.name.includes("@");
    if (isScopedPackage) {
      const [pkgScope, pkgSimpleName] = pkg.manifest.name.split("/");
      fs.mkdirSync(path.join(pkg.dir, "node_modules", pkgScope), { recursive: true });
      const selfLinkPath = path.join(pkg.dir, "node_modules", pkgScope, pkgSimpleName);
      selfLink(pkg, selfLinkPath);
      return;
    }

    const selfLinkPath = path.join(pkg.dir, "node_modules", pkg.manifest.name);
    selfLink(pkg, selfLinkPath);
  });
  console.info(`[link-packages-with-self] Done.`);
  process.exit(0);
}

function selfLink(pkg, selfLinkPath) {
  const relTargetPath = path.relative(path.dirname(selfLinkPath), pkg.dir);
  console.info(
    `[link-packages-with-self] Linking '${pkg.manifest.name}'. ${path.relative(
      pkg.dir,
      selfLinkPath
    )} -> ${relTargetPath}`
  );
  if (fs.existsSync(selfLinkPath)) {
    fs.unlinkSync(selfLinkPath);
  }
  fs.symlinkSync(relTargetPath, selfLinkPath);
}

main();
