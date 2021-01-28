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
  CustomDocument,
  CustomDocumentBackup,
  CustomDocumentEditEvent,
  EventEmitter,
  Uri
} from "vscode";
import { KogitoEditorStore } from "./KogitoEditorStore";
import { KogitoEdit } from "@kogito-tooling/channel-common-api";
import { KogitoEditor } from "./KogitoEditor";
import { VsCodeI18n } from "./i18n";
import { I18n } from "@kogito-tooling/i18n/dist/core";
import * as nodePath from "path";

export class KogitoEditableDocument implements CustomDocument {
  private readonly encoder = new TextEncoder();
  private readonly decoder = new TextDecoder("utf-8");

  private readonly _onDidDispose = new EventEmitter<void>();
  public readonly onDidDispose = this._onDidDispose.event;

  private readonly _onDidChange = new EventEmitter<CustomDocumentEditEvent<KogitoEditableDocument>>();
  public readonly onDidChange = this._onDidChange.event;

  public constructor(
    public readonly uri: Uri,
    public readonly initialBackup: Uri | undefined,
    public readonly editorStore: KogitoEditorStore,
    private readonly vsCodeI18n: I18n<VsCodeI18n>
  ) {}

  public dispose() {
    this._onDidDispose.fire();
    this._onDidDispose.dispose();
    this._onDidChange.dispose();
  }

  get relativePath() {
    // For some reason, `asRelativePath` always returns paths with the '/' separator,
    // so on Windows, we need to replace it to the correct one, which is '\'.
    return vscode.workspace.asRelativePath(this.uri).replace("/", nodePath.sep);
  }

  get fileExtension() {
    return this.uri.fsPath.split(".").pop()!;
  }

  public async save(destination: Uri, cancellation: CancellationToken): Promise<void> {
    const i18n = this.vsCodeI18n.getCurrent();
    try {
      const editor = this.editorStore.get(this.uri);
      if (!editor) {
        console.error(`Cannot save because there's no open Editor for ${this.uri.fsPath}`);
        return;
      }

      const content = await editor.getContent();
      if (cancellation.isCancellationRequested) {
        return;
      }

      await vscode.workspace.fs.writeFile(destination, this.encoder.encode(content));
      vscode.window.setStatusBarMessage(i18n.savedSuccessfully, 3000);
    } catch (e) {
      console.error("Error saving.", e);
    }
  }

  public async backup(destination: Uri, cancellation: CancellationToken): Promise<CustomDocumentBackup> {
    const editor = this.editorStore.get(this.uri);
    if (!editor) {
      throw new Error(`Cannot proceed with backup. Editor is null for path ${this.uri.fsPath}.`);
    }

    const customDocumentBackup = {
      id: destination.fsPath,
      delete: () => vscode.workspace.fs.delete(destination)
    };

    if (cancellation.isCancellationRequested) {
      return customDocumentBackup;
    }

    const content = await editor.getContent();
    await vscode.workspace.fs.writeFile(destination, this.encoder.encode(content));
    console.info("Backup saved.");

    return customDocumentBackup;
  }

  public async revert(cancellation: CancellationToken): Promise<void> {
    const input = await vscode.workspace.fs.readFile(this.uri);
    const editor = this.editorStore.get(this.uri);
    if (editor) {
      return editor.setContent(this.relativePath, this.decoder.decode(input));
    }
  }

  public notifyEdit(editor: KogitoEditor, edit: KogitoEdit) {
    this._onDidChange.fire({
      label: "edit",
      document: this,
      undo: async () => editor.undo(),
      redo: async () => editor.redo()
    });
  }
}
