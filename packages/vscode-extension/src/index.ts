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
import { Uri, ViewColumn } from "vscode";
import { KogitoEditorStore } from "./KogitoEditorStore";
import { KogitoEditorFactory } from "./KogitoEditorFactory";
import { KogitoWebviewProvider } from "./KogitoWebviewProvider";
import { EditorEnvelopeLocator, WorkspaceApi } from "@kogito-tooling/editor-envelope-protocol";
import * as __path from "path";
import * as fs from "fs";
import {
  KogitoPageChannelApi,
  KogitoPageChannelEnvelopeServer,
  PageEnvelopeLocator
} from "@kogito-tooling/page-envelope-protocol";
import { EnvelopeBusMessageBroadcaster } from "./EnvelopeBusMessageBroadcaster";
import { VsCodeWorkspaceApi } from "./VsCodeWorkspaceApi";
import { KogitoPageChannelApiImpl } from "./KogitoPageChannelApiImpl";

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
  pageEnvelopeLocator: PageEnvelopeLocator;
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
  const webviewProvider = new KogitoWebviewProvider(args.viewType, editorFactory, editorStore, args.context);

  args.context.subscriptions.push(webviewProvider.register());
  args.context.subscriptions.push(
    vscode.commands.registerCommand(args.getPreviewCommandId, async () => {
      const editor = editorStore.activeEditor;
      if (!editor) {
        console.info(`Unable to create SVG because there's no Editor open.`);
        return;
      }

      const previewSvg = await editor.getPreview();
      if (!previewSvg) {
        console.info(`Unable to create SVG for '${editor.document.uri.fsPath}'`);
        return;
      }

      const parsedPath = __path.parse(editor.document.uri.fsPath);
      fs.writeFileSync(`${parsedPath.dir}/${parsedPath.name}-svg.svg`, previewSvg);
    })
  );

  [...args.pageEnvelopeLocator.mapping.entries()].forEach(([pageId, pageMapping]) => {
    args.context.subscriptions.push(
      vscode.commands.registerCommand(`${args.extensionName}.pages.${pageId}`, () => {
        openPage(
          editorStore,
          args.context,
          messageBroadcaster,
          workspaceApi,

          pageId,
          pageMapping.title,
          ViewColumn.Beside,
          pageMapping.envelopePath,
          args.pageEnvelopeLocator.targetOrigin,
          pageMapping.backendUrl
        );
      })
    );
  });
}

function openPage(
  editorStore: KogitoEditorStore,
  context: vscode.ExtensionContext,
  messageBroadcaster: EnvelopeBusMessageBroadcaster,
  workspaceApi: WorkspaceApi,

  viewType: string,
  title: string,
  showOptions: ViewColumn.Beside,
  envelopePath: string,
  envelopeTargetOrigin: string,
  backendUrl: string
) {
  const filePath = editorStore.activeEditor?.document.uri.fsPath;

  const webviewPanel = vscode.window.createWebviewPanel(viewType, title, showOptions, {
    retainContextWhenHidden: true,
    enableCommandUris: true,
    enableScripts: true,
    localResourceRoots: [vscode.Uri.file(context.extensionPath)]
  });

  const scriptSrc = webviewPanel.webview.asWebviewUri(Uri.file(context.asAbsolutePath(envelopePath))).toString();

  webviewPanel.webview.html = `<!DOCTYPE html>
        <html lang="en">
        <head>
          <style>
            html, body, div#page-app {
                margin: 0;
                border: 0;
                padding: 0;
            }
          </style>
          <meta charset="UTF-8">
          <meta name="viewport" content="width=device-width, initial-scale=1.0">
          <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        </head>
        <body>
        <div id="page-app" />
        <script src="${scriptSrc}"></script>
        </body>
        </html>`;

  const envelopeServer = new KogitoPageChannelEnvelopeServer({
    postMessage: message => webviewPanel.webview.postMessage(message)
  });

  const api: KogitoPageChannelApi = new KogitoPageChannelApiImpl(workspaceApi, editorStore);

  const broadcastSubscription = messageBroadcaster.subscribe(msg => {
    envelopeServer.receive(msg, api);
  });

  context.subscriptions.push(
    webviewPanel.webview.onDidReceiveMessage(
      msg => messageBroadcaster.broadcast(msg),
      webviewPanel.webview,
      context.subscriptions
    )
  );

  webviewPanel.onDidDispose(
    () => {
      envelopeServer.stopInitPolling();
      messageBroadcaster.unsubscribe(broadcastSubscription);
    },
    webviewPanel.webview,
    context.subscriptions
  );

  envelopeServer.startInitPolling(envelopeTargetOrigin, {
    filePath: filePath,
    backendUrl: backendUrl
  });
}
