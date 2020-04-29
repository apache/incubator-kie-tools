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
  CustomDocumentEditEvent,
  CustomDocumentRevert,
  CustomEditorEditingDelegate
} from "vscode";
import { KogitoEditorStore } from "./KogitoEditorStore";
import { KogitoEdit } from "@kogito-tooling/core-api";

export class KogitoEditingDelegate implements CustomEditorEditingDelegate<KogitoEdit> {
  public readonly onDidEdit: vscode.Event<CustomDocumentEditEvent<KogitoEdit>>;

  private readonly editorStore: KogitoEditorStore;
  private readonly _onDidEdit = new vscode.EventEmitter<CustomDocumentEditEvent<KogitoEdit>>();

  public constructor(editorStore: KogitoEditorStore) {
    this.editorStore = editorStore;
    this.onDidEdit = this._onDidEdit.event;
  }

  public async save() {
    this.editorStore.withActive(activeEditor => activeEditor.requestSave());
    console.info("save");
  }

  public async saveAs(
    document: CustomDocument<KogitoEdit>,
    targetResource: vscode.Uri,
    cancellation: CancellationToken
  ) {
    console.info("saveAs");
  }

  public async undoEdits(document: CustomDocument<KogitoEdit>, edits: ReadonlyArray<KogitoEdit>) {
    console.info("undo");
    this.editorStore.withActive(activeEditor => activeEditor.notify_editorUndo(edits));
  }

  public async applyEdits(document: CustomDocument<KogitoEdit>, edits: ReadonlyArray<KogitoEdit>) {
    console.info("redo");
    this.editorStore.withActive(activeEditor => activeEditor.notify_editorRedo(edits));
  }

  public async backup(document: CustomDocument<KogitoEdit>, cancellation: CancellationToken) {
    console.info("backup");
  }

  public async revert(document: CustomDocument<KogitoEdit>, revert: CustomDocumentRevert<KogitoEdit>) {
    console.info("revert");
  }

  public notifyEdit(document: CustomDocument<KogitoEdit>, edit: KogitoEdit) {
    console.info('oi');
    this._onDidEdit.fire({ document, edit, label: "an edit" });
  }
}
