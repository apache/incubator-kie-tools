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

/**
 * Install routine for Kogito images. Helps with bumping versions, configuring Maven, and updating YAMLs/features.
 * @param {{ imageTag: { buildTag:string, registry:string, account:string, name:string }, resourceDir?: string, finalImageName: string, requiresMvn: boolean, imagePkgDir: string }} options
 */
function runKogitoImageInstall({
  imageTag,
  resourceDir = "./resources",
  finalImageName,
  requiresMvn = true,
  imagePkgDir,
}) {
  if (!finalImageName) {
    throw new Error("'finalImageName' parameter is required");
  }
  if (!imageTag) {
    throw new Error("'imageTag' parameter is required");
  }

  // 1) Maven revision and tail
  if (requiresMvn) {
    const consumerVersion = require(path.join(imagePkgDir, "package.json")).version;
    setupMavenConfigFile(
      `
  -Drevision=${consumerVersion}
  -Dmaven.repo.local.tail=${buildTailFromPackageJsonDependencies(path.resolve(imagePkgDir))}
  `
    );
  }

  // 2) Activate Python venv and bump via versions_manager.py
  const pythonVenvDir = path.dirname(require.resolve("@kie-tools/python-venv/package.json"));
  const imageCommonDir = path.dirname(require.resolve("@kie-tools/sonataflow-image-common/package.json"));

  const activateCmd =
    process.platform === "win32"
      ? `${pythonVenvDir}\\venv\\Scripts\\Activate.bat`
      : `. ${pythonVenvDir}/venv/bin/activate`;

  execSync(
    `${activateCmd} && \
 python3 ${imageCommonDir}/resources/scripts/versions_manager.py \
   --bump-to ${imageTag.buildTag} --source-folder ${resourceDir}`,
    { stdio: "inherit" }
  );

  // 3) Find, replace inside, and rename the *-image.yaml
  const files = fs.readdirSync(resourceDir);
  const yamlFile =
    files.find((f) => f.endsWith(`-${imageTag.name}.yaml`)) || files.find((f) => f.endsWith("-image.yaml"));
  if (!yamlFile) {
    throw new Error(`No *-image.yaml found in ${resourceDir}`);
  }
  const yamlPath = path.join(resourceDir, yamlFile);
  let content = fs.readFileSync(yamlPath, "utf8");

  const imageUrl = `${imageTag.registry}/${imageTag.account}/${imageTag.name}`;
  // Use the provided finalImageName to match and replace
  const regex = new RegExp(`(?<=").*${finalImageName}.*(?=")`, "g");
  content = content.replace(regex, imageUrl);

  fs.writeFileSync(yamlPath, content);
  fs.renameSync(yamlPath, path.join(resourceDir, `${imageTag.name}-image.yaml`));

  // 4) Update any .feature files with the new image URL tag
  replaceInFile.sync({
    files: ["**/*.feature"],
    from: /@docker.io\/apache\/.*/g,
    to: `@${imageUrl}`,
  });
}

module.exports = { runKogitoImageInstall };
