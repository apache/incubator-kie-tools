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

import { VsCodeBackendProxy } from "@kie-tools-core/backend/dist/vscode";
import { EditorEnvelopeLocator } from "@kie-tools-core/editor/dist/api";
import { I18n } from "@kie-tools-core/i18n/dist/core";
import { VsCodeWorkspaceApi } from "@kie-tools-core/workspace/dist/vscode";
import * as vscode from "vscode";
import { EnvelopeBusMessageBroadcaster } from "./EnvelopeBusMessageBroadcaster";
import { generateSvg } from "./generateSvg";
import { vsCodeI18nDefaults, vsCodeI18nDictionaries } from "./i18n";
import { KogitoEditorFactory } from "./KogitoEditorFactory";
import { KogitoEditorStore } from "./KogitoEditorStore";
import { KogitoEditorWebviewProvider } from "./KogitoEditorWebviewProvider";
import { VsCodeNotificationsApi } from "@kie-tools-core/notifications/dist/vscode";
import { VsCodeJavaCodeCompletionImpl } from "@kie-tools-core/vscode-java-code-completion/dist/vscode";
import { KogitoEditorChannelApiProducer } from "./KogitoEditorChannelApiProducer";

/**
 * Starts a Kogito extension.
 *
 *  @param args.extensionName The extension name. Used to fetch the extension configuration for supported languages.
 *  @param args.webviewLocation The relative path to search for an "index.js" file for the WebView panel.
 *  @param args.context The vscode.ExtensionContext provided on the activate method of the extension.
 *  @param args.routes The routes to be used to find resources for each language.
 *  @param args.backendProxy The proxy between channels and available backend services.
 *  @param args.channelApiProducer Optional producer of custom KogitoEditorChannelApi instances.
 */
export async function startExtension(args: {
  extensionName: string;
  context: vscode.ExtensionContext;
  viewType: string;
  generateSvgCommandId: string;
  silentlyGenerateSvgCommandId: string;
  editorEnvelopeLocator: EditorEnvelopeLocator;
  backendProxy: VsCodeBackendProxy;
  channelApiProducer?: KogitoEditorChannelApiProducer;
}) {
  await args.backendProxy.tryLoadBackendExtension(true);

  const vsCodeI18n = new I18n(vsCodeI18nDefaults, vsCodeI18nDictionaries, vscode.env.language);
  const workspaceApi = new VsCodeWorkspaceApi();
  const editorStore = new KogitoEditorStore();
  const messageBroadcaster = new EnvelopeBusMessageBroadcaster();
  const vsCodeNotificationsApi = new VsCodeNotificationsApi(workspaceApi);
  const vsCodeJavaCodeCompletionChannelApi = new VsCodeJavaCodeCompletionImpl();
  const editorFactory = new KogitoEditorFactory(
    args.context,
    editorStore,
    args.editorEnvelopeLocator,
    messageBroadcaster,
    workspaceApi,
    args.backendProxy,
    vsCodeNotificationsApi,
    vsCodeJavaCodeCompletionChannelApi,
    args.viewType,
    vsCodeI18n,
    args.channelApiProducer
  );

  const editorWebviewProvider = new KogitoEditorWebviewProvider(
    args.context,
    args.viewType,
    editorStore,
    editorFactory,
    vsCodeI18n,
    vsCodeNotificationsApi,
    args.editorEnvelopeLocator
  );

  args.context.subscriptions.push(
    vscode.window.registerCustomEditorProvider(args.viewType, editorWebviewProvider, {
      webviewOptions: { retainContextWhenHidden: true },
    })
  );

  args.context.subscriptions.push(
    vscode.commands.registerCommand(args.generateSvgCommandId, () =>
      generateSvg({
        editorStore: editorStore,
        workspaceApi: workspaceApi,
        vsCodeI18n: vsCodeI18n,
        displayNotification: true,
        editorEnvelopeLocator: args.editorEnvelopeLocator,
      })
    )
  );

  args.context.subscriptions.push(
    vscode.commands.registerCommand(args.silentlyGenerateSvgCommandId, () =>
      generateSvg({
        editorStore: editorStore,
        workspaceApi: workspaceApi,
        vsCodeI18n: vsCodeI18n,
        displayNotification: false,
        editorEnvelopeLocator: args.editorEnvelopeLocator,
      })
    )
  );
}
