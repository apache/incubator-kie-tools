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
import { ColorThemeKind, TextDocument, UIKind } from "vscode";
import {
  ChannelType,
  EditorApi,
  EditorEnvelopeLocator,
  EditorTheme,
  EnvelopeContentType,
  EnvelopeMapping,
  KogitoEditorChannelApi,
  KogitoEditorEnvelopeApi,
} from "@kie-tools-core/editor/dist/api";
import { VsCodeKieEditorStore } from "./VsCodeKieEditorStore";
import { EnvelopeBusMessage } from "@kie-tools-core/envelope-bus/dist/api";
import { EnvelopeBusMessageBroadcaster } from "./EnvelopeBusMessageBroadcaster";
import { EnvelopeServer } from "@kie-tools-core/envelope-bus/dist/channel";
import { VsCodeKieEditorCustomDocument } from "./VsCodeKieEditorCustomDocument";
import * as __path from "path";
import { getWorkspaceRoot } from "./workspace/workspaceRoot";

function fileExtension(documentUri: vscode.Uri) {
  const lastSlashIndex = documentUri.fsPath.lastIndexOf("/");
  const fileName = documentUri.fsPath.substring(lastSlashIndex + 1);

  const firstDotIndex = fileName.indexOf(".");
  const fileExtension = fileName.substring(firstDotIndex + 1);

  return fileExtension;
}

export type KogitoEditorDocument =
  | {
      type: "text";
      document: TextDocument;
    }
  | {
      type: "custom";
      document: VsCodeKieEditorCustomDocument;
    };

const decoder = new TextDecoder("utf-8");

export class VsCodeKieEditorController implements EditorApi {
  private broadcastSubscription: (msg: EnvelopeBusMessage<unknown, any>) => void;
  private changeDocumentSubscription: vscode.Disposable | undefined;

  public constructor(
    public readonly document: KogitoEditorDocument,
    public readonly panel: vscode.WebviewPanel,
    private readonly context: vscode.ExtensionContext,
    private readonly editorStore: VsCodeKieEditorStore,
    private readonly envelopeMapping: EnvelopeMapping,
    private readonly envelopeLocator: EditorEnvelopeLocator,
    private readonly messageBroadcaster: EnvelopeBusMessageBroadcaster,
    public readonly envelopeServer = new EnvelopeServer<KogitoEditorChannelApi, KogitoEditorEnvelopeApi>(
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
            fileExtension: fileExtension(document.document.uri),
            resourcesPathPrefix: envelopeMapping.resourcesPathPrefix,
            initialLocale: vscode.env.language,
            isReadOnly: false,
            channel: vscode.env.uiKind === UIKind.Desktop ? ChannelType.VSCODE_DESKTOP : ChannelType.VSCODE_WEB,
            workspaceRootAbsolutePosixPath:
              // On VS Code web, when dangling files are open, `vscode.workspace.workspaceFolders` is [].
              vscode.workspace.workspaceFolders?.[0]?.uri.path ?? __path.dirname(document.document.uri.path),
          }
        )
    )
  ) {}

  private getEditorThemeByVscodeTheme(vscodeTheme: ColorThemeKind) {
    switch (vscodeTheme) {
      case ColorThemeKind.Dark:
        return EditorTheme.DARK;
      case ColorThemeKind.HighContrast:
        return EditorTheme.HIGH_CONTRAST;
      case ColorThemeKind.HighContrastLight:
        return EditorTheme.HIGH_CONTRAST_LIGHT;
      default:
        return EditorTheme.LIGHT;
    }
  }

  public getCurrentTheme() {
    return this.getEditorThemeByVscodeTheme(vscode.window.activeColorTheme.kind);
  }

  public getContent() {
    return this.envelopeServer.envelopeApi.requests.kogitoEditor_contentRequest().then((c) => c.content);
  }

  public setContent(normalizedPosixPathRelativeToTheWorkspaceRoot: string, content: string) {
    return this.envelopeServer.envelopeApi.requests.kogitoEditor_contentChanged(
      { normalizedPosixPathRelativeToTheWorkspaceRoot, content },
      { showLoadingOverlay: true }
    );
  }

  public async undo() {
    this.envelopeServer.envelopeApi.notifications.kogitoEditor_editorUndo.send();
  }

  public async redo() {
    this.envelopeServer.envelopeApi.notifications.kogitoEditor_editorRedo.send();
  }

  public getPreview() {
    return this.envelopeServer.envelopeApi.requests.kogitoEditor_previewRequest();
  }

  public validate() {
    return this.envelopeServer.envelopeApi.requests.kogitoEditor_validate();
  }

  public async setTheme(theme: EditorTheme) {
    return this.envelopeServer.shared.kogitoEditor_theme.set(theme);
  }

  public startInitPolling(apiImpl: KogitoEditorChannelApi) {
    this.envelopeServer.startInitPolling(apiImpl);
  }

  public startListening(apiImpl: KogitoEditorChannelApi) {
    this.broadcastSubscription = this.messageBroadcaster.subscribe((msg) => {
      this.envelopeServer.receive(msg, apiImpl);
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
    return this.document.document.uri === uri;
  }

  public isActive() {
    return this.panel.active;
  }

  public setupWebviewContent() {
    if (this.envelopeMapping.envelopeContent.type === EnvelopeContentType.PATH) {
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
          <script src="${this.envelopeMapping.envelopeContent?.path}"></script>
          </body>
          </html>
      `;
    }
  }

  public startListeningToThemeChanges() {
    const changeThemeSubscription = vscode.window.onDidChangeActiveColorTheme((colorTheme) => {
      return this.setTheme(this.getEditorThemeByVscodeTheme(colorTheme.kind));
    });

    // Make sure we get rid of the listener when our editor is closed.
    this.panel.onDidDispose(() => {
      changeThemeSubscription.dispose();
    });
  }

  public stopListeningToDocumentChanges() {
    this.changeDocumentSubscription?.dispose();
    this.changeDocumentSubscription = undefined;
  }

  public startListeningToDocumentChanges() {
    this.changeDocumentSubscription?.dispose();
    this.changeDocumentSubscription = vscode.workspace.onDidChangeTextDocument(async (e) => {
      if (e.document.uri.toString() !== this.document.document.uri.toString()) {
        return;
      }

      if (e.contentChanges.length <= 0) {
        return;
      }

      this.envelopeServer.envelopeApi.requests.kogitoEditor_contentChanged(
        {
          content: e.document.getText(),
          normalizedPosixPathRelativeToTheWorkspaceRoot: __path.posix.normalize(
            __path.relative(getWorkspaceRoot(e.document).workspaceRootAbsoluteFsPath, e.document.uri.fsPath)
          ),
        },
        { showLoadingOverlay: false }
      );
    });

    this.panel.onDidDispose(() => {
      this.changeDocumentSubscription?.dispose();
    });
  }

  public async getDocumentContent() {
    if (this.document.type === "custom") {
      const fileUri = this.document.document.initialBackup ?? this.document.document.uri;
      const contentArray = await vscode.workspace.fs.readFile(fileUri);
      return decoder.decode(contentArray);
    }

    if (this.document.type === "text") {
      return this.document.document.getText();
    }

    throw new Error("Document type not supported");
  }
}
