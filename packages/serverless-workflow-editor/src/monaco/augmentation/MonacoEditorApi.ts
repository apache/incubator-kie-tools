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
import { MonacoAugmentation } from "./MonacoAugmentation";
import { OperatingSystem } from "@kie-tools-core/operating-system";

export interface MonacoEditorApi {
  show: (container: HTMLDivElement) => void;
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

export class DefaultMonacoEditor implements MonacoEditorApi {
  private readonly model: editor.ITextModel;

  private editor: editor.IStandaloneCodeEditor;
  private readonly augmentation: MonacoAugmentation;
  private readonly onContentChange: (content: string, operation: MonacoEditorOperation) => void;
  private readonly operatingSystem?: OperatingSystem;

  constructor(
    content: string,
    onContentChange: (content: string, operation: MonacoEditorOperation) => void,
    augmentation: MonacoAugmentation,
    operatingSystem?: OperatingSystem
  ) {
    this.model = editor.createModel(augmentation.language.getDefaultContent(content), augmentation.language.languageId);
    this.model.onDidChangeContent((event) => {
      if (!event.isUndoing && !event.isRedoing) {
        this.editor?.pushUndoStop();
        onContentChange(this.model.getValue(), MonacoEditorOperation.EDIT);
      }
    });
    this.onContentChange = onContentChange;
    this.augmentation = augmentation;
    this.operatingSystem = operatingSystem;
  }

  redo(): void {
    this.editor?.focus();
    this.editor?.trigger("editor", "redo", null);
  }

  undo(): void {
    this.editor?.focus();
    this.editor?.trigger("editor", "undo", null);
  }

  show(container: HTMLDivElement): void {
    if (!container) {
      throw new Error("We need a container to show the editor!");
    }

    this.editor = editor.create(container, {
      model: this.model,
      language: this.augmentation.language.languageId,
      scrollBeyondLastLine: false,
      automaticLayout: true,
    });

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
  }

  getContent(): string {
    return this.editor.getModel()?.getValue() || "";
  }

  dispose(): void {
    this.model?.dispose();
    this.editor?.dispose();
  }
}
