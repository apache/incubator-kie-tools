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

import { DefaultVsCodeKieEditorChannelApiImpl } from "@kie-tools-core/vscode-extension/dist/DefaultVsCodeKieEditorChannelApiImpl";
import { VsCodeKieEditorController } from "@kie-tools-core/vscode-extension/dist/VsCodeKieEditorController";
import { VsCodeI18n } from "@kie-tools-core/vscode-extension/dist/i18n";
import { VsCodeNotificationsChannelApiImpl } from "@kie-tools-core/vscode-extension/dist/notifications/VsCodeNotificationsChannelApiImpl";
import { VsCodeWorkspaceChannelApiImpl } from "@kie-tools-core/vscode-extension/dist/workspace/VsCodeWorkspaceChannelApiImpl";
import { JavaCodeCompletionApi } from "@kie-tools-core/vscode-java-code-completion/dist/api";
import { I18n } from "@kie-tools-core/i18n/dist/core";
import { ResourceContentService } from "@kie-tools-core/workspace/dist/api";
import {
  BpmnEditorChannelApi,
  RestTaskTestRequest,
  RestTaskTestResponse,
} from "@kie-tools/bpmn-editor-envelope/dist/BpmnEditorChannelApi";

export class BpmnEditorChannelApiImpl extends DefaultVsCodeKieEditorChannelApiImpl implements BpmnEditorChannelApi {
  constructor(
    protected readonly editor: VsCodeKieEditorController,
    protected readonly resourceContentService: ResourceContentService,
    protected readonly vscodeWorkspace: VsCodeWorkspaceChannelApiImpl,
    protected readonly vscodeNotifications: VsCodeNotificationsChannelApiImpl,
    protected readonly javaCodeCompletionApi: JavaCodeCompletionApi,
    protected readonly viewType: string,
    protected readonly i18n: I18n<VsCodeI18n>
  ) {
    super(editor, resourceContentService, vscodeWorkspace, vscodeNotifications, javaCodeCompletionApi, viewType, i18n);
  }

  public async bpmnEditor_restTaskTest(request: RestTaskTestRequest): Promise<RestTaskTestResponse> {
    try {
      // Use native fetch API available in VS Code (ignores useCorsProxy parameter as it's not needed in VSCode)
      const response = await fetch(request.url, {
        method: request.method,
        headers: request.headers,
        body: request.body,
      });

      const contentType = response.headers.get("content-type");
      let data: any;

      if (contentType?.includes("application/json")) {
        data = await response.json();
      } else {
        data = await response.text();
      }

      const headers: Record<string, string> = {};
      response.headers.forEach((value, key) => {
        headers[key] = value;
      });

      return {
        status: response.status,
        data,
        headers,
      };
    } catch (error) {
      throw new Error(`REST request failed: ${(error as Error).message}`);
    }
  }
}
