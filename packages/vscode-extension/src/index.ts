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
import { KogitoEditorStore } from "./KogitoEditorStore";
import { KogitoEditorFactory } from "./KogitoEditorFactory";
import { KogitoWebviewProvider } from "./KogitoWebviewProvider";
import { EditorEnvelopeLocator } from "@kogito-tooling/microeditor-envelope-protocol";
import * as __path from "path";
import * as fs from "fs";

/**
 * Starts a Kogito extension.
 *
 *  @param args.extensionName The extension name. Used to fetch the extension configuration for supported languages.
 *  @param args.webviewLocation The relative path to search for an "index.js" file for the WebView panel.
 *  @param args.context The vscode.ExtensionContext provided on the activate method of the extension.
 *  @param args.routes The routes to be used to find resources for each language.
 */
export function startExtension(args: {
  extensionName: string;
  context: vscode.ExtensionContext;
  viewType: string;
  getPreviewCommandId: string;
  editorEnvelopeLocator: EditorEnvelopeLocator;
}) {
  const editorStore = new KogitoEditorStore();
  const editorFactory = new KogitoEditorFactory(args.context, editorStore, args.editorEnvelopeLocator);
  const webviewProvider = new KogitoWebviewProvider(args.viewType, editorFactory, editorStore, args.context);

  args.context.subscriptions.push(webviewProvider.register());
  args.context.subscriptions.push(
    vscode.commands.registerCommand(args.getPreviewCommandId, () => {
      editorStore.withActive(async editor => {
        const previewSvg = await editor.getPreview();
        if (previewSvg) {
          const parsedPath = __path.parse(editor.document.uri.fsPath);
          fs.writeFileSync(`${parsedPath.dir}/${parsedPath.name}-svg.svg`, previewSvg);
        } else {
          console.info(`Unable to create SVG for '${editor.document.uri.fsPath}'`);
        }
      });
    })
  );
}
