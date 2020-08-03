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
import { KogitoEditorWebviewProvider } from "./KogitoEditorWebviewProvider";
import { EditorEnvelopeLocator } from "@kogito-tooling/editor/dist/api";
import { MyPageEnvelopeLocator } from "@kogito-tooling/my-page/dist/channel";
import { EnvelopeBusMessageBroadcaster } from "./EnvelopeBusMessageBroadcaster";
import { VsCodeWorkspaceApi } from "./VsCodeWorkspaceApi";
import { generateSvg } from "./generateSvg";
import { MyPageManager } from "./MyPageManager";

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
  pageEnvelopeLocator: MyPageEnvelopeLocator;
}) {
  const workspaceApi = new VsCodeWorkspaceApi();
  const editorStore = new KogitoEditorStore();
  const messageBroadcaster = new EnvelopeBusMessageBroadcaster();
  const editorFactory = new KogitoEditorFactory(
    args.context,
    editorStore,
    args.editorEnvelopeLocator,
    messageBroadcaster,
    workspaceApi
  );

  const pageManager = new MyPageManager(
    args.context,
    args.extensionName,
    args.pageEnvelopeLocator,
    editorStore,
    messageBroadcaster,
    workspaceApi
  );

  const editorWebviewProvider = new KogitoEditorWebviewProvider(
    args.context,
    args.viewType,
    editorStore,
    editorFactory
  );

  args.context.subscriptions.push(
    vscode.window.registerCustomEditorProvider(args.viewType, editorWebviewProvider, {
      webviewOptions: { retainContextWhenHidden: true }
    })
  );

  args.context.subscriptions.push(
    vscode.commands.registerCommand(args.getPreviewCommandId, () => generateSvg(editorStore, workspaceApi))
  );

  pageManager.pages.forEach(({ command, id }) => {
    args.context.subscriptions.push(vscode.commands.registerCommand(command, () => pageManager.open(id)));
  });
}
