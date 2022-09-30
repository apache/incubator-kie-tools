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

import { EditorTheme } from "@kie-tools-core/editor/dist/api";
import { OperatingSystem } from "@kie-tools-core/operating-system";
import { FileLanguage, SwfLanguageServiceCommandIds } from "@kie-tools/serverless-workflow-language-service/dist/api";
import { SwfJsonOffsets, SwfYamlOffsets } from "@kie-tools/serverless-workflow-language-service/dist/editor";
import { editor, KeyCode, KeyMod } from "monaco-editor";
import { initJsonSchemaDiagnostics } from "./augmentation/language/json";
import { initYamlSchemaDiagnostics } from "./augmentation/language/yaml";

initJsonSchemaDiagnostics();
initYamlSchemaDiagnostics();

export interface SwfTextEditorApi {
  editor: editor.IStandaloneCodeEditor | undefined;
  show: (container: HTMLDivElement, theme?: EditorTheme) => editor.IStandaloneCodeEditor;
  undo: () => void;
  redo: () => void;
  getContent: () => string;
  setTheme: (theme: EditorTheme) => void;
  forceRedraw: () => void;
  dispose: () => void;
  moveCursorToNode: (nodeName: string) => void;
}

export enum SwfTextEditorOperation {
  UNDO,
  REDO,
  EDIT,
}

export interface SwfTextEditorInstance {
  commands: SwfLanguageServiceCommandIds;
  instance: editor.IStandaloneCodeEditor;
}

export class SwfTextEditorController implements SwfTextEditorApi {
  private readonly model: editor.ITextModel;
  private swfJsonOffsets = new SwfJsonOffsets();
  private swfYamlOffsets = new SwfYamlOffsets();

  public editor: editor.IStandaloneCodeEditor | undefined;

  constructor(
    content: string,
    private readonly onContentChange: (content: string, operation: SwfTextEditorOperation) => void,
    private readonly language: FileLanguage,
    private readonly operatingSystem: OperatingSystem | undefined,
    private readonly isReadOnly: boolean,
    private readonly setValidationErrors: (errors: editor.IMarker[]) => void,
    private readonly onSelectionChanged: (nodeName: string) => void
  ) {
    this.model = editor.createModel(content, this.language);
    this.model.onDidChangeContent((event) => {
      if (!event.isUndoing && !event.isRedoing) {
        this.editor?.pushUndoStop();
        onContentChange(this.model.getValue(), SwfTextEditorOperation.EDIT);
      }
    });

    editor.onDidChangeMarkers(() => {
      this.setValidationErrors(this.getValidationMarkers());
    });

    editor.onDidCreateEditor((codeEditor) => {
      codeEditor.onDidChangeCursorPosition((event) => this.handleDidChangeCursorPosition(event));
    });
  }

  private get swfOffsetsApi(): SwfJsonOffsets | SwfYamlOffsets {
    return this.language === FileLanguage.YAML ? this.swfYamlOffsets : this.swfJsonOffsets;
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
      this.onContentChange(this.model.getValue(), SwfTextEditorOperation.UNDO);
    });

    this.editor.addCommand(KeyMod.CtrlCmd | KeyMod.Shift | KeyCode.KeyZ, () => {
      this.onContentChange(this.model.getValue(), SwfTextEditorOperation.REDO);
    });

    if (this.operatingSystem !== OperatingSystem.MACOS) {
      this.editor.addCommand(KeyMod.CtrlCmd | KeyCode.KeyY, () => {
        this.onContentChange(this.model.getValue(), SwfTextEditorOperation.REDO);
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

  /**
   * Moves the cursor to a specified node by node name.
   *
   * @param nodeName -
   * @returns
   */
  public moveCursorToNode(nodeName: string): void {
    if (!this.editor || !nodeName) {
      return;
    }

    this.swfOffsetsApi.parseContent(this.getContent());

    const targetOffset = this.swfOffsetsApi.getStateNameOffset(nodeName);

    if (!targetOffset) {
      return;
    }

    const targetPosition = this.editor?.getModel()?.getPositionAt(targetOffset);

    if (!targetPosition) {
      return;
    }

    this.editor?.revealLineInCenter(targetPosition.lineNumber);
    this.editor?.setPosition(targetPosition);
    this.editor?.focus();
  }

  public handleDidChangeCursorPosition(event: editor.ICursorPositionChangedEvent): void {
    const position = event.position;
    if (!position || event.reason !== editor.CursorChangeReason.Explicit) {
      return;
    }

    const offset = this.editor?.getModel()?.getOffsetAt(position);
    if (!offset) {
      return;
    }

    this.swfOffsetsApi.parseContent(this.getContent());

    const nodeName = this.swfOffsetsApi.getStateNameFromOffset(offset);
    if (!nodeName) {
      return;
    }

    this.onSelectionChanged(nodeName);
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
