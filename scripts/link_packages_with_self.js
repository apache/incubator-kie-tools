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

const { getPackagesSync } = require("@lerna/project");
const fs = require("fs");
const path = require("path");

function main() {
  getPackagesSync().forEach((pkg) => {
    // always create node_modules. this fixes the case where we don't install dependencies for every package.
    fs.mkdirSync(path.join(pkg.location, "node_modules"), { recursive: true });

    const isScopedPackage = pkg.name.includes("@");
    if (isScopedPackage) {
      const [pkgScope, pkgSimpleName] = pkg.name.split("/");
      fs.mkdirSync(path.join(pkg.location, "node_modules", pkgScope), { recursive: true });
      const selfLinkPath = path.join(pkg.location, "node_modules", pkgScope, pkgSimpleName);
      selfLink(pkg, selfLinkPath);
      return;
    }

    const selfLinkPath = path.join(pkg.location, "node_modules", pkg.name);
    selfLink(pkg, selfLinkPath);
  });
  console.info(`[link-packages-with-self] Done.`);
  process.exit(0);
}

function selfLink(pkg, selfLinkPath) {
  const relTargetPath = path.relative(path.dirname(selfLinkPath), pkg.location);
  console.info(
    `[link-packages-with-self] Linking '${pkg.name}'. ${path.relative(pkg.location, selfLinkPath)} -> ${relTargetPath}`
  );
  if (fs.existsSync(selfLinkPath)) {
    fs.unlinkSync(selfLinkPath);
  }
  fs.symlinkSync(relTargetPath, selfLinkPath);
}

main();
