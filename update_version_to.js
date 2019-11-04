/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

const util = require("util");
const exec = util.promisify(require("child_process").exec);

const LERNA_JSON = "./lerna.json";

//

async function updatePackages(lernaVersionArg) {
  await exec(`npx lerna version ${lernaVersionArg} --no-push --no-git-tag-version --exact --yes`);
  return require(LERNA_JSON).version;
}

// MAIN

const lernaVersionArg = process.argv[2];
if (!lernaVersionArg) {
  console.error("Missing Lerna's version argument.");
  return 1;
}

function red(str) {
  return ["\x1b[31m", str, "\x1b[0m"];
}

Promise.resolve()
  .then(() => updatePackages(lernaVersionArg))
  .then(version => {
    console.error("");
    console.info(`Updated to '${version}'.`);
  })
  .catch(error => {
    console.error(error);
    console.error("");
    console.error(...red("Error updating versions. There might be undesired changes."));
  })
  .finally(() => {
    console.error("");
  });
