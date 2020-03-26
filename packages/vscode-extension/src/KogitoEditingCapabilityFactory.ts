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
import { CancellationToken, CustomDocument, CustomEditorEditingCapability } from "vscode";
import { KogitoEditorStore } from "./KogitoEditorStore";
import { KogitoEdit } from "@kogito-tooling/core-api";

export class KogitoEditingCapabilityFactory {
  private readonly editorStore: KogitoEditorStore;

  public constructor(editorStore: KogitoEditorStore) {
    this.editorStore = editorStore;
  }

  public createNew(document: CustomDocument) {
    return new KogitoEditingCapability(this.editorStore, document);
  }
}

export class KogitoEditingCapability implements CustomEditorEditingCapability<KogitoEdit> {
  private readonly _onDidEdit = new vscode.EventEmitter<KogitoEdit>();

  private readonly editorStore: KogitoEditorStore;
  private readonly document: CustomDocument;

  public readonly onDidEdit: vscode.Event<KogitoEdit>;

  public constructor(editorStore: KogitoEditorStore, document: CustomDocument) {
    this.editorStore = editorStore;
    this.document = document;
    this.onDidEdit = this._onDidEdit.event;
  }

  public async save() {
    this.editorStore.withActive(activeEditor => activeEditor.requestSave());
    console.info("save");
  }

  public async saveAs(targetResource: vscode.Uri) {
    console.info("saveAs");
  }

  public async undoEdits(edits: KogitoEdit[]) {
    console.info("undo");
    this.editorStore.withActive(activeEditor => activeEditor.notify_editorUndo(edits));
  }

  public async applyEdits(edits: KogitoEdit[]) {
    console.info("redo");
    this.editorStore.withActive(activeEditor => activeEditor.notify_editorRedo(edits));
  }

  public async backup(cancellation: CancellationToken) {
    console.info("backup");
    return true;
  }

  public notifyEdit(edit: KogitoEdit) {
    this._onDidEdit.fire(edit);
  }
}
