/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { editor, KeyCode, KeyMod } from "monaco-editor";
import { SwfMonacoEditorCommandIds } from "./augmentation/commands";
import { initJsonSchema } from "./augmentation/language/json";
import { initYamlSchema } from "./augmentation/language/yaml";
import { OperatingSystem } from "@kie-tools-core/operating-system";

initJsonSchema();
initYamlSchema();

export interface SwfMonacoEditorApi {
  show: (container: HTMLDivElement) => editor.IStandaloneCodeEditor;
  dispose: () => void;

  undo: () => void;
  redo: () => void;

  getContent: () => string;
}

export enum MonacoEditorOperation {
  UNDO,
  REDO,
  EDIT,
}

export interface SwfMonacoEditorInstance {
  commands: SwfMonacoEditorCommandIds;
  instance: editor.IStandaloneCodeEditor;
}

export class DefaultSwfMonacoEditorController implements SwfMonacoEditorApi {
  private readonly model: editor.ITextModel;

  public editor: editor.IStandaloneCodeEditor;

  constructor(
    content: string,
    private readonly onContentChange: (content: string, operation: MonacoEditorOperation) => void,
    private readonly language: string,
    private readonly operatingSystem: OperatingSystem | undefined
  ) {
    this.model = editor.createModel(content, this.language);
    this.model.onDidChangeContent((event) => {
      if (!event.isUndoing && !event.isRedoing) {
        this.editor?.pushUndoStop();
        onContentChange(this.model.getValue(), MonacoEditorOperation.EDIT);
      }
    });
  }

  redo(): void {
    this.editor?.focus();
    this.editor?.trigger("editor", "redo", null);
  }

  undo(): void {
    this.editor?.focus();
    this.editor?.trigger("editor", "undo", null);
  }

  show(container: HTMLDivElement): editor.IStandaloneCodeEditor {
    if (!container) {
      throw new Error("We need a container to show the editor!");
    }

    const editorInstance = editor.create(container, {
      model: this.model,
      language: this.language,
      scrollBeyondLastLine: false,
      automaticLayout: true,
      fontSize: 14,
    });

    this.editor = editorInstance;

    this.editor.addCommand(KeyMod.CtrlCmd | KeyCode.KEY_Z, () => {
      this.onContentChange(this.model.getValue(), MonacoEditorOperation.UNDO);
    });

    this.editor.addCommand(KeyMod.CtrlCmd | KeyMod.Shift | KeyCode.KEY_Z, () => {
      this.onContentChange(this.model.getValue(), MonacoEditorOperation.REDO);
    });

    if (this.operatingSystem !== OperatingSystem.MACOS) {
      this.editor.addCommand(KeyMod.CtrlCmd | KeyCode.KEY_Y, () => {
        this.onContentChange(this.model.getValue(), MonacoEditorOperation.REDO);
      });
    }

    return editorInstance;
  }

  getContent(): string {
    return this.editor.getModel()?.getValue() || "";
  }

  dispose(): void {
    this.model?.dispose();
    this.editor?.dispose();
  }
}
