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
import React from "react";
import { EditorTheme } from "@kie-tools-core/editor/dist/api";
import { getOperatingSystem, OperatingSystem } from "@kie-tools-core/operating-system";
import { editor, KeyCode, KeyMod } from "monaco-editor";

export interface FormEditorEditorApi {
  editor: editor.IStandaloneCodeEditor | undefined;
  show: (container: HTMLDivElement, theme?: EditorTheme) => editor.IStandaloneCodeEditor;
  undo: () => void;
  redo: () => void;
  getContent: () => string;
  setContent: (content: string) => void;
  setTheme: (theme: EditorTheme) => void;
  forceRedraw: () => void;
  dispose: () => void;
  doResize: () => void;
}

export enum FormEditorEditorOperation {
  UNDO,
  REDO,
  EDIT,
}

export interface FormEditorEditorInstance {
  instance: editor.IStandaloneCodeEditor;
}

export class FormEditorEditorController implements FormEditorEditorApi {
  private readonly model: editor.ITextModel;

  public editor: editor.IStandaloneCodeEditor | undefined;

  constructor(
    content: string,
    private readonly onContentChange: (args: { content: string; operation: FormEditorEditorOperation }) => void,
    private readonly language: string | undefined,
    private readonly isReadOnly: boolean
  ) {
    this.model = editor.createModel(content, language);
    this.model.onDidChangeContent((event) => {
      if (!event.isUndoing && !event.isRedoing) {
        this.editor?.pushUndoStop();
        onContentChange({ content: this.model.getValue(), operation: FormEditorEditorOperation.EDIT });
      }
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
      fontSize: 14,
      theme: this.getMonacoThemeByEditorTheme(theme),
      readOnly: this.isReadOnly,
    });

    this.editor.addCommand(KeyMod.CtrlCmd | KeyCode.KeyZ, () => {
      this.onContentChange({ content: this.model.getValue(), operation: FormEditorEditorOperation.UNDO });
    });

    this.editor.addCommand(KeyMod.CtrlCmd | KeyMod.Shift | KeyCode.KeyZ, () => {
      this.onContentChange({ content: this.model.getValue(), operation: FormEditorEditorOperation.REDO });
    });

    if (getOperatingSystem() !== OperatingSystem.MACOS) {
      this.editor.addCommand(KeyMod.CtrlCmd | KeyCode.KeyY, () => {
        this.onContentChange({ content: this.model.getValue(), operation: FormEditorEditorOperation.REDO });
      });
    }

    return this.editor;
  }

  public getContent(): string {
    return this.editor?.getModel()?.getValue() || "";
  }

  public setContent(content: string) {
    this.editor?.getModel()?.setValue(content);
  }

  public forceRedraw() {
    this.editor?.render(true);
  }

  public doResize() {
    this.forceRedraw();
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
