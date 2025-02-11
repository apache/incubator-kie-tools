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

const { env } = require("./env");
const path = require("path");
const pythonVenvDir = path.dirname(require.resolve("@kie-tools/python-venv/package.json"));
const sonataflowImageCommonDir = path.dirname(require.resolve("@kie-tools/sonataflow-image-common/package.json"));
const replaceInFile = require("replace-in-file");

const activateCmd =
  process.platform === "win32"
    ? `${pythonVenvDir}\\venv\\Scripts\\Activate.bat`
    : `. ${pythonVenvDir}/venv/bin/activate`;

execSync(
  `${activateCmd} && \
  python3 ${sonataflowImageCommonDir}/resources/scripts/versions_manager.py --bump-to ${env.kogitoDbMigratorToolImage.buildTag} --source-folder ./resources`,
  { stdio: "inherit" }
);

// Find and read the -image.yaml file
const resourcesPath = path.resolve(__dirname, "./resources");
const files = fs.readdirSync(resourcesPath);
const imageYamlFiles = files.filter((fileName) => fileName.endsWith("image.yaml"));
if (imageYamlFiles.length !== 1) {
  throw new Error("There should only be one image.yaml file on ./resources!");
}
const originalYamlPath = path.join(resourcesPath, imageYamlFiles[0]);
let imageYaml = fs.readFileSync(originalYamlPath, "utf8");

const imageUrl = `${env.kogitoDbMigratorToolImage.registry}/${env.kogitoDbMigratorToolImage.account}/${env.kogitoDbMigratorToolImage.name}`;

// Replace the whole string between quotes ("") with the image name
imageYaml = imageYaml.replace(/(?<=")(.*kie-kogito-db-migrator-tool.*)(?=")/gm, imageUrl);

// Write file and then rename it to match the image name
fs.writeFileSync(originalYamlPath, imageYaml);
fs.renameSync(originalYamlPath, path.join(resourcesPath, `${env.kogitoDbMigratorToolImage.name}-image.yaml`));

// Replace image URL in .feature files
replaceInFile.sync({
  files: ["**/*.feature"],
  from: /@docker.io\/apache\/.*/g,
  to: `@${imageUrl}`,
});
