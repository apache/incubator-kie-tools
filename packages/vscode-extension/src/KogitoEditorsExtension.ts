/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import * as vscode from "vscode";
import { KogitoEditorStore } from "./KogitoEditorStore";
import { KogitoEditorFactory } from "./KogitoEditorFactory";

export class KogitoEditorsExtension {
  private readonly context: vscode.ExtensionContext;
  private readonly extensionName: string;
  private readonly editorStore: KogitoEditorStore;
  private readonly editorFactory: KogitoEditorFactory;

  constructor(
    context: vscode.ExtensionContext,
    extensionName: string,
    editorStore: KogitoEditorStore,
    editorFactory: KogitoEditorFactory
  ) {
    this.context = context;
    this.editorStore = editorStore;
    this.editorFactory = editorFactory;
    this.extensionName = extensionName;
  }

  public registerCustomSaveCommand() {
    this.context.subscriptions.push(
      vscode.commands.registerCommand("workbench.action.files.save", async () => {
        // If a text editor is active, we save it normally.
        if (vscode.window.activeTextEditor) {
          await vscode.window.activeTextEditor.document.save();
        }
        // If a kogito editor is active, its content is saved manually.
        this.editorStore.withActive(editor => editor.requestSave());
      })
    );
  }

  public registerCustomSaveAllCommand() {
    this.context.subscriptions.push(
      vscode.commands.registerCommand("workbench.action.files.saveAll", () => {
        this.editorStore.openEditors.forEach(e => e.requestSave());
        return vscode.workspace.saveAll(false);
      })
    );
  }

  public registerStateControl() {
    // here we should register the calls to editor undo/redo
  }

  public startReplacingTextEditorsByKogitoEditorsAsTheyOpenIfLanguageIsSupported() {
    this.context.subscriptions.push(
      vscode.window.onDidChangeActiveTextEditor(async (textEditor?: vscode.TextEditor) => {
        if (!textEditor) {
          return;
        }

        if (!this.supportsLanguage(textEditor.document.languageId)) {
          return;
        }

        const path = textEditor.document.uri.fsPath;
        const openKogitoEditor = this.editorStore.get(path);

        if (!openKogitoEditor) {
          await this.closeActiveTextEditor();
          this.editorFactory.openNew(path);
          return;
        }

        if (textEditor.viewColumn === openKogitoEditor.viewColumn()) {
          await this.closeActiveTextEditor();
          openKogitoEditor.focus();
          return;
        }

        //editor opened on different viewColumn, so we leave it
        //open so that people can edit it as text if they want
      })
    );
  }

  private closeActiveTextEditor() {
    return vscode.commands.executeCommand("workbench.action.closeActiveEditor");
  }

  private supportsLanguage(languageId: string) {
    const extension = vscode.extensions.getExtension(this.extensionName);
    if (!extension) {
      throw new Error("Extension configuration not found.");
    }

    const matchingLanguages = (extension.packageJSON.contributes.languages as any[]).filter(
      language => language.extensions.indexOf("." + languageId) > -1
    );

    return matchingLanguages.length > 0;
  }
}
