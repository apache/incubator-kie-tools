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
import { WebviewCustomEditorEditingDelegate } from "vscode";
import { KogitoEditType } from "./KogitoEditType";
import { KogitoEditorStore } from "./KogitoEditorStore";

export class KogitoEditingDelegate implements WebviewCustomEditorEditingDelegate<KogitoEditType> {
  private readonly _onEdit = new vscode.EventEmitter<{
    readonly resource: vscode.Uri;
    readonly edit: KogitoEditType;
  }>();

  private readonly editorStore: KogitoEditorStore;
  public readonly onEdit: vscode.Event<{ readonly resource: vscode.Uri; readonly edit: KogitoEditType }>;

  public constructor(editorStore: KogitoEditorStore) {
    this.editorStore = editorStore;
    this.onEdit = this._onEdit.event;
  }

  public async save(resource: vscode.Uri) {
    this.editorStore.withActive(activeEditor => activeEditor.requestSave());
    console.info("save");
  }

  public async saveAs(resource: vscode.Uri, targetResource: vscode.Uri) {
    console.info("saveAs");
  }

  public async undoEdits(resource: vscode.Uri, edits: KogitoEditType[]) {
    console.info("undo");
  }

  public async applyEdits(resource: vscode.Uri, edits: KogitoEditType[]) {
    console.info("redo");
  }
}
