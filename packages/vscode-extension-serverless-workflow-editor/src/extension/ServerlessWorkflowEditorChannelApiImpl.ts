/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

import { KogitoEditorChannelApiImpl } from "@kie-tools-core/vscode-extension/dist/KogitoEditorChannelApiImpl";
import {
  EditorContent,
  EditorTheme,
  KogitoEditorChannelApi,
  StateControlCommand,
} from "@kie-tools-core/editor/dist/api";
import { KogitoEditor } from "@kie-tools-core/vscode-extension/dist/KogitoEditor";
import {
  KogitoEdit,
  ResourceContent,
  ResourceContentRequest,
  ResourceContentService,
  ResourceListRequest,
  ResourcesList,
  WorkspaceApi,
} from "@kie-tools-core/workspace/dist/api";
import { BackendProxy } from "@kie-tools-core/backend/dist/api";
import { Notification, NotificationsApi } from "@kie-tools-core/notifications/dist/api";
import { JavaCodeCompletionApi } from "@kie-tools-core/vscode-java-code-completion/dist/api";
import { VsCodeI18n } from "@kie-tools-core/vscode-extension/dist/i18n";
import { I18n } from "@kie-tools-core/i18n/dist/core";
import {
  SwfServiceCatalogChannelApi,
  SwfServiceCatalogService,
  SwfServiceCatalogUser,
} from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import { Tutorial, UserInteraction } from "@kie-tools-core/guided-tour/dist/api";
import { SharedValueProvider } from "@kie-tools-core/envelope-bus/dist/api";
import { ServerlessWorkflowEditorChannelApi } from "@kie-tools/serverless-workflow-editor";
import { SwfLanguageServiceChannelApi } from "@kie-tools/serverless-workflow-language-service";
import * as vscode from "vscode";
import { CompletionItem, CodeLens, TextDocumentIdentifier, Position, Range } from "vscode-languageserver-types";

export class ServerlessWorkflowEditorChannelApiImpl implements ServerlessWorkflowEditorChannelApi {
  private readonly defaultApiImpl: KogitoEditorChannelApi;

  constructor(
    private readonly editor: KogitoEditor,
    resourceContentService: ResourceContentService,
    workspaceApi: WorkspaceApi,
    backendProxy: BackendProxy,
    notificationsApi: NotificationsApi,
    javaCodeCompletionApi: JavaCodeCompletionApi,
    viewType: string,
    i18n: I18n<VsCodeI18n>,
    initialBackup = editor.document.initialBackup,
    private readonly swfServiceCatalogApiImpl: SwfServiceCatalogChannelApi,
    private readonly swfLanguageServiceChannelApiImpl: SwfLanguageServiceChannelApi
  ) {
    this.defaultApiImpl = new KogitoEditorChannelApiImpl(
      editor,
      resourceContentService,
      workspaceApi,
      backendProxy,
      notificationsApi,
      javaCodeCompletionApi,
      viewType,
      i18n,
      initialBackup
    );
  }

  public kogitoEditor_contentRequest(): Promise<EditorContent> {
    return this.defaultApiImpl.kogitoEditor_contentRequest();
  }

  public kogitoEditor_ready(): void {
    this.defaultApiImpl.kogitoEditor_contentRequest();
  }

  public kogitoEditor_setContentError(content: EditorContent): void {
    this.defaultApiImpl.kogitoEditor_setContentError(content);
  }

  public kogitoEditor_stateControlCommandUpdate(command: StateControlCommand) {
    switch (command) {
      case StateControlCommand.REDO:
        vscode.commands.executeCommand("redo");
        break;
      case StateControlCommand.UNDO:
        vscode.commands.executeCommand("undo");
        break;
      default:
        console.info(`Unknown message type received: ${command}`);
        break;
    }
  }

  public kogitoGuidedTour_guidedTourRegisterTutorial(tutorial: Tutorial): void {
    this.defaultApiImpl.kogitoGuidedTour_guidedTourRegisterTutorial(tutorial);
  }

