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
import { I18n } from "@kie-tools-core/i18n/dist/core";
import * as KogitoVsCode from "@kie-tools-core/vscode-extension";
import * as vscode from "vscode";
import { generateFormsCommand } from "@kie-tools/form-code-generator-vscode-command/dist/generateFormCodeCommand";

export function activate(context: vscode.ExtensionContext) {
  console.info("Extension is alive.");

  KogitoVsCode.startExtension({
    extensionName: "kie-group.bpmn-vscode-extension",
    context: context,
    viewType: "kieKogitoWebviewEditorsBpmn",
    generateSvgCommandId: "extension.kogito.getPreviewSvgBpmn",
    silentlyGenerateSvgCommandId: "extension.kogito.silentlyGenerateSvgBpmn",
    editorEnvelopeLocator: new EditorEnvelopeLocator("vscode", [
      new EnvelopeMapping({
        type: "bpmn",
        filePathGlob: "**/*.bpmn?(2)",
        resourcesPathPrefix: "dist/webview/editors/bpmn",
        envelopeContent: { type: EnvelopeContentType.PATH, path: "dist/webview/BpmnEditorEnvelopeApp.js" },
      }),
    ]),
  });

  context.subscriptions.push(
    vscode.commands.registerCommand("extension.apache.kie.bpmnEditor.generateFormCode", async (args: any) =>
      generateFormsCommand()
    )
  );

  KogitoVsCode.VsCodeRecommendation.showExtendedServicesRecommendation(context);

  console.info("Extension is successfully setup.");
}

export function deactivate() {
  console.info("Extension is deactivated.");
}
