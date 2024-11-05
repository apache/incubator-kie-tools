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

import * as vscode from "vscode";
import { startExtension } from "@kie-tools-core/vscode-extension";
import * as path from "path";
import * as fs from "fs";
import { EditorEnvelopeLocator, EnvelopeContentType, EnvelopeMapping } from "@kie-tools-core/editor/dist/api";

export function activate(context: vscode.ExtensionContext) {
  console.info("Extension is alive.");

  /**
   * Starts the extension and set initial properties:
   *
   * @params args.extensionName The extension name.
   * @params args.context The VS Code extension context.
   * @params args.viewType The name of the view command to open the Editor.
   * @params args.generateSvgCommandId The name of the command to generate a SVG file. (it was set on the package.json)
   * @params args.silentlyGenerateSvgCommandId Same as above but without the toast notification.
   * @params args.editorEnvelopeLocator.targetOrigin The initial path of the envelope.
   * @params args.editorEnvelopeLocator.mapping A map associating a file extension with the respective envelope path and resources path.
   */
  startExtension({
    extensionName: "kie-tools-examples-base64png-editor-vscode-extension",
    context,
    viewType: "kieToolsExampleBase64PngEditor",
    generateSvgCommandId: "extension.kie.tools.examples.base64PngEditor.getPreviewSvg",
    silentlyGenerateSvgCommandId: "",
    editorEnvelopeLocator: new EditorEnvelopeLocator("vscode", [
      new EnvelopeMapping({
        type: "base64png",
        filePathGlob: "**/*.base64png",
        resourcesPathPrefix: "dist/",
        envelopeContent: { type: EnvelopeContentType.PATH, path: "dist/base64png-editor-envelope.js" },
      }),
    ]),
  });

  /**
   * Add a new command to VS Code. The command should be referenced on the package.json
   */
  context.subscriptions.push(
    /**
     * This command works on any png image. It will create a new file, it converts a PNG image to a `.base64png` file.
     *
     * To use it, can click on the Apache KIE icon at the top-right or use the VS Code command pallet.
     */
    vscode.commands.registerCommand(
      "extension.kie.tools.examples.base64PngEditor.createBase64Png",
      (file: { fsPath: string }) => {
        const buffer = fs.readFileSync(file.fsPath);
        const parsedPath = path.parse(file.fsPath);
        const base64FileName = `${parsedPath.name}${parsedPath.ext}.base64png`;
        const base64AbsoluteFsPath = path.join(parsedPath.dir, base64FileName);
        fs.writeFileSync(base64AbsoluteFsPath, buffer.toString("base64"));

        vscode.window
          .showInformationMessage("Generated Base64 PNG (.base64png) file successfully.", "Open")
          .then((selected) => {
            if (!selected) {
              return;
            }

            vscode.commands.executeCommand("vscode.open", vscode.Uri.file(base64AbsoluteFsPath));
          });
      }
    )
  );

  console.info("Extension is successfully setup.");
}

export function deactivate() {
  console.info("Extension is deactivating");
}
