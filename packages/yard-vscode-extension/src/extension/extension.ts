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
import * as KieToolsVsCodeExtensions from "@kie-tools-core/vscode-extension";
import * as vscode from "vscode";
import { YardVsCodeExtensionConfiguration, WEBVIEW_EDITOR_VIEW_TYPE } from "./configuration";
import { setupDiagramEditorControls } from "./setupDiagramEditorControls";

export async function activate(context: vscode.ExtensionContext) {
  console.info("Extension is alive.");

  const configuration = new YardVsCodeExtensionConfiguration();

  const kieEditorsStore = await KieToolsVsCodeExtensions.startExtension({
    editorDocumentType: "text",
    extensionName: "kie-group.yard-vscode-extension",
    context: context,
    viewType: WEBVIEW_EDITOR_VIEW_TYPE,
    editorEnvelopeLocator: new EditorEnvelopeLocator("vscode", [
      new EnvelopeMapping({
        type: "yard",
        filePathGlob: "**/*.yard.+(yml|yaml)",
        resourcesPathPrefix: "dist/webview/editors/yard",
        envelopeContent: { type: EnvelopeContentType.PATH, path: "dist/webview/YardEditorEnvelopeApp.js" },
      }),
    ]),
  });

  await setupDiagramEditorControls({
    context,
    configuration,
    kieEditorsStore,
  });

  console.info("Extension is successfully setup.");
}
