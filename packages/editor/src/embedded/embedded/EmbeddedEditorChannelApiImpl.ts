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

import { StateControl } from "../../channel";
import { KogitoEditorChannelApi, StateControlCommand, EditorContent } from "../../api";
import {
  WorkspaceEdit,
  ResourceContent,
  ResourceContentRequest,
  ResourceListRequest,
  ResourcesList,
} from "@kie-tools-core/workspace/dist/api";
import { EmbeddedEditorFile } from "../../channel";
import { Notification } from "@kie-tools-core/notifications/dist/api";
import { EditorTheme } from "../../api";

export class EmbeddedEditorChannelApiImpl implements KogitoEditorChannelApi {
  constructor(
    private readonly stateControl: StateControl,
    private readonly file: EmbeddedEditorFile,
    private readonly locale: string,
    private readonly overrides: Partial<KogitoEditorChannelApi>
  ) {}

  public kogitoWorkspace_newEdit(edit: WorkspaceEdit) {
    this.stateControl.updateCommandStack({ id: edit.id });
    this.overrides.kogitoWorkspace_newEdit?.(edit);
  }

  public kogitoEditor_stateControlCommandUpdate(command: StateControlCommand) {
    switch (command) {
      case StateControlCommand.REDO:
        this.stateControl.redo();
        break;
      case StateControlCommand.UNDO:
        this.stateControl.undo();
        break;
      default:
        console.info(`Unknown message type received: ${command}`);
        break;
    }
    this.overrides.kogitoEditor_stateControlCommandUpdate?.(command);
  }

  public async kogitoEditor_contentRequest() {
    const content = await this.file.getFileContents();
    return {
      content: content ?? "",
      normalizedPosixPathRelativeToTheWorkspaceRoot: this.file.normalizedPosixPathRelativeToTheWorkspaceRoot,
    };
  }

  public async kogitoWorkspace_resourceContentRequest(request: ResourceContentRequest) {
    return (
      this.overrides.kogitoWorkspace_resourceContentRequest?.(request) ??
      new ResourceContent(request.normalizedPosixPathRelativeToTheWorkspaceRoot, undefined)
    );
  }

  public async kogitoWorkspace_resourceListRequest(request: ResourceListRequest) {
    return this.overrides.kogitoWorkspace_resourceListRequest?.(request) ?? new ResourcesList(request.pattern, []);
  }

  public kogitoWorkspace_openFile(normalizedPosixPathRelativeToTheWorkspaceRoot: string): void {
    this.overrides.kogitoWorkspace_openFile?.(normalizedPosixPathRelativeToTheWorkspaceRoot);
  }

  public kogitoEditor_ready(): void {
    this.overrides.kogitoEditor_ready?.();
  }

  public kogitoEditor_setContentError(editorContent: EditorContent): void {
    this.overrides.kogitoEditor_setContentError?.(editorContent);
  }

  public kogitoEditor_theme() {
    return this.overrides.kogitoEditor_theme?.() ?? { defaultValue: EditorTheme.LIGHT };
  }

  public kogitoI18n_getLocale(): Promise<string> {
    return Promise.resolve(this.locale);
  }

  public kogitoNotifications_createNotification(notification: Notification): void {
    this.overrides.kogitoNotifications_createNotification?.(notification);
  }

  public kogitoNotifications_setNotifications(
    normalizedPosixPathRelativeToTheWorkspaceRoot: string,
    notifications: Notification[]
  ): void {
    this.overrides.kogitoNotifications_setNotifications?.(normalizedPosixPathRelativeToTheWorkspaceRoot, notifications);
  }

  public kogitoNotifications_removeNotifications(normalizedPosixPathRelativeToTheWorkspaceRoot: string): void {
    this.overrides.kogitoNotifications_removeNotifications?.(normalizedPosixPathRelativeToTheWorkspaceRoot);
  }
}
