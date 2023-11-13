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

const DatavisTechGraph = require("graph-data-structure");
const path = require("path");

const targetDir = process.argv[2];
const srcPackageNames = process.argv.slice(3);

function main() {
  if (!targetDir || !srcPackageNames || srcPackageNames.length === 0) {
    console.error(
      "[list-packages-dependencies] Usage 'node list_packages_dependencies.js [graph-json-dir-path] [src-pkg-names...]'"
    );
    process.exit(1);
  }

  const graphJson = require(path.resolve(path.join(targetDir, "graph.json")));
  const packagesLocationByName = new Map(graphJson.serializedPackagesLocationByName);

  const notFoundPackages = srcPackageNames.filter((p) => !packagesLocationByName.has(p));
  if (notFoundPackages.length > 0) {
    console.error("[list-packages-dependencies] Packages not found:");
    console.error(notFoundPackages.join("\n"));
    process.exit(1);
  }

  const datavisGraph = DatavisTechGraph();
  datavisGraph.deserialize(graphJson.serializedDatavisGraph);

  const dependencies = datavisGraph.depthFirstSearch(srcPackageNames, true, true);
  console.info(...dependencies.map((d) => packagesLocationByName.get(d)));

  process.exit(0);
}

main();
