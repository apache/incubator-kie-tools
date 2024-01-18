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

import { BackendProxy } from "@kie-tools-core/backend/dist/api";
import { KogitoEditorChannelApi } from "@kie-tools-core/editor/dist/api";
import { I18n } from "@kie-tools-core/i18n/dist/core";
import { NotificationsChannelApi } from "@kie-tools-core/notifications/dist/api";
import { VsCodeI18n } from "@kie-tools-core/vscode-extension/dist/i18n";
import { VsCodeKieEditorChannelApiProducer } from "@kie-tools-core/vscode-extension/dist/VsCodeKieEditorChannelApiProducer";
import { VsCodeKieEditorController } from "@kie-tools-core/vscode-extension/dist/VsCodeKieEditorController";
import { JavaCodeCompletionApi } from "@kie-tools-core/vscode-java-code-completion/dist/api";
import { ResourceContentService, WorkspaceChannelApi } from "@kie-tools-core/workspace/dist/api";
import { SwfVsCodeExtensionConfiguration } from "./configuration";
import { SwfLanguageServiceChannelApiImpl } from "./languageService/SwfLanguageServiceChannelApiImpl";
import { VsCodeSwfLanguageService } from "./languageService/VsCodeSwfLanguageService";
import { ServerlessWorkflowDiagramEditorChannelApiImpl } from "./ServerlessWorkflowDiagramEditorChannelApiImpl";
import { SwfServiceCatalogChannelApiImpl } from "./serviceCatalog/SwfServiceCatalogChannelApiImpl";
import { SwfServiceCatalogSupportActions } from "./serviceCatalog/SwfServiceCatalogSupportActions";
import { VsCodeWorkspaceChannelApiImpl } from "@kie-tools-core/vscode-extension/dist/workspace/VsCodeWorkspaceChannelApiImpl";
import { VsCodeNotificationsChannelApiImpl } from "@kie-tools-core/vscode-extension/dist/notifications/VsCodeNotificationsChannelApiImpl";

export class ServerlessWorkflowDiagramEditorChannelApiProducer implements VsCodeKieEditorChannelApiProducer {
  constructor(
    private readonly args: {
      configuration: SwfVsCodeExtensionConfiguration;
      vsCodeSwfLanguageService: VsCodeSwfLanguageService;
      swfServiceCatalogSupportActions: SwfServiceCatalogSupportActions;
    }
  ) {}
  get(
    editor: VsCodeKieEditorController,
    resourceContentService: ResourceContentService,
    vscodeWorkspace: VsCodeWorkspaceChannelApiImpl,
    backendProxy: BackendProxy,
    vscodeNotifications: VsCodeNotificationsChannelApiImpl,
    javaCodeCompletionApi: JavaCodeCompletionApi,
    viewType: string,
    i18n: I18n<VsCodeI18n>
  ): KogitoEditorChannelApi {
    return new ServerlessWorkflowDiagramEditorChannelApiImpl(
      editor,
      resourceContentService,
      vscodeWorkspace,
      backendProxy,
      vscodeNotifications,
      javaCodeCompletionApi,
      viewType,
      i18n,
      new SwfServiceCatalogChannelApiImpl({
        baseFileAbsolutePosixPath: editor.document.document.uri.path,
        configuration: this.args.configuration,
        swfServiceCatalogSupportActions: this.args.swfServiceCatalogSupportActions,
      }),
      new SwfLanguageServiceChannelApiImpl({
        ls: this.args.vsCodeSwfLanguageService.getLsForDiagramEditor(editor.document.document),
      })
    );
  }
}
