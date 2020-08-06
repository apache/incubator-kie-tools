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

import * as vscode from "vscode";
import {
  CancellationToken,
  CustomDocumentBackupContext,
  CustomDocumentEditEvent,
  CustomDocumentOpenContext,
  CustomEditorProvider,
  EventEmitter,
  Uri,
  WebviewPanel
} from "vscode";
import * as fs from "fs";
import {KogitoEditorFactory} from "./KogitoEditorFactory";
import {KogitoEditorStore} from "./KogitoEditorStore";
import {KogitoEditableDocument} from "./KogitoEditableDocument";

export class KogitoWebviewProvider implements CustomEditorProvider<KogitoEditableDocument> {
  private readonly _onDidChangeCustomDocument = new EventEmitter<CustomDocumentEditEvent<KogitoEditableDocument>>();
  public readonly onDidChangeCustomDocument = this._onDidChangeCustomDocument.event;

  public constructor(
    private readonly viewType: string,
    private readonly editorFactory: KogitoEditorFactory,
    private readonly editorStore: KogitoEditorStore,
    private readonly context: vscode.ExtensionContext
  ) {}

  public register() {
    return vscode.window.registerCustomEditorProvider(this.viewType, this, {
      webviewOptions: {
        retainContextWhenHidden: true
      }
    });
  }

  public async resolveCustomEditor(
    document: KogitoEditableDocument,
    webviewPanel: WebviewPanel,
    cancellation: CancellationToken
  ) {
    this.editorFactory.configureNew(webviewPanel, document);
  }

  public async openCustomDocument(uri: Uri, openContext: CustomDocumentOpenContext, cancellation: CancellationToken) {
    this.createStorageFolder();
    const document = new KogitoEditableDocument(uri, this.resolveBackup(openContext.backupId), this.editorStore);
    this.setupListeners(document);
    return document;
  }

  public async saveCustomDocument(document: KogitoEditableDocument, cancellation: CancellationToken) {
    return document.save(document.uri, cancellation);
  }

  public async saveCustomDocumentAs(
    document: KogitoEditableDocument,
    dest: Uri,
    cancellation: CancellationToken
  ) {
    return document.save(dest, cancellation);
  }

  public async revertCustomDocument(document: KogitoEditableDocument, cancellation: CancellationToken) {
    return document.revert(cancellation);
  }

  public backupCustomDocument(
    document: KogitoEditableDocument,
    context: CustomDocumentBackupContext,
    cancellation: CancellationToken
  ) {
    return document.backup(context.destination, cancellation);
  }

  private createStorageFolder() {
    const storagePath = this.context.storagePath ?? this.context.globalStoragePath;

    if (storagePath && !fs.existsSync(storagePath)) {
      fs.mkdirSync(storagePath);
    }
  }

  private setupListeners(document: KogitoEditableDocument) {
    const listeners = [document.onDidChange(e => this._onDidChangeCustomDocument.fire({ document, ...e }))];
    document.onDidDispose(() => listeners.forEach(listener => listener.dispose()));
  }

  private resolveBackup(backupId: string | undefined): Uri | undefined {
    if (!backupId || !fs.existsSync(backupId)) {
      return undefined;
    }

    return Uri.file(backupId);
  }
}
