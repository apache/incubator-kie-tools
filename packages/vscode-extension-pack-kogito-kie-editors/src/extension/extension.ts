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
import { registerTestScenarioRunnerCommand, VsCodeBackendProxy } from "@kie-tools-core/backend/dist/vscode";
import { I18n } from "@kie-tools-core/i18n/dist/core";
import * as KogitoVsCode from "@kie-tools-core/vscode-extension";
import { VsCodeWorkspaceApi } from "@kie-tools-core/workspace/dist/vscode";
import { VsCodeNotificationsApi } from "@kie-tools-core/notifications/dist/vscode";
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

  const bpmnEnvelope = {
    envelopePath: "dist/webview/BpmnEditorEnvelopeApp.js",
    resourcesPathPrefix: "dist/webview/editors/bpmn",
  };

  const dmnEnvelope = {
    resourcesPathPrefix: "dist/webview/editors/dmn",
    envelopePath: "dist/webview/DmnEditorEnvelopeApp.js",
  };

  const scesimEnvelope = {
    resourcesPathPrefix: "dist/webview/editors/scesim",
    envelopePath: "dist/webview/SceSimEditorEnvelopeApp.js",
  };

  const pmmlEnvelope = {
    resourcesPathPrefix: "dist/webview/editors/pmml",
    envelopePath: "dist/webview/PMMLEditorEnvelopeApp.js",
  };

  KogitoVsCode.startExtension({
    extensionName: "kie-group.vscode-extension-pack-kogito-kie-editors",
    context: context,
    viewType: "kieKogitoWebviewEditors",
    generateSvgCommandId: "extension.kogito.getPreviewSvg",
    silentlyGenerateSvgCommandId: "extension.kogito.silentlyGenerateSvg",
    editorEnvelopeLocator: {
      targetOrigin: envelopeTargetOrigin,
      mapping: new Map([
        ["bpmn", bpmnEnvelope],
        ["bpmn2", bpmnEnvelope],
        ["BPMN", bpmnEnvelope],
        ["BPMN2", bpmnEnvelope],
        ["dmn", dmnEnvelope],
        ["DMN", dmnEnvelope],
        ["scesim", scesimEnvelope],
        ["SCESIM", scesimEnvelope],
        ["pmml", pmmlEnvelope],
        ["PMML", pmmlEnvelope],
      ]),
    },
    backendProxy: backendProxy,
  });

  console.info("Extension is successfully setup.");
}

export function deactivate() {
  backendProxy?.stopServices();
}
