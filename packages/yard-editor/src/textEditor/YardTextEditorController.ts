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
import { OperatingSystem } from "@kie-tools-core/operating-system";
import { EditorTheme } from "@kie-tools-core/editor/dist/api";
import { initYamlSchemaDiagnostics } from "./augmentation/language/yaml";

initYamlSchemaDiagnostics();
export interface YardTextEditorApi {
  show: (container: HTMLDivElement, theme?: EditorTheme) => editor.IStandaloneCodeEditor;
  undo: () => void;
  redo: () => void;
  getContent: () => string;
  setTheme: (theme: EditorTheme) => void;
  forceRedraw: () => void;
  dispose: () => void;
}

export enum YardTextEditorOperation {
  UNDO,
  REDO,
  EDIT,
}

export interface YardTextEditorInstance {
  /*commands: YardLanguageServiceCommandIds; to be imported from future yard-language-service */
  instance: editor.IStandaloneCodeEditor;
}

export class YardTextEditorController implements YardTextEditorApi {
  private readonly model: editor.ITextModel;

  public editor: editor.IStandaloneCodeEditor | undefined;

  constructor(
    content: string,
    private readonly onContentChange: (content: string, operation: YardTextEditorOperation) => void,
    private readonly language: string,
    private readonly operatingSystem: OperatingSystem | undefined,
    private readonly isReadOnly: boolean,
    private readonly setValidationErrors: (errors: editor.IMarker[]) => void
  ) {
    this.model = editor.createModel(content, this.language);
    this.model.onDidChangeContent((event) => {
      if (!event.isUndoing && !event.isRedoing) {
        this.editor?.pushUndoStop();
        onContentChange(this.model.getValue(), YardTextEditorOperation.EDIT);
      }
    });

    editor.onDidChangeMarkers(() => {
      this.setValidationErrors(this.getValidationMarkers());
    });
  }

  public redo(): void {
    this.editor?.focus();
    this.editor?.trigger("editor", "redo", null);
  }

  public undo(): void {
    this.editor?.focus();
    this.editor?.trigger("editor", "undo", null);
  }

  public getValidationMarkers = (): editor.IMarker[] => {
    return editor.getModelMarkers({});
  };

  public setTheme(theme: EditorTheme): void {
    editor.setTheme(this.getMonacoThemeByEditorTheme(theme));
  }

  public show(container: HTMLDivElement, theme: EditorTheme): editor.IStandaloneCodeEditor {
    if (!container) {
      throw new Error("We need a container to show the editor!");
    }
    if (this.editor !== undefined) {
      this.setTheme(theme);
      return this.editor;
    }

    this.editor = editor.create(container, {
      model: this.model,
      language: this.language,
      scrollBeyondLastLine: false,
      automaticLayout: true,
      fontSize: 12,
      theme: this.getMonacoThemeByEditorTheme(theme),
      readOnly: this.isReadOnly,
    });

    this.editor.addCommand(KeyMod.CtrlCmd | KeyCode.KeyZ, () => {
      this.onContentChange(this.model.getValue(), YardTextEditorOperation.UNDO);
    });

    this.editor.addCommand(KeyMod.CtrlCmd | KeyMod.Shift | KeyCode.KeyZ, () => {
      this.onContentChange(this.model.getValue(), YardTextEditorOperation.REDO);
    });

    if (this.operatingSystem !== OperatingSystem.MACOS) {
      this.editor.addCommand(KeyMod.CtrlCmd | KeyCode.KeyY, () => {
        this.onContentChange(this.model.getValue(), YardTextEditorOperation.REDO);
      });
    }

    return this.editor;
  }

  public getContent(): string {
    return this.editor?.getModel()?.getValue() || "";
  }

  public forceRedraw() {
    this.editor?.render(true);
  }

  private getMonacoThemeByEditorTheme(theme?: EditorTheme): string {
    switch (theme) {
      case EditorTheme.DARK:
        return "vs-dark";
      case EditorTheme.HIGH_CONTRAST:
        return "hc-black";
      default:
        return "vs";
    }
  }

  public dispose(): void {
    this.editor?.dispose();
  }
}
