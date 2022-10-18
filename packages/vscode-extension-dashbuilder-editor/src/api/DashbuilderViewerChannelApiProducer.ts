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
import { KogitoEditorChannelApi } from "@kie-tools-core/editor/dist/api";
import { I18n } from "@kie-tools-core/i18n/dist/core";
import { NotificationsChannelApi } from "@kie-tools-core/notifications/dist/api";
import { VsCodeI18n } from "@kie-tools-core/vscode-extension/dist/i18n";
import { VsCodeKieEditorChannelApiProducer } from "@kie-tools-core/vscode-extension/dist/VsCodeKieEditorChannelApiProducer";
import { VsCodeKieEditorController } from "@kie-tools-core/vscode-extension/dist/VsCodeKieEditorController";
import { JavaCodeCompletionApi } from "@kie-tools-core/vscode-java-code-completion/dist/api";
import { ResourceContentService, WorkspaceChannelApi } from "@kie-tools-core/workspace/dist/api";
import { DashbuilderViewerChannelApiImpl } from "./DashbuilderViewerChannelApiImpl";

export class DashbuilderViewerChannelApiProducer implements VsCodeKieEditorChannelApiProducer {
  componentServerUrlPromise: Promise<string | undefined>;

  constructor(componentServerUrlPromise: Promise<string | undefined>) {
    this.componentServerUrlPromise = componentServerUrlPromise;
  }

  get(
    editor: VsCodeKieEditorController,
    resourceContentService: ResourceContentService,
    workspaceApi: WorkspaceChannelApi,
    backendProxy: BackendProxy,
    notificationsApi: NotificationsChannelApi,
    javaCodeCompletionApi: JavaCodeCompletionApi,
    viewType: string,
    i18n: I18n<VsCodeI18n>
  ): KogitoEditorChannelApi {
    return new DashbuilderViewerChannelApiImpl(
      editor,
      resourceContentService,
      workspaceApi,
      backendProxy,
      notificationsApi,
      javaCodeCompletionApi,
      viewType,
      i18n,
      this.componentServerUrlPromise
    );
  }
}
