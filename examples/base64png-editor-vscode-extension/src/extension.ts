/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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
import * as KogitoVsCode from "@kie-tools-core/vscode-extension";
import { VsCodeBackendProxy } from "@kie-tools-core/backend/dist/vscode";
import { I18n } from "@kie-tools-core/i18n/dist/core";
import { backendI18nDefaults, backendI18nDictionaries } from "@kie-tools-core/backend/dist/i18n";
import * as path from "path";
import * as fs from "fs";
import { EditorEnvelopeLocator, EnvelopeContentType, EnvelopeMapping } from "@kie-tools-core/editor/dist/api";

let backendProxy: VsCodeBackendProxy;

export function activate(context: vscode.ExtensionContext) {
  console.info("Extension is alive.");

  const backendI18n = new I18n(backendI18nDefaults, backendI18nDictionaries, vscode.env.language);
  backendProxy = new VsCodeBackendProxy(context, backendI18n);

  /**
   * Starts the extension and set initial properties:
   *
   * @params args.extensionName The extension name.
   * @params args.context The VS Code context.
   * @params args.viewType The name of the view command to open the Editor.
   * @params args.getPreviewCommandId The name of the command to generate a SVG file. (it was set on the package.json)
   * @params args.editorEnvelopeLocator.targetOrigin The initial path of the envelope.
   * @params args.editorEnvelopeLocator.mapping A map associating a file extension with the respective envelope path and resources path.
   */
  KogitoVsCode.startExtension({
    extensionName: "kie-tools-examples.base64png-editor-vscode-extension",
    context: context,
    viewType: "kieKogitoWebviewBase64PNGEditor",
    generateSvgCommandId: "extension.kogito.getPreviewSvg",
    silentlyGenerateSvgCommandId: "",
    editorEnvelopeLocator: new EditorEnvelopeLocator("vscode", [
      new EnvelopeMapping({
        type: "base64png",
        filePathGlob: "**/*.base64png",
        resourcesPathPrefix: "dist/",
        envelopeContent: { type: EnvelopeContentType.PATH, path: "dist/envelope/index.js" },
      }),
    ]),
    backendProxy: backendProxy,
  });

  /**
   * Add a new command to VS Code. The command should be referenced on the package.json
   */
  context.subscriptions.push(
    /**
     * This command works on any png image. It will create a new file, it converts a PNG image to a base64png file.
     *
     * To use it, can click on the Kogito icon on the top right or use the VS Code command pallet.
     */
    vscode.commands.registerCommand("extension.kogito.createBase64Png", (file: { fsPath: string }) => {
      const buffer = fs.readFileSync(file.fsPath);
      const parsedPath = path.parse(file.fsPath);
      const base64FileName = `${parsedPath.name}${parsedPath.ext}.base64png`;
      const base64AbsoluteFilePath = path.join(parsedPath.dir, base64FileName);
      fs.writeFileSync(base64AbsoluteFilePath, buffer.toString("base64"));

      vscode.window.showInformationMessage("Generated the Base64 file with success!", "Open").then((selected) => {
        if (!selected) {
          return;
        }

        vscode.commands.executeCommand("vscode.open", vscode.Uri.parse(base64AbsoluteFilePath));
      });
    })
  );

  console.info("Extension is successfully setup.");
}

export function deactivate() {
  console.info("Extension is deactivating");
  backendProxy?.stopServices();
}
