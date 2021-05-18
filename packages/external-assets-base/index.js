/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

const nodePath = require("path");
const nodeFs = require("fs");
const glob = require("glob");

/**
 * Two scenarios for nodeModulesDir:
 * (1) When using @kogito-tooling/external-assets-base library as dependency for other projects,
 *     __dirname is already on node_modules folder.
 * (2) When developing for kogito-tooling,
 *     external-assets-base is accessed directly so nodeModulesDir needs node_modules appended.
 */
const nodeModulesDir = "../.." + (__dirname.includes("node_modules") ? "" : "/node_modules");

module.exports = {
  dmnEditorPath: (argv) => {
    const path =
      (argv && argv["EXTERNAL_RESOURCE_PATH__dmnEditor"]) ||
      process.env["EXTERNAL_RESOURCE_PATH__dmnEditor"] ||
      nodePath.resolve(__dirname, nodeModulesDir + "/@kogito-tooling/dmn-editor-unpacked/target/dmn");

    if (!nodeFs.existsSync(path)) {
      throw new Error(`External asset :: DMN Editor path doesn't exist: ${path}`);
    }

    console.info(`External asset :: DMN Editor path: ${path}`);

    return path;
  },

  bpmnEditorPath: (argv) => {
    const path =
      (argv && argv["EXTERNAL_RESOURCE_PATH__bpmnEditor"]) ||
      process.env["EXTERNAL_RESOURCE_PATH__bpmnEditor"] ||
      nodePath.resolve(__dirname, nodeModulesDir + "/@kogito-tooling/bpmn-editor-unpacked/target/bpmn");

    if (!nodeFs.existsSync(path)) {
      throw new Error(`External asset :: BPMN Editor path doesn't exist: ${path}`);
    }

    console.info(`External asset :: BPMN Editor path: ${path}`);

    return path;
  },

  scesimEditorPath: (argv) => {
    const path =
      (argv && argv["EXTERNAL_RESOURCE_PATH__scesimEditor"]) ||
      process.env["EXTERNAL_RESOURCE_PATH__scesimEditor"] ||
      nodePath.resolve(__dirname, nodeModulesDir + "/@kogito-tooling/scesim-editor-unpacked/target/scesim");

    if (!nodeFs.existsSync(path)) {
      throw new Error(`External asset :: SceSim Editor path doesn't exist: ${path}`);
    }

    console.info(`External asset :: SceSim Editor path: ${path}`);

    return path;
  },

  quarkusRunnerPath: (argv) => {
    const path =
      (argv && argv["EXTERNAL_RESOURCE_PATH__quarkusRunner"]) ||
      process.env["EXTERNAL_RESOURCE_PATH__quarkusRunner"] ||
      nodePath.resolve(
        __dirname,
        nodeModulesDir +
          "/@kogito-tooling/quarkus-runner-unpacked/target/dependencies/kogito-extended-services-quarkus-*-runner.jar"
      );

    const matches = glob.sync(path);

    console.info(`Found ${matches.length} match(es) for External asset :: Quarkus Runner.`);

    if (matches.length <= 0) {
      throw new Error(`External asset :: Quarkus Runner path found no matches: ${path}`);
    } else if (matches.length > 1) {
      throw new Error(`External asset :: Quarkus Runner path found multiple matches: ${matches.join(", ")}`);
    }

    console.info(`External asset :: Quarkus Runner path: ${matches[0]}`);

    return matches[0];
  },
};
