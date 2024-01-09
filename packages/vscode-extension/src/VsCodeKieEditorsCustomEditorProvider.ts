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
import {
  CancellationToken,
  CustomDocumentBackupContext,
  CustomDocumentEditEvent,
  CustomDocumentOpenContext,
  CustomEditorProvider,
  EventEmitter,
  Uri,
  WebviewPanel,
} from "vscode";
import { VsCodeKieEditorControllerFactory } from "./VsCodeKieEditorControllerFactory";
import { VsCodeKieEditorStore } from "./VsCodeKieEditorStore";
import { VsCodeKieEditorCustomDocument } from "./VsCodeKieEditorCustomDocument";
import { VsCodeI18n } from "./i18n";
import { I18n } from "@kie-tools-core/i18n/dist/core";
import { EditorEnvelopeLocator } from "@kie-tools-core/editor/dist/api";
import { VsCodeNotificationsChannelApiImpl } from "./notifications/VsCodeNotificationsChannelApiImpl";

export class VsCodeKieEditorsCustomEditorProvider implements CustomEditorProvider<VsCodeKieEditorCustomDocument> {
  private readonly _onDidChangeCustomDocument = new EventEmitter<
    CustomDocumentEditEvent<VsCodeKieEditorCustomDocument>
  >();
  public readonly onDidChangeCustomDocument = this._onDidChangeCustomDocument.event;

  public constructor(
    private readonly context: vscode.ExtensionContext,
    private readonly viewType: string,
    private readonly editorStore: VsCodeKieEditorStore,
    private readonly editorFactory: VsCodeKieEditorControllerFactory,
    private readonly vsCodeI18n: I18n<VsCodeI18n>,
    private readonly vscodeNotifications: VsCodeNotificationsChannelApiImpl,
    private readonly editorEnvelopeLocator: EditorEnvelopeLocator
  ) {}

  public register() {
    return vscode.window.registerCustomEditorProvider(this.viewType, this, {
      webviewOptions: {
        retainContextWhenHidden: true,
      },
    });
  }

  public async resolveCustomEditor(
    document: VsCodeKieEditorCustomDocument,
    webviewPanel: WebviewPanel,
    cancellation: CancellationToken
  ) {
    this.editorFactory.configureNew(webviewPanel, { type: "custom", document });
  }

  public async openCustomDocument(uri: Uri, openContext: CustomDocumentOpenContext, cancellation: CancellationToken) {
    await this.createStorageFolder();
    const document = new VsCodeKieEditorCustomDocument(
      uri,
      this.resolveBackupUri(openContext.backupId),
      this.editorStore,
      this.vsCodeI18n,
      this.vscodeNotifications,
      this.editorEnvelopeLocator
    );
    this.setupListeners(document);
    return document;
  }

  public async saveCustomDocument(document: VsCodeKieEditorCustomDocument, cancellation: CancellationToken) {
    return document.save(document.uri, cancellation);
  }

  public async saveCustomDocumentAs(
    document: VsCodeKieEditorCustomDocument,
    dest: Uri,
    cancellation: CancellationToken
  ) {
    return document.save(dest, cancellation);
  }

  public async revertCustomDocument(document: VsCodeKieEditorCustomDocument, cancellation: CancellationToken) {
    return document.revert(cancellation);
  }

  public backupCustomDocument(
    document: VsCodeKieEditorCustomDocument,
    context: CustomDocumentBackupContext,
    cancellation: CancellationToken
  ) {
    return document.backup(context.destination, cancellation);
  }

  private async createStorageFolder() {
    await vscode.workspace.fs.createDirectory(this.context.storageUri ?? this.context.globalStorageUri);
  }

  private setupListeners(document: VsCodeKieEditorCustomDocument) {
    const listeners = [document.onDidChange((e) => this._onDidChangeCustomDocument.fire({ ...e, document }))];
    document.onDidDispose(() => listeners.forEach((listener) => listener.dispose()));
  }

  private resolveBackupUri(backupId: string | undefined): Uri | undefined {
    return backupId ? Uri.file(backupId) : undefined;
  }
}
