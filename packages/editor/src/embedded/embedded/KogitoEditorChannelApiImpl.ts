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
import { KogitoGuidedTour } from "@kogito-tooling/guided-tour/dist/channel";
import { Tutorial, UserInteraction } from "@kogito-tooling/guided-tour/dist/api";
import { KogitoEditorChannelApi, StateControlCommand } from "../../api";
import {
  KogitoEdit,
  ResourceContent,
  ResourceContentRequest,
  ResourceListRequest,
  ResourcesList
} from "@kogito-tooling/channel-common-api";
import { File } from "../../channel";
import { Notification } from "@kogito-tooling/notifications/dist/api";

export class KogitoEditorChannelApiImpl implements KogitoEditorChannelApi {
  constructor(
    private readonly stateControl: StateControl,
    private readonly file: File,
    private readonly locale: string,
    private readonly overrides: Partial<KogitoEditorChannelApi>
  ) {}

  public receive_newEdit(edit: KogitoEdit) {
    this.stateControl.updateCommandStack(edit.id);
    this.overrides.receive_newEdit?.(edit);
  }

  public receive_stateControlCommandUpdate(command: StateControlCommand) {
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
    this.overrides.receive_stateControlCommandUpdate?.(command);
  }

  public receive_guidedTourUserInteraction(userInteraction: UserInteraction) {
    KogitoGuidedTour.getInstance().onUserInteraction(userInteraction);
  }

  public receive_guidedTourRegisterTutorial(tutorial: Tutorial) {
    KogitoGuidedTour.getInstance().registerTutorial(tutorial);
  }

  public async receive_contentRequest() {
    const content = await this.file.getFileContents();
    return { content: content ?? "", path: this.file.fileName };
  }

  public async receive_resourceContentRequest(request: ResourceContentRequest) {
    return this.overrides.receive_resourceContentRequest?.(request) ?? new ResourceContent(request.path, undefined);
  }

  public async receive_resourceListRequest(request: ResourceListRequest) {
    return this.overrides.receive_resourceListRequest?.(request) ?? new ResourcesList(request.pattern, []);
  }

  public receive_openFile(path: string): void {
    this.overrides.receive_openFile?.(path);
  }

  public receive_ready(): void {
    this.overrides.receive_ready?.();
  }

  public receive_setContentError(errorMessage: string): void {
    this.overrides.receive_setContentError?.(errorMessage);
  }

  public receive_getLocale(): Promise<string> {
    return Promise.resolve(this.locale);
  }

  public createNotification(notification: Notification): void {
    this.overrides.createNotification?.(notification);
  }
  public setNotifications(path: string, notifications: Notification[]): void {
    this.overrides.setNotifications?.(path, notifications);
  }
  public removeNotifications(path: string): void {
    this.overrides.removeNotifications?.(path);
  }
}
