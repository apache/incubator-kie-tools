#!/usr/bin/env node

/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

const { execSync, spawn } = require("child_process");
const { existsSync } = require("fs");
const { program } = require("commander");

function packageJson(path) {
  return require(`${path}/package.json`);
}

function getOutputLines(buffer) {
  return buffer
    .toString()
    .trim()
    .split("\n");
}

function getLernasScopeListForRebuildingPackage(path) {
  if (!program.buildDependents) {
    return `--scope ${packageJson(path).name}`;
  }

  // Lists all dependent packages of package in `path`.
  const dependentPackagePaths = getOutputLines(
    execSync(`npx lerna exec "pwd" --scope ${packageJson(path).name} --include-dependents | grep -v "^lerna"`, {
      stdio: "pipe"
    })
  );

  // Finds intersection between App's dependencies and dependents of package in `path`.
  const packagesToRebuild = dependentPackagePaths
    .filter(p => appDependenciesPaths.indexOf(p) !== -1)
    .map(p => packageJson(p).name);

  // Maps the result as scope parameters to Lerna.
  return `${packagesToRebuild.map(p => `--scope ${p}`).join(" ")}`;
}

function watchSrcDirectoryOfPackage(path) {
  const pathToWatch = `${path}/src`;

  if (!existsSync(pathToWatch)) {
    console.info(`Can't watch ${pathToWatch} because it doesn't exist`);
    return;
  }

  const scopeList = getLernasScopeListForRebuildingPackage(path);
  const refreshCmmd = `npx lerna exec '${program.rebuildPackagesCmmd}' ${scopeList} --stream`;
  const invalidateCmmd = program.invalidateUrl
    ? `&& echo 'Invalidating app..' && sleep 5 && curl -s -X GET ${program.invalidateUrl} > /dev/null && echo 'Invalidated.'`
    : ``;

  spawn(`npx chokidar ${pathToWatch} -c "${refreshCmmd} ${invalidateCmmd}"`, [], {
    shell: true,
    stdio: "inherit"
  });
}

//
//
//MAIN

program.version(packageJson(".").version);
program.requiredOption("--app-package-name <name>", "The application package name");
program.requiredOption("--start-app-cmmd <cmmd>", "The command to start the application");
program.requiredOption("--rebuild-packages-cmmd <cmmd>", "The command to rebuild the dependency packages");
program.option("--no-build-dependents", "Signals not to build dependent packages after a change");
program.option("--invalidate-url <url>", "URL used to invalidate the running webapp and trigger a recompilation.");

program.parse(process.argv);

// Lists the paths of all dependency packages of `program.appPackageName`.
const appDependenciesPaths = getOutputLines(
  execSync(`npx lerna exec "pwd" --scope ${program.appPackageName} --include-dependencies | grep -v "^lerna"`, {
    stdio: "pipe"
  })
)
  // Excludes `program.appPackageName` from the list.
  .filter(path => packageJson(path).name !== program.appPackageName);

// Watches the `src` directory of the dependency packages of `program.appPackageName`.
appDependenciesPaths.forEach(watchSrcDirectoryOfPackage);

// Runs `program.appPackageName`.
spawn(program.startAppCmmd, [], { shell: true, stdio: "inherit" });

// Holds the process running
setInterval(() => {}, 1 << 30);

//TODO: Prevent `program.appPackageName` from recompiling when all packages are not done rebuilding yet.
