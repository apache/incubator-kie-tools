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
const gwtEditorMapping = require("@kie-tools/serverless-workflow-diagram-editor-envelope/dist/api/GwtEditorMapping");

module.exports = {
  swEditorPath: () => {
    const path = nodePath.resolve(__dirname, "dist", "sw");

    if (!nodeFs.existsSync(path)) {
      throw new Error(`Serverless Workflow Editor :: Serverless Editor path doesn't exist: ${path}`);
    }

    console.info(`Serverless Workflow Editor :: Serverless Editor path: ${path}`);

    return path;
  },
  swEditorFontsPath: () => {
    const path = nodePath.resolve(__dirname, "dist", "sw", gwtEditorMapping.editors.swf.name, "fonts");

    if (!nodeFs.existsSync(path)) {
      throw new Error(`Serverless Workflow Editor :: Serverless Editor fonts path doesn't exist: ${path}`);
    }

    console.info(`Serverless Workflow Editor :: Serverless Editor fonts path: ${path}`);

    return path;
  },
};
