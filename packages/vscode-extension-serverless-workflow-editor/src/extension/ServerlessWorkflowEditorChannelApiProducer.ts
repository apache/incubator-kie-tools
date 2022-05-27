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
import { SwfServiceCatalogChannelApiImpl } from "./serviceCatalog/SwfServiceCatalogChannelApiImpl";
import { EnvelopeServer } from "@kie-tools-core/envelope-bus/dist/channel";
import { SwfServiceCatalogChannelApi } from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import { CONFIGURATION_SECTIONS, SwfVsCodeExtensionConfiguration } from "./configuration";
import { SwfServiceCatalogSupportActions } from "./serviceCatalog/SwfServiceCatalogSupportActions";
import { SwfLanguageServiceChannelApiImpl } from "./languageService/SwfLanguageServiceChannelApiImpl";
import { SwfJsonLanguageService } from "@kie-tools/serverless-workflow-language-service/dist/channel";

export class ServerlessWorkflowEditorChannelApiProducer implements KogitoEditorChannelApiProducer {
  constructor(
    private readonly args: {
      configuration: SwfVsCodeExtensionConfiguration;
      swfLanguageService: SwfJsonLanguageService;
      swfServiceCatalogSupportActions: SwfServiceCatalogSupportActions;
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
    i18n: I18n<VsCodeI18n>
  ): KogitoEditorChannelApi {
    // TODO: This casting to `unknown` first is a workaround
    // See https://issues.redhat.com/browse/KOGITO-6824
    const swfServiceCatalogEnvelopeServer = editor.envelopeServer as unknown as EnvelopeServer<
      SwfServiceCatalogChannelApi,
      KogitoEditorEnvelopeApi
    >;

    return new ServerlessWorkflowEditorChannelApiImpl(
      editor,
      resourceContentService,
      workspaceApi,
      backendProxy,
      notificationsApi,
      javaCodeCompletionApi,
      viewType,
      i18n,
      new SwfServiceCatalogChannelApiImpl({
        baseFileAbsolutePosixPath: editor.document.document.uri.path,
        configuration: this.args.configuration,
        swfServiceCatalogSupportActions: this.args.swfServiceCatalogSupportActions,
      }),
      new SwfLanguageServiceChannelApiImpl({
        ls: this.args.swfLanguageService,
      })
    );
  }
}
