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
const path = require("path");
const buildEnv = require("./env");

const sonataflowPlatformFiles = fs
  .readdirSync(path.resolve(__dirname, "test/testdata"), {
    recursive: true,
  })
  .filter((fileName) => fileName.endsWith("02-sonataflow_platform.yaml"));
sonataflowPlatformFiles.forEach((filePath) => {
  const fullFilePath = path.resolve(__dirname, path.join("test/testdata"), filePath);
  fs.writeFileSync(
    fullFilePath,
    fs
      .readFileSync(fullFilePath, "utf-8")
      .replace(
        /org\.kie:kie-addons-quarkus-persistence-jdbc:[^,\n]*/,
        `org.kie:kie-addons-quarkus-persistence-jdbc:${buildEnv.env.kogitoRuntime.version}`
      )
      .replace(
        /org\.kie\.kogito:kogito-addons-quarkus-jobs-knative-eventing:[^,\n]*/,
        `org.kie.kogito:kogito-addons-quarkus-jobs-knative-eventing:${buildEnv.env.kogitoRuntime.version}`
      )
  );
});
