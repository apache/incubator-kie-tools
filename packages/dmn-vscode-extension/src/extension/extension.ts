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
    extensionName: "kie-group.dmn-vscode-extension",
    context: context,
    viewType: "kieKogitoWebviewEditorsDmn",
    generateSvgCommandId: "extension.kogito.getPreviewSvgDmn",
    silentlyGenerateSvgCommandId: "extension.kogito.silentlyGenerateSvgDmn",
    editorEnvelopeLocator: new EditorEnvelopeLocator("vscode", [
      new EnvelopeMapping({
        type: "dmn",
        filePathGlob: "**/*.dmn",
        resourcesPathPrefix: "dist/webview/editors/dmn",
        envelopeContent: { type: EnvelopeContentType.PATH, path: "dist/webview/DmnEditorEnvelopeApp.js" },
      }),
      new EnvelopeMapping({
        type: "scesim",
        filePathGlob: "**/*.scesim",
        resourcesPathPrefix: "dist/webview/editors/scesim",
        envelopeContent: { type: EnvelopeContentType.PATH, path: "dist/webview/SceSimEditorEnvelopeApp.js" },
      }),
    ]),
  });

  KogitoVsCode.startExtension({
    extensionName: "kie-group.dmn-vscode-extension",
    context: context,
    viewType: "kieToolsDmnEditor",
    generateSvgCommandId: "extension.kie.tools.generatePreviewSvgDmn",
    silentlyGenerateSvgCommandId: "extension.kie.tools.silentlyGenerateSvgDmn",
    editorEnvelopeLocator: new EditorEnvelopeLocator("vscode", [
      new EnvelopeMapping({
        type: "dmn",
        filePathGlob: "**/*.dmn",
        resourcesPathPrefix: "",
        envelopeContent: { type: EnvelopeContentType.PATH, path: "dist/webview/NewDmnEditorEnvelopeApp.js" },
      }),
      new EnvelopeMapping({
        type: "scesim",
        filePathGlob: "**/*.scesim",
        resourcesPathPrefix: "",
        envelopeContent: { type: EnvelopeContentType.PATH, path: "dist/webview/NewTestScenarioEditorEnvelopeApp.js" },
      }),
    ]),
  });

  KogitoVsCode.VsCodeRecommendation.showExtendedServicesRecommendation(context);

  console.info("Extension is successfully setup.");
}

export function deactivate() {
  console.info("Extension is deactivated.");
}
