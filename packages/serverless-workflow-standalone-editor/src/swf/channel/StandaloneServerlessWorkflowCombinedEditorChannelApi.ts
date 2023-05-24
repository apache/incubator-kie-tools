/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {
  ServerlessWorkflowCombinedEditorChannelApi,
  SwfStaticEnvelopeContentProviderChannelApi,
  SwfFeatureToggle,
  SwfFeatureToggleChannelApi,
  SwfPreviewOptions,
  SwfPreviewOptionsChannelApi,
} from "@kie-tools/serverless-workflow-combined-editor/dist/api";
import {
  EditorContent,
  EditorTheme,
  KogitoEditorChannelApi,
  StateControlCommand,
} from "@kie-tools-core/editor/dist/api";
import {
  SwfServiceCatalogChannelApi,
  SwfServiceCatalogService,
  SwfServiceRegistriesSettings,
} from "@kie-tools/serverless-workflow-service-catalog/dist/api";
import { SwfLanguageServiceChannelApi } from "@kie-tools/serverless-workflow-language-service/dist/api";
import { CodeLens, CompletionItem, Position, Range } from "vscode-languageserver-types";
import { Position as MonacoPosition } from "monaco-editor";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import {
  ResourceContent,
  ResourceContentRequest,
  ResourceListRequest,
  ResourcesList,
  WorkspaceEdit,
} from "@kie-tools-core/workspace/dist/api";
import { SharedValueProvider } from "@kie-tools-core/envelope-bus/dist/api";

export class StandaloneServerlessWorkflowCombinedEditorChannelApi
  implements ServerlessWorkflowCombinedEditorChannelApi
{
  constructor(
    private readonly defaultApiImpl: KogitoEditorChannelApi,
    private readonly swfFeatureToggleApiImpl?: SwfFeatureToggleChannelApi,
    private readonly swfServiceCatalogApiImpl?: SwfServiceCatalogChannelApi,
    private readonly swfLanguageServiceChannelApiImpl?: SwfLanguageServiceChannelApi,
    private readonly swfPreviewOptionsChannelApiImpl?: SwfPreviewOptionsChannelApi,
    private readonly swfStaticEnvelopeContentProviderChannelApi?: SwfStaticEnvelopeContentProviderChannelApi
  ) {}

  public kogitoEditor_contentRequest(): Promise<EditorContent> {
    return this.defaultApiImpl.kogitoEditor_contentRequest();
  }

  public kogitoEditor_ready(): void {
    this.defaultApiImpl.kogitoEditor_ready();
  }

  public kogitoEditor_setContentError(content: EditorContent): void {
    this.defaultApiImpl.kogitoEditor_setContentError(content);
  }

  public kogitoEditor_stateControlCommandUpdate(command: StateControlCommand) {
    this.defaultApiImpl.kogitoEditor_stateControlCommandUpdate(command);
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

  public kogitoWorkspace_newEdit(edit: WorkspaceEdit): void {
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
    return this.swfServiceCatalogApiImpl?.kogitoSwfServiceCatalog_services() ?? { defaultValue: [] };
  }

  public kogitoSwfServiceCatalog_refresh(): void {
    this.swfServiceCatalogApiImpl?.kogitoSwfServiceCatalog_refresh();
  }

  public kogitoSwfServiceCatalog_importFunctionFromCompletionItem(args: {
    containingService: SwfServiceCatalogService;
    documentUri: string;
  }): void {
    this.swfServiceCatalogApiImpl?.kogitoSwfServiceCatalog_importFunctionFromCompletionItem(args);
  }

  public async kogitoSwfLanguageService__getCompletionItems(args: {
    content: string;
    uri: string;
    cursorPosition: Position;
    cursorWordRange: Range;
  }): Promise<CompletionItem[]> {
    return new Promise<CompletionItem[]>((resolve, reject) => {
      try {
        const result = this.swfLanguageServiceChannelApiImpl?.kogitoSwfLanguageService__getCompletionItems(args) ?? [];
        resolve(result);
      } catch (err) {
        reject(err);
      }
    });
  }

  public async kogitoSwfLanguageService__getCodeLenses(args: { uri: string; content: string }): Promise<CodeLens[]> {
    return this.swfLanguageServiceChannelApiImpl?.kogitoSwfLanguageService__getCodeLenses(args) ?? [];
  }

  public kogitoSwfServiceCatalog_serviceRegistriesSettings(): SharedValueProvider<SwfServiceRegistriesSettings> {
    return (
      this.swfServiceCatalogApiImpl?.kogitoSwfServiceCatalog_serviceRegistriesSettings() ?? {
        defaultValue: { registries: [] },
      }
    );
  }

  public kogitoSwfServiceCatalog_logInServiceRegistries(): void {
    this.swfServiceCatalogApiImpl?.kogitoSwfServiceCatalog_logInServiceRegistries();
  }

  public kogitoSwfServiceCatalog_setupServiceRegistriesSettings(): void {
    this.swfServiceCatalogApiImpl?.kogitoSwfServiceCatalog_setupServiceRegistriesSettings();
  }

  public kogitoSwfFeatureToggle_get(): SharedValueProvider<SwfFeatureToggle> {
    return (
      this.swfFeatureToggleApiImpl?.kogitoSwfFeatureToggle_get() ?? {
        defaultValue: { stunnerEnabled: true },
      }
    );
  }

  kogitoSwfPreviewOptions_get(): SharedValueProvider<SwfPreviewOptions> {
    return (
      this.swfPreviewOptionsChannelApiImpl?.kogitoSwfPreviewOptions_get() ?? {
        defaultValue: { defaultWidth: "50%", editorMode: "full" },
      }
    );
  }

  public kogitoSwfGetDiagramEditorEnvelopeContent(): SharedValueProvider<string> {
    return (
      this.swfStaticEnvelopeContentProviderChannelApi?.kogitoSwfGetDiagramEditorEnvelopeContent() ?? {
        defaultValue: "",
      }
    );
  }

  public kogitoSwfGetMermaidEnvelopeContent(): SharedValueProvider<string> {
    return (
      this.swfStaticEnvelopeContentProviderChannelApi?.kogitoSwfGetMermaidEnvelopeContent() ?? {
        defaultValue: "",
      }
    );
  }

  public kogitoSwfGetTextEditorEnvelopeContent(): SharedValueProvider<string> {
    return (
      this.swfStaticEnvelopeContentProviderChannelApi?.kogitoSwfGetTextEditorEnvelopeContent() ?? {
        defaultValue: "",
      }
    );
  }

  public kogitoSwfCombinedEditor_moveCursorToPosition(_position: MonacoPosition): void {
    // no-op
  }

  kogitoSwfCombinedEditor_combinedEditorReady(): void {
    // no-op
  }
}
