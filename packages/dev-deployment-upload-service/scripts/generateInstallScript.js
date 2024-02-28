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

const fs = require("fs");
const version = require("../package.json").version;
const devDeploymentUploadServiceEnv = require("../env");
const { argv } = require("process");

const baseInstallScriptFile = fs.readFileSync("getDevDeploymentUploadService.sh");

const contents = baseInstallScriptFile.toString();

const downloadPath = devDeploymentUploadServiceEnv.env.devDeploymentUploadService.url.path;
const downloadHost = devDeploymentUploadServiceEnv.env.devDeploymentUploadService.url.host;

let downloadUrl = `${downloadHost}/${downloadPath}`;
if (argv[2] === "--dev") {
  downloadUrl = `http://localhost:8090/${downloadPath}`;
}

if (!fs.existsSync("dist")) {
  fs.mkdirSync("dist");
}

fs.writeFileSync(
  "dist/getDevDeploymentUploadService.sh",
  contents.replace("<DOWNLOAD_URL>", downloadUrl).replace("<VERSION>", version)
);
fs.chmodSync("dist/getDevDeploymentUploadService.sh", "755");
