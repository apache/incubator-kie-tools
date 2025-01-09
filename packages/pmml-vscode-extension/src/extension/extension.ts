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

import { EditorEnvelopeLocator, EnvelopeContentType, EnvelopeMapping } from "@kie-tools-core/editor/dist/api";
import * as KogitoVsCode from "@kie-tools-core/vscode-extension";
import * as vscode from "vscode";

export function activate(context: vscode.ExtensionContext) {
  console.info("Extension is alive.");

  KogitoVsCode.startExtension({
    extensionName: "kie-group.pmml-vscode-extension",
    context: context,
    viewType: "kieKogitoWebviewEditorsPmml",
    editorEnvelopeLocator: new EditorEnvelopeLocator("vscode", [
      new EnvelopeMapping({
        type: "pmml",
        filePathGlob: "**/*.pmml",
        resourcesPathPrefix: "dist/webview/editors/pmml",
        envelopeContent: { type: EnvelopeContentType.PATH, path: "dist/webview/PmmlEditorEnvelopeApp.js" },
      }),
    ]),
  });

  console.info("Extension is successfully setup.");
}

export function deactivate() {
  console.info("Extension is deactivated.");
}
