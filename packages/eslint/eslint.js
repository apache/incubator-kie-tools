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

const execSync = require("child_process").execSync;
const path = require("path");
const fs = require("fs");

const lintPath = path.resolve(process.argv[2]);
const configPath = require.resolve("./.eslintrc.js");
const potentialIgnorePath = path.resolve(".", ".eslintignore");

console.info("[kie-tools--eslint] Lint path: " + lintPath);
console.info("[kie-tools--eslint] Config path: " + configPath);

let ignorePathArgument = "";
if (fs.existsSync(potentialIgnorePath)) {
  console.info("[kie-tools--eslint] Ignore path: " + potentialIgnorePath);
  ignorePathArgument = `--ignore-path ${potentialIgnorePath}`;
}

try {
  execSync(`pnpm eslint ${lintPath} --ext .ts,.tsx --config ${configPath} ${ignorePathArgument}`, {
    stdio: "inherit",
    cwd: __dirname,
  });
} catch (e) {
  console.info("[kie-tools--eslint] Error.");
  process.exit(1);
}

console.info("[kie-tools--eslint] Done.");
