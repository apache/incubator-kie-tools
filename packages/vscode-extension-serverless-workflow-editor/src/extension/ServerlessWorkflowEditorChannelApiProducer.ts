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

import * as vscode from "vscode";
import { AuthenticationSession, Uri } from "vscode";
import { KogitoEditorChannelApiProducer } from "@kie-tools-core/vscode-extension/dist/KogitoEditorChannelApiProducer";
import { ServerlessWorkflowEditorChannelApiImpl } from "./ServerlessWorkflowEditorChannelApiImpl";
import { KogitoEditor } from "@kie-tools-core/vscode-extension/dist/KogitoEditor";
import { ResourceContentService, WorkspaceApi } from "@kie-tools-core/workspace/dist/api";
import { BackendProxy } from "@kie-tools-core/backend/dist/api";
import { NotificationsApi } from "@kie-tools-core/notifications/dist/api";
import { JavaCodeCompletionApi } from "@kie-tools-core/vscode-java-code-completion/dist/api";
import { I18n } from "@kie-tools-core/i18n/dist/core";
import { VsCodeI18n } from "@kie-tools-core/vscode-extension/dist/i18n";
import { KogitoEditorChannelApi, KogitoEditorEnvelopeApi } from "@kie-tools-core/editor/dist/api";
import { getSwfServiceCatalogStore } from "./serviceCatalog";
import { SwfServiceCatalogChannelApiImpl } from "@kie-tools/serverless-workflow-service-catalog/dist/channel";
import { EnvelopeServer } from "@kie-tools-core/envelope-bus/dist/channel";
import {
  SwfServiceCatalogChannelApi,
  SwfServiceCatalogService,
} from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import { SwfVsCodeExtensionSettings } from "./settings";
import { RhhccAuthenticationStore } from "./rhhcc/RhhccAuthenticationStore";
import { SwfServiceCatalogFunction } from "@kie-tools/serverless-workflow-service-catalog/dist/api";

export class ServerlessWorkflowEditorChannelApiProducer implements KogitoEditorChannelApiProducer {
  constructor(
    private readonly args: { settings: SwfVsCodeExtensionSettings; rhhccAuthenticationStore: RhhccAuthenticationStore }
  ) {}
  get(
    editor: KogitoEditor,
    resourceContentService: ResourceContentService,
    workspaceApi: WorkspaceApi,
    backendProxy: BackendProxy,
    notificationsApi: NotificationsApi,
    javaCodeCompletionApi: JavaCodeCompletionApi,
    viewType: string,
    i18n: I18n<VsCodeI18n>,
    initialBackup?: Uri
  ): KogitoEditorChannelApi {
    const swfServiceCatalogStore = getSwfServiceCatalogStore({
      filePath: editor.document.uri.path,
      configuredSpecsDirPath: this.args.settings.getSpecsDirPath(),
      rhhccAuthenticationStore: this.args.rhhccAuthenticationStore,
    });

    // TODO: This is a workaround
    const swfServiceCatalogEnvelopeServer = editor.envelopeServer as unknown as EnvelopeServer<
      SwfServiceCatalogChannelApi,
      KogitoEditorEnvelopeApi
    >;

    swfServiceCatalogStore.init(async (services) =>
      swfServiceCatalogEnvelopeServer.shared.kogitoSwfServiceCatalog_services.set(services)
    );

    const rhhccSessionSubscription = this.args.rhhccAuthenticationStore.subscribe((session) => {
      swfServiceCatalogEnvelopeServer.shared.kogitoSwfServiceCatalog_user.set(getUser(session));
    });

    editor.panel.onDidDispose(() => {
      swfServiceCatalogStore.dispose();
      this.args.rhhccAuthenticationStore.unsubscribe(rhhccSessionSubscription);
    });

    return new ServerlessWorkflowEditorChannelApiImpl(
      editor,
      resourceContentService,
      workspaceApi,
      backendProxy,
      notificationsApi,
      javaCodeCompletionApi,
      viewType,
      i18n,
      initialBackup,
      new SwfServiceCatalogChannelApiImpl({
        defaultUser: getUser(this.args.rhhccAuthenticationStore.session),
        onRefresh: () => {
          vscode.window.setStatusBarMessage(
            "Serverless Workflow Editor: Refreshing Service Catalog using Service Registries from Red Hat Hybrid Cloud Console...",
            3000
          );
          return swfServiceCatalogStore.refresh();
        },
        onLogInToRhhcc: () => vscode.commands.executeCommand("extension.kogito.swf.logInToRhhcc"),
        onImportFunctionFromCompletionItem: (
          service: SwfServiceCatalogService,
          importedFunction: SwfServiceCatalogFunction
        ) => {
          vscode.window.showInformationMessage(JSON.stringify(service) + JSON.stringify(importedFunction));
        },
      })
    );
  }
}

function getUser(session: AuthenticationSession | undefined) {
  return session ? { username: session.account.label } : undefined;
}
