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

import { KogitoEditorChannelApi } from "@kie-tools-core/editor/dist/api";
import { VsCodeKieEditorController } from "./VsCodeKieEditorController";
import { ResourceContentService, WorkspaceChannelApi } from "@kie-tools-core/workspace/dist/api";
import { BackendProxy } from "@kie-tools-core/backend/dist/api";
import { NotificationsChannelApi } from "@kie-tools-core/notifications/dist/api";
import { JavaCodeCompletionApi } from "@kie-tools-core/vscode-java-code-completion/dist/api";
import { I18n } from "@kie-tools-core/i18n/dist/core";
import { VsCodeI18n } from "./i18n";
import { DefaultVsCodeKieEditorChannelApiImpl } from "./DefaultVsCodeKieEditorChannelApiImpl";
import { VsCodeWorkspaceChannelApiImpl } from "./workspace/VsCodeWorkspaceChannelApiImpl";
import { VsCodeNotificationsChannelApiImpl } from "./notifications/VsCodeNotificationsChannelApiImpl";

/**
 * Produces instances of KogitoEditorChannelApi to be used if we want to provide the extension a Channel API with
 * custom features.
 */
export interface VsCodeKieEditorChannelApiProducer {
  /**
   * Method to obtain the KogitoEditorChannelApi instance.
   * @param editor
   * @param resourceContentService
   * @param vscodeWorkspace
   * @param backendProxy
   * @param vscodeNotifications
   * @param javaCodeCompletionApi
   * @param viewType
   * @param i18n
   */
  get(
    editor: VsCodeKieEditorController,
    resourceContentService: ResourceContentService,
    vscodeWorkspace: VsCodeWorkspaceChannelApiImpl,
    backendProxy: BackendProxy,
    vscodeNotifications: VsCodeNotificationsChannelApiImpl,
    javaCodeCompletionApi: JavaCodeCompletionApi,
    viewType: string,
    i18n: I18n<VsCodeI18n>
  ): KogitoEditorChannelApi;
}

export class DefaultVsCodeEditorChannelApiProducer implements VsCodeKieEditorChannelApiProducer {
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
    return new DefaultVsCodeKieEditorChannelApiImpl(
      editor,
      resourceContentService,
      vscodeWorkspace,
      backendProxy,
      vscodeNotifications,
      javaCodeCompletionApi,
      viewType,
      i18n
    );
  }
}
