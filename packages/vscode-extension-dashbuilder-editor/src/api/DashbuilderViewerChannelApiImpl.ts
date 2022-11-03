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

import { BackendProxy } from "@kie-tools-core/backend/dist/api";
import {
  EditorContent,
  EditorTheme,
  KogitoEditorChannelApi,
  StateControlCommand,
} from "@kie-tools-core/editor/dist/api";
import { SharedValueProvider } from "@kie-tools-core/envelope-bus/dist/api";

import { Tutorial, UserInteraction } from "@kie-tools-core/guided-tour/dist/api";
import { I18n } from "@kie-tools-core/i18n/dist/core";
import { Notification, NotificationsChannelApi } from "@kie-tools-core/notifications/dist/api";
import { DefaultVsCodeKieEditorChannelApiImpl } from "@kie-tools-core/vscode-extension/dist/DefaultVsCodeKieEditorChannelApiImpl";
import { VsCodeI18n } from "@kie-tools-core/vscode-extension/dist/i18n";
import { VsCodeKieEditorController } from "@kie-tools-core/vscode-extension/dist/VsCodeKieEditorController";
import { JavaCodeCompletionApi } from "@kie-tools-core/vscode-java-code-completion/dist/api";

import {
  ResourceContent,
  ResourceContentRequest,
  ResourceContentService,
  ResourceListRequest,
  ResourcesList,
  WorkspaceChannelApi,
  WorkspaceEdit,
} from "@kie-tools-core/workspace/dist/api";
import { DashbuilderViewerChannelApi } from "@kie-tools/dashbuilder-viewer";

export class DashbuilderViewerChannelApiImpl implements DashbuilderViewerChannelApi {
  private readonly defaultApiImpl: KogitoEditorChannelApi;
  componentServerUrlPromise: Promise<string | undefined>;

  constructor(
    private readonly editor: VsCodeKieEditorController,
    resourceContentService: ResourceContentService,
    workspaceApi: WorkspaceChannelApi,
    backendProxy: BackendProxy,
    notificationsApi: NotificationsChannelApi,
    javaCodeCompletionApi: JavaCodeCompletionApi,
    viewType: string,
    i18n: I18n<VsCodeI18n>,
    componentServerUrlPromise: Promise<string | undefined>
  ) {
    this.defaultApiImpl = new DefaultVsCodeKieEditorChannelApiImpl(
      editor,
      resourceContentService,
      workspaceApi,
      backendProxy,
      notificationsApi,
      javaCodeCompletionApi,
      viewType,
      i18n
    );
    this.componentServerUrlPromise = componentServerUrlPromise;
  }
  getComponentsServerUrl(): Promise<string | undefined> {
    return this.componentServerUrlPromise;
  }
  kogitoEditor_ready(): void {
    this.defaultApiImpl.kogitoEditor_ready();
  }
  kogitoEditor_setContentError(content: EditorContent): void {
    this.defaultApiImpl.kogitoEditor_setContentError(content);
  }
  kogitoEditor_stateControlCommandUpdate(command: StateControlCommand): void {
    this.defaultApiImpl.kogitoEditor_stateControlCommandUpdate(command);
  }
  kogitoEditor_contentRequest(): Promise<EditorContent> {
    return this.defaultApiImpl.kogitoEditor_contentRequest();
  }
  kogitoEditor_theme(): SharedValueProvider<EditorTheme> {
    return this.defaultApiImpl.kogitoEditor_theme();
  }
  kogitoGuidedTour_guidedTourUserInteraction(userInteraction: UserInteraction): void {
    this.defaultApiImpl.kogitoGuidedTour_guidedTourUserInteraction(userInteraction);
  }
  kogitoGuidedTour_guidedTourRegisterTutorial(tutorial: Tutorial): void {
    this.defaultApiImpl.kogitoGuidedTour_guidedTourRegisterTutorial(tutorial);
  }
  kogitoI18n_getLocale(): Promise<string> {
    return this.defaultApiImpl.kogitoI18n_getLocale();
  }
  kogitoWorkspace_newEdit(edit: WorkspaceEdit): void {
    this.defaultApiImpl.kogitoWorkspace_newEdit(edit);
  }
  kogitoWorkspace_openFile(path: string): void {
    this.defaultApiImpl.kogitoWorkspace_openFile(path);
  }
  kogitoWorkspace_resourceContentRequest(request: ResourceContentRequest): Promise<ResourceContent | undefined> {
    return this.defaultApiImpl.kogitoWorkspace_resourceContentRequest(request);
  }
  kogitoWorkspace_resourceListRequest(request: ResourceListRequest): Promise<ResourcesList> {
    return this.defaultApiImpl.kogitoWorkspace_resourceListRequest(request);
  }
  kogitoNotifications_createNotification(notification: Notification): void {
    this.defaultApiImpl.kogitoNotifications_createNotification(notification);
  }
  kogitoNotifications_setNotifications(path: string, notifications: Notification[]): void {
    this.defaultApiImpl.kogitoNotifications_setNotifications(path, notifications);
  }
  kogitoNotifications_removeNotifications(path: string): void {
    this.defaultApiImpl.kogitoNotifications_removeNotifications(path);
  }
}
