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

const { env } = require("./env");
const yaml = require("yaml");
const fs = require("fs");

// Set version for the Chart (and its dependencies) and Subcharts
console.log("[kie-sandbox-helm-chart install.js] Updating Chart.yaml files...");
const chartFiles = [
  "src/Chart.yaml",
  "src/charts/extended_services/Chart.yaml",
  "src/charts/cors_proxy/Chart.yaml",
  "src/charts/kie_sandbox/Chart.yaml",
];
chartFiles.forEach((file) => {
  const doc = yaml.parseDocument(fs.readFileSync(file, "utf8"));
  doc.setIn(["version"], env.kieSandboxHelmChart.tag);
  doc.setIn(["appVersion"], env.kieSandboxHelmChart.tag);
  if (doc.getIn(["dependencies"])) {
    doc.setIn(["dependencies", "0", "version"], env.kieSandboxHelmChart.tag);
    doc.setIn(["dependencies", "1", "version"], env.kieSandboxHelmChart.tag);
    doc.setIn(["dependencies", "2", "version"], env.kieSandboxHelmChart.tag);
  }
  console.log(yaml.stringify(doc));
  fs.writeFileSync(file, yaml.stringify(doc), "utf8");
});

// Set tags used for images
console.log("[kie-sandbox-helm-chart install.js] Updating values.yaml files...");
const valuesFiles = [
  "src/charts/extended_services/values.yaml",
  "src/charts/cors_proxy/values.yaml",
  "src/charts/kie_sandbox/values.yaml",
];
valuesFiles.forEach((file) => {
  const doc = yaml.parseDocument(fs.readFileSync(file, "utf8"));
  doc.setIn(["image", "tag"], env.root.streamName);
  console.log(yaml.stringify(doc));
  fs.writeFileSync(file, yaml.stringify(doc));
});

console.log("[kie-sandbox-helm-chart install.js] Done.");
