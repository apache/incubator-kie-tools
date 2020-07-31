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

import {
  CancellationToken,
  CustomDocument,
  CustomDocumentBackup,
  CustomDocumentEditEvent,
  EventEmitter,
  Uri
} from "vscode";
import { KogitoEditorStore } from "./KogitoEditorStore";
import { KogitoEdit } from "@kogito-tooling/microeditor-envelope-protocol";

export class KogitoEditableDocument implements CustomDocument {
  private readonly _onDidDispose = new EventEmitter<void>();
  public readonly onDidDispose = this._onDidDispose.event;

  private readonly _onDidChange = new EventEmitter<CustomDocumentEditEvent<KogitoEditableDocument>>();
  public readonly onDidChange = this._onDidChange.event;

  public constructor(
    public readonly uri: Uri,
    public readonly initialBackup: Uri | undefined,
    public readonly editorStore: KogitoEditorStore
  ) {}

  public dispose() {
    this._onDidDispose.fire();
    this._onDidDispose.dispose();
    this._onDidChange.dispose();
  }

  public async save(destination: Uri, cancellation: CancellationToken): Promise<void> {
    return this.editorStore.withUriAsync(this.uri, editor => editor.requestSave(destination, cancellation));
  }

  public async backup(destination: Uri, cancellation: CancellationToken): Promise<CustomDocumentBackup> {
    const editor = this.editorStore.get(this.uri);

    if (!editor) {
      throw new Error(`Cannot proceed with backup. Editor is null for path ${this.uri.fsPath}.`);
    }

    return editor.requestBackup(destination, cancellation).then(() => {
      return {
        id: destination.fsPath,
        delete: () => editor.deleteBackup(destination)
      };
    });
  }

  public async revert(cancellation: CancellationToken): Promise<void> {
    return this.editorStore.withActiveAsync(activeEditor => activeEditor.notify_editorRevert());
  }

  public notifyEdit(edit: KogitoEdit) {
    this._onDidChange.fire({
      label: "edit",
      document: this,
      undo: async () => this.editorStore.withActiveAsync(activeEditor => activeEditor.notify_editorUndo()),
      redo: async () => this.editorStore.withActiveAsync(activeEditor => activeEditor.notify_editorRedo())
    });
  }
}
