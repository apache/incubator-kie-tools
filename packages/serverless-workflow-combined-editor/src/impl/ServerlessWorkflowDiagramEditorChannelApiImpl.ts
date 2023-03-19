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

import {
  EditorContent,
  EditorTheme,
  KogitoEditorChannelApi,
  StateControlCommand,
} from "@kie-tools-core/editor/dist/api";
import { MessageBusClientApi, SharedValueProvider } from "@kie-tools-core/envelope-bus/dist/api";
import { Tutorial, UserInteraction } from "@kie-tools-core/guided-tour/dist/api";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import {
  ResourceContent,
  ResourceContentRequest,
  ResourceListRequest,
  ResourcesList,
  WorkspaceEdit,
} from "@kie-tools-core/workspace/dist/api";
import { ServerlessWorkflowDiagramEditorChannelApi } from "@kie-tools/serverless-workflow-diagram-editor-envelope/dist/api";
import { ServerlessWorkflowTextEditorEnvelopeApi } from "@kie-tools/serverless-workflow-text-editor/dist/api";

export class ServerlessWorkflowDiagramEditorChannelApiImpl implements ServerlessWorkflowDiagramEditorChannelApi {
  constructor(
    private readonly defaultApiImpl: KogitoEditorChannelApi,
    private readonly textEditorEnvelopeApi?: MessageBusClientApi<ServerlessWorkflowTextEditorEnvelopeApi>
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

  public kogitoSwfDiagramEditor__onNodeSelected(args: { nodeName: string; documentUri?: string }): void {
    return this.textEditorEnvelopeApi?.notifications.kogitoSwfTextEditor__moveCursorToNode.send(args);
  }
}
