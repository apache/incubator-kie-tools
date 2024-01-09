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

import * as vscode from "vscode";
import { Uri, Webview } from "vscode";
import * as __path from "path";
import { NotificationsChannelApi } from "@kie-tools-core/notifications/dist/api";
import { BackendProxy } from "@kie-tools-core/backend/dist/api";
import { ResourceContentService, WorkspaceChannelApi } from "@kie-tools-core/workspace/dist/api";
import {
  EditorEnvelopeLocator,
  EnvelopeContent,
  EnvelopeContentType,
  EnvelopeMapping,
  KogitoEditorChannelApi,
} from "@kie-tools-core/editor/dist/api";
import { EnvelopeBusMessageBroadcaster } from "./EnvelopeBusMessageBroadcaster";
import { VsCodeKieEditorController, KogitoEditorDocument } from "./VsCodeKieEditorController";
import { VsCodeKieEditorStore } from "./VsCodeKieEditorStore";
import { VsCodeResourceContentServiceForDanglingFiles } from "./workspace/VsCodeResourceContentServiceForDanglingFiles";
import { VsCodeResourceContentServiceForWorkspaces } from "./workspace/VsCodeResourceContentServiceForWorkspaces";
import { I18n } from "@kie-tools-core/i18n/dist/core";
import { VsCodeI18n } from "./i18n";
import { JavaCodeCompletionApi } from "@kie-tools-core/vscode-java-code-completion/dist/api";
import {
  DefaultVsCodeEditorChannelApiProducer,
  VsCodeKieEditorChannelApiProducer,
} from "./VsCodeKieEditorChannelApiProducer";
import { getWorkspaceRoot } from "./workspace/workspaceRoot";
import { VsCodeNotificationsChannelApiImpl } from "./notifications/VsCodeNotificationsChannelApiImpl";
import { VsCodeWorkspaceChannelApiImpl } from "./workspace/VsCodeWorkspaceChannelApiImpl";

export class VsCodeKieEditorControllerFactory {
  constructor(
    private readonly context: vscode.ExtensionContext,
    private readonly editorStore: VsCodeKieEditorStore,
    private readonly editorEnvelopeLocator: EditorEnvelopeLocator,
    private readonly messageBroadcaster: EnvelopeBusMessageBroadcaster,
    private readonly vscodeWorkspace: VsCodeWorkspaceChannelApiImpl,
    private readonly backendProxy: BackendProxy,
    private readonly vscodeNotifications: VsCodeNotificationsChannelApiImpl,
    private readonly javaCodeCompletionApi: JavaCodeCompletionApi,
    private readonly viewType: string,
    private readonly i18n: I18n<VsCodeI18n>,
    private readonly channelApiProducer: VsCodeKieEditorChannelApiProducer = new DefaultVsCodeEditorChannelApiProducer()
  ) {}

  public configureNew(webviewPanel: vscode.WebviewPanel, document: KogitoEditorDocument) {
    webviewPanel.webview.options = {
      enableCommandUris: true,
      enableScripts: true,
      localResourceRoots: [this.context.extensionUri],
    };

    const editorEnvelopeLocator = this.getEditorEnvelopeLocatorForWebview(webviewPanel.webview);
    const resourceContentService = this.createResourceContentService(document);

    const envelopeMapping = editorEnvelopeLocator.getEnvelopeMapping(document.document.uri.fsPath);
    if (!envelopeMapping) {
      throw new Error(`No envelope mapping found for '${document.document.uri}'`);
    }

    const editor = new VsCodeKieEditorController(
      document,
      webviewPanel,
      this.context,
      this.editorStore,
      envelopeMapping,
      editorEnvelopeLocator,
      this.messageBroadcaster
    );

    const editorChannelApi = this.getChannelApi(editor, resourceContentService);

    this.editorStore.addAsActive(editor);
    editor.startListening(editorChannelApi);
    editor.startInitPolling(editorChannelApi);
    editor.setupPanelActiveStatusChange();
    editor.setupPanelOnDidDispose();
    editor.setupWebviewContent();
    editor.startListeningToThemeChanges();
    editor.startListeningToDocumentChanges();
  }

  public createResourceContentService(document: KogitoEditorDocument): ResourceContentService {
    const workspaceRoot = getWorkspaceRoot(document.document);
    if (workspaceRoot.type === "workspace") {
      return new VsCodeResourceContentServiceForWorkspaces({
        workspaceRootAbsoluteFsPath: workspaceRoot.workspaceRootAbsoluteFsPath,
        document: document.document,
      });
    } else if (workspaceRoot.type === "dangling") {
      return new VsCodeResourceContentServiceForDanglingFiles({ openFileAbsoluteFsPath: document.document.uri.fsPath });
    } else {
      throw new Error("Unknown workspaceRoot type: " + workspaceRoot.type);
    }
  }

  private getChannelApi(
    editor: VsCodeKieEditorController,
    resourceContentService: ResourceContentService
  ): KogitoEditorChannelApi {
    return this.channelApiProducer.get(
      editor,
      resourceContentService,
      this.vscodeWorkspace,
      this.backendProxy,
      this.vscodeNotifications,
      this.javaCodeCompletionApi,
      this.viewType,
      this.i18n
    );
  }

  private getEditorEnvelopeLocatorForWebview(webview: vscode.Webview): EditorEnvelopeLocator {
    return new EditorEnvelopeLocator(
      this.editorEnvelopeLocator.targetOrigin,
      [...this.editorEnvelopeLocator.envelopeMappings].reduce((envelopeMappings, mapping) => {
        envelopeMappings.push(
          new EnvelopeMapping({
            type: mapping.type,
            filePathGlob: mapping.filePathGlob,
            resourcesPathPrefix: this.getWebviewPath(webview, mapping.resourcesPathPrefix),
            envelopeContent: {
              type: EnvelopeContentType.PATH,
              path: this.getWebViewPathFromEnvelopeContent(webview, mapping.envelopeContent),
            },
          })
        );
        return envelopeMappings;
      }, [] as EnvelopeMapping[])
    );
  }

  private getWebViewPathFromEnvelopeContent(webview: Webview, envelopeContent: EnvelopeContent) {
    if (envelopeContent.type === EnvelopeContentType.PATH) {
      return this.getWebviewPath(webview, envelopeContent.path);
    }

    return "";
  }

  private getWebviewPath(webview: Webview, relativeUriPath: string) {
    return webview.asWebviewUri(Uri.joinPath(this.context.extensionUri, relativeUriPath)).toString();
  }

  private getParentFolder(absoluteFsPath: string) {
    if (absoluteFsPath.includes(__path.sep)) {
      return absoluteFsPath.substring(0, absoluteFsPath.lastIndexOf(__path.sep) + 1);
    }
    return "";
  }
}
