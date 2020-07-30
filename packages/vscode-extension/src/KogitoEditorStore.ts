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

import { KogitoEditor } from "./KogitoEditor";
import { Uri } from "vscode";

export class KogitoEditorStore {
  public activeEditor?: KogitoEditor;
  public openEditors: Set<KogitoEditor>;

  constructor() {
    this.openEditors = new Set();
  }

  public addAsActive(editor: KogitoEditor) {
    this.activeEditor = editor;
    this.openEditors.add(editor);
  }

  public setActive(editor: KogitoEditor) {
    this.activeEditor = editor;
  }

  public isActive(editor: KogitoEditor) {
    return this.activeEditor === editor;
  }

  public setNoneActive() {
    this.activeEditor = undefined;
  }

  public close(editor: KogitoEditor) {
    this.openEditors.delete(editor);

    if (this.isActive(editor)) {
      this.setNoneActive();
    }
  }

  public get(uri: Uri) {
    let found: KogitoEditor | undefined;

    this.openEditors.forEach(editor => {
      if (editor.hasUri(uri)) {
        found = editor;
      }
    });

    return found;
  }
}
