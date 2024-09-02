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
console.log("[runtime-tools-consoles-helm-chart install.js] Updating Chart.yaml files...");
const chartFiles = ["src/Chart.yaml", "src/charts/task-console/Chart.yaml", "src/charts/management-console/Chart.yaml"];
chartFiles.forEach((file) => {
  const doc = yaml.parseDocument(fs.readFileSync(file, "utf8"));
  if (file == "src/Chart.yaml") {
    doc.setIn(["name"], env.runtimeToolsConsolesHelmChart.name);
  }
  doc.setIn(["version"], env.runtimeToolsConsolesHelmChart.tag);
  doc.setIn(["appVersion"], env.runtimeToolsConsolesHelmChart.tag);
  if (doc.getIn(["dependencies"])) {
    doc.setIn(["dependencies", "0", "version"], env.runtimeToolsConsolesHelmChart.tag);
    doc.setIn(["dependencies", "1", "version"], env.runtimeToolsConsolesHelmChart.tag);
  }
  console.log(yaml.stringify(doc));
  fs.writeFileSync(file, yaml.stringify(doc), "utf8");
});

// Set tags used for images
console.log("[runtime-tools-consoles-helm-chart install.js] Updating values.yaml files...");
const valuesFiles = ["src/charts/task-console/values.yaml", "src/charts/management-console/values.yaml"];
valuesFiles.forEach((file) => {
  const doc = yaml.parseDocument(fs.readFileSync(file, "utf8"));
  doc.setIn(["image", "tag"], env.root.streamName);
  console.log(yaml.stringify(doc));
  fs.writeFileSync(file, yaml.stringify(doc));
});

console.log("[runtime-tools-consoles-helm-chart install.js] Done.");
