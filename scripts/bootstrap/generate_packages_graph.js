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

const graphviz = require("graphviz");
const DatavisTechGraph = require("graph-data-structure");
const findWorkspacePackages = require("@pnpm/find-workspace-packages").default;
const fs = require("fs");
const path = require("path");
const prettier = require("prettier");

const targetDir = process.argv[2];

async function main() {
  if (!targetDir) {
    console.error("[generate-packages-graph] Usage 'node generate_packages_graph.js [dir-path]'");
    process.exit(1);
  }

  const dotGraphFilePath = path.join(targetDir, "graph.dot");
  const datavisGraphFilePath = path.join(targetDir, "graph.json");
  console.info(`[generate-packages-graph] Writing packages DOT graph to '${dotGraphFilePath}'...`);

  const packages = await findWorkspacePackages(".");
  const packageMap = new Map(packages.map((p) => [p.manifest.name, p]));
  const packageNames = new Set(packages.map((p) => p.manifest.name));

  const adjMatrix = {};
  for (const pkg of packages) {
    adjMatrix[pkg.manifest.name] = adjMatrix[pkg.manifest.name] ?? {};
    const dependencies = Object.keys(pkg.manifest.dependencies ?? {}).sort();
    for (const depName of dependencies) {
      if (packageNames.has(depName)) {
        adjMatrix[pkg.manifest.name][depName] = "dependency";
      }
    }
    const devDependencies = Object.keys(pkg.manifest.devDependencies ?? {}).sort();
    for (const depName of devDependencies) {
      if (packageNames.has(depName)) {
        adjMatrix[pkg.manifest.name][depName] = "devDependency";
      }
    }
  }

  // transitive reduction
  const trMatrix = JSON.parse(JSON.stringify(adjMatrix));
  for (const s in trMatrix)
    for (const u in trMatrix)
      if (trMatrix[u][s] === "dependency" || trMatrix[u][s] === "devDependency" || trMatrix[u][s] === "transitive")
        for (const v in trMatrix)
          if (
            trMatrix[s][v] === "dependency" ||
            trMatrix[s][v] === "devDependency" ||
            trMatrix[s][v] === "transitive"
          ) {
            trMatrix[u][v] = "transitive";
          }

  const resMatrix = trMatrix;

  // print DOT graph
  const g = graphviz.digraph("G");

  g.use = "dot";
  g.set("ranksep", "2");
  g.set("splines", "polyline");
  g.set("rankdir", "TB");
  g.set("ordering", "out");

  g.setNodeAttribut("shape", "box");

  g.setEdgeAttribut("headport", "n");
  g.setEdgeAttribut("tailport", "s");
  g.setEdgeAttribut("arrowhead", "dot");
  g.setEdgeAttribut("arrowsize", "0.5");

  for (const pkgName in resMatrix) {
    const displayPkgName = pkgName;

    const pkgProperties = (() => {
      if (pkgName.startsWith("@kie-tools-examples") || pkgName.startsWith("kie-tools-examples-")) {
        return { color: "orange", nodeStyle: "dashed, rounded" };
      } else if (packageMap.get(pkgName)?.manifest.private) {
        return { color: "black", nodeStyle: "dashed, rounded" };
      } else if (pkgName.startsWith("@kie-tools-core")) {
        return { color: "purple", nodeStyle: "rounded" };
      } else {
        return { color: "blue", nodeStyle: "rounded" };
      }
    })();

    const node = g.addNode(displayPkgName);
    node.set("color", pkgProperties.color);
    node.set("fontcolor", pkgProperties.color);
    node.set("style", pkgProperties.nodeStyle);

    for (const depName in resMatrix[pkgName]) {
      const displayDepName = depName;
      if (resMatrix[pkgName][depName] === "dependency") {
        const edge = g.addEdge(displayPkgName, displayDepName, {});
        edge.set("style", "solid");
        edge.set("color", pkgProperties.color);
      } else if (resMatrix[pkgName][depName] === "devDependency") {
        const edge = g.addEdge(displayPkgName, displayDepName, {});
        edge.set("style", "dashed");
        edge.set("color", pkgProperties.color);
      } else if (resMatrix[pkgName][depName] === "transitive") {
        // ignore
      }
    }
  }

  if (!fs.existsSync(path.resolve(targetDir))) {
    fs.mkdirSync(path.resolve(targetDir), { recursive: true });
  }

  fs.writeFileSync(dotGraphFilePath, g.to_dot());
  console.info(`[generate-packages-graph] Wrote packages DOT graph to '${dotGraphFilePath}'`);

  console.info(`[generate-packages-graph] Writing packages Datavis graph to '${datavisGraphFilePath}'...`);
  const datavisGraph = DatavisTechGraph();

  for (const pkgName in resMatrix) {
    const pkg = packageMap.get(pkgName);
    const pkgNode = pkg.manifest.name;
    datavisGraph.addNode(pkgNode);

    for (const depName in resMatrix[pkgName]) {
      if (resMatrix[pkgName][depName] === "transitive") {
        continue;
      }

      const depPkg = packageMap.get(depName);
      const depNode = depPkg.manifest.name;
      datavisGraph.addEdge(pkgNode, depNode);
    }
  }

  fs.writeFileSync(
    datavisGraphFilePath,
    prettier.format(
      JSON.stringify({
        serializedDatavisGraph: datavisGraph.serialize(),
        serializedPackagesLocationByName: Array.from(packageMap.entries()).map(([k, v]) => [
          k,
          path.relative(path.resolve("."), v.dir).split(path.sep).join(path.posix.sep),
        ]),
      }),
      { ...(await prettier.resolveConfig(".")), parser: "json" }
    )
  );

  console.info(`[generate-packages-graph] Wrote packages Datavis graph to '${datavisGraphFilePath}'`);

  console.info(`[generate-packages-graph] Done.`);
  process.exit(0);
}

main();
