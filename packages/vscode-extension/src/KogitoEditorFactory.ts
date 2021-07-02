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
import { Uri, Webview } from "vscode";
import * as nodePath from "path";
import { NotificationsApi } from "@kie-tooling-core/notifications/dist/api";
import { BackendProxy } from "@kie-tooling-core/backend/dist/api";
import { ResourceContentService } from "@kie-tooling-core/workspace/dist/api";
import { EditorEnvelopeLocator, EnvelopeMapping } from "@kie-tooling-core/editor/dist/api";
import { WorkspaceApi } from "@kie-tooling-core/workspace/dist/api";
import { EnvelopeBusMessageBroadcaster } from "./EnvelopeBusMessageBroadcaster";
import { KogitoEditableDocument } from "./KogitoEditableDocument";
import { KogitoEditor } from "./KogitoEditor";
import { KogitoEditorChannelApiImpl } from "./KogitoEditorChannelApiImpl";
import { KogitoEditorStore } from "./KogitoEditorStore";
import { VsCodeNodeResourceContentService } from "./VsCodeNodeResourceContentService";
import { VsCodeResourceContentService } from "./VsCodeResourceContentService";
import { I18n } from "@kie-tooling-core/i18n/dist/core";
import { VsCodeI18n } from "./i18n";

export class KogitoEditorFactory {
  constructor(
    private readonly context: vscode.ExtensionContext,
    private readonly editorStore: KogitoEditorStore,
    private readonly editorEnvelopeLocator: EditorEnvelopeLocator,
    private readonly messageBroadcaster: EnvelopeBusMessageBroadcaster,
    private readonly workspaceApi: WorkspaceApi,
    private readonly backendProxy: BackendProxy,
    private readonly notificationsApi: NotificationsApi,
    private readonly viewType: string,
    private readonly i18n: I18n<VsCodeI18n>
  ) {}

  public configureNew(webviewPanel: vscode.WebviewPanel, document: KogitoEditableDocument) {
    webviewPanel.webview.options = {
      enableCommandUris: true,
      enableScripts: true,
      localResourceRoots: [vscode.Uri.file(this.context.extensionPath)],
    };

    const editorEnvelopeLocator = this.getEditorEnvelopeLocatorForWebview(webviewPanel.webview);
    const resourceContentService = this.createResourceContentService(document.uri.fsPath, document.relativePath);

    const envelopeMapping = editorEnvelopeLocator.mapping.get(document.fileExtension);
    if (!envelopeMapping) {
      throw new Error(`No envelope mapping found for '${document.fileExtension}'`);
    }

    const editor = new KogitoEditor(
      document,
      webviewPanel,
      this.context,
      this.editorStore,
      envelopeMapping,
      editorEnvelopeLocator,
      this.messageBroadcaster
    );

    const editorChannelApi = new KogitoEditorChannelApiImpl(
      editor,
      resourceContentService,
      this.workspaceApi,
      this.backendProxy,
      this.notificationsApi,
      this.viewType,
      this.i18n
    );

    this.editorStore.addAsActive(editor);
    editor.startListening(editorChannelApi);
    editor.startInitPolling();
    editor.setupPanelActiveStatusChange();
    editor.setupPanelOnDidDispose();
    editor.setupWebviewContent();
  }

  private getEditorEnvelopeLocatorForWebview(webview: vscode.Webview): EditorEnvelopeLocator {
    return {
      targetOrigin: this.editorEnvelopeLocator.targetOrigin,
      mapping: [...this.editorEnvelopeLocator.mapping.entries()].reduce((mapping, [fileExtension, m]) => {
        mapping.set(fileExtension, {
          envelopePath: this.getWebviewPath(webview, m.envelopePath),
          resourcesPathPrefix: this.getWebviewPath(webview, m.resourcesPathPrefix),
        });
        return mapping;
      }, new Map<string, EnvelopeMapping>()),
    };
  }

  private getWebviewPath(webview: Webview, relativePath: string) {
    return webview.asWebviewUri(Uri.file(this.context.asAbsolutePath(relativePath))).toString();
  }

  public createResourceContentService(path: string, workspacePath: string): ResourceContentService {
    if (this.isAssetInWorkspace(path)) {
      return new VsCodeResourceContentService(this.getParentFolder(workspacePath));
    } else {
      return new VsCodeNodeResourceContentService(this.getParentFolder(path));
    }
  }

  private isAssetInWorkspace(path: string): boolean {
    return vscode.workspace.workspaceFolders?.map((f) => f.uri.fsPath).find((p) => path.startsWith(p)) !== undefined;
  }

  private getParentFolder(assetPath: string) {
    if (assetPath.includes(nodePath.sep)) {
      return assetPath.substring(0, assetPath.lastIndexOf(nodePath.sep) + 1);
    }
    return "";
  }
}
