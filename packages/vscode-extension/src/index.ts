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

import { VsCodeBackendProxy } from "@kie-tooling-core/backend/dist/vscode";
import { EditorEnvelopeLocator } from "@kie-tooling-core/editor/dist/api";
import { I18n } from "@kie-tooling-core/i18n/dist/core";
import { VsCodeWorkspaceApi } from "@kie-tooling-core/workspace/dist/vscode";
import * as vscode from "vscode";
import { EnvelopeBusMessageBroadcaster } from "./EnvelopeBusMessageBroadcaster";
import { generateSvg } from "./generateSvg";
import { vsCodeI18nDefaults, vsCodeI18nDictionaries } from "./i18n";
import { KogitoEditorFactory } from "./KogitoEditorFactory";
import { KogitoEditorStore } from "./KogitoEditorStore";
import { KogitoEditorWebviewProvider } from "./KogitoEditorWebviewProvider";
import { VsCodeNotificationsApi } from "@kie-tooling-core/notifications/dist/vscode";

/**
 * Starts a Kogito extension.
 *
 *  @param args.extensionName The extension name. Used to fetch the extension configuration for supported languages.
 *  @param args.webviewLocation The relative path to search for an "index.js" file for the WebView panel.
 *  @param args.context The vscode.ExtensionContext provided on the activate method of the extension.
 *  @param args.routes The routes to be used to find resources for each language.
 *  @param args.backendProxy The proxy between channels and available backend services.
 */
export async function startExtension(args: {
  extensionName: string;
  context: vscode.ExtensionContext;
  viewType: string;
  getPreviewCommandId: string;
  editorEnvelopeLocator: EditorEnvelopeLocator;
  backendProxy: VsCodeBackendProxy;
}) {
  await args.backendProxy.tryLoadBackendExtension(true);

  const vsCodeI18n = new I18n(vsCodeI18nDefaults, vsCodeI18nDictionaries, vscode.env.language);
  const workspaceApi = new VsCodeWorkspaceApi();
  const editorStore = new KogitoEditorStore();
  const messageBroadcaster = new EnvelopeBusMessageBroadcaster();
  const vsCodeNotificationsApi = new VsCodeNotificationsApi(workspaceApi);
  const editorFactory = new KogitoEditorFactory(
    args.context,
    editorStore,
    args.editorEnvelopeLocator,
    messageBroadcaster,
    workspaceApi,
    args.backendProxy,
    vsCodeNotificationsApi,
    args.viewType,
    vsCodeI18n
  );

  const editorWebviewProvider = new KogitoEditorWebviewProvider(
    args.context,
    args.viewType,
    editorStore,
    editorFactory,
    vsCodeI18n,
    vsCodeNotificationsApi
  );

  args.context.subscriptions.push(
    vscode.window.registerCustomEditorProvider(args.viewType, editorWebviewProvider, {
      webviewOptions: { retainContextWhenHidden: true },
    })
  );

  args.context.subscriptions.push(
    vscode.commands.registerCommand(args.getPreviewCommandId, () => generateSvg(editorStore, workspaceApi, vsCodeI18n))
  );
}
