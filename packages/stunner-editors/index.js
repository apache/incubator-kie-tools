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

const nodePath = require("path");
const nodeFs = require("fs");

module.exports = {
  dmnEditorPath: () => {
    const path = nodePath.resolve(__dirname, "dist", "dmn");

    if (!nodeFs.existsSync(path)) {
      throw new Error(`Stunner Editors :: DMN Editor path doesn't exist: ${path}`);
    }

    console.info(`Stunner Editors :: DMN Editor path: ${path}`);

    return path;
  },

  bpmnEditorPath: () => {
    const path = nodePath.resolve(__dirname, "dist", "bpmn");

    if (!nodeFs.existsSync(path)) {
      throw new Error(`Stunner Editors :: BPMN Editor path doesn't exist: ${path}`);
    }

    console.info(`Stunner Editors :: BPMN Editor path: ${path}`);

    return path;
  },

  scesimEditorPath: () => {
    const path = nodePath.resolve(__dirname, "dist", "scesim");

    if (!nodeFs.existsSync(path)) {
      throw new Error(`Stunner Editors :: SceSim Editor path doesn't exist: ${path}`);
    }

    console.info(`Stunner Editors :: SceSim Editor path: ${path}`);

    return path;
  },
};
