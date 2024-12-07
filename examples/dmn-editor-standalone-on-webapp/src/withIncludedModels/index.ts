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

import * as DmnEditor from "@kie-tools/dmn-editor-standalone/dist";
import { DmnEditorStandaloneResource } from "@kie-tools/dmn-editor-standalone/dist";
import { loadFile } from "../fileLoader";

document.addEventListener("DOMContentLoaded", function () {
  loadEditor();
});

function loadEditor() {
  // Loads the DMN Editor in the `<div id="dmn-editor-container" />` element.
  // Initializes with an existing file called `can-drive.dmn` on the root of the
  // workspace.
  // The `loadFile` function loads files from the /static/models directory and returns
  // a Promise that resolves into a string.
  // In this case resources are added alongside the main content and can be used as
  // an included model.
  // The paths are important here! Since path2/loan-pre-qualification.dmn is in a
  // different parent path than the main content (path1/can-drive.dmn), it won't be availabe
  // to be used as an included model.
  // Examples:
  // | Path                           | Available |
  // | ------------------------------ | --------- |
  // | path1/test/myModel.dmn         |     ✓     |
  // | myTypes.dmn                    |     x     |
  // | path2/model.dmn                |     x     |
  // | path1/sample.dm                |     ✓     |
  // | ------------------------------ | --------- |
  const editor = DmnEditor.open({
    container: document.getElementById("dmn-editor-container")!,
    initialFileNormalizedPosixPathRelativeToTheWorkspaceRoot: "path1/can-drive.dmn",
    initialContent: loadFile("can-drive.dmn"),
    resources: new Map<string, DmnEditorStandaloneResource>([
      ["path1/can-drive-types.dmn", { contentType: "text", content: loadFile("can-drive-types.dmn") }],
      ["path2/loan-pre-qualification.dmn", { contentType: "text", content: loadFile("loan-pre-qualification.dmn") }], // Won't be available! Read comment above.
    ]),
    readOnly: false,
  });

  console.log({ editor });
}
