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
import * as path_ from "path";
import { KogitoEditorStore } from "./KogitoEditorStore";
import { KogitoEditor } from "./KogitoEditor";
import { Router, ResourceContentService } from "@kogito-tooling/core-api";

export class KogitoEditorFactory {
  private readonly context: vscode.ExtensionContext;
  private readonly editorStore: KogitoEditorStore;
  private readonly webviewLocation: string;
  private readonly router: Router;
  private readonly resourceContentService: ResourceContentService;

  constructor(
    context: vscode.ExtensionContext,
    router: Router,
    webviewLocation: string,
    editorStore: KogitoEditorStore,
    resourceContentService: ResourceContentService
  ) {
    this.context = context;
    this.editorStore = editorStore;
    this.router = router;
    this.webviewLocation = webviewLocation;
    this.resourceContentService = resourceContentService;
  }

  public openNew(path: string) {
    if (path.length <= 0) {
      throw new Error("parameter 'path' cannot be empty");
    }

    const panel = this.openNewPanel(path);
    const editor = new KogitoEditor(path, panel, this.context, this.router, this.webviewLocation, this.editorStore, this.resourceContentService);
    this.editorStore.addAsActive(editor);
    editor.setupEnvelopeBus();
    editor.setupPanelActiveStatusChange();
    editor.setupPanelOnDidDispose();
    editor.setupWebviewContent();
  }

  private openNewPanel(path: string) {
    const panelTitle = path.split(path_.sep).pop()!;

    //this will open a panel on vscode's UI.
    return vscode.window.createWebviewPanel(
      "kogito-editor",
      panelTitle,
      { viewColumn: vscode.ViewColumn.Active, preserveFocus: false },
      { enableCommandUris: true, enableScripts: true, retainContextWhenHidden: true }
    );
  }
}
