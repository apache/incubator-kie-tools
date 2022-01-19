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
import { registerTestScenarioRunnerCommand, VsCodeBackendProxy } from "@kie-tooling-core/backend/dist/vscode";
import { EditorEnvelopeLocator, EnvelopeMapping } from "@kie-tooling-core/editor/dist/api";
import { I18n } from "@kie-tooling-core/i18n/dist/core";
import * as KogitoVsCode from "@kie-tooling-core/vscode-extension";
import { VsCodeWorkspaceApi } from "@kie-tooling-core/workspace/dist/vscode";
import { VsCodeNotificationsApi } from "@kie-tooling-core/notifications/dist/vscode";
import * as vscode from "vscode";

let backendProxy: VsCodeBackendProxy;

export async function activate(context: vscode.ExtensionContext) {
  console.info("Extension is alive.");

  const envelopeTargetOrigin = "vscode";

  const workspaceApi = new VsCodeWorkspaceApi();
  const backendI18n = new I18n(backendI18nDefaults, backendI18nDictionaries, vscode.env.language);
  const notificationsApi = new VsCodeNotificationsApi(workspaceApi);
  backendProxy = new VsCodeBackendProxy(context, backendI18n, "kie-group.vscode-extension-backend");

  registerTestScenarioRunnerCommand({
    command: "extension.kogito.runTest",
    context: context,
    backendProxy: backendProxy,
    backendI18n: backendI18n,
    workspaceApi: workspaceApi,
    notificationsApi: notificationsApi,
  });

  KogitoVsCode.startExtension({
    extensionName: "kie-group.vscode-extension-pack-kogito-kie-editors",
    context: context,
    viewType: "kieKogitoWebviewEditors",
    generateSvgCommandId: "extension.kogito.getPreviewSvg",
    silentlyGenerateSvgCommandId: "extension.kogito.silentlyGenerateSvg",
    editorEnvelopeLocator: new EditorEnvelopeLocator(window.location.origin, [
      new EnvelopeMapping("**/*.bpmn?(2)", "dist/webview/editors/bpmn", "dist/webview/BpmnEditorEnvelopeApp.js"),
      new EnvelopeMapping("**/*.dmn", "dist/webview/editors/dmn", "dist/webview/DmnEditorEnvelopeApp.js"),
      new EnvelopeMapping("**/*.scesim", "dist/webview/editors/scesim", "dist/webview/SceSimEditorEnvelopeApp.js"),
      new EnvelopeMapping("**/*.pmml", "dist/webview/editors/pmml", "dist/webview/PMMLEditorEnvelopeApp.js"),
    ]),
    backendProxy: backendProxy,
  });

  console.info("Extension is successfully setup.");
}

export function deactivate() {
  backendProxy?.stopServices();
}
