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

const { execSync } = require("child_process");
const fs = require("fs");
const path = require("path");
const replaceInFile = require("replace-in-file");
const { setupMavenConfigFile, buildTailFromPackageJsonDependencies } = require("@kie-tools/maven-base");

// Constants for dependent packages
const PYTHON_VENV_PKG = "@kie-tools/python-venv";
const IMAGE_COMMON_PKG = "@kie-tools/sonataflow-image-common";

/**
 * Shared install routine for bumping versions, configuring Maven and updating YAMLs/features.
 * @param {{ imageEnv: { buildTag:string, registry:string, account:string, name:string }, resourceDir?: string, finalImageName: string, requiresMvn: boolean }} options
 */
function runSharedInstall({ imageEnv, resourceDir = "./resources", finalImageName, requiresMvn = true }) {
  if (!finalImageName) {
    throw new Error("'finalImageName' parameter is required");
  }
  if (!imageEnv) {
    throw new Error("'imageEnv' parameter is required");
  }

  // 1) Maven revision and tail
  if (requiresMvn) {
    const { version } = require(path.resolve(__dirname, "package.json"));
    setupMavenConfigFile(
      `
  -Drevision=${version}
  -Dmaven.repo.local.tail=${buildTailFromPackageJsonDependencies()}
  `
    );
  }

  // 2) Activate Python venv and bump via versions_manager.py
  const pythonVenvDir = path.dirname(require.resolve(`${PYTHON_VENV_PKG}/package.json`));
  const imageCommonDir = path.dirname(require.resolve(`${IMAGE_COMMON_PKG}/package.json`));

  const activateCmd =
    process.platform === "win32"
      ? `${pythonVenvDir}\\venv\\Scripts\\Activate.bat`
      : `. ${pythonVenvDir}/venv/bin/activate`;

  execSync(
    `${activateCmd} && \
 python3 ${imageCommonDir}/resources/scripts/versions_manager.py \
   --bump-to ${imageEnv.buildTag} --source-folder ${resourceDir}`,
    { stdio: "inherit" }
  );

  // 3) Find, replace inside, and rename the *-image.yaml
  const files = fs.readdirSync(resourceDir);
  const yamlFile =
    files.find((f) => f.endsWith(`-${imageEnv.name}.yaml`)) || files.find((f) => f.endsWith("-image.yaml"));
  if (!yamlFile) {
    throw new Error(`No *-image.yaml found in ${resourceDir}`);
  }
  const yamlPath = path.join(resourceDir, yamlFile);
  let content = fs.readFileSync(yamlPath, "utf8");

  const imageUrl = `${imageEnv.registry}/${imageEnv.account}/${imageEnv.name}`;
  // Use the provided finalImageName to match and replace
  const regex = new RegExp(`(?<=").*${finalImageName}.*(?=")`, "g");
  content = content.replace(regex, imageUrl);

  fs.writeFileSync(yamlPath, content);
  fs.renameSync(yamlPath, path.join(resourceDir, `${imageEnv.name}-image.yaml`));

  // 4) Update any .feature files with the new image URL tag
  replaceInFile.sync({
    files: ["**/*.feature"],
    from: /@docker.io\/apache\/.*/g,
    to: `@${imageUrl}`,
  });
}

module.exports = { runSharedInstall };
