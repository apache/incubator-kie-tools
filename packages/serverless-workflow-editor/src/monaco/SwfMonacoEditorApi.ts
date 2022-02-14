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
import { initJsonSchema } from "./augmentation/language/json";
import { initYamlSchema } from "./augmentation/language/yaml";

initJsonSchema();
initYamlSchema();

export interface SwfMonacoEditorApi {
  show: (container: HTMLDivElement) => editor.IStandaloneCodeEditor;
  dispose: () => void;

  undo: () => void;
  redo: () => void;
}

export type SwfMonacoEditorCommandTypes = "FunctionsWidget" | "StatesWidget" | "FunctionsCompletion";

export type SwfMonacoEditorCommands = Record<SwfMonacoEditorCommandTypes, string>;

export interface SwfMonacoEditorInstance {
  commands: SwfMonacoEditorCommands;
  instance: editor.IStandaloneCodeEditor;
}

export class DefaultSwfMonacoEditorController implements SwfMonacoEditorApi {
  private readonly model: editor.ITextModel;

  public editor: editor.IStandaloneCodeEditor;

  constructor(content: string, onContentChange: (content: string) => void, private readonly language: string) {
    this.model = editor.createModel(content, this.language);
    this.model.onDidChangeContent((event) => onContentChange(this.model.getValue()));
  }

  redo(): void {
    this.editor?.trigger("whatever...", "redo", null);
  }

  undo(): void {
    this.editor?.trigger("whatever...", "undo", null);
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

    return editorInstance;
  }

  dispose(): void {
    this.model?.dispose();
    this.editor?.dispose();
  }
}
