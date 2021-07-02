/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import { StateControl } from "../../channel";
import { KogitoGuidedTour } from "@kie-tooling-core/guided-tour/dist/channel";
import { Tutorial, UserInteraction } from "@kie-tooling-core/guided-tour/dist/api";
import { KogitoEditorChannelApi, StateControlCommand, EditorContent } from "../../api";
import {
  KogitoEdit,
  ResourceContent,
  ResourceContentRequest,
  ResourceListRequest,
  ResourcesList,
} from "@kie-tooling-core/workspace/dist/api";
import { File } from "../../channel";
import { Notification } from "@kie-tooling-core/notifications/dist/api";

export class KogitoEditorChannelApiImpl implements KogitoEditorChannelApi {
  constructor(
    private readonly stateControl: StateControl,
    private readonly file: File,
    private readonly locale: string,
    private readonly overrides: Partial<KogitoEditorChannelApi>
  ) {}

  public kogitoWorkspace_newEdit(edit: KogitoEdit) {
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

  public kogitoGuidedTour_guidedTourUserInteraction(userInteraction: UserInteraction) {
    KogitoGuidedTour.getInstance().onUserInteraction(userInteraction);
  }

  public kogitoGuidedTour_guidedTourRegisterTutorial(tutorial: Tutorial) {
    KogitoGuidedTour.getInstance().registerTutorial(tutorial);
  }

  public async kogitoEditor_contentRequest() {
    const content = await this.file.getFileContents();
    return { content: content ?? "", path: this.file.fileName };
  }

  public async kogitoWorkspace_resourceContentRequest(request: ResourceContentRequest) {
    return (
      this.overrides.kogitoWorkspace_resourceContentRequest?.(request) ?? new ResourceContent(request.path, undefined)
    );
  }

  public async kogitoWorkspace_resourceListRequest(request: ResourceListRequest) {
    return this.overrides.kogitoWorkspace_resourceListRequest?.(request) ?? new ResourcesList(request.pattern, []);
  }

  public kogitoWorkspace_openFile(path: string): void {
    this.overrides.kogitoWorkspace_openFile?.(path);
  }

  public kogitoEditor_ready(): void {
    this.overrides.kogitoEditor_ready?.();
  }

  public kogitoEditor_setContentError(editorContent: EditorContent): void {
    this.overrides.kogitoEditor_setContentError?.(editorContent);
  }

  public kogitoI18n_getLocale(): Promise<string> {
    return Promise.resolve(this.locale);
  }

  public kogitoNotifications_createNotification(notification: Notification): void {
    this.overrides.kogitoNotifications_createNotification?.(notification);
  }

  public kogitoNotifications_setNotifications(path: string, notifications: Notification[]): void {
    this.overrides.kogitoNotifications_setNotifications?.(path, notifications);
  }

  public kogitoNotifications_removeNotifications(path: string): void {
    this.overrides.kogitoNotifications_removeNotifications?.(path);
  }
}
