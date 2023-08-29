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

const buildEnv = require("./env");
const path = require("path");
const fs = require("fs");
const prettier = require("prettier");

async function updateChromeExtensionManifest(version, manifestFilePath) {
  fs.writeFileSync(
    manifestFilePath,
    prettier.format(JSON.stringify({ ...require(manifestFilePath), version }), {
      ...(await prettier.resolveConfig(".")),
      parser: "json",
    })
  );
}

async function main() {
  console.info("[chrome-extension-pack-kogito-kie-editors-install] Updating  manifest files...");
  const version = buildEnv.env.chromeExtension.version;
  await updateChromeExtensionManifest(version, path.resolve("manifest.dev.json"));
  await updateChromeExtensionManifest(version, path.resolve("manifest.prod.json"));
  console.info("[chrome-extension-pack-kogito-kie-editors-install] Done.");
}

main();
