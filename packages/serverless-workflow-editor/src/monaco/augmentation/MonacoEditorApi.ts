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

import { editor } from "monaco-editor";
import { MonacoAugmentation } from "./MonacoAugmentation";

export interface MonacoEditorApi {
  show: (container: HTMLDivElement) => void;
  dispose: () => void;

  undo: () => void;
  redo: () => void;
}

export class DefaultMonacoEditor implements MonacoEditorApi {
  private readonly model: editor.ITextModel;

  private editor: editor.IStandaloneCodeEditor;
  private readonly augmentation: MonacoAugmentation;

  constructor(content: string, onContentChange: (content: string) => void, augmentation: MonacoAugmentation) {
    this.model = editor.createModel(augmentation.language.getDefaultContent(content), augmentation.language.languageId);
    this.model.onDidChangeContent((event) => onContentChange(this.model.getValue()));
    this.augmentation = augmentation;
  }

  redo(): void {
    this.editor?.trigger("whatever...", "redo", null);
  }

  undo(): void {
    this.editor?.trigger("whatever...", "undo", null);
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

    // this.editor.onMouseDown((event) => showWidget(event, this.editor));
  }

  dispose(): void {
    this.model?.dispose();
    this.editor?.dispose();
  }
}
