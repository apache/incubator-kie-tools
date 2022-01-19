/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import { backendI18nDefaults, backendI18nDictionaries } from "@kie-tooling-core/backend/dist/i18n";
import { VsCodeBackendProxy } from "@kie-tooling-core/backend/dist/vscode";
import { EditorEnvelopeLocator, EnvelopeMapping } from "@kie-tooling-core/editor/dist/api";
import { I18n } from "@kie-tooling-core/i18n/dist/core";
import * as KogitoVsCode from "@kie-tooling-core/vscode-extension";
import * as vscode from "vscode";

let backendProxy: VsCodeBackendProxy;

export function activate(context: vscode.ExtensionContext) {
  console.info("Extension is alive.");

  const backendI18n = new I18n(backendI18nDefaults, backendI18nDictionaries, vscode.env.language);
  backendProxy = new VsCodeBackendProxy(context, backendI18n);

  KogitoVsCode.startExtension({
    extensionName: "kie-group.vscode-extension-dmn-editor",
    context: context,
    viewType: "kieKogitoWebviewEditorsDmn",
    generateSvgCommandId: "extension.kogito.getPreviewSvgDmn",
    silentlyGenerateSvgCommandId: "extension.kogito.silentlyGenerateSvgDmn",
    editorEnvelopeLocator: new EditorEnvelopeLocator("vscode", [
      new EnvelopeMapping("**/*.dmn", "dist/webview/DmnEditorEnvelopeApp.js", "dist/webview/editors/dmn"),
      new EnvelopeMapping("**/*.scesim", "dist/webview/SceSimEditorEnvelopeApp.js", "dist/webview/editors/scesim"),
    ]),
    backendProxy: backendProxy,
  });

  console.info("Extension is successfully setup.");
}

export function deactivate() {
  backendProxy?.stopServices();
}
