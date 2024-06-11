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

import {
  EditorContent,
  EditorTheme,
  KogitoEditorChannelApi,
  StateControlCommand,
} from "@kie-tools-core/editor/dist/api";
import { MessageBusClientApi, SharedValueProvider } from "@kie-tools-core/envelope-bus/dist/api";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import {
  WorkspaceEdit,
  ResourceContent,
  ResourceContentRequest,
  ResourceListRequest,
  ResourcesList,
} from "@kie-tools-core/workspace/dist/api";
import { ServerlessWorkflowDiagramEditorEnvelopeApi } from "@kie-tools/serverless-workflow-diagram-editor-envelope/dist/api";
import {
  SwfServiceCatalogChannelApi,
  SwfServiceCatalogService,
  SwfServiceRegistriesSettings,
} from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import { ServerlessWorkflowTextEditorChannelApi } from "@kie-tools/serverless-workflow-text-editor/dist/api";
import { CodeLens, CompletionItem, Position, Range } from "vscode-languageserver-types";

export class ServerlessWorkflowTextEditorChannelApiImpl implements ServerlessWorkflowTextEditorChannelApi {
  constructor(
    private readonly args: {
      defaultApiImpl: KogitoEditorChannelApi;
      channelApi: MessageBusClientApi<ServerlessWorkflowTextEditorChannelApi>;
      swfServiceCatalogApiImpl?: SwfServiceCatalogChannelApi;
      diagramEditorEnvelopeApi?: MessageBusClientApi<ServerlessWorkflowDiagramEditorEnvelopeApi>;
    }
  ) {}

  public kogitoEditor_contentRequest(): Promise<EditorContent> {
    return this.args.defaultApiImpl.kogitoEditor_contentRequest();
  }

  public kogitoEditor_ready(): void {
    this.args.defaultApiImpl.kogitoEditor_ready();
  }

  public kogitoEditor_setContentError(content: EditorContent): void {
    this.args.defaultApiImpl.kogitoEditor_setContentError(content);
  }

  public kogitoEditor_stateControlCommandUpdate(command: StateControlCommand) {
    this.args.defaultApiImpl.kogitoEditor_stateControlCommandUpdate(command);
  }

  public kogitoI18n_getLocale(): Promise<string> {
    return this.args.defaultApiImpl.kogitoI18n_getLocale();
  }

  public kogitoNotifications_createNotification(notification: Notification): void {
    this.args.defaultApiImpl.kogitoNotifications_createNotification(notification);
  }

  public kogitoNotifications_removeNotifications(normalizedPosixPathRelativeToTheWorkspaceRoot: string): void {
    this.args.defaultApiImpl.kogitoNotifications_removeNotifications(normalizedPosixPathRelativeToTheWorkspaceRoot);
  }

  public kogitoNotifications_setNotifications(
    normalizedPosixPathRelativeToTheWorkspaceRoot: string,
    notifications: Notification[]
  ): void {
    this.args.defaultApiImpl.kogitoNotifications_setNotifications(
      normalizedPosixPathRelativeToTheWorkspaceRoot,
      notifications
    );
  }

  public kogitoWorkspace_newEdit(edit: WorkspaceEdit): void {
    this.args.defaultApiImpl.kogitoWorkspace_newEdit(edit);
  }

  public kogitoWorkspace_openFile(normalizedPosixPathRelativeToTheWorkspaceRoot: string): void {
    this.args.defaultApiImpl.kogitoWorkspace_openFile(normalizedPosixPathRelativeToTheWorkspaceRoot);
  }

  public kogitoWorkspace_resourceContentRequest(request: ResourceContentRequest): Promise<ResourceContent | undefined> {
    return this.args.defaultApiImpl.kogitoWorkspace_resourceContentRequest(request);
  }

  public kogitoWorkspace_resourceListRequest(request: ResourceListRequest): Promise<ResourcesList> {
    return this.args.defaultApiImpl.kogitoWorkspace_resourceListRequest(request);
  }

  public kogitoEditor_theme(): SharedValueProvider<EditorTheme> {
    return this.args.defaultApiImpl.kogitoEditor_theme();
  }

  public kogitoSwfServiceCatalog_serviceRegistriesSettings(): SharedValueProvider<SwfServiceRegistriesSettings> {
    return {
      defaultValue: { registries: [] },
    };
  }

  public kogitoSwfServiceCatalog_services(): SharedValueProvider<SwfServiceCatalogService[]> {
    return this.args.swfServiceCatalogApiImpl?.kogitoSwfServiceCatalog_services() ?? { defaultValue: [] };
  }

  public kogitoSwfServiceCatalog_refresh(): void {
    this.args.channelApi.notifications.kogitoSwfServiceCatalog_refresh.send();
  }

  public kogitoSwfServiceCatalog_importFunctionFromCompletionItem(args: {
    containingService: SwfServiceCatalogService;
    documentUri: string;
  }): void {
    this.args.channelApi.notifications.kogitoSwfServiceCatalog_importFunctionFromCompletionItem.send(args);
  }

  public kogitoSwfServiceCatalog_importEventFromCompletionItem(args: {
    containingService: SwfServiceCatalogService;
    documentUri: string;
  }): void {
    this.args.channelApi.notifications.kogitoSwfServiceCatalog_importEventFromCompletionItem.send(args);
  }

  public kogitoSwfServiceCatalog_logInServiceRegistries(): void {
    this.args.channelApi.notifications.kogitoSwfServiceCatalog_logInServiceRegistries.send();
  }

  public async kogitoSwfLanguageService__getCompletionItems(args: {
    content: string;
    uri: string;
    cursorPosition: Position;
    cursorWordRange: Range;
  }): Promise<CompletionItem[]> {
    return this.args.channelApi.requests.kogitoSwfLanguageService__getCompletionItems(args);
  }

  public async kogitoSwfLanguageService__getCodeLenses(args: { uri: string; content: string }): Promise<CodeLens[]> {
    return this.args.channelApi.requests.kogitoSwfLanguageService__getCodeLenses(args);
  }

  public kogitoSwfServiceCatalog_setupServiceRegistriesSettings(): void {
    this.args.channelApi.notifications.kogitoSwfServiceCatalog_setupServiceRegistriesSettings.send();
  }

  public kogitoSwfTextEditor__onSelectionChanged(args: { nodeName: string }): void {
    this.args.diagramEditorEnvelopeApi?.notifications.kogitoSwfDiagramEditor__highlightNode.send(args);
  }
}
