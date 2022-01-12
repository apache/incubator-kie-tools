/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import * as monaco from "monaco-editor";
import { editor } from "monaco-editor";
// import { initCompletion } from "./completion";

// Setting up the JSON language defaults
export const SW_SPEC_SCHEMA =
  "https://raw.githubusercontent.com/serverlessworkflow/specification/0.6.x/schema/workflow.json";

monaco.languages.json.jsonDefaults.setDiagnosticsOptions({
  validate: true,
  allowComments: false,
  schemas: [
    {
      uri: SW_SPEC_SCHEMA,
      fileMatch: ["*"],
    },
  ],
  enableSchemaRequest: true,
});

export interface IMonacoEditor {
  show: (container: HTMLDivElement) => void;
  dispose: () => void;

  undo: () => void;
  redo: () => void;
}

class DefaultMonacoEditor implements IMonacoEditor {
  private readonly model: editor.ITextModel;
  private readonly onContentChange: (content: string) => void;

  private editor: editor.IStandaloneCodeEditor;

  constructor(content: string, onContentChange: (content: string) => void) {
    this.model = editor.createModel(content, "json");
    this.model.onDidChangeContent((event) => onContentChange(this.model.getValue()));
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
      language: "json",
      scrollBeyondLastLine: false,
      automaticLayout: true,
    });
  }

  dispose(): void {
    this.model?.dispose();
    this.editor?.dispose();
  }
}

export function buildEditor(content: string = "{}", onContentChange: (content: string) => void): IMonacoEditor {
  // initCompletion();
  return new DefaultMonacoEditor(content, onContentChange);
}
