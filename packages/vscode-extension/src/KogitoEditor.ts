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
  ChannelType,
  EditorApi,
  EditorEnvelopeLocator,
  EnvelopeMapping,
  KogitoEditorChannelApi,
  KogitoEditorEnvelopeApi,
} from "@kie-tooling-core/editor/dist/api";
import { KogitoEditorStore } from "./KogitoEditorStore";
import { KogitoEditableDocument } from "./KogitoEditableDocument";
import { EnvelopeBusMessage } from "@kie-tooling-core/envelope-bus/dist/api";
import { EnvelopeBusMessageBroadcaster } from "./EnvelopeBusMessageBroadcaster";
import { EnvelopeServer } from "@kie-tooling-core/envelope-bus/dist/channel";

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
    private readonly envelopeServer = new EnvelopeServer<KogitoEditorChannelApi, KogitoEditorEnvelopeApi>(
      {
        postMessage: (msg) => {
          try {
            this.panel.webview.postMessage(msg);
          } catch (e) {
            /* The webview has been disposed, thus cannot receive messages anymore. */
          }
        },
      },
      envelopeLocator.targetOrigin,
      (self) =>
        self.envelopeApi.requests.kogitoEditor_initRequest(
          { origin: self.origin, envelopeServerId: self.id },
          {
            fileExtension: document.fileExtension,
            resourcesPathPrefix: envelopeMapping.resourcesPathPrefix,
            initialLocale: vscode.env.language,
            isReadOnly: false,
            channel: ChannelType.VSCODE,
          }
        )
    )
  ) {}

  public getElementPosition(selector: string) {
    return this.envelopeServer.envelopeApi.requests.kogitoGuidedTour_guidedTourElementPositionRequest(selector);
  }

  public getContent() {
    return this.envelopeServer.envelopeApi.requests.kogitoEditor_contentRequest().then((c) => c.content);
  }

  public setContent(path: string, content: string) {
    return this.envelopeServer.envelopeApi.requests.kogitoEditor_contentChanged({ path: path, content: content });
  }

  public async undo() {
    this.envelopeServer.envelopeApi.notifications.kogitoEditor_editorUndo();
  }

  public async redo() {
    this.envelopeServer.envelopeApi.notifications.kogitoEditor_editorRedo();
  }

  public getPreview() {
    return this.envelopeServer.envelopeApi.requests.kogitoEditor_previewRequest();
  }

  public validate() {
    return this.envelopeServer.envelopeApi.requests.kogitoEditor_validate();
  }

  public startInitPolling() {
    this.envelopeServer.startInitPolling();
  }

  public startListening(editorChannelApi: KogitoEditorChannelApi) {
    this.broadcastSubscription = this.messageBroadcaster.subscribe((msg) => {
      this.envelopeServer.receive(msg, editorChannelApi);
    });

    this.context.subscriptions.push(
      this.panel.webview.onDidReceiveMessage(
        (msg) => this.messageBroadcaster.broadcast(msg),
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

  public close() {
    this.panel.dispose();
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
