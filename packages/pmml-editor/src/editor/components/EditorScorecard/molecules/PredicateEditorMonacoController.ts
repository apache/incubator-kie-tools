/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as Monaco from "@kie-tools-core/monaco-editor";

export class PredicateEditorMonacoController {
  private container: HTMLElement | undefined;
  private editor: Monaco.editor.IStandaloneCodeEditor;

  public createEditor(container: HTMLElement, onChange: (text: string) => void): void {
    this.dispose();

    this.container = container;

    if (container) {
      this.editor = Monaco.editor.create(container, {
        language: "scorecards",
        theme: "scorecards",
        glyphMargin: false,
        scrollBeyondLastLine: false,
      });
      this.editor.onDidChangeModelContent((event: Monaco.editor.IModelContentChangedEvent) => {
        onChange(this.editor.getValue());
      });
      this.editor.focus();
    }
  }

  public setValue(value: string | undefined): void {
    const model = this.editor?.getModel();
    if (model && model.getValue() !== value) {
      this.editor.setValue(value ?? "");
      this.editor.setSelection(model.getFullModelRange());
    }
  }

  public dispose(): void {
    this.editor?.dispose();
    this.container = undefined;
  }
}
