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

import { KogitoEditorStore } from "./KogitoEditorStore";
import { KogitoEditor } from "./KogitoEditor";
import { KogitoEdit, ResourceContentService } from "@kogito-tooling/core-api";
import { VsCodeNodeResourceContentService } from "./VsCodeNodeResourceContentService";
import { VsCodeResourceContentService } from "./VsCodeResourceContentService";

import * as vscode from "vscode";
import * as nodePath from "path";
import { DefaultVsCodeRouter } from "./DefaultVsCodeRouter";
import { Routes } from "@kogito-tooling/microeditor-envelope-protocol";

export class KogitoEditorFactory {
  private readonly context: vscode.ExtensionContext;
  private readonly editorStore: KogitoEditorStore;
  private readonly webviewLocation: string;
  private readonly routes: Routes;

  constructor(
    context: vscode.ExtensionContext,
    routes: Routes,
    webviewLocation: string,
    editorStore: KogitoEditorStore
  ) {
    this.context = context;
    this.editorStore = editorStore;
    this.routes = routes;
    this.webviewLocation = webviewLocation;
  }

  public configureNew(
    uri: vscode.Uri,
    initialBackup: vscode.Uri | undefined,
    webviewPanel: vscode.WebviewPanel,
    signalEdit: (edit: KogitoEdit) => void
  ) {
    const path = uri.fsPath;
    if (path.length <= 0) {
      throw new Error("parameter 'path' cannot be empty");
    }

    webviewPanel.webview.options = {
      enableCommandUris: true,
      enableScripts: true,
      localResourceRoots: [vscode.Uri.file(this.context.extensionPath)]
    };

    const router = new DefaultVsCodeRouter(this.context, webviewPanel.webview, this.routes);

    const workspacePath = vscode.workspace.asRelativePath(path);

    const resourceContentService = this.createResourceContentService(path, workspacePath);

    const editor = new KogitoEditor(
      workspacePath,
      uri,
      initialBackup,
      webviewPanel,
      this.context,
      router,
      this.webviewLocation,
      this.editorStore,
      resourceContentService,
      signalEdit
    );
    this.editorStore.addAsActive(editor);
    editor.setupEnvelopeBus();
    editor.setupPanelActiveStatusChange();
    editor.setupPanelOnDidDispose();
    editor.setupWebviewContent();
  }

  public createResourceContentService(path: string, workspacePath: string): ResourceContentService {
    if (this.isAssetInWorkspace(path)) {
      return new VsCodeResourceContentService(this.getParentFolder(workspacePath));
    }
    return new VsCodeNodeResourceContentService(this.getParentFolder(path));
  }

  private isAssetInWorkspace(path: string): boolean {
    return vscode.workspace.workspaceFolders?.map(f => f.uri.path).find(p => path.startsWith(p)) !== undefined;
  }

  private getParentFolder(assetPath: string) {
    if (assetPath.includes(nodePath.sep)) {
      return assetPath.substring(0, assetPath.lastIndexOf(nodePath.sep) + 1);
    }
    return "";
  }
}
