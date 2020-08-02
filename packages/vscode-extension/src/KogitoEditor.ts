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
import {
  EditorEnvelopeLocator,
  EnvelopeMapping,
  KogitoEditorChannelEnvelopeServer,
  KogitoEditorChannelApi
} from "@kogito-tooling/editor-envelope-protocol";
import { KogitoEditorStore } from "./KogitoEditorStore";
import { EditorApi } from "@kogito-tooling/editor-api";
import { KogitoEditableDocument } from "./KogitoEditableDocument";
import { EnvelopeBusMessage } from "@kogito-tooling/envelope-bus/dist/api";
import { EnvelopeBusMessageBroadcaster } from "./EnvelopeBusMessageBroadcaster";

export class KogitoEditor implements EditorApi {
  private broadcastSubscription: (msg: EnvelopeBusMessage<unknown, any>) => void;

  public constructor(
    public readonly document: KogitoEditableDocument,
    private readonly panel: vscode.WebviewPanel,
    private readonly context: vscode.ExtensionContext,
    private readonly editorStore: KogitoEditorStore,
    private readonly envelopeMapping: EnvelopeMapping,
    private readonly envelopeLocator: EditorEnvelopeLocator,
    private readonly messageBroadcaster: EnvelopeBusMessageBroadcaster,
    private readonly envelopeServer = new KogitoEditorChannelEnvelopeServer({
      postMessage: msg => {
        console.info(`Posting: ${msg.type} -> busId: ${msg.busId} (posted by: ${this.document.uri.fsPath})`)
        this.panel.webview.postMessage(msg)
      }
    })
  ) {}

  public getElementPosition(selector: string) {
    return this.envelopeServer.request_guidedTourElementPositionResponse(selector);
  }

  public getContent() {
    return this.envelopeServer.request_contentResponse().then(c => c.content);
  }

  public async setContent(path: string, content: string) {
    this.envelopeServer.notify_contentChanged({ path: path, content: content });
  }

  public async undo() {
    this.envelopeServer.notify_editorUndo();
  }

  public async redo() {
    this.envelopeServer.notify_editorRedo();
  }

  public getPreview() {
    return this.envelopeServer.request_previewResponse();
  }

  public startInitPolling() {
    this.envelopeServer.startInitPolling(this.envelopeLocator.targetOrigin, {
      fileExtension: this.document.fileExtension,
      resourcesPathPrefix: this.envelopeMapping.resourcesPathPrefix
    });
  }

  public startListening(editorChannelApi: KogitoEditorChannelApi) {
    this.broadcastSubscription = this.messageBroadcaster.subscribe(msg => {
      this.envelopeServer.receive(msg, editorChannelApi);
    });

    this.context.subscriptions.push(
      this.panel.webview.onDidReceiveMessage(
        msg => {
          console.info(`Received: ${msg.type} -> busId: ${msg.busId} (received by id: ${this.envelopeServer.busId})`)
          this.messageBroadcaster.broadcast(msg);
        },
        this,
        this.context.subscriptions
      )
    );
  }

  public setupPanelActiveStatusChange() {
    this.panel.onDidChangeViewState(
      () => {
        if (this.panel.active) {
          this.editorStore.setActive(this);
        }

        if (!this.panel.active && this.editorStore.isActive(this)) {
          this.editorStore.setNoneActive();
        }
      },
      this,
      this.context.subscriptions
    );
  }

  public setupPanelOnDidDispose() {
    this.panel.onDidDispose(
      () => {
        this.envelopeServer.stopInitPolling();
        this.editorStore.close(this);
        this.messageBroadcaster.unsubscribe(this.broadcastSubscription);
      },
      this,
      this.context.subscriptions
    );
  }

  public hasUri(uri: vscode.Uri) {
    return this.document.uri === uri;
  }

  public isActive() {
    return this.panel.active;
  }

  public setupWebviewContent() {
    this.panel.webview.html = `
        <!DOCTYPE html>
        <html lang="en">
        <head>
            <style>
                html, body, div#envelope-app {
                    margin: 0;
                    border: 0;
                    padding: 0;
                    overflow: hidden;
                    height: 100%;
                }
                .panel-heading.uf-listbar-panel-header span {
                    color: white !important;
                }
                body {
                    background-color: #fff !important
                }
            </style>
        
            <title></title>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        </head>
        <body>
        <div id="envelope-app"></div>
        <script src="${this.envelopeMapping.envelopePath}"></script>
        </body>
        </html>
    `;
  }
}
