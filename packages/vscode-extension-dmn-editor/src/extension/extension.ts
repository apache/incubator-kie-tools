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

import { backendI18nDefaults, backendI18nDictionaries } from "@kie-tools-core/backend/dist/i18n";
import { VsCodeBackendProxy } from "@kie-tools-core/backend/dist/vscode";
import { I18n } from "@kie-tools-core/i18n/dist/core";
import * as KogitoVsCode from "@kie-tools-core/vscode-extension";
import * as vscode from "vscode";

let backendProxy: VsCodeBackendProxy;

export function activate(context: vscode.ExtensionContext) {
  console.info("Extension is alive.");

  const backendI18n = new I18n(backendI18nDefaults, backendI18nDictionaries, vscode.env.language);
  backendProxy = new VsCodeBackendProxy(context, backendI18n);

  const dmnEnvelope = {
    envelopePath: "dist/webview/DmnEditorEnvelopeApp.js",
    resourcesPathPrefix: "dist/webview/editors/dmn",
  };

  const scesimEnvelope = {
    envelopePath: "dist/webview/SceSimEditorEnvelopeApp.js",
    resourcesPathPrefix: "dist/webview/editors/scesim",
  };

  KogitoVsCode.startExtension({
    extensionName: "kie-group.vscode-extension-dmn-editor",
    context: context,
    viewType: "kieKogitoWebviewEditorsDmn",
    generateSvgCommandId: "extension.kogito.getPreviewSvgDmn",
    silentlyGenerateSvgCommandId: "extension.kogito.silentlyGenerateSvgDmn",
    editorEnvelopeLocator: {
      targetOrigin: "vscode",
      mapping: new Map([
        ["dmn", dmnEnvelope],
        ["DMN", dmnEnvelope],
        ["scesim", scesimEnvelope],
        ["SCESIM", scesimEnvelope],
      ]),
    },
    backendProxy: backendProxy,
  });

  console.info("Extension is successfully setup.");
}

export function deactivate() {
  backendProxy?.stopServices();
}
