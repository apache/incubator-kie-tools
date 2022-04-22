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

import * as monaco from "monaco-editor";
import { editor, IDisposable, KeyCode, KeyMod } from "monaco-editor";
import { SwfLanguageServiceCommandIds } from "@kie-tools/serverless-workflow-language-service";
import { initJsonSchemaDiagnostics } from "./augmentation/language/json";
import { initYamlSchemaDiagnostics } from "./augmentation/language/yaml";
import { OperatingSystem } from "@kie-tools-core/operating-system";
import { EditorTheme } from "@kie-tools-core/editor/dist/api";

initJsonSchemaDiagnostics();
initYamlSchemaDiagnostics();

export interface SwfTextEditorController {
  show: (container: HTMLDivElement, theme?: EditorTheme) => editor.IStandaloneCodeEditor;
  undo: () => void;
  redo: () => void;
  getContent: () => string;
  setContent: (content: string) => void;
  setTheme: (theme: EditorTheme) => void;
  forceRedraw: () => void;
}

export enum SwfTextEditorOperation {
  UNDO,
  REDO,
  EDIT,
}

export interface SwfMonacoEditorInstance {
  commands: SwfLanguageServiceCommandIds;
  instance: editor.IStandaloneCodeEditor;
}

const MONACO_CHANGES_DEBOUNCE_TIME_IN_MS = 400;

export class DefaultSwfTextEditorController implements SwfTextEditorController {
  private readonly model: editor.ITextModel;

  public editor: editor.IStandaloneCodeEditor | undefined;
  private onDidChangeContentSubscription: IDisposable;

  constructor(
    private readonly onContentChange: (content: string, operation: SwfTextEditorOperation, versionId?: number) => void,
    private readonly language: string,
    private readonly operatingSystem: OperatingSystem | undefined,
    private readonly uri: string
  ) {
    console.error("@@@@: Settings up a new Monaco controller. This should only happen once.");
    this.model = editor.createModel("", this.language, monaco.Uri.parse(this.uri));

    this.startListeningToContentChanges();
  }

  private startListeningToContentChanges() {
    let debouncedHandler: ReturnType<typeof setTimeout> | undefined;

    this.onDidChangeContentSubscription = this.model.onDidChangeContent((event) => {
      if (debouncedHandler !== undefined) {
        clearTimeout(debouncedHandler);
      }

      debouncedHandler = setTimeout(() => {
        debouncedHandler = undefined;
        this.handleNewContent(event);
      }, MONACO_CHANGES_DEBOUNCE_TIME_IN_MS);
    });
  }

  private handleNewContent(event: editor.IModelContentChangedEvent) {
    if (event.isUndoing || event.isRedoing) {
      return;
    }

    console.error("@@@@: Monaco did change. Sending newEdit");
    this.onContentChange(this.model.getValue(), SwfTextEditorOperation.EDIT, event.versionId);
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

  public setContent(content: string): void {
    this.onDidChangeContentSubscription.dispose();
    const position = this.editor?.getPosition();
    this.model.setValue(content);

    // FIXME: tiago - this is not ideal. I think Monaco should be handling the undo/redos and letting the channel know through a newEdit once it's done.
    if (position) {
      this.editor?.setPosition(position);
    }

    this.startListeningToContentChanges();
  }

  public show(container: HTMLDivElement, theme: EditorTheme): editor.IStandaloneCodeEditor {
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
    });

    this.editor.updateOptions({ wordBasedSuggestions: false });

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
    return this.model.getValue();
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
}
