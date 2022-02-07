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
import { StateControlCommand } from "@kie-tools-core/editor/dist/api";
import * as vscode from "vscode";

export class ServerlessWorkflowChannelApiImpl extends KogitoEditorChannelApiImpl {
  private readonly apiDelegate: KogitoEditorChannelApiImpl;

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
    private readonly serviceCatalogApiDelegate: ServiceCatalogChannelApi
  ) {
    this.apiDelegate = new KogitoEditorChannelApiImpl(
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
    return this.apiDelegate.kogitoEditor_contentRequest();
  }

  public kogitoEditor_ready(): void {
    this.apiDelegate.kogitoEditor_contentRequest();
  }

  public kogitoEditor_setContentError(content: EditorContent): void {
    this.apiDelegate.kogitoEditor_setContentError(content);
  }

  public kogitoEditor_stateControlCommandUpdate(command: StateControlCommand): void {
    this.apiDelegate.kogitoEditor_stateControlCommandUpdate(command);
  }

  public kogitoGuidedTour_guidedTourRegisterTutorial(tutorial: Tutorial): void {
    this.apiDelegate.kogitoGuidedTour_guidedTourRegisterTutorial(tutorial);
  }

  public kogitoGuidedTour_guidedTourUserInteraction(userInteraction: UserInteraction): void {
    this.apiDelegate.kogitoGuidedTour_guidedTourUserInteraction(userInteraction);
  }

  public kogitoI18n_getLocale(): Promise<string> {
    return this.apiDelegate.kogitoI18n_getLocale();
  }

  public kogitoNotifications_createNotification(notification: Notification): void {
    this.apiDelegate.kogitoNotifications_createNotification(notification);
  }

  kogitoNotifications_removeNotifications(path: string): void {
    this.apiDelegate.kogitoNotifications_removeNotifications(path);
  }

  public kogitoNotifications_setNotifications(path: string, notifications: Notification[]): void {
    this.apiDelegate.kogitoNotifications_setNotifications(path, notifications);
  }

  public kogitoWorkspace_newEdit(edit: KogitoEdit): void {
    this.apiDelegate.kogitoWorkspace_newEdit(edit);
  }

  public kogitoWorkspace_openFile(path: string): void {
    this.apiDelegate.kogitoWorkspace_openFile(path);
  }

  public kogitoWorkspace_resourceContentRequest(request: ResourceContentRequest): Promise<ResourceContent | undefined> {
    return this.apiDelegate.kogitoWorkspace_resourceContentRequest(request);
  }

  public kogitoWorkspace_resourceListRequest(request: ResourceListRequest): Promise<ResourcesList> {
    return this.apiDelegate.kogitoWorkspace_resourceListRequest(request);
  }

  public kogitoServiceCatalog_getServiceDefinitions(): Promise<ServiceDefinition[]> {
    return this.serviceCatalogApiDelegate.kogitoServiceCatalog_getServiceDefinitions();
  }

  public kogitoServiceCatalog_getFunctionDefinitions(serviceId?: string): Promise<FunctionDefinition[]> {
    return this.serviceCatalogApiDelegate.kogitoServiceCatalog_getFunctionDefinitions(serviceId);
  }

  kogitoServiceCatalog_getFunctionDefinitionByOperation(operationId: string): Promise<FunctionDefinition | undefined> {
    return this.serviceCatalogApiDelegate.kogitoServiceCatalog_getFunctionDefinitionByOperation(operationId);
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
}
