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

import { KogitoEditorStore } from "./KogitoEditorStore";
import * as vscode from "vscode";
import { Uri, ViewColumn } from "vscode";
import { EnvelopeBusMessageBroadcaster } from "./EnvelopeBusMessageBroadcaster";
import { WorkspaceApi } from "@kogito-tooling/editor-envelope-protocol";
import { KogitoPageChannelApiImpl } from "./KogitoPageChannelApiImpl";
import {MyPageChannelEnvelopeServer, MyPageEnvelopeLocator} from "@kogito-tooling/my-page/dist/channel";
import {MyPageChannelApi} from "@kogito-tooling/my-page/dist/api";

export class MyPageManager {
  constructor(
    private readonly context: vscode.ExtensionContext,
    private readonly extensionName: string,
    private readonly myPageEnvelopeLocator: MyPageEnvelopeLocator,
    private readonly editorStore: KogitoEditorStore,
    private readonly messageBroadcaster: EnvelopeBusMessageBroadcaster,
    private readonly workspaceApi: WorkspaceApi
  ) {}

  get pages() {
    return [...this.myPageEnvelopeLocator.mapping.entries()].map(([pageId]) => ({
      id: pageId,
      command: `${this.extensionName}.pages.${pageId}`
    }));
  }

  public open(pageId: string) {
    const pageMapping = this.myPageEnvelopeLocator.mapping.get(pageId);
    if (!pageMapping) {
      throw new Error(`Cannot open page '${pageId}'`);
    }

    const filePath = this.editorStore.activeEditor?.document.uri.fsPath;

    const webviewPanel = vscode.window.createWebviewPanel(pageId, pageMapping.title, ViewColumn.Beside, {
      retainContextWhenHidden: true,
      enableCommandUris: true,
      enableScripts: true,
      localResourceRoots: [vscode.Uri.file(this.context.extensionPath)]
    });

    const scriptSrc = webviewPanel.webview
      .asWebviewUri(Uri.file(this.context.asAbsolutePath(pageMapping.envelopePath)))
      .toString();

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

    const envelopeServer = new MyPageChannelEnvelopeServer({
      postMessage: message => webviewPanel.webview.postMessage(message)
    });

    const api: MyPageChannelApi = new KogitoPageChannelApiImpl(this.workspaceApi, this.editorStore);

    const broadcastSubscription = this.messageBroadcaster.subscribe(msg => {
      envelopeServer.receive(msg, api);
    });

    this.context.subscriptions.push(
      webviewPanel.webview.onDidReceiveMessage(
        msg => this.messageBroadcaster.broadcast(msg),
        webviewPanel.webview,
        this.context.subscriptions
      )
    );

    webviewPanel.onDidDispose(
      () => {
        envelopeServer.stopInitPolling();
        this.messageBroadcaster.unsubscribe(broadcastSubscription);
      },
      webviewPanel.webview,
      this.context.subscriptions
    );

    envelopeServer.startInitPolling(this.myPageEnvelopeLocator.targetOrigin, {
      filePath: filePath,
      backendUrl: pageMapping.backendUrl
    });
  }
}
