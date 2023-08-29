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

import { VsCodeKieEditorController } from "./VsCodeKieEditorController";
import { Uri } from "vscode";

export class VsCodeKieEditorStore {
  public activeEditor?: VsCodeKieEditorController;
  public openEditors: Set<VsCodeKieEditorController>;

  constructor() {
    this.openEditors = new Set();
  }

  public addAsActive(editor: VsCodeKieEditorController) {
    this.activeEditor = editor;
    this.openEditors.add(editor);
  }

  public setActive(editor: VsCodeKieEditorController) {
    this.activeEditor = editor;
  }

  public isActive(editor: VsCodeKieEditorController) {
    return this.activeEditor === editor;
  }

  public setNoneActive() {
    this.activeEditor = undefined;
  }

  public close(editor: VsCodeKieEditorController) {
    this.openEditors.delete(editor);

    if (this.isActive(editor)) {
      this.setNoneActive();
    }
  }

  public get(uri: Uri) {
    let found: VsCodeKieEditorController | undefined;

    this.openEditors.forEach((editor) => {
      if (editor.hasUri(uri)) {
        found = editor;
      }
    });

    return found;
  }
}