  public kogitoGuidedTour_guidedTourUserInteraction(userInteraction: UserInteraction): void {
    this.defaultApiImpl.kogitoGuidedTour_guidedTourUserInteraction(userInteraction);
  }

  public kogitoI18n_getLocale(): Promise<string> {
    return this.defaultApiImpl.kogitoI18n_getLocale();
  }

  public kogitoNotifications_createNotification(notification: Notification): void {
    this.defaultApiImpl.kogitoNotifications_createNotification(notification);
  }

  public kogitoNotifications_removeNotifications(path: string): void {
    this.defaultApiImpl.kogitoNotifications_removeNotifications(path);
  }

  public kogitoNotifications_setNotifications(path: string, notifications: Notification[]): void {
    this.defaultApiImpl.kogitoNotifications_setNotifications(path, notifications);
  }

  public kogitoWorkspace_newEdit(edit: KogitoEdit): void {
    this.defaultApiImpl.kogitoWorkspace_newEdit(edit);
  }

  public kogitoWorkspace_openFile(path: string): void {
    this.defaultApiImpl.kogitoWorkspace_openFile(path);
  }

  public kogitoWorkspace_resourceContentRequest(request: ResourceContentRequest): Promise<ResourceContent | undefined> {
    return this.defaultApiImpl.kogitoWorkspace_resourceContentRequest(request);
  }

  public kogitoWorkspace_resourceListRequest(request: ResourceListRequest): Promise<ResourcesList> {
    return this.defaultApiImpl.kogitoWorkspace_resourceListRequest(request);
  }

  public kogitoEditor_theme(): SharedValueProvider<EditorTheme> {
    return this.defaultApiImpl.kogitoEditor_theme();
  }

  public kogitoSwfServiceCatalog_services(): SharedValueProvider<SwfServiceCatalogService[]> {
    return this.swfServiceCatalogApiImpl.kogitoSwfServiceCatalog_services();
  }

  public kogitoSwfServiceCatalog_user(): SharedValueProvider<SwfServiceCatalogUser | undefined> {
    return this.swfServiceCatalogApiImpl.kogitoSwfServiceCatalog_user();
  }

  public kogitoSwfServiceCatalog_serviceRegistryUrl(): SharedValueProvider<string | undefined> {
    return this.swfServiceCatalogApiImpl.kogitoSwfServiceCatalog_serviceRegistryUrl();
  }

  public kogitoSwfServiceCatalog_refresh(): void {
    this.swfServiceCatalogApiImpl.kogitoSwfServiceCatalog_refresh();
  }

  public kogitoSwfServiceCatalog_logInToRhhcc(): void {
    this.swfServiceCatalogApiImpl.kogitoSwfServiceCatalog_logInToRhhcc();
  }

  public kogitoSwfServiceCatalog_importFunctionFromCompletionItem(containingService: SwfServiceCatalogService): void {
    this.swfServiceCatalogApiImpl.kogitoSwfServiceCatalog_importFunctionFromCompletionItem(containingService);
  }

  public kogitoSwfServiceCatalog_setupServiceRegistryUrl(): void {
    this.swfServiceCatalogApiImpl.kogitoSwfServiceCatalog_setupServiceRegistryUrl();
  }

  public async kogitoSwfLanguageService__doCompletion(args: {
    content: string;
    uri: string;
    cursorPosition: Position;
    cursorWordRange: Range;
  }): Promise<CompletionItem[]> {
    return this.swfLanguageServiceChannelApiImpl.kogitoSwfLanguageService__doCompletion(args);
  }

  public async kogitoSwfLanguageService__doCodeLenses(
    textDocumentIdentifier: TextDocumentIdentifier
  ): Promise<CodeLens[]> {
    return this.swfLanguageServiceChannelApiImpl.kogitoSwfLanguageService__doCodeLenses(textDocumentIdentifier);
  }
}
