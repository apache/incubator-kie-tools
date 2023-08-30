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

import { editor, KeyCode, KeyMod, Position } from "monaco-editor";
import { initYamlSchemaDiagnostics } from "./augmentation/language/yaml";
import { OperatingSystem } from "@kie-tools-core/operating-system";
import { EditorTheme } from "@kie-tools-core/editor/dist/api";

initYamlSchemaDiagnostics();

const LANGUAGE = "yaml";

export interface DashbuilderMonacoEditorApi {
  show: (container: HTMLDivElement, theme?: EditorTheme) => editor.IStandaloneCodeEditor;
  dispose: () => void;
  undo: () => void;
  redo: () => void;
  getContent: () => string;
  setTheme: (theme: EditorTheme) => void;
  forceRedraw: () => void;
  moveCursorToPosition: (position: Position) => void;
}

export enum MonacoEditorOperation {
  UNDO,
  REDO,
  EDIT,
}

export interface DashbuilderMonacoEditorInstance {
  instance: editor.IStandaloneCodeEditor;
}

export class DefaultDashbuilderMonacoEditorController implements DashbuilderMonacoEditorApi {
  private readonly model: editor.ITextModel;

  public editor: editor.IStandaloneCodeEditor | undefined;

  constructor(
    content: string,
    private readonly onContentChange: (content: string, operation: MonacoEditorOperation) => void,
    private readonly operatingSystem: OperatingSystem | undefined
  ) {
    this.model = editor.createModel(content, LANGUAGE);
    this.model.onDidChangeContent((event) => {
      if (!event.isUndoing && !event.isRedoing) {
        this.editor?.pushUndoStop();
        onContentChange(this.model.getValue(), MonacoEditorOperation.EDIT);
      }
    });
  }

  public dispose(): void {
    this.editor?.dispose();
  }

  public redo(): void {
    this.editor?.focus();
    this.editor?.trigger("editor", "redo", null);
  }

  public undo(): void {
    this.editor?.focus();
    this.editor?.trigger("editor", "undo", null);
  }

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
      language: LANGUAGE,
      scrollBeyondLastLine: false,
      automaticLayout: true,
      fontSize: 12,
      theme: this.getMonacoThemeByEditorTheme(theme),
    });

    this.editor.addCommand(KeyMod.CtrlCmd | KeyCode.KeyZ, () => {
      this.onContentChange(this.model.getValue(), MonacoEditorOperation.UNDO);
    });

    this.editor.addCommand(KeyMod.CtrlCmd | KeyMod.Shift | KeyCode.KeyZ, () => {
      this.onContentChange(this.model.getValue(), MonacoEditorOperation.REDO);
    });

    if (this.operatingSystem !== OperatingSystem.MACOS) {
      this.editor.addCommand(KeyMod.CtrlCmd | KeyCode.KeyY, () => {
        this.onContentChange(this.model.getValue(), MonacoEditorOperation.REDO);
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

  public moveCursorToPosition(position: Position): void {
    if (!this.editor) {
      return;
    }

    this.editor.revealLineInCenter(position.lineNumber);
    this.editor.setPosition(position);
    this.editor.focus();
  }
}
