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

const { spawn } = require("node:child_process");
const path = require("path");
const { env } = require("../env");

const buildEnv = env;

const mvn = spawn(
  "mvn",
  [
    "-f",
    path.join(__dirname, "./quarkus-app"),
    "clean",
    "quarkus:dev",
    "-Dmaven.test.skip",
    `-Dquarkus.platform.version=${buildEnv.quarkusPlatform.version}`,
    `-Dversion.org.kie.kogito=${buildEnv.kogitoRuntime.version}`,
    `-Dquarkus.http.port=${buildEnv.devDeploymentDmnFormWebapp.dev.quarkusPort}`,
    `-Dkogito.service.url=http://localhost:${buildEnv.devDeploymentDmnFormWebapp.dev.quarkusPort}`,
    "-Dquarkus.http.root-path=/",
  ],
  { shell: true }
);

mvn.stdout.on("data", (data) => {
  console.log(`[QUARKUS STDOUT]: ${data}`);
});

mvn.stderr.on("data", (data) => {
  console.error(`[QUARKUS STDERR]: ${data}`);
});

mvn.on("close", (code) => {
  console.log(`[QUARKUS CLOSE]: child process exited with code ${code}`);
});

mvn.on("error", (error) => {
  console.log(`[QUARKUS ERROR]: ${error}`);
});

let mode = "dev";
// check for --env arg and if the next arg is "live"
if (process.argv.indexOf("--env") !== -1 && process.argv[process.argv.indexOf("--env") + 1] === "live") {
  mode = "live";
}

const webpack = spawn(
  "npx",
  ["webpack", "serve", "-c", path.join(__dirname, "./webapp/webpack.config.js"), "--host", "0.0.0.0", "--env", mode],
  { shell: true }
);

webpack.stdout.on("data", (data) => {
  console.log(`[WEBPACK STDOUT]: ${data}`);
});

webpack.stderr.on("data", (data) => {
  console.error(`[WEBPACK STDERR]: ${data}`);
});

webpack.on("close", (code) => {
  console.log(`[WEBPACK CLOSE]: child process exited with code ${code}`);
});

webpack.on("error", (error) => {
  console.log(`[WEBPACK ERROR]: ${error}`);
});
