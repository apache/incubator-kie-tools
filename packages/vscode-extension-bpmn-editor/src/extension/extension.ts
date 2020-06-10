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
import { GwtEditorRoutes } from "@kogito-tooling/kie-bc-editors";
import * as KogitoVsCode from "@kogito-tooling/vscode-extension";
import { DefaultVsCodeRouter } from "@kogito-tooling/vscode-extension";

export function activate(context: vscode.ExtensionContext) {
  console.info("Extension is alive.");

  KogitoVsCode.startExtension({
    extensionName: "kie-group.vscode-extension-bpmn-editor",
    webviewLocation: "dist/webview/index.js",
    context: context,
    viewType: "kieKogitoWebviewEditorsBpmn",
    getPreviewCommandId: "extension.kogito.getPreviewSvgBpmn",
    router: new DefaultVsCodeRouter(
      context,
      new GwtEditorRoutes({
        bpmnPath: "dist/webview/editors/bpmn",
        dmnPath: "",
        scesimPath: ""
      })
    )
  });

  console.info("Extension is successfully setup.");
}
