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

import * as vscode from "vscode";
import * as KogitoVsCode from "@kogito-tooling/vscode-extension";

export function activate(context: vscode.ExtensionContext) {
  console.info("Extension is alive.");

  KogitoVsCode.startExtension({
    extensionName: "kie-group.vscode-extension-pack-kogito-kie-editors",
    context: context,
    viewType: "kieKogitoWebviewEditors",
    getPreviewCommandId: "extension.kogito.getPreviewSvg",
    editorEnvelopeLocator: {
      targetOrigin: "vscode",
      mapping: new Map([
        ["bpmn", { resourcesPathPrefix: "dist/webview/editors/bpmn", envelopePath: "dist/webview/index.js" }],
        ["bpmn2", { resourcesPathPrefix: "dist/webview/editors/bpmn", envelopePath: "dist/webview/index.js" }],
        ["dmn", { resourcesPathPrefix: "dist/webview/editors/dmn", envelopePath: "dist/webview/index.js" }],
        ["scesim", { resourcesPathPrefix: "dist/webview/editors/scesim", envelopePath: "dist/webview/index.js" }]
      ])
    }
  });

  console.info("Extension is successfully setup.");
}

export function deactivate() {
  //FIXME: For some reason, this method is not being called :(
  console.info("Extension is deactivating");
}
