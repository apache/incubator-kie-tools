/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import { KogitoEditor } from "./KogitoEditor";
import { Disposable } from "vscode";
import * as vscode from "vscode";

export class KogitoActiveEditor {
  private static readonly UNDO_COMMAND_ID = "undo";
  private static readonly REDO_COMMAND_ID = "redo";

  public readonly editor: KogitoEditor;

  private readonly undoCommand: KogitoActiveEditorCommand;
  private readonly reCommand: KogitoActiveEditorCommand;

  constructor(editor: KogitoEditor) {
    this.editor = editor;

    this.undoCommand = new KogitoActiveEditorCommand(KogitoActiveEditor.UNDO_COMMAND_ID, () => {
      editor.notifyUndo();
    });
    this.reCommand = new KogitoActiveEditorCommand(KogitoActiveEditor.REDO_COMMAND_ID, () => {
      editor.notifyRedo();
    });
  }

  public dispose() {
    this.undoCommand.dispose();
    this.reCommand.dispose();
  }
}

class KogitoActiveEditorCommand {
  private readonly id: string;
  private readonly disposable: Disposable;

  constructor(id: string, runnable: () => void) {
    this.id = id;

    this.disposable = vscode.commands.registerCommand(id, runnable);
  }

  public dispose() {
    this.disposable.dispose();
  }
}