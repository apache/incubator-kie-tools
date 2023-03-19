/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import {
  EditorContent,
  EditorTheme,
  KogitoEditorChannelApi,
  StateControlCommand,
} from "@kie-tools-core/editor/dist/api";
import { SharedValueProvider } from "@kie-tools-core/envelope-bus/dist/api";
import { Tutorial, UserInteraction } from "@kie-tools-core/guided-tour/dist/api";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import {
  ResourceContent,
  ResourceContentRequest,
  ResourceListRequest,
  ResourcesList,
  WorkspaceEdit,
} from "@kie-tools-core/workspace/dist/api";
import { SwfLanguageServiceChannelApi } from "@kie-tools/serverless-workflow-language-service/dist/api";
import {
  SwfServiceCatalogChannelApi,
  SwfServiceCatalogService,
  SwfServiceRegistriesSettings,
} from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import { CodeLens, CompletionItem, Position, Range } from "vscode-languageserver-types";
import { ServerlessWorkflowTextEditorChannelApi } from "../../src/api/ServerlessWorkflowTextEditorChannelApi";

export class SwfTextEditorChannelApiImpl implements ServerlessWorkflowTextEditorChannelApi {
  constructor(
    private readonly args: {
      defaultApiImpl: KogitoEditorChannelApi;
      swfLanguageServiceChannelApiImpl?: SwfLanguageServiceChannelApi;
      swfServiceCatalogApiImpl?: SwfServiceCatalogChannelApi;
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

  public kogitoGuidedTour_guidedTourRegisterTutorial(tutorial: Tutorial): void {
    this.args.defaultApiImpl.kogitoGuidedTour_guidedTourRegisterTutorial(tutorial);
  }

  public kogitoGuidedTour_guidedTourUserInteraction(userInteraction: UserInteraction): void {
    this.args.defaultApiImpl.kogitoGuidedTour_guidedTourUserInteraction(userInteraction);
  }

  public kogitoI18n_getLocale(): Promise<string> {
    return this.args.defaultApiImpl.kogitoI18n_getLocale();
  }

  public kogitoNotifications_createNotification(notification: Notification): void {
    this.args.defaultApiImpl.kogitoNotifications_createNotification(notification);
  }

  public kogitoNotifications_removeNotifications(path: string): void {
    this.args.defaultApiImpl.kogitoNotifications_removeNotifications(path);
  }

  public kogitoNotifications_setNotifications(path: string, notifications: Notification[]): void {
    this.args.defaultApiImpl.kogitoNotifications_setNotifications(path, notifications);
  }

  public kogitoWorkspace_newEdit(edit: WorkspaceEdit): void {
    this.args.defaultApiImpl.kogitoWorkspace_newEdit(edit);
  }

  public kogitoWorkspace_openFile(path: string): void {
    this.args.defaultApiImpl.kogitoWorkspace_openFile(path);
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
    this.args.swfServiceCatalogApiImpl?.kogitoSwfServiceCatalog_refresh();
  }

  public kogitoSwfServiceCatalog_importFunctionFromCompletionItem(args: {
    containingService: SwfServiceCatalogService;
    documentUri: string;
  }): void {
    this.args.swfServiceCatalogApiImpl?.kogitoSwfServiceCatalog_importFunctionFromCompletionItem(args);
  }

  public kogitoSwfServiceCatalog_logInServiceRegistries(): void {
    this.args.swfServiceCatalogApiImpl?.kogitoSwfServiceCatalog_logInServiceRegistries();
  }

  public async kogitoSwfLanguageService__getCompletionItems(args: {
    content: string;
    uri: string;
    cursorPosition: Position;
    cursorWordRange: Range;
  }): Promise<CompletionItem[]> {
    return this.args.swfLanguageServiceChannelApiImpl?.kogitoSwfLanguageService__getCompletionItems(args) ?? [];
  }

  public async kogitoSwfLanguageService__getCodeLenses(args: { uri: string; content: string }): Promise<CodeLens[]> {
    return this.args.swfLanguageServiceChannelApiImpl?.kogitoSwfLanguageService__getCodeLenses(args) ?? [];
  }

  public kogitoSwfServiceCatalog_setupServiceRegistriesSettings(): void {
    this.args.swfServiceCatalogApiImpl?.kogitoSwfServiceCatalog_setupServiceRegistriesSettings();
  }

  public kogitoSwfTextEditor__onSelectionChanged(args: { nodeName: string }): void {
    // no-op
  }
}
