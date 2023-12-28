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
import { Notification } from "@kie-tools-core/notifications/dist/api";
import {
  WorkspaceEdit,
  ResourceContentRequest,
  ResourceContent,
  ResourceListRequest,
  ResourcesList,
} from "@kie-tools-core/workspace/dist/api";
import { YardEditorChannelApi } from "../api";
import { SharedValueProvider } from "@kie-tools-core/envelope-bus/dist/api";
import { Position, Range, CompletionItem, CodeLens } from "vscode-languageserver-types";
import { YardLanguageServiceChannelApi } from "@kie-tools/yard-language-service/dist/api";

export class YardEditorChannelApiImpl implements YardEditorChannelApi {
  constructor(
    private readonly defaultApiImpl: KogitoEditorChannelApi,
    private readonly yardLanguageServiceChannelApiImpl: YardLanguageServiceChannelApi
  ) {}

  public kogitoEditor_ready(): void {
    this.defaultApiImpl.kogitoEditor_ready();
  }

  public kogitoEditor_setContentError(content: EditorContent): void {
    this.defaultApiImpl.kogitoEditor_setContentError(content);
  }

  public kogitoEditor_stateControlCommandUpdate(command: StateControlCommand) {
    this.defaultApiImpl.kogitoEditor_stateControlCommandUpdate(command);
  }

  public kogitoEditor_contentRequest(): Promise<EditorContent> {
    return this.defaultApiImpl.kogitoEditor_contentRequest();
  }

  public kogitoI18n_getLocale(): Promise<string> {
    return this.defaultApiImpl.kogitoI18n_getLocale();
  }

  public kogitoWorkspace_newEdit(edit: WorkspaceEdit): void {
    this.defaultApiImpl.kogitoWorkspace_newEdit(edit);
  }

  public kogitoWorkspace_openFile(normalizedPosixPathRelativeToTheWorkspaceRoot: string): void {
    this.defaultApiImpl.kogitoWorkspace_openFile(normalizedPosixPathRelativeToTheWorkspaceRoot);
  }

  public kogitoWorkspace_resourceContentRequest(request: ResourceContentRequest): Promise<ResourceContent | undefined> {
    return this.defaultApiImpl.kogitoWorkspace_resourceContentRequest(request);
  }

  public kogitoWorkspace_resourceListRequest(request: ResourceListRequest): Promise<ResourcesList> {
    return this.defaultApiImpl.kogitoWorkspace_resourceListRequest(request);
  }

  public kogitoNotifications_createNotification(notification: Notification): void {
    this.defaultApiImpl.kogitoNotifications_createNotification(notification);
  }

  public kogitoNotifications_removeNotifications(normalizedPosixPathRelativeToTheWorkspaceRoot: string): void {
    this.defaultApiImpl.kogitoNotifications_removeNotifications(normalizedPosixPathRelativeToTheWorkspaceRoot);
  }

  public kogitoNotifications_setNotifications(
    normalizedPosixPathRelativeToTheWorkspaceRoot: string,
    notifications: Notification[]
  ): void {
    this.defaultApiImpl.kogitoNotifications_setNotifications(
      normalizedPosixPathRelativeToTheWorkspaceRoot,
      notifications
    );
  }

  public kogitoEditor_theme(): SharedValueProvider<EditorTheme> {
    return this.defaultApiImpl.kogitoEditor_theme();
  }

  public kogitoYardLanguageService__getCompletionItems(args: {
    content: string;
    uri: string;
    cursorPosition: Position;
    cursorWordRange: Range;
  }): Promise<CompletionItem[]> {
    return this.yardLanguageServiceChannelApiImpl.kogitoYardLanguageService__getCompletionItems(args) ?? [];
  }
  public kogitoYardLanguageService__getCodeLenses(args: { uri: string; content: string }): Promise<CodeLens[]> {
    return this.yardLanguageServiceChannelApiImpl.kogitoYardLanguageService__getCodeLenses(args) ?? [];
  }
}
