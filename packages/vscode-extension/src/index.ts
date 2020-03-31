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
import { Router } from "@kogito-tooling/core-api";
import { VsCodeResourceContentService } from "./VsCodeResourceContentService";
import { KogitoWebviewProvider } from "./KogitoWebviewProvider";
import { KogitoEditingCapabilityFactory } from "./KogitoEditingCapabilityFactory";

/**
 * Starts a Kogito extension.
 *
 *  @param args.extensionName The extension name. Used to fetch the extension configuration for supported languages.
 *  @param args.webviewLocation The relative path to search for an "index.js" file for the WebView panel.
 *  @param args.context The vscode.ExtensionContext provided on the activate method of the extensin.
 *  @param args.router The Router to be used to find resources for each language.
 */
export function startExtension(args: {
  extensionName: string;
  webviewLocation: string;
  context: vscode.ExtensionContext;
  router: Router;
}) {
  const resourceContentService = new VsCodeResourceContentService();
  const editorStore = new KogitoEditorStore();
  const editorFactory = new KogitoEditorFactory(
    args.context,
    args.router,
    args.webviewLocation,
    editorStore,
    resourceContentService
  );
  const editingCapabilityFactory = new KogitoEditingCapabilityFactory(editorStore);
  const webviewProvider = new KogitoWebviewProvider(editorFactory, editingCapabilityFactory);

  args.context.subscriptions.push(webviewProvider.register());
  args.context.subscriptions.push(
    vscode.commands.registerCommand("extension.kogito.getPreviewSvg", () => {
      editorStore.withActive(e => e.requestPreview());
    })
  );
}

export * from "./DefaultVsCodeRouter";
