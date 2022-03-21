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
import { SwfServiceCatalogStore } from "./serviceCatalog/SwfServiceCatalogStore";
import { SwfServiceCatalogChannelApiImpl } from "./serviceCatalog/SwfServiceCatalogChannelApiImpl";
import { EnvelopeServer } from "@kie-tools-core/envelope-bus/dist/channel";
import { SwfServiceCatalogChannelApi } from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import { CONFIGURATION_SECTIONS, SwfVsCodeExtensionSettings } from "./settings";
import { RhhccAuthenticationStore } from "./rhhcc/RhhccAuthenticationStore";
import { SwfServiceCatalogUser } from "@kie-tools/serverless-workflow-service-catalog/src/api";
import { RhhccServiceRegistryServiceCatalogStore } from "./serviceCatalog/rhhccServiceRegistry/RhhccServiceRegistryServiceCatalogStore";
import { FsWatchingServiceCatalogStore } from "./serviceCatalog/fs";
import { askForServiceRegistryUrl } from "./serviceCatalog/rhhccServiceRegistry";

export class ServerlessWorkflowEditorChannelApiProducer implements KogitoEditorChannelApiProducer {
  constructor(
    private readonly args: {
      settings: SwfVsCodeExtensionSettings;
      rhhccAuthenticationStore: RhhccAuthenticationStore;
    }
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
    const rhhccServiceRegistryServiceCatalogStore = new RhhccServiceRegistryServiceCatalogStore({
      baseFileAbsolutePath: editor.document.uri.path,
      rhhccAuthenticationStore: this.args.rhhccAuthenticationStore,
      settings: this.args.settings,
    });

    const fsWatchingServiceCatalogStore = new FsWatchingServiceCatalogStore({
      baseFileAbsolutePath: editor.document.uri.path,
      settings: this.args.settings,
    });

    const swfServiceCatalogStore = new SwfServiceCatalogStore({
      fsWatchingServiceCatalogStore,
      rhhccServiceRegistryServiceCatalogStore,
    });

    // TODO: This is a workaround
    const swfServiceCatalogEnvelopeServer = editor.envelopeServer as unknown as EnvelopeServer<
      SwfServiceCatalogChannelApi,
      KogitoEditorEnvelopeApi
    >;

    swfServiceCatalogStore.init({
      onNewServices: async (services) => {
        swfServiceCatalogEnvelopeServer.shared.kogitoSwfServiceCatalog_services.set(services);
      },
    });

    const rhhccSessionSubscription = this.args.rhhccAuthenticationStore.subscribeToSessionChange(async (session) => {
      swfServiceCatalogEnvelopeServer.shared.kogitoSwfServiceCatalog_user.set(getUser(session));

      if (!session) {
        return rhhccServiceRegistryServiceCatalogStore.refresh();
      }

      const configuredServiceRegistryUrl = this.args.settings.getServiceRegistryUrl();
      if (configuredServiceRegistryUrl) {
        return rhhccServiceRegistryServiceCatalogStore.refresh();
      }

      const serviceRegistryUrl = await askForServiceRegistryUrl({ currentValue: configuredServiceRegistryUrl });
      vscode.workspace.getConfiguration().update(CONFIGURATION_SECTIONS.serviceRegistryUrl, serviceRegistryUrl);
      vscode.window.setStatusBarMessage("Serverless Workflow: Service Registry URL saved.", 3000);

      return rhhccServiceRegistryServiceCatalogStore.refresh();
    });

    const rhhccServiceRegistryUrlSubscription = vscode.workspace.onDidChangeConfiguration(async (e) => {
      if (e.affectsConfiguration(CONFIGURATION_SECTIONS.serviceRegistryUrl)) {
        swfServiceCatalogEnvelopeServer.shared.kogitoSwfServiceCatalog_serviceRegistryUrl.set(
          this.args.settings.getServiceRegistryUrl()
        );
      }
    });

    editor.panel.onDidDispose(() => {
      swfServiceCatalogStore.dispose();
      rhhccServiceRegistryUrlSubscription.dispose();
      this.args.rhhccAuthenticationStore.unsubscribeToSessionChange(rhhccSessionSubscription);
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
        swfServiceCatalogStore,
        baseFileAbsolutePath: editor.document.uri.path,
        settings: this.args.settings,
        defaultUser: getUser(this.args.rhhccAuthenticationStore.session),
        defaultServiceRegistryUrl: this.args.settings.getServiceRegistryUrl(),
      })
    );
  }
}

function getUser(session: AuthenticationSession | undefined): SwfServiceCatalogUser | undefined {
  return session ? { username: session.account.label } : undefined;
}
